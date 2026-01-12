#!/bin/bash
set -e

# This script updates VERSION_CODE and VERSION_NAME in gradle.properties
# It's called by semantic-release with the new semantic version as a parameter

NEW_VERSION_NAME="$1"
PROPS_FILE="gradle.properties"

if [ -z "$NEW_VERSION_NAME" ]; then
    echo "Error: No version provided"
    echo "Usage: $0 <new-version>"
    exit 1
fi

# Extract current VERSION_CODE using portable sed
CURRENT_VERSION_CODE=$(sed -n 's/^VERSION_CODE=\([0-9]*\)$/\1/p' "$PROPS_FILE")

if [ -z "$CURRENT_VERSION_CODE" ]; then
    echo "Error: Could not extract VERSION_CODE from $PROPS_FILE"
    exit 1
fi

# Increment VERSION_CODE
NEW_VERSION_CODE=$((CURRENT_VERSION_CODE + 1))

echo "Updating versions in $PROPS_FILE:"
echo "  VERSION_CODE: $CURRENT_VERSION_CODE → $NEW_VERSION_CODE"
echo "  VERSION_NAME: → $NEW_VERSION_NAME"

# Update VERSION_CODE
sed -i.bak "s/^VERSION_CODE=.*/VERSION_CODE=$NEW_VERSION_CODE/" "$PROPS_FILE"

# Update VERSION_NAME
sed -i.bak "s/^VERSION_NAME=.*/VERSION_NAME=$NEW_VERSION_NAME/" "$PROPS_FILE"

# Remove backup file
rm -f "${PROPS_FILE}.bak"

echo "✓ Successfully updated $PROPS_FILE"
