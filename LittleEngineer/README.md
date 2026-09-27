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
- **Step 9 (emulator verification):** partially done. With network access
  widened and an Android SDK installed (platform-tools, `platforms;android-34`,
  `build-tools;34.0.0`), `./gradlew assembleDebug` succeeds -- **all Kotlin
  compiles cleanly, no source errors** -- and `./gradlew lintDebug` also
  passes clean (remaining lint notices are non-issues: fixed TV orientation
  is intentional, "missing (Context, AttributeSet) constructor" is expected
  since every custom view here is built programmatically rather than
  inflated from XML, plus routine outdated-dependency notices).
  What's still unverified: an actual emulator/device run. This sandbox has
  no `/dev/kvm` and no exposed virtualization, so an Android TV emulator
  would run in full software emulation (if it boots at all) -- not
  practical here. To finish this step:
  1. Run on an Android TV emulator (Play Store or Google TV image, API 30+,
     1920x1080) on a machine with hardware virtualization, D-pad-only
     input, or side-load `app/build/outputs/apk/debug/app-debug.apk` onto
     a real Mi TV 4X.
  2. Actually play through all 7 stages once to confirm focus movement,
     stage completion, and the Reveal animation behave as designed --
     compiling clean is not the same as the game being fun/correct to play.
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
