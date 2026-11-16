package com.outdoor.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.outdoor.server.dto.LoginDTO;
import com.outdoor.server.dto.RegisterDTO;
import com.outdoor.server.dto.UpdateProfileDTO;
import com.outdoor.server.dto.ChangePasswordDTO;
import com.outdoor.server.entity.User;

import java.util.Map;

public interface UserService extends IService<User> {
    Map<String, Object> register(RegisterDTO dto);
    Map<String, Object> login(LoginDTO dto);
    User info(Long userId);
    User updateProfile(Long userId, UpdateProfileDTO dto);
    void changePassword(Long userId, ChangePasswordDTO dto);
}
