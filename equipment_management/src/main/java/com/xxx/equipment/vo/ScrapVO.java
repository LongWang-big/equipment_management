package com.xxx.equipment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ScrapVO {

    private Long id;

    private Long deviceId;

    /** 设备名称 */
    private String deviceName;

    /** 设备编码 */
    private String deviceCode;

    /** 报损数量 */
    private Integer scrapQuantity;

    /** 报损原因 */
    private String scrapReason;

    /** 报损类型 */
    private String scrapType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scrapDate;

    /** 申请人 */
    private String applicant;

    /** 审批人 */
    private String approver;

    /** 状态 */
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
