// Admin Dashboard Module
const API_BASE_URL = 'http://localhost:8080/api';

// Initialize Admin Dashboard
document.addEventListener('DOMContentLoaded', () => {
    if (!isLoggedIn()) {
        window.location.href = '/index.html';
        return;
    }

    const user = getCurrentUser();
    if (user.role !== 'ADMIN') {
        alert('You do not have admin access!');
        window.location.href = '/dashboard.html';
        return;
    }

    // Set up form listeners
    document.getElementById('addUserForm').addEventListener('submit', handleAddUser);
    document.getElementById('fluctuateStockForm').addEventListener('submit', handleFluctuateStock);
    document.getElementById('searchUser').addEventListener('input', searchActivityLogs);
    document.getElementById('searchAction').addEventListener('change', searchActivityLogs);

    // Load initial data
    loadAllUsers();
    loadAllStocks();
    loadTransactions();
    loadActivityLogs();
});

/**
 * Switch Between Tabs
 */
function switchTab(tabName, button) {
    // Hide all tabs
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });

    // Remove active class from all buttons
    document.querySelectorAll('.tab-button').forEach(btn => {
        btn.classList.remove('active');
    });

    // Show selected tab and highlight button
    document.getElementById(tabName).classList.add('active');
    button.classList.add('active');

    // Reload data based on tab
    if (tabName === 'users') loadAllUsers();
    if (tabName === 'stocks') loadAllStocks();
    if (tabName === 'trades') loadTransactions();
    if (tabName === 'logs') loadActivityLogs();
}

/**
 * ==================== USER MANAGEMENT ====================
 */

async function handleAddUser(e) {
    e.preventDefault();

    const username = document.getElementById('newUsername').value;
    const password = document.getElementById('newPassword').value;
    const initialBalance = parseFloat(document.getElementById('initialBalance').value) || 1000;
    const role = document.getElementById('userRole').value;

    const messageDiv = document.getElementById('userMessage');

    try {
        const response = await fetch(`${API_BASE_URL}/admin/users/create`, {
            method: 'POST',
            headers: getAuthHeader(),
            body: JSON.stringify({
                username,
                password,
                initialBalance,
                role
            })
        });

        const data = await response.json();

        if (response.ok) {
            showMessage(messageDiv, 'User created successfully!', 'success');
            document.getElementById('addUserForm').reset();
            loadAllUsers();
        } else {
            showMessage(messageDiv, data.message || 'Failed to create user', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showMessage(messageDiv, 'Error creating user', 'error');
    }
}

async function loadAllUsers() {
    const tbody = document.querySelector('#usersTable tbody');

    try {
        const response = await fetch(`${API_BASE_URL}/admin/users/list`, {
            headers: getAuthHeader()
        });

        if (response.ok) {
            const users = await response.json();

            if (!users || users.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" style="text-align: center;">No users found</td></tr>';
                return;
            }

            tbody.innerHTML = users.map(user => {
                const isActive = user.isActive !== undefined ? user.isActive : user.active;
                const userRole = user.role || 'USER';
                const balance = user.balance ? (typeof user.balance === 'number' ? user.balance : user.balance.amount || 0) : 0;
                
                return `
                <tr>
                    <td>${user.id}</td>
                    <td><strong>${user.username}</strong></td>
                    <td>${formatCurrency(balance)}</td>
                    <td>
                        <span style="background: ${userRole === 'ADMIN' ? '#FF1493' : '#4CAF50'}; 
                                     color: white; padding: 5px 10px; border-radius: 5px; font-weight: bold;">
                            ${userRole}
                        </span>
                    </td>
                    <td>
                        <span style="background: ${isActive ? '#4CAF50' : '#F44336'}; 
                                     color: white; padding: 5px 10px; border-radius: 5px;">
                            ${isActive ? 'Active' : 'Inactive'}
                        </span>
                    </td>
                    <td>
                        <div class="action-buttons" style="display: flex; gap: 8px;">
                            <button class="btn-edit" onclick="editUser(${user.id})" style="padding: 8px 12px; background: #2196F3; color: white; border: none; border-radius: 5px; cursor: pointer;">
                                <i class="fas fa-edit"></i> Edit
                            </button>
                            <button class="btn-delete" onclick="deleteUser(${user.id})" style="padding: 8px 12px; background: #F44336; color: white; border: none; border-radius: 5px; cursor: pointer;">
                                <i class="fas fa-trash"></i> Delete
                            </button>
                        </div>
                    </td>
                </tr>
            `;
            }).join('');
        } else {
            console.error('Error response:', response.status);
            tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; color: red;">Failed to load users (Status: ' + response.status + ')</td></tr>';
        }
    } catch (error) {
        console.error('Error loading users:', error);
        tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; color: red;">Error loading users: ' + error.message + '</td></tr>';
    }
}

async function deleteUser(userId) {
    if (!confirm('Are you sure you want to delete this user?')) return;

    try {
        const response = await fetch(`${API_BASE_URL}/admin/users/delete/${userId}`, {
            method: 'DELETE',
            headers: getAuthHeader()
        });

        if (response.ok) {
            loadAllUsers();
            alert('User deleted successfully');
        } else {
            alert('Failed to delete user');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error deleting user');
    }
}

/**
 * ==================== STOCK CONTROL ====================
 */

async function loadAllStocks() {
    const token = localStorage.getItem('token');
    const tbody = document.querySelector('#stocksTable tbody');
    const selectElement = document.getElementById('stockSelect');

    try {
        const response = await fetch(`${API_BASE_URL}/stocks/all`, {
            headers: getAuthHeader()
        });

        if (response.ok) {
            const stocks = await response.json();

            // Update stock select dropdown
            selectElement.innerHTML = '<option value="">-- Choose Stock --</option>' +
                stocks.map(stock => `<option value="${stock.id}">${stock.symbol} - ${stock.name}</option>`).join('');

            // Update stocks table
            tbody.innerHTML = stocks.map(stock => `
                <tr>
                    <td><strong>${stock.symbol}</strong></td>
                    <td>${stock.name}</td>
                    <td>${formatCurrency(stock.price)}</td>
                    <td>
                        <span style="color: ${stock.changePercent >= 0 ? '#4CAF50' : '#F44336'}; font-weight: bold;">
                            ${stock.changePercent >= 0 ? '+' : ''}${stock.changePercent.toFixed(2)}%
                        </span>
                    </td>
                    <td>
                        <span style="background: ${stock.isSuspended ? '#F44336' : '#4CAF50'}; 
                                     color: white; padding: 5px 10px; border-radius: 5px;">
                            ${stock.isSuspended ? 'Suspended' : 'Active'}
                        </span>
                    </td>
                    <td>
                        <button class="btn-edit" onclick="toggleStockSuspension(${stock.id}, ${stock.isSuspended})">
                            <i class="fas fa-${stock.isSuspended ? 'play' : 'pause'}"></i>
                            ${stock.isSuspended ? 'Resume' : 'Suspend'}
                        </button>
                    </td>
                </tr>
            `).join('');
        }
    } catch (error) {
        console.error('Error loading stocks:', error);
        tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; color: red;">Error loading stocks</td></tr>';
    }
}

async function handleFluctuateStock(e) {
    e.preventDefault();

    const stockId = document.getElementById('stockSelect').value;
    const newPrice = parseFloat(document.getElementById('newPrice').value);
    const reason = document.getElementById('priceReason').value;
    const messageDiv = document.getElementById('stockMessage');

    if (!stockId) {
        showMessage(messageDiv, 'Please select a stock', 'error');
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/admin/stocks/fluctuate`, {
            method: 'POST',
            headers: getAuthHeader(),
            body: JSON.stringify({
                stockId: parseInt(stockId),
                newPrice,
                reason
            })
        });

        const data = await response.json();

        if (response.ok) {
            showMessage(messageDiv, `Stock price updated to ${formatCurrency(newPrice)}!`, 'success');
            document.getElementById('fluctuateStockForm').reset();
            loadAllStocks();
        } else {
            showMessage(messageDiv, data.message || 'Failed to update stock price', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showMessage(messageDiv, 'Error updating stock price', 'error');
    }
}

async function toggleStockSuspension(stockId, currentStatus) {
    try {
        const response = await fetch(`${API_BASE_URL}/admin/stocks/suspend`, {
            method: 'POST',
            headers: getAuthHeader(),
            body: JSON.stringify({
                stockId,
                suspend: !currentStatus
            })
        });

        if (response.ok) {
            loadAllStocks();
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

/**
 * ==================== TRADE ROLLBACK ====================
 */

async function loadTransactions() {
    const token = localStorage.getItem('token');
    const tbody = document.querySelector('#transactionsTable tbody');

    try {
        const response = await fetch(`${API_BASE_URL}/admin/transactions/all`, {
            headers: getAuthHeader()
        });

        if (response.ok) {
            const transactions = await response.json();

            if (transactions.length === 0) {
                tbody.innerHTML = '<tr><td colspan="9" style="text-align: center;">No transactions found</td></tr>';
                return;
            }

            tbody.innerHTML = transactions.map(t => `
                <tr>
                    <td>${t.id}</td>
                    <td>${t.username}</td>
                    <td><strong>${t.stockSymbol}</strong></td>
                    <td>
                        <span style="background: ${t.type === 'BUY' ? '#4CAF50' : '#F44336'}; 
                                     color: white; padding: 3px 8px; border-radius: 4px; font-weight: bold;">
                            ${t.type}
                        </span>
                    </td>
                    <td>${t.quantity}</td>
                    <td>${formatCurrency(t.price)}</td>
                    <td>${formatDate(t.createdAt)}</td>
                    <td>
                        <span style="background: ${getStatusColor(t.status)}; color: white; padding: 3px 8px; border-radius: 4px; font-size: 0.85rem;">
                            ${t.status}
                        </span>
                    </td>
                    <td>
                        ${t.type === 'SELL' && t.status === 'COMPLETED' ? `
                            <button class="btn-rollback" onclick="rollbackTransaction(${t.id}, '${t.username}')">
                                <i class="fas fa-undo"></i> Rollback
                            </button>
                        ` : '<span style="color: var(--text-light);">N/A</span>'}
                    </td>
                </tr>
            `).join('');
        }
    } catch (error) {
        console.error('Error loading transactions:', error);
        tbody.innerHTML = '<tr><td colspan="9" style="text-align: center; color: red;">Error loading transactions</td></tr>';
    }
}

async function rollbackTransaction(transactionId, username) {
    if (!confirm(`Rollback this transaction for ${username}? They will receive compensation.`)) return;

    try {
        const response = await fetch(`${API_BASE_URL}/admin/transactions/rollback/${transactionId}`, {
            method: 'POST',
            headers: getAuthHeader()
        });

        if (response.ok) {
            alert('Transaction rolled back successfully!');
            loadTransactions();
        } else {
            alert('Failed to rollback transaction');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error rolling back transaction');
    }
}

/**
 * ==================== ACTIVITY LOGS ====================
 */

async function loadActivityLogs() {
    const token = localStorage.getItem('token');
    const tbody = document.querySelector('#activityTable tbody');

    try {
        const response = await fetch(`${API_BASE_URL}/admin/activities/all`, {
            headers: getAuthHeader()
        });

        if (response.ok) {
            const logs = await response.json();

            if (logs.length === 0) {
                tbody.innerHTML = '<tr><td colspan="4" style="text-align: center;">No activities logged</td></tr>';
                return;
            }

            tbody.innerHTML = logs.slice(0, 50).map(log => `
                <tr>
                    <td><strong>${log.username}</strong></td>
                    <td>
                        <span style="background: var(--primary-yellow); color: var(--text-dark); 
                                     padding: 4px 10px; border-radius: 5px; font-weight: 600; font-size: 0.9rem;">
                            ${log.actionType}
                        </span>
                    </td>
                    <td>${log.details}</td>
                    <td>${formatDate(log.loggedAt)}</td>
                </tr>
            `).join('');
        }
    } catch (error) {
        console.error('Error loading activity logs:', error);
        tbody.innerHTML = '<tr><td colspan="4" style="text-align: center; color: red;">Error loading logs</td></tr>';
    }
}

async function searchActivityLogs() {
    const username = document.getElementById('searchUser').value;
    const actionType = document.getElementById('searchAction').value;
    const tbody = document.querySelector('#activityTable tbody');

    try {
        let url = `${API_BASE_URL}/admin/activities/search?`;
        if (username) url += `username=${username}&`;
        if (actionType) url += `actionType=${actionType}`;

        const response = await fetch(url, {
            headers: getAuthHeader()
        });

        if (response.ok) {
            const logs = await response.json();

            if (logs.length === 0) {
                tbody.innerHTML = '<tr><td colspan="4" style="text-align: center;">No matching activities</td></tr>';
                return;
            }

            tbody.innerHTML = logs.map(log => `
                <tr>
                    <td><strong>${log.username}</strong></td>
                    <td>
                        <span style="background: var(--primary-yellow); color: var(--text-dark); 
                                     padding: 4px 10px; border-radius: 5px; font-weight: 600; font-size: 0.9rem;">
                            ${log.actionType}
                        </span>
                    </td>
                    <td>${log.details}</td>
                    <td>${formatDate(log.loggedAt)}</td>
                </tr>
            `).join('');
        }
    } catch (error) {
        console.error('Error searching logs:', error);
    }
}

/**
 * ==================== UTILITY FUNCTIONS ====================
 */

function showMessage(element, message, type) {
    const className = type === 'success' ? 'success-message' : 'error-message';
    element.innerHTML = `<div class="${className}">${message}</div>`;
    
    setTimeout(() => {
        element.innerHTML = '';
    }, 5000);
}

function formatCurrency(value) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(value || 0);
}

function formatDate(dateString) {
    const options = { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
    return new Date(dateString).toLocaleDateString('en-US', options);
}

function getStatusColor(status) {
    switch(status) {
        case 'COMPLETED': return '#4CAF50';
        case 'PENDING': return '#FF9800';
        case 'FAILED': return '#F44336';
        case 'CANCELLED': return '#9E9E9E';
        default: return '#2196F3';
    }
}

function editUser(userId) {
    // Get user data from the table
    const tableBody = document.querySelector('#usersTable tbody');
    const rows = tableBody.querySelectorAll('tr');
    let userRow = null;
    let rowData = null;

    // Find the row for this user
    for (let row of rows) {
        const cells = row.querySelectorAll('td');
        if (cells[0] && parseInt(cells[0].textContent) === userId) {
            userRow = row;
            const username = cells[1].textContent.trim();
            const balanceText = cells[2].textContent.replace(/[$,]/g, '');
            const balance = parseFloat(balanceText);
            const roleCell = cells[3];
            const role = roleCell.textContent.trim();
            const statusCell = cells[4];
            const status = statusCell.textContent.trim();
            const isActive = status === 'Active';
            
            rowData = { username, balance, role, isActive };
            break;
        }
    }

    if (!rowData) {
        alert('User not found in table');
        return;
    }

    const { username, balance, role, isActive } = rowData;

    // Create edit modal
    const modal = document.createElement('div');
    modal.id = 'editUserModal';
    modal.style.cssText = `
        position: fixed; top: 0; left: 0; width: 100%; height: 100%;
        background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center;
        z-index: 1000;
    `;

    modal.innerHTML = `
        <div style="background: white; padding: 30px; border-radius: 15px; width: 400px; max-width: 90%;">
            <h3 style="margin-bottom: 20px; color: var(--text-dark);">Edit User</h3>
            <form id="editUserForm">
                <div style="margin-bottom: 15px;">
                    <label style="display: block; margin-bottom: 5px; font-weight: bold;">Username</label>
                    <input type="text" id="editUsername" value="${username}" required style="width: 100%; padding: 10px; border: 2px solid var(--border-light); border-radius: 8px; box-sizing: border-box;">
                </div>
                <div style="margin-bottom: 15px;">
                    <label style="display: block; margin-bottom: 5px; font-weight: bold;">New Password (leave empty to keep current)</label>
                    <input type="password" id="editPassword" placeholder="Enter new password" style="width: 100%; padding: 10px; border: 2px solid var(--border-light); border-radius: 8px; box-sizing: border-box;">
                </div>
                <div style="margin-bottom: 15px;">
                    <label style="display: block; margin-bottom: 5px; font-weight: bold;">Balance ($)</label>
                    <input type="number" id="editBalance" value="${balance}" min="0" step="0.01" required style="width: 100%; padding: 10px; border: 2px solid var(--border-light); border-radius: 8px; box-sizing: border-box;">
                </div>
                <div style="margin-bottom: 15px;">
                    <label style="display: block; margin-bottom: 5px; font-weight: bold;">Role</label>
                    <select id="editRole" style="width: 100%; padding: 10px; border: 2px solid var(--border-light); border-radius: 8px; box-sizing: border-box;">
                        <option value="USER" ${role === 'USER' ? 'selected' : ''}>User</option>
                        <option value="ADMIN" ${role === 'ADMIN' ? 'selected' : ''}>Admin</option>
                    </select>
                </div>
                <div style="margin-bottom: 20px;">
                    <label style="display: block; margin-bottom: 5px; font-weight: bold;">
                        <input type="checkbox" id="editIsActive" ${isActive ? 'checked' : ''}> Active
                    </label>
                </div>
                <div style="display: flex; gap: 10px;">
                    <button type="submit" style="flex: 1; padding: 12px; background: #4CAF50; color: white; border: none; border-radius: 8px; cursor: pointer; font-weight: bold;">Save Changes</button>
                    <button type="button" onclick="document.getElementById('editUserModal').remove()" style="flex: 1; padding: 12px; background: #F44336; color: white; border: none; border-radius: 8px; cursor: pointer; font-weight: bold;">Cancel</button>
                </div>
            </form>
        </div>
    `;

    document.body.appendChild(modal);

    // Handle form submission
    document.getElementById('editUserForm').addEventListener('submit', async (e) => {
        e.preventDefault();

        const editUsername = document.getElementById('editUsername').value;
        const editPassword = document.getElementById('editPassword').value;
        const editBalance = parseFloat(document.getElementById('editBalance').value);
        const editRole = document.getElementById('editRole').value;
        const editIsActive = document.getElementById('editIsActive').checked;

        try {
            const response = await fetch(`${API_BASE_URL}/admin/users/edit/${userId}`, {
                method: 'POST',
                headers: getAuthHeader(),
                body: JSON.stringify({
                    username: editUsername,
                    password: editPassword,
                    balance: editBalance,
                    role: editRole,
                    isActive: editIsActive
                })
            });

            const data = await response.json();

            if (response.ok) {
                alert('User updated successfully!');
                document.getElementById('editUserModal').remove();
                loadAllUsers();
            } else {
                alert('Failed to update user: ' + (data.message || 'Unknown error'));
            }
        } catch (error) {
            console.error('Error:', error);
            alert('Error updating user: ' + error.message);
        }
    });
}
