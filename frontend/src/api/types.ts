export interface ArticleVO {
  id: number
  title: string
  content: string
  summary: string
  categoryId: number
  categoryName: string
  categoryType?: string
  userId: number
  authorName: string
  authorAvatar: string
  viewCount: number
  likeCount: number
  commentCount: number
  createTime: string
  updateTime: string
}

export interface CategoryVO {
  id: number
  name: string
  type?: string
  articleCount?: number
}

export interface FilterDTO {
  page: number
  size: number
  keyword?: string
  categoryId?: number
  sortBy?: string
  startDate?: string
  endDate?: string
} 