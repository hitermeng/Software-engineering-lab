package com.hit.articlemgr.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class UpdateUserRoleDTO {
    
    @NotBlank(message = "管理员密码不能为空")
    private String adminPassword;
    
    @NotBlank(message = "目标用户名不能为空")
    private String username;
    
    @NotBlank(message = "角色不能为空")
    private String role;
} 