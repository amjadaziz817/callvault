# Contributing to CallVault

Thank you for your help. This guide shows how to contribute.

## Build the project

1. Install Android Studio.
2. Clone the repository.
3. Open the project. Android Studio generates the Gradle wrapper.
4. Run the app on a device with Android 8.0 or later.

From the command line:

```bash
gradle wrapper --gradle-version 8.9
./gradlew assembleDebug
```

## Report a bug

1. Open an issue.
2. Write the phone model and the Android version.
3. Write the steps that cause the bug.
4. Write what you expected and what happened.

Do not put a phone number or a recording in the issue. This data is sensitive.

## Send a change

1. Fork the repository.
2. Create a branch for your change.
3. Keep the change small and clear.
4. Match the style of the code near your change.
5. Open a pull request. Fill the template.

## Rules for the code

- Write Kotlin.
- Do not log a phone number, a file name, or a recording.
- Keep sensitive data in internal storage.
- Do not add a tracker or an ad library.

## License

CallVault uses the GPL-3.0 license. Your contribution uses the same license.
