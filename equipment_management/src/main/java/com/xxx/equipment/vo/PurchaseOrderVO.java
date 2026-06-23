package com.xxx.equipment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PurchaseOrderVO {

    private Long id;

    private Long deviceId;

    /** 设备名称 */
    private String deviceName;

    /** 采购数量 */
    private Integer purchaseQuantity;

    /** 采购单价 */
    private BigDecimal purchasePrice;

    /** 采购总金额 */
    private BigDecimal totalAmount;

    /** 采购员 */
    private String purchaser;

    /** 采购日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
