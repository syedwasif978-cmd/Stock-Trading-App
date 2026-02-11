// Portfolio Module
document.addEventListener('DOMContentLoaded', () => {
    if (!isLoggedIn()) {
        window.location.href = '/index.html';
        return;
    }

    loadPortfolio();
});

/**
 * Load Portfolio Data
 */
async function loadPortfolio() {
    const token = localStorage.getItem('token');

    try {
        const response = await fetch('http://localhost:8080/api/dashboard/summary', {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const data = await response.json();
            displayPortfolio(data.portfolio);
        } else {
            console.error('Failed to load portfolio data');
        }
    } catch (error) {
        console.error('Error loading portfolio:', error);
    }
}

/**
 * Display Portfolio
 */
function displayPortfolio(portfolio) {
    const tbody = document.querySelector('#portfolioTable tbody');
    tbody.innerHTML = '';

    if (!portfolio || portfolio.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6">No holdings in portfolio</td></tr>';
        return;
    }

    portfolio.forEach(item => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${item.symbol}</td>
            <td>${item.quantity}</td>
            <td>${formatCurrency(item.averagePrice)}</td>
            <td>${formatCurrency(item.currentPrice)}</td>
            <td>${formatCurrency(item.totalValue)}</td>
            <td class="${item.profitLoss >= 0 ? 'profit' : 'loss'}">${formatCurrency(item.profitLoss)}</td>
            <td><button onclick="sellStock('${item.symbol}', ${item.stockId}, ${item.quantity})" class="btn-sell">Sell</button></td>
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

/**
 * Sell Stock
 */
function sellStock(symbol, stockId, maxQuantity) {
    const quantity = prompt(`Enter quantity to sell (max ${maxQuantity}):`);
    if (!quantity || isNaN(quantity) || quantity <= 0 || quantity > maxQuantity) {
        alert('Invalid quantity');
        return;
    }

    const token = localStorage.getItem('token');
    fetch('http://localhost:8080/api/transactions/sell', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ stockId: parseInt(stockId), quantity: parseInt(quantity) })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('Stock sold successfully!');
            // Refresh data
            loadPortfolio();
        } else {
            alert('Error: ' + data.message);
        }
    })
    .catch(error => {
        console.error('Error selling stock:', error);
        alert('Error selling stock');
    });
}