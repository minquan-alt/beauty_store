// Global variables
let users = [];
let filteredUsers = [];
let currentPage = 1;
const usersPerPage = 10;

// Valid roles
const VALID_ROLES = ['ROLE_ADMIN', 'ROLE_USER', 'ROLE_MANAGER'];

// DOM elements
const userTableBody = document.getElementById('user-table-body');
const roleFilter = document.getElementById('roleFilter');
const searchInput = document.getElementById('searchProduct');
const prevPageBtn = document.getElementById('prevPageBtn');
const nextPageBtn = document.getElementById('nextPageBtn');

// Modal elements
const addUserModal = new bootstrap.Modal(document.getElementById('addUserModal'));
const editUserModal = new bootstrap.Modal(document.getElementById('editUserModal'));
const deleteUserModal = new bootstrap.Modal(document.getElementById('deleteUserModal'));

// Form elements
const addUserName = document.getElementById('addUserName');
const addUserEmail = document.getElementById('addUserEmail');
const addUserPhone = document.getElementById('addUserPhone');
const addUserRole = document.getElementById('addUserRole');

const editUserId = document.getElementById('editUserId');
const editUserName = document.getElementById('editUserName');
const editUserEmail = document.getElementById('editUserEmail');
const editUserPhone = document.getElementById('editUserPhone');
const editUserRole = document.getElementById('editUserRole');

const deleteUserId = document.getElementById('deleteUserId');

// Toast
const userToast = new bootstrap.Toast(document.getElementById('userToast'));
const userToastMessage = document.getElementById('userToastMessage');

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Check if all required elements exist before initializing
    if (!userTableBody || !roleFilter || !searchInput || !prevPageBtn || !nextPageBtn) {
        console.error('Required DOM elements not found. Please check your HTML structure.');
        return;
    }
    
    loadUsers();
    setupEventListeners();
    populateRoleSelects();
});

// Populate role select dropdowns with valid roles
function populateRoleSelects() {
    const roleSelects = [addUserRole, editUserRole, roleFilter];
    
    roleSelects.forEach(select => {
        if (select) {
            // Clear existing options (except "All" for filter)
            if (select === roleFilter) {
                select.innerHTML = '<option value="">All Roles</option>';
            } else {
                select.innerHTML = '<option value="">Select Role</option>';
            }
            
            // Add valid role options
            VALID_ROLES.forEach(role => {
                const option = document.createElement('option');
                option.value = role;
                option.textContent = formatRoleDisplay(role);
                select.appendChild(option);
            });
        }
    });
}

// Setup event listeners
function setupEventListeners() {
    // Filter and search
    roleFilter.addEventListener('change', filterUsers);
    searchInput.addEventListener('input', filterUsers);
    
    // Pagination
    prevPageBtn.addEventListener('click', () => changePage(-1));
    nextPageBtn.addEventListener('click', () => changePage(1));
    
    // Modal buttons
    document.getElementById('saveUserBtn').addEventListener('click', saveUser);
    document.getElementById('updateUserBtn').addEventListener('click', updateUser);
    document.getElementById('confirmDeleteUserBtn').addEventListener('click', confirmDeleteUser);
}

// API calls
async function loadUsers() {
    try {
        showLoadingState();
        const response = await fetch('/users');
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        
        if (data.code === 1000) {
            users = Array.isArray(data.result) ? data.result : [];
            console.log(users);
            filteredUsers = [...users];
            renderUsers();
            updatePagination();
        } else {
            showToast('Error loading users: ' + (data.message || 'Unknown error'), 'error');
            users = [];
            filteredUsers = [];
            renderUsers();
            updatePagination();
        }
    } catch (error) {
        console.error('Error loading users:', error);
        showToast('Failed to load users. Please check your connection.', 'error');
        users = [];
        filteredUsers = [];
        renderUsers();
        updatePagination();
    }
}

async function saveUser() {
    const userData = {
        name: addUserName.value.trim(),
        email: addUserEmail.value.trim(),
        phone: addUserPhone.value.trim(),
        role: addUserRole.value
    };
    
    // Validation
    if (!userData.name || !userData.email || !userData.role || !userData.phone) {
        showToast('Please fill in all required fields', 'error');
        return;
    }
    
    if (!isValidEmail(userData.email)) {
        showToast('Please enter a valid email address', 'error');
        return;
    }
    
    if (!isValidRole(userData.role)) {
        showToast('Please select a valid role', 'error');
        return;
    }
    
    try {
        const response = await fetch('/users', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData)
        });
        
        const data = await response.json();
        
        if (data.code === 1000) {
            addUserModal.hide();
            clearAddUserForm();
            loadUsers();
            showToast('User created successfully', 'success');
        } else {
            showToast('Error creating user: ' + data.message, 'error');
        }
    } catch (error) {
        console.error('Error saving user:', error);
        showToast('Failed to create user', 'error');
    }
}

async function updateUser() {
    const userId = parseInt(editUserId.value);
    const userData = {
        name: editUserName.value.trim(),
        email: editUserEmail.value.trim(),
        phone: editUserPhone.value.trim(),
        role: editUserRole.value
    };
    
    // Validation
    if (!userData.name || !userData.email || !userData.role || !userData.phone) {
        showToast('Please fill in all required fields', 'error');
        return;
    }
    
    if (!isValidEmail(userData.email)) {
        showToast('Please enter a valid email address', 'error');
        return;
    }
    
    if (!isValidRole(userData.role)) {
        showToast('Please select a valid role', 'error');
        return;
    }
    
    try {
        const response = await fetch(`/users/update-user/${userId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData)
        });
        
        const data = await response.json();
        
        if (data.code === 1000) {
            editUserModal.hide();
            loadUsers();
            showToast('User updated successfully', 'success');
        } else {
            showToast('Error updating user: ' + data.message, 'error');
        }
    } catch (error) {
        console.error('Error updating user:', error);
        showToast('Failed to update user', 'error');
    }
}

async function confirmDeleteUser() {
    const userId = parseInt(deleteUserId.value);
    
    try {
        const response = await fetch(`/users/${userId}`, {
            method: 'DELETE'
        });
        
        const data = await response.json();
        
        if (data.code === 1000) {
            deleteUserModal.hide();
            loadUsers();
            showToast('User deleted successfully', 'success');
        } else {
            showToast('Error deleting user: ' + data.message, 'error');
        }
    } catch (error) {
        console.error('Error deleting user:', error);
        showToast('Failed to delete user', 'error');
    }
}

// UI Functions
function renderUsers() {
    const startIndex = (currentPage - 1) * usersPerPage;
    const endIndex = startIndex + usersPerPage;
    const paginatedUsers = filteredUsers.slice(startIndex, endIndex);
    
    if (paginatedUsers.length === 0) {
        userTableBody.innerHTML = `
            <tr>
                <td colspan="5" class="text-center py-4">
                    <i class="bi bi-inbox fs-1 text-muted"></i>
                    <p class="text-muted mt-2">No users found</p>
                </td>
            </tr>
        `;
        return;
    }
    
    userTableBody.innerHTML = paginatedUsers.map(user => `
        <tr>
            <td>${user.id}</td>
            <td>${user.name || 'N/A'}</td>
            <td>${user.email || 'N/A'}</td>
            <td>${user.phone || 'N/A'}</td>
            <td>
                <span class="badge ${getRoleBadgeClass(user.role)}">
                    ${formatRoleDisplay(user.role) || 'N/A'}
                </span>
            </td>
            <td>
                <div class="btn-group" role="group">
                    <button class="btn btn-sm btn-outline-primary" onclick="openEditModal(${user.id})" title="Edit">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="openDeleteModal(${user.id})" title="Delete">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

function showLoadingState() {
    userTableBody.innerHTML = `
        <tr>
            <td colspan="5" class="text-center py-4">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
                <p class="text-muted mt-2">Loading users...</p>
            </td>
        </tr>
    `;
}

function filterUsers() {
    const roleValue = roleFilter.value;
    const searchValue = searchInput.value.toLowerCase();
    
    filteredUsers = users.filter(user => {
        const roleMatch = !roleValue || user.role === roleValue;
        const searchMatch = !searchValue || 
            (user.name && user.name.toLowerCase().includes(searchValue)) ||
            (user.email && user.email.toLowerCase().includes(searchValue));
        
        return roleMatch && searchMatch;
    });
    
    currentPage = 1; // Reset to first page
    renderUsers();
    updatePagination();
}

function updatePagination() {
    const totalPages = Math.ceil(filteredUsers.length / usersPerPage);
    
    prevPageBtn.disabled = currentPage <= 1;
    nextPageBtn.disabled = currentPage >= totalPages;
    
    // Update or create page info
    const pageInfo = totalPages > 0 ? `Page ${currentPage} of ${totalPages}` : 'No pages';
    let pageInfoElement = document.getElementById('pageInfo');
    
    if (pageInfoElement) {
        pageInfoElement.textContent = pageInfo;
    } else {
        // Create page info element
        const paginationContainer = document.querySelector('.d-flex.justify-content-end');
        if (paginationContainer) {
            const pageInfoSpan = document.createElement('span');
            pageInfoSpan.id = 'pageInfo';
            pageInfoSpan.className = 'align-self-center text-muted small ms-3';
            pageInfoSpan.textContent = pageInfo;
            paginationContainer.appendChild(pageInfoSpan);
        }
    }
}

function changePage(direction) {
    const totalPages = Math.ceil(filteredUsers.length / usersPerPage);
    const newPage = currentPage + direction;
    
    if (newPage >= 1 && newPage <= totalPages) {
        currentPage = newPage;
        renderUsers();
        updatePagination();
    }
}

// Modal functions
function openEditModal(userId) {
    const user = users.find(u => u.id === userId);
    if (!user) {
        showToast('User not found', 'error');
        return;
    }
    
    editUserId.value = user.id;
    editUserName.value = user.name || '';
    editUserEmail.value = user.email || '';
    editUserPhone.value = user.phone || '';
    editUserRole.value = user.role || '';
    
    editUserModal.show();
}

function openDeleteModal(userId) {
    deleteUserId.value = userId;
    deleteUserModal.show();
}

function clearAddUserForm() {
    addUserName.value = '';
    addUserEmail.value = '';
    addUserPhone.value = '';
    addUserRole.value = '';
}

// Utility functions
function getRoleBadgeClass(role) {
    switch (role) {
        case 'ROLE_ADMIN':
            return 'bg-danger';
        case 'ROLE_MANAGER':
            return 'bg-warning';
        case 'ROLE_USER':
            return 'bg-primary';
        default:
            return 'bg-secondary';
    }
}

function formatRoleDisplay(role) {
    switch (role) {
        case 'ROLE_ADMIN':
            return 'Admin';
        case 'ROLE_MANAGER':
            return 'Manager';
        case 'ROLE_USER':
            return 'User';
        default:
            return role;
    }
}

function isValidRole(role) {
    return VALID_ROLES.includes(role);
}

function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

function showToast(message, type = 'success') {
    const toastElement = document.getElementById('userToast');
    const toastBody = document.getElementById('userToastMessage');
    
    // Remove existing classes
    toastElement.classList.remove('text-bg-success', 'text-bg-danger', 'text-bg-warning');
    
    // Add appropriate class based on type
    switch (type) {
        case 'error':
            toastElement.classList.add('text-bg-danger');
            break;
        case 'warning':
            toastElement.classList.add('text-bg-warning');
            break;
        default:
            toastElement.classList.add('text-bg-success');
    }
    
    toastBody.textContent = message;
    userToast.show();
}

// Global functions for onclick handlers
window.openEditModal = openEditModal;
window.openDeleteModal = openDeleteModal;