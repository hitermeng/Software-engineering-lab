package com.hit.articlemgr.service.impl;

import com.hit.articlemgr.dto.LoginDTO;
import com.hit.articlemgr.dto.RegisterDTO;
import com.hit.articlemgr.dto.TokenDTO;
import com.hit.articlemgr.dto.UserUpdateDTO;
import com.hit.articlemgr.entity.User;
import com.hit.articlemgr.mapper.UserMapper;
import com.hit.articlemgr.service.UserService;
import com.hit.articlemgr.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


/**
 * 用户服务实现类
 *
 * @author HIT
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Override
    @Transactional
    public void register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        if (existsByUsername(registerDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (existsByEmail(registerDTO.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }

        // 检查两次密码是否一致
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setNickname(StringUtils.hasText(registerDTO.getNickname()) ?
                registerDTO.getNickname() : registerDTO.getUsername());
        user.setRole("USER");
        user.setStatus(1);

        userMapper.insert(user);
    }

    @Override
    public TokenDTO login(LoginDTO loginDTO) {
        // 根据用户名或邮箱查询用户
        User user = userMapper.findByUsernameOrEmail(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被禁用");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 构建返回结果
        TokenDTO.UserInfo userInfo = new TokenDTO.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setNickname(user.getNickname());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setRole(user.getRole());

        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setAccessToken(token);
        tokenDTO.setTokenType("Bearer");
        tokenDTO.setExpiresIn(jwtExpiration);
        tokenDTO.setUserInfo(userInfo);

        return tokenDTO;
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.findByUsernameOrEmail(username);
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        userMapper.updateById(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userMapper.existsByUsername(username) > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userMapper.existsByEmail(email) > 0;
    }

    @Override
    @Transactional
    public User updateUserProfile(Long userId, UserUpdateDTO userUpdateDTO) {
        // 获取当前用户
        User user = getUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户名是否已存在（如果修改了用户名）
        if (!user.getUsername().equals(userUpdateDTO.getUsername()) 
            && existsByUsername(userUpdateDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在（如果修改了邮箱）
        if (!user.getEmail().equals(userUpdateDTO.getEmail()) 
            && existsByEmail(userUpdateDTO.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }

        // 更新用户信息
        user.setUsername(userUpdateDTO.getUsername());
        user.setEmail(userUpdateDTO.getEmail());
        user.setNickname(userUpdateDTO.getNickname());
        if (StringUtils.hasText(userUpdateDTO.getAvatar())) {
            user.setAvatar(userUpdateDTO.getAvatar());
        }

        // 保存更新
        userMapper.updateById(user);
        return user;
    }
}