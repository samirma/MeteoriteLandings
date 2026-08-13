# Meteorite Landings Spots

An Android app listing every known meteorite landing recorded by NASA — searchable, sortable by
how close each one fell to you, and plotted on a map.

Available on [Google Play](https://play.google.com/store/apps/details?id=com.antonio.samir.meteoritelandingsspots).

## Build

Requires JDK 21. Two files are deliberately untracked and must be supplied locally:

* `app/src/main/res/values/secret.xml` — the Google Maps API key:

  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <resources>
      <string name="key_map" translatable="false">YOUR_MAPS_API_KEY</string>
  </resources>
  ```

* `app/production.properties` — release signing credentials. Without it, the release build falls
  back to the debug key.

```bash
./gradlew :app:assembleDebug        # build
./gradlew :app:testDebugUnitTest    # unit tests
./gradlew :app:lintDebug            # lint
```

CI runs all three on every push.

## Architecture

Single module, 100% Compose, following
[Google's architecture recommendations](https://developer.android.com/topic/architecture/recommendations).

* **UI** — Compose and Material 3. One `uiState: StateFlow` per screen, collected with
  `collectAsStateWithLifecycle`. Navigation 3 with per-entry `ViewModel` scoping, and an adaptive
  list-detail layout on large windows.
* **Data** — Room, offline-first from a prepopulated asset. Network connectivity, GPS location and
  reverse geocoding each sit behind a repository; nothing above the data layer touches those
  platform APIs directly.
* **Background work** — WorkManager (via `HiltWorkerFactory`) reverse-geocodes addresses.

Hilt for dependency injection, Paging 3 for the list, Retrofit with kotlinx.serialization for the
feed.

### Data source

NASA's [Meteorite Landings](https://data.nasa.gov/dataset/meteorite-landings) dataset (~45,700
records). The app addresses the file by CKAN resource UUID rather than by storage path, so the
download survives NASA relocating it. Two things worth knowing before changing this:

* The old Socrata API (`data.nasa.gov/resource/y77d-th95.json`) is gone. data.nasa.gov moved to
  CKAN and no queryable API replaced it — the DataStore extension is installed but unpopulated.
* The catalog entry titled *"Meteorite Landings API"* is a static file holding only 1,000 of the
  ~45,700 records. It has exactly the right shape, so it looks like a working replacement.

## Credits

* Icon — [Flat Icons](http://www.iconarchive.com/show/flat-icons-by-flat-icons.com/Meteor-icon.html)
* Feature graphic — [Android Feature Graphic Generator](https://www.norio.be/android-feature-graphic-generator/)
* Privacy policy — [generator](https://app-privacy-policy-generator.firebaseapp.com/)
* UI design — [Figma](https://www.figma.com/file/5QPuIqE2otf4SJMhwnBzmo/New-UI---App-MeteoR?node-id=0%3A1)
