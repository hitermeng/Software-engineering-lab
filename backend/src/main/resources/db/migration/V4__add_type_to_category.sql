-- 添加type字段到category表
ALTER TABLE category
ADD COLUMN type VARCHAR(20) NOT NULL DEFAULT 'tech' COMMENT '分类类型' AFTER name;

-- 创建type字段索引
CREATE INDEX idx_category_type ON category(type); 