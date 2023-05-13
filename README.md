Storage Cleaner
=============================

## Project Setup
Give permission to manage all files on device from terminal with API >= 30:
- adb shell appops set --uid com.pg.cloudclean MANAGE_EXTERNAL_STORAGE allow

## Code Style
ktlint

## Architecture and components
- LiveData, MVVM
- Jetpack compose
- For Images: Glide-Jetpack-Compose