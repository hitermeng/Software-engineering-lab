package com.hit.articlemgr.controller;

import com.hit.articlemgr.dto.UserUpdateDTO;
import com.hit.articlemgr.dto.UpdateUserRoleDTO;
import com.hit.articlemgr.entity.User;
import com.hit.articlemgr.service.UserService;
import com.hit.articlemgr.util.R;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

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

    /**
     * 更新用户角色
     */
    @PutMapping("/role")
    public R<Void> updateUserRole(@RequestBody UpdateUserRoleDTO updateUserRoleDTO) {
        try {
            // 验证管理员密码
            if (!"admin123456".equals(updateUserRoleDTO.getAdminPassword())) {
                return R.error("管理员密码错误");
            }

            // 获取目标用户
            User targetUser = userService.getUserByUsername(updateUserRoleDTO.getUsername());
            if (targetUser == null) {
                return R.error("目标用户不存在");
            }

            // 更新用户角色
            targetUser.setRole(updateUserRoleDTO.getRole());
            userService.updateUser(targetUser);

            return R.success("用户角色更新成功", null);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 禁用/启用用户
     */
    @PutMapping("/{username}/status")
    public R<Void> updateUserStatus(
            @PathVariable String username,
            @RequestParam Integer status,
            @RequestParam String adminPassword) {
        try {
            // 验证管理员密码
            if (!"admin123456".equals(adminPassword)) {
                return R.error("管理员密码错误");
            }

            // 获取目标用户
            User targetUser = userService.getUserByUsername(username);
            if (targetUser == null) {
                return R.error("目标用户不存在");
            }

            // 更新用户状态
            targetUser.setStatus(status);
            userService.updateUser(targetUser);

            return R.success(status == 1 ? "用户已启用" : "用户已禁用", null);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
} 