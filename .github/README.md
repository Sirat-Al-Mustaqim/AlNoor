# GitHub Workflows

## Develop Branch Release

The `auto-release.yml` workflow automatically manages versioning and creates draft releases for the AlNoor app whenever changes are pushed to the `develop` branch. It uses **semantic versioning** based on **PR labels** and builds the release APK.

### How it works

1. **Trigger**: Activates on:
   - Every push to the `develop` branch
   - Pull request events (for auto-labeling)
2. **Release Drafter**: 
   - Analyzes PR labels to determine version bump
   - Determines the next version (MAJOR, MINOR, or PATCH)
   - Generates release notes from merged PRs
3. **Version Update**: 
   - Updates `VERSION_CODE` (increments by 1) and `VERSION_NAME` in `gradle.properties`
   - Commits changes back to `develop` branch with `[skip ci]` tag
4. **APK Build**: 
   - Decodes keystore from secrets
   - Builds and signs the release APK
5. **Draft Release**: 
   - Creates or updates a GitHub draft release
   - Attaches the built APK
   - Includes generated release notes
   - Creates a tag (e.g., `v1.2.3`) but keeps release in draft mode
6. **Manual Publishing**: Release remains in draft until manually published from GitHub

### PR Labels and Version Bumping

The workflow uses PR labels to determine version bumps:

```
major label                    → MAJOR bump (1.0.0 → 2.0.0)
minor, enhancement, feature    → MINOR bump (1.0.0 → 1.1.0)
patch, fix, bugfix, bug        → PATCH bump (1.0.0 → 1.0.1)

Default (no label)             → PATCH bump (1.0.0 → 1.0.1)
```

**Auto-labeling**: PRs are automatically labeled based on title:
- `🚀` or `🎉` in title → `feature` label
- `🐛` or `bug` in title → `bug` label
- `bugfix` in title → `bugfix` label
- Modifies `*.md` files → `docs` label

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

**Example 1: Feature PR**
```bash
# PR title: 🚀 Add prayer times feature
# Auto-labeled with: feature
# gradle.properties before:
VERSION_CODE=1
VERSION_NAME=1.0.0

# After merging to develop:
VERSION_CODE=2
VERSION_NAME=1.1.0  # MINOR bump
# Draft release v1.1.0 created with APK
```

**Example 2: Bug fix PR**
```bash
# PR title: 🐛 Fix time calculation
# Auto-labeled with: bug
# gradle.properties before:
VERSION_CODE=2
VERSION_NAME=1.1.0

# After merging to develop:
VERSION_CODE=3
VERSION_NAME=1.1.1  # PATCH bump
# Draft release v1.1.1 created with APK
```

**Example 3: Major update PR**
```bash
# PR manually labeled with: major
# gradle.properties before:
VERSION_CODE=3
VERSION_NAME=1.1.1

# After merging to develop:
VERSION_CODE=4
VERSION_NAME=2.0.0  # MAJOR bump
# Draft release v2.0.0 created with APK
```

### Features

- **Semantic versioning**: Automatically determines version bump based on PR labels
- **Draft releases**: All releases start as drafts for manual review before publishing
- **Automated builds**: Builds and signs APK with every release
- **Release notes**: Automatically generated from merged PRs with collapsible details
- **Auto-labeling**: PRs are automatically labeled based on title and files changed
- **Organized changelog**: PRs grouped into categories (Enhancements, Bug Fixes, etc.)
- **Git integration**: Commits version changes back to develop branch

### Publishing a Release

1. Create a PR to `develop` branch with appropriate title (use emojis for auto-labeling)
2. Merge the PR to `develop`
3. Workflow automatically runs and creates/updates a draft release
4. Go to GitHub Releases page
5. Review the draft release and its attached APK
6. Click "Publish release" when ready to make it public

### Required Secrets

The workflow requires these GitHub secrets:
- `GH_PAT`: GitHub Personal Access Token with repo access
- `ANDROID_KEYSTORE`: Base64-encoded Android keystore file
- `SIGNING_STORE_PASSWORD`: Keystore password
- `SIGNING_KEY_ALIAS`: Key alias in keystore
- `SIGNING_KEY_PASSWORD`: Key password

### Notes

- Uses release-drafter v6 for version calculation and release notes generation
- Tags are created with draft releases but not pushed until published
- Only commits `gradle.properties`, not CHANGELOG.md
- Requires Java 17 (installed automatically in workflow)
- Release notes are organized by PR category
- Draft releases can be edited before publishing
