let nextProductId = 1;
// Math.max(...sampleProducts.map((p) => p.productId), 1000) + 1;

let currentPage = 1;
const pageSize = 10;
let pages = 0;
let deleteId = null;
let categories = [];

function loadProducts(page = 1) {
  isSearching = false;
  fetch(`http://localhost:8080/products?page=${page}&size=${pageSize}`)
    .then((res) => res.json())
    .then((data) => {
      const products = data.result.data;
      pages = data.result.meta.pages;
      renderProducts(products);
      updatePrevNextButtons();
    })
    .catch((error) => {
      console.error("Failed to load products:", error);
    });
}

function renderProducts(products) {
  const start = (currentPage - 1) * pageSize;
  const body = document.getElementById("product-table-body");
  body.innerHTML = "";

  let productsToRender = [...products];

  // Lọc theo tên
  // const searchText = document
  //   .getElementById("searchProduct")
  //   .value.toLowerCase();
  // if (searchText) {
  //   productsToRender = productsToRender.filter((p) =>
  //     p.name.toLowerCase().includes(searchText)
  //   );
  // }

  // Lọc theo danh mục
  // const filterCategory = document
  //   .getElementById("filterCategory")
  //   .value.toLowerCase();
  // if (filterCategory) {
  //   productsToRender = productsToRender.filter(
  //     (p) => p.category.toLowerCase() === filterCategory
  //   );
  // }

  // Sắp xếp
  // const sortValue = document.getElementById("sortSelect").value;
  // switch (sortValue) {
  //   case "name-asc":
  //     productsToRender.sort((a, b) => a.name.localeCompare(b.name));
  //     break;
  //   case "name-desc":
  //     productsToRender.sort((a, b) => b.name.localeCompare(a.name));
  //     break;
  //   case "price-asc":
  //     productsToRender.sort((a, b) => a.price - b.price);
  //     break;
  //   case "price-desc":
  //     productsToRender.sort((a, b) => b.price - a.price);
  //     break;
  // }

  productsToRender.forEach((p, i) => {
    body.innerHTML += `
      <tr>
        <td>${start + i + 1}</td>
        <td>${p.id}</td>
        <td><img src="${p.imageUrls[0]}" alt="product" height="50"></td>
        <td>${p.name}</td>
        <td>${p.categoryName}</td>
        <td>${p.price.toLocaleString()} đ</td>
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

document.getElementById("sortSelect").addEventListener("change", () => {
  currentPage = 0;
  renderProducts();
});

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
  const modal = new bootstrap.Modal(
    document.getElementById("deleteConfirmModal")
  );
  modal.show();
}
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
  categories = (await res.json()).result;
  const select = document.getElementById("productSupplier");
  select.innerHTML = '<option value="">Select Supplier</option>';
  categories.forEach((c) => {
    const opt = document.createElement("option");
    opt.value = c.id;
    opt.textContent = c.name;
    select.appendChild(opt);
  });
}

async function loadInventories() {
  const res = await fetch("http://localhost:8080/inventories");
  categories = (await res.json()).result;
  const select = document.getElementById("productInventory");
  select.innerHTML = '<option value="">Select Inventory</option>';
  categories.forEach((c) => {
    const opt = document.createElement("option");
    opt.value = c.id;
    opt.textContent = c.name + " - " + c.address;
    select.appendChild(opt);
  });
}

// document.getElementById("confirmDeleteBtn").onclick = () => {
//   const index = sampleProducts.findIndex((p) => p.productId === deleteId);
//   if (index !== -1) {
//     sampleProducts.splice(index, 1);
//     saveToStorage();
//     renderProducts();
//     deleteId = null;
//     bootstrap.Modal.getInstance(
//       document.getElementById("deleteConfirmModal")
//     ).hide();
//   }
// };

function showEditModal(productId) {
  fetch(`http://localhost:8080/products/${productId}`)
    .then((response) => {
      if (!response.ok) {
        throw new Error("Không thể lấy dữ liệu sản phẩm");
      }
      return response.json();
    })
    .then((data) => {
      const product = data.result;
      if (!product) return;

      const select = document.getElementById("editProductCategory");
      select.innerHTML = '<option value="">Select Category</option>';
      categories.forEach((c) => {
        const opt = document.createElement("option");
        opt.value = c.name;
        opt.textContent = c.name;
        select.appendChild(opt);
      });

      document.getElementById("editProductId").value = product.id;
      document.getElementById("editProductName").value = product.name;
      document.getElementById("editProductCategory").value =
        product.categoryName;
      console.log(">>>category: ", product.categoryName);
      document.getElementById("editProductPrice").value = product.price;
      document.getElementById("editProductDescription").value =
        product.description || "";

      const preview = document.getElementById("editPreviewImage");
      preview.src = product.imageUrls[0];
      preview.classList.remove("d-none");

      document.getElementById("updateProduct").dataset.productId = productId;

      const modal = new bootstrap.Modal(
        document.getElementById("editProductModal")
      );
      modal.show();
    })
    .catch((error) => {
      console.error("Lỗi khi gọi API:", error);
      alert("Không thể tải thông tin sản phẩm. Vui lòng thử lại sau.");
    });
}

// document.getElementById("updateProduct").addEventListener("click", function () {
//   const index = this.dataset.index;
//   const name = document.getElementById("editProductName").value.trim();
//   const category = document.getElementById("editProductCategory").value.trim();
//   const price = parseFloat(document.getElementById("editProductPrice").value);
//   const description = document
//     .getElementById("editProductDescription")
//     .value.trim();
//   const imageFile = document.getElementById("editProductImageFile").files[0];

//   if (!name || !category || isNaN(price))
//     return showToast("Please fill in all required fields!", "danger");
//   if (price < 0) return showToast("Price must not be negative!", "danger");

//   const update = (imageBase64) => {
//     sampleProducts[index] = {
//       ...sampleProducts[index],
//       name,
//       category,
//       price,
//       description,
//       image: imageBase64 || sampleProducts[index].image,
//     };
//     saveToStorage();
//     renderProducts();
//     bootstrap.Modal.getInstance(
//       document.getElementById("editProductModal")
//     ).hide();
//   };

//   if (imageFile) {
//     const reader = new FileReader();
//     reader.onload = (e) => update(e.target.result);
//     reader.readAsDataURL(imageFile);
//   } else {
//     update(null);
//   }
// });

document
  .getElementById("editProductImageFile")
  .addEventListener("change", function () {
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

document
  .getElementById("productImageFile")
  .addEventListener("change", function () {
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
  const description = document
    .getElementById("productDescription")
    .value.trim();
  const imageFile = document.getElementById("productImageFile").files[0];

  if (!name || !category || isNaN(price))
    return showToast("Please fill in all required fields!", "danger");
  if (price < 0) return showToast("Price must not be negative!", "danger");

  const reader = new FileReader();
  reader.onload = (e) => {
    const newProduct = {
      productId: nextProductId++,
      name,
      category,
      price,
      description,
      image: e.target.result,
    };
    sampleProducts.push(newProduct);
    saveToStorage();
    renderProducts();
    resetForm();
    bootstrap.Modal.getInstance(
      document.getElementById("addProductModal")
    ).hide();
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

document
  .getElementById("addProductModal")
  .addEventListener("shown.bs.modal", () => {
    loadCategories("productCategory");
    loadSuppliers();
  });

window.addEventListener("load", function () {
  loadProducts();
  loadCategories("filterCategory");
  loadInventories();
});
document.getElementById("searchProduct").addEventListener("input", () => {
  currentPage = 0;
  renderProducts();
});

document.getElementById("filterCategory").addEventListener("change", () => {
  currentPage = 0;
  renderProducts();
});
