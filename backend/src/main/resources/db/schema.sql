-- 创建数据库
CREATE DATABASE IF NOT EXISTS article_manager DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE article_manager;

-- 用户表
CREATE TABLE `user` (
                        `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
                        `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
                        `email` VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
                        `password` VARCHAR(255) NOT NULL COMMENT '密码',
                        `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
                        `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
                        `role` VARCHAR(20) DEFAULT 'USER' COMMENT '角色',
                        `status` TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
                        `deleted` TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
                        `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        INDEX `idx_username` (`username`),
                        INDEX `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 分类表
CREATE TABLE `category` (
                            `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
                            `user_id` BIGINT NOT NULL COMMENT '用户ID',
                            `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID，0表示根分类',
                            `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
                            `description` TEXT COMMENT '分类描述',
                            `sort_order` INT DEFAULT 0 COMMENT '排序',
                            `level` INT DEFAULT 1 COMMENT '层级，从1开始',
                            `path` VARCHAR(500) DEFAULT NULL COMMENT '路径，用逗号分隔的ID序列',
                            `deleted` TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
                            `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            INDEX `idx_user_id` (`user_id`),
                            INDEX `idx_parent_id` (`parent_id`),
                            INDEX `idx_path` (`path`),
                            FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

ALTER TABLE category
    ADD COLUMN color VARCHAR(20) DEFAULT NULL,
    ADD COLUMN icon VARCHAR(50) DEFAULT NULL,
    ADD COLUMN is_active TINYINT(1) DEFAULT 1,
    ADD COLUMN sort INT DEFAULT 0;

ALTER TABLE category MODIFY user_id BIGINT DEFAULT NULL;
-- 文章表
CREATE TABLE `article` (
                           `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '文章ID',
                           `user_id` BIGINT NOT NULL COMMENT '用户ID',
                           `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
                           `title` VARCHAR(200) NOT NULL COMMENT '文章标题',
                           `content` LONGTEXT NOT NULL COMMENT '文章内容',
                           `summary` TEXT COMMENT '文章摘要',
                           `tags` VARCHAR(500) COMMENT '标签，逗号分隔',
                           `is_shared` TINYINT DEFAULT 0 COMMENT '是否共享：0-私有，1-共享',
                           `view_count` INT DEFAULT 0 COMMENT '阅读次数',
                           `like_count` INT DEFAULT 0 COMMENT '点赞次数',
                           `comment_count` INT DEFAULT 0 COMMENT '评论次数',
                           `status` TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-草稿',
                           `deleted` TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
                           `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           INDEX `idx_user_id` (`user_id`),
                           INDEX `idx_category_id` (`category_id`),
                           INDEX `idx_title` (`title`),
                           INDEX `idx_is_shared` (`is_shared`),
                           INDEX `idx_create_time` (`create_time`),
                           FULLTEXT INDEX `idx_fulltext` (`title`, `content`, `summary`) WITH PARSER ngram,
                           FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                           FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- 评论表
CREATE TABLE `comment` (
                           `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
                           `article_id` BIGINT NOT NULL COMMENT '文章ID',
                           `user_id` BIGINT NOT NULL COMMENT '用户ID',
                           `parent_id` BIGINT DEFAULT 0 COMMENT '父评论ID，0表示顶级评论',
                           `content` TEXT NOT NULL COMMENT '评论内容',
                           `deleted` TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
                           `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           INDEX `idx_article_id` (`article_id`),
                           INDEX `idx_user_id` (`user_id`),
                           INDEX `idx_parent_id` (`parent_id`),
                           FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE,
                           FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 插入默认管理员用户（密码：admin123456）
INSERT INTO `user` (`username`, `email`, `password`, `nickname`, `role`, `status`) VALUES
    ('admin', 'admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKVecnC.rT5hg7NJ7HxJXZQGKPwu', '管理员', 'ADMIN', 1);