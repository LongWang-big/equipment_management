package com.xxx.equipment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xxx.equipment.common.Result;
import com.xxx.equipment.service.StockRecordService;
import com.xxx.equipment.vo.StockRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "出入库记录")
@RestController
@RequestMapping("/stock-record")
@RequiredArgsConstructor
public class StockRecordController {

    private final StockRecordService stockRecordService;

    @Operation(summary = "分页查询出入库记录")
    @GetMapping("/page")
    public Result<IPage<StockRecordVO>> getRecordPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String operationType) {
        return Result.success(stockRecordService.getRecordPage(pageNum, pageSize, operationType));
    }
}
