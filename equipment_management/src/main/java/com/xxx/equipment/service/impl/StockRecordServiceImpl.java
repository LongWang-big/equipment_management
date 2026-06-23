package com.xxx.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxx.equipment.entity.Device;
import com.xxx.equipment.entity.StockRecord;
import com.xxx.equipment.mapper.DeviceMapper;
import com.xxx.equipment.mapper.StockRecordMapper;
import com.xxx.equipment.service.StockRecordService;
import com.xxx.equipment.vo.StockRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockRecordServiceImpl implements StockRecordService {

    private final StockRecordMapper stockRecordMapper;
    private final DeviceMapper deviceMapper;

    @Override
    public IPage<StockRecordVO> getRecordPage(int pageNum, int pageSize, String operationType) {
        Page<StockRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<StockRecord> wrapper = new LambdaQueryWrapper<>();
        if (operationType != null && !operationType.isEmpty()) {
            wrapper.eq(StockRecord::getOperationType, operationType);
        }
        wrapper.orderByDesc(StockRecord::getOperationTime);
        IPage<StockRecord> recordPage = stockRecordMapper.selectPage(page, wrapper);

        // 批量查询设备名称
        List<Long> deviceIds = recordPage.getRecords().stream()
                .map(StockRecord::getDeviceId).distinct().toList();
        Map<Long, String> deviceNameMap = new HashMap<>();
        if (!deviceIds.isEmpty()) {
            deviceMapper.selectBatchIds(deviceIds)
                    .forEach(d -> deviceNameMap.put(d.getId(), d.getDeviceName()));
        }

        return recordPage.convert(record -> {
            StockRecordVO vo = new StockRecordVO();
            BeanUtils.copyProperties(record, vo);
            vo.setDeviceName(deviceNameMap.get(record.getDeviceId()));
            return vo;
        });
    }
}
