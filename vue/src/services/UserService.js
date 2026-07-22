import axios from "axios"

const API_URL = "http://localhost:8080/api/users"

export default {

    // =========================
    // Lấy danh sách User
    // =========================

    async getAllUsers() {

        try {

            const response = await axios.get(API_URL)

            return response.data

        }

        catch (error) {

            console.error("Lỗi lấy danh sách User:", error)

            return []

        }

    },

    // =========================
    // Lấy User theo ID
    // =========================

    async getUserById(id) {

        try {

            const response = await axios.get(`${API_URL}/${id}`)

            return response.data

        }

        catch (error) {

            console.error("Lỗi lấy User:", error)

            return null

        }

    },

    // =========================
    // Thêm User
    // =========================

    async createUser(user) {

        try {

            const response = await axios.post(API_URL, user)

            return response.data

        }

        catch (error) {

            console.error("Lỗi thêm User:", error)

            throw error

        }

    },

    // =========================
    // Cập nhật User
    // =========================

    async updateUser(id, user) {

        try {

            const response = await axios.put(`${API_URL}/${id}`, user)

            return response.data

        }

        catch (error) {

            console.error("Lỗi cập nhật User:", error)

            throw error

        }

    },

    // =========================
    // Xóa User
    // =========================

    async deleteUser(id) {

        try {

            const response = await axios.delete(`${API_URL}/${id}`)

            return response.data

        }

        catch (error) {

            console.error("Lỗi xóa User:", error)

            throw error

        }

    },

    // =========================
    // Khóa / Mở khóa User
    // =========================

    async changeStatus(id) {

        try {

            const response = await axios.patch(`${API_URL}/${id}/status`)

            return response.data

        }

        catch (error) {

            console.error("Lỗi đổi trạng thái:", error)

            throw error

        }

    }

}