// History Module
document.addEventListener('DOMContentLoaded', () => {
    if (!isLoggedIn()) {
        window.location.href = '/index.html';
        return;
    }

    loadHistory();
});

/**
 * Load Transaction History
 */
async function loadHistory() {
    const token = localStorage.getItem('token');

    try {
        const response = await fetch('http://localhost:8080/api/transactions/list', {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const transactions = await response.json();
            displayHistory(transactions);
        } else {
            console.error('Failed to load history data');
        }
    } catch (error) {
        console.error('Error loading history:', error);
    }
}

/**
 * Display History
 */
function displayHistory(transactions) {
    const tbody = document.querySelector('#historyTable tbody');
    tbody.innerHTML = '';

    if (!transactions || transactions.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7">No transactions found</td></tr>';
        return;
    }

    transactions.forEach(transaction => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${new Date(transaction.timestamp).toLocaleDateString()}</td>
            <td>${transaction.stockSymbol}</td>
            <td>${transaction.type}</td>
            <td>${transaction.quantity}</td>
            <td>${formatCurrency(transaction.price)}</td>
            <td>${formatCurrency(transaction.total)}</td>
            <td>${transaction.status}</td>
        `;
        tbody.appendChild(row);
    });
}

/**
 * Format Currency
 */
function formatCurrency(amount) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(amount);
}