// Order Management System - Refactored Version

class OrderManager {
  constructor() {
    this.orders = [];
    this.filteredOrders = [];
    this.currentPage = 0;
    this.rowsPerPage = 5;
    this.init();
  }

  init() {
    this.attachEventListeners();
    this.loadOrders();
  }

  async loadOrders() {
    try {
      const response = await fetch("http://localhost:8080/api/orders", {
        method: "GET",
        headers: {
          "Content-Type": "application/json"
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      // console.log('Orders loaded:', data);
      
      this.orders = data.result || data || [];
      this.filteredOrders = [...this.orders];
      this.renderOrders();
      this.updatePaginationInfo();
      
    } catch (error) {
      console.error("Error loading orders:", error);
      this.showError("Failed to load orders. Please try again.");
    }
  }

  async updateOrderStatus(orderId, newStatus) {
    try {
      const response = await fetch(`http://localhost:8080/api/orders/${orderId}`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ status: newStatus })
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      // Update local data
      const order = this.orders.find(o => o.orderId === orderId);
      if (order) {
        order.status = newStatus;
      }
      
      console.log(`Order ${orderId} status updated to ${newStatus}`);
      
    } catch (error) {
      console.error("Error updating order status:", error);
      this.showError("Failed to update order status.");
    }
  }

  async deleteOrder(orderId) {
    try {
      const response = await fetch(`http://localhost:8080/api/orders/${orderId}`, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json"
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      // Remove from local data
      const index = this.orders.findIndex(o => o.id === orderId);
      if (index !== -1) {
        this.orders.splice(index, 1);
        this.searchOrders();
      }
      
      console.log(`Order ${orderId} deleted successfully`);
      
    } catch (error) {
      console.error("Error deleting order:", error);
      this.showError("Failed to delete order.");
    }
  }

  // Rendering Methods
  renderOrders() {
    const tbody = document.getElementById("orderTableBody");
    if (!tbody) {
      console.error("Order table body not found");
      return;
    }

    tbody.innerHTML = "";
    
    if (this.filteredOrders.length === 0) {
      tbody.innerHTML = '<tr><td colspan="7" class="text-center">No orders found</td></tr>';
      return;
    }

    const start = this.currentPage * this.rowsPerPage;
    const end = start + this.rowsPerPage;
    const ordersToShow = this.filteredOrders.slice(start, end);

    ordersToShow.forEach((order) => {
      const row = this.createOrderRow(order);
      tbody.appendChild(row);
    });

    this.attachStatusChangeListeners();
    this.updatePaginationControls();
  }

  createOrderRow(order) {
    const row = document.createElement('tr');
    row.innerHTML = `
      <td>#${order.orderId}</td>
      <td>${this.escapeHtml(order.customerName || 'N/A')}</td>
      <td>${this.escapeHtml(order.orderDate || 'N/A')}</td>
      <td>
        <select class="form-select form-select-sm status-select status-${(order.status || 'Pending').toLowerCase()}" 
                data-id="${order.orderId}">
          <option value="Pending"  ${order.status === "Pending" ? "selected" : ""}>Pending</option>
          <option value="Confirmed" ${order.status === "Confirmed" ? "selected" : ""}>Confirmed</option>
          <option value="Delivered" ${order.status === "Delivered" ? "selected" : ""}>Delivered</option>
          <option value="Cancelled" ${order.status === "Cancelled" ? "selected" : ""}>Cancelled</option>
        </select>
      </td>
      <td>${this.escapeHtml(order.paymentMethod || 'N/A')}</td>
      <td>${this.formatCurrency(order.totalAmount || 0)}</td>
      <td>
        <button class="btn btn-sm btn-outline-primary me-1" 
                onclick="orderManager.showDetails(${order.orderId})">Detail</button>
      </td>
    `;
    return row;
  }

  // Search and Filter Methods
  searchOrders() {
    const searchInput = document.getElementById("searchInput");
    const filterStatus = document.getElementById("filterStatus");
    
    if (!searchInput || !filterStatus) {
      console.error("Search elements not found");
      return;
    }

    const keyword = searchInput.value.trim().toLowerCase();
    const statusFilter = filterStatus.value;

    this.filteredOrders = this.orders.filter(order => {
      const matchKeyword = 
        (order.customerName && order.customerName.toLowerCase().includes(keyword)) ||
        (order.orderId && order.orderId.toString().includes(keyword));
      
      const matchStatus = 
        statusFilter === "" || 
        (order.status && order.status.toLowerCase() === statusFilter.toLowerCase());
      
      return matchKeyword && matchStatus;
    });

    this.currentPage = 0;
    this.renderOrders();
    this.updatePaginationInfo();
  }

  // Modal Methods
  showDetails(orderId) {
    const order = this.orders.find(o => o.orderId === orderId);
    if (!order) {
      console.error(`Order with ID ${orderId} not found`);
      return;
    }

    // Update modal content
    this.updateElement("orderModalTitle", `Order #${order.orderId}`);
    this.updateElement("orderDate", order.orderDate || 'N/A');
    // this.updateElement("orderCustomer", order.customer || 'N/A');
    this.updateElement("orderAddress", order.address || 'N/A');
    this.updateElement("orderPayment", order.paymentMethod || 'N/A');

    // Update order items table
    const tbody = document.getElementById("orderDetailBody");
    if (tbody && order.items && Array.isArray(order.items)) {
      tbody.innerHTML = order.items.map(item => `
        <tr>
          <td>${this.escapeHtml(item.productName || 'N/A')}</td>
          <td>${item.quantity || 0}</td>
          <td>${this.formatCurrency(item.price || 0)}</td>
          <td>${this.formatCurrency((item.quantity || 0) * (item.price || 0))}</td>
        </tr>
      `).join("");
    }

    // Show modal
    const modal = document.getElementById("orderDetailModal");
    if (modal && typeof bootstrap !== 'undefined') {
      new bootstrap.Modal(modal).show();
    }
  }

  confirmDelete(orderId) {
    const modal = document.getElementById("cancelOrderModal");
    if (!modal) {
      console.error("Cancel order modal not found");
      return;
    }

    const bootstrapModal = new bootstrap.Modal(modal);
    bootstrapModal.show();

    const confirmBtn = document.getElementById("confirmCancelBtn");
    if (confirmBtn) {
      // Remove existing listeners to prevent multiple attachments
      const newBtn = confirmBtn.cloneNode(true);
      confirmBtn.parentNode.replaceChild(newBtn, confirmBtn);
      
      newBtn.onclick = () => {
        this.deleteOrder(orderId);
        bootstrapModal.hide();
      };
    }
  }

  // Export Methods
  exportOrders() {
    if (this.orders.length === 0) {
      this.showError("No orders to export");
      return;
    }

    try {
      let csv = "Order ID,Customer,Date,Phone,Address,Payment,Status,Total\n";
      
      this.orders.forEach(order => {
        const row = [
          order.id || '',
          this.escapeCsv(order.customerName || ''),
          this.escapeCsv(order.date || ''),
          this.escapeCsv(order.phone || ''),
          this.escapeCsv(order.address || ''),
          this.escapeCsv(order.paymentMethod || ''),
          this.escapeCsv(order.status || ''),
          order.total || 0
        ].join(',');
        
        csv += row + '\n';
      });

      const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
      const link = document.createElement("a");
      link.href = URL.createObjectURL(blob);
      link.download = `orders_${new Date().toISOString().split('T')[0]}.csv`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      
    } catch (error) {
      console.error("Error exporting orders:", error);
      this.showError("Failed to export orders");
    }
  }

  // Pagination Methods
  prevPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.renderOrders();
    }
  }

  nextPage() {
    const maxPage = Math.ceil(this.filteredOrders.length / this.rowsPerPage) - 1;
    if (this.currentPage < maxPage) {
      this.currentPage++;
      this.renderOrders();
    }
  }

  updatePaginationControls() {
    const prevBtn = document.getElementById("prevPage");
    const nextBtn = document.getElementById("nextPage");
    
    if (prevBtn) {
      prevBtn.disabled = this.currentPage === 0;
    }
    
    if (nextBtn) {
      const maxPage = Math.ceil(this.filteredOrders.length / this.rowsPerPage) - 1;
      nextBtn.disabled = this.currentPage >= maxPage || this.filteredOrders.length === 0;
    }
  }

  updatePaginationInfo() {
    const info = document.getElementById("paginationInfo");
    if (info) {
      const start = this.currentPage * this.rowsPerPage + 1;
      const end = Math.min((this.currentPage + 1) * this.rowsPerPage, this.filteredOrders.length);
      const total = this.filteredOrders.length;
      
      info.textContent = `Showing ${start}-${end} of ${total} orders`;
    }
  }

  // Event Listeners
  attachEventListeners() {
    // Search and filter
    const searchInput = document.getElementById("searchInput");
    const filterStatus = document.getElementById("filterStatus");
    
    if (searchInput) {
      searchInput.addEventListener("input", () => this.searchOrders());
    }
    
    if (filterStatus) {
      filterStatus.addEventListener("change", () => this.searchOrders());
    }

    // Export
    const exportBtn = document.getElementById("exportOrders");
    if (exportBtn) {
      exportBtn.addEventListener("click", () => this.exportOrders());
    }

    // Pagination
    const prevBtn = document.getElementById("prevPage");
    const nextBtn = document.getElementById("nextPage");
    
    if (prevBtn) {
      prevBtn.addEventListener("click", () => this.prevPage());
    }
    
    if (nextBtn) {
      nextBtn.addEventListener("click", () => this.nextPage());
    }
  }

  attachStatusChangeListeners() {
    document.querySelectorAll(".status-select").forEach(select => {
      select.addEventListener("change", async (e) => {
        const orderId = parseInt(e.target.dataset.id);
        const newStatus = e.target.value;
        
        // Update UI immediately
        e.target.className = `form-select form-select-sm status-select status-${newStatus.toLowerCase()}`;
        
        // Update on server
        await this.updateOrderStatus(orderId, newStatus);
      });
    });
  }

  escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  escapeCsv(text) {
    if (typeof text !== 'string') return text;
    if (text.includes(',') || text.includes('"') || text.includes('\n')) {
      return `"${text.replace(/"/g, '""')}"`;
    }
    return text;
  }

  formatCurrency(amount) {
    if (typeof amount !== 'number') return '$0 ';
    return `$ ${amount.toLocaleString()} `;
  }

  updateElement(id, content) {
    const element = document.getElementById(id);
    if (element) {
      element.textContent = content;
    }
  }

  showError(message) {
    console.error(message);
    alert(message);
  }
}

// Initialize the application
let orderManager;

document.addEventListener('DOMContentLoaded', () => {
  orderManager = new OrderManager();
});

// Fallback for older browsers
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', () => {
    if (!orderManager) {
      orderManager = new OrderManager();
    }
  });
} else {
  orderManager = new OrderManager();
}