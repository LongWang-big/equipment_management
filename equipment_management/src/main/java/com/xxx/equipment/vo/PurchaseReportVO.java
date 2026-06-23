package com.xxx.equipment.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseReportVO {

    /** 设备名称 */
    private String deviceName;

    /** 采购数量 */
    private Integer purchaseQuantity;

    /** 采购金额 */
    private BigDecimal totalAmount;

    /** 年月（月报用，如 2026-06） */
    private String yearMonth;

    /** 年份（年报用，如 2026） */
    private String year;
}
