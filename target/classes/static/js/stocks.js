// Stocks Module
document.addEventListener('DOMContentLoaded', () => {
    if (!isLoggedIn()) {
        window.location.href = '/index.html';
        return;
    }

    loadStocks();
});

/**
 * Load Stocks Data
 */
async function loadStocks() {
    try {
        const response = await fetch('http://localhost:8080/api/stocks/all');

        if (response.ok) {
            let stocks = await response.json();
            // Sort stocks by price using merge sort (cheapest first)
            stocks = mergeSort(stocks, (a, b) => a.price - b.price);
            displayStocks(stocks);
        } else {
            console.error('Failed to load stocks data');
        }
    } catch (error) {
        console.error('Error loading stocks:', error);
    }
}

/**
 * Display Stocks
 */
function displayStocks(stocks) {
    const tbody = document.querySelector('#stocksTable tbody');
    tbody.innerHTML = '';

    if (!stocks || stocks.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5">No stocks available</td></tr>';
        return;
    }

    stocks.forEach(stock => {
        const row = document.createElement('tr');
        const changeClass = stock.changePercent >= 0 ? 'profit' : 'loss';
        row.innerHTML = `
            <td>${stock.symbol}</td>
            <td>${stock.name}</td>
            <td>${formatCurrency(stock.price)}</td>
            <td class="${changeClass}">${stock.changePercent}%</td>
            <td>
                <button onclick="buyStock(${stock.id})" class="btn-buy">Buy</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

/**
 * Buy Stock
 */
function buyStock(stockId) {
    const quantity = prompt('Enter quantity to buy:');
    if (!quantity || isNaN(quantity) || quantity <= 0) {
        alert('Invalid quantity');
        return;
    }

    const token = localStorage.getItem('token');
    fetch('http://localhost:8080/api/transactions/buy', {
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
            alert('Stock bought successfully!');
            // Refresh data
            loadStocks();
        } else {
            alert('Error: ' + data.message);
        }
    })
    .catch(error => {
        console.error('Error buying stock:', error);
        alert('Error buying stock');
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
 * Merge Sort Implementation
 */
function mergeSort(arr, compareFn) {
    if (arr.length <= 1) {
        return arr;
    }

    const mid = Math.floor(arr.length / 2);
    const left = mergeSort(arr.slice(0, mid), compareFn);
    const right = mergeSort(arr.slice(mid), compareFn);

    return merge(left, right, compareFn);
}

function merge(left, right, compareFn) {
    const result = [];
    let leftIndex = 0;
    let rightIndex = 0;

    while (leftIndex < left.length && rightIndex < right.length) {
        if (compareFn(left[leftIndex], right[rightIndex]) <= 0) {
            result.push(left[leftIndex]);
            leftIndex++;
        } else {
            result.push(right[rightIndex]);
            rightIndex++;
        }
    }

    return result.concat(left.slice(leftIndex)).concat(right.slice(rightIndex));
}