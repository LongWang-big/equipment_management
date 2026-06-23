package com.xxx.equipment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xxx.equipment.common.Result;
import com.xxx.equipment.common.annotation.OperationLog;
import com.xxx.equipment.dto.PurchaseOrderDTO;
import com.xxx.equipment.service.PurchaseOrderService;
import com.xxx.equipment.vo.PurchaseOrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "采购订单管理")
@RestController
@RequestMapping("/purchase-order")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @Operation(summary = "新增采购订单")
    @OperationLog("新增采购订单")
    @PostMapping
    public Result<Void> addOrder(@RequestBody @Valid PurchaseOrderDTO dto) {
        purchaseOrderService.addOrder(dto);
        return Result.success();
    }

    @Operation(summary = "修改采购订单")
    @OperationLog("修改采购订单")
    @PutMapping
    public Result<Void> updateOrder(@RequestBody @Valid PurchaseOrderDTO dto) {
        purchaseOrderService.updateOrder(dto);
        return Result.success();
    }

    @Operation(summary = "删除采购订单")
    @OperationLog("删除采购订单")
    @DeleteMapping("/{id}")
    public Result<Void> deleteOrder(@PathVariable Long id) {
        purchaseOrderService.deleteOrder(id);
        return Result.success();
    }

    @Operation(summary = "根据ID查询采购订单")
    @GetMapping("/{id}")
    public Result<PurchaseOrderVO> getOrderById(@PathVariable Long id) {
        return Result.success(purchaseOrderService.getOrderById(id));
    }

    @Operation(summary = "分页查询采购订单")
    @GetMapping("/page")
    public Result<IPage<PurchaseOrderVO>> getOrderPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(purchaseOrderService.getOrderPage(pageNum, pageSize, startDate, endDate));
    }
}
