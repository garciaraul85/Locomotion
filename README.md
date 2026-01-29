# Locomotion TV - Streaming App Specs

## Overview
This project is an Android TV + mobile streaming app for a single channel (Locomotion). It shows a TV guide and plays the live stream with automatic failover across backup URLs.

## Target platforms
- Android TV + Android mobile
- minSdk 21 (Android 5.0)
- TV remote (D-pad focus), mobile touch support


## UI adaptability
- UI must adapt to different screen sizes (TVs and phones).
- Use resource qualifiers and/or responsive layouts to scale the guide grid, typography, and spacing.
- Prefer flexible layouts (ConstraintLayout, weight-based rows/cols) and avoid fixed pixel sizes.


## Key features
- TV Guide screen (single channel for MVP)
- Player screen with overlay controls
- Horizontal time scrolling (TV D-pad left/right, mobile swipe)
- Schedule panel (bottom sheet on mobile)
- Network/offline detection and error states
- Stream URL failover (primary + backups)

## Screens
### 1) TV Guide
- Channel list on the left, time axis across the top, program grid in the body
- Selecting any program opens the Player
- Controls for Now / Previous / Next
- Offline and empty states

### 2) Player
- Fullscreen video by default
- Overlay controls: play/pause, volume, back, optional fullscreen toggle
- Bottom sheet schedule on mobile (drag up/down)
- Error overlays for offline or unavailable stream

## Data sources
### Schedule (XML EPG)
- Endpoint: `http://epg.locomotiontv.com/guia.xml`
- Root: `<tv>`
- Channels: `<channel id="Locomotion">` with display-name, icon, url
- Programs: `<programme>` with `start`, `stop`, `channel`, `title`, `desc`, `date`, `icon`
- Time format: `yyyyMMddHHmmss Z` (example: `20260112000000 -0400`)

Observed XML details (curl on 2026-01-29):
- Includes `<channel>` entries for `Locomotion` and `AZTV`.
- `<programme>` entries include `start/stop`, `title`, `desc`, `date`, and `icon` fields.

### Streams (HLS)
Source playlist: `m3u.m3u` (repo root)

Locomotion URLs (ordered failover):
- Primary: `http://51.222.85.85:81/hls/loco/index.m3u8`
- Backup 1 (high): `http://146.19.49.197:81/live/loco_hi/index.m3u8`
- Backup 2 (low): `http://146.19.49.197:81/live/loco_low/index.m3u8`
- Backup 3 (adaptive): `http://146.19.49.197:81/live/loco.m3u8`

Observed playlist details (curl on 2026-01-29):
- `http://51.222.85.85:81/hls/loco/index.m3u8` is a media playlist with `.ts` segments (not a master).
- `http://146.19.49.197:81/live/loco.m3u8` is a master playlist with variants:
  - `loco_low/index.m3u8`
  - `loco_mid/index.m3u8`
  - `loco_hi/index.m3u8`
- `http://146.19.49.197:81/live/loco_hi/index.m3u8` and `http://146.19.49.197:81/live/loco_low/index.m3u8` are media playlists with `.ts` segments.

Other channels in the playlist (not used for MVP):
- AnimeZoneTV: `http://animezonetv.com/hls/stream.m3u8`
- Anime XTV High Server 1: `http://animezonetv.net:81/live_hls/animetest/index.m3u8`
- Anime XTV High Server 2: `http://146.19.49.197:81/live/animextv_hi/index.m3u8`

Observed playlist details (curl on 2026-01-29):
- `http://animezonetv.com/hls/stream.m3u8` is a media playlist with `/hls/animeloving/*.ts` segments.
- `http://animezonetv.net:81/live_hls/animetest/index.m3u8` is a media playlist with `.ts` segments.
- `http://146.19.49.197:81/live/animextv_hi/index.m3u8` is a media playlist with `.ts` segments.

### How we use M3U and M3U8
- `m3u.m3u` gives the top-level HLS URLs.
- Each `.m3u8` playlist references short `.ts` segments.
- We do NOT download `.ts` segments manually. ExoPlayer handles segment fetching, buffering, and playback.
- If a URL fails, we switch to the next one in the list.

## Architecture (MVVM)
### Presentation layer
- Activities/Fragments (XML UI): Guide and Player screens
- ViewModels: expose UI state via StateFlow/LiveData, handle user events
- UI state models: guide rows, time axis, selected program, loading/error states

### Domain layer
- Use cases: FetchSchedule, ObserveConnectivity, SelectProgram, StartPlayback, SwitchStreamUrl
- Entities: Channel, Program, Schedule

### Data layer
- Repositories: ScheduleRepository, StreamRepository, ConnectivityRepository
- Remote: Retrofit + XML converter, OkHttp
- Local: Room and DataStore

### Cross-cutting
- DI: Hilt
- Async: Coroutines + Flow
- Media: ExoPlayer (Media3)

## Libraries
- Kotlin, Coroutines, Flow
- AndroidX: ViewModel, Lifecycle, Navigation
- Hilt (DI)
- Retrofit + XML converter, OkHttp
- Room, DataStore
- Media3 ExoPlayer
- Timber

## Data models (initial)
- Channel: id, name, logoUrl, streamUrls[]
- Program: id, channelId, title, description, startTime, endTime, iconUrl, year(optional)
- Schedule: channelId, date, programs[]

## Error handling
- Offline: show blocking banner/overlay, pause playback
- Stream unavailable: error screen + retry
- Schedule failure: show cached schedule if available

## UI inspiration
Reference screenshots in repo root (names include a narrow no-break space before PM on macOS):
- `Screenshot 2026-01-26 at 12.10.49 PM.png`
- `Screenshot 2026-01-26 at 12.11.15 PM.png`

Notes:
- Apple-like polish, soft gradients, high-contrast typography
- Dark navy background, subtle borders
- Ignore star ratings and hover hints (do not implement)

## Recent progress
- Architecture scaffolding added (core/data/domain/player packages, DI modules, models, repos, use cases).
- Gradle/AGP/Kotlin aligned for AGP 9 + built‑in Kotlin.
- Switched Hilt processing to KSP.
- Replaced SimpleXML with Retrofit Scalars; EPG now fetched as raw XML string.
- TV module modernized to remove deprecated APIs and pass lint.
- Full `./gradlew build` now succeeds.
- Added connectivity detection with offline banners on guide, mobile, and playback screens.

## Development checklist (detailed)
### 0) Project setup
- [ ] Confirm package name, app name, icons
- [ ] Set minSdk 21, targetSdk latest stable
- [ ] Base theme + TV-friendly dimensions

### 1) Architecture scaffolding
- [x] Package structure: `ui/`, `domain/`, `data/`, `player/`, `core/`
- [x] Models: Channel, Program, Schedule
- [x] Repositories: Schedule, Stream, Connectivity (interfaces + impl stubs)
- [x] Use cases: FetchSchedule, ObserveConnectivity, SwitchStreamUrl, SelectProgram
- [x] Hilt modules for networking, repos, player
- [x] Hilt Application class wired in manifests
- [x] Added dependencies: Hilt, Retrofit, OkHttp, SimpleXML

### 2) Networking and parsing
- [x] Retrofit + XML (raw) via Scalars converter
- [x] XML models for `<tv>`, `<channel>`, `<programme>` (parsed manually)
- [x] Parse `start/stop` as `yyyyMMddHHmmss Z`
- [x] Map XML -> domain models
- [x] Schedule API call + retry

### 3) Schedule caching
- [x] Room entities for Schedule + Program
- [x] Cache TTL (5-15 min)
- [x] Serve cached schedule on failure
- [x] Flow for schedule updates

### 4) Connectivity
- [x] ConnectivityManager + NetworkCallback
- [x] Expose online/offline state
- [x] Global offline banner/overlay

### 5) Guide screen
- [x] XML layouts: header, time axis, channel list, program grid
- [x] Horizontal scroll (TV D-pad + mobile swipe)
- [x] Focus states + selection highlight
- [x] Bind schedule to grid
- [x] Program click -> Player
- [x] Now/Previous/Next controls

### 6) Player screen
- [ ] PlayerFragment + Media3 ExoPlayer
- [ ] Controls overlay
- [ ] Auto-hide controls
- [ ] Buffering indicator
- [ ] Stream error overlay

### 7) Stream URL failover
- [ ] Load Locomotion URLs from `m3u.m3u`
- [ ] Start with primary
- [ ] Switch on error
- [ ] Show stream unavailable when exhausted

### 8) Mobile schedule panel
- [ ] Bottom sheet list
- [ ] Drag up/down
- [ ] Sync selection with current program

### 9) Performance
- [ ] Smooth scroll + focus
- [ ] Tune player buffering
- [ ] Image caching

### 10) QA
- [ ] TV device/emulator
- [ ] Mobile device
- [ ] Offline + retry
- [ ] Stream failover
- [ ] XML edge cases

### 11) Release
- [ ] Optional analytics/logging
- [ ] R8/ProGuard rules for Media3 and XML
- [ ] Versioning + release notes


• I can’t commit or push yet: Git can’t create .git/index.lock (Operation not permitted). Even touch .git/index.lock fails, so the repo is blocking new files in .git.

Please fix permissions, then I’ll commit and push. Common fixes:

1. Give your terminal Full Disk Access (System Settings → Privacy & Security → Full Disk Access) and restart the terminal/IDE.
2. Ensure the repo isn’t on a protected/readonly volume (or move it).
3. Verify ownership/permissions: ls -ld .git and chmod -R u+rwX .git (if needed).

Once that’s fixed, say “retry commit” and I’ll finish the commit + push.
