# Clinical Abbreviation (临床医学缩写词典)

[![Platform](https://img.shields.io/badge/Platform-Android%207.0%2B-green.svg)](https://developer.android.com)
[![Compile SDK](https://img.shields.io/badge/Compile%20SDK-36-blue.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

An optimized, modern Android offline dictionary application for clinical research coordinators (CRC), clinical research associates (CRA), doctors, and pharmaceutical professionals to quickly search, query, and reference medical abbreviations.

---

## 📱 Features

- **⚡ Fast Offline Search**: Instant prefix, exact match, and weighted relevance search across 320+ curated clinical abbreviations, full English forms, and Chinese translations.
- **📋 One-Tap Copy**: Tap or long-press any abbreviation card to instantly copy the complete record to the clipboard.
- **🎨 Modern Material 3 UI**: Clean Material You design with edge-to-edge system bars support, elevation, and card corner styling.
- **🛡️ Robust & Crash-Free**: Optimized with R8 ProGuard rules preserving JSON serialization, and deterministic data loading.
- **📦 Compact Binary**: Release APK footprint optimized down to ~2.2 MB.

---

## 🏗️ Project Architecture

```plaintext
clinical_abbreviation/
├── _tools/                     # Internal automation & data pipeline tools
│   ├── sync_abbreviations.py   # Cleans CSV dataset & syncs to JSON assets
│   └── build_apk.py            # Automated Gradle build & signature verification
├── skills/                     # Antigravity project skills & SOPs
│   ├── clinical-abbreviation-pipeline/
│   │   └── SKILL.md            # Skill specification & SOP check lists
│   └── SKILLS_INDEX.md         # Global skills catalog
├── app/                        # Android application module
│   ├── src/main/assets/        # Bundled clinical abbreviation JSON dataset
│   ├── src/main/java/          # Core Java source code
│   │   └── com/example/clinical_abbreviation/
│   │       ├── MainActivity.java       # Edge-to-edge UI & search controller
│   │       ├── AbbreviationAdapter.java# Weighted search & DiffUtil adapter
│   │       └── AbbreviationModel.java  # Data model with serialization rules
│   ├── src/main/res/           # Layouts, themes, and localized resources
│   └── proguard-rules.pro      # ProGuard & R8 keep rules
├── abbreviation list.csv       # Master clinical abbreviations dataset
├── build.gradle.kts            # Project-level Gradle build configuration
└── settings.gradle.kts         # Repository mirrors & plugin management
```

---

## 🚀 Building & Packaging

### Prerequisites
- JDK 17 (Temurin / OpenJDK 17)
- Android SDK (Build-Tools 34.0.0 or 36.0.0, API 36)
- Python 3.8+ (for data pipeline tooling)

### 1. Synchronize Dataset
To update or re-clean the abbreviation database:
```bash
python _tools/sync_abbreviations.py
```

### 2. Build Release APK
Run the automated build script:
```bash
python _tools/build_apk.py
```
Or directly use Gradle Wrapper:
```powershell
.\gradlew.bat assembleRelease
```
The installable, signed Release APK will be generated at:
`app/build/outputs/apk/release/app-release.apk`

---

## 📄 License
This project is licensed under the [MIT License](LICENSE).
