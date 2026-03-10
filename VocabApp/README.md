# VocabDaily — Android Vocabulary Learning App

A calm, beautifully designed Android app for building daily English vocabulary habits through spaced repetition, streak tracking, and gentle daily reminders.

---

## 📱 Features

### 🗂 Word Management
- Save words with meaning, pronunciation, part of speech, example sentences, and personal notes
- Organize into 10 built-in categories (General, Business, Academic, Literature, Science, Travel, Emotions, Phrasal Verbs, Idioms, Technology)
- Search and filter your word list in real time
- Edit or delete words at any time

### 🧠 Spaced Repetition (SM-2 Algorithm)
- Full SM-2 algorithm implementation for scientifically optimal review scheduling
- Four-button rating: **Again / Hard / Good / Easy**
- Memory strength indicator (0–5 dots) shown on every card
- Words marked as "Mastered" after extended intervals
- Due words highlighted with an amber indicator

### 🎯 Practice Sessions
- Flashcard-style review with front (word) → back (meaning + example) flip
- Tap to reveal, then rate recall quality
- Session progress bar and live word counter
- Completion screen with Lottie celebration animation
- Session accuracy stats (% correct)

### 🔥 Streak System
- Daily streak counter with animated fire icon
- Streak maintained by completing any practice session
- Preserved across app restarts via DataStore
- Resets if a day is missed

### 📊 Statistics
- Total words, Mastered, Learning, and New word counts
- Mastery progress bar (% of words mastered)
- Top categories breakdown
- Powered by Room live queries

### 🔔 Daily Reminders
- WorkManager-based reliable push notifications
- User-configurable reminder time via TimePickerDialog
- Toggle notifications on/off (with permission handling for Android 13+)
- Notifications reschedule automatically after device reboot
- Motivational messages chosen randomly each day

### ⚙️ Settings
- Enable/disable daily reminder
- Choose reminder time
- Adjustable daily review goal (5–30 words, step 5)
- Stats overview (current streak, total practice days)

---

## 🏗 Architecture

```
VocabDaily/
├── app/src/main/
│   ├── java/com/vocabdaily/
│   │   ├── VocabDailyApp.kt              # Application class + notification channel
│   │   ├── data/
│   │   │   ├── model/
│   │   │   │   ├── Word.kt               # Room entity (SM-2 fields)
│   │   │   │   ├── PracticeSession.kt    # Session tracking entity
│   │   │   │   └── Achievement.kt        # Achievements entity
│   │   │   ├── db/
│   │   │   │   ├── VocabDatabase.kt      # Room database + pre-populated sample words
│   │   │   │   ├── WordDao.kt            # Word CRUD + review queue queries
│   │   │   │   ├── PracticeSessionDao.kt # Session queries
│   │   │   │   └── AchievementDao.kt     # Achievement queries
│   │   │   └── repository/
│   │   │       └── VocabRepository.kt    # Single source of truth
│   │   ├── ui/
│   │   │   ├── MainActivity.kt           # Navigation host + bottom nav
│   │   │   ├── SplashActivity.kt
│   │   │   ├── screens/
│   │   │   │   ├── HomeFragment.kt       # Dashboard
│   │   │   │   ├── HomeViewModel.kt
│   │   │   │   ├── WordListFragment.kt   # Searchable word list
│   │   │   │   ├── WordViewModel.kt
│   │   │   │   ├── AddWordFragment.kt    # Add / edit word form
│   │   │   │   ├── WordDetailFragment.kt # Full word detail view
│   │   │   │   ├── PracticeFragment.kt   # SM-2 flashcard practice
│   │   │   │   ├── PracticeViewModel.kt
│   │   │   │   ├── StatsFragment.kt      # Statistics dashboard
│   │   │   │   └── SettingsFragment.kt   # Reminders & preferences
│   │   │   └── components/
│   │   │       └── WordAdapter.kt        # RecyclerView adapter
│   │   └── utils/
│   │       ├── SpacedRepetition.kt       # SM-2 algorithm
│   │       ├── UserPreferences.kt        # DataStore wrapper
│   │       ├── ReminderWorker.kt         # WorkManager notification worker
│   │       └── BootReceiver.kt           # Reschedule on reboot
│   └── res/
│       ├── layout/                       # All XML layouts
│       ├── drawable/                     # Vector icons & backgrounds
│       ├── values/                       # Colors, themes, strings, arrays
│       ├── navigation/nav_graph.xml      # Navigation component graph
│       ├── menu/                         # Bottom nav + context menus
│       ├── anim/                         # Slide and fade animations
│       └── font/                         # Nunito font family (Google Fonts)
```

---

## 🛠 Setup Instructions

### 1. Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- Android SDK 34
- JDK 17
- Gradle 8.2.2

### 2. Clone & Open
```bash
git clone <repo-url>
cd VocabApp
```
Open in Android Studio → File → Open → select `VocabApp/`

### 3. Sync Dependencies
Android Studio will auto-sync. If not: **File → Sync Project with Gradle Files**

Key dependencies added via `app/build.gradle`:
- `androidx.room:room-runtime:2.6.1` — Local database
- `androidx.work:work-runtime-ktx:2.9.0` — Scheduled notifications
- `com.airbnb.android:lottie:6.3.0` — Celebration animations
- `androidx.datastore:datastore-preferences:1.0.0` — Streak & settings storage
- `androidx.navigation:navigation-fragment-ktx:2.7.6` — Fragment navigation

### 4. Add Lottie Animation
The completion screen uses a Lottie animation. Download a free celebration JSON from [LottieFiles.com](https://lottiefiles.com) (search "confetti" or "celebration") and save it as:
```
app/src/main/assets/celebration.json
```
Create the `assets/` folder if it doesn't exist.

### 5. Font Setup
Fonts use Android's downloadable fonts system (Google Fonts via GMS). They download on first run. If testing offline, you can bundle Nunito TTF files directly:
```
app/src/main/res/font/nunito.ttf
app/src/main/res/font/nunito_semibold.ttf
app/src/main/res/font/nunito_bold.ttf
```
Then update `font/nunito.xml` etc. to use `<font android:font="@font/nunito" />` format.

### 6. Run
Select a device/emulator running API 26+ → Click ▶ Run

---

## 🎨 Design System

| Element | Value |
|---------|-------|
| Primary | `#4A7C6F` (Sage green) |
| Secondary | `#7C6B4A` (Warm brown) |
| Background | `#F7F5F2` (Warm off-white) |
| Streak | `#E87A3A` (Warm orange) |
| Font | Nunito (400 / 600 / 700) |
| Corner radius | 12–20dp throughout |
| Card elevation | 2dp |

---

## 📐 SM-2 Spaced Repetition

Based on the SuperMemo SM-2 algorithm:

| Button | Quality | Result |
|--------|---------|--------|
| Again | 0 | Reset to 1 day |
| Hard | 2 | Slightly shorter interval |
| Good | 4 | Normal interval progression |
| Easy | 5 | Longer interval, higher ease factor |

Words are marked **Mastered** when interval ≥ 21 days and last rating ≥ Good.

---

## 🔔 Notification System

Uses `WorkManager` with `PeriodicWorkRequest` (daily period). The reminder:
1. Schedules at the user's chosen time
2. Delivers a random motivational message
3. Deep-links directly to the Practice screen
4. Reschedules after device reboot via `BootReceiver`

---

## 🚀 Future Enhancements
- Import words from CSV/text files
- iCloud/Google Drive backup
- Themes (Dark mode)
- Word of the Day widget
- Share a word card as an image
- Achievement badge animations
- Haptic feedback on card flip
