package com.hit.articlemgr.entity;

import com.baomidou.mybatisplus.annotation.*;
        import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 * @author HIT
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
public class User {

    /**
     * 用户ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 头像URL
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 角色
     */
    @TableField("role")
    private String role;

    /**
     * 状态：1-正常，0-禁用
     */
    @TableField("status")
    private Integer status;

    /**
     * 删除标记：0-未删除，1-已删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}