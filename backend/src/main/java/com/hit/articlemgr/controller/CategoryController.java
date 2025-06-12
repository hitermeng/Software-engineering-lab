package com.hit.articlemgr.controller;

import com.hit.articlemgr.dto.CategoryDTO;
//import com.hit.articlemgr.dto.CategoryVO;
//import com.hit.articlemgr.entity.Category;
import com.hit.articlemgr.service.CategoryService;
import com.hit.articlemgr.util.R;
//import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 *
 * @author HIT
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取分类树
     */
    @GetMapping
    public R<List<CategoryDTO>> getCategoryTree() {
        try {
            List<CategoryDTO> list = categoryService.getCategoryTree();
            return R.success(list);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取分类详情
     */
    @GetMapping("/{id}")
    public R<CategoryDTO> getCategoryById(@PathVariable Long id) {
        try {
            CategoryDTO dto = categoryService.getCategoryById(id);
            if (dto == null) {
                return R.notFound("分类不存在");
            }
            return R.success(dto);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 创建分类
     */
    @PostMapping
    public R<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        try {
            CategoryDTO dto = categoryService.createCategory(categoryDTO);
            return R.success("创建成功", dto);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public R<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        try {
            categoryDTO.setId(id);
            CategoryDTO dto = categoryService.updateCategory(categoryDTO);
            return R.success("更新成功", dto);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return R.success("删除成功", null);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 批量删除分类
     */
    @DeleteMapping("/batch")
    public R<Void> batchDelete(@RequestBody List<Long> ids) {
        try {
            categoryService.batchDelete(ids);
            return R.success("批量删除成功", null);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 移动分类
     */
    @PutMapping("/{id}/move")
    public R<Void> moveCategory(@PathVariable Long id, @RequestParam Long newParentId) {
        try {
            categoryService.moveCategory(id, newParentId);
            return R.success("移动成功", null);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 更新分类排序
     */
    @PutMapping("/{id}/sort")
    public R<Void> updateSort(@PathVariable Long id, @RequestParam Integer newSort) {
        try {
            categoryService.updateSort(id, newSort);
            return R.success("排序更新成功", null);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取可选父分类
     */
    @GetMapping("/available-parents")
    public R<List<CategoryDTO>> getAvailableParents(@RequestParam(required = false) Long excludeId) {
        try {
            List<CategoryDTO> list = categoryService.getAvailableParents(excludeId);
            return R.success(list);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
}