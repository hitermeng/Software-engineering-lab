package com.hit.articlemgr.controller;

import com.hit.articlemgr.dto.LoginDTO;
import com.hit.articlemgr.dto.RegisterDTO;
import com.hit.articlemgr.dto.TokenDTO;
import com.hit.articlemgr.entity.User;
import com.hit.articlemgr.service.UserService;
import com.hit.articlemgr.util.JwtUtil;
import com.hit.articlemgr.util.R;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author HIT
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public R<Void> register(@Validated @RequestBody RegisterDTO registerDTO) {
        try {
            userService.register(registerDTO);
            return R.success("注册成功", null);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public R<TokenDTO> login(@Validated @RequestBody LoginDTO loginDTO) {
        try {
            TokenDTO tokenDTO = userService.login(loginDTO);
            return R.success("登录成功", tokenDTO);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public R<User> getProfile(@AuthenticationPrincipal Long userId) {
        try {
            User user = userService.getUserById(userId);
            if (user != null) {
                // 不返回密码
                user.setPassword(null);
            }
            return R.success(user);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    public R<Void> updateProfile(@AuthenticationPrincipal Long userId,
                                 @RequestBody User userUpdate) {
        try {
            User existingUser = userService.getUserById(userId);
            if (existingUser == null) {
                return R.error("用户不存在");
            }

            // 只允许更新特定字段
            existingUser.setNickname(userUpdate.getNickname());
            existingUser.setAvatar(userUpdate.getAvatar());

            userService.updateUser(existingUser);
            return R.success("更新成功", null);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 验证Token
     */
    @PostMapping("/validate")
    public R<Boolean> validateToken(@RequestParam String token) {
        try {
            boolean isValid = jwtUtil.validateToken(token);
            return R.success(isValid);
        } catch (Exception e) {
            return R.success(false);
        }
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public R<Void> logout() {
        // 在实际应用中，可以将token加入黑名单
        return R.success("退出成功", null);
    }
}