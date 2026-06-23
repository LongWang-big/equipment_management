package com.xxx.equipment.service;

import com.xxx.equipment.dto.LoginDTO;
import com.xxx.equipment.dto.RegisterDTO;
import com.xxx.equipment.vo.LoginVO;

public interface LoginService {

    LoginVO login(LoginDTO loginDTO);

    void register(RegisterDTO registerDTO);
}
