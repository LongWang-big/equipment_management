package com.xxx.equipment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xxx.equipment.common.Result;
import com.xxx.equipment.common.annotation.OperationLog;
import com.xxx.equipment.dto.ScrapDTO;
import com.xxx.equipment.service.DeviceScrapService;
import com.xxx.equipment.vo.ScrapVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "设备报损管理")
@RestController
@RequestMapping("/scrap")
@RequiredArgsConstructor
public class DeviceScrapController {

    private final DeviceScrapService scrapService;

    @Operation(summary = "新增报损申请")
    @OperationLog("新增设备报损")
    @PostMapping
    public Result<Void> addScrap(@RequestBody @Valid ScrapDTO dto) {
        scrapService.addScrap(dto);
        return Result.success();
    }

    @Operation(summary = "审批报损")
    @OperationLog("审批设备报损")
    @PutMapping("/approve")
    public Result<Void> approveScrap(@RequestBody ScrapDTO dto) {
        scrapService.approveScrap(dto);
        return Result.success();
    }

    @Operation(summary = "删除报损记录")
    @OperationLog("删除设备报损")
    @DeleteMapping("/{id}")
    public Result<Void> deleteScrap(@PathVariable Long id) {
        scrapService.deleteScrap(id);
        return Result.success();
    }

    @Operation(summary = "分页查询报损记录")
    @GetMapping("/page")
    public Result<IPage<ScrapVO>> getScrapPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status) {
        return Result.success(scrapService.getScrapPage(pageNum, pageSize, status));
    }
}
