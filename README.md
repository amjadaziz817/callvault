# CallVault — Automatic Call Recorder for Android (No Root)

**CallVault is a free and open source automatic call recorder for Android. It records phone calls without root.** It uses an Android accessibility service, so it keeps recording when the system blocks normal apps.

> Keywords: call recorder app, automatic call recorder, record phone calls android, call recorder for android without root, free call recorder app, open source call recorder.

## Features

- Records every phone call automatically.
- Works without root on many Android phones.
- Records both sides of the call when the phone allows it.
- Saves each recording with the phone number and the time.
- Lists all recordings in a simple screen.
- Plays, shares, and deletes a recording.
- Stores files on the device. No account and no server.
- Free and open source under the GPL-3.0 license.

## Why CallVault

Since Android 10, the system gives silence to normal apps that record a call. Since 2022, Google Play bans call recorder apps that use the accessibility method. CallVault is **not** on Google Play. You install it from this repository or from F-Droid. So it can use the accessibility method and record your calls.

## How it works

1. You turn on the CallVault accessibility service once.
2. The app detects the start and the end of each call.
3. The app records the call with the device microphone.
4. The app can turn on the speaker, so the microphone also picks up the other person.
5. The app saves the file and shows it in the list.

**The recording quality depends on your phone model.** On some phones the app records both voices clearly. On other phones it records only your voice unless the speaker is on. Test on your phone first.

## Install

You need Android 8.0 (API 26) or later.

1. Download the APK from the [Releases](../../releases) page.
2. Allow install from unknown sources.
3. Open the app and follow the setup screen.
4. On Android 13 or later, open the app info page and tap **Allow restricted settings**. Then turn on the accessibility service.

## Build from source

```bash
git clone https://github.com/amjadaziz817/callvault.git
cd callvault
# Generate the Gradle wrapper JAR once (not stored in git):
gradle wrapper --gradle-version 8.9
./gradlew assembleDebug
```

If you open the project in Android Studio, the IDE generates the wrapper JAR for
you, and you do not need the `gradle wrapper` step.

The APK is in `app/build/outputs/apk/debug/`.

## Releasing

The release workflow builds a **signed release APK**. Add these GitHub secrets
first (repo Settings, then Secrets and variables, then Actions):

| Secret | Value |
|---|---|
| `KEYSTORE_BASE64` | Your keystore file, encoded with `base64`. |
| `KEYSTORE_PASSWORD` | The keystore password. |
| `KEY_ALIAS` | The key alias. |
| `KEY_PASSWORD` | The key password. |

Create a keystore once:

```bash
keytool -genkey -v -keystore release.keystore -alias callvault \
  -keyalg RSA -keysize 2048 -validity 10000
base64 -i release.keystore | pbcopy   # paste into KEYSTORE_BASE64
```

Then push a tag (for example `v1.0.0`) to build and publish the release.

## Permissions

| Permission | Reason |
|---|---|
| `RECORD_AUDIO` | To record the call audio. |
| `READ_PHONE_STATE` | To detect the start and the end of a call. |
| `READ_CALL_LOG` | To read the phone number of the call. |
| `FOREGROUND_SERVICE` + `FOREGROUND_SERVICE_MICROPHONE` | To keep the recorder active during the call. |
| `POST_NOTIFICATIONS` | To show the recording notification. |
| Accessibility service | To record audio during a call on Android 10 and later. |

## Legal notice

The laws on call recording differ by country. In many places you may record a call that you take part in. In some places you must tell the other person. A secret recording can lead to a privacy dispute. **You are responsible for how you use this app.** Check your local law before you record a call.

## License

CallVault is free software under the [GNU General Public License v3.0](LICENSE).
