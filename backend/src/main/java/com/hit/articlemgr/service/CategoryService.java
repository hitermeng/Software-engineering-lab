package com.hit.articlemgr.service;

import com.hit.articlemgr.dto.CategoryDTO;
import java.util.List;
import org.springframework.security.core.Authentication;

/**
 * 分类服务接口
 *
 * @author HIT
 */
public interface CategoryService {
    List<CategoryDTO> getCategoryTree();
    
    CategoryDTO getCategoryById(Long id);
    
    CategoryDTO createCategory(CategoryDTO categoryDTO, Authentication authentication);
    
    CategoryDTO updateCategory(CategoryDTO categoryDTO);
    
    void deleteCategory(Long id);
    
    void batchDelete(List<Long> ids);
    
    void moveCategory(Long id, Long newParentId);
    
    void updateSort(Long id, Integer newSort);
    
    List<CategoryDTO> getAvailableParents(Long excludeId);
}