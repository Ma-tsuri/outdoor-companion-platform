package com.outdoor.server.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.outdoor.server.dto.LoginDTO;
import com.outdoor.server.dto.RegisterDTO;
import com.outdoor.server.dto.UpdateProfileDTO;
import com.outdoor.server.dto.ChangePasswordDTO;
import com.outdoor.server.entity.User;
import com.outdoor.server.mapper.UserMapper;
import com.outdoor.server.service.UserService;
import com.outdoor.server.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Map<String, Object> register(RegisterDTO dto) {
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(SecureUtil.md5(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole() == null ? 0 : dto.getRole());
        user.setStatus(1);
        save(user);
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("user", user);
        return map;
    }

    @Override
    public Map<String, Object> login(LoginDTO dto) {
        User user = baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (user == null || !user.getPassword().equals(SecureUtil.md5(dto.getPassword()))) {
            throw new RuntimeException("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("user", user);
        return map;
    }

    @Override
    public User info(Long userId) {
        return getById(userId);
    }

    @Override
    public User updateProfile(Long userId, UpdateProfileDTO dto) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setAvatar(dto.getAvatar());
        updateById(user);
        return user;
    }

    @Override
    public void changePassword(Long userId, ChangePasswordDTO dto) {
        User user = getById(userId);
        if (user == null || !user.getPassword().equals(SecureUtil.md5(dto.getOldPassword()))) {
            throw new RuntimeException("原密码不正确");
        }
        user.setPassword(SecureUtil.md5(dto.getNewPassword()));
        updateById(user);
    }
}
