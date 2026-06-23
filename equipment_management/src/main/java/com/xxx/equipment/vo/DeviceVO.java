package com.xxx.equipment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DeviceVO {

    private Long id;

    private String deviceCode;

    private String deviceName;

    /** 分类ID */
    private Long categoryId;

    /** 分类名称 */
    private String categoryName;

    private String supplier;

    private String model;

    private BigDecimal unitPrice;

    private String manufacturer;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
