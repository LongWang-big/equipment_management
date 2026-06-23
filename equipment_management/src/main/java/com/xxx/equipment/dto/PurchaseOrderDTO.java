package com.xxx.equipment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PurchaseOrderDTO {

    /** 主键，有值为修改，无值为新增 */
    private Long id;

    @NotNull(message = "设备ID不能为空")
    private Long deviceId;

    @NotNull(message = "采购数量不能为空")
    @Min(value = 1, message = "采购数量必须大于0")
    private Integer purchaseQuantity;

    @NotNull(message = "采购单价不能为空")
    @Min(value = 0, message = "采购单价不能为负数")
    private BigDecimal purchasePrice;

    @NotBlank(message = "采购员不能为空")
    private String purchaser;

    @NotNull(message = "采购日期不能为空")
    private LocalDate purchaseDate;
}
