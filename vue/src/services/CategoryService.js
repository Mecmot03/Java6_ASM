import axios from 'axios'

const API_URL = 'http://localhost:8080/api/categories'

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
      const response = await axios.post(API_URL, category)
      return response.data
    } catch (error) {
      console.error('Lỗi thêm danh mục:', error)
      throw error
    }
  },

  // Cập nhật danh mục
  async updateCategory(id, category) {
    try {
      const response = await axios.put(`${API_URL}/${id}`, category)
      return response.data
    } catch (error) {
      console.error(`Lỗi cập nhật danh mục ID ${id}:`, error)
      throw error
    }
  },

  // Xóa danh mục
  async deleteCategory(id) {
    try {
      const response = await axios.delete(`${API_URL}/${id}`)
      return response.data
    } catch (error) {
      console.error(`Lỗi xóa danh mục ID ${id}:`, error)
      throw error
    }
  }
}