#!/bin/bash

# Configuration
PACKAGE_NAME="com.siratalmustaqim.alnoor"
RECEIVER_NAME=".admin.AlNoorDeviceAdminReceiver"
DEVICE_OWNER_COMP="$PACKAGE_NAME/$RECEIVER_NAME"

echo "------------------------------------------------"
echo "Starting Dynamic Device Owner Setup"
echo "------------------------------------------------"

# 1. Connectivity Check
adb wait-for-device
echo "[+] Device detected."

# 2. Enable Airplane Mode
echo "[*] Enabling Airplane Mode..."
adb shell settings put global airplane_mode_on 1
adb shell cmd connectivity airplane-mode enable

# 3. Identify and Disable Account-Owning Packages
echo "[*] Scanning for active accounts..."

# Get the full account dump
ACCOUNT_DUMP=$(adb shell "dumpsys account")

# Extract account types from lines like "Account {name=..., type=com.xiaomi}"
# Use awk for more reliable parsing
ACCOUNT_TYPES=$(echo "$ACCOUNT_DUMP" | grep "Account {" | awk -F'type=' '{print $2}' | awk -F'}' '{print $1}' | tr -d '\r' | sort -u)

# For each account type, find the package from ComponentInfo or append .account
PACKAGES=""
for TYPE in $ACCOUNT_TYPES; do
    # Skip empty types
    if [ -z "$TYPE" ]; then
        continue
    fi
    
    # Look for ComponentInfo{package/...} where type matches
    PKG=$(echo "$ACCOUNT_DUMP" | grep "type=$TYPE}" | awk -F'ComponentInfo{' '{print $2}' | awk -F'/' '{print $1}' | tr -d '\r' | head -1)
    
    if [ -z "$PKG" ]; then
        # Count the number of parts in the type (separated by dots)
        PART_COUNT=$(echo "$TYPE" | tr '.' '\n' | wc -l | tr -d ' ')
        
        if [ "$PART_COUNT" -eq 2 ]; then
            # Only append .account if type has exactly 2 parts (e.g., com.xiaomi -> com.xiaomi.account)
            PKG="${TYPE}.account"
        else
            # Type already has 3+ parts, use it as-is (e.g., com.google.android.gms)
            PKG="$TYPE"
        fi
    fi
    
    if [ -n "$PKG" ]; then
        PACKAGES="$PACKAGES $PKG"
    fi
done

# Get unique packages
PACKAGES=$(echo "$PACKAGES" | tr ' ' '\n' | grep -v '^$' | sort -u | tr '\n' ' ')

if [ -z "$PACKAGES" ]; then
    echo "[!] No active accounts found. Proceeding..."
else
    echo "[*] Disabling the following account-owning packages:"
    echo "$PACKAGES"
    
    for PKG in $PACKAGES; do
        # We use disable-user to hide it from the current user
        OUTPUT=$(adb shell pm disable-user --user 0 "$PKG" 2>&1)
        if [[ $OUTPUT == *"Error"* ]] || [[ $OUTPUT == *"Exception"* ]]; then
            echo "    [!] Failed to disable $PKG: $OUTPUT"
        else
            echo "    [+] Disabled: $PKG"
        fi
    done
fi

# Also disable GMS/GSF just in case they didn't show up in the specific account dump
OUTPUT=$(adb shell pm disable-user --user 0 com.google.android.gms 2>&1)
if [[ $OUTPUT == *"Error"* ]] || [[ $OUTPUT == *"Exception"* ]]; then
    echo "    [!] Failed to disable GMS: $OUTPUT"
else
    echo "    [+] Disabled: com.google.android.gms"
fi

OUTPUT=$(adb shell pm disable-user --user 0 com.google.android.gsf 2>&1)
if [[ $OUTPUT == *"Error"* ]] || [[ $OUTPUT == *"Exception"* ]]; then
    echo "    [!] Failed to disable GSF: $OUTPUT"
else
    echo "    [+] Disabled: com.google.android.gsf"
fi

echo "[+] Account packages hidden."

# 4. Set Device Owner
echo "[*] Attempting to set Device Owner..."
OUTPUT=$(adb shell dpm set-device-owner "$DEVICE_OWNER_COMP" 2>&1)

if [[ $OUTPUT == *"Success"* ]]; then
    echo "[SUCCESS] $PACKAGE_NAME is now the Device Owner!"
else
    echo "[ERROR] Failed to set Device Owner."
    echo "Reason: $OUTPUT"
fi

# 5. Re-enable all packages
echo "[*] Restoring all disabled packages..."
for PKG in $PACKAGES; do
    OUTPUT=$(adb shell pm enable "$PKG" 2>&1)
    if [[ $OUTPUT == *"Error"* ]] || [[ $OUTPUT == *"Exception"* ]]; then
        echo "    [!] Failed to enable $PKG: $OUTPUT"
    else
        echo "    [+] Enabled: $PKG"
    fi
done

OUTPUT=$(adb shell pm enable com.google.android.gms 2>&1)
if [[ $OUTPUT == *"Error"* ]] || [[ $OUTPUT == *"Exception"* ]]; then
    echo "    [!] Failed to enable GMS: $OUTPUT"
else
    echo "    [+] Enabled: com.google.android.gms"
fi

OUTPUT=$(adb shell pm enable com.google.android.gsf 2>&1)
if [[ $OUTPUT == *"Error"* ]] || [[ $OUTPUT == *"Exception"* ]]; then
    echo "    [!] Failed to enable GSF: $OUTPUT"
else
    echo "    [+] Enabled: com.google.android.gsf"
fi

# 6. Disable Airplane Mode
echo "[*] Disabling Airplane Mode..."
adb shell settings put global airplane_mode_on 0
adb shell cmd connectivity airplane-mode disable

# 7. Final Reboot
echo "[*] Rebooting device..."
# adb reboot

echo "------------------------------------------------"
echo "Process complete."