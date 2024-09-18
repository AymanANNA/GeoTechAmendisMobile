[![Kotlin Version](https://img.shields.io/badge/Kotlin-1.9.23-blue.svg?style=flat-square)](http://kotlinlang.org/)
[![Gradle](https://img.shields.io/badge/Gradle-8.2.0-blue.svg?style=flat-square)](https://developer.android.com/build/releases/gradle-plugin)
[![API](https://img.shields.io/badge/Min%20SDK-24%20[Android%207.0]-blue.svg?style=flat-square)](https://github.com/AndroidSDKSources/android-sdk-sources-list)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-34%20[Android%2014]-blue.svg?style=flat-square)](https://developer.android.com/about/versions/13)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=flat-square)](http://www.apache.org/licenses/LICENSE-2.0)
[![Android Studio](https://img.shields.io/badge/Android-Studio-Jellyfish)](https://developer.android.com/studio/preview)

# GeoTechAmendis - Technician Check-In/Check-Out

This mobile application is part of the larger **GeoTechAmendis** project, designed for the technicians of Amendis. It allows technicians to check in and check out during their field operations. The application uses **JetPack Compose** and **Foreground Service** to capture location data, even when running in the background on devices running Android 10 and above.

## Features:
- **Real-time location tracking**: Captures technician location data while the app is in the foreground and background.
- **Check-in/Check-out system**: Technicians can easily log their presence in various locations during their shifts.
- **Background location tracking**: Even when the app is not actively open, location data is collected using background services.
  
<img src="https://github.com/AymanANNA/GeoTechAmendisMobile/app1.jpg" />

## Notification Screenshot 
<img src="https://github.com/AbdulRehmanNazar/BackgroundLocation/blob/main/screenshots/Background%20Location.jpg" width=320 />

## Permissions
From Android 10, the option **"Allow only while using the app"** has been introduced. If this option is selected, the app will not be able to track location in the background, which may hinder the check-in/check-out functionality.

<img src="https://github.com/AbdulRehmanNazar/BackgroundLocation/blob/main/screenshots/Permission%20Screen.jpg" width=320 />

## License:

This project is licensed under the Apache License, Version 2.0. See the [LICENSE](http://www.apache.org/licenses/LICENSE-2.0) file for details.
