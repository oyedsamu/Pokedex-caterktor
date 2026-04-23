# CaterKtor Migration Plan

## Migration status

This migration has now been completed for the app's current networking slice.

What was done:

- Replaced the shared module's direct Ktor client dependencies with CaterKtor modules in [gradle/libs.versions.toml](/Users/fmy-980/StudioProjects/Pokedex-caterktor/gradle/libs.versions.toml:1) and [shared/build.gradle.kts](/Users/fmy-980/StudioProjects/Pokedex-caterktor/shared/build.gradle.kts:1).
- Replaced the old Ktor-oriented client builder with a CaterKtor builder in [NetworkClientFactory.kt](/Users/fmy-980/StudioProjects/Pokedex-caterktor/shared/src/commonMain/kotlin/com/mocoding/pokedex/core/network/NetworkClientFactory.kt:1).
- Replaced platform `HttpClient` engine factories with platform transport factories in:
  - [android NetworkTransportFactory.kt](/Users/fmy-980/StudioProjects/Pokedex-caterktor/shared/src/androidMain/kotlin/com/mocoding/pokedex/core/network/NetworkTransportFactory.kt:1)
  - [ios NetworkTransportFactory.kt](/Users/fmy-980/StudioProjects/Pokedex-caterktor/shared/src/iosMain/kotlin/com/mocoding/pokedex/core/network/NetworkTransportFactory.kt:1)
  - [desktop NetworkTransportFactory.kt](/Users/fmy-980/StudioProjects/Pokedex-caterktor/shared/src/desktopMain/kotlin/com.mocoding.pokedex/core/network/NetworkTransportFactory.kt:1)
- Updated DI in [NetworkModule.kt](/Users/fmy-980/StudioProjects/Pokedex-caterktor/shared/src/commonMain/kotlin/com/mocoding/pokedex/core/network/di/NetworkModule.kt:1) to provide a CaterKtor `NetworkClient`.
- Rewrote [PokemonClient.kt](/Users/fmy-980/StudioProjects/Pokedex-caterktor/shared/src/commonMain/kotlin/com/mocoding/pokedex/core/network/client/PokemonClient.kt:1) to use CaterKtor typed `get<T>()` calls.
- Replaced the old Ktor-specific error bridge with CaterKtor `NetworkResult` and `NetworkError` mapping in [ErrorHandler.kt](/Users/fmy-980/StudioProjects/Pokedex-caterktor/shared/src/commonMain/kotlin/com/mocoding/pokedex/core/network/helper/ErrorHandler.kt:1).
- Kept repository and UI-facing behavior stable by preserving the existing `PokedexException` / `PokedexError` contract.
- Added focused tests for the migrated layer in:
  - [PokemonClientTest.kt](/Users/fmy-980/StudioProjects/Pokedex-caterktor/shared/src/commonTest/kotlin/com/mocoding/pokedex/core/network/client/PokemonClientTest.kt:1)
  - [ErrorHandlerTest.kt](/Users/fmy-980/StudioProjects/Pokedex-caterktor/shared/src/commonTest/kotlin/com/mocoding/pokedex/core/network/helper/ErrorHandlerTest.kt:1)

How it was achieved:

- The migration was done as a compatibility migration first, not a deeper architectural rewrite.
- The transport and request layer changed from raw Ktor usage to CaterKtor, while repository APIs and screen-facing behavior stayed the same.
- JSON behavior was preserved by explicitly configuring `KotlinxJsonConverter(Json { ignoreUnknownKeys = true })`.
- Android, iOS, and desktop transport selection was mapped explicitly:
  - Android -> `OkHttpTransport()`
  - iOS -> `DarwinTransport()`
  - Desktop -> `CioTransport()`
- The existing exception-based flow was preserved by mapping `NetworkResult.Failure` back into app-specific exceptions.
- Verification was done incrementally rather than all at once.

Verification completed:

1. `./gradlew :shared:testDebugUnitTest`
2. `./gradlew :shared:desktopTest`
3. `./gradlew :android:assembleDebug`
4. Earlier in the migration, Android install and launch on the emulator were also confirmed.

Ease of migration rating:

- **8/10 for this project**

Why it was fairly easy:

- Ktor usage was isolated to a very small networking slice.
- Only two real HTTP calls existed.
- The app already had a clean place to map network failures into app-specific errors.
- CaterKtor's typed API mapped well to the existing request shapes.

What prevented it from being a 10/10:

- CaterKtor's typed helpers currently do not expose a built-in query-parameter DSL, so paginated requests had to build the query string manually.
- The app had Ktor-oriented naming that needed cleanup after the transport swap.
- Test wiring in this KMP setup needed a bit of extra work to make shared tests run cleanly on Android local unit tests.

## Current Ktor usage in this project

Ktor was originally used in a small, focused slice of the shared networking layer:

- Dependency wiring lives in [shared/build.gradle.kts](/Users/fmy-980/StudioProjects/Pokedex-caterktor/shared/build.gradle.kts:45).
- A shared `HttpClient` used to be created with JSON deserialization and logging in the old `HttpClient.kt` file, which has now been replaced by [NetworkClientFactory.kt](/Users/fmy-980/StudioProjects/Pokedex-caterktor/shared/src/commonMain/kotlin/com/mocoding/pokedex/core/network/NetworkClientFactory.kt:1).
- Platform engine selection lives in:
  - the old Ktor-specific factory files have now been replaced by target-specific `NetworkTransportFactory.kt` files
- Only two actual HTTP calls exist, both GET requests, in [shared/src/commonMain/kotlin/com/mocoding/pokedex/core/network/client/PokemonClient.kt](/Users/fmy-980/StudioProjects/Pokedex-caterktor/shared/src/commonMain/kotlin/com/mocoding/pokedex/core/network/client/PokemonClient.kt:1).
- Network responses and exceptions are converted into app-specific exceptions in [shared/src/commonMain/kotlin/com/mocoding/pokedex/core/network/helper/ErrorHandler.kt](/Users/fmy-980/StudioProjects/Pokedex-caterktor/shared/src/commonMain/kotlin/com/mocoding/pokedex/core/network/helper/ErrorHandler.kt:1).
- DI binds the network client in [shared/src/commonMain/kotlin/com/mocoding/pokedex/core/network/di/NetworkModule.kt](/Users/fmy-980/StudioProjects/Pokedex-caterktor/shared/src/commonMain/kotlin/com/mocoding/pokedex/core/network/di/NetworkModule.kt:1).

## CaterKtor understanding

I reviewed CaterKtor locally from:

- [README.md](/Users/fmy-980/StudioProjects/caterktor/README.md:12)
- [caterktor-core/src/commonMain/kotlin/io/github/oyedsamu/caterktor/NetworkClientExtensions.kt](/Users/fmy-980/StudioProjects/caterktor/caterktor-core/src/commonMain/kotlin/io/github/oyedsamu/caterktor/NetworkClientExtensions.kt:1)
- [caterktor-serialization-json/src/commonMain/kotlin/io/github/oyedsamu/caterktor/serialization/json/KotlinxJsonConverter.kt](/Users/fmy-980/StudioProjects/caterktor/caterktor-serialization-json/src/commonMain/kotlin/io/github/oyedsamu/caterktor/serialization/json/KotlinxJsonConverter.kt:1)
- [caterktor-auth/src/commonMain/kotlin/io/github/oyedsamu/caterktor/auth/AuthDsl.kt](/Users/fmy-980/StudioProjects/caterktor/caterktor-auth/src/commonMain/kotlin/io/github/oyedsamu/caterktor/auth/AuthDsl.kt:1)

Key observations:

- CaterKtor provides a `NetworkClient` abstraction built via `CaterKtor { ... }`.
- Requests are made through typed helpers such as `get<T>()`, `post<T, B>()`, `put<T, B>()`, and `delete<T>()`.
- Responses are modeled as `NetworkResult.Success<T>` or `NetworkResult.Failure`.
- Errors are structured as `NetworkError.Http`, `NetworkError.Timeout`, `NetworkError.ConnectionFailed`, `NetworkError.Serialization`, and others.
- Platform transport is selected explicitly with modules like `OkHttpTransport()`, `DarwinTransport()`, and `CioTransport()`.
- JSON conversion is done by `KotlinxJsonConverter`, which is strict by default, so we will need to pass a custom `Json { ignoreUnknownKeys = true }` instance to preserve current behavior.

## Migration strategy

The safest path is a compatibility migration first, not a semantic rewrite.

That means:

- Replace transport/client construction with CaterKtor.
- Keep repository APIs and UI behavior stable.
- Preserve the current app-specific error mapping at first.
- Only after the system is stable should we consider exposing CaterKtor's richer `NetworkError` model higher in the stack.

## Migration steps

### 1. Replace dependencies

Status: Done

Update the version catalog and shared module dependencies to use CaterKtor instead of direct Ktor client modules.

Add at least:

- `caterktor-core`
- `caterktor-ktor`
- `caterktor-serialization-json`
- `caterktor-logging` if we want to preserve configurable logging
- Platform engines:
  - `caterktor-engine-okhttp` for Android
  - `caterktor-engine-darwin` for iOS
  - `caterktor-engine-cio` or `caterktor-engine-okhttp` for desktop JVM

Files to update:

- [gradle/libs.versions.toml](/Users/fmy-980/StudioProjects/Pokedex-caterktor/gradle/libs.versions.toml:1)
- [shared/build.gradle.kts](/Users/fmy-980/StudioProjects/Pokedex-caterktor/shared/build.gradle.kts:45)

### 2. Decide JVM transport mapping

Status: Done

Choose the desktop transport before implementation:

- Android: `OkHttpTransport()`
- iOS: `DarwinTransport()`
- Desktop:
  - default recommendation: `CioTransport()`
  - alternative: `OkHttpTransport()` for transport parity with Android

### 3. Replace the current shared HttpClient builder

Status: Done

The current Ktor-specific builder has been replaced with a CaterKtor builder in [NetworkClientFactory.kt](/Users/fmy-980/StudioProjects/Pokedex-caterktor/shared/src/commonMain/kotlin/com/mocoding/pokedex/core/network/NetworkClientFactory.kt:1).

Target shape:

- create a new `createNetworkClient(enableLogging: Boolean)` function
- set `baseUrl = NetworkConstants.baseUrl`
- add `KotlinxJsonConverter(Json { ignoreUnknownKeys = true })`
- configure platform transport based on source set
- optionally add CaterKtor logging when `enableLogging` is true

### 4. Remove platform-specific Ktor HttpClient factories

Status: Done

These platform files currently only exist to return Ktor engine-backed `HttpClient` instances:

- [android factory](/Users/fmy-980/StudioProjects/Pokedex-caterktor/shared/src/androidMain/kotlin/com/mocoding/pokedex/core/network/HttpClientFactory.kt:1)
- [ios factory](/Users/fmy-980/StudioProjects/Pokedex-caterktor/shared/src/iosMain/kotlin/com/mocoding/pokedex/core/network/HttpClientFactory.kt:1)
- [desktop factory](/Users/fmy-980/StudioProjects/Pokedex-caterktor/shared/src/desktopMain/kotlin/com.mocoding.pokedex/core/network/HttpClientFactory.kt:1)

These should either:

- be deleted entirely, or
- be replaced by CaterKtor transport factory helpers if we still want platform-specific construction split across source sets

### 5. Update DI from Ktor HttpClient to CaterKtor NetworkClient

Status: Done

The current DI setup in [NetworkModule.kt](/Users/fmy-980/StudioProjects/Pokedex-caterktor/shared/src/commonMain/kotlin/com/mocoding/pokedex/core/network/di/NetworkModule.kt:1) binds a Ktor `HttpClient`.

It should instead bind a CaterKtor `NetworkClient`.

Plan:

- replace `single { createHttpClient(enableLogging) }`
- with `single { createNetworkClient(enableLogging) }`
- update `PokemonClient` constructor injection accordingly

### 6. Rewrite PokemonClient against CaterKtor

Status: Done

`PokemonClient` is the main migration surface.

Current responsibilities:

- GET pokemon list with `limit` and `offset`
- GET pokemon by name
- rely on `handleErrors { ... }` for status/exception handling

Target responsibilities:

- use `NetworkClient.get<T>()`
- build URLs relative to `baseUrl`
- carry query parameters in the final URL or via lower-level request construction if needed
- return decoded models without touching Ktor APIs directly

Planned changes:

- constructor changes from `HttpClient` to `NetworkClient`
- replace `httpClient.get(...)` calls with CaterKtor typed calls
- handle query param support for list pagination

Notes:

- CaterKtor typed helpers support relative URLs and path params directly.
- Query parameter support does not currently have a built-in DSL in the typed helpers, so the final URL string is composed manually for now.
- This gap was reported upstream in [oyedsamu/caterktor#1](https://github.com/oyedsamu/caterktor/issues/1).

### 7. Replace ErrorHandler with CaterKtor result/error mapping

Status: Done

Current behavior in [ErrorHandler.kt](/Users/fmy-980/StudioProjects/Pokedex-caterktor/shared/src/commonMain/kotlin/com/mocoding/pokedex/core/network/helper/ErrorHandler.kt:1):

- catches `IOException`
- branches on HTTP status code manually
- calls `result.body()`
- throws `PokedexException`

With CaterKtor, the call result is already classified.

Recommended first-step migration:

- keep `PokedexError` and `PokedexException`
- add a mapper from `NetworkResult.Failure` or `NetworkError` to those app-level errors
- remove all direct Ktor references from `ErrorHandler`

Suggested mapping:

- `NetworkError.ConnectionFailed` and `NetworkError.Timeout` -> `PokedexError.ServiceUnavailable`
- `NetworkError.Http` with 4xx -> `PokedexError.ClientError`
- `NetworkError.Http` with 5xx -> `PokedexError.ServerError`
- `NetworkError.Serialization`, `NetworkError.Protocol`, `NetworkError.Unknown` -> `PokedexError.UnknownError` or `ServerError` depending on desired behavior

### 8. Preserve current app behavior during first migration

Status: Done

To keep the migration low-risk:

- preserve `ignoreUnknownKeys = true`
- preserve current endpoint paths from [NetworkConstants.kt](/Users/fmy-980/StudioProjects/Pokedex-caterktor/shared/src/commonMain/kotlin/com/mocoding/pokedex/core/network/NetworkConstants.kt:1)
- preserve repository return types as `Result<...>`
- preserve current caching behavior in repositories
- preserve optional logging behavior

### 9. Remove obsolete Ktor-specific app code

Status: Mostly done

After the new CaterKtor flow is working:

- remove old Ktor-oriented files
- replace them with CaterKtor-aligned factory naming
- remove direct `io.ktor.*` imports from app code
- remove any now-unused helper code built around `HttpResponse`

### 10. Add focused tests for the new network layer

Status: Done

This migration is a good chance to add dedicated client-level tests.

Recommended tests:

- pokemon list success decode
- pokemon details success decode
- 404 mapping to app error
- 5xx mapping to app error
- malformed JSON mapping
- logging enabled vs disabled behavior if logging remains configurable

If we use `caterktor-testing`, these tests can be added without real network dependency.

### 11. Verify in stages

Status: Done

Implementation should be validated incrementally:

1. `:shared:compileDebugKotlinAndroid`
2. `:android:assembleDebug`
3. install and launch on emulator
4. optionally compile desktop as well if the desktop transport changes

## Recommended execution order

1. Add CaterKtor dependencies and transports.
2. Build a new `NetworkClient` factory.
3. Switch DI to `NetworkClient`.
4. Rewrite `PokemonClient`.
5. Replace `ErrorHandler` with CaterKtor-aware mapping.
6. Remove old Ktor-only files.
7. Add tests.
8. Verify Android build and launch.

## Risks and attention points

- CaterKtor JSON is strict by default, so we must explicitly preserve `ignoreUnknownKeys = true`.
- Query parameter handling for `getPokemonList()` is the main API-shape detail to validate before coding.
- Desktop transport choice should be settled before implementation to avoid churn.
- If we later want richer error handling in stores/UI, that should be a second migration after the transport swap is stable.

## Recommendation

Do the migration in one focused networking PR, but keep it compatibility-oriented:

- swap transport layer
- preserve app-facing behavior
- defer deeper architectural changes until after CaterKtor is proven stable in this app

That recommendation held up well in practice for this repository. The compatibility-first approach kept the migration small, understandable, and easy to verify.
