package com.hit.articlemgr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hit.articlemgr.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户数据访问接口
 *
 * @author HIT
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名或邮箱查询用户
     */
    @Select("SELECT * FROM user WHERE (username = #{usernameOrEmail} OR email = #{usernameOrEmail}) AND deleted = 0")
    User findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    /**
     * 检查用户名是否存在
     */
    @Select("SELECT COUNT(*) FROM user WHERE username = #{username} AND deleted = 0")
    int existsByUsername(@Param("username") String username);

    /**
     * 检查邮箱是否存在
     */
    @Select("SELECT COUNT(*) FROM user WHERE email = #{email} AND deleted = 0")
    int existsByEmail(@Param("email") String email);
}