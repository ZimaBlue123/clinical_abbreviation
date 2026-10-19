---
name: "clinical-abbreviation-pipeline"
description: "End-to-end data synchronization, validation, and Android release compilation for the Clinical Abbreviation project."
version: "1.0.0"
author: "ZimaBlue123"
tags:
  - "android"
  - "data-pipeline"
  - "automation"
  - "clinical"
---

# Clinical Abbreviation Pipeline Skill

## Overview
This skill governs the data lifecycle and continuous build process for the Clinical Abbreviation Android Dictionary application. It covers CSV data hygiene, JSON asset compilation, and ProGuard-optimized signed APK generation.

---

## Pre-flight Check (SOP)
Before executing any data sync or release compilation, ensure the following prerequisites are met:

1. **Environment Verification**:
   - [ ] Java Development Kit (JDK 17+) is installed and accessible via `JAVA_HOME`.
   - [ ] Android SDK (API 34/36) build-tools are present.
   - [ ] Python 3.8+ runtime is available.
2. **Data Integrity Check**:
   - [ ] Source dataset (`abbreviation list.csv`) exists and is encoded in UTF-8.
   - [ ] Required columns (`全称`, `缩写`, `译文`) are present.
3. **Configuration Check**:
   - [ ] `app/build.gradle.kts` release signing config is set.
   - [ ] `app/proguard-rules.pro` retains model fields and generic signatures (`-keepattributes Signature`).

---

## Execution Workflow

### Step 1: Data Normalization and Sync
Run the automated data pipeline tool:
```bash
python _tools/sync_abbreviations.py
```
- Converts non-breaking whitespace (`\xa0`) and unicode hyphens (`\u2011`).
- Generates clean JSON output at `app/src/main/assets/abbreviations.json`.

### Step 2: Android Build and R8 Optimization
Trigger the Gradle release assembly:
```bash
python _tools/build_apk.py
```
Or directly via Gradle wrapper:
```powershell
.\gradlew.bat assembleRelease
```

---

## Post-execution Validation (SOP)
Verify the final deliverables after pipeline execution:

1. **Asset Consistency**:
   - [ ] Asset JSON exists and item count matches CSV row count (>= 320 items).
   - [ ] Unit tests pass: `.\gradlew.bat test`.
2. **APK Output Verification**:
   - [ ] Release APK is produced at `app/build/outputs/apk/release/app-release.apk`.
   - [ ] APK is signed (V2 signature verified via `apksigner verify -v <apk>`).
   - [ ] APK file size is within optimal range (< 5MB).
