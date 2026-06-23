package com.xxx.equipment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xxx.equipment.common.Result;
import com.xxx.equipment.common.annotation.OperationLog;
import com.xxx.equipment.dto.InventoryCheckDTO;
import com.xxx.equipment.dto.StockInDTO;
import com.xxx.equipment.dto.StockOutDTO;
import com.xxx.equipment.dto.StockReturnDTO;
import com.xxx.equipment.service.InventoryService;
import com.xxx.equipment.vo.InventoryCheckVO;
import com.xxx.equipment.vo.InventoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "库存管理")
@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "库存分页查询")
    @GetMapping("/page")
    public Result<IPage<InventoryVO>> getInventoryPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(inventoryService.getInventoryPage(pageNum, pageSize));
    }

    @Operation(summary = "库存预警查询")
    @GetMapping("/warning")
    public Result<IPage<InventoryVO>> getWarningList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(inventoryService.getWarningList(pageNum, pageSize));
    }

    @Operation(summary = "设备入库")
    @OperationLog("设备入库")
    @PostMapping("/in")
    public Result<Void> stockIn(@RequestBody @Valid StockInDTO stockInDTO) {
        inventoryService.stockIn(stockInDTO);
        return Result.success();
    }

    @Operation(summary = "设备出库")
    @OperationLog("设备出库")
    @PostMapping("/out")
    public Result<Void> stockOut(@RequestBody @Valid StockOutDTO stockOutDTO) {
        inventoryService.stockOut(stockOutDTO);
        return Result.success();
    }

    @Operation(summary = "设备退库")
    @OperationLog("设备退库")
    @PostMapping("/return")
    public Result<Void> stockReturn(@RequestBody @Valid StockReturnDTO stockReturnDTO) {
        inventoryService.stockReturn(stockReturnDTO);
        return Result.success();
    }

    @Operation(summary = "设备盘点")
    @OperationLog("设备盘点")
    @PostMapping("/check")
    public Result<InventoryCheckVO> check(@RequestBody @Valid InventoryCheckDTO inventoryCheckDTO) {
        return Result.success(inventoryService.check(inventoryCheckDTO));
    }
}
