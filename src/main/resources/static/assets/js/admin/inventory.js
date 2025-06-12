// // inventory.js
// let stockData = JSON.parse(localStorage.getItem("inventoryRecords")) || [];
// const stockTableBody = document.getElementById("stockTableBody");
// const toastAlert = new bootstrap.Toast(document.getElementById("toastAlert"));
// const toastMessage = document.getElementById("toastMessage");
// const searchInput = document.getElementById("searchInput");
// const typeFilter = document.getElementById("typeFilter");
// const sortField = document.getElementById("sortField");
// const sortOrder = document.getElementById("sortOrder");
// const applyFilterBtn = document.getElementById("applyFilter");
// const prevPageBtn = document.getElementById("prevPage");
// const nextPageBtn = document.getElementById("nextPage");
// const pageInfo = document.getElementById("pageInfo");

// let currentPage = 1;
// const recordsPerPage = 5;

// function showToast(msg) {
//   toastMessage.textContent = msg;
//   toastAlert.show();
// }

// function renderStockTable() {
//   stockTableBody.innerHTML = "";

//   let filtered = [...stockData];
//   const keyword = searchInput.value.trim().toLowerCase();
//   const type = typeFilter.value;

//   if (keyword) {
//     filtered = filtered.filter(
//       (r) =>
//         r.productId.toString().includes(keyword) ||
//         r.supplier.toLowerCase().includes(keyword)
//     );
//   }

//   if (type) {
//     filtered = filtered.filter((r) => r.transactionType === type);
//   }

//   const field = sortField.value;
//   const order = sortOrder.value;
//   filtered.sort((a, b) => {
//     if (field === "quantity") {
//       return order === "asc"
//         ? a.quantity - b.quantity
//         : b.quantity - a.quantity;
//     } else {
//       return order === "asc"
//         ? new Date(a.date) - new Date(b.date)
//         : new Date(b.date) - new Date(a.date);
//     }
//   });

//   const totalPages = Math.ceil(filtered.length / recordsPerPage);
//   currentPage = Math.min(currentPage, totalPages);
//   const start = (currentPage - 1) * recordsPerPage;
//   const paginated = filtered.slice(start, start + recordsPerPage);

//   paginated.forEach((record) => {
//     const row = document.createElement("tr");
//     row.innerHTML = `
//       <td>${record.id}</td>
//       <td>${record.productId}</td>
//       <td>${record.quantity}</td>
//       <td>${record.date}</td>
//       <td>${record.supplier}</td>
//       <td>${record.transactionType}</td>
//       <td>${record.sourceType}</td>
//       <td>
//         <button class="btn btn-sm btn-info me-1" onclick="viewDetail(${record.id})"><i class="bi bi-eye"></i></button>
//         <button class="btn btn-sm btn-warning me-1" onclick="editRecord(${record.id})"><i class="bi bi-pencil"></i></button>
//         <button class="btn btn-sm btn-danger" onclick="deleteRecord(${record.id})"><i class="bi bi-trash"></i></button>
//       </td>
//     `;
//     stockTableBody.appendChild(row);
//   });

//   pageInfo.textContent = `Page ${currentPage} of ${totalPages || 1}`;
// }

// function resetFormInputs() {
//   [
//     "productIdInput",
//     "quantityInput",
//     "supplierInput",
//     "sourceTypeInput",
//   ].forEach((id) => (document.getElementById(id).value = ""));
//   document.getElementById("transactionTypeInput").value = "IN";
// }

// document.getElementById("saveStockBtn").addEventListener("click", addNewRecord);

// function addNewRecord() {
//   const productId = document.getElementById("productIdInput").value.trim();
//   const quantity = document.getElementById("quantityInput").value.trim();
//   const supplier = document.getElementById("supplierInput").value.trim();
//   const transactionType = document.getElementById("transactionTypeInput").value;
//   const sourceType = document.getElementById("sourceTypeInput").value.trim();

//   if (!productId || !quantity || !supplier || !transactionType || !sourceType) {
//     showToast("Please fill in all fields");
//     return;
//   }

//   const newRecord = {
//     id: stockData.length > 0 ? stockData[stockData.length - 1].id + 1 : 1,
//     productId: parseInt(productId),
//     quantity: parseInt(quantity),
//     date: new Date().toLocaleString(),
//     supplier,
//     transactionType,
//     sourceType,
//   };

//   stockData.push(newRecord);
//   localStorage.setItem("inventoryRecords", JSON.stringify(stockData));
//   renderStockTable();
//   showToast("Inventory record added successfully");
//   bootstrap.Modal.getInstance(document.getElementById("addStockModal")).hide();
//   resetFormInputs();
// }

// function viewDetail(id) {
//   const record = stockData.find((r) => r.id === id);
//   document.getElementById("detailProductId").textContent = record.productId;
//   document.getElementById("detailQuantity").textContent = record.quantity;
//   document.getElementById("detailDate").textContent = record.date;
//   document.getElementById("detailSupplier").textContent = record.supplier;
//   document.getElementById("detailType").textContent = record.transactionType;
//   document.getElementById("detailSource").textContent = record.sourceType;
//   new bootstrap.Modal(document.getElementById("viewDetailModal")).show();
// }

// function editRecord(id) {
//   const record = stockData.find((r) => r.id === id);
//   if (!record) return;

//   document.getElementById("editId").value = record.id;
//   document.getElementById("editProductId").value = record.productId;
//   document.getElementById("editQuantity").value = record.quantity;
//   document.getElementById("editSupplier").value = record.supplier;
//   document.getElementById("editTransactionType").value = record.transactionType;
//   document.getElementById("editSourceType").value = record.sourceType;

//   new bootstrap.Modal(document.getElementById("editModal")).show();
// }

// document.getElementById("updateStockBtn").addEventListener("click", () => {
//   const id = parseInt(document.getElementById("editId").value);
//   const productId = parseInt(document.getElementById("editProductId").value);
//   const quantity = parseInt(document.getElementById("editQuantity").value);
//   const supplier = document.getElementById("editSupplier").value.trim();
//   const transactionType = document.getElementById("editTransactionType").value;
//   const sourceType = document.getElementById("editSourceType").value.trim();

//   const record = stockData.find((r) => r.id === id);
//   if (record) {
//     record.productId = productId;
//     record.quantity = quantity;
//     record.supplier = supplier;
//     record.transactionType = transactionType;
//     record.sourceType = sourceType;
//     localStorage.setItem("inventoryRecords", JSON.stringify(stockData));
//     renderStockTable();
//     showToast("Record updated successfully");
//     bootstrap.Modal.getInstance(document.getElementById("editModal")).hide();
//   }
// });

// function deleteRecord(id) {
//   document.getElementById("deleteId").value = id;
//   new bootstrap.Modal(document.getElementById("deleteModal")).show();
// }

// document.getElementById("confirmDeleteBtn").addEventListener("click", () => {
//   const id = parseInt(document.getElementById("deleteId").value);
//   stockData = stockData.filter((r) => r.id !== id);
//   localStorage.setItem("inventoryRecords", JSON.stringify(stockData));
//   renderStockTable();
//   showToast("Inventory record deleted");
//   bootstrap.Modal.getInstance(document.getElementById("deleteModal")).hide();
// });

// applyFilterBtn.addEventListener("click", () => {
//   currentPage = 1;
//   renderStockTable();
// });

// searchInput.addEventListener("input", () => {
//   currentPage = 1;
//   renderStockTable();
// });

// typeFilter.addEventListener("change", () => {
//   currentPage = 1;
//   renderStockTable();
// });

// sortField.addEventListener("change", () => {
//   renderStockTable();
// });

// sortOrder.addEventListener("change", () => {
//   renderStockTable();
// });

// prevPageBtn.addEventListener("click", () => {
//   if (currentPage > 1) {
//     currentPage--;
//     renderStockTable();
//   }
// });

// nextPageBtn.addEventListener("click", () => {
//   const keyword = searchInput.value.trim().toLowerCase();
//   const type = typeFilter.value;
//   let filtered = [...stockData];

//   if (keyword)
//     filtered = filtered.filter(
//       (r) =>
//         r.productId.toString().includes(keyword) ||
//         r.supplier.toLowerCase().includes(keyword)
//     );
//   if (type) filtered = filtered.filter((r) => r.transactionType === type);

//   const totalPages = Math.ceil(filtered.length / recordsPerPage);
//   if (currentPage < totalPages) {
//     currentPage++;
//     renderStockTable();
//   }
// });

// renderStockTable();

const toastAlert = new bootstrap.Toast(document.getElementById("toastAlert"));

let currentPage = 1;
const pageSize = 10;
let pages = 0;

async function loadPurchaseOrders(page = 1) {
  const queryParams = new URLSearchParams({
    page,
    size: pageSize,
  });

  try {
    const response = await fetch(
      `http://localhost:8080/purchase-orders?${queryParams.toString()}`
    );
    const data = await response.json();

    const purchaseOrders = data.result.data;
    pages = data.result.meta.pages;

    renderPurchaseOrder(purchaseOrders);
    updatePrevNextButtons();
  } catch (error) {
    console.error("Failed to load purchase orders:", error);
  }
}

function formatDateToCustom(dateString) {
  const date = new Date(dateString);
  const pad = (n) => n.toString().padStart(2, "0");

  const day = pad(date.getDate());
  const month = pad(date.getMonth() + 1);
  const year = date.getFullYear();
  const hours = pad(date.getHours());
  const minutes = pad(date.getMinutes());
  const seconds = pad(date.getSeconds());

  return `${day}-${month}-${year} ${hours}:${minutes}:${seconds}`;
}

function renderPurchaseOrder(purchaseOrders) {
  const body = document.getElementById("stockTableBody");
  body.innerHTML = "";

  let purchaseOrdersToRender = [...purchaseOrders];

  purchaseOrdersToRender.forEach((p, i) => {
    const orderDateFormatted = formatDateToCustom(p.orderDate);

    let actionButtons = `
    <button class="btn btn-sm btn-outline-info view-btn" data-id="${p.purchaseOrderId}">
      <i class="bi bi-eye"></i>
    </button>
  `;

    if (p.status == "Pending") {
      actionButtons += `
      <button class="btn btn-sm btn-outline-success approve-btn ms-2" data-id="${p.purchaseOrderId}">
        <i class="bi bi-check-circle"></i>
      </button>
      <button class="btn btn-sm btn-outline-danger decline-btn ms-2" data-id="${p.purchaseOrderId}">
        <i class="bi bi-x-circle"></i>
      </button>
    `;
    }

    body.innerHTML += `
      <tr>
        <td>${p.purchaseOrderId}</td>
        <td>${orderDateFormatted}</td>
        <td>${p.status}</td>
        <td>${actionButtons}</td>
      </tr>
    `;
  });

  document.querySelectorAll(".approve-btn").forEach((btn) => {
    btn.addEventListener("click", () => {
      handleConfirm(btn.dataset.id, "approve");
    });
  });

  document.querySelectorAll(".decline-btn").forEach((btn) => {
    btn.addEventListener("click", () =>
      handleConfirm(btn.dataset.id, "decline")
    );
  });

  document.querySelectorAll(".view-btn").forEach((btn) => {
    btn.addEventListener("click", () => {
      const id = parseInt(btn.dataset.id);
      const order = purchaseOrders.find((po) => po.purchaseOrderId === id);
      if (order) {
        showPurchaseOrderDetail(order);
      }
    });
  });
}

function handleConfirm(id, action) {
  const confirmed = confirm(`Are you sure you want to ${action} this order?`);

  if (confirmed) {
    fetch(`/purchase-orders/${id}/${action}`, {
      method: "PUT",
    })
      .then((res) => {
        if (res.ok) {
          showToast(`${action} successful!`);
          loadPurchaseOrders();
        } else {
          showToast(`Failed to ${action} order.`);
        }
      })
      .catch((err) => console.error("Error:", err));
  }
}

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
  loadPurchaseOrders(currentPage);
};

document.getElementById("nextPage").onclick = () => {
  if (currentPage + 1 <= pages) currentPage++;
  loadPurchaseOrders(currentPage);
};

document.getElementById("saveStockBtn").addEventListener("click", addNewRecord);
function addNewRecord() {
  const items = [];
  const rows = document.querySelectorAll(".purchase-item-row");

  rows.forEach((row) => {
    const productId = row.querySelector(".product-id").value;
    const quantity = row.querySelector(".quantity").value;
    const unitPrice = row.querySelector(".unit-price").value;

    if (productId && quantity && unitPrice) {
      items.push({
        productId: Number(productId),
        quantity: Number(quantity),
        unitPrice: parseFloat(unitPrice),
      });
    }
  });

  if (items.length === 0) {
    showToast("Please enter at least one item.");
    return;
  }

  const payload = { items };

  fetch("/purchase-orders", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Failed to create order");
      return res.json();
    })
    .then((data) => {
      loadPurchaseOrders();
      showToast("Purchase order created!");
      document;
    })
    .catch((err) => {
      alert("Error: " + err.message);
    });

  loadPurchaseOrders();
  showToast("Inventory record added successfully");
  bootstrap.Modal.getInstance(document.getElementById("addStockModal")).hide();
  resetFormInputs();
}

document.getElementById("addRowBtn").addEventListener("click", function () {
  const container = document.getElementById("purchaseItemsContainer");
  const row = document.createElement("div");
  row.className = "row mb-3 purchase-item-row";

  row.innerHTML = `
    <div class="col">
      <input type="number" class="form-control product-id" placeholder="Product ID">
    </div>
    <div class="col">
      <input type="number" class="form-control quantity" placeholder="Quantity">
    </div>
    <div class="col">
      <input type="number" class="form-control unit-price" placeholder="Unit Price" step="0.01">
    </div>
  `;

  container.appendChild(row);
});

function showToast(msg) {
  toastMessage.textContent = msg;
  toastAlert.show();
}

window.addEventListener("load", function () {
  loadPurchaseOrders();
});

function showPurchaseOrderDetail(order) {
  const modalBody = document.getElementById("purchaseOrderDetail");

  const html = `
    <p><strong>Order ID:</strong> ${order.purchaseOrderId}</p>
    <p><strong>Order Date:</strong> ${new Date(
      order.orderDate
    ).toLocaleString()}</p>
    <p><strong>Status:</strong> ${order.status}</p>

    <h6>Items:</h6>
    <table class="table table-bordered">
      <thead class="table-dark">
        <tr>
          <th>Product ID</th>
          <th>Product Name</th>
          <th>Quantity</th>
          <th>Unit Price</th>
          <th>Total</th>
        </tr>
      </thead>
      <tbody>
        ${order.items
          .map(
            (item) => `
          <tr>
            <td>${item.productId}</td>
            <td>${item.productName}</td>
            <td>${item.quantity}</td>
            <td>${item.unitPrice}</td>
            <td>${item.quantity * item.unitPrice}</td>
          </tr>
        `
          )
          .join("")}
      </tbody>
    </table>
  `;

  modalBody.innerHTML = html;
  const modal = new bootstrap.Modal(document.getElementById("viewDetailModal"));
  modal.show();
}
