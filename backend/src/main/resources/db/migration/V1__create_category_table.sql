CREATE TABLE IF NOT EXISTS category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    description VARCHAR(200) COMMENT '分类描述',
    parent_id BIGINT COMMENT '父分类ID',
    color VARCHAR(20) COMMENT '分类颜色',
    icon VARCHAR(50) COMMENT '分类图标',
    sort INT DEFAULT 0 COMMENT '排序',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    sort_order INT DEFAULT 0 COMMENT '排序值',
    level INT DEFAULT 1 COMMENT '层级，从1开始',
    path VARCHAR(500) DEFAULT NULL COMMENT '路径，用逗号分隔的ID序列',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    type VARCHAR(20) NOT NULL COMMENT '分类类型',
    FOREIGN KEY (parent_id) REFERENCES category(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

CREATE INDEX idx_category_name ON category(name);
CREATE INDEX idx_category_type ON category(type); 