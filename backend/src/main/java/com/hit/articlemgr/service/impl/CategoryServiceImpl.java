package com.hit.articlemgr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hit.articlemgr.dto.CategoryDTO;
import com.hit.articlemgr.entity.Category;
import com.hit.articlemgr.mapper.CategoryMapper;
import com.hit.articlemgr.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类服务实现类
 *
 * @author HIT
 */
@Transactional
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<CategoryDTO> getCategoryTree() {
        List<Category> categories = list();
        return buildTree(categories);
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        Category category = getById(id);
        return convertToDTO(category);
    }

    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        save(category);
        return convertToDTO(category);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        updateById(category);
        return convertToDTO(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        removeById(id);
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        removeByIds(ids);
    }

    @Override
    @Transactional
    public void moveCategory(Long id, Long newParentId) {
        Category category = getById(id);
        if (category != null) {
            category.setParentId(newParentId);
            updateById(category);
        }
    }

    @Override
    @Transactional
    public void updateSort(Long id, Integer newSort) {
        Category category = getById(id);
        if (category != null) {
            category.setSort(newSort);
            updateById(category);
        }
    }

    @Override
    public List<CategoryDTO> getAvailableParents(Long excludeId) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        if (excludeId != null) {
            wrapper.ne(Category::getId, excludeId);
        }
        List<Category> categories = list(wrapper);
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private List<CategoryDTO> buildTree(List<Category> categories) {
        List<CategoryDTO> result = new ArrayList<>();
        Map<Long, List<Category>> parentMap = categories.stream()
                .collect(Collectors.groupingBy(Category::getParentId));

        // 获取顶级分类
        List<Category> rootCategories = parentMap.getOrDefault(null, new ArrayList<>());
        for (Category category : rootCategories) {
            CategoryDTO dto = convertToDTO(category);
            dto.setChildren(buildChildren(category.getId(), parentMap));
            result.add(dto);
        }

        return result;
    }

    private List<CategoryDTO> buildChildren(Long parentId, Map<Long, List<Category>> parentMap) {
        List<Category> children = parentMap.getOrDefault(parentId, new ArrayList<>());
        return children.stream()
                .map(category -> {
                    CategoryDTO dto = convertToDTO(category);
                    dto.setChildren(buildChildren(category.getId(), parentMap));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private CategoryDTO convertToDTO(Category category) {
        if (category == null) {
            return null;
        }
        CategoryDTO dto = new CategoryDTO();
        BeanUtils.copyProperties(category, dto);
        return dto;
    }
}