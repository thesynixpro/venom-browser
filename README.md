# Venom Browser

A premium, dark-themed Android web browser built with Kotlin and Jetpack Compose, featuring a distinctive floating Venom Button inspired by modern browser UX.

## Features

- **Modern UI**: Dark-first design with venom green accents (#A4F45A)
- **Venom Button**: Signature floating capsule button with radial menu for quick actions
- **WebView Engine**: Built on Android System WebView for reliable browsing
- **Tabs Management**: Multiple tabs with easy switching
- **Bookmarks & History**: Local persistence for bookmarks and browsing history
- **Smart Address Bar**: Auto-detects URLs vs search queries
- **Customizable Settings**: Themes, search engine, Venom Button position/visibility
- **Privacy Controls**: Clear browsing data, cookie management
- **Downloads**: Integrated download handling

## Architecture

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVVM pattern
- **Dependencies**: Hilt for DI, Room for local storage, DataStore for preferences
- **Browser Engine**: AndroidX WebKit / Android System WebView (with abstraction for future Chromium integration)

## Requirements

- Android Studio Hedgehog or later
- Android SDK 34
- Minimum Android version: API 24 (Android 7.0)
- Target Android version: API 34 (Android 14)

## Building

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Build and run on device or emulator

```bash
./gradlew assembleDebug
```

The APK will be generated at: `app/build/outputs/apk/debug/app-debug.apk`

## Browser Engine

This version uses Android System WebView for maximum compatibility and ease of building. The architecture includes a `BrowserEngine` abstraction to allow future integration with a custom Chromium engine if needed. For a full Chromium build, additional native dependencies, source checkout, and build infrastructure would be required.

## Known Limitations

- Uses Android System WebView (not a custom Chromium build)
- Some advanced features may depend on WebView version on device
- System-wide floating overlay requires special permissions

## License

This project is built for demonstration purposes.

## Security Notes

- HTTPS is enforced by default
- SSL certificate errors are not automatically bypassed
- JavaScript bridges are restricted
- External intents are validated
