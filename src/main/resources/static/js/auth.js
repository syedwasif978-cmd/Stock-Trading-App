// Stock Trading Platform - Authentication Module
const API_BASE_URL = 'http://localhost:8080/api';

// DOM Elements
const loginForm = document.getElementById('loginForm');
const adminLoginForm = document.getElementById('adminLoginForm');
const adminLoginPanel = document.getElementById('adminLoginPanel');
const registerPanel = document.getElementById('registerPanel');
const registerForm = document.getElementById('registerForm');

// Event Listeners
if (loginForm) {
    loginForm.addEventListener('submit', handleUserLogin);
}

if (adminLoginForm) {
    adminLoginForm.addEventListener('submit', handleAdminLogin);
}

if (registerForm) {
    registerForm.addEventListener('submit', handleRegister);
}

/**
 * Handle User Login
 */
async function handleUserLogin(e) {
    e.preventDefault();
    
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    
    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (response.ok) {
            // Store user info and token
            localStorage.setItem('user', JSON.stringify(data.user));
            localStorage.setItem('token', data.token);
            
            // Redirect to dashboard
            window.location.href = '/dashboard.html';
        } else {
            showError(data.message || 'Login failed');
        }
    } catch (error) {
        console.error('Login error:', error);
        showError('Error during login. Please try again.');
    }
}

/**
 * Handle Admin Login
 */
async function handleAdminLogin(e) {
    e.preventDefault();
    
    const username = document.getElementById('adminUsername').value;
    const password = document.getElementById('adminPassword').value;
    
    try {
        const response = await fetch(`${API_BASE_URL}/auth/admin-login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (response.ok && data.user.role === 'ADMIN') {
            // Store admin info and token
            localStorage.setItem('user', JSON.stringify(data.user));
            localStorage.setItem('token', data.token);
            
            // Redirect to admin dashboard
            window.location.href = '/admin-dashboard.html';
        } else {
            showError(data.message || 'Admin login failed');
        }
    } catch (error) {
        console.error('Admin login error:', error);
        showError('Error during admin login. Please try again.');
    }
}

/**
 * Switch to Admin Login Panel
 */
function switchToAdmin(e) {
    e.preventDefault();
    document.querySelector('.login-card').style.display = 'none';
    adminLoginPanel.style.display = 'block';
}

/**
 * Switch Back to User Login
 */
function switchToUser(e) {
    e.preventDefault();
    document.querySelector('.login-card').style.display = 'block';
    adminLoginPanel.style.display = 'none';
    if (registerPanel) registerPanel.style.display = 'none';
}

/**
 * Switch to Register (Placeholder)
 */
function switchToRegister(e) {
    e.preventDefault();
    // Show the register panel and hide the main login card and admin panel
    const mainCard = document.querySelector('.login-card');
    if (mainCard) mainCard.style.display = 'none';
    if (adminLoginPanel) adminLoginPanel.style.display = 'none';
    if (registerPanel) registerPanel.style.display = 'block';
}

/**
 * Handle User Registration
 */
async function handleRegister(e) {
    e.preventDefault();

    const username = document.getElementById('regUsername').value.trim();
    const password = document.getElementById('regPassword').value;
    const confirm = document.getElementById('regConfirmPassword').value;

    if (!username || !password) {
        showError('Username and password are required');
        return;
    }

    if (password !== confirm) {
        showError('Passwords do not match');
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (response.ok) {
            showSuccess('Registration successful. You can now log in.');
            // Return to login view
            setTimeout(() => {
                switchToUser(new Event('click'));
            }, 800);
        } else {
            showError(data.message || 'Registration failed');
        }
    } catch (err) {
        console.error('Registration error:', err);
        showError('Error during registration. Please try again.');
    }
}

/**
 * Show Error Message
 */
function showError(message) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-error';
    alertDiv.textContent = message;
    
    const form = document.querySelector('form');
    form.insertBefore(alertDiv, form.firstChild);
    
    setTimeout(() => alertDiv.remove(), 5000);
}

/**
 * Show Success Message
 */
function showSuccess(message) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-success';
    alertDiv.textContent = message;
    
    const form = document.querySelector('form');
    form.insertBefore(alertDiv, form.firstChild);
    
    setTimeout(() => alertDiv.remove(), 5000);
}

/**
 * Get Auth Header with Token
 */
function getAuthHeader() {
    const token = localStorage.getItem('token');
    return {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
    };
}

/**
 * Check if User is Logged In
 */
function isLoggedIn() {
    return localStorage.getItem('token') !== null;
}

/**
 * Get Current User
 */
function getCurrentUser() {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
}

/**
 * Logout
 */
function logout() {
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    window.location.href = '/index.html';
}
