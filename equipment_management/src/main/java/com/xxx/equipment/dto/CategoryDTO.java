package com.xxx.equipment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryDTO {

    /** 主键，有值为修改，无值为新增 */
    private Long id;

    @NotBlank(message = "分类名称不能为空")
    private String categoryName;

    private String description;
}
