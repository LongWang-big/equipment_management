package com.xxx.equipment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("device")
public class Device {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 设备编码 */
    private String deviceCode;

    /** 设备名称 */
    private String deviceName;

    /** 分类ID */
    private Long categoryId;

    /** 供应商 */
    private String supplier;

    /** 规格型号 */
    private String model;

    /** 单价 */
    private BigDecimal unitPrice;

    /** 生产厂家 */
    private String manufacturer;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;
}
