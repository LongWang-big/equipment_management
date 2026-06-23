package com.xxx.equipment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("operation_log")
public class OperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名 */
    private String username;

    /** 操作内容 */
    private String operationContent;

    /** 请求方法 */
    private String requestMethod;

    /** 请求路径 */
    private String requestUrl;

    /** 操作IP */
    private String ip;

    /** 操作结果 0失败 1成功 */
    private Integer result;

    /** 操作时间 */
    private LocalDateTime operationTime;
}
