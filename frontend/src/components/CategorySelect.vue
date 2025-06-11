<template>
  <el-select
      :model-value="modelValue"
      @update:model-value="handleChange"
      placeholder="请选择分类"
      clearable
      filterable
      style="width: 100%"
  >
    <el-option
        v-for="category in categoryStore.flatCategories"
        :key="category.id"
        :label="getCategoryLabel(category)"
        :value="category.id"
    >
      <span :style="{ paddingLeft: category.level * 20 + 'px' }">
        <el-icon v-if="category.children && category.children.length > 0">
          <Folder />
        </el-icon>
        <el-icon v-else>
          <Document />
        </el-icon>
        {{ category.name }}
      </span>
    </el-option>
  </el-select>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useCategoryStore } from '@/store/category'
import { Folder, Document } from '@element-plus/icons-vue'
import type { Category } from '@/api'

// Props 和 Emits
interface Props {
  modelValue?: number
}

interface Emits {
  (e: 'update:modelValue', value: number | undefined): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const categoryStore = useCategoryStore()

// 处理值变化
const handleChange = (value: number | undefined) => {
  emit('update:modelValue', value)
}

// 获取分类标签（带层级缩进）
const getCategoryLabel = (category: Category) => {
  const indent = '　'.repeat(category.level) // 全角空格用于缩进
  return indent + category.name
}
</script>

<style scoped>
:deep(.el-select-dropdown__item) {
  padding: 8px 20px;
}

:deep(.el-select-dropdown__item .el-icon) {
  margin-right: 6px;
  color: #909399;
}
</style>