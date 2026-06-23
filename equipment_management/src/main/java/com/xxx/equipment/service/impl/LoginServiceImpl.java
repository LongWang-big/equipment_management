package com.xxx.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xxx.equipment.common.JwtUtil;
import com.xxx.equipment.dto.LoginDTO;
import com.xxx.equipment.dto.RegisterDTO;
import com.xxx.equipment.entity.SysUser;
import com.xxx.equipment.exception.BusinessException;
import com.xxx.equipment.mapper.SysUserMapper;
import com.xxx.equipment.service.LoginService;
import com.xxx.equipment.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final SysUserMapper sysUserMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 1. 查询用户是否存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, loginDTO.getUsername());
        SysUser user = sysUserMapper.selectOne(wrapper);

        // 2. 校验密码（统一错误提示，防止用户枚举）
        if (user == null || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 3. 生成 Token
        String token = jwtUtil.generateToken(user.getUsername());

        // 4. 返回
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setRealName(user.getRealName());
        loginVO.setRole(user.getRole());
        return loginVO;
    }

    @Override
    public void register(RegisterDTO registerDTO) {
        // 1. 检查用户名是否已存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, registerDTO.getUsername());
        if (sysUserMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 2. 创建用户
        SysUser user = new SysUser();
        user.setUsername(registerDTO.getUsername());
        user.setRealName(registerDTO.getRealName());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRole("user");

        // 3. 插入数据库
        try {
            sysUserMapper.insert(user);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("用户名已存在");
        }
    }
}
