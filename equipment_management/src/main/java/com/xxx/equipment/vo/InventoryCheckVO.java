package com.xxx.equipment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryCheckVO {

    private Long id;

    private Long deviceId;

    /** 设备名称 */
    private String deviceName;

    /** 系统库存 */
    private Integer systemQuantity;

    /** 实际库存 */
    private Integer actualQuantity;

    /** 差异数量 */
    private Integer differenceQuantity;

    /** 盘点人 */
    private String checker;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkTime;
}
