# Little Engineer

Android TV construction game for kids 5-6, targeting Mi TV 4X. D-pad only,
no touch. See the task description for the full design brief.

## Status

Steps 1-6 and 8 of the build order are implemented:

1. Project scaffold, Gradle Kotlin DSL, Leanback manifest, deterministic
   D-pad focus wiring (`util/GridFocusHelper.kt`).
2. Stage 1 (Survey) + Stage 2 (Dig) - `ui/stages/SurveyStageView.kt`,
   `DigStageView.kt`, built on the shared `GridStageView.kt`.
3. Stage 3 (Pour) + Stage 4 (Bricks) - `PourStageView.kt`, `BricksStageView.kt`.
4. Stage 5 (Roof) + Stage 6 (Decorate) - `RoofStageView.kt`,
   `DecorateStageView.kt`, both built on the shared `PickerStageView.kt`.
5. Stage 7 (Reveal) - `RevealStageView.kt` + `HouseRevealView.kt` (canvas-drawn
   placeholder house with door-open / waving-person / chimney-smoke animation).
6. Full flow wired in `GameActivity.kt`: Main Menu -> Survey -> Dig -> Pour ->
   Bricks -> Roof -> Decorate -> Reveal -> Build Another / Main Menu.
8. `MyTownActivity.kt` + `model/GameProgress.kt`: persistent slot screen and
   unlock-every-5-builds bookkeeping (a second building type's *art* is not
   wired in yet, only the unlock counting - see Step 7 below).

All art right now is placeholder: flat colors, simple shapes, and emoji
standing in for vehicle icons (bulldozer/excavator/cement-mixer). This
matches the build order's instruction to prove the mechanics before art.

## Not yet done

- **Step 7 (final art & audio):** `util/SoundManager.kt` is wired to look up
  `res/raw/sfx_*` and `res/raw/music_loop` by name and silently no-ops if
  they don't exist yet, so swapping in real audio is just dropping files
  into `res/raw/`. Real vehicle/house sprites still need to replace the
  shapes in `TileCellView`, `HouseRevealView`, and the launcher icon/banner.
- **Step 9 (emulator verification):** not run. This environment's network
  policy blocks `dl.google.com`, which is where the Android Gradle Plugin,
  compileSdk platform, and build-tools are fetched from - so `./gradlew` and
  an emulator can't be exercised from here. To build:
  1. Allow `dl.google.com` in this environment's network settings, or
     open the project on a machine with normal internet access / an
     existing Android SDK.
  2. `./gradlew assembleDebug` (the wrapper itself is already checked in
     and points at Gradle 8.7, which the environment could reach).
  3. Run on an Android TV emulator (Play Store or Google TV image, API 30+,
     1920x1080) with D-pad-only input, or side-load onto a real Mi TV 4X.
- A second building type's stage art (bridge/tower) - the loop already
  reuses cleanly via `BuildingType`, but `GameActivity` always passes
  `BuildingType.HOUSE` today.

## Architecture notes

- No Compose, no Leanback widgets for gameplay screens - plain `View`/
  `ViewGroup` built programmatically in Kotlin, for full control over the
  D-pad focus chain and simple custom Canvas rendering (`TileCellView`,
  `HouseRevealView`).
- `GridFocusHelper` wires explicit `nextFocusUpId`/`DownId`/`LeftId`/`RightId`
  on every interactive view instead of relying on Android's default 2D focus
  search, so movement is 100% predictable on a fixed grid/row/chain.
- `InputDebouncer` guards every Select/click handler (~180ms) so a mashed
  remote can't double-fire an action.
- `GridStageView` is the one mechanic behind Stages 1-3 (move cursor, Select
  converts a tile from its start state to its target state, stage ends when
  none remain). `PickerStageView` is the one mechanic behind Stage 5 and all
  of Stage 6's picks (browse left/right, Select confirms). This keeps the
  stage-specific files tiny and keeps the "one clear D-pad decision per
  stage" rule structurally enforced rather than just by convention.
