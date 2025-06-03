// suppliers.js

let suppliers = [
  {
    id: 101,
    name: "Công ty Mỹ phẩm ABC",
    contact: "Địa chỉ: 123 Đường Lê Lợi, Q1, TP.HCM | ĐT: 0909123456 | Email: abc@example.com"
  },
  {
    id: 102,
    name: "Thương hiệu Derma Beauty",
    contact: "Địa chỉ: 456 Đường Nguyễn Huệ, Q1, TP.HCM | ĐT: 0987654321 | Email: info@dermabeauty.com"
  },
  {
    id: 103,
    name: "Nhà phân phối Global Cosmetics",
    contact: "789 Đường Pasteur, Q3, TP.HCM | ĐT: 02838223344 | Email: contact@globalcosmetics.vn"
  }
];

let deleteId = null;
let currentFilter = "";
let currentSort = "";
let currentPage = 1;
const pageSize = 5;

function renderSuppliers() {
  const body = document.getElementById("supplierBody");
  body.innerHTML = "";

  let filteredSuppliers = suppliers.filter(item =>
    item.name.toLowerCase().includes(currentFilter.toLowerCase()) ||
    item.contact.toLowerCase().includes(currentFilter.toLowerCase())
  );

  if (currentSort === "name-asc") {
    filteredSuppliers.sort((a, b) => a.name.localeCompare(b.name));
  } else if (currentSort === "name-desc") {
    filteredSuppliers.sort((a, b) => b.name.localeCompare(a.name));
  } else if (currentSort === "id-asc") {
    filteredSuppliers.sort((a, b) => a.id - b.id);
  } else if (currentSort === "id-desc") {
    filteredSuppliers.sort((a, b) => b.id - a.id);
  }

  const totalPages = Math.ceil(filteredSuppliers.length / pageSize);
  currentPage = Math.min(currentPage, totalPages);
  const start = (currentPage - 1) * pageSize;
  const pageItems = filteredSuppliers.slice(start, start + pageSize);

  pageItems.forEach((item) => {
    body.innerHTML += `
      <tr>
        <td>${item.id}</td>
        <td>${item.name}</td>
        <td>${item.contact}</td>
        <td>
          <button class="btn btn-sm btn-outline-primary me-1" onclick="editSupplier(${item.id})">
            <i class="bi bi-pencil"></i>
          </button>
          <button class="btn btn-sm btn-outline-danger" onclick="showDeleteModal(${item.id})">
            <i class="bi bi-trash"></i>
          </button>
        </td>
      </tr>
    `;
  });

  document.getElementById("prevPageBtn").disabled = currentPage === 1;
  document.getElementById("nextPageBtn").disabled = currentPage === totalPages || totalPages === 0;
}

function showToast(message, type = "success") {
  const toastElement = document.getElementById("toastAlert");
  const toastBody = document.getElementById("toastMessage");

  toastElement.className = `toast align-items-center text-bg-${type} border-0 show`;
  toastBody.textContent = message;

  const toast = bootstrap.Toast.getOrCreateInstance(toastElement);
  toast.show();
}

function showDeleteModal(id) {
  deleteId = id;
  const modal = new bootstrap.Modal(document.getElementById("confirmDeleteModal"));
  modal.show();
}

document.getElementById("confirmDeleteBtn").addEventListener("click", () => {
  if (deleteId !== null) {
    suppliers = suppliers.filter(s => s.id !== deleteId);
    renderSuppliers();
    showToast("Supplier deleted successfully!", "success");
    deleteId = null;
    bootstrap.Modal.getInstance(document.getElementById("confirmDeleteModal")).hide();
  }
});

document.getElementById("saveSupplierBtn").addEventListener("click", () => {
  const name = document.getElementById("supplierName").value.trim();
  const contact = document.getElementById("supplierContact").value.trim();

  if (!name || !contact) {
    showToast("Please enter all fields!", "danger");
    return;
  }

  const editingId = document.getElementById("saveSupplierBtn").getAttribute("data-edit-id");

  if (editingId) {
    const idx = suppliers.findIndex(s => s.id == editingId);
    if (idx !== -1) {
      suppliers[idx].name = name;
      suppliers[idx].contact = contact;
      showToast("Supplier updated successfully!", "success");
    }
    document.getElementById("saveSupplierBtn").removeAttribute("data-edit-id");
  } else {
    const maxId = suppliers.length > 0 ? Math.max(...suppliers.map(s => s.id)) : 100;
    suppliers.push({ id: maxId + 1, name, contact });
    showToast("Supplier added successfully!", "success");
  }

  renderSuppliers();
  bootstrap.Modal.getInstance(document.getElementById("addSupplierModal")).hide();
  document.getElementById("supplierName").value = "";
  document.getElementById("supplierContact").value = "";
});

function editSupplier(id) {
  const supplier = suppliers.find(s => s.id === id);
  if (supplier) {
    document.getElementById("supplierName").value = supplier.name;
    document.getElementById("supplierContact").value = supplier.contact;
    document.getElementById("saveSupplierBtn").setAttribute("data-edit-id", supplier.id);
    const modal = new bootstrap.Modal(document.getElementById("addSupplierModal"));
    modal.show();
  }
}

document.getElementById("searchSupplier").addEventListener("input", (e) => {
  currentFilter = e.target.value;
  currentPage = 1;
  renderSuppliers();
});

document.getElementById("sortSupplier").addEventListener("change", (e) => {
  currentSort = e.target.value;
  renderSuppliers();
});

document.getElementById("prevPageBtn").addEventListener("click", () => {
  if (currentPage > 1) {
    currentPage--;
    renderSuppliers();
  }
});

document.getElementById("nextPageBtn").addEventListener("click", () => {
  currentPage++;
  renderSuppliers();
});

window.onload = () => {
  renderSuppliers();
};
