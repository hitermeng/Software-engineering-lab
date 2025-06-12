package com.hit.articlemgr.service;

import com.hit.articlemgr.dto.LoginDTO;
import com.hit.articlemgr.dto.RegisterDTO;
import com.hit.articlemgr.dto.TokenDTO;
import com.hit.articlemgr.dto.UserUpdateDTO;
import com.hit.articlemgr.entity.User;

/**
 * 用户服务接口
 *
 * @author HIT
 */
public interface UserService {

    /**
     * 用户注册
     */
    void register(RegisterDTO registerDTO);

    /**
     * 用户登录
     */
    TokenDTO login(LoginDTO loginDTO);

    /**
     * 根据用户名获取用户
     */
    User getUserByUsername(String username);

    /**
     * 根据ID获取用户
     */
    User getUserById(Long id);

    /**
     * 更新用户信息
     */
    void updateUser(User user);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param userUpdateDTO 更新的用户信息
     * @return 更新后的用户信息
     */
    User updateUserProfile(Long userId, UserUpdateDTO userUpdateDTO);
}