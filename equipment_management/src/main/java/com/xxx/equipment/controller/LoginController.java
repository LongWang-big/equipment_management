package com.xxx.equipment.controller;

import com.xxx.equipment.common.Result;
import com.xxx.equipment.dto.LoginDTO;
import com.xxx.equipment.dto.RegisterDTO;
import com.xxx.equipment.exception.BusinessException;
import com.xxx.equipment.service.LoginService;
import com.xxx.equipment.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Tag(name = "登录管理")
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    /** 简易登录限流：同一 IP 5 分钟内最多尝试 5 次 */
    private final ConcurrentHashMap<String, long[]> loginAttempts = new ConcurrentHashMap<>();

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO loginDTO, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        checkRateLimit(ip);
        try {
            return Result.success(loginService.login(loginDTO));
        } catch (BusinessException e) {
            recordFailedAttempt(ip);
            throw e;
        }
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid RegisterDTO registerDTO) {
        loginService.register(registerDTO);
        return Result.success();
    }

    private void checkRateLimit(String ip) {
        long[] record = loginAttempts.get(ip);
        if (record != null && System.currentTimeMillis() - record[1] > 5 * 60 * 1000) {
            loginAttempts.remove(ip);
            record = null;
        }
        if (record != null && record[0] >= 5) {
            throw new BusinessException("登录尝试次数过多，请5分钟后再试");
        }
    }

    private void recordFailedAttempt(String ip) {
        loginAttempts.compute(ip, (key, val) -> {
            if (val == null || System.currentTimeMillis() - val[1] > 5 * 60 * 1000) {
                return new long[]{1, System.currentTimeMillis()};
            }
            val[0]++;
            val[1] = System.currentTimeMillis();
            return val;
        });
    }
}
