<template>
  <div v-if="showModal" class="modal fade show d-block" tabindex="-1" style="background: rgba(0,0,0,0.5)">
    <div class="modal-dialog modal-lg modal-dialog-centered">
      <div class="modal-content border-0 shadow-lg">
        <div class="modal-header bg-primary text-white">
          <h5 class="modal-title fw-bold">
            <i class="bi me-2" :class="isEdit ? 'bi-pencil-square' : 'bi-plus-circle-fill'"></i>
            {{ isEdit ? "Cập nhật sản phẩm" : "Thêm sản phẩm mới" }}
          </h5>
          <button type="button" class="btn-close btn-close-white" @click="close"></button>
        </div>

        <form @submit.prevent="saveProduct" novalidate>
          <div class="modal-body p-4" style="max-height: 80vh; overflow-y: auto;">
            <div class="row g-3">
              <!-- ID (Chỉ hiển thị khi cập nhật) -->
              <div v-if="isEdit" class="col-md-2">
                <label class="form-label fw-semibold">ID</label>
                <input class="form-control bg-light" v-model="form.id" readonly>
              </div>

              <!-- Tên sản phẩm -->
              <div :class="isEdit ? 'col-md-10' : 'col-md-12'">
                <label class="form-label fw-semibold">Tên sản phẩm <span class="text-danger">*</span></label>
                <!-- NEW: Thêm ref="inputName" -->
                <input ref="inputName" v-model="form.name" class="form-control" placeholder="Ví dụ: Laptop Dell XPS 15" required>
              </div>

              <!-- Danh mục -->
              <div class="col-md-6">
                <label class="form-label fw-semibold">Danh mục <span class="text-danger">*</span></label>
                <!-- NEW: Thêm ref="inputCategory" -->
                <select ref="inputCategory" class="form-select" v-model="form.category">
                  <option :value="null">-- Chọn danh mục --</option>
                  <option v-for="item in categories" :key="item.id" :value="item">
                    {{ item.name }}
                  </option>
                </select>
              </div>

              <!-- Thương hiệu -->
              <div class="col-md-6">
                <label class="form-label fw-semibold">Thương hiệu <span class="text-danger">*</span></label>
                <!-- NEW: Thêm ref="inputBrand" -->
                <select ref="inputBrand" class="form-select" v-model="form.brand">
                  <option disabled value="">-- Chọn thương hiệu --</option>
                  <option v-for="brand in brands" :key="brand" :value="brand">
                    {{ brand }}
                  </option>
                </select>
              </div>

              <!-- Giá bán -->
              <div class="col-md-6">
                <label class="form-label fw-semibold">Giá bán (VNĐ) <span class="text-danger">*</span></label>
                <input type="number" class="form-control" v-model="form.price" placeholder="0">
              </div>

              <!-- Số lượng -->
              <div class="col-md-6">
                <label class="form-label fw-semibold">Số lượng tồn <span class="text-danger">*</span></label>
                <input type="number" class="form-control" v-model="form.quantity" placeholder="0">
              </div>

              <!-- Chọn Ảnh -->
              <div class="col-12">
                <label class="form-label fw-semibold">Hình ảnh sản phẩm</label>
                <input type="file" class="form-control" accept="image/*" @change="chooseImage">
                <div class="form-text small" v-if="form.image">File đã chọn: <code>{{ form.image }}</code></div>
              </div>

              <!-- Xem trước ảnh -->
              <div class="col-12 text-center" v-if="previewImage">
                <div class="p-2 border rounded bg-light d-inline-block">
                  <div class="small text-muted mb-1">Xem trước hình ảnh:</div>
                  <img :src="previewImage" class="img-thumbnail" style="max-height: 120px; object-fit: contain;">
                </div>
              </div>

              <!-- Mô tả -->
              <div class="col-12">
                <label class="form-label fw-semibold">Mô tả sản phẩm</label>
                <textarea rows="3" class="form-control" v-model="form.description" placeholder="Nhập mô tả chi tiết sản phẩm..."></textarea>
              </div>

              <!-- Trạng thái Switch -->
              <div class="col-12">
                <div class="form-check form-switch mt-2">
                  <input class="form-check-input" type="checkbox" id="productStatusSwitch" v-model="form.status">
                  <label class="form-check-label fw-semibold" for="productStatusSwitch">Mở bán sản phẩm này</label>
                </div>
              </div>
            </div>
          </div>

          <div class="modal-footer bg-light">
            <button type="button" class="btn btn-secondary px-4" @click="close">Hủy</button>
            <button type="submit" class="btn btn-primary fw-bold px-4">
              <i class="bi bi-floppy me-1"></i> {{ isEdit ? "Cập nhật" : "Thêm mới" }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, watch, computed, ref, onMounted, nextTick } from "vue" // NEW: Thêm nextTick
import CategoryService from "../../services/CategoryService"
import { notify } from '../../utils/notify'

const props = defineProps({
  showModal: Boolean,
  product: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(["save", "close"])

const categories = ref([])
const previewImage = ref("")

// NEW: Khai báo Element Refs hỗ trợ tự cuộn & focus
const inputName = ref(null)
const inputCategory = ref(null)
const inputBrand = ref(null)

const brands = ["Apple", "Samsung", "Xiaomi", "Oppo", "Vivo", "Asus", "Acer", "Dell", "HP", "Lenovo", "MSI", "Logitech", "Razer", "Corsair", "SteelSeries", "HyperX", "Kingston", "Baseus", "Anker", "UGREEN"]

const form = reactive({
  id: null,
  name: "",
  brand: "",
  price: 0,
  quantity: 0,
  image: "",
  description: "",
  status: true,
  category: null,
  discountId: null
})

const isEdit = computed(() => form.id != null)

const loadCategories = async () => {
  try {
    categories.value = await CategoryService.getAllCategories()
  } catch (error) {
    console.error("Lỗi tải danh mục:", error)
  }
}

// NEW: Hàm cuộn mượt và focus vào element lỗi
const focusElement = async (el) => {
  await nextTick()
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'center' })
    if (typeof el.focus === 'function') el.focus()
  }
}

watch(() => props.product, (value) => {
  Object.assign(form, {
    id: value?.id ?? null,
    name: value?.name ?? "",
    brand: value?.brand ?? "",
    price: value?.price ?? 0,
    quantity: value?.quantity ?? 0,
    image: value?.image ?? "",
    description: value?.description ?? "",
    status: value?.status ?? true,
    category: value?.category ?? null,
    discountId: value?.discountId ?? null
  })

  if (value?.image) {
    previewImage.value = value.image.startsWith('data:') || value.image.startsWith('blob:') ? value.image : "/images/" + value.image
  } else {
    previewImage.value = ""
  }
}, { immediate: true })

const chooseImage = (event) => {
  const file = event.target.files[0]
  if (!file) return
  form.image = file.name
  previewImage.value = URL.createObjectURL(file)
}

const saveProduct = () => {
  if (!String(form.name || '').trim()) {
    notify('Vui lòng nhập tên sản phẩm.', 'warning')
    focusElement(inputName.value) // NEW: Cuộn và focus
    return
  }
  if (!form.category) {
    notify('Vui lòng chọn danh mục.', 'warning')
    focusElement(inputCategory.value) // NEW: Cuộn và focus
    return
  }
  if (!String(form.brand || '').trim()) {
    notify('Vui lòng chọn thương hiệu.', 'warning')
    focusElement(inputBrand.value) // NEW: Cuộn và focus
    return
  }
  emit("save", { ...form })
}

const close = () => {
  emit("close")
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.form-control, .form-select { border-radius: 8px; }
.form-check-input { width: 2.5em; height: 1.25em; cursor: pointer; }
</style>