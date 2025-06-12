package com.hit.articlemgr.controller;

import com.hit.articlemgr.dto.UserUpdateDTO;
import com.hit.articlemgr.entity.User;
import com.hit.articlemgr.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        // 从认证信息中获取用户ID
        Long userId = Long.parseLong(authentication.getName());
        
        // 更新用户信息
        User updatedUser = userService.updateUserProfile(userId, userUpdateDTO);
        
        return ResponseEntity.ok(updatedUser);
    }
} 