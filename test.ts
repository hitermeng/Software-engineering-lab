import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { categoryApi, type Category, type CreateCategoryDTO, type UpdateCategoryDTO } from '@/api'

export const useCategoryStore = defineStore('category', () => {
  // 状态
  const categories = ref<Category[]>([])
  const flatCategories = ref<Category[]>([])
  const loading = ref(false)
  const selectedCategory = ref<Category | null>(null)

  // 计算属性
  const categoryTree = computed(() => categories.value)
  const categoryMap = computed(() => {
    const map = new Map<number, Category>()
    const addToMap = (cats: Category[]) => {
      cats.forEach(cat => {
        map.set(cat.id, cat)
        if (cat.children?.length) {
          addToMap(cat.children)
        }
      })
    }
    addToMap(categories.value)
    return map
  })

  // 获取分类树
  const fetchCategories = async () => {
    loading.value = true
    try {
      const data = await categoryApi.getCategories()
      categories.value = data
      return data
    } catch (error: any) {
      ElMessage.error(error.message || '获取分类失败')
      return []
    } finally {
      loading.value = false
    }
  }

  // 获取扁平分类列表
  const fetchFlatCategories = async () => {
    try {
      const data = await categoryApi.getFlatCategories()
      flatCategories.value = data
      return data
    } catch (error: any) {
      ElMessage.error(error.message || '获取分类列表失败')
      return []
    }
  }

  // 创建分类
  const createCategory = async (data: CreateCategoryDTO) => {
    loading.value = true
    try {
      const newCategory = await categoryApi.createCategory(data)
      ElMessage.success('分类创建成功')
      await fetchCategories() // 重新获取分类树
      return newCategory
    } catch (error: any) {
      ElMessage.error(error.message || '创建分类失败')
      throw error
    } finally {
      loading.value = false
    }
  }

  // 更新分类
  const updateCategory = async (data: UpdateCategoryDTO) => {
    loading.value = true
    try {
      const updatedCategory = await categoryApi.updateCategory(data)
      ElMessage.success('分类更新成功')
      await fetchCategories() // 重新获取分类树
      return updatedCategory
    } catch (error: any) {
      ElMessage.error(error.message || '更新分类失败')
      throw error
    } finally {
      loading.value = false
    }
  }

  // 删除分类
  const deleteCategory = async (id: number) => {
    loading.value = true
    try {
      await categoryApi.deleteCategory(id)
      ElMessage.success('分类删除成功')
      await fetchCategories() // 重新获取分类树
    } catch (error: any) {
      ElMessage.error(error.message || '删除分类失败')
      throw error
    } finally {
      loading.value = false
    }
  }

  // 批量删除分类
  const batchDeleteCategories = async (ids: number[]) => {
    loading.value = true
    try {
      await categoryApi.batchDeleteCategories(ids)
      ElMessage.success(`成功删除 ${ids.length} 个分类`)
      await fetchCategories() // 重新获取分类树
    } catch (error: any) {
      ElMessage.error(error.message || '批量删除失败')
      throw error
    } finally {
      loading.value = false
    }
  }

  // 移动分类
  const moveCategory = async (id: number, parentId?: number, sort?: number) => {
    loading.value = true
    try {
      const movedCategory = await categoryApi.moveCategory(id, parentId, sort)
      ElMessage.success('分类移动成功')
      await fetchCategories() // 重新获取分类树
      return movedCategory
    } catch (error: any) {
      ElMessage.error(error.message || '移动分类失败')
      throw error
    } finally {
      loading.value = false
    }
  }

  // 更新排序
  const updateCategorySort = async (sortData: { id: number; sort: number; parentId?: number }[]) => {
    loading.value = true
    try {
      await categoryApi.updateCategorySort(sortData)
      ElMessage.success('排序更新成功')
      await fetchCategories() // 重新获取分类树
    } catch (error: any) {
      ElMessage.error(error.message || '更新排序失败')
      throw error
    } finally {
      loading.value = false
    }
  }

  // 根据ID获取分类
  const getCategoryById = (id: number): Category | undefined => {
    return categoryMap.value.get(id)
  }

  // 获取分类路径
  const getCategoryPath = (categoryId: number): Category[] => {
    const path: Category[] = []
    let current = getCategoryById(categoryId)
    
    while (current) {
      path.unshift(current)
      current = current.parentId ? getCategoryById(current.parentId) : undefined
    }
    
    return path
  }

  // 检查是否为子分类
  const isChildCategory = (childId: number, parentId: number): boolean => {
    const child = getCategoryById(childId)
    if (!child || child.id === parentId) return false
    
    let current = child.parentId ? getCategoryById(child.parentId) : undefined
    while (current) {
      if (current.id === parentId) return true
      current = current.parentId ? getCategoryById(current.parentId) : undefined
    }
    
    return false
  }

  // 获取可选父分类（排除自己和子分类）
  const getAvailableParentCategories = (excludeId?: number): Category[] => {
    const result: Category[] = []
    
    const addCategories = (cats: Category[], level = 0) => {
      cats.forEach(cat => {
        if (excludeId && (cat.id === excludeId || isChildCategory(excludeId, cat.id))) {
          return
        }
        
        result.push({
          ...cat,
          level,
          name: '　'.repeat(level) + cat.name
        })
        
        if (cat.children?.length) {
          addCategories(cat.children, level + 1)
        }
      })
    }
    
    addCategories(categories.value)
    return result
  }

  return {
    // 状态
    categories,
    flatCategories,
    loading,
    selectedCategory,
    
    // 计算属性
    categoryTree,
    categoryMap,
    
    // 方法
    fetchCategories,
    fetchFlatCategories,
    createCategory,
    updateCategory,
    deleteCategory,
    batchDeleteCategories,
    moveCategory,
    updateCategorySort,
    getCategoryById,
    getCategoryPath,
    isChildCategory,
    getAvailableParentCategories
  }
})