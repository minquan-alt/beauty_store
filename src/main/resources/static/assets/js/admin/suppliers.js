let suppliers = [];
let deleteId = null;
let currentFilter = "";
let currentSort = "";
let currentPage = 1;
const itemsPerPage = 10;

document.addEventListener('DOMContentLoaded', function() {
  loadSuppliersFromDOM();
  renderSuppliers();
  setupEventListeners();
});

function loadSuppliersFromDOM() {
  const rows = document.querySelectorAll('#supplierBody tr');
  suppliers = [];
  
  rows.forEach((row, index) => {
    const cells = row.querySelectorAll('td');
    if (cells.length >= 3) {
      suppliers.push({
        id: parseInt(cells[0].textContent) || index + 1,
        name: cells[1].textContent.trim(),
        contactInfo: cells[2].textContent.trim()
      });
    }
  });
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

function deleteSupplier(id) {
  showDeleteModal(id);
}

function editSupplier(id) {
  const supplier = suppliers.find(s => s.id === id);
  if (supplier) {
    document.getElementById("supplierName").value = supplier.name;
    document.getElementById("supplierContact").value = supplier.contactInfo;
    document.getElementById("saveSupplierBtn").setAttribute("data-edit-id", supplier.id);
    
    // Change modal title for editing
    document.querySelector('#addSupplierModal .modal-title').textContent = 'Edit Supplier';
    
    const modal = new bootstrap.Modal(document.getElementById("addSupplierModal"));
    modal.show();
  }
}

function filterAndSortSuppliers() {
  let filtered = [...suppliers];
  
  if (currentFilter) {
    filtered = filtered.filter(supplier => 
      supplier.name.toLowerCase().includes(currentFilter.toLowerCase()) ||
      supplier.contactInfo.toLowerCase().includes(currentFilter.toLowerCase())
    );
  }
  
  if (currentSort) {
    const [field, order] = currentSort.split('-');
    filtered.sort((a, b) => {
      let aVal = a[field];
      let bVal = b[field];
      
      if (field === 'name' || field === 'contactInfo') {
        aVal = aVal.toLowerCase();
        bVal = bVal.toLowerCase();
      }
      
      if (order === 'asc') {
        return aVal > bVal ? 1 : -1;
      } else {
        return aVal < bVal ? 1 : -1;
      }
    });
  }
  
  return filtered;
}

function paginateSuppliers(filtered) {
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  return filtered.slice(startIndex, endIndex);
}

function renderSuppliers() {
  const tbody = document.getElementById("supplierBody");
  const filtered = filterAndSortSuppliers();
  const paginated = paginateSuppliers(filtered);
  
  if (paginated.length === 0) {
    tbody.innerHTML = '<tr><td colspan="4" class="text-center">No suppliers found</td></tr>';
  } else {
    tbody.innerHTML = paginated.map((supplier, index) => {
      const globalIndex = (currentPage - 1) * itemsPerPage + index + 1;
      return `
        <tr>
          <td>${globalIndex}</td>
          <td>${supplier.name}</td>
          <td>${supplier.contactInfo}</td>
          <td>
            <button type="button" class="btn btn-primary btn-sm" onclick="editSupplier(${supplier.id})">
              Edit
            </button>
            <button type="button" class="btn btn-danger btn-sm" onclick="deleteSupplier(${supplier.id})">
              Delete
            </button>
          </td>
        </tr>
      `;
    }).join('');
  }
  
  updatePaginationButtons(filtered.length);
}

function updatePaginationButtons(totalItems) {
  const totalPages = Math.ceil(totalItems / itemsPerPage);
  const prevBtn = document.getElementById("prevPageBtn");
  const nextBtn = document.getElementById("nextPageBtn");
  
  prevBtn.disabled = currentPage <= 1;
  nextBtn.disabled = currentPage >= totalPages;
}

function setupEventListeners() {
  document.getElementById("searchSupplier").addEventListener("input", (e) => {
    currentFilter = e.target.value;
    currentPage = 1;
    renderSuppliers();
  });
  
  document.getElementById("sortSupplier").addEventListener("change", (e) => {
    currentSort = e.target.value;
    currentPage = 1;
    renderSuppliers();
  });
  
  document.getElementById("prevPageBtn").addEventListener("click", () => {
    if (currentPage > 1) {
      currentPage--;
      renderSuppliers();
    }
  });
  
  document.getElementById("nextPageBtn").addEventListener("click", () => {
    const filtered = filterAndSortSuppliers();
    const totalPages = Math.ceil(filtered.length / itemsPerPage);
    if (currentPage < totalPages) {
      currentPage++;
      renderSuppliers();
    }
  });
  
  document.getElementById("saveSupplierBtn").addEventListener("click", async () => {
    const name = document.getElementById("supplierName").value.trim();
    const contact = document.getElementById("supplierContact").value.trim();
    
    if (!name || !contact) {
      showToast("Please enter all fields!", "danger");
      return;
    }
    
    const editingId = document.getElementById("saveSupplierBtn").getAttribute("data-edit-id");
    
    if (editingId) {
      // EDIT MODE - Cập nhật supplier
      const idx = suppliers.findIndex(s => s.id == editingId);
      if (idx !== -1) {
        const updatedSupplier = {
          id: parseInt(editingId),
          name: name,
          contactInfo: contact
        };
        
        // Gọi API để cập nhật trên server
        const success = await updateSupplierOnServer(updatedSupplier);
        
        if (success) {
          // Chỉ cập nhật local array khi server thành công
          suppliers[idx].name = name;
          suppliers[idx].contactInfo = contact;
          showToast("Supplier updated successfully!", "success");
          
          // Đóng modal và clear form
          const modal = bootstrap.Modal.getInstance(document.getElementById("addSupplierModal"));
          modal.hide();
          clearModalForm();
          renderSuppliers();
        }
      }
      document.getElementById("saveSupplierBtn").removeAttribute("data-edit-id");
      
    } else {
      const newSupplier = {
        name: name,
        contactInfo: contact
      };
      
      const result = await addSupplierOnServer(newSupplier);
      
      if (result.success) {
        suppliers.push({ 
          id: result.data.id, 
          name: result.data.name, 
          contactInfo: result.data.contactInfo 
        });
        showToast("Supplier added successfully!", "success");
        
        // Đóng modal và clear form
        const modal = bootstrap.Modal.getInstance(document.getElementById("addSupplierModal"));
        modal.hide();
        clearModalForm();
        renderSuppliers();
      }
    }
    
    document.getElementById("saveSupplierBtn").blur();
  });
  
  document.getElementById("confirmDeleteBtn").addEventListener("click", () => {
    if (deleteId !== null) {

      const success = deleteSupplierFromServer(deleteId);
      if(success) {
        suppliers = suppliers.filter(s => s.id !== deleteId);
        renderSuppliers();
        showToast("Supplier deleted successfully!", "success");
        deleteId = null;

      }

      const modal = bootstrap.Modal.getInstance(document.getElementById("confirmDeleteModal"));
      modal.hide();
      document.getElementById("confirmDeleteBtn").blur();
    }
  });
  
  document.querySelector('[data-bs-target="#addSupplierModal"]').addEventListener('click', () => {
    document.querySelector('#addSupplierModal .modal-title').textContent = 'Add Supplier';
    document.getElementById("saveSupplierBtn").removeAttribute("data-edit-id");
    clearModalForm();
  });
}

function clearModalForm() {
  document.getElementById("supplierName").value = "";
  document.getElementById("supplierContact").value = "";
}


async function updateSupplierOnServer(supplier) {
  try {
    const response = await fetch(`/suppliers/${supplier.id}`, { // Sửa endpoint
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(supplier)
    });
    
    if (!response.ok) {
      throw new Error('Failed to update supplier');
    }
    
    return true;
  } catch (error) {
    console.error('Error updating supplier:', error);
    showToast("Error updating supplier on server", "danger");
    return false; 
  }
}

async function addSupplierOnServer(supplier) {
  try {
    console.log(supplier);
    const response = await fetch('/suppliers', { 
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(supplier)
    });
    
    if (!response.ok) {
      throw new Error('Failed to add supplier');
    }
    
    const data = await response.json();
    return { success: true, data: data.result };
    
  } catch (error) {
    console.error('Error adding supplier:', error);
    showToast("Error adding supplier to server", "danger");
    return { success: false, data: null };
  }
}

async function deleteSupplierFromServer(id) {
  try {
    console.log("delete ID: ", id )
    const response = await fetch(`/suppliers/${id}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    
    if (!response.ok) {
      const data = await response.json(); 
      console.log(data)
      throw new Error(data.message);
    }
    
    return true;
  } catch (error) {
    console.error('Error deleting supplier:', error);
    showToast(`${error}`, "danger");
    return error;
  }
}