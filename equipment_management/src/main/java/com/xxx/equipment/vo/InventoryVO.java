package com.xxx.equipment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryVO {

    private Long id;

    private Long deviceId;

    /** 设备名称（来自 device 表） */
    private String deviceName;

    /** 库存数量 */
    private Integer quantity;

    /** 设备状态 */
    private String status;

    /** 库存预警值 */
    private Integer warningValue;

    /** 存放位置 */
    private String location;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
