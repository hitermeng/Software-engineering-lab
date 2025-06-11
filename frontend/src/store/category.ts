import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { categoryAPI, type Category, type CategoryDTO } from '@/api'
import { ElMessage } from 'element-plus'

export const useCategoryStore = defineStore('category', () => {
  // 状态
  const categories = ref<Category[]>([])
  const loading = ref(false)
  const selectedCategory = ref<Category | null>(null)
  const flatCategories = ref<Category[]>([])

  // 计算属性
  // 分类树
  const categoryTree = computed(() => categories.value)

  // 分类Map（加速查找）
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

  // 平铺分类列表（带level）
  const flatCategoriesComputed = computed(() => {
    const flatten = (cats: Category[], level = 0): Category[] => {
      const result: Category[] = []
      cats.forEach(cat => {
        result.push({ ...cat, level })
        if (cat.children && cat.children.length > 0) {
          result.push(...flatten(cat.children, level + 1))
        }
      })
      return result
    }
    return flatten(categories.value)
  })

  // 获取分类树
  const fetchCategoryTree = async () => {
    loading.value = true
    try {
      const response = await categoryAPI.getTree()
      categories.value = response.data || []
      flatCategories.value = flatCategoriesComputed.value
      return categories.value
    } catch (error: any) {
      ElMessage.error(error.message || '获取分类失败')
      console.error('获取分类树失败:', error)
      return []
    } finally {
      loading.value = false
    }
  }

  // 创建分类
  const createCategory = async (categoryData: CategoryDTO) => {
    loading.value = true
    try {
      await categoryAPI.create(categoryData)
      ElMessage.success('创建分类成功')
      await fetchCategoryTree()
      return true
    } catch (error: any) {
      ElMessage.error(error.message || '创建分类失败')
      console.error('创建分类失败:', error)
      return false
    } finally {
      loading.value = false
    }
  }

  // 更新分类
  const updateCategory = async (categoryData: Partial<CategoryDTO> & { id: number }) => {
    loading.value = true
    try {
      await categoryAPI.update(categoryData.id, categoryData as CategoryDTO)
      ElMessage.success('更新分类成功')
      await fetchCategoryTree()
      return true
    } catch (error: any) {
      ElMessage.error(error.message || '更新分类失败')
      console.error('更新分类失败:', error)
      return false
    } finally {
      loading.value = false
    }
  }

  // 删除分类
  const deleteCategory = async (id: number) => {
    loading.value = true
    try {
      await categoryAPI.delete(id)
      ElMessage.success('删除分类成功')
      await fetchCategoryTree()
      return true
    } catch (error: any) {
      ElMessage.error(error.message || '删除分类失败')
      console.error('删除分类失败:', error)
      return false
    } finally {
      loading.value = false
    }
  }

  // 批量删除分类
  const batchDeleteCategories = async (ids: number[]) => {
    loading.value = true
    try {
      await categoryAPI.batchDelete(ids)
      ElMessage.success(`成功删除 ${ids.length} 个分类`)
      await fetchCategoryTree()
      return true
    } catch (error: any) {
      ElMessage.error(error.message || '批量删除失败')
      console.error('批量删除失败:', error)
      return false
    } finally {
      loading.value = false
    }
  }

  // 移动分类
  const moveCategory = async (id: number, parentId?: number, sort?: number) => {
    loading.value = true
    try {
      await categoryAPI.move(id, parentId, sort)
      ElMessage.success('分类移动成功')
      await fetchCategoryTree()
      return true
    } catch (error: any) {
      ElMessage.error(error.message || '移动分类失败')
      console.error('移动分类失败:', error)
      return false
    } finally {
      loading.value = false
    }
  }

  // 更新排序
  const updateCategorySort = async (sortData: { id: number; sort: number; parentId?: number }[]) => {
    loading.value = true
    try {
      await categoryAPI.updateSort(sortData)
      ElMessage.success('排序更新成功')
      await fetchCategoryTree()
      return true
    } catch (error: any) {
      ElMessage.error(error.message || '更新排序失败')
      console.error('更新排序失败:', error)
      return false
    } finally {
      loading.value = false
    }
  }

  // 根据ID查找分类（用Map优化）
  const findCategoryById = (id: number): Category | undefined => {
    return categoryMap.value.get(id)
  }

  // 获取分类路径（字符串）
  const getCategoryPath = (id: number): string => {
    const path: string[] = []
    let current = findCategoryById(id)
    while (current) {
      path.unshift(current.name)
      current = current.parentId ? findCategoryById(current.parentId) : undefined
    }
    return path.join(' > ')
  }

  // 获取分类路径（对象数组）
  const getCategoryPathList = (id: number): Category[] => {
    const path: Category[] = []
    let current = findCategoryById(id)
    while (current) {
      path.unshift(current)
      current = current.parentId ? findCategoryById(current.parentId) : undefined
    }
    return path
  }

  // 判断是否为子分类
  const isChildCategory = (childId: number, parentId: number): boolean => {
    const child = findCategoryById(childId)
    if (!child || child.id === parentId) return false
    let current = child.parentId ? findCategoryById(child.parentId) : undefined
    while (current) {
      if (current.id === parentId) return true
      current = current.parentId ? findCategoryById(current.parentId) : undefined
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
    loading,
    selectedCategory,
    flatCategories,
    // 计算属性
    categoryTree,
    flatCategoriesComputed,
    // 方法
    fetchCategoryTree,
    createCategory,
    updateCategory,
    deleteCategory,
    batchDeleteCategories,
    moveCategory,
    updateCategorySort,
    findCategoryById,
    getCategoryPath,
    getCategoryPathList,
    isChildCategory,
    getAvailableParentCategories
  }
})