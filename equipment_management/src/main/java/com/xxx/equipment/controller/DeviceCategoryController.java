package com.xxx.equipment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xxx.equipment.common.Result;
import com.xxx.equipment.common.annotation.OperationLog;
import com.xxx.equipment.dto.CategoryDTO;
import com.xxx.equipment.service.DeviceCategoryService;
import com.xxx.equipment.vo.CategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "设备分类管理")
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class DeviceCategoryController {

    private final DeviceCategoryService categoryService;

    @Operation(summary = "新增分类")
    @OperationLog("新增设备分类")
    @PostMapping
    public Result<Void> addCategory(@RequestBody @Valid CategoryDTO dto) {
        categoryService.addCategory(dto);
        return Result.success();
    }

    @Operation(summary = "修改分类")
    @OperationLog("修改设备分类")
    @PutMapping
    public Result<Void> updateCategory(@RequestBody @Valid CategoryDTO dto) {
        categoryService.updateCategory(dto);
        return Result.success();
    }

    @Operation(summary = "删除分类")
    @OperationLog("删除设备分类")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }

    @Operation(summary = "分页查询分类")
    @GetMapping("/page")
    public Result<IPage<CategoryVO>> getCategoryPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(categoryService.getCategoryPage(pageNum, pageSize));
    }
}
