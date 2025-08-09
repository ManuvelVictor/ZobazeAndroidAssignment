# ZobazeAndroidAssignment

Smart Daily Expense Tracker (AI-First, Jetpack Compose, MVVM, Hilt, Room)

App Overview (2–3 lines)
- A modern expense tracking module for small business owners to quickly add, view, analyze, and export daily expenses.
- Built with Jetpack Compose, MVVM, Hilt (DI), Room (local DB), Navigation, and Material 3. Supports receipt attachment, category picker via bottom sheet, reactive totals, charts, and CSV/PDF (mock) export with share.

AI Usage Summary (3–5 lines)
- Used ChatGPT/Copilot to scaffold MVVM structure, Compose screens (Entry, List, Report), and Hilt/Room DI setup with KSP.
- Iterated prompts to modernize UI: Material3 TopAppBar, Bottom Navigation, Category Bottom Sheet, Snackbar, and Lottie empty state.
- Leveraged AI to design reactive flows (StateFlow) for today totals, daily expenses, category totals, and last-7-days chart data, plus export (CSV/PDF mock) and sharesheet integration.
- Employed prompt retries for dependency versions via TOML, icon/import fixes, and resolving Hilt duplicate binding errors.

Prompt Logs (key prompts and fixes)
- “Build a Full-featured Smart Daily Expense Tracker Module for Small Business Owners… use Jetpack Compose MVVM”
- “Use Hilt with KSP for the DB and ViewModel object creation”
- “DAO and Database related — there is no file based on that”
- “Do I need two files like ExpenseEntity and Expense or is that the same?”
- “Make screens modern, add app bar and bottom navigation, fix SmallTopAppBar/ReceiptLong errors”
- “Provide libs.versions.toml for Hilt/Room/KSP/Compose”
- “Use Hilt in MainActivity and ViewModels; remove manual ViewModel creation”
- “Fix Dagger DuplicateBindings for ExpenseDao” (merged modules into single AppModule)
- “List not loading: switch to date BETWEEN start/end; use hiltViewModel; Room-backed flows”
- “Replace Toast with Snackbar, add category bottom sheet with single-select, SAF receipt picker”
- “Report screen: charts + CSV/PDF export via SAF + sharesheet”
- “List screen: date picker bottom sheet, grouping toggle (time/category), Lottie empty state”

Checklist of Features Implemented
Core Screens and Flows
- Expense Entry Screen:
  - Inputs: Title, Amount (₹), Category (mock list: Staff, Travel, Food, Utility), Notes (max 100 chars)
  - Receipt image: pick from device (SAF, URI stored)
  - Category selection: scrollable single-select modal bottom sheet
  - Submit: validates inputs, inserts into Room via Hilt-injected repository, shows Snackbar, subtle submit animation
  - Real-time “Total Spent Today” at top (StateFlow)
- Expense List Screen:
  - Default: Today’s expenses
  - Previous dates: Material3 DatePicker in modal bottom sheet
  - Grouping: Toggle between Time and Category
  - Totals: Count and total amount header
  - Empty state: Centered Lottie animation (res/raw/empty.json)
- Expense Report Screen:
  - Mock report for last 7 days: daily totals + category-wise totals (reactive from DB)
  - Mock charts: Category bar chart and Last-7-days bar chart (Compose-based)
  - Export: CSV and mock PDF via Storage Access Framework (CreateDocument)
  - Share: Sharesheet for last exported file (CSV/PDF)

Architecture and Data
- MVVM with reactive StateFlow
- Hilt (DI) + KSP for compiler processing
- Room database:
  - ExpenseEntity, ExpenseDao (insert, get between dates, totals)
  - Query by day using BETWEEN startOfDay and endOfDay
- Repository: maps Entity  Domain, exposes Flows and suspend insert
- Navigation Compose for multi-screen flow

UI/UX
- Material 3: TopAppBar, NavigationBar, Buttons, TextFields, Snackbars
- Bottom Navigation with three tabs: Entry, List, Report
- Category picker: modern bottom sheet with radio-style single select
- Lottie empty state for List screen

Permissions and Storage
- Manifest includes media-read permissions (scoped on modern Android) and INTERNET (optional)
- SAF for picking receipt (image/pdf) and creating export files (CSV/PDF mock)
- Optional share via ACTION_SEND with URI grant

Build/Tooling
- Version catalogs (libs.versions.toml) for AGP, Kotlin 2.2.0, Compose BOM, Hilt 2.57, Room 2.7.x, KSP
- No kapt; KSP used for Hilt and Room
- @AndroidEntryPoint on MainActivity; @HiltAndroidApp Application; AppModule provides DB/DAO/Repository as @Singleton

Nice-to-haves and Bonus
- Charts are mocked with Compose drawing; can be swapped with a chart lib
- Edge-to-edge and Material3 theming ready
- Easy to extend to Room migrations, PDF generator, or offline sync

How to Run (quick)
- Ensure libs.versions.toml includes Compose BOM, Hilt, Room, KSP, Lottie Compose
- Add Application class with @HiltAndroidApp and register in AndroidManifest
- Put an empty-state Lottie file at res/raw/empty.json (or adjust reference)
- Build and run; use bottom nav to switch screens. Add expenses in Entry; see them populate List and Report.

Export/Share Notes
- Export CSV/PDF mock: prompts file creation via SAF and writes content
- Share uses granted read URI permission to share the exported file

If you need a zipped project structure summary or actual file paths for each class (AppModule, Dao, Entity, Repository, ViewModel, Screens), say the word and I’ll add a compact tree with file snippets.
