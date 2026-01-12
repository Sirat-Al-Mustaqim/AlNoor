#!/bin/bash
set -e

# This script builds the signed release APK and packages it with version-specific naming
# Usage: ./build-and-package-apk.sh <version>

VERSION="$1"

if [ -z "$VERSION" ]; then
    echo "Error: Version parameter required"
    echo "Usage: $0 <version>"
    exit 1
fi

echo "Building signed release APK..."
./gradlew assembleRelease --console=plain --no-daemon

echo "Finding and packaging APK..."
APK_PATH=$(find app/build/outputs/apk/release -name "*.apk" | head -1)

if [ -z "$APK_PATH" ]; then
    echo "Error: No APK file found in app/build/outputs/apk/release"
    exit 1
fi

echo "Found APK: $APK_PATH"

# Copy with version-specific name
OUTPUT_NAME="al-noor-v${VERSION}.apk"
cp "$APK_PATH" "$OUTPUT_NAME"
ls -lh "$OUTPUT_NAME"

echo "✓ APK built and packaged successfully as $OUTPUT_NAME"
