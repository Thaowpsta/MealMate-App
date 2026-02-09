# MealMate

**MealMate** is a comprehensive Android meal planning and recipe discovery application. It allows users to search for recipes, view detailed cooking instructions, manage a weekly meal plan, and save favorite dishes. The app supports user authentication, offline caching, and a personalized experience with support for dark mode and localization (English/Arabic).

## 📱 Features

* **Recipe Discovery**: Browse meals by Category, Area (Cuisine), or Main Ingredient.
* **Smart Search**: Search for specific meals with robust filtering options.
* **Meal Planning**: Plan your breakfast, lunch, and dinner for the entire week.
* **Favorites**: Save your best-loved recipes for quick access.
* **Detailed Views**: View ingredients, measurements, step-by-step instructions, and watch YouTube cooking tutorials directly in the app.
* **User Authentication**:
* Sign up/Login with Email & Password.
* Social Login (Google & Facebook).
* Guest Mode access for quick exploration.


* **Offline Support**: Caches favorites and meal plans using a local database (Room) for offline access.
* **Personalization**:
* Dark/Light Theme toggle.
* Localization support (English & Arabic).



## 🛠 Tech Stack & Libraries

* **Language**: Java
* **Architecture**: MVP (Model-View-Presenter)
* **Networking**: Retrofit2 with Gson Converter
* **Asynchronous Programming**: RxJava3 & RxAndroid
* **Database**: Room Database (Local Caching)
* **Backend & Auth**: Firebase Authentication & Firebase Firestore
* **Image Loading**: Glide
* **UI Components**:
* Android Navigation Component
* Lottie Animations
* Material Design Components (Chips, Cards, BottomSheets)
* YouTube Player API


* **Social SDKs**: Google Sign-In, Facebook Login

## 🚀 Getting Started

### Prerequisites

* Android Studio Iguana or newer.
* JDK 17 or newer.
* A Firebase Project for backend services.

### Installation

1. **Clone the Repository**
```bash
git clone https://github.com/Thaowpsta/MealMate.git

```


2. **Firebase Setup**
* Go to the [Firebase Console](https://console.firebase.google.com/).
* Create a new project.
* Add an Android app with the package name: `com.example.mealmate`.
* Download the `google-services.json` file and place it in the `app/` directory of the project.
* **Authentication**: Enable **Email/Password**, **Google**, **Facebook**, and **Anonymous** sign-in providers in the Firebase Authentication console.
* **Firestore**: Create a Firestore database and set the security rules to allow read/write for authenticated users.


3. **Google & Facebook Configuration**
* **Google**: Add your SHA-1 fingerprint to the Firebase project settings to generate the OAuth 2.0 Client ID. Update `R.string.default_web_client_id` in your `strings.xml` with the Web Client ID.
* **Facebook**: Follow the [Facebook Developers guide](https://developers.facebook.com/docs/android/getting-started/) to get your App ID and Client Token. Add these to your `strings.xml` and Manifest.


4. **Build and Run**
* Open the project in Android Studio.
* Sync Gradle files.
* Run the app on an Emulator or Physical Device.



## 📂 Project Structure

The project follows the **MVP** architecture pattern to ensure separation of concerns and testability:

* **`data`**: Handles data retrieval from Remote (Retrofit/Firebase) and Local (Room/SharedPreferences) sources.
* `network`: API interfaces and Clients.
* `db`: Room database entities (DTOs) and DAOs.
* `repositories`: Mediates between data sources and presenters.


* **`ui`**: Contains the View and Presenter layers.
* `auth`: Login and Sign-Up logic.
* `home`: Main dashboard logic.
* `search`: Search and filter implementation.
* `plans`: Weekly calendar and meal management.
* `meal_details`: Logic for displaying specific meal data and video handling.



## 🧩 API Reference

This project uses [TheMealDB](https://www.themealdb.com/api.php) for recipe data.

* Base URL: `https://www.themealdb.com/api/json/v1/1/`

## 👤 Author

**Thaowpsta**

* Mobile App Developer (Java, Flutter, Android)
* [GitHub Profile](https://www.google.com/search?q=https://github.com/Thaowpsta)

