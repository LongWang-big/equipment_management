package com.xxx.equipment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xxx.equipment.dto.DeviceDTO;
import com.xxx.equipment.vo.DeviceVO;

public interface DeviceService {

    void addDevice(DeviceDTO deviceDTO);

    void updateDevice(DeviceDTO deviceDTO);

    void deleteDevice(Long id);

    DeviceVO getDeviceById(Long id);

    IPage<DeviceVO> getDevicePage(int pageNum, int pageSize, String keyword);
}
