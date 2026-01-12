#!/bin/bash
set -e

# This script builds the signed release APK and packages it for semantic-release
# It's called by semantic-release during the publish step

echo "Building signed release APK..."
./gradlew assembleRelease --console=plain --no-daemon

echo "Finding and packaging APK..."
APK_PATH=$(find app/build/outputs/apk/release -name "*.apk" | head -1)

if [ -z "$APK_PATH" ]; then
    echo "Error: No APK file found in app/build/outputs/apk/release"
    exit 1
fi

echo "Found APK: $APK_PATH"
cp "$APK_PATH" al-noor.apk
ls -lh al-noor.apk

echo "✓ APK built and packaged successfully"
