# 💻 CodeCollab — Collaborative Code Review & PR Management System

CodeCollab is a professional, production-ready code review and pull request management platform. It enables development teams to create repositories, open pull requests, view diffs line-by-line, leave inline comments, automatically trigger rule-based static code reviews, and track review SLA breaches.

---

### 🌐 Live Deployment Links
* **Frontend (Vercel):** [https://code-collab-one-omega.vercel.app/](https://code-collab-one-omega.vercel.app/)
* **Backend (Render):** [https://codecollab-sc8z.onrender.com](https://codecollab-sc8z.onrender.com)

---

## ✨ Key Features
1. **Core Repository & PR Management:** Create codebases, open pull requests, select reviewers, and track PR lifecycle states (Open, Reviewing, Approved, Merged, Closed).
2. **Automated Static Code Review:** An event-driven rule engine automatically scans submitted files for banned keywords, file-size limits, empty descriptions, and excessive changed file counts.
3. **Interactive Line-by-Line Commenting:** Leave inline discussions on diff lines. Team members can mark threads as resolved once feedback is addressed.
4. **SLA Breach Tracker:** Enforces a 24-hour review SLA. The system tracks review durations and triggers scheduler alerts if reviews breach the deadline.
5. **Detailed Audit Timelines:** Stores state transitions, reviews, approvals, and comments in a history log to visualize the PR lifecycle.

---

## 🛠️ Tech Stack

### Frontend
* **Core:** React 19, Vite (Fast HMR build tool)
* **Styling:** Tailwind CSS, PostCSS (v4)
* **Icons:** Lucide React
* **Client & Routing:** Axios (intercepted JWT auth), React Router DOM (v7)

### Backend
* **Core Framework:** Spring Boot 3.2.5, Java 17
* **Security:** Spring Security (Stateless JWT token authentication, bcrypt hashing)
* **Data Access:** Spring Data JPA, Hibernate ORM
* **Database:** PostgreSQL
* **Validation:** Jakarta Validation (API request DTO constraints)
* **Utilities:** Lombok, Maven build manager

---

## 📁 Code Structure

```text
codeCollab/
├── backend/
│   ├── src/main/java/com/codecollab/
│   │   ├── admin/             # Admin stats & dashboard logic
│   │   ├── audit/             # Timeline event auditing & logs
│   │   ├── auth/              # JWT filter, config, utility & controller
│   │   ├── autoreview/        # Rule-based auto review engine (Banned keywords, size rules)
│   │   ├── comment/           # Inline PR file commenting
│   │   ├── diff/              # Diff calculation strategy
│   │   ├── events/            # Spring ApplicationEvents (Audit logs, SLA, Auto-review)
│   │   ├── pullrequest/       # PR state machine, builders & controllers
│   │   ├── repository/        # Repository creation & retrieval
│   │   ├── sla/               # Background scheduler & tracker for SLA breach limits
│   │   └── user/              # User account repository & service
│   ├── src/main/resources/    # Application properties (environment configuration)
│   └── Dockerfile             # Multi-stage production Docker configuration
├── frontend/
│   ├── src/
│   │   ├── api/               # Axios instance configuration with base API routing
│   │   ├── assets/            # Static assets
│   │   ├── components/        # Reusable UI parts (DiffViewer, CommentThread, SLABadge)
│   │   ├── context/           # AuthContext (keeps track of JWT, user roles & logins)
│   │   └── pages/             # Dashboard, Repository Details, PR Details, Register, Login
│   ├── tailwind.config.js     # Layout styling setup
│   └── package.json           # Frontend npm dependencies
└── run.ps1                    # One-click runner script for Windows PowerShell
```

---

## ⚙️ How It Works
1. **Developer Registration:** A developer registers an account (Author, Reviewer, or Admin) and logs in.
2. **Repository Creation:** Authors create virtual codebases.
3. **Pull Request Submissions:** Authors create a PR, supply code diffs (original vs modified contents), and assign reviewers.
4. **Auto-Review Check:** Spring boot triggers an asynchronous auto-review event. It scores the files and lists immediate static analysis warnings.
5. **Interactive Review:** Reviewers view the diffs side-by-side, write inline comments on specific lines, approve or request changes, and resolve open comment threads.
6. **SLA Deadlines:** If the PR is not resolved or reviewed in 24 hours, the `SLAScheduler` flags the breach.

---

## 🚀 How to Run Locally

### The Quick Way (Windows PowerShell)
We have provided an automated script that installs Java 17 and Maven locally in a temporary directory (does not require admin rights or UAC elevation) and launches both servers in separate windows.

Open PowerShell in the root directory and execute:
```powershell
powershell -ExecutionPolicy Bypass -File .\run.ps1
```

---

### The Manual Way

#### Prerequisites
* Install **JDK 17**
* Install **Apache Maven 3.9+**
* Install **Node.js (v18+)**
* Running **PostgreSQL Database** instance

#### 1. Setup Backend
1. Navigate to the backend folder:
   ```bash
   cd backend
   ```
2. Configure your database credentials in `src/main/resources/application.properties` (or set the environment variables `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`).
3. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```
   *The backend will boot on port `8085`.*

#### 2. Setup Frontend
1. Navigate to the frontend folder:
   ```bash
   cd ../frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Run the development server:
   ```bash
   npm run dev
   ```
   *The frontend will boot on `http://localhost:5173`.*

---

## 🚢 Production Deployment

### Backend (Render via Docker)
* **Runtime:** Docker
* **Root Directory:** `backend`
* **Env Variables required:**
  * `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`
  * `JWT_SECRET` (Secure 256-bit token key)
  * `ALLOWED_ORIGINS` (Your Vercel URL, e.g. `https://code-collab-one-omega.vercel.app`)

### Frontend (Vercel)
* **Framework Preset:** Vite
* **Root Directory:** `frontend`
* **Env Variables required:**
  * `VITE_API_URL` (Your Render Backend URL with `/api` appended, e.g. `https://codecollab-sc8z.onrender.com/api`)
