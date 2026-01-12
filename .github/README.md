# GitHub Workflows

## Main Branch Release

The `auto-release.yml` workflow automatically manages versioning and creates draft releases for the AlNoor app whenever changes are pushed to the `main` branch. It uses **semantic versioning** based on **conventional commit messages** and builds the release APK.

### How it works

1. **Trigger**: Activates on every push to the `main` branch
2. **Loop Prevention**: Checks if the last commit was a version bump (`chore(release):`) to prevent infinite loops
3. **Semantic Release**: 
   - Analyzes commit messages using conventional commits
   - Determines the next version (MAJOR, MINOR, or PATCH)
   - Generates release notes from commits
4. **Version Update**: 
   - Updates `VERSION_CODE` (increments by 1) and `VERSION_NAME` in `gradle.properties`
   - Commits changes back to `main` branch with `[skip ci]` tag
5. **APK Build**: 
   - Decodes keystore from secrets
   - Builds and signs the release APK
6. **Draft Release**: 
   - Creates or updates a GitHub draft release
   - Attaches the built APK
   - Includes generated release notes
   - Creates a tag (e.g., `v1.2.3`) but keeps release in draft mode
7. **Manual Publishing**: Release remains in draft until manually published from GitHub

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
# Draft release v1.1.0 created with APK
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
# Draft release v1.1.1 created with APK
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
# Draft release v2.0.0 created with APK
```

### Features

- **Semantic versioning**: Automatically determines version bump based on conventional commits
- **Draft releases**: All releases start as drafts for manual review before publishing
- **Automated builds**: Builds and signs APK with every release
- **Release notes**: Automatically generated from commit messages
- **Loop prevention**: Skips execution for version bump commits
- **No CHANGELOG.md**: Release notes are in GitHub releases, not a file
- **Git integration**: Commits version changes back to main branch

### Publishing a Release

1. Push commits to `main` branch using conventional commit messages
2. Workflow automatically runs and creates/updates a draft release
3. Go to GitHub Releases page
4. Review the draft release and its attached APK
5. Click "Publish release" when ready to make it public

### Required Secrets

The workflow requires these GitHub secrets:
- `GH_PAT`: GitHub Personal Access Token with repo access
- `ANDROID_KEYSTORE`: Base64-encoded Android keystore file
- `SIGNING_STORE_PASSWORD`: Keystore password
- `SIGNING_KEY_ALIAS`: Key alias in keystore
- `SIGNING_KEY_PASSWORD`: Key password

### Notes

- Uses semantic-release for version calculation and release notes generation
- No tags are created until release is published
- Only commits `gradle.properties`, not CHANGELOG.md
- Requires Node.js and Java 17 (installed automatically in workflow)
- Follows [Conventional Commits](https://www.conventionalcommits.org/) specification
- Draft releases can be edited before publishing
