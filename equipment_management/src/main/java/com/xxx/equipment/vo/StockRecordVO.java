package com.xxx.equipment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StockRecordVO {

    private Long id;

    private Long deviceId;

    /** 设备名称 */
    private String deviceName;

    /** 操作类型 */
    private String operationType;

    /** 数量 */
    private Integer quantity;

    /** 领用人 */
    private String receiver;

    /** 来源 */
    private String sourcePlace;

    /** 去处 */
    private String targetPlace;

    /** 经手人 */
    private String operatorName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operationTime;
}
