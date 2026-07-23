import axios from 'axios'
import { getAuthConfig, requestWithOptionalAuth } from './authRequest'

const API_URL = '/api/categories'

export default {
  // Lấy danh sách tất cả danh mục
  async getAllCategories() {
    try {
      const response = await axios.get(API_URL)
      return response.data
    } catch (error) {
      console.error('Lỗi lấy danh sách danh mục:', error)
      throw error
    }
  },

  // Lấy chi tiết 1 danh mục theo ID
  async getCategoryById(id) {
    try {
      const response = await axios.get(`${API_URL}/${id}`)
      return response.data
    } catch (error) {
      console.error(`Lỗi lấy danh mục ID ${id}:`, error)
      throw error
    }
  },

  // Thêm mới danh mục
  async createCategory(category) {
    try {
      const response = await requestWithOptionalAuth(
        () => axios.post(API_URL, category),
        () => axios.post(API_URL, category, getAuthConfig())
      )
      return response.data
    } catch (error) {
      console.error('Lỗi thêm danh mục:', error)
      throw error
    }
  },

  // Cập nhật danh mục
  async updateCategory(id, category) {
    try {
      const response = await requestWithOptionalAuth(
        () => axios.put(`${API_URL}/${id}`, category),
        () => axios.put(`${API_URL}/${id}`, category, getAuthConfig())
      )
      return response.data
    } catch (error) {
      console.error(`Lỗi cập nhật danh mục ID ${id}:`, error)
      throw error
    }
  },

  // Xóa danh mục
  async deleteCategory(id) {
    try {
      const response = await requestWithOptionalAuth(
        () => axios.delete(`${API_URL}/${id}`),
        () => axios.delete(`${API_URL}/${id}`, getAuthConfig())
      )
      return response.data
    } catch (error) {
      console.error(`Lỗi xóa danh mục ID ${id}:`, error)
      throw error
    }
  }
}