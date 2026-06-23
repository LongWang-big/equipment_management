package com.xxx.equipment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("purchase_order")
public class PurchaseOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 设备ID */
    private Long deviceId;

    /** 采购数量 */
    private Integer purchaseQuantity;

    /** 采购单价 */
    private BigDecimal purchasePrice;

    /** 采购总金额 */
    private BigDecimal totalAmount;

    /** 采购员 */
    private String purchaser;

    /** 采购日期 */
    private LocalDate purchaseDate;

    /** 创建时间 */
    private LocalDateTime createTime;
}
