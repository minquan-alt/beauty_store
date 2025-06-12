let currentPage = 1;
const pageSize = 10;
let pages = 0;
let deleteId = null;
let categories = [];
let suppliers = [];
let inventories = [];

/***************************************************************************************************************************/
/**
 * Khi refresh trang
 */
window.addEventListener("load", function () {
  loadProducts();
  loadCategories("filterCategory");
  loadInventories();
  loadSuppliers();
});

/***************************************************************************************************************************/
/**
 * Load các category, supplier, inventory để cho thẻ select
 */
async function loadCategories(selectId = "filterCategory") {
  const res = await fetch("http://localhost:8080/categories");
  const categories = (await res.json()).result;
  const select = document.getElementById(selectId);
  if (!select) return;

  if (selectId === "filterCategory") {
    select.innerHTML = '<option value="">All Categories</option>';
  } else {
    select.innerHTML = '<option value="">Select Category</option>';
  }

  categories.forEach((c) => {
    const opt = document.createElement("option");
    opt.value = c.id;
    opt.textContent = c.name;
    select.appendChild(opt);
  });
}

async function loadSuppliers() {
  const res = await fetch("http://localhost:8080/suppliers");
  const allSuppliers = (await res.json()).result;
  const select = document.getElementById("productSupplier");
  select.innerHTML = '<option value="">Select Supplier</option>';
  allSuppliers.forEach((c) => {
    const opt = document.createElement("option");
    opt.value = c.id;
    opt.textContent = c.name;
    select.appendChild(opt);
  });
}

async function loadInventories() {
  const res = await fetch("http://localhost:8080/inventories");
  let allCategories = (await res.json()).result;
  const select = document.getElementById("productInventory");
  select.innerHTML = '<option value="">Select Inventory</option>';
  allCategories.forEach((c) => {
    const opt = document.createElement("option");
    opt.value = c.id;
    opt.textContent = c.name + " - " + c.address;
    select.appendChild(opt);
  });
}

async function loadMasterData() {
  try {
    const [catRes, supRes, invRes] = await Promise.all([
      fetch("http://localhost:8080/categories").then((r) => r.json()),
      fetch("http://localhost:8080/suppliers").then((r) => r.json()),
      fetch("http://localhost:8080/inventories").then((r) => r.json()),
    ]);

    categories = catRes.result || [];
    suppliers = supRes.result || [];
    inventories = invRes.result || [];
  } catch (err) {
    console.error("Lỗi khi nạp dữ liệu danh mục:", err);
    alert("Không thể tải dữ liệu danh mục / kho / nhà cung cấp.");
  }
}

function renderSelect(selector, list, selectedName) {
  const sel = document.getElementById(selector);
  if (!sel) return;

  if (!selectedName) {
    sel.innerHTML = '<option value="">-- Select --</option>';
  } else sel.innerHTML = "";

  list.forEach((item) => {
    const opt = document.createElement("option");
    opt.value = item.name;
    opt.textContent = item.name;
    if (selector == "editProductInventory")
      opt.textContent = item.name + " - " + item.address;
    if (item.name === selectedName) opt.selected = true;
    sel.appendChild(opt);
  });
}

/***************************************************************************************************************************/
/**
 * Tạo product
 */
let selectedImageUrls = [];
let uploadInProgress = false;

//Khi mở modal lên
document
  .getElementById("addProductModal")
  .addEventListener("shown.bs.modal", () => {
    loadCategories("productCategory");
  });

//Nhấn nút save
document.getElementById("saveProduct").addEventListener("click", async () => {
  if (uploadInProgress) {
    Swal.fire({
      title: 'Processing!',
      text: 'Please wait, loading...',
      icon: 'warning',
      confirmButtonText: 'OK'
    });
    return;
  }

  const name = document.getElementById("productName").value.trim();
  const category_id = document.getElementById("productCategory").value;
  const supplier_id = document.getElementById("productSupplier").value;
  const inventory_id = document.getElementById("productInventory").value;
  const price = parseFloat(document.getElementById("productPrice").value);
  const description = document
    .getElementById("productDescription")
    .value.trim();

  if (
    !name ||
    !category_id ||
    !supplier_id ||
    !inventory_id ||
    isNaN(price) ||
    !description
  ) {
    Swal.fire({
      title: 'Missing form!',
      text: 'Please fill in form!',
      icon: 'warning',
      confirmButtonText: 'OK'
    });
    return;
  }

  if (price < 0) {
    Swal.fire({
      title: 'Unsuitable price!',
      text: 'Price must be more than 0!',
      icon: 'error',
      confirmButtonText: 'OK'
    });
    return;
  }

  if (!selectedImageUrls.length) {
    Swal.fire({
      title: 'Missing images!',
      text: 'No image uploaded!',
      icon: 'warning',
      confirmButtonText: 'OK'
    });
    return;
  }

  try {
    const productPayload = {
      name,
      price,
      description,
      category_id,
      supplier_id,
      inventory_id,
      imageUrls: selectedImageUrls,
    };
    console.log(productPayload);

    const res = await fetch("/products", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(productPayload),
    });
    if (!res.ok) {
      const err = await res.json();
      throw new Error(err.message ?? "Create product failed!");
    }

    Swal.fire({
      title: 'Successful!',
      text: 'Product is created successfully!',
      icon: 'success',
      confirmButtonText: 'OK'
    });

    await loadProducts();
    resetForm();
    bootstrap.Modal.getInstance(
      document.getElementById("addProductModal")
    ).hide();
  } catch (err) {
    console.error(err);
    Swal.fire({
      title: 'Lỗi!',
      text: err.message,
      icon: 'error',
      confirmButtonText: 'OK'
    });
  }
});

//Xử lý phần image
document.addEventListener("DOMContentLoaded", () => {
  const previewContainer = document.getElementById("previewContainer");
  const saveBtn = document.getElementById("saveProduct");

  document
    .getElementById("productImageFile")
    .addEventListener("change", async (e) => {
      const files = Array.from(e.target.files);
      previewContainer.innerHTML = "";
      selectedImageUrls = [];
      if (!files.length) return;

      files.forEach((file) => {
        const reader = new FileReader();
        reader.onload = (evt) => {
          const wrapper = document.createElement("div");
          wrapper.className = "image-preview-wrapper";

          const img = document.createElement("img");
          img.src = evt.target.result;
          img.className = "img-thumbnail";
          img.style.maxHeight = "100px";

          const actions = document.createElement("div");
          actions.className = "preview-action";

          const eyeIcon = document.createElement("i");
          eyeIcon.className = "bi bi-eye-fill";
          eyeIcon.title = "View";
          eyeIcon.onclick = () => {
            document.getElementById("modalImage").src = img.src;
            new bootstrap.Modal(
              document.getElementById("imagePreviewModal")
            ).show();
          };

          const deleteIcon = document.createElement("i");
          deleteIcon.className = "bi bi-x-circle-fill";
          deleteIcon.title = "Remove";
          deleteIcon.onclick = () => {
            const index = wrapper.getAttribute("data-index");
            selectedImageUrls.splice(index, 1);
            wrapper.remove();
            updatePreviewIndexes();
          };

          actions.appendChild(eyeIcon);
          actions.appendChild(deleteIcon);

          wrapper.appendChild(img);
          wrapper.appendChild(actions);
          previewContainer.appendChild(wrapper);
        };
        reader.readAsDataURL(file);
      });

      uploadInProgress = true;
      saveBtn.disabled = true;
      try {
        const uploadPromises = files.map(uploadSingleFile);
        const urls = await Promise.all(uploadPromises);
        selectedImageUrls = urls;
        console.log(selectedImageUrls);
        showToast("Images uploaded!", "success");
        updatePreviewIndexes();
      } catch (err) {
        console.error(err);
        showToast(err.message, "danger");
        selectedImageUrls = [];
        previewContainer.innerHTML = "";
        document.getElementById("productImageFile").value = "";
      } finally {
        uploadInProgress = false;
        saveBtn.disabled = false;
      }
    });
});

function updatePreviewIndexes() {
  const wrappers = previewContainer.querySelectorAll(".image-preview-wrapper");
  wrappers.forEach((w, i) => w.setAttribute("data-index", i));
}

/***************************************************************************************************************************/
/**
 * Xóa product
 */
function showDeleteModal(productId) {
  deleteId = productId;
  const modal = new bootstrap.Modal(
    document.getElementById("deleteConfirmModal")
  );
  modal.show();
}

document.getElementById("confirmDeleteBtn").onclick = async () => {
  if (!deleteId) return;

  try {
    const response = await fetch(`/products/${deleteId}`, {
      method: "DELETE",
      headers: {
        Accept: "application/json",
      },
    });

    if (!response.ok) {
      throw new Error(`Lỗi xoá: ${response.status}`);
    }

    await loadProducts();
    Swal.fire({
          title: 'Deleted Successfully!',
          text: 'The product was deleted.',
          icon: 'success',
          confirmButtonText: 'OK'
        });
    deleteId = null;

    const modalEl = document.getElementById("deleteConfirmModal");
    bootstrap.Modal.getInstance(modalEl).hide();
  } catch (err) {
    console.error(err);
        Swal.fire({
          title: 'Deleted failed!',
          text: 'Please try again.',
          icon: 'error',
          confirmButtonText: 'OK'
        });
  }
};

/***************************************************************************************************************************/
/**
 * Cập nhật product
 */
let editImages = [];
let editImagesLoading = false;

//Khi mở modal
async function showEditModal(productId) {
  try {
    if (!categories.length) await loadMasterData();

    const res = await fetch(`http://localhost:8080/products/${productId}`);
    if (!res.ok) throw new Error("Không thể lấy dữ liệu sản phẩm");
    const { result: product } = await res.json();

    renderSelect("editProductCategory", categories, product.categoryName);
    renderSelect("editProductSupplier", suppliers, product.supplierName);
    renderSelect("editProductInventory", inventories, product.inventoryName);

    document.getElementById("editProductId").value = product.id;
    document.getElementById("editProductName").value = product.name;
    document.getElementById("editProductPrice").value = product.price;
    document.getElementById("editProductDescription").value =
      product.description ?? "";

    editImages = [...product.imageUrls];
    const editPreviewContainer = document.getElementById(
      "editPreviewContainer"
    );
    editPreviewContainer.innerHTML = "";

    (product.imageUrls || []).forEach((url, i) => {
      const wrapper = document.createElement("div");
      wrapper.className = "image-preview-wrapper";
      wrapper.dataset.index = i;

      const img = document.createElement("img");
      img.src = url;
      img.className = "img-thumbnail";
      img.style.maxHeight = "100px";

      const actions = document.createElement("div");
      actions.className = "preview-action";

      const eyeIcon = document.createElement("i");
      eyeIcon.className = "bi bi-eye-fill";
      eyeIcon.title = "View";
      eyeIcon.onclick = () => {
        document.getElementById("modalImage").src = img.src;
        new bootstrap.Modal(
          document.getElementById("imagePreviewModal")
        ).show();
      };

      const deleteIcon = document.createElement("i");
      deleteIcon.className = "bi bi-x-circle-fill";
      deleteIcon.title = "Remove";
      deleteIcon.onclick = () => {
        const idx = Number(wrapper.dataset.index);
        editImages.splice(idx, 1);
        wrapper.remove();
        reIndexPreviews();
        console.log(editImages);
      };

      actions.appendChild(eyeIcon);
      actions.appendChild(deleteIcon);

      wrapper.appendChild(img);
      wrapper.appendChild(actions);
      editPreviewContainer.appendChild(wrapper);
    });
    document.getElementById("updateProduct").dataset.productId = productId;

    new bootstrap.Modal(document.getElementById("editProductModal")).show();
  } catch (err) {
    console.error("Lỗi khi gọi API:", err);
    alert("Không thể tải thông tin sản phẩm. Vui lòng thử lại sau.");
  }
}

//Khi nhấn update
document.getElementById("updateProduct").addEventListener("click", async () => {
  if (editImagesLoading)
    return showToast("Please wait, image upload in progress…", "warning");

  const productId = document.getElementById("updateProduct").dataset.productId;

  const name = document.getElementById("editProductName").value.trim();
  const category = document.getElementById("editProductCategory").value.trim();
  const supplier = document.getElementById("editProductSupplier").value.trim();
  const inventory = document
    .getElementById("editProductInventory")
    .value.trim();

  const category_id = categories.find(
    (c) => c.name.trim().toLowerCase() === category.toLowerCase()
  ).id;

  const supplier_id = suppliers.find(
    (c) => c.name.trim().toLowerCase() === supplier.toLowerCase()
  ).id;

  const inventory_id = inventories.find(
    (c) => c.name.trim().toLowerCase() === inventory.toLowerCase()
  )?.id;

  const price = parseFloat(document.getElementById("editProductPrice").value);
  const description = document
    .getElementById("editProductDescription")
    .value.trim();

  if (!name || !category || !supplier || !inventory || isNaN(price))
    return showToast("Please fill in all required fields!", "danger");
  if (price < 0) return showToast("Price must not be negative!", "danger");
  if (!editImages.length) return showToast("No images uploaded yet!", "danger");

  try {
    const productPayload = {
      name,
      price,
      description,
      category_id,
      supplier_id,
      inventory_id,
      imageUrls: editImages,
    };
    console.log(productPayload);
    const res = await fetch(`/products/${productId}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(productPayload),
    });
    if (!res.ok) {
      const err = await res.json();
      throw new Error(err.message ?? "Update product failed!");
    }

        Swal.fire({
          title: 'Updated Successfully!',
          text: 'Product updated successfully',
          icon: 'success',
          confirmButtonText: 'OK'
        });
    await loadProducts();
    resetForm();
    bootstrap.Modal.getInstance(
      document.getElementById("editProductModal")
    ).hide();
  } catch (err) {
    console.error(err);
    showToast(err.message, "danger");
  }
});

//Xử lý image
document.addEventListener("DOMContentLoaded", () => {
  const editPreviewContainer = document.getElementById("editPreviewContainer");
  const updateBtn = document.getElementById("updateProduct");

  document
    .getElementById("editProductImageFile")
    .addEventListener("change", async function (e) {
      const files = Array.from(e.target.files);
      editPreviewContainer.innerHTML = "";
      editImages = [];
      if (!files.length) return;

      files.forEach((file) => {
        const reader = new FileReader();
        reader.onload = (evt) => {
          const wrapper = document.createElement("div");
          wrapper.className = "image-preview-wrapper";

          const img = document.createElement("img");
          img.src = evt.target.result;
          img.className = "img-thumbnail";
          img.style.maxHeight = "100px";

          const actions = document.createElement("div");
          actions.className = "preview-action";

          const eyeIcon = document.createElement("i");
          eyeIcon.className = "bi bi-eye-fill";
          eyeIcon.title = "View";
          eyeIcon.onclick = () => {
            document.getElementById("modalImage").src = img.src;
            new bootstrap.Modal(
              document.getElementById("imagePreviewModal")
            ).show();
          };

          const deleteIcon = document.createElement("i");
          deleteIcon.className = "bi bi-x-circle-fill";
          deleteIcon.title = "Remove";
          deleteIcon.onclick = () => {
            const index = wrapper.getAttribute("data-index");
            editImages.splice(index, 1);
            wrapper.remove();
            updatePreviewIndexes();
          };

          actions.appendChild(eyeIcon);
          actions.appendChild(deleteIcon);

          wrapper.appendChild(img);
          wrapper.appendChild(actions);
          editPreviewContainer.appendChild(wrapper);
        };
        reader.readAsDataURL(file);
      });

      editImagesLoading = true;
      updateBtn.disabled = true;
      try {
        const uploadPromises = files.map(uploadSingleFile);
        const urls = await Promise.all(uploadPromises);
        editImages = urls;
        showToast("Images uploaded!", "success");
        updatePreviewIndexes();
      } catch (err) {
        console.error(err);
        showToast(err.message, "danger");
        editImages = [];
        editPreviewContainer.innerHTML = "";
        document.getElementById("editProductImageFile").value = "";
      } finally {
        editImagesLoading = false;
        updateBtn.disabled = false;
      }
    });
});

function reIndexPreviews() {
  const wrappers = document.querySelectorAll(
    "#editPreviewContainer .image-preview-wrapper"
  );
  wrappers.forEach((w, i) => (w.dataset.index = i));
}

/***************************************************************************************************************************/
/**
 * Filter và Sort
 */

//Debounce
const debouncedLoadProducts = debounce(() => {
  currentPage = 1;
  loadProducts(1);
}, 400);

//Search sản phẩm với debounce
document
  .getElementById("searchProduct")
  .addEventListener("input", debouncedLoadProducts);

//Filter theo category
document.getElementById("filterCategory").addEventListener("change", () => {
  currentPage = 1;
  loadProducts();
});

//Sort
document.getElementById("sortSelect").addEventListener("change", () => {
  currentPage = 1;
  loadProducts();
});

/***************************************************************************************************************************/
/**
 * Helper function
 *
 */

//Hiển thị toast
function showToast(message, type = "success") {
  const toastElement = document.getElementById("toastAlert");
  const toastBody = document.getElementById("toastMessage");

  toastElement.className = `toast align-items-center text-bg-${type} border-0`;
  toastBody.textContent = message;

  const toast = new bootstrap.Toast(toastElement);
  toast.show();
}

//Kỹ thuật debounce
function debounce(func, delay) {
  let timeoutId;
  return function (...args) {
    clearTimeout(timeoutId);
    timeoutId = setTimeout(() => func.apply(this, args), delay);
  };
}

//Call api để lấy danh sách product
async function loadProducts(page = 1) {
  isSearching = false;

  const sortValue = document.getElementById("sortSelect").value;
  const categoryId = document.getElementById("filterCategory").value;
  const name = document.getElementById("searchProduct").value.trim();

  let sort = "";
  if (sortValue === "price-asc") sort = "price";
  else if (sortValue === "price-desc") sort = "-price";
  else if (sortValue === "name-asc") sort = "name";
  else if (sortValue === "name-desc") sort = "-name";

  const queryParams = new URLSearchParams({
    page,
    size: pageSize,
  });

  if (name !== "") queryParams.append("name", name);
  if (categoryId !== "") queryParams.append("category_id", categoryId);
  if (sort !== "") queryParams.append("sort", sort);

  try {
    const response = await fetch(
      `http://localhost:8080/products/search?${queryParams.toString()}`
    );
    const data = await response.json();

    const products = data.result.data;
    pages = data.result.meta.pages;

    renderProducts(products);
    updatePrevNextButtons();
  } catch (error) {
    console.error("Failed to load products:", error);
  }
}

//Product table
function renderProducts(products) {
  const start = (currentPage - 1) * pageSize;
  const body = document.getElementById("product-table-body");
  body.innerHTML = "";

  let productsToRender = [...products];

  productsToRender.forEach((p, i) => {
    body.innerHTML += `
      <tr>
        <td>${start + i + 1}</td>
        <td>${p.id}</td>
        <td><img src="${p.imageUrls[0]}" alt="product" height="50"></td>
        <td>${p.name}</td>
        <td>${p.categoryName}</td>
        <td>$${p.price.toLocaleString()}</td>
        <td>
          <button class="btn btn-sm btn-outline-primary me-2" onclick="showEditModal(${
            p.id
          })"><i class="bi bi-pencil"></i></button>
          <button class="btn btn-sm btn-danger" onclick="showDeleteModal(${
            p.id
          })"><i class="bi bi-trash"></i></button>
        </td>
      </tr>`;
  });
}

//Xử lý phần pagination
function updatePrevNextButtons() {
  const prevBtn = document.getElementById("prevPage");
  const nextBtn = document.getElementById("nextPage");

  if (currentPage <= 1) {
    prevBtn.style.display = "none";
  } else {
    prevBtn.style.display = "inline-block";
  }

  if (currentPage >= pages) {
    nextBtn.style.display = "none";
  } else {
    nextBtn.style.display = "inline-block";
  }
}

document.getElementById("prevPage").onclick = () => {
  if (currentPage > 0) currentPage--;
  loadProducts(currentPage);
};

document.getElementById("nextPage").onclick = () => {
  if (currentPage + 1 <= pages) currentPage++;
  loadProducts(currentPage);
};

//Upload ảnh
async function uploadSingleFile(file) {
  const formData = new FormData();
  formData.append("fileImage", file);

  const res = await fetch("/upload", { method: "POST", body: formData });
  if (!res.ok) throw new Error("Upload failed!");

  const json = await res.json();
  console.log("Upload JSON:", json);

  if (json.code !== 1000) {
    throw new Error(json.message || "Upload error");
  }

  const imageUrl = json.result?.imageUrl;
  if (!imageUrl) throw new Error("imageUrl missing in response!");
  return imageUrl;
}

//Reset lại form sau khi create, update
function resetForm() {
  document.getElementById("productName").value = "";
  document.getElementById("productCategory").value = "";
  document.getElementById("productSupplier").value = "";
  document.getElementById("productInventory").value = "";
  document.getElementById("productPrice").value = "";
  document.getElementById("productDescription").value = "";
  document.getElementById("productImageFile").value = "";
  document.getElementById("previewContainer").innerHTML = "";
  selectedImageUrls = [];
}
