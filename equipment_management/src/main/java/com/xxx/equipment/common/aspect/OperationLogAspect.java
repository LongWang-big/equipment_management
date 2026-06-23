package com.xxx.equipment.common.aspect;

import com.xxx.equipment.common.JwtUtil;
import com.xxx.equipment.mapper.OperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogMapper operationLogMapper;
    private final JwtUtil jwtUtil;

    @Around("@annotation(com.xxx.equipment.common.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 获取注解上的操作描述
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        com.xxx.equipment.common.annotation.OperationLog annotation =
                signature.getMethod().getAnnotation(com.xxx.equipment.common.annotation.OperationLog.class);
        String operationContent = annotation.value();

        // 2. 获取当前请求信息
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String username = "system";
        String requestMethod = "";
        String requestUrl = "";
        String ip = "";

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            requestMethod = request.getMethod();
            requestUrl = request.getRequestURI();
            ip = request.getRemoteAddr();
            username = getCurrentUsername();
        }

        Object result = null;
        boolean success = true;
        try {
            // 3. 执行目标方法
            result = joinPoint.proceed();
        } catch (Throwable e) {
            success = false;
            throw e;
        } finally {
            // 4. 记录操作日志
            com.xxx.equipment.entity.OperationLog logRecord = new com.xxx.equipment.entity.OperationLog();
            logRecord.setUsername(username);
            logRecord.setOperationContent(success ? operationContent : operationContent + "（失败）");
            logRecord.setRequestMethod(requestMethod);
            logRecord.setRequestUrl(requestUrl);
            logRecord.setIp(ip);
            logRecord.setResult(success ? 1 : 0);
            logRecord.setOperationTime(LocalDateTime.now());
            operationLogMapper.insert(logRecord);
        }

        return result;
    }

    /**
     * 从 Authorization 请求头中解析用户名
     */
    private String getCurrentUsername() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return "system";
            }
            HttpServletRequest request = attributes.getRequest();
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                return jwtUtil.getUsernameFromToken(token);
            }
        } catch (Exception e) {
            log.warn("解析当前用户失败", e);
        }
        return "system";
    }
}
