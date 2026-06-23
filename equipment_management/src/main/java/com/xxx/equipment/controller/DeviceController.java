package com.xxx.equipment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xxx.equipment.common.Result;
import com.xxx.equipment.common.annotation.OperationLog;
import com.xxx.equipment.dto.DeviceDTO;
import com.xxx.equipment.service.DeviceService;
import com.xxx.equipment.vo.DeviceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "设备管理")
@RestController
@RequestMapping("/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @Operation(summary = "新增设备")
    @OperationLog("新增设备")
    @PostMapping
    public Result<Void> addDevice(@RequestBody @Valid DeviceDTO deviceDTO) {
        deviceService.addDevice(deviceDTO);
        return Result.success();
    }

    @Operation(summary = "修改设备")
    @OperationLog("修改设备")
    @PutMapping
    public Result<Void> updateDevice(@RequestBody @Valid DeviceDTO deviceDTO) {
        deviceService.updateDevice(deviceDTO);
        return Result.success();
    }

    @Operation(summary = "删除设备")
    @OperationLog("删除设备")
    @DeleteMapping("/{id}")
    public Result<Void> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return Result.success();
    }

    @Operation(summary = "根据ID查询设备")
    @GetMapping("/{id}")
    public Result<DeviceVO> getDeviceById(@PathVariable Long id) {
        return Result.success(deviceService.getDeviceById(id));
    }

    @Operation(summary = "分页查询设备")
    @GetMapping("/page")
    public Result<IPage<DeviceVO>> getDevicePage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(deviceService.getDevicePage(pageNum, pageSize, keyword));
    }
}
