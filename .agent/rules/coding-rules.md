---
trigger: always_on
---

# AlNoor Project Coding Rules

Follow these rules strictly when writing or modifying code in this project:

## 1. String Resources Only - No Static Strings

- **Never** use hardcoded static strings in UI components
- Always define strings in `res/values/strings.xml`
- Reference strings using `stringResource(R.string.your_string_id)` in Compose
- This ensures proper localization support and maintainability

```kotlin
// ❌ WRONG
Text(text = "Hello World")

// ✅ CORRECT
Text(text = stringResource(R.string.hello_world))
```

## 2. UiState Data Class for State Management

- Always use a `UiState` data class to manage screen state
- The UiState should be a single source of truth for all UI state
- Keep the UiState immutable with `val` properties

```kotlin
// ✅ CORRECT
data class MyScreenUiState(
    val isLoading: Boolean = false,
    val data: List<Item> = emptyList(),
    val errorMessage: String? = null
)
```

## 3. State Managing Callbacks in UiState Data Class

- Define all state-changing callbacks inside the UiState data class
- This keeps the state and its mutations co-located
- Makes it easier to test and reason about state changes

```kotlin
// ✅ CORRECT
data class MyScreenUiState(
    val count: Int = 0,
    val onIncrement: () -> Unit = {},
    val onDecrement: () -> Unit = {},
    val onReset: () -> Unit = {}
)
```

## 4. Hilt ViewModel Import (2026 Update)

- **DEPRECATED**: `androidx.hilt.navigation.compose.hiltViewModel`
- **USE INSTEAD**: `androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel`

```kotlin
// ❌ DEPRECATED - DO NOT USE
import androidx.hilt.navigation.compose.hiltViewModel

// ✅ CORRECT - Use this import
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
```

Make sure your dependencies are updated to the latest Hilt version that supports this import path.