# GitHub Workflows

## Auto Version Bump

The `version-bump.yml` workflow automatically updates the application version in `app/build.gradle.kts` whenever changes are pushed to the `develop` branch.

### How it works

1. **Trigger**: Activates on every push to the `develop` branch (typically after a merge)
2. **Loop Prevention**: Checks if the last commit was a version bump to prevent infinite loops
3. **Version Extraction**: Reads current `versionCode` and `versionName` from `app/build.gradle.kts`
4. **Validation**: Ensures version values are extracted successfully and in valid format
5. **Version Increment**:
   - `versionCode`: Increments by 1 (e.g., 1 → 2)
   - `versionName`: Increments patch version following semantic versioning (e.g., 1.0 → 1.0.1, 1.0.1 → 1.0.2)
6. **Update & Commit**: Updates the file, pulls latest changes with rebase, and commits the change back to the `develop` branch
7. **Summary**: Provides a summary of the new version in the workflow run

### Example

Before merge:
```kotlin
versionCode = 1
versionName = "1.0"
```

After workflow runs:
```kotlin
versionCode = 2
versionName = "1.0.1"
```

### Features

- **Loop Prevention**: Automatically skips execution if the last commit was a version bump
- **Error Handling**: Validates version extraction and format before proceeding
- **Conflict Resolution**: Pulls latest changes with rebase before pushing to avoid conflicts
- **Safe Regex**: Escapes special characters in version strings for safe sed replacements

### Notes

- The workflow uses the GitHub Actions bot account for commits
- Only runs if version changes are detected
- Requires `GITHUB_TOKEN` with write permissions (automatically provided by GitHub Actions)
- Assumes semantic versioning format (MAJOR.MINOR.PATCH)

