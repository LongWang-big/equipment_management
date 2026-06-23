package com.xxx.equipment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("stock_record")
public class StockRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 设备ID */
    private Long deviceId;

    /** 操作类型 IN/OUT/RETURN */
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

    /** 操作时间 */
    private LocalDateTime operationTime;
}
