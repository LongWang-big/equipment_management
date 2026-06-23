package com.xxx.equipment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("device_scrap")
public class DeviceScrap {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 设备ID */
    private Long deviceId;

    /** 报损数量 */
    private Integer scrapQuantity;

    /** 报损原因 */
    private String scrapReason;

    /** 报损类型：SCRAP/REPAIR/DAMAGE */
    private String scrapType;

    /** 报损日期 */
    private LocalDate scrapDate;

    /** 申请人 */
    private String applicant;

    /** 审批人 */
    private String approver;

    /** 状态：PENDING/APPROVED/REJECTED */
    private String status;

    /** 创建时间 */
    private LocalDateTime createTime;
}
