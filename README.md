# FaceOnLive — ID Recognition SDK for Android

On-device ID document recognition (MRZ, OCR, and portrait extraction) for Android. Scan passports, national ID cards, and driver's licenses entirely on the device — no data leaves the phone.

> Part of the [FaceOnLive](https://faceonlive.com) on-premises biometric SDK suite.

---

## Features

- **ID document recognition** — extract text fields, MRZ, and the portrait photo from an ID.
- **On-device & offline** — all processing runs locally; no network calls, no cloud.
- **Camera & gallery input** — recognize from a live camera frame or an existing image.
- **Structured JSON output** — document type, holder details, MRZ, and cropped images.

## Requirements

| | |
|---|---|
| Min Android SDK | 24 (Android 7.0) |
| Compile / Target SDK | 34 |
| Language | Kotlin / Java |
| IDE | Android Studio (Giraffe or newer) |
| Permissions | `CAMERA` (for live capture) |

## Project structure

```
ID_Recognition_SDK_Android/
├── app/                     # Demo application
│   └── src/main/java/com/bio/idcardrecognition/
│       ├── Config.kt        # ← put your license key here
│       ├── MainActivity.kt  # Entry point: activate, init, pick image
│       ├── CameraActivityKt.kt  # Live camera recognition
│       └── ResultActivity.kt    # Renders the JSON result
├── libidsdk/                # The ID Recognition SDK (idsdk.aar)
└── libfotoapparat/          # Camera helper library
```

## Setup

1. Open the project in **Android Studio**.
2. Get a license key — start a free trial at **https://faceonlive.com**.
3. Open `app/src/main/java/com/bio/idcardrecognition/Config.kt` and set your key:
   ```kotlin
   object Config {
       const val LICENSE_KEY = "PASTE_YOUR_LICENSE_KEY_HERE"
   }
   ```
4. Build and run on a physical device (recommended for camera use).

## Quick start

```kotlin
import com.bio.idsdk.IDSDK

// 1. Activate + initialize (once, e.g. in onCreate)
if (IDSDK.setActivation(Config.LICENSE_KEY) == IDSDK.SDK_SUCCESS) {
    IDSDK.init(context)
}

// 2. Recognize an ID document from a Bitmap
val json: String? = IDSDK.idcardRecognition(bitmap)

// 3. Parse the result
json?.let {
    val result = JSONObject(it)
    val name = result.optString("Full Name")
    val docType = result.optString("Document Name")
}
```

## API reference

`com.bio.idsdk.IDSDK`

| Method | Description | Returns |
|---|---|---|
| `setActivation(license: String): Int` | Activate the SDK with your license key. Call once before `init`. | `SDK_SUCCESS` on success, else an error code |
| `init(context: Context): Int` | Load the recognition models. Call once after activation. | `SDK_SUCCESS` on success |
| `idcardRecognition(bitmap: Bitmap): String?` | Recognize an ID document in the image. | JSON result string, or `null` if nothing was detected |
| `yuv2Bitmap(frame: ByteArray, width: Int, height: Int, rotation: Int): Bitmap` | Convert a camera YUV frame to a `Bitmap` for live recognition. | `Bitmap` |

**Constants**

| Constant | Meaning |
|---|---|
| `IDSDK.SDK_SUCCESS` | Operation succeeded (`0`). Any other value is an error. |

### Result format

`idcardRecognition()` returns a JSON string:

```json
{
  "Document Name": "Passport",
  "Issuing State Code": "USA",
  "Full Name": "JOHN DOE",
  "Position": "…",
  "Quality": "…",
  "MRZ": { "…": "…" },
  "Images": {
    "Portrait": "<base64 JPEG>",
    "Document": "<base64 JPEG>"
  }
}
```

If no document is detected, `Document Name` is `"Unknown"`.

## License & activation

This SDK requires a valid license key. Get a free trial or a commercial license at **[faceonlive.com](https://faceonlive.com)**. Do **not** commit your license key or signing keystore to version control.

## Support

- 🌐 Website: https://faceonlive.com
- ✉️ Email: contact@faceonlive.com

## 📦 Full SDK download
This repository contains the source/demo code only. Download the complete SDK — engine libraries and models, with full project structure — from the [Releases](../../releases) page and extract it over this project.
