# ALGORITHMS & MATHEMATICS DOCUMENTATION
## Stock Trading Application - Viva Demonstration Guide

---

## Table of Contents
1. [Binary Search Algorithm](#1-binary-search-algorithm)
2. [Linear Search Algorithm](#2-linear-search-algorithm)
3. [Insertion Sort Algorithm](#3-insertion-sort-algorithm)
4. [Merge Sort Algorithm](#4-merge-sort-algorithm-note)
5. [Price Fluctuation Mathematics](#5-price-fluctuation-mathematics)
6. [API Routes Reference](#6-api-routes-reference)
7. [Quick Demo Guide](#7-quick-demo-guide-for-viva)

---

## 1. Binary Search Algorithm 

### 📍 Location in Project
- **File**: `src/main/java/com/stockapp/algorithms/BinarySearch.java`
- **Used in**: `src/main/java/com/stockapp/dao/UserDAO.java`
- **Method**: `getUserByIdUsingBinarySearch(int userId)`

### 🎯 What It Does
Binary search finds a user by their ID in a sorted list by repeatedly dividing the search interval in half.

### ⏱️ Time Complexity
- **Best Case**: O(1) - found immediately at middle
- **Average/Worst Case**: O(log n) - logarithmic time

### 📊 Example
Searching for User ID=5 in a list of 100 users:
- **Linear Search**: ~50 comparisons on average
- **Binary Search**: ~7 comparisons maximum
- **Efficiency**: ~87% faster!

### 💻 Code Implementation
```java
public static User searchUserById(List<User> sortedUsers, int targetId) {
    int left = 0;
    int right = sortedUsers.size() - 1;
    
    while (left <= right) {
        int mid = left + (right - left) / 2;
        User midUser = sortedUsers.get(mid);
        
        if (midUser.getId() == targetId) {
            return midUser; // Found!
        } else if (midUser.getId() < targetId) {
            left = mid + 1; // Search right half
        } else {
            right = mid - 1; // Search left half
        }
    }
    return null; // Not found
}
```

### 🔗 API Route to Test
```http
GET http://localhost:8080/api/admin/users/search/{id}
```

**Note**: Demonstrates binary search in admin user management. Console logs will show the search iterations.

### 🗣️ Viva Explanation
> "Binary search is used in the UserDAO to find users by ID efficiently. It requires the list to be sorted first. For 100 users, it only needs 7 comparisons instead of 50, making it much faster than linear search. The algorithm works by checking the middle element and eliminating half of the remaining elements in each iteration. This is perfect for admin user management where you need to quickly find specific users by their ID."

---

## 2. Linear Search Algorithm

### 📍 Location in Project
- **File**: `src/main/java/com/stockapp/algorithms/LinearSearch.java`
- **Used in**: `src/main/java/com/stockapp/dao/StockDAO.java`
- **Method**: `getStockBySymbolUsingLinearSearch(String symbol)`

### 🎯 What It Does
Linear search checks each stock sequentially until it finds one matching the ticker symbol (e.g., "AAPL").

### ⏱️ Time Complexity
- **Best Case**: O(1) - found at first position
- **Average Case**: O(n/2) - found in middle
- **Worst Case**: O(n) - found at end or not found

### 📊 Why Use Linear Search Here?
Stock symbols are **strings** (not numbers), and the list might not be sorted alphabetically. Linear search is perfect because:
1. Works on unsorted lists
2. String comparison is needed (case-insensitive)
3. Simple and straightforward

### 💻 Code Implementation
```java
public static Stock searchStockBySymbol(List<Stock> stocks, String targetSymbol) {
    for (int i = 0; i < stocks.size(); i++) {
        Stock currentStock = stocks.get(i);
        
        // Case-insensitive string comparison
        if (currentStock.getSymbol().equalsIgnoreCase(targetSymbol)) {
            return currentStock; // Found!
        }
    }
    return null; // Not found after checking all stocks
}
```

### 🔗 API Route to Test
```http
GET http://localhost:8080/api/stocks/symbol/AAPL?useAlgorithm=true
```

**Query Parameter**: `useAlgorithm=true` triggers linear search instead of SQL query

### 📝 Real-World Use Cases
1. Searching for "AAPL" in stock list
2. Finding a transaction by ID in user history
3. Searching by company name (partial match)
4. Counting stocks above a certain price

### 🗣️ Viva Explanation
> "Linear search is used to find stocks by their symbol like 'AAPL'. Since stock symbols are strings and might not be alphabetically sorted, we check each stock one by one. For a list of 50 stocks, it checks an average of 25 stocks. While slower than binary search, it's simpler and works on unsorted data."

---

## 3. Insertion Sort Algorithm

### 📍 Location in Project
- **File**: `src/main/java/com/stockapp/algorithms/InsertionSort.java`
- **Used in**: `src/main/java/com/stockapp/dao/StockDAO.java`
- **Methods**: 
  - `getAllStocksSortedByPrice(boolean ascending)`
  - `getAllStocksSortedBySymbol()`
  - `getStocksSortedByChange(boolean showGainers)`

### 🎯 What It Does
Insertion sort arranges stocks in order (by price, symbol, or change %) by inserting each element into its correct position, similar to sorting playing cards in your hand.

### ⏱️ Time Complexity
- **Best Case**: O(n) - already sorted
- **Average Case**: O(n²)
- **Worst Case**: O(n²)

### ✅ Why Use Insertion Sort?
Perfect for **small datasets** (10-50 stocks):
- ✔️ Simple to understand and implement
- ✔️ Efficient for small lists
- ✔️ **Stable sort** (maintains order of equal elements)
- ✔️ **Adaptive** (fast for nearly sorted data)

### 💻 Code Implementation
```java
public static void sortStocksByPrice(List<Stock> stocks, boolean ascending) {
    // Start from second element
    for (int i = 1; i < stocks.size(); i++) {
        Stock key = stocks.get(i);
        int j = i - 1;
        
        // Move elements greater than key one position ahead
        while (j >= 0 && stocks.get(j).getPrice().compareTo(key.getPrice()) > 0) {
            stocks.set(j + 1, stocks.get(j));
            j = j - 1;
        }
        
        // Insert key at correct position
        stocks.set(j + 1, key);
    }
}
```

### 🔗 API Routes to Test

**Sort by Price (Ascending)**:
```http
GET http://localhost:8080/api/stocks/all?sort=price&order=asc
```

**Sort by Price (Descending)**:
```http
GET http://localhost:8080/api/stocks/all?sort=price&order=desc
```

**Sort Alphabetically by Symbol**:
```http
GET http://localhost:8080/api/stocks/all?sort=symbol
```

**Top Gainers (Highest % change)**:
```http
GET http://localhost:8080/api/stocks/all?sort=change&order=desc
```

**Top Losers (Lowest % change)**:
```http
GET http://localhost:8080/api/stocks/all?sort=change&order=asc
```

### 📊 Sorting Options
1. **By Price**: Show cheapest or most expensive stocks first
2. **By Symbol**: Alphabetical order (AAPL, AMZN, GOOG, MSFT, TSLA)
3. **By Change %**: Top gainers or top losers of the day

### 🗣️ Viva Explanation
> "Insertion sort is used to arrange stocks by price, symbol, or change percentage. It works like sorting playing cards - you pick each stock and insert it into the correct position among already sorted stocks. For our typical dataset of 20-50 stocks, this O(n²) algorithm is perfectly efficient and easy to understand. We use it to show 'Top Gainers' or sort stocks from cheapest to most expensive."

---

## 4. Merge Sort Algorithm

### 📍 Location in Project
- **File**: `src/main/resources/static/js/stocks.js`
- **Used in**: `loadStocks()` function (line 21)
- **Methods**: `mergeSort()` (lines 107-117), `merge()` (lines 119-135)

### 🎯 What It Does
Merge sort is a divide-and-conquer algorithm that recursively splits the stock array in half, sorts each half, then merges them back together in sorted order.

### ⏱️ Time Complexity
- **All Cases**: O(n log n) - guaranteed performance
- **Space Complexity**: O(n) - requires extra space for merging

### 📊 Why Use Merge Sort Here?
In the frontend JavaScript, merge sort is used to sort stocks by price when displaying them to the user. Benefits:
1. ✅ **Guaranteed O(n log n)** performance - no worst case slowdown
2. ✅ **Stable sort** - maintains relative order of equal prices
3. ✅ **Works well in JavaScript** - recursive approach fits the language
4. ✅ **Frontend sorting** - sorts data once after fetching from API

### 💻 Code Implementation
```javascript
/**
 * Merge Sort Implementation (JavaScript)
 * Recursively divides array in half, sorts, and merges
 */
function mergeSort(arr, compareFn) {
    // Base case: array of length 0 or 1 is already sorted
    if (arr.length <= 1) {
        return arr;
    }
    
    // Divide: split array in half
    const mid = Math.floor(arr.length / 2);
    const left = mergeSort(arr.slice(0, mid), compareFn);
    const right = mergeSort(arr.slice(mid), compareFn);
    
    // Conquer: merge sorted halves
    return merge(left, right, compareFn);
}

/**
 * Merge two sorted arrays into one sorted array
 */
function merge(left, right, compareFn) {
    const result = [];
    let leftIndex = 0;
    let rightIndex = 0;
    
    // Compare elements from left and right, add smaller to result
    while (leftIndex < left.length && rightIndex < right.length) {
        if (compareFn(left[leftIndex], right[rightIndex]) <= 0) {
            result.push(left[leftIndex]);
            leftIndex++;
        } else {
            result.push(right[rightIndex]);
            rightIndex++;
        }
    }
    
    // Add remaining elements
    return result.concat(left.slice(leftIndex)).concat(right.slice(rightIndex));
}
```

### 📝 How It's Used in the Project
```javascript
// From stocks.js lines 14-22
async function loadStocks() {
    const response = await fetch('http://localhost:8080/api/stocks/all');
    
    if (response.ok) {
        let stocks = await response.json();
        
        // MERGE SORT: Sort stocks by price (cheapest first)
        stocks = mergeSort(stocks, (a, b) => a.price - b.price);
        
        displayStocks(stocks);
    }
}
```

### 🔗 Where to See It
**Page**: Open `/stocks.html` in the browser after logging in  
**Effect**: Stocks are automatically displayed sorted by price from cheapest to most expensive  
**Frontend Route**: `http://localhost:8080/stocks.html`

### 📊 Merge Sort vs Insertion Sort

| Aspect | Merge Sort (Frontend) | Insertion Sort (Backend) |
|--------|----------------------|--------------------------|
| **Language** | JavaScript | Java |
| **Location** | stocks.js | InsertionSort.java |
| **Time (Best)** | O(n log n) | O(n) |
| **Time (Worst)** | O(n log n) | O(n²) |
| **Space** | O(n) | O(1) in-place |
| **Use Case** | Frontend data sorting | Backend sorting with options |
| **Stability** | Stable | Stable |

### 🎓 Divide-and-Conquer Example

Sorting 8 stocks by price using merge sort:

```
                [150, 200, 75, 300, 50, 180, 220, 100]
                              ↓ DIVIDE
                /                              \
        [150, 200, 75, 300]              [50, 180, 220, 100]
              ↓ DIVIDE                         ↓ DIVIDE
        /              \                  /              \
    [150, 200]      [75, 300]        [50, 180]      [220, 100]
       ↓               ↓                 ↓               ↓
    [150, 200]      [75, 300]        [50, 180]      [100, 220]
         \              /                 \              /
          ↓ MERGE ↓                        ↓ MERGE ↓
      [75, 150, 200, 300]              [50, 100, 180, 220]
                   \                      /
                    \                    /
                     ↓ FINAL MERGE ↓
           [50, 75, 100, 150, 180, 200, 220, 300]
```

### 🗣️ Viva Explanation
> "Merge sort is implemented in JavaScript on the frontend to sort stocks by price when displaying them to users. It uses a divide-and-conquer approach: split the array in half recursively until you have single elements, then merge them back together in sorted order. It guarantees O(n log n) time complexity, making it efficient for any dataset size. We use it in the frontend because it's stable, predictable, and works beautifully with JavaScript's functional programming style. When you open the stocks page, the list is automatically sorted from cheapest to most expensive using merge sort."

### 💡 Why Both Merge Sort AND Insertion Sort?

**Frontend (Merge Sort - JavaScript)**:
- Sorts data once after fetching from API
- No backend integration needed
- O(n log n) guaranteed for any data size
- Located in: `stocks.js`
- Always sorts by price (cheapest first)

**Backend (Insertion Sort - Java)**:
- Provides API endpoints with sorting options
- Allows clients to request sorted data
- Better for small datasets (typical stock portfolios)
- More educational for demonstrating algorithm differences
- Located in: `InsertionSort.java`
- Flexible sorting (price, symbol, change %)

Both approaches are valid and serve different purposes!

---

## 5. Price Fluctuation Mathematics

The stock prices in this application fluctuate using two mathematical approaches:

### 📊 Method 1: Admin Manual Fluctuation

#### 📍 Location
`src/main/java/com/stockapp/controllers/AdminController.java` (lines 174-179)

#### 📐 Formula
```
changePercent = ((newPrice - oldPrice) / oldPrice) × 100
```

#### 💻 Code
```java
BigDecimal oldPrice = stock.getPrice();
BigDecimal newPriceBD = new BigDecimal(newPrice);

double changePercent = 0.0;
if (oldPrice.compareTo(BigDecimal.ZERO) > 0) {
    changePercent = newPriceBD.subtract(oldPrice)
                              .divide(oldPrice, 4, BigDecimal.ROUND_HALF_UP)
                              .doubleValue() * 100.0;
}

stock.setPrice(newPriceBD);
stock.setChangePercent(changePercent);
```

#### 📝 Example Calculation
```
Old Price: $200.00
New Price: $220.00

changePercent = ((220 - 200) / 200) × 100
              = (20 / 200) × 100
              = 0.10 × 100
              = 10.0%

Result: Stock gained 10% ✅
```

#### 🔗 API Route
```http
POST http://localhost:8080/api/admin/stocks/fluctuate
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "stockId": 1,
  "newPrice": 220.00,
  "reason": "Strong quarterly earnings"
}
```

### 📊 Method 2: Daily Simulation Fluctuation

#### 📍 Location
`src/main/java/com/stockapp/dao/StockDAO.java` (lines 29-30, 48-50)

#### 📐 Formula
```
seed = currentTimeMillis / (1000 × 60 × 60 × 24)
random = new Random(seed)

changePercent = (random.nextDouble() × 0.10) - 0.05

Range: -5% to +5%
```

#### 💻 Code
```java
// Generate seed based on current day (stable per day)
long seed = System.currentTimeMillis() / (1000 * 60 * 60 * 24);
Random random = new Random(seed);

while (rs.next()) {
    double changePercent = rs.getDouble("change_percent");
    
    // Simulate if database has default 0.0
    if (changePercent == 0.0) {
        // Random value between -5% and +5%
        changePercent = (random.nextDouble() * 0.10) - 0.05;
    }
    
    stocks.add(new Stock(id, symbol, name, price, changePercent, suspended));
}
```

#### 📝 How It Works
1. **Seed Generation**: Based on current day (milliseconds / day-in-ms)
2. **Same Seed = Same Random Numbers**: All stocks get consistent fluctuation for the day
3. **Random Range**: 
   - `random.nextDouble()` returns value between 0.0 and 1.0
   - Multiply by 0.10 → range 0.0 to 0.10
   - Subtract 0.05 → range -0.05 to +0.05
   - Result: -5% to +5%

#### 📊 Example Simulation
```
random.nextDouble() = 0.73

changePercent = (0.73 × 0.10) - 0.05
              = 0.073 - 0.05
              = 0.023
              = 2.3%

Result: Stock gained 2.3% today ✅
```

### 🗣️ Viva Explanation
> "The project uses two methods for price fluctuation. First, admins can manually set new prices, and the system calculates the percentage change using the formula: (newPrice - oldPrice) / oldPrice × 100. Second, for simulation, we use Java's Random class with a daily seed to generate consistent fluctuations between -5% and +5% each day. This creates realistic market simulation while keeping the same values throughout the day."

---

## 6. API Routes Reference

### 🔐 Authentication
Most routes are protected. Include this header:
```
Authorization: Bearer {your_jwt_token}
```

### 📈 Stock Endpoints

#### Get All Stocks (Basic)
```http
GET /api/stocks/all
```
Returns: List of all stocks (unsorted)

#### Get All Stocks (Sorted by Price) - USES INSERTION SORT ✨
```http
GET /api/stocks/all?sort=price&order=asc
GET /api/stocks/all?sort=price&order=desc
```
- `asc`: Cheapest first
- `desc`: Most expensive first

#### Get All Stocks (Sorted Alphabetically) - USES INSERTION SORT ✨
```http
GET /api/stocks/all?sort=symbol
```
Returns stocks in alphabetical order: AAPL, AMZN, GOOG...

#### Get All Stocks (Sorted by Change %) - USES INSERTION SORT ✨
```http
GET /api/stocks/all?sort=change&order=desc  (Top Gainers)
GET /api/stocks/all?sort=change&order=asc   (Top Losers)
```

#### Get Stock by Symbol (SQL Query)
```http
GET /api/stocks/symbol/{symbol}
Example: GET /api/stocks/symbol/AAPL
```

#### Get Stock by Symbol (Linear Search) - USES LINEAR SEARCH ✨
```http
GET /api/stocks/symbol/{symbol}?useAlgorithm=true
Example: GET /api/stocks/symbol/AAPL?useAlgorithm=true
```

### 👥 User Endpoints

#### Get User by ID (SQL Query)
```http
GET /api/users/{id}
```

### 🔧 Admin Endpoints

#### Search User by ID (Binary Search) - USES BINARY SEARCH ✨
```http
GET /api/admin/users/search/{id}
Authorization: Bearer {admin_token}
```
*Note: This specific endpoint is for demonstrating binary search algorithm efficiency.*

#### Fluctuate Stock Price (Manual) - USES MATH FORMULA ✨
```http
POST /api/admin/stocks/fluctuate
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "stockId": 1,
  "newPrice": 250.00,
  "reason": "Market demand increased"
}
```

#### Suspend/Resume Stock Trading
```http
POST /api/admin/stocks/suspend
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "stockId": 2,
  "suspend": true
}
```

#### Get All Transactions
```http
GET /api/admin/transactions/all
Authorization: Bearer {admin_token}
```

#### Rollback Transaction
```http
POST /api/admin/transactions/rollback/{transactionId}
Authorization: Bearer {admin_token}
```

### 💰 Transaction Endpoints

#### Buy Stock
```http
POST /api/transactions/buy
Authorization: Bearer {token}
Content-Type: application/json

{
  "stockId": 1,
  "quantity": 10
}
```

#### Sell Stock
```http
POST /api/transactions/sell
Authorization: Bearer {token}
Content-Type: application/json

{
  "stockId": 1,
  "quantity": 5
}
```

#### Get User Transactions
```http
GET /api/transactions/user
Authorization: Bearer {token}
```

---

## 7. Quick Demo Guide (For Viva)

### 🎬 Demonstration Script

#### **Demo 1: Binary Search** (2 minutes)
1. Login as Admin and get token
2. Call: `GET http://localhost:8080/api/admin/users/search/1`
3. **Check console logs** - you'll see:
   ```
   ========== USING BINARY SEARCH ALGORITHM (UserDAO) ==========
   [ALGORITHM] Binary Search: Searching for User ID=1
     Iteration 1: Checking position 5 (ID=5)
     Iteration 2: Checking position 2 (ID=2)
     Iteration 3: Checking position 1 (ID=1)
   [ALGORITHM] Binary Search: Found after 3 iterations!
   ```
4. **Explain**: "Binary search split the user list in half each time, finding user #1 in just 3 comparisons instead of checking all users one by one."

#### **Demo 2: Linear Search** (2 minutes)
1. Call: `GET http://localhost:8080/api/stocks/symbol/AAPL?useAlgorithm=true`
2. **Check console logs**:
   ```
   ========== USING LINEAR SEARCH ALGORITHM ==========
   [ALGORITHM] Linear Search: Searching for Stock Symbol=AAPL
     Iteration 1: Checking AAPL
   [ALGORITHM] Linear Search: Found 'AAPL' after checking 1 stocks
   ```
3. **Explain**: "Linear search checked each symbol sequentially until it found AAPL."

#### **Demo 3: Insertion Sort** (2 minutes)
1. Call: `GET http://localhost:8080/api/stocks/all?sort=price&order=desc`
2. **Check console logs**:
   ```
   ========== USING INSERTION SORT ALGORITHM ==========
   [ALGORITHM] Insertion Sort: Sorting 10 stocks by price (descending)
   [ALGORITHM] Insertion Sort: Complete! 25 comparisons, 12 swaps
   ```
3. **Show response** - stocks are sorted from most expensive to cheapest
4. **Explain**: "Insertion sort arranged all 10 stocks by price in descending order."

#### **Demo 4: Price Fluctuation** (2 minutes)
1. Login as admin first
2. Call: `POST /api/admin/stocks/fluctuate` with body:
   ```json
   {
     "stockId": 1,
     "newPrice": 250.00,
     "reason": "Demo for viva"
   }
   ```
3. **Explain the math**: "Old price was $200, new is $250. Change = (250-200)/200 × 100 = 25%"
4. Verify by calling: `GET /api/stocks/1`
5. Show the `changePercent` field is now 25.0

#### **Demo 5: Merge Sort** (2 minutes)
1. Open browser and login to the application
2. Navigate to: `http://localhost:8080/stocks.html`
3. **Open browser console** (F12 → Console tab)
4. **Observe the stock list** - stocks are automatically sorted from cheapest to most expensive
5. **Explain**: 
   - "When the page loads, it fetches all stocks from the API"
   - "Then merge sort runs in JavaScript to sort them by price"
   - "You can see in the source code at stocks.js line 21"
   - "Merge sort uses divide-and-conquer with guaranteed O(n log n) performance"
   - "The cheapest stocks appear at the top of the table"
6. **Show the code**: Open DevTools → Sources → stocks.js → lines 107-135
7. **Point out**: "This is frontend sorting - happens in the browser, not on the server"


---

## 📚 Summary Table

| Algorithm | Location | Route | Time Complexity | Use Case |
|-----------|----------|-------|-----------------|----------|
| **Binary Search** | `BinarySearch.java` | `/api/admin/users/search/{id}` | O(log n) | Find user by ID (UserDAO) |
| **Linear Search** | `LinearSearch.java` | `/api/stocks/symbol/AAPL?useAlgorithm=true` | O(n) | Find stock by symbol (StockDAO) |
| **Insertion Sort** | `InsertionSort.java` | `/api/stocks/all?sort=price&order=asc` | O(n²) | Sort stocks by price/symbol/change |
| **Merge Sort** | `stocks.js` (JavaScript) | `http://localhost:8080/stocks.html` (frontend) | O(n log n) | Frontend: Sort stocks by price on page load |

---

## 🎓 Viva Questions & Answers

### Q1: Why use Binary Search instead of Linear Search?
**A**: Binary search is much faster for sorted data. For 100 users, binary search needs ~7 comparisons vs ~50 for linear search. However, it requires the list to be sorted first. In our app, we use it to find users by ID in the admin panel.

### Q2: When would you use Linear Search?
**A**: When searching by strings (like stock symbols), when data is unsorted, or for very small datasets where the sorting overhead isn't worth it.

### Q3: Why Insertion Sort instead of Merge Sort for backend?
**A**: Both are implemented! Merge sort is used in the frontend JavaScript (stocks.js) for sorting stocks by price with guaranteed O(n log n) performance. Insertion sort is used in the backend Java API to provide flexible sorting options (by price, symbol, or change percentage). For our typical dataset of 10-50 stocks, insertion sort is simpler, uses less memory (O(1) vs O(n)), and is actually faster. We use both to demonstrate different algorithms in different contexts.

### Q4: Explain the price fluctuation calculation.
**A**: We calculate percentage change using: `(newPrice - oldPrice) / oldPrice × 100`. For example, if a stock goes from $200 to $220, that's (220-200)/200 × 100 = 10% gain.

### Q5: How does daily simulation work?
**A**: We use Java's Random class with a seed based on the current day. This generates consistent random values between -5% and +5% for the entire day, simulating realistic market fluctuations.

---

## 📞 Quick Reference

**Project Directory**: `d:\Stock-Trading-App\Stock-Trading-App\`

**Algorithm Files**:
- `src/main/java/com/stockapp/algorithms/BinarySearch.java`
- `src/main/java/com/stockapp/algorithms/LinearSearch.java`
- `src/main/java/com/stockapp/algorithms/InsertionSort.java`

**Integration Files**:
- `src/main/java/com/stockapp/dao/UserDAO.java` (Binary Search)
- `src/main/java/com/stockapp/dao/StockDAO.java` (Linear Search, Insertion Sort)
- `src/main/java/com/stockapp/controllers/StocksController.java`
- `src/main/java/com/stockapp/controllers/AdminController.java` (User Search demo endpoint)

**Start Application**: Run `run_app.bat` or `mvn spring-boot:run`

**Server**: `http://localhost:8080`

---

*Document prepared for viva demonstration - January 2026*
*All algorithms tested and verified ✅*
