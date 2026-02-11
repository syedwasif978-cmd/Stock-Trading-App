// Dashboard Module - Chart.js Integration
let portfolioChart, performanceChart, activityChart, balanceChart;

// Initialize Dashboard
document.addEventListener('DOMContentLoaded', () => {
    if (!isLoggedIn()) {
        window.location.href = '/index.html';
        return;
    }

    const user = getCurrentUser();
    document.getElementById('userName').textContent = user.username;

    // Show admin link if user is admin
    if (user.role === 'ADMIN') {
        const adminLink = document.getElementById('adminLink');
        if (adminLink) adminLink.style.display = 'inline-block';
    }

    loadDashboardData();
    initializeCharts();
    loadRecentTransactions();

    // Refresh data every 5 seconds for real-time updates
    setInterval(loadDashboardData, 5000);
    setInterval(loadRecentTransactions, 5000);
});

/**
 * Load Dashboard Data
 */
async function loadDashboardData() {
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
            
            // Update stat cards
            document.getElementById('totalBalance').textContent = formatCurrency(data.totalBalance);
            document.getElementById('portfolioValue').textContent = formatCurrency(data.portfolioValue);
            document.getElementById('profitLoss').textContent = formatCurrency(data.profitLoss);
            document.getElementById('totalTransactions').textContent = data.totalTransactions;

            // Update charts with new data
            updateCharts(data);
        } else {
            console.error('Failed to load dashboard data');
        }
    } catch (error) {
        console.error('Error loading dashboard data:', error);
    }
}

/**
 * Initialize Chart.js Charts
 */
function initializeCharts() {
    // Portfolio Distribution Chart (Pie)
    const portfolioCtx = document.getElementById('portfolioChart').getContext('2d');
    portfolioChart = new Chart(portfolioCtx, {
        type: 'doughnut',
        data: {
            labels: [],
            datasets: [{
                data: [],
                backgroundColor: [
                    '#FFD700', '#FFC700', '#FFB700', '#FFA700',
                    '#FF9700', '#FF8700', '#FF7700', '#FF6700'
                ],
                borderColor: '#FFFFFF',
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        font: { size: 12, weight: 'bold' },
                        padding: 15,
                        usePointStyle: true
                    }
                }
            }
        }
    });

    // Stock Performance Chart (Line)
    const performanceCtx = document.getElementById('performanceChart').getContext('2d');
    performanceChart = new Chart(performanceCtx, {
        type: 'line',
        data: {
            labels: [],
            datasets: [{
                label: 'Stock Price',
                data: [],
                borderColor: '#FFD700',
                backgroundColor: 'rgba(255, 215, 0, 0.1)',
                borderWidth: 3,
                fill: true,
                tension: 0.4,
                pointRadius: 6,
                pointBackgroundColor: '#FFD700',
                pointBorderColor: '#FFC700',
                pointBorderWidth: 2,
                pointHoverRadius: 8
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    labels: {
                        font: { size: 12, weight: 'bold' }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: false,
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)'
                    }
                }
            }
        }
    });

    // Trading Activity Chart (Bar)
    const activityCtx = document.getElementById('activityChart').getContext('2d');
    activityChart = new Chart(activityCtx, {
        type: 'bar',
        data: {
            labels: [],
            datasets: [{
                label: 'Buy Orders',
                data: [],
                backgroundColor: '#4CAF50',
                borderRadius: 6,
                borderSkipped: false
            },
            {
                label: 'Sell Orders',
                data: [],
                backgroundColor: '#F44336',
                borderRadius: 6,
                borderSkipped: false
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    labels: {
                        font: { size: 12, weight: 'bold' }
                    }
                }
            },
            scales: {
                x: {
                    stacked: false
                },
                y: {
                    beginAtZero: true,
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)'
                    }
                }
            }
        }
    });

    // Balance History Chart (Line with Area)
    const balanceCtx = document.getElementById('balanceChart').getContext('2d');
    balanceChart = new Chart(balanceCtx, {
        type: 'line',
        data: {
            labels: [],
            datasets: [{
                label: 'Account Balance',
                data: [],
                borderColor: '#FFD700',
                backgroundColor: 'rgba(255, 215, 0, 0.2)',
                borderWidth: 3,
                fill: true,
                tension: 0.4,
                pointRadius: 5,
                pointBackgroundColor: '#FFD700',
                pointBorderColor: '#FFC700',
                pointBorderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    labels: {
                        font: { size: 12, weight: 'bold' }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: false,
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)'
                    }
                }
            }
        }
    });
}

function updateCharts(data) {
    // Portfolio Distribution
    if (data.portfolio && data.portfolio.length > 0) {
        portfolioChart.data.labels = data.portfolio.map(p => p.symbol);
        portfolioChart.data.datasets[0].data = data.portfolio.map(p => p.totalValue);
        portfolioChart.update();
    }

    // Stock Performance
    if (data.stockPrices && data.stockPrices.length > 0) {
        performanceChart.data.labels = data.stockPrices.map(sp => sp.symbol);
        performanceChart.data.datasets[0].data = data.stockPrices.map(sp => sp.price);
        performanceChart.update();
    }

    // Trading Activity
    if (data.tradingActivity) {
        activityChart.data.labels = data.tradingActivity.dates || [];
        activityChart.data.datasets[0].data = data.tradingActivity.buyOrders || [];
        activityChart.data.datasets[1].data = data.tradingActivity.sellOrders || [];
        activityChart.update();
    }

    // Balance History
    if (data.balanceHistory && data.balanceHistory.length > 0) {
        balanceChart.data.labels = data.balanceHistory.map(bh => bh.date);
        balanceChart.data.datasets[0].data = data.balanceHistory.map(bh => bh.balance);
        balanceChart.update();
    }
}

/**
 * Load Recent Transactions
 */
async function loadRecentTransactions() {
    const token = localStorage.getItem('token');
    const tbody = document.querySelector('#recentTransactions tbody');

    try {
        const response = await fetch('http://localhost:8080/api/transactions/recent', {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const transactions = await response.json();
            
            if (transactions.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7" style="text-align: center;">No transactions yet</td></tr>';
                return;
            }

            tbody.innerHTML = transactions.slice(0, 10).map(t => `
                <tr>
                    <td>${formatDate(t.createdAt)}</td>
                    <td><strong>${t.stockSymbol}</strong></td>
                    <td>
                        <span style="background: ${t.type === 'BUY' ? '#4CAF50' : '#F44336'}; 
                                     color: white; padding: 5px 10px; border-radius: 5px; font-weight: bold;">
                            ${t.type}
                        </span>
                    </td>
                    <td>${t.quantity}</td>
                    <td>${formatCurrency(t.price)}</td>
                    <td>${formatCurrency(t.quantity * t.price)}</td>
                    <td>
                        <span style="background: ${getStatusColor(t.status)}; 
                                     color: white; padding: 5px 10px; border-radius: 5px; font-size: 0.9rem;">
                            ${t.status}
                        </span>
                    </td>
                </tr>
            `).join('');
        }
    } catch (error) {
        console.error('Error loading transactions:', error);
        tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; color: red;">Error loading transactions</td></tr>';
    }
}

/**
 * Utility Functions
 */

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
