package com.xxx.equipment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("inventory_check")
public class InventoryCheck {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 设备ID */
    private Long deviceId;

    /** 系统库存 */
    private Integer systemQuantity;

    /** 实际库存 */
    private Integer actualQuantity;

    /** 差异数量 */
    private Integer differenceQuantity;

    /** 盘点人 */
    private String checker;

    /** 盘点时间 */
    private LocalDateTime checkTime;
}
