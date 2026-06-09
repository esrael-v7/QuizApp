# QuizApp Backend - Architecture & Developer Guide

## 📚 Table of Contents
- [Overview](#overview)
- [Project Structure](#project-structure)
- [Architecture Layers](#architecture-layers)
- [Data Flow](#data-flow)
- [Design Patterns](#design-patterns)
- [Authentication & Authorization](#authentication--authorization)
- [Database Design](#database-design)
- [Error Handling](#error-handling)
- [Performance Optimization](#performance-optimization)

---

## Overview

The QuizApp backend is a Node.js/Express API that serves the Android frontend. It handles authentication, quiz management, user stats, admin operations, and offline-first data synchronization using Last-Write-Wins conflict resolution.

**Tech Stack:**
- Node.js 18+
- Express.js 5
- PostgreSQL 12+
- JWT for authentication
- Node-cache for performance

---

## Project Structure

```
backend/
├── config/
│   └── db.js                 # PostgreSQL connection pools
├── controllers/              # Request handlers
│   ├── authController.js     # Auth logic
│   ├── quizController.js     # Quiz operations
│   ├── userController.js     # User stats
│   ├── adminController.js    # Admin operations
│   └── syncController.js     # Sync operations
├── middleware/
│   └── authMiddleware.js     # JWT + RBAC
├── models/
│   └── SyncModel.js          # Data access layer
├── routes/
│   ├── authRoutes.js
│   ├── quizRoutes.js
│   ├── userRoutes.js
│   ├── adminRoutes.js
│   └── syncRoutes.js
├── services/
│   └── SyncService.js        # Business logic
├── utils/
│   ├── emailService.js       # Email/OTP
│   ├── auditLogger.js        # Audit logging
│   └── cache.js              # Caching
├── server.js                 # Express app
├── package.json              # Dependencies
└── .env                      # Configuration
```

---

## Architecture Layers

### 1. Route Layer (routes/)
**Responsibility**: HTTP endpoint definition

```javascript
// routes/quizRoutes.js
router.get('/categories', protect, getCategories);
router.post('/submit', protect, submitQuizResult);
```

### 2. Middleware Layer (middleware/)
**Responsibility**: Cross-cutting concerns

```javascript
// middleware/authMiddleware.js
const protect = (req, res, next) => {
    const token = req.headers.authorization?.split(' ')[1];
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    req.user = decoded;
    next();
};
```

### 3. Controller Layer (controllers/)
**Responsibility**: Request handling & response formatting

```javascript
// controllers/quizController.js
exports.getCategories = async (req, res) => {
    try {
        const cachedCats = cache.get('categories');
        if (cachedCats) return res.json({ status: 'success', data: cachedCats });
        
        const { rows } = await clientPool.query('SELECT * FROM categories');
        cache.set('categories', rows);
        res.json({ status: 'success', data: rows });
    } catch (error) {
        res.status(500).json({ status: 'error', message: error.message });
    }
};
```

### 4. Service Layer (services/)
**Responsibility**: Business logic & complex operations

```javascript
// services/SyncService.js
class SyncService {
    static async processBulkSync(userId, lastSyncTime, changes) {
        // Implements Last-Write-Wins conflict resolution
        // Manages transactions
        // Returns server changes
    }
}
```

### 5. Model Layer (models/)
**Responsibility**: Data access abstraction

```javascript
// models/SyncModel.js
class SyncModel {
    static async findById(id) { /* query */ }
    static async create(record) { /* insert */ }
    static async update(id, userId, data, lastModifiedAt) { /* update */ }
}
```

### 6. Configuration Layer (config/)
**Responsibility**: External service setup

```javascript
// config/db.js
const clientPool = new Pool({ /* client connection */ });
const adminPool = new Pool({ /* admin connection */ });
module.exports = { clientPool, adminPool };
```

### 7. Utility Layer (utils/)
**Responsibility**: Reusable helpers

```javascript
// utils/emailService.js - Send OTP emails
// utils/auditLogger.js - Log user actions
// utils/cache.js - Cache management
```

---

## Data Flow

### Example: Submit Quiz Result
```
Android App (HTTP POST /api/quiz/submit)
    ↓
Express Router (route matching)
    ↓
Middleware Chain
    ├─ protect (JWT verification)
    └─ adminOnly (if required)
    ↓
Controller (quizController.submitQuizResult)
    ├─ Extract request data
    ├─ Validate input
    ├─ Call Model.create()
    └─ Return response
    ↓
Model (database query)
    ├─ INSERT into quiz_history
    └─ Return created record
    ↓
Controller (format response)
    ├─ Log action (auditLogger)
    └─ Send JSON response
    ↓
Android App (receive response)
```

---

## Design Patterns

### 1. MVC Pattern
- **View**: HTTP requests/responses
- **Controller**: Request handlers
- **Model**: Data access layer

### 2. Middleware Chain
Composable middleware for cross-cutting concerns:
```javascript
app.use(helmet());           // Security headers
app.use(cors());             // CORS
app.use(express.json());     // JSON parser
app.use(limiter);            // Rate limiting
app.use('/api/auth', authRoutes);  // Routes
```

### 3. Async/Await Pattern
All async operations use promises:
```javascript
try {
    const { rows } = await clientPool.query('SELECT * FROM users');
    res.json({ status: 'success', data: rows });
} catch (error) {
    res.status(500).json({ status: 'error', message: error.message });
}
```

### 4. Transaction Management
Data consistency with transactions:
```javascript
await client.query('BEGIN');
try {
    await client.query('INSERT INTO ...');
    await client.query('UPDATE ...');
    await client.query('COMMIT');
} catch (error) {
    await client.query('ROLLBACK');
}
```

### 5. Last-Write-Wins (LWW) Conflict Resolution
For offline-first sync:
```javascript
const serverTime = new Date(rows[0].last_modified_at).getTime();
const clientTime = new Date(item.last_modified_at).getTime();

// Client change wins if newer or equal
if (clientTime >= serverTime) {
    await update(item);
}
```

---

## Authentication & Authorization

### JWT Tokens
```javascript
const token = jwt.sign(
    { id: user.id, role: user.role },
    process.env.JWT_SECRET,
    { expiresIn: process.env.JWT_EXPIRES_IN }
);
```

**Token Structure**: `{ id, role }`  
**Expiration**: 1 day (configurable)  
**Storage**: Client sends in `Authorization: Bearer <token>` header

### Role-Based Access Control (RBAC)
```javascript
// Roles: 'user', 'admin'

// User routes - any authenticated user
router.get('/categories', protect, getCategories);

// Admin routes - requires admin role
router.post('/admin/questions', protect, adminOnly, addQuestion);
```

---

## Database Design

### Connection Pooling
```javascript
// Client Pool (read-heavy)
const clientPool = new Pool({ max: 10 });

// Admin Pool (CRUD operations)
const adminPool = new Pool({ max: 5 });
```

**Benefits:**
- Connection reuse
- Prevent exhaustion
- Role-based access at DB level
- Least privilege principle

### Key Tables
```sql
users                   -- User profiles & authentication
categories              -- Quiz categories
questions               -- Multiple-choice questions
quiz_history            -- User quiz submissions
user_answers            -- Detailed answer tracking
user_favourites         -- Bookmarked questions
password_reset_codes    -- OTP for password reset
audit_logs              -- User action logging
sync_records            -- Offline sync data
```

---

## Error Handling

### HTTP Status Codes
| Code | Meaning |
|------|---------|
| 200 | OK - Success |
| 201 | Created - Resource created |
| 400 | Bad Request - Invalid input |
| 401 | Unauthorized - Missing/invalid JWT |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource not found |
| 500 | Server Error |

### Response Format
```javascript
// Success
{ status: 'success', data: {...} }

// Error
{ status: 'error', message: 'Error description' }
```

### Error Handling Pattern
```javascript
try {
    // Database operations
} catch (error) {
    if (error.code === '23505') {  // Unique constraint violation
        return res.status(400).json({ 
            status: 'fail', 
            message: 'User already exists' 
        });
    }
    res.status(500).json({ status: 'error', message: error.message });
}
```

---

## Performance Optimization

### 1. Caching Strategy
```javascript
// Cache for 1 hour
cache.set('categories', data, 3600);

// On update/delete, invalidate
cache.del('categories');
```

**Cached Data:**
- Quiz categories
- Paginated questions

### 2. Database Optimization
```sql
-- Add indexes for frequently queried columns
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_quiz_history_user_id ON quiz_history(user_id);
CREATE INDEX idx_questions_category_id ON questions(category_id);
```

### 3. Connection Pooling
Reuse database connections instead of creating new ones for each request.

### 4. Pagination
```javascript
const limit = parseInt(req.query.limit) || 10;
const offset = (page - 1) * limit;
// SELECT ... LIMIT $limit OFFSET $offset
```

---

## Security Features

✅ **Authentication**: JWT tokens with expiration  
✅ **Authorization**: Role-based access control (RBAC)  
✅ **Password Security**: bcryptjs hashing  
✅ **SQL Injection Prevention**: Parameterized queries  
✅ **Rate Limiting**: 500 requests/15 minutes  
✅ **CORS**: Configured for cross-origin requests  
✅ **Security Headers**: Helmet.js  
✅ **Audit Logging**: All user actions logged  
✅ **Data Validation**: Input validation on all endpoints  

---

## Testing

### Manual Testing with Postman
Import `QuizApp_Postman_Collection.json` to test all endpoints.

### Example cURL Requests
```bash
# Register
curl -X POST http://localhost:5000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","password":"pass123"}'

# Login
curl -X POST http://localhost:5000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"pass123"}'

# Get Categories (with token)
curl -X GET http://localhost:5000/api/quiz/categories \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Contributing Guidelines

### Code Style
- Use camelCase for functions/variables
- Use PascalCase for classes
- Add JSDoc comments for functions
- 2-space indentation

### Adding a New Endpoint
1. Create route in `routes/`
2. Create controller function in `controllers/`
3. Create model methods in `models/` if needed
4. Add tests to Postman collection
5. Document in API_REFERENCE.md

### Example: New Quiz Statistics Endpoint
```javascript
// routes/quizRoutes.js
router.get('/stats/:categoryId', protect, getQuizStats);

// controllers/quizController.js
exports.getQuizStats = async (req, res) => {
    const { categoryId } = req.params;
    const userId = req.user.id;
    try {
        const { rows } = await clientPool.query(
            'SELECT AVG(score) FROM quiz_history WHERE user_id = $1 AND category_id = $2',
            [userId, categoryId]
        );
        res.json({ status: 'success', data: rows[0] });
    } catch (error) {
        res.status(500).json({ status: 'error', message: error.message });
    }
};
```

---

## Troubleshooting

### Issue: "Not authorized, no token"
**Solution**: Include `Authorization: Bearer <token>` header in request

### Issue: "Token failed"
**Solution**: Check JWT_SECRET matches, token hasn't expired

### Issue: Database connection timeout
**Solution**: Check database is running, credentials in .env are correct

### Issue: Rate limiting blocking requests
**Solution**: Add route to skip list in server.js or increase limit

---

## Future Enhancements

- [ ] GraphQL API alternative
- [ ] WebSocket for real-time updates
- [ ] Redis for advanced caching
- [ ] Swagger/OpenAPI documentation
- [ ] Jest unit & integration tests
- [ ] Database migrations (Knex/Sequelize)
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Microservices architecture
- [ ] API versioning

---

**Last Updated**: June 2026  
**Maintained By**: Esrael Bekele  
**Version**: 1.0.0
