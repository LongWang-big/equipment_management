package com.xxx.equipment.controller;

import com.xxx.equipment.common.Result;
import com.xxx.equipment.service.PurchaseReportService;
import com.xxx.equipment.vo.PurchaseReportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "采购报表")
@RestController
@RequestMapping("/report/purchase")
@RequiredArgsConstructor
@Validated
public class PurchaseReportController {

    private final PurchaseReportService purchaseReportService;

    @Operation(summary = "按月统计采购金额")
    @GetMapping("/month")
    public Result<List<PurchaseReportVO>> getMonthlyReport(
            @RequestParam @NotBlank(message = "年份不能为空")
            @Pattern(regexp = "^\\d{4}$", message = "年份格式不正确，应为4位数字") String year) {
        return Result.success(purchaseReportService.getMonthlyReport(year));
    }

    @Operation(summary = "按年统计采购金额")
    @GetMapping("/year")
    public Result<List<PurchaseReportVO>> getYearlyReport(
            @RequestParam @NotBlank(message = "年份不能为空")
            @Pattern(regexp = "^\\d{4}$", message = "年份格式不正确，应为4位数字") String year) {
        return Result.success(purchaseReportService.getYearlyReport(year));
    }
}
