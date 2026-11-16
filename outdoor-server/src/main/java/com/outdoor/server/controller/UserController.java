package com.outdoor.server.controller;

import com.outdoor.server.common.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.outdoor.server.dto.LoginDTO;
import com.outdoor.server.dto.RegisterDTO;
import com.outdoor.server.dto.UpdateProfileDTO;
import com.outdoor.server.dto.ChangePasswordDTO;
import com.outdoor.server.entity.User;
import com.outdoor.server.service.UserService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "用户账号", description = "注册、登录、个人资料与密码管理")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public R<Map<String, Object>> register(@Valid @RequestBody RegisterDTO dto) {
        return R.ok(userService.register(dto));
    }

    @PostMapping("/login")
    public R<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        return R.ok(userService.login(dto));
    }

    @GetMapping("/info")
    public R<?> info(@RequestAttribute("userId") Long userId) {
        return R.ok(userService.info(userId));
    }

    @PutMapping("/profile")
    public R<User> updateProfile(@RequestAttribute("userId") Long userId, @RequestBody UpdateProfileDTO dto) {
        return R.ok(userService.updateProfile(userId, dto));
    }

    @PutMapping("/password")
    public R<?> changePassword(@RequestAttribute("userId") Long userId, @Valid @RequestBody ChangePasswordDTO dto) {
        userService.changePassword(userId, dto);
        return R.ok();
    }
}
