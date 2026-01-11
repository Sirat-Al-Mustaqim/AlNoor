# 0.1.0 (2026-01-11)


### Bug Fixes

* Add backward compatibility for VpnStatistics parcelable retrieval and update formatting in VpnManager ([c879bef](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/c879bef1a7de84fdfddeb322fd526c8830a72308))
* add error handling, loop prevention, and conflict resolution to version bump workflow ([a796e0b](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/a796e0bd730b0a783d2b6de857af23b357d7dbfa))
* add explicit permissions to workflow for security compliance ([e897bd5](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/e897bd5d351a637f2fea9a0f7852db9fd64dcd3b))
* Enable blocking mode in VPN configuration during establishment. ([482222c](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/482222c46fac3de1631bbac35cc6ef79b8957903))
* refine package name detection in set_owner.sh and expand device admin policies in XML configuration. ([57714ab](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/57714ab01552e6a1de292a53cd388cef103e43b1))
* Update verification protection label and allow text pasting in debug builds. ([799cce6](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/799cce6581fcb9c98ca731b9a3d277927b71da05))


### Features

* add Ayah verification screen and logic to protect disabling "Always-On Protection" ([4dc4d14](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/4dc4d14cf93dba458eb188726bc061c8b9214e85))
* add brand-new splash screen, update app icons, and rename "Nur Blocker" to "Al Noor" across the application. ([1261954](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/1261954224f94958dda06dba7abe9632f9fce98b))
* add custom splash screen and update app icons and notification assets ([546eb4e](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/546eb4ef8f842a4d597daca0c9be01e593c74344))
* Add device owner check for Always-on Protection ([e905773](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/e9057732902f9c48ccfaa5346898ea1d509c9d37))
* add GitHub workflow for automatic version bumping on develop branch merges ([9949903](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/9949903a23f0812e00a244c281c3d5331da1cb01))
* add GuardMode settings with VPN and Private DNS options and update DataStore persistence. ([602e98f](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/602e98f4e5c9d1911405e2adb8a5a5ef011da50c))
* Add Hilt dependency injection setup and related libraries. ([5477803](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/54778036d27475514158e9b9f1098e59f6f524e7))
* Add initial Quran database by converting MySQL dump to SQLite, normalizing schema, and fixing reserved keywords. ([46b25e4](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/46b25e45d9f52f2b7bc764b61f0716dffd25f00e))
* add IPv6 support to VPN configuration with family-safe DNS addresses ([38b32e0](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/38b32e0c05f5b8dcd250027c9fb10b22466cf90c))
* Add KSP, Navigation Compose, and Room dependencies. ([6427380](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/6427380afec39e76fabcebc79d6310736e1b316a))
* Add More, Qibla, and Blocker screens, update navigation items and graph, and adjust theme colors. ([a2e3cdc](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/a2e3cdc05dd5d8cc0176f690c4df2d424c4172ba))
* Add new prayer settings for location, audio, and reminders with updated UI and data persistence. ([28eb9fc](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/28eb9fc745f8b2b097d6e422c3eba76a4e3e80b7))
* add notification icon asset ([1ef3c1a](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/1ef3c1a462538b1308839af4a7a45585195ae04f))
* Add offline mode setting with UI updates and DataStore persistence. ([d0ee3a7](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/d0ee3a711b24c6731368da72355730c370d3aeeb))
* Add quran-simple-sqlite database file ([5168667](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/516866799be9fc71de1b58bbb0bbe1f8dfbbabd1))
* Add quran.db asset and update its SQL source. ([e245eeb](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/e245eeb3c3f79dd6787726737be68b1bb0bf2b54))
* add RtlLayout component and apply RTL direction to Ayah verification input and list items. ([4f4c93e](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/4f4c93eeed7e8f5fc99bdf7c0af6394360f80a17))
* Add UI previews for Guard, Home, Quran, Settings, Main, and Duas screens, including a dark mode preview for Home. ([81f459c](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/81f459c82ccda77f359e850a55836c0cad0543de))
* configure debug build type with `.debug` application ID suffix and debuggability. ([0217e77](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/0217e77ab898cca407fb052f4edafc6793d09f24))
* Delete BlockerScreen, rename Blocker navigation to Guard, and promote GuardScreen to primary navigation. ([d9b4ad4](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/d9b4ad4ce3703ed06d888b67c13d8cd750f31ba5))
* Display dynamic app version in settings and update debug build suffixes to `.dev`. ([cd412b2](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/cd412b2e71b61e2987520f75cd373d378c6361bc))
* dynamic Ayah verification requirement based on build type with UI and string updates. ([657705c](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/657705ca1165129e5b602d0f20afeb903a297179))
* Enable BuildConfig generation and fix `@ApplicationContext` annotation usage. ([26ed49c](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/26ed49c80340d7cfc1b46432cb611f35a636bb0d))
* ensure VPN starts when enabling always-on protection and update debug build configuration comment ([32b5a30](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/32b5a306000a530b89036025f6f249106c66c09a))
* implement `DnsObserverService` to monitor and enforce DNS protection as a foreground service. ([24710fd](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/24710fd082dacb75a10190fb56a0edcbb9ca56ce))
* implement `DnsObserverService` to monitor and enforce DNS protection as a foreground service. ([0955084](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/0955084e2c5460c307711439e68e433e20e46e8c))
* implement ADB and Developer Options protection in GuardRepository for device owner mode. ([c95dfd5](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/c95dfd523e3efa39c4b4861a627a08df24bddb8c))
* Implement always-on protection setting and update guard settings UI to control it. ([90740d2](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/90740d251d212b6c33501086ca27f4490f5d13a6))
* Implement backup private DNS configuration using global settings in GuardRepository. ([6ec8174](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/6ec81746f021ca22574dfae0271ef015f56c6316))
* Implement device admin and owner-based app protection ([28205b5](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/28205b596480b0c5bc1bcfaa4ae59800ca74cd50))
* Implement DNS ContentObserver in `GuardRepository` to automatically revert changes when always-on protection is enabled. ([a509b42](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/a509b4206120f4be77746739e4fece6112a25d99))
* implement initial app structure with core UI, navigation, and theming ([e38dd8a](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/e38dd8acfec95119689d23572e07fd38776794e0))
* Implement Parcelable for VpnStatistics and streamline VPN stats broadcasting ([2db9059](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/2db9059e287038d64fb51c3f50eb8a2140a0b107))
* implement periodic DNS enforcement using WorkManager and Hilt integration. ([7f4befd](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/7f4befd4a09c3521de73ee8a9152229ba509e761))
* Implement Quran data layer and UI with Room database and Hilt injection ([7d61c82](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/7d61c8220ed2cf09cce515777c0e96d26ac71de8))
* implement settings management with dedicated UI sections and data store, and remove quran.db ([58435f9](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/58435f94d417cb8c0c68eb64d3a33dc706fa44cb))
* Implement the Guard screen with VPN connection control, status display, statistics, and an always-on VPN toggle, supported by a new ViewModel. ([bf38f24](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/bf38f247670f369bc8f1c0ed70276953f9fcf249))
* Implement VPN activation logic and UI status updates ([7b26431](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/7b264311cefc30e157651dd82bdf27559888e584))
* Implement VPN service auto-restart when app is closed ([0e18439](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/0e18439cf7beac2b23601d660f0f68ce1264a2f5))
* Implement VPN statistics broadcasting, update VpnManager to handle state via receiver, and raise minSdk to 26. ([338d8dd](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/338d8dd6e981fe9f216cf86086e0151c30af3e3a))
* integrate Android 12+ Splash Screen API and implement navigation splash screen ([9e7fb36](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/9e7fb361411c215b83cb8aa470c51d274e3e5bb7))
* integrate Firebase Cloud Messaging and implement `GuardMessagingService` for remote service management. ([efcca6c](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/efcca6cf215a9d0cf0d3f9e462d8af2fccc71735))
* integrate Firebase Cloud Messaging and implement `GuardMessagingService` for remote service management. ([25017a8](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/25017a83eb3604d40a982fee799d6f29eb81627c))
* integrate semantic-release for intelligent version bumping based on conventional commits ([770910e](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/770910e6d0eddbe534b7f156e6066cd1f0e3d3b8))
* integrate Timber logging library and initialize for debug builds. ([7811f84](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/7811f84eb2d5dc972afc8aec5e9a835154cc0b00))
* Modify VPN configuration to act as a non-blocking DNS override without traffic routing. ([da762d0](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/da762d0060c898c747f6dbc8c7b4960ae22928e1))
* Move "Always-on protection" toggle from a separate screen to the main Guard screen. ([690bd21](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/690bd21f9e2011ff3928d3ac80df36eb6e094b81))
* prevent VPN disconnection when always-on protection is enabled and add project setup documentation. ([3004d2b](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/3004d2b5805f06584e9c5a31fe83b4f5a555a957))
* redesign Ayah Verification screen with enhanced UI, progress tracking, and localized strings ([e8c584d](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/e8c584d0ffff8b841490913217f11044786a538e))
* Refactor `SettingsScreen` to pass `StateFlow`s to new section wrappers for isolated state collection and extract preview content. ([a3119d9](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/a3119d9233779e8bcea9264ee567bc7f5ffe5b88))
* remove `SettingsScreenPreviewContent` and update previews to use `SettingsScreenContent` with `MutableStateFlow` ([fcbb329](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/fcbb3293e29237ff0f6899197363f790116d0cdc))
* replace generic settings screen preview with detailed component examples and add necessary imports. ([48eca45](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/48eca458ced1912457e986409ffd971c8521d09b))
* replace VPN-based content filtering with Private DNS via Device Policy Manager ([6b86c5f](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/6b86c5ffab36617a905fbba35483748625402627))
* replace VPN-based content filtering with Private DNS via Device Policy Manager ([a559dbb](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/a559dbb94b6993f05714834d4a1f8b36973cd376))
* restrict private DNS configuration in Guard mode for Android 29+ and update IDE project settings. ([d0b5818](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/d0b581849d0f24c5ee09270644cfd37cf1758515))
* Revamp UI with a new color palette, updated typography, and refined theme definitions. ([d7b4893](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/d7b489363de88bc630070d841736cf437628ac9e))
* Simplify Guard statistics UI to show only session duration and remove network usage metrics. ([24d4993](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/24d4993051c4f753a33d6a3116daefb011f1a5eb))
* Update `setAppProtection` to configure Always-on VPN and lockdown mode ([0b458c8](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/0b458c80ddc92fa617aabb9af8a56d9c8d4b3300))
* update dark theme colors and redesign settings page UI with new fonts and layout ([a099173](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/a0991735735669c4a943c267ead37515a4aa6363))
* Update Quran simple SQLite database schema and data ([1e3390d](https://github.com/Sirat-Al-Mustaqim/AlNoor/commit/1e3390da162025db026d0e21fcc834e8676491ee))

# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.0] - 2026-01-11

### Added
- Initial release of AlNoor application
- Content filtering VPN service
- Always-on protection features
- Device Policy Manager protections
- Firebase Cloud Messaging heartbeat mechanism
