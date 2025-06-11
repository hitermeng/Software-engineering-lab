package com.hit.articlemgr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hit.articlemgr.dto.CategoryVO;
import com.hit.articlemgr.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 分类数据访问接口
 *
 * @author HIT
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 查询用户的分类树
     */
    @Select("SELECT c.*, " +
            "(SELECT COUNT(*) FROM article a WHERE a.category_id = c.id AND a.deleted = 0) as article_count " +
            "FROM category c " +
            "WHERE c.user_id = #{userId} AND c.deleted = 0 " +
            "ORDER BY c.sort_order ASC, c.create_time ASC")
    List<CategoryVO> findCategoryTreeByUserId(@Param("userId") Long userId);

    /**
     * 查询子分类
     */
    @Select("SELECT * FROM category WHERE parent_id = #{parentId} AND user_id = #{userId} AND deleted = 0 ORDER BY sort_order ASC")
    List<Category> findChildrenByParentId(@Param("parentId") Long parentId, @Param("userId") Long userId);

    /**
     * 检查分类名称是否存在
     */
    @Select("SELECT COUNT(*) FROM category WHERE name = #{name} AND parent_id = #{parentId} AND user_id = #{userId} AND deleted = 0")
    int existsByNameAndParentId(@Param("name") String name, @Param("parentId") Long parentId, @Param("userId") Long userId);

    /**
     * 检查分类名称是否存在（排除指定ID）
     */
    @Select("SELECT COUNT(*) FROM category WHERE name = #{name} AND parent_id = #{parentId} AND user_id = #{userId} AND id != #{excludeId} AND deleted = 0")
    int existsByNameAndParentIdExcludeId(@Param("name") String name, @Param("parentId") Long parentId, @Param("userId") Long userId, @Param("excludeId") Long excludeId);
}