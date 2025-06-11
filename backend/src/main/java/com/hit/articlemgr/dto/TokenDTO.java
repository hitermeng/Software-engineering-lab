package com.hit.articlemgr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token响应DTO
 *
 * @author HIT
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenDTO {

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 令牌类型
     */
    private String tokenType = "Bearer";

    /**
     * 过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 用户信息
     */
    private UserInfo userInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String nickname;
        private String avatar;
        private String role;
    }
}