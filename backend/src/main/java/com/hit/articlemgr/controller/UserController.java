package com.hit.articlemgr.controller;

import com.hit.articlemgr.dto.UserUpdateDTO;
import com.hit.articlemgr.entity.User;
import com.hit.articlemgr.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        Long userId = Long.parseLong(authentication.getName());
        User updatedUser = userService.updateUserProfile(userId, userUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadAvatar(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {
        log.info("Received file upload request: {}", file.getOriginalFilename());
        
        try {
            // 从认证信息中获取用户ID
            Long userId = Long.parseLong(authentication.getName());
            log.info("User ID from authentication: {}", userId);
            
            // 上传头像并获取URL
            String avatarUrl = userService.uploadAvatar(userId, file);
            log.info("File uploaded successfully, URL: {}", avatarUrl);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "上传成功");
            response.put("data", Map.of("url", avatarUrl));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("File upload failed", e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "上传失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
} 