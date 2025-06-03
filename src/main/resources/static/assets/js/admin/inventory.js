// inventory.js
let stockData = JSON.parse(localStorage.getItem("inventoryRecords")) || [];
const stockTableBody = document.getElementById("stockTableBody");
const toastAlert = new bootstrap.Toast(document.getElementById("toastAlert"));
const toastMessage = document.getElementById("toastMessage");
const searchInput = document.getElementById("searchInput");
const typeFilter = document.getElementById("typeFilter");
const sortField = document.getElementById("sortField");
const sortOrder = document.getElementById("sortOrder");
const applyFilterBtn = document.getElementById("applyFilter");
const prevPageBtn = document.getElementById("prevPage");
const nextPageBtn = document.getElementById("nextPage");
const pageInfo = document.getElementById("pageInfo");

let currentPage = 1;
const recordsPerPage = 5;

function showToast(msg) {
  toastMessage.textContent = msg;
  toastAlert.show();
}

function renderStockTable() {
  stockTableBody.innerHTML = "";

  let filtered = [...stockData];
  const keyword = searchInput.value.trim().toLowerCase();
  const type = typeFilter.value;

  if (keyword) {
    filtered = filtered.filter(
      r => r.productId.toString().includes(keyword) || r.supplier.toLowerCase().includes(keyword)
    );
  }

  if (type) {
    filtered = filtered.filter(r => r.transactionType === type);
  }

  const field = sortField.value;
  const order = sortOrder.value;
  filtered.sort((a, b) => {
    if (field === "quantity") {
      return order === "asc" ? a.quantity - b.quantity : b.quantity - a.quantity;
    } else {
      return order === "asc" ? new Date(a.date) - new Date(b.date) : new Date(b.date) - new Date(a.date);
    }
  });

  const totalPages = Math.ceil(filtered.length / recordsPerPage);
  currentPage = Math.min(currentPage, totalPages);
  const start = (currentPage - 1) * recordsPerPage;
  const paginated = filtered.slice(start, start + recordsPerPage);

  paginated.forEach(record => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${record.id}</td>
      <td>${record.productId}</td>
      <td>${record.quantity}</td>
      <td>${record.date}</td>
      <td>${record.supplier}</td>
      <td>${record.transactionType}</td>
      <td>${record.sourceType}</td>
      <td>
        <button class="btn btn-sm btn-info me-1" onclick="viewDetail(${record.id})"><i class="bi bi-eye"></i></button>
        <button class="btn btn-sm btn-warning me-1" onclick="editRecord(${record.id})"><i class="bi bi-pencil"></i></button>
        <button class="btn btn-sm btn-danger" onclick="deleteRecord(${record.id})"><i class="bi bi-trash"></i></button>
      </td>
    `;
    stockTableBody.appendChild(row);
  });

  pageInfo.textContent = `Page ${currentPage} of ${totalPages || 1}`;
}

function resetFormInputs() {
  ["productIdInput", "quantityInput", "supplierInput", "sourceTypeInput"].forEach(id => document.getElementById(id).value = "");
  document.getElementById("transactionTypeInput").value = "IN";
}

document.getElementById("saveStockBtn").addEventListener("click", addNewRecord);

function addNewRecord() {
  const productId = document.getElementById("productIdInput").value.trim();
  const quantity = document.getElementById("quantityInput").value.trim();
  const supplier = document.getElementById("supplierInput").value.trim();
  const transactionType = document.getElementById("transactionTypeInput").value;
  const sourceType = document.getElementById("sourceTypeInput").value.trim();

  if (!productId || !quantity || !supplier || !transactionType || !sourceType) {
    showToast("Please fill in all fields");
    return;
  }

  const newRecord = {
    id: stockData.length > 0 ? stockData[stockData.length - 1].id + 1 : 1,
    productId: parseInt(productId),
    quantity: parseInt(quantity),
    date: new Date().toLocaleString(),
    supplier,
    transactionType,
    sourceType
  };

  stockData.push(newRecord);
  localStorage.setItem("inventoryRecords", JSON.stringify(stockData));
  renderStockTable();
  showToast("Inventory record added successfully");
  bootstrap.Modal.getInstance(document.getElementById("addStockModal")).hide();
  resetFormInputs();
}

function viewDetail(id) {
  const record = stockData.find(r => r.id === id);
  document.getElementById("detailProductId").textContent = record.productId;
  document.getElementById("detailQuantity").textContent = record.quantity;
  document.getElementById("detailDate").textContent = record.date;
  document.getElementById("detailSupplier").textContent = record.supplier;
  document.getElementById("detailType").textContent = record.transactionType;
  document.getElementById("detailSource").textContent = record.sourceType;
  new bootstrap.Modal(document.getElementById("viewDetailModal")).show();
}

function editRecord(id) {
  const record = stockData.find(r => r.id === id);
  if (!record) return;

  document.getElementById("editId").value = record.id;
  document.getElementById("editProductId").value = record.productId;
  document.getElementById("editQuantity").value = record.quantity;
  document.getElementById("editSupplier").value = record.supplier;
  document.getElementById("editTransactionType").value = record.transactionType;
  document.getElementById("editSourceType").value = record.sourceType;

  new bootstrap.Modal(document.getElementById("editModal")).show();
}

document.getElementById("updateStockBtn").addEventListener("click", () => {
  const id = parseInt(document.getElementById("editId").value);
  const productId = parseInt(document.getElementById("editProductId").value);
  const quantity = parseInt(document.getElementById("editQuantity").value);
  const supplier = document.getElementById("editSupplier").value.trim();
  const transactionType = document.getElementById("editTransactionType").value;
  const sourceType = document.getElementById("editSourceType").value.trim();

  const record = stockData.find(r => r.id === id);
  if (record) {
    record.productId = productId;
    record.quantity = quantity;
    record.supplier = supplier;
    record.transactionType = transactionType;
    record.sourceType = sourceType;
    localStorage.setItem("inventoryRecords", JSON.stringify(stockData));
    renderStockTable();
    showToast("Record updated successfully");
    bootstrap.Modal.getInstance(document.getElementById("editModal")).hide();
  }
});

function deleteRecord(id) {
  document.getElementById("deleteId").value = id;
  new bootstrap.Modal(document.getElementById("deleteModal")).show();
}

document.getElementById("confirmDeleteBtn").addEventListener("click", () => {
  const id = parseInt(document.getElementById("deleteId").value);
  stockData = stockData.filter(r => r.id !== id);
  localStorage.setItem("inventoryRecords", JSON.stringify(stockData));
  renderStockTable();
  showToast("Inventory record deleted");
  bootstrap.Modal.getInstance(document.getElementById("deleteModal")).hide();
});

applyFilterBtn.addEventListener("click", () => {
  currentPage = 1;
  renderStockTable();
});

searchInput.addEventListener("input", () => {
  currentPage = 1;
  renderStockTable();
});

typeFilter.addEventListener("change", () => {
  currentPage = 1;
  renderStockTable();
});

sortField.addEventListener("change", () => {
  renderStockTable();
});

sortOrder.addEventListener("change", () => {
  renderStockTable();
});

prevPageBtn.addEventListener("click", () => {
  if (currentPage > 1) {
    currentPage--;
    renderStockTable();
  }
});

nextPageBtn.addEventListener("click", () => {
  const keyword = searchInput.value.trim().toLowerCase();
  const type = typeFilter.value;
  let filtered = [...stockData];

  if (keyword) filtered = filtered.filter(r => r.productId.toString().includes(keyword) || r.supplier.toLowerCase().includes(keyword));
  if (type) filtered = filtered.filter(r => r.transactionType === type);

  const totalPages = Math.ceil(filtered.length / recordsPerPage);
  if (currentPage < totalPages) {
    currentPage++;
    renderStockTable();
  }
});

renderStockTable();
