package com.xxx.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxx.equipment.dto.ScrapDTO;
import com.xxx.equipment.entity.Device;
import com.xxx.equipment.entity.DeviceScrap;
import com.xxx.equipment.entity.Inventory;
import com.xxx.equipment.entity.StockRecord;
import com.xxx.equipment.exception.BusinessException;
import com.xxx.equipment.mapper.DeviceMapper;
import com.xxx.equipment.mapper.DeviceScrapMapper;
import com.xxx.equipment.mapper.InventoryMapper;
import com.xxx.equipment.mapper.StockRecordMapper;
import com.xxx.equipment.service.DeviceScrapService;
import com.xxx.equipment.vo.ScrapVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeviceScrapServiceImpl implements DeviceScrapService {

    private final DeviceScrapMapper scrapMapper;
    private final DeviceMapper deviceMapper;
    private final InventoryMapper inventoryMapper;
    private final StockRecordMapper stockRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addScrap(ScrapDTO dto) {
        // 校验设备是否存在
        Device device = deviceMapper.selectById(dto.getDeviceId());
        if (device == null) {
            throw new BusinessException("设备不存在");
        }

        // 校验库存是否充足
        LambdaQueryWrapper<Inventory> invWrapper = new LambdaQueryWrapper<>();
        invWrapper.eq(Inventory::getDeviceId, dto.getDeviceId());
        Inventory inventory = inventoryMapper.selectOne(invWrapper);
        if (inventory == null) {
            throw new BusinessException("该设备没有库存记录");
        }
        if (dto.getScrapQuantity() > inventory.getQuantity()) {
            throw new BusinessException("报损数量超过当前库存，当前库存: " + inventory.getQuantity());
        }

        DeviceScrap scrap = new DeviceScrap();
        BeanUtils.copyProperties(dto, scrap);
        scrap.setStatus("PENDING");
        scrap.setCreateTime(LocalDateTime.now());
        scrapMapper.insert(scrap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveScrap(ScrapDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("报损记录ID不能为空");
        }
        if (dto.getAction() == null || dto.getAction().isEmpty()) {
            throw new BusinessException("审批操作不能为空");
        }
        if (dto.getApprover() == null || dto.getApprover().isEmpty()) {
            throw new BusinessException("审批人不能为空");
        }
        DeviceScrap scrap = scrapMapper.selectById(dto.getId());
        if (scrap == null) {
            throw new BusinessException("报损记录不存在");
        }
        if (!"PENDING".equals(scrap.getStatus())) {
            throw new BusinessException("该记录已处理，无法重复审批");
        }

        if ("APPROVE".equals(dto.getAction())) {
            // 审批通过：扣减库存
            LambdaQueryWrapper<Inventory> invWrapper = new LambdaQueryWrapper<>();
            invWrapper.eq(Inventory::getDeviceId, scrap.getDeviceId());
            Inventory inventory = inventoryMapper.selectOne(invWrapper);
            if (inventory == null) {
                throw new BusinessException("库存记录不存在");
            }
            if (scrap.getScrapQuantity() > inventory.getQuantity()) {
                throw new BusinessException("库存不足，无法审批通过");
            }
            inventory.setQuantity(inventory.getQuantity() - scrap.getScrapQuantity());
            inventory.setUpdateTime(LocalDateTime.now());
            inventoryMapper.updateById(inventory);

            // 记录出库（报损出库）
            StockRecord record = new StockRecord();
            record.setDeviceId(scrap.getDeviceId());
            record.setOperationType("OUT");
            record.setQuantity(scrap.getScrapQuantity());
            record.setReceiver(scrap.getApplicant());
            record.setOperatorName(dto.getApprover());
            record.setSourcePlace("库存");
            record.setTargetPlace("报损");
            record.setOperationTime(LocalDateTime.now());
            stockRecordMapper.insert(record);

            scrap.setStatus("APPROVED");
        } else if ("REJECT".equals(dto.getAction())) {
            scrap.setStatus("REJECTED");
        } else {
            throw new BusinessException("审批操作无效");
        }

        scrap.setApprover(dto.getApprover());
        scrapMapper.updateById(scrap);
    }

    @Override
    public void deleteScrap(Long id) {
        DeviceScrap scrap = scrapMapper.selectById(id);
        if (scrap == null) {
            throw new BusinessException("报损记录不存在");
        }
        if ("APPROVED".equals(scrap.getStatus())) {
            throw new BusinessException("已审批的记录不能删除");
        }
        scrapMapper.deleteById(id);
    }

    @Override
    public IPage<ScrapVO> getScrapPage(int pageNum, int pageSize, String status) {
        Page<DeviceScrap> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DeviceScrap> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(DeviceScrap::getStatus, status);
        }
        wrapper.orderByDesc(DeviceScrap::getCreateTime);
        IPage<DeviceScrap> scrapPage = scrapMapper.selectPage(page, wrapper);

        // 批量查询设备信息
        List<Long> deviceIds = scrapPage.getRecords().stream()
                .map(DeviceScrap::getDeviceId).distinct().toList();
        Map<Long, Device> deviceMap = new HashMap<>();
        if (!deviceIds.isEmpty()) {
            deviceMapper.selectBatchIds(deviceIds).forEach(d -> deviceMap.put(d.getId(), d));
        }

        return scrapPage.convert(scrap -> {
            ScrapVO vo = new ScrapVO();
            BeanUtils.copyProperties(scrap, vo);
            Device device = deviceMap.get(scrap.getDeviceId());
            if (device != null) {
                vo.setDeviceName(device.getDeviceName());
                vo.setDeviceCode(device.getDeviceCode());
            }
            return vo;
        });
    }
}
