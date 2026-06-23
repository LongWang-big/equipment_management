package com.xxx.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxx.equipment.dto.InventoryCheckDTO;
import com.xxx.equipment.dto.StockInDTO;
import com.xxx.equipment.dto.StockOutDTO;
import com.xxx.equipment.dto.StockReturnDTO;
import com.xxx.equipment.entity.Device;
import com.xxx.equipment.entity.Inventory;
import com.xxx.equipment.entity.InventoryCheck;
import com.xxx.equipment.entity.StockRecord;
import com.xxx.equipment.exception.BusinessException;
import com.xxx.equipment.mapper.DeviceMapper;
import com.xxx.equipment.mapper.InventoryCheckMapper;
import com.xxx.equipment.mapper.InventoryMapper;
import com.xxx.equipment.mapper.StockRecordMapper;
import com.xxx.equipment.service.InventoryService;
import com.xxx.equipment.vo.InventoryCheckVO;
import com.xxx.equipment.vo.InventoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryMapper inventoryMapper;
    private final StockRecordMapper stockRecordMapper;
    private final InventoryCheckMapper inventoryCheckMapper;
    private final DeviceMapper deviceMapper;

    // ==================== 查询 ====================

    @Override
    public IPage<InventoryVO> getInventoryPage(int pageNum, int pageSize) {
        Page<InventoryVO> page = new Page<>(pageNum, pageSize);
        return inventoryMapper.selectPageWithDeviceName(page);
    }

    @Override
    public IPage<InventoryVO> getWarningList(int pageNum, int pageSize) {
        Page<InventoryVO> page = new Page<>(pageNum, pageSize);
        return inventoryMapper.selectWarningList(page);
    }

    // ==================== 出入库 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stockIn(StockInDTO stockInDTO) {
        changeQuantity(stockInDTO.getDeviceId(), stockInDTO.getQuantity());
        saveStockRecord(stockInDTO.getDeviceId(), "IN", stockInDTO.getQuantity(),
                null, stockInDTO.getOperatorName(),
                null, stockInDTO.getTargetPlace());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stockOut(StockOutDTO stockOutDTO) {
        Inventory inventory = getInventory(stockOutDTO.getDeviceId());

        if (stockOutDTO.getQuantity() > inventory.getQuantity()) {
            throw new BusinessException("库存不足，当前库存: " + inventory.getQuantity());
        }

        changeQuantity(stockOutDTO.getDeviceId(), -stockOutDTO.getQuantity());
        saveStockRecord(stockOutDTO.getDeviceId(), "OUT", stockOutDTO.getQuantity(),
                stockOutDTO.getReceiver(), stockOutDTO.getOperatorName(),
                stockOutDTO.getSourcePlace(), stockOutDTO.getTargetPlace());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stockReturn(StockReturnDTO stockReturnDTO) {
        changeQuantity(stockReturnDTO.getDeviceId(), stockReturnDTO.getQuantity());
        saveStockRecord(stockReturnDTO.getDeviceId(), "RETURN", stockReturnDTO.getQuantity(),
                stockReturnDTO.getReceiver(), stockReturnDTO.getOperatorName(),
                stockReturnDTO.getSourcePlace(), null);
    }

    // ==================== 盘点 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InventoryCheckVO check(InventoryCheckDTO dto) {
        Inventory inventory = getInventory(dto.getDeviceId());

        int differenceQuantity = dto.getActualQuantity() - inventory.getQuantity();

        InventoryCheck check = new InventoryCheck();
        check.setDeviceId(dto.getDeviceId());
        check.setSystemQuantity(inventory.getQuantity());
        check.setActualQuantity(dto.getActualQuantity());
        check.setDifferenceQuantity(differenceQuantity);
        check.setChecker(dto.getChecker());
        check.setCheckTime(LocalDateTime.now());
        inventoryCheckMapper.insert(check);

        // 盘点后修正库存数量为实际数量
        inventory.setQuantity(dto.getActualQuantity());
        inventory.setUpdateTime(LocalDateTime.now());
        inventoryMapper.updateById(inventory);

        Device device = deviceMapper.selectById(dto.getDeviceId());

        InventoryCheckVO vo = new InventoryCheckVO();
        vo.setId(check.getId());
        vo.setDeviceId(check.getDeviceId());
        vo.setDeviceName(device != null ? device.getDeviceName() : null);
        vo.setSystemQuantity(check.getSystemQuantity());
        vo.setActualQuantity(check.getActualQuantity());
        vo.setDifferenceQuantity(check.getDifferenceQuantity());
        vo.setChecker(check.getChecker());
        vo.setCheckTime(check.getCheckTime());
        return vo;
    }

    // ==================== 私有方法 ====================

    /**
     * 查询设备库存，不存在则抛出异常
     */
    private Inventory getInventory(Long deviceId) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getDeviceId, deviceId);
        Inventory inventory = inventoryMapper.selectOne(wrapper);
        if (inventory == null) {
            throw new BusinessException("该设备没有库存记录");
        }
        return inventory;
    }

    /**
     * 增减库存数量，不存在则新建
     */
    private void changeQuantity(Long deviceId, int delta) {
        // 校验设备是否存在
        Device device = deviceMapper.selectById(deviceId);
        if (device == null) {
            throw new BusinessException("设备不存在");
        }

        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getDeviceId, deviceId);
        Inventory inventory = inventoryMapper.selectOne(wrapper);

        if (inventory == null) {
            inventory = new Inventory();
            inventory.setDeviceId(deviceId);
            inventory.setQuantity(Math.max(delta, 0));
            inventory.setStatus("正常");
            inventory.setWarningValue(5);
            inventory.setUpdateTime(LocalDateTime.now());
            inventoryMapper.insert(inventory);
        } else {
            inventory.setQuantity(inventory.getQuantity() + delta);
            inventory.setUpdateTime(LocalDateTime.now());
            inventoryMapper.updateById(inventory);
        }
    }

    /**
     * 保存出入库记录
     */
    private void saveStockRecord(Long deviceId, String operationType,
                                 int quantity, String receiver, String operatorName,
                                 String sourcePlace, String targetPlace) {
        StockRecord record = new StockRecord();
        record.setDeviceId(deviceId);
        record.setOperationType(operationType);
        record.setQuantity(quantity);
        record.setReceiver(receiver);
        record.setOperatorName(operatorName);
        record.setSourcePlace(sourcePlace);
        record.setTargetPlace(targetPlace);
        record.setOperationTime(LocalDateTime.now());
        stockRecordMapper.insert(record);
    }
}
