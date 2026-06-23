package com.xxx.equipment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDTO {

    /** 主键，有值为修改，无值为新增 */
    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 密码（新增时必填，修改时可为空表示不修改） */
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @NotBlank(message = "角色不能为空")
    private String role;
}
