-- 为 article 表添加全文索引
ALTER TABLE article ADD FULLTEXT INDEX idx_article_search (title, content, summary); 