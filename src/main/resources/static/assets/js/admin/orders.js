const orders = [
  {
    id: 1001,
    customer: "Nguyễn Văn A",
    date: "2024-05-10",
    address: "123 Lê Lợi, Quận 1, TP.HCM",
    status: "Pending",
    paymentMethod: "Cash on Delivery",
    total: 1250000,
    items: [
      { name: "Lipstick", quantity: 2, price: 250000 },
      { name: "Perfume", quantity: 1, price: 750000 }
    ]
  },
  {
    id: 1002,
    customer: "Trần Thị B",
    date: "2024-05-11",
    address: "456 Trần Hưng Đạo, Quận 5, TP.HCM",
    status: "Processing",
    paymentMethod: "MOMO",
    total: 950000,
    items: [
      { name: "Shampoo", quantity: 3, price: 200000 },
      { name: "Eyeliner", quantity: 1, price: 150000 }
    ]
  }
];

let currentPage = 0;
const rowsPerPage = 5;
let filteredOrders = [...orders];

function renderOrders() {
  const tbody = document.getElementById("orderTableBody");
  tbody.innerHTML = "";
  const start = currentPage * rowsPerPage;
  const end = start + rowsPerPage;

  filteredOrders.slice(start, end).forEach((order, index) => {
    tbody.innerHTML += `
      <tr>
        <td>#${order.id}</td>
        <td>${order.customer}</td>
        <td>${order.date}</td>
        <td>
  <select class="form-select form-select-sm status-select status-${order.status.toLowerCase()}" data-id="${order.order_id}">
    <option value="Pending" ${order.status === "Pending" ? "selected" : ""}>Pending</option>
    <option value="Confirmed" ${order.status === "Confirmed" ? "selected" : ""}>Confirmed</option>
    <option value="Processing" ${order.status === "Processing" ? "selected" : ""}>Processing</option>
    <option value="Shipped" ${order.status === "Shipped" ? "selected" : ""}>Shipped</option>
    <option value="Delivered" ${order.status === "Delivered" ? "selected" : ""}>Delivered</option>
    <option value="Cancelled" ${order.status === "Cancelled" ? "selected" : ""}>Cancelled</option>
    <option value="Returned" ${order.status === "Returned" ? "selected" : ""}>Returned</option>
  </select>
</td>

        <td>${order.paymentMethod}</td>
        <td>${order.total.toLocaleString()} đ</td>
        <td>
          <button class="btn btn-sm btn-outline-primary" onclick="showDetails(${order.id})">Detail</button>
          <button class="btn btn-sm btn-outline-danger" onclick="confirmDelete(${order.id})">Delete</button>
        </td>
      </tr>
    `;
  });

  document.querySelectorAll(".status-select").forEach(select => {
    select.addEventListener("change", e => {
      const id = +e.target.dataset.id;
      const newStatus = e.target.value;
      const order = orders.find(o => o.id === id);
      if (order) {
        order.status = newStatus;
        e.target.className = `form-select form-select-sm status-select status-${newStatus.toLowerCase()}`;
      }
    });
  });
}

function searchOrders() {
  const keyword = document.getElementById("searchInput").value.trim().toLowerCase();
  const statusFilter = document.getElementById("filterStatus").value;

  filteredOrders = orders.filter(order => {
    const matchKeyword =
      order.customer.toLowerCase().includes(keyword) ||
      order.id.toString().includes(keyword);
    const matchStatus =
      statusFilter === "" || order.status.toLowerCase() === statusFilter.toLowerCase();
    return matchKeyword && matchStatus;
  });

  currentPage = 0;
  renderOrders();
}

function showDetails(orderId) {
  const order = orders.find(o => o.id === orderId);
  if (!order) return;

  document.getElementById("orderModalTitle").textContent = `Order #${order.id}`;
  document.getElementById("orderDate").textContent = order.date;
  document.getElementById("orderCustomer").textContent = order.customer;
  document.getElementById("orderAddress").textContent = order.address || "N/A";
  document.getElementById("orderPayment").textContent = order.paymentMethod || "N/A";

  const tbody = document.getElementById("orderDetailBody");
  tbody.innerHTML = order.items.map(item => `
    <tr>
      <td>${item.name}</td>
      <td>${item.quantity}</td>
      <td>${item.price.toLocaleString()} đ</td>
      <td>${(item.quantity * item.price).toLocaleString()} đ</td>
    </tr>
  `).join("");

  new bootstrap.Modal(document.getElementById("orderDetailModal")).show();
}

function confirmDelete(orderId) {
  const modal = new bootstrap.Modal(document.getElementById("cancelOrderModal"));
  modal.show();

  document.getElementById("confirmCancelBtn").onclick = () => {
    const index = orders.findIndex(o => o.id === orderId);
    if (index !== -1) {
      orders.splice(index, 1);
      searchOrders();
      modal.hide();
    }
  };
}

function exportOrders() {
  let csv = "Order ID,Customer,Date,Phone,Address,Payment,Status,Total\n";
  orders.forEach(o => {
    csv += `${o.id},${o.customer},${o.date},${o.phone},${o.address},${o.paymentMethod},${o.status},${o.total}\n`;
  });
  const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
  const link = document.createElement("a");
  link.href = URL.createObjectURL(blob);
  link.download = "orders.csv";
  link.click();
}

// Event listeners
document.getElementById("searchInput").addEventListener("input", searchOrders);
document.getElementById("filterStatus").addEventListener("change", searchOrders);
document.getElementById("exportOrders").addEventListener("click", exportOrders);
document.getElementById("prevPage").addEventListener("click", () => {
  if (currentPage > 0) currentPage--;
  renderOrders();
});
document.getElementById("nextPage").addEventListener("click", () => {
  if ((currentPage + 1) * rowsPerPage < filteredOrders.length) currentPage++;
  renderOrders();
});

// Init
window.onload = () => {
  filteredOrders = [...orders];
  renderOrders();
};
