package com.xxx.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxx.equipment.dto.PurchaseOrderDTO;
import com.xxx.equipment.entity.Device;
import com.xxx.equipment.entity.PurchaseOrder;
import com.xxx.equipment.exception.BusinessException;
import com.xxx.equipment.mapper.DeviceMapper;
import com.xxx.equipment.mapper.PurchaseOrderMapper;
import com.xxx.equipment.service.PurchaseOrderService;
import com.xxx.equipment.vo.PurchaseOrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderMapper purchaseOrderMapper;
    private final DeviceMapper deviceMapper;

    @Override
    public void addOrder(PurchaseOrderDTO dto) {
        // 校验设备是否存在
        Device device = deviceMapper.selectById(dto.getDeviceId());
        if (device == null) {
            throw new BusinessException("设备不存在");
        }

        PurchaseOrder order = new PurchaseOrder();
        BeanUtils.copyProperties(dto, order);
        // 自动计算总金额
        order.setTotalAmount(dto.getPurchasePrice().multiply(
                new java.math.BigDecimal(dto.getPurchaseQuantity())));
        order.setCreateTime(java.time.LocalDateTime.now());
        purchaseOrderMapper.insert(order);
    }

    @Override
    public void updateOrder(PurchaseOrderDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("采购订单ID不能为空");
        }

        PurchaseOrder existing = purchaseOrderMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("采购订单不存在");
        }

        // 校验设备是否存在
        Device device = deviceMapper.selectById(dto.getDeviceId());
        if (device == null) {
            throw new BusinessException("设备不存在");
        }

        PurchaseOrder order = new PurchaseOrder();
        BeanUtils.copyProperties(dto, order);
        // 自动计算总金额
        order.setTotalAmount(dto.getPurchasePrice().multiply(
                new java.math.BigDecimal(dto.getPurchaseQuantity())));
        purchaseOrderMapper.updateById(order);
    }

    @Override
    public void deleteOrder(Long id) {
        if (purchaseOrderMapper.selectById(id) == null) {
            throw new BusinessException("采购订单不存在");
        }
        purchaseOrderMapper.deleteById(id);
    }

    @Override
    public PurchaseOrderVO getOrderById(Long id) {
        PurchaseOrder order = purchaseOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("采购订单不存在");
        }
        return toVO(order);
    }

    @Override
    public IPage<PurchaseOrderVO> getOrderPage(int pageNum, int pageSize, String startDate, String endDate) {
        Page<PurchaseOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(PurchaseOrder::getPurchaseDate, startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(PurchaseOrder::getPurchaseDate, endDate);
        }
        wrapper.orderByDesc(PurchaseOrder::getPurchaseDate);
        IPage<PurchaseOrder> orderPage = purchaseOrderMapper.selectPage(page, wrapper);

        // 批量查询关联设备名称，避免 N+1
        List<Long> deviceIds = orderPage.getRecords().stream()
                .map(PurchaseOrder::getDeviceId)
                .distinct()
                .toList();
        Map<Long, String> deviceNameMap = new HashMap<>();
        if (!deviceIds.isEmpty()) {
            List<Device> devices = deviceMapper.selectBatchIds(deviceIds);
            devices.forEach(d -> deviceNameMap.put(d.getId(), d.getDeviceName()));
        }

        return orderPage.convert(order -> {
            PurchaseOrderVO vo = new PurchaseOrderVO();
            BeanUtils.copyProperties(order, vo);
            vo.setDeviceName(deviceNameMap.get(order.getDeviceId()));
            return vo;
        });
    }

    private PurchaseOrderVO toVO(PurchaseOrder order) {
        PurchaseOrderVO vo = new PurchaseOrderVO();
        BeanUtils.copyProperties(order, vo);
        // 查询设备名称
        Device device = deviceMapper.selectById(order.getDeviceId());
        if (device != null) {
            vo.setDeviceName(device.getDeviceName());
        }
        return vo;
    }
}
