# GameBoost - Gaming Coaching Marketplace

## Overview

**GameBoost** is an Uber-like mobile platform that connects players who want to improve (Beneficiaries) with high-level gamers (Coaches). The platform emphasizes quality through human validation of experts and direct communication.

## Project Vision

- **For Players**: Find and book coaching sessions with verified high-level players to improve their gaming skills
- **For Coaches**: Earn money by sharing expertise with players who want to learn
- **For Admins**: Moderate coaches, manage users, and monitor platform activity

## Key Features

### Player Features
- Browse and search for verified coaches filtered by game and rank
- Request coaching sessions with flexible scheduling
- Direct messaging with coaches
- Session management (track requests, accepts, completions)
- Rate and review coaches
- View session history

### Coach Features
- Create a profile with credentials and hourly rate
- Apply to become a verified coach (requires admin approval)
- Accept or decline session requests
- Direct messaging with players
- Track earnings and session history
- Manage coaching availability

### Admin Features
- Review and approve/reject new coach applications
- Manage all users on the platform
- View platform statistics and activity dashboard
- Moderate platform content

## Architecture

The application follows a **4-Tier N-Tier Architecture**:

```
┌─────────────────────────────────────┐
│   Presentation Layer (Mobile App)   │
│         (Android - Flutter/etc)     │
├─────────────────────────────────────┤
│      Service Layer (REST API)       │
│    (Node.js/Python/Java/PHP)        │
├─────────────────────────────────────┤
│   Business Logic Layer (Core)       │
│  (Rules, validations, workflows)    │
├─────────────────────────────────────┤
│  Data Access Layer (Database)       │
│  (SQLite/PostgreSQL/MySQL/MongoDB)  │
└─────────────────────────────────────┘
```

## Technology Stack

### Backend
- **Runtime**: Node.js (Express) / Python (Django, Flask) / Java (Spring Boot) / PHP (Laravel)
- **Database**: SQLite / PostgreSQL / MySQL / MongoDB
- **Authentication**: JWT (HS256)
- **Security**: bcrypt password hashing

### Frontend (Mobile - Android)
- **Framework**: Flutter / React Native / Android Native (Kotlin/Java)
- **State Management**: Provider / Riverpod / GetX / Bloc / MobX
- **HTTP Client**: Dio / http / Retrofit / Axios
- **Storage**: SharedPreferences / Hive / SQLite / AsyncStorage
- **UI**: Material Design / Cupertino / Custom Design System

## API Overview

The backend exposes 20 RESTful API endpoints across 6 modules:

### Authentication Module
- User registration (Player/Coach)
- User login with JWT token generation
- Get/Update user profile

### Coach Discovery Module
- List all approved coaches
- Get detailed coach information
- Update coach profile (for coaches)

### Session Management Module
- Request coaching session
- Get sessions list (for player and coach)
- Accept session request (coach action)
- Complete session (coach declares end)
- Confirm session completion (player confirms)

### Payments & Reviews Module
- Process payments (simulated for MVP)
- Submit session review and rating
- Get coach reviews and ratings

### Chat Module
- Send messages between player and coach
- Get message history
- Real-time messaging support

### Admin Module
- List pending coach applications
- Approve/reject coach applications
- Get platform statistics
- Manage users

## Database Schema

The platform uses 5 core tables:

1. **Users**: Player, Coach, and Admin accounts
2. **Coach_Profiles**: Extended coach information (bio, rank, hourly rate, approval status)
3. **Sessions**: Coaching session records with status tracking
4. **Messages**: Chat messages between players and coaches
5. **Reviews**: Session ratings and feedback

## User Roles

- **JOUEUR (Player)**: Beneficiary seeking coaching
- **COACH**: Service provider offering coaching
- **ADMIN**: Platform administrator with moderation powers

## Authentication & Security

- **JWT Token**: 24-hour validity, stored securely in .env
- **Password Hashing**: bcrypt with 10 salt rounds
- **Protected Endpoints**: All endpoints except registration and login require valid JWT
- **Role-Based Access**: Different permissions based on user role

## Session Workflow

```
REQUESTED → ACCEPTED → COMPLETED → CONFIRMED → PAID
```

A coaching session follows this state machine:
1. Player requests a session (REQUESTED)
2. Coach accepts the request (ACCEPTED)
3. Coach marks session as complete (COMPLETED)
4. Player confirms completion (CONFIRMED)
5. Payment is processed (PAID)

## Mobile Interface

The Android application includes **28 screens** organized as follows:

### Common Screens (All Users)
- Splash Screen
- Login / Register
- User Profile & Profile Editing
- Settings

### Player Screens (9 screens)
- Home Dashboard
- Coach List & Search
- Coach Details
- Session Request
- My Sessions
- Session Details
- Chat
- Review & Rating

### Coach Screens (9 screens)
- Home Dashboard
- Coach Profile & Editing
- Session Management
- Session Details
- Chat
- Earnings Dashboard
- Reviews & Ratings

### Admin Screens (4 screens)
- Dashboard
- Pending Coaches
- Coach Details & Approval
- Users Management




## Documentation

For detailed technical specifications, refer to:
- **SystemDesign.md**: High-level project architecture and design
- **backend.md**: Backend implementation details, API contracts, database schema
- **frontend.md**: Frontend specifications, screen details, UI/UX guidelines

## License

This is an academic project.

---

**Last Updated**: February 2026
