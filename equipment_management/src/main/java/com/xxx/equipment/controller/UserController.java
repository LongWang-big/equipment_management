package com.xxx.equipment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xxx.equipment.common.Result;
import com.xxx.equipment.common.annotation.OperationLog;
import com.xxx.equipment.dto.UserDTO;
import com.xxx.equipment.service.UserService;
import com.xxx.equipment.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "新增用户")
    @OperationLog("新增用户")
    @PostMapping
    public Result<Void> addUser(@RequestBody @Valid UserDTO dto) {
        userService.addUser(dto);
        return Result.success();
    }

    @Operation(summary = "修改用户")
    @OperationLog("修改用户")
    @PutMapping
    public Result<Void> updateUser(@RequestBody @Valid UserDTO dto) {
        userService.updateUser(dto);
        return Result.success();
    }

    @Operation(summary = "删除用户")
    @OperationLog("删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @Operation(summary = "重置密码（管理员）")
    @OperationLog("重置用户密码")
    @PutMapping("/{id}/password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        userService.resetPassword(id, newPassword);
        return Result.success();
    }

    @Operation(summary = "修改密码（当前用户）")
    @OperationLog("修改个人密码")
    @PutMapping("/change-password")
    public Result<Void> changePassword(@RequestBody java.util.Map<String, String> params) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        userService.changePassword(username, oldPassword, newPassword);
        return Result.success();
    }

    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    public Result<IPage<UserVO>> getUserPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(userService.getUserPage(pageNum, pageSize, keyword));
    }
}
