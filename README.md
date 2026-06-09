# QuizApp - Full Stack Quiz Application

A comprehensive quiz application combining an **Android frontend** with a **Node.js backend**, featuring Google Sign-In, password recovery, admin dashboard, and real-time data synchronization.

## 📱 Project Structure

```
QuizApp/
├── app/                              # Android Frontend (Kotlin)
│   ├── manifests/
│   ├── java/                         # Android source code
│   ├── res/                          # Android resources (layouts, strings, etc.)
│   └── build.gradle.kts
│
├── backend/                          # Node.js/Express Backend
│   ├── config/
│   │   └── db.js                     # PostgreSQL connection pools (client & admin)
│   │
│   ├── controllers/
│   │   ├── authController.js         # Registration, Login, Google Auth, Password Reset
│   │   ├── quizController.js         # Quiz categories, questions, submissions, leaderboard
│   │   ├── userController.js         # User stats, sync, account deletion
│   │   ├── adminController.js        # Admin question management
│   │   └── syncController.js         # Data synchronization
│   │
│   ├── middleware/
│   │   └── authMiddleware.js         # JWT authentication & admin authorization
│   │
│   ├── routes/
│   │   ├── authRoutes.js             # Auth endpoints
│   │   ├── quizRoutes.js             # Quiz endpoints
│   │   ├── userRoutes.js             # User endpoints
│   │   ├── adminRoutes.js            # Admin endpoints
│   │   └── syncRoutes.js             # Sync endpoints
│   │
│   ├── models/
│   │   └── SyncModel.js              # Data sync models
│   │
│   ├── services/
│   │   └── SyncService.js            # Bulk sync processing
│   │
│   ├── utils/
│   │   ├── emailService.js           # Email/OTP sending
│   │   ├── auditLogger.js            # User action logging
│   │   └── cache.js                  # Node-cache for performance
│   │
│   ├── server.js                     # Main Express server
│   ├── package.json                  # Dependencies
│   ├── .env                          # Environment configuration
│   └── .gitignore
│
├── database_schema.sql               # Initial database schema
├── seed.sql                          # Sample data
├── gradle/
├── build.gradle.kts
└── README.md
```

---

## 🚀 Features

### Android App Features
- ✅ **Google Sign-In** - Quick authentication with Google
- ✅ **Email/Password Login** - Traditional account creation
- ✅ **Password Recovery** - OTP-based password reset
- ✅ **Quiz Taking** - Category-based quizzes with multiple-choice questions
- ✅ **Progress Tracking** - User stats, scores, and history
- ✅ **Favorites** - Save questions for later review
- ✅ **Leaderboard** - Compete with other users
- ✅ **Admin Dashboard** - Manage questions and categories
- ✅ **Cloud Sync** - Offline-first with background sync

### Backend API Features
- ✅ **Authentication** - JWT-based with role-based access control (RBAC)
- ✅ **Quiz Management** - Create, read, update, delete questions & categories
- ✅ **User Management** - Profile stats, account deletion (GDPR compliance)
- ✅ **Audit Logging** - Track all user actions for security
- ✅ **Email Service** - OTP for password reset via SMTP
- ✅ **Performance Caching** - 1-hour TTL for categories and questions
- ✅ **Rate Limiting** - 500 requests per 15 minutes (API security)
- ✅ **Data Synchronization** - Efficient bulk sync for offline changes
- ✅ **Security Headers** - Helmet.js for HTTP security

---

## 🛠️ Tech Stack

### Frontend
- **Language**: Kotlin
- **Platform**: Android (Jetpack Compose recommended)
- **Authentication**: Google Sign-In SDK
- **Networking**: Retrofit/OkHttp
- **Local Storage**: Room Database
- **Sync**: WorkManager (background jobs)

### Backend
- **Runtime**: Node.js (v18+)
- **Framework**: Express.js v5
- **Database**: PostgreSQL
- **Authentication**: JWT (jsonwebtoken)
- **Email**: Nodemailer
- **Security**: Helmet, bcryptjs, express-rate-limit
- **Caching**: node-cache
- **Logging**: Winston

---

## 📋 Prerequisites

### For Backend
- Node.js 18+ and npm
- PostgreSQL 12+
- Gmail account (for password reset emails)
- Google OAuth credentials

### For Frontend
- Android Studio 4.2+
- Android SDK (API level 21+)
- Kotlin plugin

---

## ⚙️ Backend Setup

### 1. Install Dependencies
```bash
cd backend
npm install
```

### 2. Configure Environment Variables
Create a `.env` file in the `backend/` directory:
```env
PORT=5000
NODE_ENV=development
JWT_SECRET=your_secret_key_here
JWT_EXPIRES_IN=1d

# Database Configuration
DB_CLIENT_HOST=localhost
DB_CLIENT_USER=quiz_app_client
DB_CLIENT_PASSWORD=secure_password
DB_CLIENT_NAME=quiz_app_db

DB_ADMIN_HOST=localhost
DB_ADMIN_USER=quiz_admin_user
DB_ADMIN_PASSWORD=secure_password
DB_ADMIN_NAME=quiz_app_db

# Google OAuth
GOOGLE_CLIENT_ID=your_google_client_id

# Email Service (Gmail)
EMAIL_USER=your_gmail@gmail.com
EMAIL_PASS=your_app_password
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=465
EMAIL_SECURE=true
```

### 3. Setup PostgreSQL Database
```bash
# Create the main database
psql -U postgres -d postgres -c "CREATE DATABASE quiz_app_db;"

# Load schema
psql -U postgres -d quiz_app_db < database_schema.sql

# Load sample data (optional)
psql -U postgres -d quiz_app_db < seed.sql
```

### 4. Start Backend Server
```bash
cd backend

# Development mode (with nodemon)
npm run dev

# Production mode
npm start
```

Backend will be available at: **http://localhost:5000**

---

## 📡 API Documentation

### Authentication Endpoints
```
POST /api/auth/register           - Register new user
POST /api/auth/login              - Login with email/password
POST /api/auth/google             - Google OAuth authentication
POST /api/auth/forgot-password    - Request password reset OTP
POST /api/auth/reset-password     - Reset password with OTP
DELETE /api/auth/erase            - Delete user account (GDPR)
```

### Quiz Endpoints
```
GET /api/quiz/categories          - Get all quiz categories
GET /api/quiz/questions/:id       - Get questions for a category
POST /api/quiz/submit             - Submit quiz results
POST /api/quiz/submit-detailed    - Submit detailed quiz with answers
GET /api/quiz/history             - Get user's quiz history
GET /api/quiz/leaderboard         - Get top 10 users (leaderboard)
GET /api/quiz/favourites          - Get favorite questions
POST /api/quiz/favourites         - Add question to favorites
DELETE /api/quiz/favourites/:id   - Remove from favorites
```

### Admin Endpoints
```
POST /api/admin/categories        - Create new category (admin)
POST /api/admin/questions         - Add new question (admin)
GET /api/admin/questions          - List all questions with search/filter
PUT /api/admin/questions/:id      - Update question (admin)
DELETE /api/admin/questions/:id   - Delete question (admin)
```

### User Endpoints
```
GET /api/user/stats               - Get user statistics
POST /api/user/sync               - Sync user data (level, etc.)
DELETE /api/user/account          - Delete user account
```

### Request Authentication
All protected endpoints require a JWT token in the header:
```
Authorization: Bearer <token>
```

---

## 🔐 Security Features

### Implemented Security Measures
- **JWT Authentication** - Stateless authentication with 1-day expiration
- **Password Hashing** - bcryptjs with salt rounds (10)
- **Role-Based Access Control** - Admin-only endpoints protected
- **Rate Limiting** - 500 requests per 15 minutes per IP
- **CORS** - Configured for cross-origin requests
- **Helmet.js** - HTTP security headers
- **Input Validation** - Joi for request validation
- **Audit Logging** - All user actions logged with IP address
- **SQL Injection Prevention** - Parameterized queries (PostgreSQL)
- **Separate DB Pools** - Client and Admin pools with minimal privileges

---

## 💾 Database Schema Highlights

### Core Tables
- **users** - User profiles (username, email, password_hash, role, level)
- **categories** - Quiz categories
- **questions** - Quiz questions (multiple-choice)
- **quiz_history** - User quiz submission records
- **user_answers** - Detailed answer tracking
- **user_favourites** - Bookmarked questions
- **password_reset_codes** - OTP storage for password reset
- **audit_logs** - User action logging

### Key Features
- ✅ Foreign Key Cascading - Deleting a user removes all related data
- ✅ Timestamps - created_at, updated_at for audit trail
- ✅ Indexes - On frequently queried columns for performance

---

## 🧪 Testing

### Manual API Testing with Postman
Import the included `QuizApp_Postman_Collection.json` file into Postman to test all endpoints.

### Example API Calls
```bash
# Register
curl -X POST http://localhost:5000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","password":"secure123"}'

# Login
curl -X POST http://localhost:5000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"secure123"}'

# Get Categories (with token)
curl -X GET http://localhost:5000/api/quiz/categories \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 🚀 Deployment

### Production Checklist
- [ ] Update `.env` with production values
- [ ] Set `NODE_ENV=production`
- [ ] Use strong JWT_SECRET (generate with `crypto.randomBytes(32).toString('hex')`)
- [ ] Configure PostgreSQL with proper backups
- [ ] Set up SSL certificates for HTTPS
- [ ] Use process manager (PM2, systemd) for Node.js
- [ ] Enable rate limiting (already implemented)
- [ ] Monitor logs and errors (Winston logging configured)

### Deployment Options
- **Heroku**: Add Procfile and deploy with `git push heroku main`
- **AWS**: Use EC2 + RDS for database
- **DigitalOcean**: App Platform for simplified deployment
- **Docker**: Create Dockerfile for containerization

---

## 📱 Android App Integration

### Backend URL Configuration
In your Android app, configure the backend base URL:
```kotlin
const val API_BASE_URL = "http://192.168.x.x:5000"  // For testing
const val API_BASE_URL = "https://yourdomain.com"    // For production
```

### Key Implementation Points
1. **Store JWT Token** - Save token in SharedPreferences or DataStore
2. **Add Token to Requests** - Include in Authorization header
3. **Handle Token Expiry** - Refresh token or prompt re-login
4. **Offline-First Sync** - Use Room DB + WorkManager for background sync
5. **Google Sign-In** - Pass idToken to `/api/auth/google` endpoint

---

## 🐛 Troubleshooting

### Common Issues

**Port 5000 already in use**
```bash
lsof -i :5000  # Find process
kill -9 <PID>  # Kill process
```

**Database connection error**
- Verify PostgreSQL is running: `pg_isready`
- Check credentials in `.env`
- Ensure database exists: `createdb quiz_app_db`

**Email not sending**
- Enable "Less secure app access" in Gmail settings
- Use Google App Password instead of main password
- Check EMAIL_USER and EMAIL_PASS in `.env`

**JWT token invalid**
- Verify JWT_SECRET matches on encode/decode
- Check token hasn't expired (JWT_EXPIRES_IN)
- Ensure "Bearer" prefix in Authorization header

---

## 📝 License

This project is open-source and available under the MIT License.

---

## 👥 Contributors

- **Esrael Bekele** - Developer
- [@esrael-v7](https://github.com/esrael-v7)

---

## 📞 Support

For issues or questions:
1. Check the [Troubleshooting](#-troubleshooting) section
2. Review GitHub Issues
3. Contact the development team

---

## 🎯 Future Enhancements

- [ ] GraphQL API alternative
- [ ] Real-time notifications (WebSockets)
- [ ] Advanced analytics dashboard
- [ ] Machine learning for personalized questions
- [ ] Mobile app for iOS
- [ ] Multi-language support
- [ ] Blockchain certification (blockchain-based badges)

---

**Last Updated**: June 2026  
**Version**: 1.0.0
