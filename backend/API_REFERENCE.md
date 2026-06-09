# QuizApp API Reference - Complete Endpoint Documentation

## 🔐 Authentication Header
All protected endpoints require:
```
Authorization: Bearer <JWT_TOKEN>
```

---

## 📋 Authentication Endpoints

### 1. Register User
```
POST /api/auth/register
```
**Access:** Public  
**Body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securePass123"
}
```
**Response:** `201 Created`
```json
{
  "status": "success",
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "role": "user",
    "token": "eyJhbGciOiJIUzI1NiIs..."
  }
}
```

---

### 2. Login
```
POST /api/auth/login
```
**Access:** Public  
**Body:**
```json
{
  "email": "john@example.com",
  "password": "securePass123"
}
```
**Response:** `200 OK`
```json
{
  "status": "success",
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "role": "user",
    "token": "eyJhbGciOiJIUzI1NiIs..."
  }
}
```

---

### 3. Google OAuth
```
POST /api/auth/google
```
**Access:** Public  
**Body:**
```json
{
  "idToken": "eyJhbGciOiJSUzI1NiIs..."
}
```
**Response:** `200 OK`
```json
{
  "status": "success",
  "data": {
    "id": 2,
    "username": "John Doe",
    "email": "john@gmail.com",
    "role": "user",
    "token": "eyJhbGciOiJIUzI1NiIs..."
  }
}
```

---

### 4. Forgot Password
```
POST /api/auth/forgot-password
```
**Access:** Public  
**Body:**
```json
{
  "email": "john@example.com"
}
```
**Response:** `200 OK`
```json
{
  "success": true
}
```
**Note:** OTP sent to email

---

### 5. Reset Password
```
POST /api/auth/reset-password
```
**Access:** Public  
**Body:**
```json
{
  "email": "john@example.com",
  "otp": "123456",
  "new_password": "newSecurePass456"
}
```
**Response:** `200 OK`
```json
{
  "success": true
}
```

---

### 6. Delete Account (GDPR)
```
DELETE /api/auth/erase
```
**Access:** Protected (Any User)  
**Response:** `200 OK`
```json
{
  "status": "success",
  "message": "Account and associated data completely erased."
}
```

---

## 🎯 Quiz Endpoints

### 1. Get Categories
```
GET /api/quiz/categories
```
**Access:** Protected (User)  
**Response:** `200 OK`
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "name": "General Knowledge",
      "description": "Test your knowledge",
      "question_count": 50
    }
  ]
}
```

---

### 2. Get Questions
```
GET /api/quiz/questions/:categoryId?page=1&limit=10
```
**Access:** Protected (User)  
**Query Params:**
- `page` (default: 1)
- `limit` (default: 10)

**Response:** `200 OK`
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "category_id": 1,
      "question_text": "What is the capital of France?",
      "option_a": "London",
      "option_b": "Paris",
      "option_c": "Berlin",
      "option_d": "Madrid"
    }
  ],
  "meta": {
    "total": 50,
    "page": 1,
    "limit": 10,
    "totalPages": 5
  }
}
```

---

### 3. Submit Quiz Result
```
POST /api/quiz/submit
```
**Access:** Protected (User)  
**Body:**
```json
{
  "category_id": 1,
  "score": 8,
  "total_questions": 10
}
```
**Response:** `201 Created`
```json
{
  "status": "success",
  "message": "Quiz result saved"
}
```

---

### 4. Submit Detailed Quiz
```
POST /api/quiz/submit-detailed
```
**Access:** Protected (User)  
**Body:**
```json
{
  "category_id": 1,
  "score": 8,
  "total_questions": 10,
  "answers": [
    {"question_id": 1, "is_correct": true},
    {"question_id": 2, "is_correct": false}
  ]
}
```
**Response:** `201 Created`
```json
{
  "status": "success",
  "message": "Detailed quiz result saved"
}
```

---

### 5. Get Quiz History
```
GET /api/quiz/history
```
**Access:** Protected (User)  
**Response:** `200 OK`
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "category_name": "General Knowledge",
      "score": 8,
      "total_questions": 10,
      "completed_at": "2026-06-09T10:30:00Z"
    }
  ]
}
```

---

### 6. Get Leaderboard
```
GET /api/quiz/leaderboard?period=all_time
```
**Access:** Protected (User)  
**Query Params:**
- `period` - 'today', 'week', 'all_time' (default: 'all_time')

**Response:** `200 OK`
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "username": "john_doe",
      "level": 5,
      "total_points": 1200,
      "quizzes_taken": 12
    }
  ]
}
```

---

### 7. Get Favorites
```
GET /api/quiz/favourites
```
**Access:** Protected (User)  
**Response:** `200 OK`
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "question_text": "What is the capital of France?",
      "option_a": "London",
      "option_b": "Paris",
      "option_c": "Berlin",
      "option_d": "Madrid",
      "saved_at": "2026-06-09T10:00:00Z"
    }
  ]
}
```

---

### 8. Add to Favorites
```
POST /api/quiz/favourites
```
**Access:** Protected (User)  
**Body:**
```json
{
  "questionId": 1
}
```
**Response:** `201 Created`
```json
{
  "status": "success",
  "message": "Added to favourites"
}
```

---

### 9. Remove from Favorites
```
DELETE /api/quiz/favourites/:questionId
```
**Access:** Protected (User)  
**Response:** `200 OK`
```json
{
  "status": "success",
  "message": "Removed from favourites"
}
```

---

## 👤 User Endpoints

### 1. Get User Stats
```
GET /api/user/stats
```
**Access:** Protected (User)  
**Response:** `200 OK`
```json
{
  "status": "success",
  "data": {
    "level": 5,
    "last_synced_at": "2026-06-09T10:00:00Z",
    "total_quizzes": 12,
    "avg_score": 78,
    "total_points": 1200,
    "scores_by_category": [
      {
        "category_name": "General Knowledge",
        "score": 85
      }
    ]
  }
}
```

---

### 2. Sync User Data
```
POST /api/user/sync
```
**Access:** Protected (User)  
**Body:**
```json
{
  "level": 5
}
```
**Response:** `200 OK`
```json
{
  "status": "success",
  "message": "Sync successful"
}
```

---

### 3. Delete User Account
```
DELETE /api/user/account
```
**Access:** Protected (User)  
**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Account and all associated data deleted successfully."
}
```

---

## 🔧 Admin Endpoints

### 1. Create Category
```
POST /api/admin/categories
```
**Access:** Protected (Admin)  
**Body:**
```json
{
  "name": "Physics",
  "description": "Physics-related questions"
}
```
**Response:** `201 Created`
```json
{
  "status": "success",
  "data": {
    "id": 5,
    "name": "Physics",
    "description": "Physics-related questions"
  }
}
```

---

### 2. Add Question
```
POST /api/admin/questions
```
**Access:** Protected (Admin)  
**Body:**
```json
{
  "categoryId": 1,
  "questionText": "What is the speed of light?",
  "optionA": "3x10^8 m/s",
  "optionB": "2x10^8 m/s",
  "optionC": "5x10^8 m/s",
  "optionD": "1x10^8 m/s",
  "correctOption": "A",
  "explanation": "The speed of light is 3x10^8 m/s",
  "difficulty": "medium"
}
```
**Response:** `201 Created`
```json
{
  "status": "success",
  "message": "Question added successfully"
}
```

---

### 3. Get Admin Questions
```
GET /api/admin/questions?limit=20&search=capital&categoryId=1
```
**Access:** Protected (Admin)  
**Query Params:**
- `limit` (default: 100)
- `search` (optional, search text)
- `categoryId` (optional, filter by category)

**Response:** `200 OK`
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "question_text": "What is the capital of France?",
      "category_name": "General Knowledge",
      "difficulty": "easy"
    }
  ]
}
```

---

### 4. Update Question
```
PUT /api/admin/questions/:id
```
**Access:** Protected (Admin)  
**Body:**
```json
{
  "questionText": "What is the capital of England?",
  "difficulty": "medium"
}
```
**Response:** `200 OK`
```json
{
  "status": "success",
  "data": { /* updated question */ }
}
```

---

### 5. Delete Question
```
DELETE /api/admin/questions/:id
```
**Access:** Protected (Admin)  
**Response:** `200 OK`
```json
{
  "status": "success",
  "message": "Question deleted successfully"
}
```

---

## 🔄 Sync Endpoints

### 1. Bulk Sync
```
POST /api/sync/bulk
```
**Access:** Protected (User)  
**Body:**
```json
{
  "lastSyncTime": "2026-06-09T09:00:00Z",
  "changes": {
    "created": [
      {
        "id": "abc123",
        "type": "quiz_attempt",
        "data": { /* data */ },
        "last_modified_at": "2026-06-09T09:30:00Z"
      }
    ],
    "updated": [
      {
        "id": "def456",
        "type": "quiz_attempt",
        "data": { /* updated data */ },
        "last_modified_at": "2026-06-09T09:45:00Z"
      }
    ],
    "deleted": ["ghi789"]
  }
}
```
**Response:** `200 OK`
```json
{
  "status": "success",
  "timestamp": "2026-06-09T10:00:00Z",
  "serverChanges": {
    "created": [
      {
        "id": "jkl101",
        "type": "quiz",
        "data": { /* server change */ }
      }
    ],
    "updated": [],
    "deleted": []
  }
}
```

---

## ⚠️ Error Responses

### 400 Bad Request
```json
{
  "status": "fail",
  "message": "Invalid input"
}
```

### 401 Unauthorized
```json
{
  "status": "fail",
  "message": "Not authorized, no token"
}
```

### 403 Forbidden
```json
{
  "status": "fail",
  "message": "Not authorized as an admin"
}
```

### 404 Not Found
```json
{
  "status": "error",
  "message": "Resource not found"
}
```

### 500 Internal Server Error
```json
{
  "status": "error",
  "message": "Internal server error"
}
```

---

## 📊 Rate Limiting
- **Limit**: 500 requests per 15 minutes per IP
- **Headers**: `X-RateLimit-*` headers in response
- **Exceptions**: Some routes are skipped (e.g., `/api/quiz/categories`)

---

## 🧪 Testing with cURL

### Register
```bash
curl -X POST http://localhost:5000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","password":"pass123"}'
```

### Login
```bash
curl -X POST http://localhost:5000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"pass123"}'
```

### Get Categories (with token)
```bash
curl -X GET http://localhost:5000/api/quiz/categories \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Submit Quiz
```bash
curl -X POST http://localhost:5000/api/quiz/submit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"category_id":1,"score":8,"total_questions":10}'
```

---

**Last Updated**: June 2026  
**Version**: 1.0.0
