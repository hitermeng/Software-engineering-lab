package com.hit.articlemgr.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 用户登录DTO
 *
 * @author HIT
 */
@Data
public class LoginDTO {

    /**
     * 用户名或邮箱
     */
    @NotBlank(message = "用户名或邮箱不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}