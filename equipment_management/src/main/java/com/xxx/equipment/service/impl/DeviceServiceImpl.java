package com.xxx.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxx.equipment.dto.DeviceDTO;
import com.xxx.equipment.entity.Device;
import com.xxx.equipment.entity.DeviceCategory;
import com.xxx.equipment.entity.DeviceScrap;
import com.xxx.equipment.entity.Inventory;
import com.xxx.equipment.entity.PurchaseOrder;
import com.xxx.equipment.entity.StockRecord;
import com.xxx.equipment.exception.BusinessException;
import com.xxx.equipment.mapper.DeviceCategoryMapper;
import com.xxx.equipment.mapper.DeviceMapper;
import com.xxx.equipment.mapper.DeviceScrapMapper;
import com.xxx.equipment.mapper.InventoryMapper;
import com.xxx.equipment.mapper.PurchaseOrderMapper;
import com.xxx.equipment.mapper.StockRecordMapper;
import com.xxx.equipment.service.DeviceService;
import com.xxx.equipment.vo.DeviceVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final DeviceMapper deviceMapper;
    private final InventoryMapper inventoryMapper;
    private final StockRecordMapper stockRecordMapper;
    private final DeviceCategoryMapper categoryMapper;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final DeviceScrapMapper deviceScrapMapper;

    @Override
    public void addDevice(DeviceDTO deviceDTO) {
        Device device = new Device();
        BeanUtils.copyProperties(deviceDTO, device);
        try {
            deviceMapper.insert(device);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("设备编码已存在");
        }
    }

    @Override
    public void updateDevice(DeviceDTO deviceDTO) {
        if (deviceDTO.getId() == null) {
            throw new BusinessException("设备ID不能为空");
        }

        // 校验设备是否存在
        Device existing = deviceMapper.selectById(deviceDTO.getId());
        if (existing == null) {
            throw new BusinessException("设备不存在");
        }

        // 校验设备编码是否与其他设备重复
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getDeviceCode, deviceDTO.getDeviceCode())
               .ne(Device::getId, deviceDTO.getId());
        if (deviceMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("设备编码已存在");
        }

        Device device = new Device();
        BeanUtils.copyProperties(deviceDTO, device);
        deviceMapper.updateById(device);
    }

    @Override
    public void deleteDevice(Long id) {
        if (deviceMapper.selectById(id) == null) {
            throw new BusinessException("设备不存在");
        }

        // 检查是否存在关联的库存记录
        LambdaQueryWrapper<Inventory> inventoryWrapper = new LambdaQueryWrapper<>();
        inventoryWrapper.eq(Inventory::getDeviceId, id);
        if (inventoryMapper.selectCount(inventoryWrapper) > 0) {
            throw new BusinessException("该设备存在库存记录，无法删除");
        }

        // 检查是否存在关联的出入库记录
        LambdaQueryWrapper<StockRecord> stockWrapper = new LambdaQueryWrapper<>();
        stockWrapper.eq(StockRecord::getDeviceId, id);
        if (stockRecordMapper.selectCount(stockWrapper) > 0) {
            throw new BusinessException("该设备存在出入库记录，无法删除");
        }

        // 检查是否存在关联的采购订单
        LambdaQueryWrapper<PurchaseOrder> purchaseWrapper = new LambdaQueryWrapper<>();
        purchaseWrapper.eq(PurchaseOrder::getDeviceId, id);
        if (purchaseOrderMapper.selectCount(purchaseWrapper) > 0) {
            throw new BusinessException("该设备存在采购记录，无法删除");
        }

        // 检查是否存在关联的报损记录
        LambdaQueryWrapper<DeviceScrap> scrapWrapper = new LambdaQueryWrapper<>();
        scrapWrapper.eq(DeviceScrap::getDeviceId, id);
        if (deviceScrapMapper.selectCount(scrapWrapper) > 0) {
            throw new BusinessException("该设备存在报损记录，无法删除");
        }

        deviceMapper.deleteById(id);
    }

    @Override
    public DeviceVO getDeviceById(Long id) {
        Device device = deviceMapper.selectById(id);
        if (device == null) {
            throw new BusinessException("设备不存在");
        }
        DeviceVO vo = new DeviceVO();
        BeanUtils.copyProperties(device, vo);
        return vo;
    }

    @Override
    public IPage<DeviceVO> getDevicePage(int pageNum, int pageSize, String keyword) {
        Page<Device> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w
                .like(Device::getDeviceCode, keyword.trim())
                .or()
                .like(Device::getDeviceName, keyword.trim()));
        }
        wrapper.orderByDesc(Device::getCreateTime);
        IPage<Device> devicePage = deviceMapper.selectPage(page, wrapper);
        return devicePage.convert(this::toVO);
    }

    private DeviceVO toVO(Device device) {
        DeviceVO vo = new DeviceVO();
        BeanUtils.copyProperties(device, vo);
        // 查询分类名称
        if (device.getCategoryId() != null) {
            DeviceCategory category = categoryMapper.selectById(device.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getCategoryName());
            }
        }
        return vo;
    }
}
