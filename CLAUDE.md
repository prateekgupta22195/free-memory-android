# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run all unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Run a single test class
./gradlew test --tests "com.pg.cloudcleaner.ExampleUnitTest"

# Lint
./gradlew lint
```

The app `applicationId` is `com.pg.freememory` but the source package is `com.pg.cloudcleaner`. Both are intentional and must not be conflated.

## Architecture Overview

**Pattern**: Single-activity, Jetpack Compose UI, clean architecture with three layers.

```
presentation/        ViewModels + Composable pages + UI components
domain/interactors/  Use cases (FileUseCases, HomeUseCases) — no Android deps
data/                Room DB, DAOs, repository implementations
helper/              WorkManager workers (background scan, checksum update)
utils/               Stateless utilities (file I/O, MD5, image optimizer)
app/                 App singleton, NavController, Router, theme
```

There is a legacy `misc/` package containing an unused Google Drive feature (its own DB, ViewModels, network layer). Do not extend it; it is not wired into the main navigation.

## Data Flow: File Scan Pipeline

The scan is the core feature. Understanding it prevents regressions:

1. **`ReadFileWorker`** (WorkManager, expedited) — triggered on storage permission grant and on "Scan Again". Calls `db.clearAllTables()` then traverses the filesystem via `FileUseCases.syncAllFilesToDb()`.
2. **`FileUseCases.traverseDir()`** — parallel directory traversal using coroutines + a `Channel<List<LocalFile>>`. Each directory's files are sent as a batch with a `partialMd5` checksum (head + tail of file, not full hash).
3. **`LocalFilesRepoImpl.insertAll()`** — bulk-inserts via a single `INSERT OR REPLACE` SQL statement per 500-file chunk (using `@RawQuery`). SQLite's 999-variable limit is the reason for chunking at 500 rows × 7 columns = 3500 bound vars.
4. **`UpdateChecksumWorker`** — separate periodic worker that computes full MD5s for files that still have `null` checksums, in batches of 50.
5. **`HomeVM`** observes `WorkManager.getWorkInfosForUniqueWorkFlow("file reader")` to drive the scanning UI state.

## Database

- **Entity**: `LocalFile` — `id` is the absolute file path (primary key). `size` is stored in **KB** (not bytes). All size-to-bytes conversions multiply by 1024.
- **Version**: 6, `fallbackToDestructiveMigration = true`. Schema changes don't need migrations.
- **Duplicate detection**: done entirely in SQL. `md5` column has an index. Duplicates are identified by `EXISTS` subqueries, not by loading all files into memory.
- **Optimised images**: marked via EXIF `TAG_USER_COMMENT = "cc_optimised"` written by `ImageOptimizer`. The `isOptimised` column is read from this EXIF tag at scan time via `File.readIsOptimised()`.
- **DAO pattern**: all list queries use `@RawQuery` with `SimpleSQLiteQuery` for flexibility. `insertAll` also uses `@RawQuery` (bulk insert). Avoid adding `@Insert(list)` — it generates N individual statements.

## Navigation

Routes are defined as constants in `Routes` companion object (`app/Router.kt`). The `FILE_DETAIL_VIEWER` route takes URL-encoded query params: `url` (file path, `Uri.encode`'d), `category`, and optional `md5` (for duplicates group). Always `Uri.encode` file paths before putting them in nav routes.

Category string constants used throughout (not from `strings.xml`):
- `"category_duplicates"`, `"category_images"`, `"category_videos"`, `"category_large_files"`, `"category_screenshots"`

## Media Handling

- **Video playback** (`VideoPlayer.kt`): uses `androidx.media3` (ExoPlayer3). Player is `remember(videoUrl)`-keyed so it recreates on URL change. `DisposableEffect(player)` handles release. `PlayerView` API is `@UnstableApi` — annotate callers with `@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)` when needed.
- **Thumbnails** (`VideoThumbnailCompose.kt`): Coil3 + `coil-video` (`VideoFrameDecoder`). The global `ImageLoader` in `App` limits decoder parallelism to 2 via `.decoderCoroutineContext(Dispatchers.IO.limitedParallelism(2))` to prevent `MediaMetadataRetriever` finalizer timeouts.
- **Image viewing** (`ImageViewer.kt`): uses Coil3 `AsyncImage` (not `rememberAsyncImagePainter`) so layout-measured size is passed to the decoder automatically — no hardcoded size needed.
- **`FileDetailViewerPage`** pager uses `beyondViewportPageCount = 0` to prevent adjacent pages holding bitmaps/ExoPlayer instances in memory simultaneously.

## Key Gotchas

- `File.size()` returns `length() / 1024` (KB). The DB `size` column is in KB everywhere. When displaying to users or comparing against byte thresholds, multiply by 1024.
- `partialMd5()` appends `_<fileSize>` to the hash so files with identical partial content but different sizes don't collide.
- `SavedMemoryTracker` persists cumulative freed bytes to `SharedPreferences` ("app_prefs" / "total_saved_bytes") and exposes a `StateFlow` for the home screen badge.
- Permission handling differs by API level: Android R+ uses `MANAGE_EXTERNAL_STORAGE` (all-files access), below R uses `READ/WRITE_EXTERNAL_STORAGE`.
- The `NavController` is stored on the `App` singleton (`App.instance.navController()`). It is initialized in `MainActivity.setContent` via `App.instance.initNavController(rememberNavController())`. Don't call `navController()` before the main app composable is active.

## Localisation

String resources live in `values/strings.xml` (English base). Current locales: `values-zh-rCN`, `values-ko`, `values-ja`, `values-in` (Indonesian), `values-hi` (Hindi), `values-pt` (Portuguese base), `values-pt-rBR`, `values-pt-rPT`. When adding strings, add to all locale files. The `pt` base uses Brazilian conventions; `pt-rPT` overrides use European conventions (ficheiro, eliminar, "a + infinitive" progressive form).

## Observability

Sentry is integrated for crash reporting and performance tracing (`sentry-android`, `sentry-compose-android`). `HomeVM` manually calls `Sentry.captureException` for unexpected worker cancellation/failure. Sample rates are set to 1.0 in the manifest — adjust for production load.
