# EnglishPro Dashboard - API Integration Guide

## 📋 Backend Implementation Checklist

This document outlines all the API endpoints that need to be implemented on the backend to support the Dashboard system.

## 🔐 Authentication

### 1. Login

**Endpoint:** `POST /auth/login`

**Request:**

```json
{
  "email": "admin@example.com",
  "password": "password123"
}
```

**Response (200):**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "user": {
    "id": "user123",
    "fullName": "Admin User",
    "email": "admin@example.com",
    "username": "admin",
    "phone": "0123456789",
    "role": "ADMIN"
  }
}
```

### 2. Register

**Endpoint:** `POST /auth/register`

**Request:**

```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "username": "johndoe",
  "phone": "0123456789",
  "password": "password123",
  "role": "STUDENT"
}
```

**Response (201):**

```json
{
  "id": "user456",
  "fullName": "John Doe",
  "email": "john@example.com",
  "username": "johndoe",
  "role": "STUDENT"
}
```

## 👥 Admin - User Management

### 1. Get All Users

**Endpoint:** `GET /admin/users`

**Query Parameters:**

```
page=1&limit=10&role=STUDENT&status=active&search=john
```

**Response (200):**

```json
{
  "data": [
    {
      "id": "user123",
      "fullName": "John Doe",
      "email": "john@example.com",
      "username": "johndoe",
      "phone": "0123456789",
      "role": "STUDENT",
      "status": "active",
      "createdAt": "2024-01-10T10:00:00Z"
    }
  ],
  "total": 100,
  "page": 1,
  "limit": 10
}
```

### 2. Create User

**Endpoint:** `POST /admin/users`

**Request:**

```json
{
  "fullName": "Jane Doe",
  "email": "jane@example.com",
  "username": "janedoe",
  "phone": "0987654321",
  "role": "TEACHER",
  "status": "active"
}
```

**Response (201):**

```json
{
  "id": "user789",
  "fullName": "Jane Doe",
  "email": "jane@example.com",
  "role": "TEACHER",
  "status": "active"
}
```

### 3. Update User

**Endpoint:** `PUT /admin/users/:id`

**Request:**

```json
{
  "fullName": "Jane Smith",
  "role": "TEACHER",
  "status": "active"
}
```

**Response (200):**

```json
{
  "id": "user789",
  "fullName": "Jane Smith",
  "email": "jane@example.com",
  "role": "TEACHER",
  "status": "active"
}
```

### 4. Delete User

**Endpoint:** `DELETE /admin/users/:id`

**Response (204):**

```
No content
```

### 5. Admin Dashboard Stats

**Endpoint:** `GET /admin/dashboard/stats`

**Response (200):**

```json
{
  "totalRevenue": 125000000,
  "totalUsers": 1250,
  "totalCourses": 45,
  "todayOrders": 12,
  "userStats": {
    "students": 950,
    "teachers": 280,
    "admins": 20
  },
  "recentOrders": [
    {
      "id": "ORD001",
      "user": "Nguyen Van A",
      "amount": 2500000,
      "method": "VNPay",
      "status": "Success"
    }
  ]
}
```

## 💳 Admin - Order Management

### 1. Get All Orders

**Endpoint:** `GET /admin/orders`

**Query Parameters:**

```
page=1&limit=10&status=success&method=vnpay&search=ORD001
```

**Response (200):**

```json
{
  "data": [
    {
      "id": "ORD001",
      "user": "Nguyen Van A",
      "email": "a@example.com",
      "course": "English for Beginners",
      "amount": 2500000,
      "method": "VNPay",
      "status": "success",
      "createdAt": "2024-01-15T10:00:00Z",
      "notes": "Transaction successful"
    }
  ],
  "total": 45,
  "page": 1,
  "limit": 10
}
```

### 2. Get Order Detail

**Endpoint:** `GET /admin/orders/:id`

**Response (200):**

```json
{
  "id": "ORD001",
  "user": "Nguyen Van A",
  "email": "a@example.com",
  "course": "English for Beginners",
  "amount": 2500000,
  "method": "VNPay",
  "status": "success",
  "createdAt": "2024-01-15T10:00:00Z",
  "notes": "Transaction successful"
}
```

### 3. Update Order Status

**Endpoint:** `PUT /admin/orders/:id/status`

**Request:**

```json
{
  "status": "success"
}
```

**Response (200):**

```json
{
  "id": "ORD001",
  "status": "success",
  "updatedAt": "2024-01-16T10:00:00Z"
}
```

## 📚 Teacher - Course Management

### 1. Get Teacher's Courses

**Endpoint:** `GET /teacher/courses`

**Query Parameters:**

```
status=published&search=english
```

**Response (200):**

```json
{
  "data": [
    {
      "id": "course123",
      "title": "English for Beginners",
      "description": "Basic English course",
      "price": 2500000,
      "level": "A1",
      "category": "beginner",
      "thumbnail": "https://...",
      "language": "English/Vietnamese",
      "duration": 40,
      "status": "published",
      "students": 180,
      "lessons": 24,
      "createdAt": "2024-01-10T10:00:00Z"
    }
  ]
}
```

### 2. Create Course

**Endpoint:** `POST /teacher/courses`

**Request:**

```json
{
  "title": "English for Beginners",
  "description": "Basic English course for beginners",
  "price": 2500000,
  "level": "A1",
  "category": "beginner",
  "thumbnail": "base64_or_url",
  "language": "English/Vietnamese",
  "duration": 40,
  "status": "draft",
  "skills": ["speaking", "listening", "reading", "writing"]
}
```

**Response (201):**

```json
{
  "id": "course123",
  "title": "English for Beginners",
  "status": "draft",
  "createdAt": "2024-01-10T10:00:00Z"
}
```

### 3. Update Course

**Endpoint:** `PUT /teacher/courses/:id`

**Request:**

```json
{
  "title": "English for Beginners - Updated",
  "price": 3000000,
  "status": "published"
}
```

**Response (200):**

```json
{
  "id": "course123",
  "title": "English for Beginners - Updated",
  "price": 3000000,
  "status": "published"
}
```

### 4. Delete Course

**Endpoint:** `DELETE /teacher/courses/:id`

**Response (204):**

```
No content
```

### 5. Teacher Dashboard Stats

**Endpoint:** `GET /teacher/dashboard/stats`

**Response (200):**

```json
{
  "totalStudents": 245,
  "totalCourses": 8,
  "monthlyRevenue": 45000000,
  "averageRating": 4.5,
  "recentEnrollments": [
    {
      "id": "enroll123",
      "student": "Nguyen Van A",
      "course": "English for Beginners",
      "date": "2024-01-20"
    }
  ],
  "topCourses": [
    {
      "id": "course123",
      "name": "English for Beginners",
      "enrollment": 95,
      "students": 180
    }
  ]
}
```

## 📖 Teacher - Curriculum Management

### 1. Get Course Chapters

**Endpoint:** `GET /teacher/courses/:courseId/chapters`

**Response (200):**

```json
{
  "data": [
    {
      "id": "chapter1",
      "title": "Chapter 1: Basic Greetings",
      "order": 1,
      "lessons": [
        {
          "id": "lesson1",
          "title": "Lesson 1: How to greet",
          "order": 1,
          "content": "...",
          "media": "https://..."
        }
      ]
    }
  ]
}
```

### 2. Create Chapter

**Endpoint:** `POST /teacher/courses/:courseId/chapters`

**Request:**

```json
{
  "title": "Chapter 1: Basic Greetings"
}
```

**Response (201):**

```json
{
  "id": "chapter1",
  "title": "Chapter 1: Basic Greetings",
  "order": 1
}
```

### 3. Update Chapter

**Endpoint:** `PUT /teacher/courses/:courseId/chapters/:chapterId`

**Request:**

```json
{
  "title": "Chapter 1: Advanced Greetings",
  "order": 1
}
```

### 4. Delete Chapter

**Endpoint:** `DELETE /teacher/courses/:courseId/chapters/:chapterId`

### 5. Create Lesson

**Endpoint:** `POST /teacher/courses/:courseId/lessons`

**Request:**

```json
{
  "chapterId": "chapter1",
  "title": "Lesson 1: How to greet",
  "content": "Hello everyone...",
  "media": "https://youtube.com/watch?v=...",
  "order": 1
}
```

### 6. Update Lesson

**Endpoint:** `PUT /teacher/courses/:courseId/lessons/:lessonId`

**Request:**

```json
{
  "title": "Lesson 1: Professional Greetings",
  "content": "In business...",
  "media": "https://youtube.com/watch?v=..."
}
```

### 7. Delete Lesson

**Endpoint:** `DELETE /teacher/courses/:courseId/lessons/:lessonId`

## 🏋️ Teacher - Exercise Management

### 1. Get Exercises for Lesson

**Endpoint:** `GET /teacher/courses/:courseId/lessons/:lessonId/exercises`

**Response (200):**

```json
{
  "data": [
    {
      "id": "ex1",
      "type": "multiple",
      "question": "How do you greet in the morning?",
      "options": ["Good evening", "Good morning", "Good night"],
      "correctAnswer": 1,
      "points": 10
    },
    {
      "id": "ex2",
      "type": "fillBlank",
      "question": "Nice to ____ you",
      "answer": "meet",
      "points": 5
    }
  ]
}
```

### 2. Create Exercise

**Endpoint:** `POST /teacher/courses/:courseId/lessons/:lessonId/exercises`

**Request (Multiple Choice):**

```json
{
  "type": "multiple",
  "question": "How do you greet?",
  "options": ["Good morning", "Good evening", "Good night", "Hello"],
  "correctAnswer": 0,
  "points": 10
}
```

**Request (Fill in the Blank):**

```json
{
  "type": "fillBlank",
  "question": "Nice to ____ you",
  "answer": "meet",
  "points": 5
}
```

### 3. Update Exercise

**Endpoint:** `PUT /teacher/courses/:courseId/lessons/:lessonId/exercises/:exerciseId`

### 4. Delete Exercise

**Endpoint:** `DELETE /teacher/courses/:courseId/lessons/:lessonId/exercises/:exerciseId`

## 👨‍🎓 Teacher - Student Management

### 1. Get Teacher's Students

**Endpoint:** `GET /teacher/students`

**Query Parameters:**

```
course=course123&search=john&status=active
```

**Response (200):**

```json
{
  "data": [
    {
      "id": "student1",
      "name": "Nguyen Van A",
      "email": "a@example.com",
      "course": "English for Beginners",
      "enrolledDate": "2024-01-10",
      "progress": 85,
      "score": 87,
      "status": "active",
      "coursesCompleted": 2
    }
  ],
  "total": 245
}
```

### 2. Get Student Progress

**Endpoint:** `GET /teacher/students/:studentId/progress`

**Response (200):**

```json
{
  "studentId": "student1",
  "courseId": "course123",
  "progress": 85,
  "score": 87,
  "lessons": [
    {
      "id": "lesson1",
      "title": "Lesson 1: Greeting",
      "completedAt": "2024-01-20",
      "score": 10,
      "maxScore": 10,
      "timeSpent": 45
    }
  ]
}
```

### 3. Send Message to Student

**Endpoint:** `POST /teacher/students/:studentId/message`

**Request:**

```json
{
  "content": "Great job on the last lesson!"
}
```

## 🔄 Error Responses

All endpoints should return proper error responses:

### 400 Bad Request

```json
{
  "error": "Validation error",
  "details": {
    "email": "Invalid email format"
  }
}
```

### 401 Unauthorized

```json
{
  "error": "Unauthorized access",
  "message": "Please log in first"
}
```

### 403 Forbidden

```json
{
  "error": "Access denied",
  "message": "You don't have permission to access this resource"
}
```

### 404 Not Found

```json
{
  "error": "Resource not found"
}
```

### 500 Server Error

```json
{
  "error": "Internal server error",
  "message": "Something went wrong"
}
```

## 🔐 Security Requirements

1. **Authentication**: All endpoints (except login/register) require authorization header

   ```
   Authorization: Bearer <accessToken>
   ```

2. **Authorization**: Endpoints should check user role

   ```typescript
   // Example Spring/Java
   @PreAuthorize("hasRole('ADMIN')")
   GET /admin/users

   @PreAuthorize("hasRole('TEACHER')")
   GET /teacher/courses
   ```

3. **Data Validation**: Validate all input before processing

4. **Rate Limiting**: Implement rate limiting to prevent abuse

## 📊 Pagination

All list endpoints should support pagination:

**Query Parameters:**

```
page=1          // Page number (1-indexed)
limit=10        // Items per page
sort=id:asc     // Sort by field (optional)
```

**Response Format:**

```json
{
  "data": [...],
  "pagination": {
    "total": 100,
    "page": 1,
    "limit": 10,
    "totalPages": 10
  }
}
```

## 🚀 Implementation Priority

1. **Priority 1** (Must Have)
   - Auth endpoints (login, register)
   - User CRUD endpoints
   - Course CRUD endpoints
   - Course stats

2. **Priority 2** (Should Have)
   - Chapter/Lesson management
   - Exercise management
   - Order management
   - Student management

3. **Priority 3** (Nice to Have)
   - Analytics endpoints
   - File upload endpoints
   - Message/Notification endpoints
   - Export endpoints (CSV, PDF)

## 📝 Notes for Backend Team

1. Use proper HTTP methods (GET, POST, PUT, DELETE, PATCH)
2. Use appropriate status codes (200, 201, 204, 400, 401, 403, 404, 500)
3. Return consistent error messages
4. Implement proper pagination for list endpoints
5. Add timestamps (createdAt, updatedAt) to all resources
6. Validate all input data
7. Check user permissions on every operation
8. Use transactions for multi-step operations (course creation with chapters)
9. Log all API calls for debugging
10. Test all endpoints thoroughly before release
