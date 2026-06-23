package com.xxx.equipment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryCheckDTO {

    @NotNull(message = "设备ID不能为空")
    private Long deviceId;

    @NotNull(message = "实际库存不能为空")
    @Min(value = 0, message = "实际库存不能小于0")
    private Integer actualQuantity;

    @NotBlank(message = "盘点人不能为空")
    private String checker;
}
