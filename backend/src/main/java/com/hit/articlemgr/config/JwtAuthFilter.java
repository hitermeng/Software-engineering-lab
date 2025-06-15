package com.hit.articlemgr.config;

import com.hit.articlemgr.util.JwtUtil;
import com.hit.articlemgr.service.UserService;
import com.hit.articlemgr.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * JWT认证过滤器
 *
 * @author HIT
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        log.debug("Processing authentication for requestURI: {}", requestURI);

        // 获取Authorization头
        String authHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            // 提取token
            String token = authHeader.substring(7);
            log.debug("Extracted token: {}", token);

            try {
                // 验证token
                if (jwtUtil.validateToken(token)) {
                    // 从token中获取用户信息
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    String username = jwtUtil.getUsernameFromToken(token);
                    log.debug("Token validated. User ID: {}, Username: {}", userId, username);

                    // 从数据库获取用户角色
                    User user = userService.getUserById(userId);
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    if (user != null && StringUtils.hasText(user.getRole())) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
                    }
                    log.debug("User roles: {}", authorities);

                    // 创建认证对象
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, authorities);

                    // 设置认证信息到SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Authentication set for user: {}", username);
                } else {
                    log.warn("Invalid JWT token for requestURI: {}", requestURI);
                    SecurityContextHolder.clearContext();
                }
            } catch (Exception e) {
                // token无效，清除认证信息
                log.error("JWT token processing error for requestURI: {}", requestURI, e);
                SecurityContextHolder.clearContext();
            }
        } else {
            log.debug("No JWT token found in Authorization header for requestURI: {}", requestURI);
        }

        filterChain.doFilter(request, response);
    }
}