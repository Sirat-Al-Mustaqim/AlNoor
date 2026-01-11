# GitHub Workflows

## Auto Version Bump

The `version-bump.yml` workflow automatically updates the application version in `gradle.properties` whenever changes are pushed to the `develop` branch. The version is then referenced by `app/build.gradle.kts`.

### How it works

1. **Trigger**: Activates on every push to the `develop` branch (typically after a merge)
2. **Loop Prevention**: Checks if the last commit was a version bump to prevent infinite loops
3. **Version Extraction**: Reads current `VERSION_CODE` and `VERSION_NAME` from `gradle.properties`
4. **Validation**: Ensures version values are extracted successfully and in valid format
5. **Version Increment**:
   - `VERSION_CODE`: Increments by 1 (e.g., 1 → 2)
   - `VERSION_NAME`: Increments patch version following semantic versioning (e.g., 1.0 → 1.0.1, 1.0.1 → 1.0.2)
6. **Update & Commit**: Updates `gradle.properties`, pulls latest changes with rebase, and commits the change back to the `develop` branch
7. **Summary**: Provides a summary of the new version in the workflow run

### Version Management

Versions are managed centrally in `gradle.properties`:
```properties
VERSION_CODE=1
VERSION_NAME=1.0
```

And referenced in `app/build.gradle.kts`:
```kotlin
defaultConfig {
    versionCode = project.property("VERSION_CODE").toString().toInt()
    versionName = project.property("VERSION_NAME").toString()
}
```

### Example

Before merge in `gradle.properties`:
```properties
VERSION_CODE=1
VERSION_NAME=1.0
```

After workflow runs:
```properties
VERSION_CODE=2
VERSION_NAME=1.0.1
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

