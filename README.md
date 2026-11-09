# Growora Android wrapper

This is a separate Android shell for the already-published Growora web game. It does not copy or modify the game source. The app loads the live game URL:

`https://moh22201.github.io/Growora.sunflawer/`

## What this project does

- Shows Growora inside a full-screen Android WebView.
- Enables JavaScript and DOM storage, which the web game requires.
- Keeps HTTPS-only navigation and blocks local-file access.
- Restricts in-app navigation to the Growora GitHub Pages host and its Supabase project host.
- Sends other HTTPS links to the device browser.
- Displays a retry screen if the game cannot be reached.
- Builds an APK artifact through GitHub Actions.

## Important limitations before public distribution

1. This is a connected app shell. The game needs internet access; this is not an offline game package.
2. The game itself is served from GitHub Pages. Publishing a web update changes what players see when they next load/reload the app; no new APK is needed for ordinary game-content updates.
3. The release APK produced by the workflow is unsigned. It can be installed for testing after signing with your own private Android release key. Keep that key and passwords private and backed up. Do not commit them to this repository.
4. Test Growora login, session persistence, save loading, Supabase actions, keyboard behavior, screen rotation, and external authentication flows on a real Android phone before distributing.
5. This wrapper uses the existing package ID `com.growora.game`. If you later distribute a signed build, keep the same package ID and signing key for updates.
6. No Android app signing key or credentials are included.

## Build with GitHub Actions

1. Open **Actions** and run **Build Growora Android APK**, or push a commit to `main`.
2. Download the `Growora-Android-APK` artifact from the successful workflow run.
3. Sign the APK before sharing it with players.

## Build locally

Install Android Studio with Android SDK 36 and JDK 17, open this folder, allow Gradle sync, then run:

`gradle :app:assembleDebug`

The debug APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## Project identity

- App label: Growora
- Application ID: `com.growora.game`
- Minimum Android version: Android 6.0 (API 23)
- Live game URL: `https://moh22201.github.io/Growora.sunflawer/`
