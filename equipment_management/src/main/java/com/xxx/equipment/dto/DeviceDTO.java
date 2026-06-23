package com.xxx.equipment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeviceDTO {

    /** 主键，有值为修改，无值为新增 */
    private Long id;

    @NotBlank(message = "设备编码不能为空")
    private String deviceCode;

    @NotBlank(message = "设备名称不能为空")
    private String deviceName;

    /** 分类ID */
    private Long categoryId;

    private String supplier;

    private String model;

    private BigDecimal unitPrice;

    private String manufacturer;

    private String remark;
}
