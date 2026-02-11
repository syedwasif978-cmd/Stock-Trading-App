# Stock Trading App - Features Report

## User Management (CREATE, READ, UPDATE, DELETE)
- Create new users with initial balance and role assignment (admin)
- View all users and user profiles
- Update user balance and active status
- Delete users (admin)

## Transaction Management (CREATE, READ, UPDATE, DELETE)
- Create BUY and SELL transactions
- View transaction history (personal and all - admin)
- Update transaction status (PENDING → COMPLETED)
- Rollback/cancel sold transactions (admin refunds user)

## Stock Management (READ, UPDATE)
- View all stocks with prices and change percentages
- Search stocks by symbol or ID
- Update stock prices (admin)
- Suspend/resume trading on stocks (admin)

## Portfolio Management (READ, UPDATE)
- View personal portfolio holdings
- View portfolio value and profit/loss
- View portfolio distribution by stock
- Auto-update on buy/sell transactions

## User Dashboard
- View balance and portfolio summary
- See profit/loss calculations
- View 4 interactive charts (composition, performance, trading activity, balance history)
- View recent transactions

## Admin Dashboard
- **Tab 1:** Create users, manage all users, delete users
- **Tab 2:** View all stocks, fluctuate prices, suspend/resume trading
- **Tab 3:** View all transactions, rollback sold trades
- **Tab 4:** View activity logs with search by username and action type

## Authentication & Security
- User login with username/password
- Admin login with role verification
- Token-based authentication (Bearer tokens)
- Password hashing
- Role-based access control (ADMIN/USER)
- CORS configuration
- SQL injection prevention

## Reporting & Activity Logs
- User activity audit logs (all actions tracked)
- View action types (LOGIN, BUY, SELL, ADMIN_ACTION)
- Timestamp on all logs
- Search logs by user or action type

## Frontend Features
- Light white/yellow responsive theme
- Smooth animations
- 4 real-time charts on dashboard
- Form validation and error messages
- Tab-based admin navigation
- Mobile-friendly design

## Technical Architecture
- Spring Boot REST API (20+ endpoints)
- MySQL database backend
- JDBC for data access
- HTML/CSS/JavaScript frontend
- Chart.js for data visualization
- Maven build system

