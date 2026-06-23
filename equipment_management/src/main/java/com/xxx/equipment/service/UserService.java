package com.xxx.equipment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xxx.equipment.dto.UserDTO;
import com.xxx.equipment.vo.UserVO;

public interface UserService {

    void addUser(UserDTO dto);

    void updateUser(UserDTO dto);

    void deleteUser(Long id);

    void resetPassword(Long id, String newPassword);

    void changePassword(String username, String oldPassword, String newPassword);

    IPage<UserVO> getUserPage(int pageNum, int pageSize, String keyword);
}
