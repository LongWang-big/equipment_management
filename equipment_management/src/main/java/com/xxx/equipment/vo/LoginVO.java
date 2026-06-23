package com.xxx.equipment.vo;

import lombok.Data;

@Data
public class LoginVO {

    private String token;

    /** 真实姓名 */
    private String realName;

    /** 角色 */
    private String role;
}
