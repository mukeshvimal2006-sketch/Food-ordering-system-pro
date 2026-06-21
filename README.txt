============================================================
 TastyHub PRO - Online Food Ordering System (Animated Edition)
 Java Swing + JDBC + MySQL | Final Year Academic Project
============================================================

This is the upgraded, animated edition of the TastyHub project: a richer
visual experience with a splash screen, animated gradient backgrounds,
drifting/rotating procedurally-drawn food illustrations (no external image
files needed), card-based menu browsing with hover-lift animations, toast
notifications, and a polished sidebar-navigation dashboard for both Admin
and Customer roles.

------------------------------------------------------------
1. WHAT'S NEW vs the previous version
------------------------------------------------------------
- Animated SPLASH SCREEN on launch (rotating logo, progress bar, status text)
- Fully animated LOGIN & REGISTER screens: shifting gradient backdrop with
  softly drifting/rotating food icons, glass-card layout, fade-in entrance
- Procedurally drawn FOOD ICONS (pizza, burger, drink, dessert, salad,
  noodles, chicken, fries) - hand-coded with Java2D gradients & shapes,
  no external image/GIF files required, auto-selected per food category
- Card-based MENU BROWSING (instead of a plain table) with hover-lift
  animation and a styled "+ Add" button per item
- Animated TOAST NOTIFICATIONS ("Item added to cart") that slide in/out
  instead of plain popup dialogs
- Sidebar NAVIGATION layout (CardLayout) for both Admin and Customer
  consoles, replacing the old tab strip
- Rounded, shadowed CARD COMPONENTS throughout (stat cards, tables, dialogs)
- Custom animated BUTTONS with smooth hover color/lift transitions
- Live cart counter shown directly on the "My Cart" nav button
- Reusable SpinnerIcon and CartBadge components included for further
  customization (e.g. add a loading spinner to checkout if desired)

All backend logic (database access, password hashing, order placement,
transactions) is unchanged and proven working from the previous version.

------------------------------------------------------------
2. PROJECT STRUCTURE (NetBeans Ant-based Java project)
------------------------------------------------------------
FoodOrderingSystemPro/
 |-- build.xml                 -> NetBeans Ant build file
 |-- manifest.mf                -> JAR manifest
 |-- nbproject/
 |     |-- project.xml
 |     |-- project.properties   -> main class: foodorder.ui.SplashScreen
 |       (NOTE: build-impl.xml is intentionally NOT included - NetBeans
 |        auto-generates it fresh the first time you open/build the
 |        project. Do not create a placeholder for this file.)
 |-- lib/                       -> put mysql-connector-j-8.4.0.jar here
 |-- database/
 |     |-- food_ordering_db.sql -> run this in MySQL/phpMyAdmin first
 |-- src/foodorder/
       |-- model/      -> User, FoodItem, CartItem, Order, OrderItem
       |-- dao/         -> UserDAO, FoodItemDAO, OrderDAO (JDBC logic)
       |-- util/        -> DBConnection, PasswordUtil, UIHelper, UITheme
       |-- ui/           -> SplashScreen (MAIN CLASS), LoginFrame,
                            RegisterFrame, AdminDashboardFrame,
                            CustomerDashboardFrame
       |-- ui/components/ -> FoodIcon, AnimatedGradientPanel, RoundedButton,
                              RoundedCardPanel, FoodCardPanel,
                              ToastNotification, CartBadge, SpinnerIcon

------------------------------------------------------------
3. HOW TO OPEN IN APACHE NETBEANS
------------------------------------------------------------
1. Extract this ZIP to a plain local folder (NOT inside OneDrive/Dropbox -
   cloud-sync folders can lock build files mid-compile and cause errors).
   Example: C:\Projects\FoodOrderingSystemPro
2. Open Apache NetBeans -> File -> Open Project...
3. Select the "FoodOrderingSystemPro" folder and click Open Project.
4. NetBeans will auto-generate nbproject/build-impl.xml the first time it
   scans the project - this is normal and expected.

------------------------------------------------------------
4. DATABASE SETUP (MySQL via XAMPP or MySQL Server)
------------------------------------------------------------
1. Start MySQL (via XAMPP Control Panel, or your MySQL Server service).
2. Open phpMyAdmin (http://localhost/phpmyadmin) or MySQL Workbench.
3. Run the script: database/food_ordering_db.sql
   This creates the "food_ordering_db" database, all 4 tables, and seeds:
     - Admin login    : admin@tastyhub.com   / admin123
     - Sample customer: john@example.com     / john123
     - 12 sample menu items across 5 categories
4. DBConnection.java is pre-configured for XAMPP defaults (root user, no
   password). If your MySQL setup uses a different user/password, edit:
       src/foodorder/util/DBConnection.java
   and update DB_URL / DB_USER / DB_PASS accordingly.

------------------------------------------------------------
5. ADD THE MYSQL JDBC DRIVER (REQUIRED)
------------------------------------------------------------
1. Download mysql-connector-j-8.4.0.jar directly from:
       https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.4.0/mysql-connector-j-8.4.0.jar
2. Place it inside this project's "lib" folder.
3. In NetBeans: right-click the project -> Properties -> Libraries
   -> Compile tab -> Add JAR/Folder -> select the jar from lib/.

------------------------------------------------------------
6. RUNNING THE APPLICATION
------------------------------------------------------------
1. Right-click the project -> "Set as Main Project".
2. Confirm Main Class is foodorder.ui.SplashScreen (already set in
   nbproject/project.properties).
3. Press F6 (Run Project) or click the green Run button.
4. Watch the animated splash screen, then the Login screen appears.
5. Use ONLY the main Run Project button to launch - do not try to "Run
   File" on individual classes like FoodItemDAO.java, those are support
   classes without a main() method and aren't meant to run standalone.

------------------------------------------------------------
7. KEY FEATURES (full list)
------------------------------------------------------------
- Animated splash screen with rotating logo + progress bar
- Secure login with SHA-256 password hashing
- Role-based screens (Admin vs Customer) from a single login form
- Customer registration with validation
- Animated gradient login/register backdrops with drifting food icons
- Customer: card-based menu grid with hover-lift animation, category
  badges, and procedurally drawn food illustrations
- Customer: add to cart with animated slide-in toast confirmation
- Customer: cart screen with live quantity editing, remove, clear, and
  a styled checkout dialog (address + payment method selection)
- Customer: order history with live status tracking
- Admin: animated stat dashboard (orders, pending, revenue, menu count)
  with icon-accented stat cards
- Admin: add/edit/delete menu items (with a live icon preview by category)
- Admin: view all orders, update status, inspect line items per order
- Admin: view registered customers
- Clean sidebar navigation (CardLayout) on both dashboards
- All SQL uses PreparedStatement (no SQL injection risk)
- Order placement wrapped in a JDBC transaction (commit/rollback)

------------------------------------------------------------
8. NOTES
------------------------------------------------------------
- This is a Swing DESKTOP application (not a web app).
- No external image/GIF files are required - all food illustrations and
  animations are generated entirely in code via Java2D, so the project
  has zero missing-asset risk when you share/submit the zip.
- If NetBeans ever shows "Target jar/run-single does not exist", do a
  Clean and Build (Shift+F11) once to force-regenerate build-impl.xml.
============================================================
