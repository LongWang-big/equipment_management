package com.xxx.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxx.equipment.dto.UserDTO;
import com.xxx.equipment.entity.SysUser;
import com.xxx.equipment.exception.BusinessException;
import com.xxx.equipment.mapper.SysUserMapper;
import com.xxx.equipment.service.UserService;
import com.xxx.equipment.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void addUser(UserDTO dto) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        // 新增用户必须设置密码
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new BusinessException("密码不能为空");
        }
        if (dto.getPassword().length() < 6) {
            throw new BusinessException("密码长度不能少于6位");
        }
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        try {
            sysUserMapper.insert(user);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("用户名已存在");
        }
    }

    @Override
    public void updateUser(UserDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        SysUser existing = sysUserMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        // 修改时如果不传密码则不修改密码
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            user.setPassword(null);
        } else {
            if (dto.getPassword().length() < 6) {
                throw new BusinessException("密码长度不能少于6位");
            }
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        try {
            sysUserMapper.updateById(user);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("用户名已存在");
        }
    }

    @Override
    public void deleteUser(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if ("admin".equals(user.getUsername())) {
            throw new BusinessException("不能删除管理员账号");
        }
        sysUserMapper.deleteById(id);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new BusinessException("密码长度不能少于6位");
        }
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        sysUserMapper.updateById(user);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser user = sysUserMapper.selectOne(wrapper);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new BusinessException("新密码长度不能少于6位");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        sysUserMapper.updateById(user);
    }

    @Override
    public IPage<UserVO> getUserPage(int pageNum, int pageSize, String keyword) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w
                .like(SysUser::getUsername, keyword.trim())
                .or()
                .like(SysUser::getRealName, keyword.trim()));
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        IPage<SysUser> userPage = sysUserMapper.selectPage(page, wrapper);
        return userPage.convert(this::toVO);
    }

    private UserVO toVO(SysUser user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
