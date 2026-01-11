# GitHub Workflows

## Auto Version Bump

The `version-bump.yml` workflow automatically updates the application version in `gradle.properties` whenever changes are pushed to the `develop` branch. It uses **semantic versioning** based on **conventional commit messages** to determine the bump type.

### How it works

1. **Trigger**: Activates on every push to the `develop` branch (typically after a merge)
2. **Loop Prevention**: Checks if the last commit was a version bump to prevent infinite loops
3. **Commit Analysis**: Analyzes commit messages using conventional commits to determine bump type
4. **Version Extraction**: Reads current `VERSION_CODE` and `VERSION_NAME` from `gradle.properties`
5. **Validation**: Ensures version values are extracted successfully and in valid format
6. **Version Calculation**:
   - **MAJOR bump** (X.0.0): Breaking changes (`feat!:`, `BREAKING CHANGE:`)
   - **MINOR bump** (0.X.0): New features (`feat:`, `feature:`)
   - **PATCH bump** (0.0.X): Bug fixes and improvements (`fix:`, `perf:`, `refactor:`)
   - `VERSION_CODE`: Always increments by 1
7. **Update & Commit**: Updates `gradle.properties`, pulls latest changes with rebase, and commits back to `develop`
8. **Summary**: Provides a summary showing bump type and new version

### Conventional Commits

The workflow recognizes these commit message formats:

```
feat: add new feature          → MINOR bump (1.0.0 → 1.1.0)
fix: resolve bug               → PATCH bump (1.0.0 → 1.0.1)
feat!: breaking change         → MAJOR bump (1.0.0 → 2.0.0)
refactor: improve code         → PATCH bump (1.0.0 → 1.0.1)
perf: optimize performance     → PATCH bump (1.0.0 → 1.0.1)

BREAKING CHANGE: in body       → MAJOR bump (1.0.0 → 2.0.0)
```

### Version Management

Versions are managed centrally in `gradle.properties`:
```properties
VERSION_CODE=1
VERSION_NAME=1.0.0
```

And referenced in `app/build.gradle.kts`:
```kotlin
defaultConfig {
    versionCode = project.property("VERSION_CODE").toString().toInt()
    versionName = project.property("VERSION_NAME").toString()
}
```

### Examples

**Example 1: Feature commit**
```bash
# Commit: feat: add prayer times feature
# gradle.properties before:
VERSION_CODE=1
VERSION_NAME=1.0.0

# After workflow runs:
VERSION_CODE=2
VERSION_NAME=1.1.0  # MINOR bump
```

**Example 2: Bug fix commit**
```bash
# Commit: fix: correct time calculation
# gradle.properties before:
VERSION_CODE=2
VERSION_NAME=1.1.0

# After workflow runs:
VERSION_CODE=3
VERSION_NAME=1.1.1  # PATCH bump
```

**Example 3: Breaking change**
```bash
# Commit: feat!: redesign navigation
# gradle.properties before:
VERSION_CODE=3
VERSION_NAME=1.1.1

# After workflow runs:
VERSION_CODE=4
VERSION_NAME=2.0.0  # MAJOR bump
```

### Features

- **Semantic versioning**: Automatically determines version bump based on conventional commits
- **Loop prevention**: Skips execution if last commit was a version bump
- **Error handling**: Validates version extraction and format before proceeding
- **Conflict resolution**: Pulls latest changes with rebase before pushing
- **Security**: Explicit `contents: write` permission scope
- **Fallback**: Defaults to PATCH bump if no conventional commit format is found

### Notes

- Uses semantic-release principles for version calculation
- Requires Node.js for semantic-release tools (installed automatically in workflow)
- Only runs if version changes are detected
- Requires `GITHUB_TOKEN` with write permissions (automatically provided by GitHub Actions)
- Follows [Conventional Commits](https://www.conventionalcommits.org/) specification

