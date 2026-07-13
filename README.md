# ⚡ HabitFlow

HabitFlow is a premium, offline-first daily habit tracking application built with modern Android technologies. Designed to help users form healthy habits, keep track of streaks, and stay consistent with their routines.

---

## ✨ Features

- **🌅 Today\'s Routine Checklist:** Dynamic dashboard grouping your habits by time-of-day slots (Morning, Afternoon, Evening, Anytime).
- **👉 Swipe-to-Complete:** Smooth swipe interactions on Today's tasks with instant tactile haptic feedback.
- **🎉 Confetti Celebrations:** Custom particle physics engine overlay that explodes when you reach 100% completion for the day.
- **↕️ Manual Reordering:** Interactive reorder mode on the Habits management tab to organize your routine priority.
- **🚀 Smart Onboarding:** Quick starter walkthrough presenting popular daily habits as suggestions to toggle and bulk-add on first launch.
- **📊 Comprehensive Stats & Heatmaps:**
  - Dynamic **Consistency Score** incorporating completion rates and active streaks.
  - Interactive **GitHub-style Heatmap Calendar** displaying completion density.
  - **Streak Leaderboard** showing your best habits.
- **📝 Log Notes:** Add and edit context notes for specific completion dates.
- **💾 Data Backup:** Export your entire database (habits and records) as a clean, standardized JSON backup.
- **📱 Home Screen Widget:** Built with modern **Jetpack Compose Glance** showing today's progress and pending routine check-ins.
- **⏰ Weekly Summaries:** Periodic Worker (`WorkManager`) scheduling summary notifications every Sunday.
- **☁️ Cloud Synchronization:** Seamless Firebase Firestore sync matching your local Room Database with Google Sign-in auth.

---

## 🛠️ Tech Stack & Architecture

- **UI Framework:** 100% Jetpack Compose using Material 3 design tokens.
- **App Widgets:** Jetpack Compose Glance.
- **Local Database:** Room (V2 schema migration path).
- **Dependency Injection:** Dagger Hilt.
- **Background Tasks:** Jetpack WorkManager.
- **Asynchronous Flow:** Kotlin Coroutines & Reactive Flows.
- **Authentication:** Android Credentials Manager API & Google Sign-In.
- **Architecture Pattern:** MVVM (Model-View-ViewModel) adhering to Clean Architecture principles.

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Koala / Ladybug or newer.
- Gradle JDK 17+.
- Android Device or Emulator running Android API 26 (Android 8.0) or higher.

### Compilation & Build

1. Clone this repository to your local machine:
   ```bash
   git clone <repository-url>
   cd HabitFlow
   ```
2. Build the debug APK using the Gradle Wrapper:
   ```bash
   ./gradlew assembleDebug
   ```
3. Run the unit/instrumented tests:
   ```bash
   ./gradlew test
   ```

---

## 📁 Project Structure

```
app/src/main/java/com/pankaj/habitflow/
│
├── data/                  # Data Layer (Room, Preferences, Repositories, WorkManager Workers)
│   ├── local/             # Room Entity structures, DAOs, AppDatabase schema definitions
│   ├── repository/        # Repository implementations mapping local Room to domain models
│   └── sync/              # Cloud Synchronization workers and schedulers
│
├── domain/                # Business Domain Layer (Models, Repositories definitions)
│   ├── model/             # Habit, Record, DayStats, Onboarding models
│   └── repository/        # Repository contracts
│
├── presentation/          # UI Presentation Layer (Screens, ViewModels, Theme)
│   ├── components/        # Reusable UI elements (Charts, Heatmaps, Confetti particle canvas)
│   ├── navigation/        # NavHost and screen router graph setup
│   ├── screen/            # Individual screens (Today, Habits, Stats, Settings, Onboarding, Detail)
│   └── theme/             # Material 3 Theme configurations, fonts, and dark mode controls
│
└── widget/                # Glance Home Screen App Widget definitions
```
