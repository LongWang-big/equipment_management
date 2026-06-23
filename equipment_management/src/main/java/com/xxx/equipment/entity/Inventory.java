package com.xxx.equipment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("inventory")
public class Inventory {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 设备ID */
    private Long deviceId;

    /** 库存数量 */
    private Integer quantity;

    /** 设备状态 */
    private String status;

    /** 库存预警值 */
    private Integer warningValue;

    /** 存放位置 */
    private String location;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
