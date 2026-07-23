import axios from "axios"
import { getAuthConfig, requestWithOptionalAuth } from "./authRequest"

const API_URL = "/api/products"

export default {

    // =========================
    // Lấy tất cả sản phẩm
    // =========================
    async getAllProducts() {

        try {

            const response = await axios.get(API_URL)

            return response.data

        }

        catch (error) {

            console.error("Lỗi lấy danh sách sản phẩm:", error)

            return []

        }

    },

    // =========================
    // Lấy chi tiết sản phẩm
    // =========================
    async getProductById(id) {

        try {

            const response = await axios.get(`${API_URL}/${id}`)

            return response.data

        }

        catch (error) {

            console.error("Lỗi lấy chi tiết sản phẩm:", error)

            return null

        }

    },

    // =========================
    // Thêm sản phẩm
    // =========================
    async createProduct(product) {

        try {

            const response = await requestWithOptionalAuth(
                () => axios.post(API_URL, product),
                () => axios.post(API_URL, product, getAuthConfig())
            )

            return response.data

        }

        catch (error) {

            console.error("Lỗi thêm sản phẩm:", error)

            throw error

        }

    },

    // =========================
    // Cập nhật sản phẩm
    // =========================
    async updateProduct(id, product) {

        try {

            const response = await requestWithOptionalAuth(
                () => axios.put(`${API_URL}/${id}`, product),
                () => axios.put(`${API_URL}/${id}`, product, getAuthConfig())
            )

            return response.data

        }

        catch (error) {

            console.error("Lỗi cập nhật sản phẩm:", error)

            throw error

        }

    },

    // =========================
    // Xóa sản phẩm
    // =========================
    async deleteProduct(id) {

        try {

            const response = await requestWithOptionalAuth(
                () => axios.delete(`${API_URL}/${id}`),
                () => axios.delete(`${API_URL}/${id}`, getAuthConfig())
            )

            return response.data

        }

        catch (error) {

            console.error("Lỗi xóa sản phẩm:", error)

            throw error

        }

    }

}