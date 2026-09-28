# Hayagriva - The Engineer

Android TV 3D JCB digging game for Mi TV 4X, D-pad only. A single `WebView`
Activity that hosts the whole game (Three.js scene, UI, sound) as a local
page in `assets/index.html` -- no network access needed at all, since
Three.js and both fonts are bundled directly into the app.

## How it works

- `MainActivity.java`: creates a fullscreen `WebView`, loads
  `file:///android_asset/index.html`, and translates real D-pad `KeyEvent`s
  into calls on the page's `window.JCB` interface:
  - `DPAD_LEFT` / `DPAD_RIGHT` → turn (held)
  - `DPAD_UP` → drive forward (held)
  - `DPAD_DOWN`, `DPAD_CENTER`, or `ENTER` → dig (single press)
- `assets/index.html`: the entire game -- 3D JCB backhoe loader (rear
  digging arm + front loader arm, both animated), deformable sand terrain,
  a dump truck to load, scoring with clap/confetti celebration, an
  animated title screen, and all sound synthesized live with the Web Audio
  API (no audio files, nothing to license).
- `assets/js/three.min.js`: Three.js r128 (MIT licensed), bundled so the
  app never needs internet.
- `assets/fonts/`: the two Google Fonts used (Baloo 2, Fredoka), Latin
  subset only, bundled as `.woff2` for the same reason.

The same `index.html` also runs standalone in any browser (open the file
directly) -- useful for iterating on the game without a full Gradle build;
in a browser it falls back to an on-screen D-pad and the keyboard arrow
keys, both wired to the identical `window.JCB` interface the Android
wrapper calls.

## Build

```
./gradlew assembleDebug
```

Needs an Android SDK (`local.properties` with `sdk.dir=...`, not checked
in). Output: `app/build/outputs/apk/debug/app-debug.apk`.

Verified: `assembleDebug` and `lintDebug` both pass clean. Not verified:
an actual run on an emulator or a real Mi TV 4X -- this was built in a
sandbox with no GPU/KVM available, so please test on real hardware and
report anything that looks or feels wrong.
