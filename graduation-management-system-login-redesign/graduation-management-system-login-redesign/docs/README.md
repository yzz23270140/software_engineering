# Graduation Management System (Minimal Runnable)

## Backend
- Path: `backend`
- Stack: Spring Boot 3 + MyBatis Plus + MySQL

### Setup
1. Import the database schema:
   - File: `/Users/michaeltian/Desktop/软件工程课程实践/graduation_system_optimized.sql`
2. Update DB credentials in `backend/src/main/resources/application.yml`.
3. Run:
   - `mvn spring-boot:run`

API base: `http://localhost:8080`

## Frontend
- Path: `frontend`
- Stack: Vue 3 + Vite

### Setup
1. Install dependencies:
   - `npm install`
2. Run dev server:
   - `npm run dev`

Frontend base: `http://localhost:5173`

## Notes
- The login endpoint is a demo placeholder and does not validate credentials.
- The pages are minimal CRUD forms to verify the main flow.
