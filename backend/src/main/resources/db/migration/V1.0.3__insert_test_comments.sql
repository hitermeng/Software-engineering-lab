-- 为文章1（React Hooks 完全指南）创建评论
INSERT INTO comments (article_id, content, user_id, parent_id, reply_to_user_id, create_time) VALUES
(1, '这篇文章写得非常详细，对React Hooks的讲解很到位！', 2, NULL, NULL, '2025-06-14 20:00:00'),
(1, '感谢分享，学到了很多！', 3, NULL, NULL, '2025-06-14 21:00:00'),
(1, '请问useEffect的依赖数组应该怎么设置？', 4, NULL, NULL, '2025-06-14 22:00:00'),
(1, '建议在useEffect中返回清理函数，这样可以避免内存泄漏。', 2, 3, 4, '2025-06-14 22:30:00'),
(1, '是的，清理函数很重要，特别是在处理订阅和事件监听器时。', 1, 3, 4, '2025-06-14 23:00:00'),
(1, 'useCallback和useMemo的区别是什么？', 5, NULL, NULL, '2025-06-15 10:00:00'),
(1, 'useCallback用于缓存函数，useMemo用于缓存计算结果。', 2, 6, 5, '2025-06-15 10:30:00'),
(1, '这篇文章对初学者很友好，讲解得很清晰！', 6, NULL, NULL, '2025-06-15 11:00:00'),
(1, '请问如何处理异步操作？', 7, NULL, NULL, '2025-06-15 12:00:00'),
(1, '可以使用async/await或者Promise来处理异步操作。', 1, 9, 7, '2025-06-15 12:30:00');

-- 为文章2（Spring Boot 最佳实践）创建评论
INSERT INTO comments (article_id, content, user_id, parent_id, reply_to_user_id, create_time) VALUES
(2, '文章结构清晰，对Spring Boot的配置讲解很详细！', 3, NULL, NULL, '2025-06-15 14:00:00'),
(2, '请问如何处理跨域问题？', 4, NULL, NULL, '2025-06-15 15:00:00'),
(2, '可以使用@CrossOrigin注解或者配置CorsFilter。', 1, 12, 4, '2025-06-15 15:30:00'),
(2, '文章中的安全配置部分很有帮助！', 5, NULL, NULL, '2025-06-15 16:00:00'),
(2, '请问如何处理文件上传？', 6, NULL, NULL, '2025-06-15 17:00:00'),
(2, '可以使用MultipartFile来处理文件上传。', 2, 15, 6, '2025-06-15 17:30:00'),
(2, '文章中的数据库配置部分很实用！', 7, NULL, NULL, '2025-06-15 18:00:00'),
(2, '请问如何处理事务？', 8, NULL, NULL, '2025-06-15 19:00:00'),
(2, '可以使用@Transactional注解来管理事务。', 1, 18, 8, '2025-06-15 19:30:00'),
(2, '文章中的缓存配置部分很有启发性！', 9, NULL, NULL, '2025-06-15 20:00:00');

-- 为文章3（Docker 容器化部署）创建评论
INSERT INTO comments (article_id, content, user_id, parent_id, reply_to_user_id, create_time) VALUES
(3, '文章对Docker的基本概念讲解很清晰！', 4, NULL, NULL, '2025-06-16 10:00:00'),
(3, '请问如何处理容器间的网络通信？', 5, NULL, NULL, '2025-06-16 11:00:00'),
(3, '可以使用Docker网络或者Docker Compose来管理容器网络。', 2, 22, 5, '2025-06-16 11:30:00'),
(3, '文章中的镜像构建部分很实用！', 6, NULL, NULL, '2025-06-16 12:00:00'),
(3, '请问如何处理数据持久化？', 7, NULL, NULL, '2025-06-16 13:00:00'),
(3, '可以使用Docker卷或者绑定挂载来实现数据持久化。', 1, 25, 7, '2025-06-16 13:30:00'),
(3, '文章中的多阶段构建部分很有启发性！', 8, NULL, NULL, '2025-06-16 14:00:00'),
(3, '请问如何处理容器日志？', 9, NULL, NULL, '2025-06-16 15:00:00'),
(3, '可以使用Docker日志驱动或者ELK栈来处理容器日志。', 2, 28, 9, '2025-06-16 15:30:00'),
(3, '文章中的安全最佳实践部分很有帮助！', 10, NULL, NULL, '2025-06-16 16:00:00');

-- 为文章4（微服务架构设计）创建评论
INSERT INTO comments (article_id, content, user_id, parent_id, reply_to_user_id, create_time) VALUES
(4, '文章对微服务架构的讲解很全面！', 5, NULL, NULL, '2025-06-16 17:00:00'),
(4, '请问如何处理服务发现？', 6, NULL, NULL, '2025-06-16 18:00:00'),
(4, '可以使用Eureka、Consul或者Nacos来实现服务发现。', 1, 32, 6, '2025-06-16 18:30:00'),
(4, '文章中的服务治理部分很实用！', 7, NULL, NULL, '2025-06-16 19:00:00'),
(4, '请问如何处理分布式事务？', 8, NULL, NULL, '2025-06-16 20:00:00'),
(4, '可以使用Seata、TCC或者Saga模式来处理分布式事务。', 2, 35, 8, '2025-06-16 20:30:00'),
(4, '文章中的服务监控部分很有启发性！', 9, NULL, NULL, '2025-06-16 21:00:00'),
(4, '请问如何处理服务熔断？', 10, NULL, NULL, '2025-06-16 22:00:00'),
(4, '可以使用Hystrix、Sentinel或者Resilience4j来实现服务熔断。', 1, 38, 10, '2025-06-16 22:30:00'),
(4, '文章中的服务安全部分很有帮助！', 11, NULL, NULL, '2025-06-16 23:00:00');

-- 为文章5（人工智能入门）创建评论
INSERT INTO comments (article_id, content, user_id, parent_id, reply_to_user_id, create_time) VALUES
(5, '文章对AI的基本概念讲解很清晰！', 6, NULL, NULL, '2025-06-17 10:00:00'),
(5, '请问如何处理数据预处理？', 7, NULL, NULL, '2025-06-17 11:00:00'),
(5, '可以使用pandas、numpy等库来处理数据。', 2, 42, 7, '2025-06-17 11:30:00'),
(5, '文章中的机器学习部分很实用！', 8, NULL, NULL, '2025-06-17 12:00:00'),
(5, '请问如何选择模型？', 9, NULL, NULL, '2025-06-17 13:00:00'),
(5, '需要根据具体问题和数据特点来选择合适的模型。', 1, 45, 9, '2025-06-17 13:30:00'),
(5, '文章中的深度学习部分很有启发性！', 10, NULL, NULL, '2025-06-17 14:00:00'),
(5, '请问如何处理过拟合？', 11, NULL, NULL, '2025-06-17 15:00:00'),
(5, '可以使用正则化、早停、Dropout等方法来防止过拟合。', 2, 48, 11, '2025-06-17 15:30:00'),
(5, '文章中的实践案例部分很有帮助！', 12, NULL, NULL, '2025-06-17 16:00:00');

-- 更新文章的评论数
UPDATE article SET comment_count = 10 WHERE id = 1;
UPDATE article SET comment_count = 10 WHERE id = 2;
UPDATE article SET comment_count = 10 WHERE id = 3;
UPDATE article SET comment_count = 10 WHERE id = 4;
UPDATE article SET comment_count = 10 WHERE id = 5; 