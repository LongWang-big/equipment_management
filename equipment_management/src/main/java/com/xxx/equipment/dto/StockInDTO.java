package com.xxx.equipment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockInDTO {

    @NotNull(message = "设备ID不能为空")
    private Long deviceId;

    @NotNull(message = "入库数量不能为空")
    @Min(value = 1, message = "入库数量必须大于0")
    private Integer quantity;

    @NotBlank(message = "经手人不能为空")
    private String operatorName;

    @NotBlank(message = "存放位置不能为空")
    private String targetPlace;
}
