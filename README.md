# Aprax Browser — Android Kotlin starter

A dark, premium-feeling Android browser starter with an address/search bar, WebView browsing, quick links, back/forward, home, reload, bookmarks, sharing, external-browser handoff, and clear-browsing-data controls. The UI intentionally avoids the Venom theme and uses a restrained charcoal/lavender palette.

## Open the project
1. Extract `ApraxBrowser.zip`.
2. Open the `ApraxBrowser` folder in Android Studio.
3. Use JDK 17 and allow Gradle sync to finish.
4. Run the `app` configuration on an Android device or emulator.

## Build an APK on GitHub Actions
1. Upload the complete project folder to a GitHub repository (the `.github/workflows/build.yml` file must be included).
2. Open the repository's **Actions** tab.
3. Select **Build Android APK** and, if needed, tap **Run workflow**.
4. When the run succeeds, open it and download the `ApraxBrowser-debug-apk` artifact. Extract the downloaded artifact ZIP to get the `.apk` file.

The workflow installs JDK 17 and Gradle 8.9 directly, so this project does not need a checked-in Gradle wrapper.

## Important notes
- This project uses Android `WebView`, which uses the WebView engine supplied by the device. It is **not** a custom Chromium build. Chromium is a very large native codebase and is not embedded just by writing a Kotlin UI; a true Chromium-based Android browser needs a Chromium build/toolchain and native integration.
- Bookmarks are held in memory in this starter and reset when the app process is recreated. Add Room or DataStore persistence before release.
- Multi-tab UI, downloads, private browsing, permission prompts, popup handling, ad/tracker blocking, sync, and a production privacy policy are not implemented yet.
- WebView security defaults are retained for file access and mixed content. JavaScript and DOM storage are enabled for normal modern websites. Review these choices and implement a stricter site-permission model before publishing.
- For release builds, set up signing, test on target Android versions, review Play policy requirements, and change the application ID if desired.

## Project structure

```text
ApraxBrowser/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
└── app/
    ├── build.gradle.kts
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/aprax/browser/MainActivity.kt
        └── res/
            ├── drawable/
            ├── layout/activity_main.xml
            └── values/
```
