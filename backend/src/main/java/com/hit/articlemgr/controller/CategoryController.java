package com.hit.articlemgr.controller;

import com.hit.articlemgr.dto.CategoryDTO;
import com.hit.articlemgr.dto.CategoryVO;
import com.hit.articlemgr.entity.Category;
import com.hit.articlemgr.service.CategoryService;
import com.hit.articlemgr.util.R;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<CategoryDTO>> getCategoryTree() {
        return ResponseEntity.ok(categoryService.getCategoryTree());
    }

    /**
     * 获取分类详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    /**
     * 创建分类
     */
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.createCategory(categoryDTO));
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        categoryDTO.setId(id);
        return ResponseEntity.ok(categoryService.updateCategory(categoryDTO));
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量删除分类
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDelete(@RequestBody List<Long> ids) {
        categoryService.batchDelete(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * 移动分类
     */
    @PutMapping("/{id}/move")
    public ResponseEntity<Void> moveCategory(@PathVariable Long id, @RequestParam Long newParentId) {
        categoryService.moveCategory(id, newParentId);
        return ResponseEntity.ok().build();
    }

    /**
     * 更新分类排序
     */
    @PutMapping("/{id}/sort")
    public ResponseEntity<Void> updateSort(@PathVariable Long id, @RequestParam Integer newSort) {
        categoryService.updateSort(id, newSort);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取可选父分类
     */
    @GetMapping("/available-parents")
    public ResponseEntity<List<CategoryDTO>> getAvailableParents(@RequestParam(required = false) Long excludeId) {
        return ResponseEntity.ok(categoryService.getAvailableParents(excludeId));
    }
}