# EnglishPro - Online English Learning Platform

<img src="https://img.shields.io/badge/Vue-3.5-brightgreen" alt="Vue 3">
<img src="https://img.shields.io/badge/TypeScript-5.0-blue" alt="TypeScript">
<img src="https://img.shields.io/badge/Tailwind-4.0-38B2AC" alt="Tailwind CSS">
<img src="https://img.shields.io/badge/Vite-7.3-646CFF" alt="Vite">
<img src="https://img.shields.io/badge/Pinia-3.0-yellow" alt="Pinia">

A comprehensive online English learning platform with an admin dashboard, teacher management system, and student learning interface.

## 🌟 Features

### For Students

- Browse and enroll in courses
- Take online lessons and exercises
- Create and review flashcards
- Take practice exams
- Progress tracking
- Course completion certificates

### For Teachers

- Create and manage courses
- Organize curriculum
- Create and manage exercises
- Track student progress
- View performance analytics
- Student feedback and ratings

### For Admins

- User management (Admin, Teacher, Student)
- Order and payment management
- Platform analytics dashboard
- User statistics and insights

## 📋 Project Structure

```
.
├── public/                    # Static assets
├── src/
│   ├── api/                  # API calls (Axios)
│   ├── assets/               # CSS and static files
│   ├── components/           # Reusable Vue components
│   │   └── dashboard/        # Dashboard-specific components
│   ├── layouts/              # Layout components
│   ├── router/               # Vue Router configuration
│   ├── stores/               # Pinia stores
│   ├── types/                # TypeScript type definitions
│   ├── views/                # Page components
│   │   ├── dashboard/        # NEW: Dashboard admin/teacher views
│   │   ├── courses/          # Course pages
│   │   ├── exam/             # Exam pages
│   │   ├── flashcard/        # Flashcard pages
│   │   ├── cart/             # Shopping cart
│   │   ├── checkout/         # Checkout
│   │   ├── payment/          # Payment result
│   │   ├── user/             # User authentication
│   │   └── home/             # Home page
│   ├── App.vue               # Root component
│   └── main.js               # Entry point
├── DASHBOARD_SETUP.md        # NEW: Setup guide
├── DASHBOARD_SUMMARY.md      # NEW: Implementation summary
├── API_INTEGRATION.md        # NEW: API specification
├── package.json              # Dependencies
├── tsconfig.json             # TypeScript config
├── vite.config.js            # Vite config
└── README.md                 # This file
```

## 🚀 Quick Start

### Prerequisites

- Node.js 20.19.0 or higher
- npm or yarn

### Installation

1. Clone the repository

```bash
git clone <repository-url>
cd EnglishProFE
```

2. Install dependencies

```bash
npm install
```

3. Set up environment variables

```bash
# Create .env file and configure API endpoints
cp .env.example .env
```

4. Start development server

```bash
npm run dev
```

5. Build for production

```bash
npm run build
```

6. Preview production build

```bash
npm run preview
```

## 📚 Documentation

### Main Documentation Files

- **[DASHBOARD_SETUP.md](./DASHBOARD_SETUP.md)** - Dashboard setup, configuration, and usage guide
- **[DASHBOARD_SUMMARY.md](./DASHBOARD_SUMMARY.md)** - Complete implementation summary
- **[API_INTEGRATION.md](./API_INTEGRATION.md)** - Backend API specification
- **[src/views/dashboard/README.md](./src/views/dashboard/README.md)** - Dashboard features documentation

### Key Sections

1. [Dashboard System](./DASHBOARD_SETUP.md#🎨-component-usage-examples)
2. [Authentication & Roles](./DASHBOARD_SETUP.md#🔐-authentication--role-system)
3. [Component Library](./DASHBOARD_SETUP.md#🎨-component-usage-examples)
4. [API Integration](./API_INTEGRATION.md)

## 🎯 Dashboard System

### Admin Dashboard (`/dashboard/admin`)

Access restricted to users with `ADMIN` role

**Features:**

- Dashboard overview with key metrics
- User management (CRUD operations)
- Order and payment management
- Platform analytics

**Sub-pages:**

- `/dashboard/admin` - Overview
- `/dashboard/admin/users` - User management
- `/dashboard/admin/orders` - Order management

### Teacher Dashboard (`/dashboard/teacher`)

Access restricted to users with `TEACHER` role

**Features:**

- Dashboard with student and course stats
- Course management
- Curriculum editor
- Exercise management
- Student progress tracking

**Sub-pages:**

- `/dashboard/teacher` - Overview
- `/dashboard/teacher/courses` - Course list
- `/dashboard/teacher/courses/create` - Create course
- `/dashboard/teacher/courses/:id/edit` - Edit course
- `/dashboard/teacher/courses/:id/curriculum` - Curriculum editor
- `/dashboard/teacher/courses/:id/exercises` - Exercise management
- `/dashboard/teacher/students` - Student management

## 🏗️ Architecture

### Tech Stack

- **Frontend**: Vue 3 (Composition API)
- **Language**: TypeScript
- **Styling**: Tailwind CSS 4
- **Build Tool**: Vite
- **State Management**: Pinia
- **Routing**: Vue Router 5
- **HTTP Client**: Axios
- **UI Theme**: Dark mode (Premium SaaS style)

### Key Components

#### Reusable Components (10+)

- `DashboardLayout` - Main layout wrapper
- `DashboardSidebar` - Dynamic navigation
- `DashboardHeader` - Header with profile
- `DataTable` - Sortable, filterable table
- `StatCard` - Statistics display
- `Modal` - Dialog component
- `Button` - Button with variants
- `FormInput` - Form input with validation
- And more...

#### Stores (State Management)

- `auth.ts` - Authentication and user info
- `toast.ts` - Toast notifications
- `cart.ts` - Shopping cart
- `exam.ts` - Exam data

#### API Modules

- `axios.ts` - Axios configuration
- `courses.ts` - Course API calls
- `exam.ts` - Exam API calls
- `examples.ts` - Example API calls
- `flashcard.ts` - Flashcard API calls
- `mockExamData.ts` - Mock exam data
- `mockFlashcardData.ts` - Mock flashcard data
- `orders.ts` - Order API calls

## 🔐 Security Features

### Authentication

- JWT token-based authentication
- Refresh token mechanism
- Secure password handling

### Authorization

- Role-based access control (RBAC)
- Route guards
- Component-level permission checks

```typescript
// Routes are protected by role
{
  path: '/dashboard/admin',
  meta: { requiresAuth: true, roles: ['ADMIN'] }
}
```

### User Roles

- **ADMIN** - Full platform access
- **TEACHER** - Course management access
- **STUDENT** - Learning access

## 🎨 UI/UX Design

### Design Philosophy

- Modern, professional SaaS aesthetic
- Dark mode as primary theme
- Accessibility-first approach
- Mobile-responsive

### Color Palette

- Primary: Blue (#3B82F6) + Indigo (#6366F1)
- Success: Green (#10B981)
- Warning: Yellow (#F59E0B)
- Error: Red (#EF4444)
- Backgrounds: Dark grays (#111827, #1F2937)

### Typography

- Font: Inter or Outfit (Google Fonts)
- Responsive sizing
- Clear hierarchy

### Components

- Reusable, composable components
- Consistent styling
- Smooth transitions
- Accessible by default

## 📊 State Management (Pinia)

### Auth Store

```typescript
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
auth.login({ email, password })
auth.logout()
auth.isAdmin // Check if user is admin
auth.isTeacher // Check if user is teacher
```

### Toast Store

```typescript
import { useToastStore } from '@/stores/toast'

const toast = useToastStore()
toast.success('Operation successful!')
toast.error('An error occurred')
toast.warning('Please be careful')
toast.info('Information message')
```

## 🔌 API Integration

### Backend Requirements

The following API endpoints need to be implemented:

**Admin APIs:**

- `GET /admin/users` - List users
- `POST /admin/users` - Create user
- `PUT /admin/users/:id` - Update user
- `DELETE /admin/users/:id` - Delete user
- `GET /admin/orders` - List orders
- `PUT /admin/orders/:id/status` - Update order status

**Teacher APIs:**

- `GET /teacher/courses` - List courses
- `POST /teacher/courses` - Create course
- `PUT /teacher/courses/:id` - Update course
- `DELETE /teacher/courses/:id` - Delete course
- `GET /teacher/courses/:id/chapters` - Get chapters
- `POST /teacher/courses/:id/exercises` - Create exercise
- `GET /teacher/students` - List students

See [API_INTEGRATION.md](./API_INTEGRATION.md) for complete specification.

## 🧪 Testing

### Running Tests

```bash
# Run unit tests
npm run test

# Run e2e tests
npm run test:e2e

# Run with coverage
npm run test:coverage
```

### Testing Checklist

- [ ] Role-based access control
- [ ] Form validation
- [ ] API error handling
- [ ] Toast notifications
- [ ] Responsive design
- [ ] Dark mode rendering

## 📈 Performance Optimization

- Lazy loading of routes and components
- Optimized bundle size with Vite
- CSS-in-JS for dynamic styling
- Efficient state management with Pinia
- Minimal re-renders with Vue 3 reactivity

## 🌐 Browser Support

- Chrome/Edge 88+
- Firefox 87+
- Safari 14+
- Mobile browsers (iOS Safari, Chrome Android)

## 📝 Configuration

### Environment Variables

```bash
VITE_API_BASE_URL=http://api.example.com
VITE_APP_TITLE=EnglishPro
VITE_APP_VERSION=1.0.0
```

### Build Configuration

See `vite.config.js` for Vite configuration options.

## 🚀 Deployment

### Build for Production

```bash
npm run build
```

### Deploy

1. Build the project: `npm run build`
2. Deploy `dist` folder to your hosting
3. Configure API endpoints
4. Set up environment variables

### Deployment Platforms

- Vercel
- Netlify
- AWS S3 + CloudFront
- GitHub Pages

## 📚 Learning Resources

- [Vue 3 Documentation](https://vuejs.org/)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)
- [Tailwind CSS](https://tailwindcss.com/docs)
- [Pinia Documentation](https://pinia.vuejs.org/)
- [Vue Router](https://router.vuejs.org/)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📋 Code Style

- Use Prettier for code formatting: `npm run format`
- Follow Airbnb JavaScript style guide
- Use TypeScript for type safety
- Write meaningful commit messages

## 🐛 Known Issues & Limitations

1. Charts are placeholders (need Chart.js integration)
2. File upload uses base64 (can be optimized)
3. Mock data in dashboard (needs API integration)
4. No real-time updates (can add WebSocket)

## 🗺️ Future Roadmap

- [ ] Chart.js integration
- [ ] Real-time notifications
- [ ] Advanced search filters
- [ ] Bulk operations
- [ ] CSV/PDF export
- [ ] Two-factor authentication
- [ ] Activity logging
- [ ] Advanced analytics

## 📞 Support & Contact

- Email: support@englishpro.com
- Documentation: See [DASHBOARD_SETUP.md](./DASHBOARD_SETUP.md)
- Issues: [GitHub Issues](https://github.com/your-repo/issues)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](./LICENSE) file for details.

## 🙏 Acknowledgments

- Vue.js team for the amazing framework
- Tailwind CSS for the utility-first CSS framework
- Contributors and community members

---

**Framework Version**: Vue 3.5.30  
**Build Tool**: Vite 7.3.1  
**Last Updated**: January 2024  
**Status**: Production Ready
