// --- Khởi tạo ---
const STORAGE_KEY = "beauty_admin_products";

// Load từ localStorage hoặc fallback về dữ liệu mẫu
let sampleProducts = JSON.parse(localStorage.getItem(STORAGE_KEY)) || [
  { productId: 1000, name: "Lipstick", category: "Makeup", price: 150000, image: "https://picsum.photos/seed/lipstick/60", description: "Matte lipstick" },
  { productId: 1001, name: "Facial Cleanser", category: "Skincare", price: 120000, image: "https://picsum.photos/seed/cleanser/60", description: "Gentle facial wash" },
  { productId: 1002, name: "Perfume", category: "Fragrance", price: 350000, image: "https://picsum.photos/seed/perfume/60", description: "Long-lasting fragrance" },
  { productId: 1003, name: "Shampoo", category: "Haircare", price: 100000, image: "https://picsum.photos/seed/shampoo/60", description: "Anti-dandruff shampoo" },
  { productId: 1004, name: "Eyeliner", category: "Makeup", price: 90000, image: "https://picsum.photos/seed/eyeliner/60", description: "Waterproof eyeliner" }
];

// Tìm ID cao nhất hiện tại để sinh ID mới
let nextProductId = Math.max(...sampleProducts.map(p => p.productId), 1000) + 1;

let currentPage = 0;
const rowsPerPage = 5;
let deleteId = null;

// --- Hàm tiện ích ---
function saveToStorage() {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(sampleProducts));
}

function renderProducts() {
  const start = currentPage * rowsPerPage;
  const body = document.getElementById("product-table-body");
  body.innerHTML = "";

  let productsToRender = [...sampleProducts];

  // Lọc theo tên
  const searchText = document.getElementById("searchProduct").value.toLowerCase();
  if (searchText) {
    productsToRender = productsToRender.filter(p => p.name.toLowerCase().includes(searchText));
  }

  // Lọc theo danh mục
  const filterCategory = document.getElementById("filterCategory").value.toLowerCase();
  if (filterCategory) {
    productsToRender = productsToRender.filter(p => p.category.toLowerCase() === filterCategory);
  }

  // Sắp xếp
  const sortValue = document.getElementById("sortSelect").value;
  switch (sortValue) {
    case "name-asc": productsToRender.sort((a, b) => a.name.localeCompare(b.name)); break;
    case "name-desc": productsToRender.sort((a, b) => b.name.localeCompare(a.name)); break;
    case "price-asc": productsToRender.sort((a, b) => a.price - b.price); break;
    case "price-desc": productsToRender.sort((a, b) => b.price - a.price); break;
  }

  // Phân trang
  productsToRender.slice(start, start + rowsPerPage).forEach((p, i) => {
    body.innerHTML += `
      <tr>
        <td>${start + i + 1}</td>
        <td>${p.productId}</td>
        <td><img src="${p.image}" alt="product" height="50"></td>
        <td>${p.name}</td>
        <td>${p.category}</td>
        <td>${p.price.toLocaleString()} đ</td>
        <td>
          <button class="btn btn-sm btn-outline-primary me-2" onclick="showEditModal(${p.productId})"><i class="bi bi-pencil"></i></button>
          <button class="btn btn-sm btn-danger" onclick="showDeleteModal(${p.productId})"><i class="bi bi-trash"></i></button>
        </td>
      </tr>`;
  });
}


document.getElementById("sortSelect").addEventListener("change", () => {
  currentPage = 0;
  renderProducts();
});

document.getElementById("prevPage").onclick = () => {
  if (currentPage > 0) currentPage--;
  renderProducts();
};

document.getElementById("nextPage").onclick = () => {
  if ((currentPage + 1) * rowsPerPage < sampleProducts.length) currentPage++;
  renderProducts();
};

function showToast(message, type = "success") {
  const toastElement = document.getElementById("toastAlert");
  const toastBody = document.getElementById("toastMessage");

  toastElement.className = `toast align-items-center text-bg-${type} border-0`;
  toastBody.textContent = message;

  const toast = new bootstrap.Toast(toastElement);
  toast.show();
}

function showDeleteModal(productId) {
  deleteId = productId;
  const modal = new bootstrap.Modal(document.getElementById("deleteConfirmModal"));
  modal.show();
}

document.getElementById("confirmDeleteBtn").onclick = () => {
  const index = sampleProducts.findIndex(p => p.productId === deleteId);
  if (index !== -1) {
    sampleProducts.splice(index, 1);
    saveToStorage();
    renderProducts();
    deleteId = null;
    bootstrap.Modal.getInstance(document.getElementById("deleteConfirmModal")).hide();
  }
};

function showEditModal(productId) {
  const index = sampleProducts.findIndex(p => p.productId === productId);
  const product = sampleProducts[index];
  if (!product) return;

  document.getElementById("editProductId").value = product.productId;
  document.getElementById("editProductName").value = product.name;
  document.getElementById("editProductCategory").value = product.category;
  document.getElementById("editProductPrice").value = product.price;
  document.getElementById("editProductDescription").value = product.description || "";

  const preview = document.getElementById("editPreviewImage");
  preview.src = product.image;
  preview.classList.remove("d-none");

  document.getElementById("updateProduct").dataset.index = index;

  const modal = new bootstrap.Modal(document.getElementById("editProductModal"));
  modal.show();
}

document.getElementById("updateProduct").addEventListener("click", function () {
  const index = this.dataset.index;
  const name = document.getElementById("editProductName").value.trim();
  const category = document.getElementById("editProductCategory").value.trim();
  const price = parseFloat(document.getElementById("editProductPrice").value);
  const description = document.getElementById("editProductDescription").value.trim();
  const imageFile = document.getElementById("editProductImageFile").files[0];

  if (!name || !category || isNaN(price)) return showToast("Please fill in all required fields!", "danger");
  if (price < 0) return showToast("Price must not be negative!", "danger");

  const update = (imageBase64) => {
    sampleProducts[index] = {
      ...sampleProducts[index],
      name,
      category,
      price,
      description,
      image: imageBase64 || sampleProducts[index].image
    };
    saveToStorage();
    renderProducts();
    bootstrap.Modal.getInstance(document.getElementById("editProductModal")).hide();
  };

  if (imageFile) {
    const reader = new FileReader();
    reader.onload = (e) => update(e.target.result);
    reader.readAsDataURL(imageFile);
  } else {
    update(null);
  }
});

document.getElementById("editProductImageFile").addEventListener("change", function () {
  const file = this.files[0];
  const preview = document.getElementById("editPreviewImage");
  if (file) {
    const reader = new FileReader();
    reader.onload = (e) => {
      preview.src = e.target.result;
      preview.classList.remove("d-none");
    };
    reader.readAsDataURL(file);
  } else {
    preview.classList.add("d-none");
    preview.src = "";
  }
});

document.getElementById("productImageFile").addEventListener("change", function () {
  const file = this.files[0];
  const preview = document.getElementById("previewImage");
  if (file) {
    const reader = new FileReader();
    reader.onload = (e) => {
      preview.src = e.target.result;
      preview.classList.remove("d-none");
    };
    reader.readAsDataURL(file);
  } else {
    preview.classList.add("d-none");
    preview.src = "";
  }
});

document.getElementById("saveProduct").addEventListener("click", () => {
  const name = document.getElementById("productName").value.trim();
  const category = document.getElementById("productCategory").value.trim();
  const price = parseFloat(document.getElementById("productPrice").value);
  const description = document.getElementById("productDescription").value.trim();
  const imageFile = document.getElementById("productImageFile").files[0];

  if (!name || !category || isNaN(price)) return showToast("Please fill in all required fields!", "danger");
  if (price < 0) return showToast("Price must not be negative!", "danger");

  const reader = new FileReader();
  reader.onload = (e) => {
    const newProduct = {
      productId: nextProductId++,
      name,
      category,
      price,
      description,
      image: e.target.result
    };
    sampleProducts.push(newProduct);
    saveToStorage();
    renderProducts();
    resetForm();
    bootstrap.Modal.getInstance(document.getElementById("addProductModal")).hide();
  };

  if (imageFile) {
    reader.readAsDataURL(imageFile);
  } else {
    showToast("Please choose an image!", "danger");
  }
});

function resetForm() {
  document.getElementById("productName").value = "";
  document.getElementById("productCategory").value = "";
  document.getElementById("productPrice").value = "";
  document.getElementById("productImageFile").value = "";
  document.getElementById("previewImage").src = "";
  document.getElementById("previewImage").classList.add("d-none");
  document.getElementById("productDescription").value = "";
}

window.onload = renderProducts;
document.getElementById("searchProduct").addEventListener("input", () => {
  currentPage = 0;
  renderProducts();
});

document.getElementById("filterCategory").addEventListener("change", () => {
  currentPage = 0;
  renderProducts();
});
