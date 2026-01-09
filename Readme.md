Steps:

1. Install Dhizuku on your Android device
Download and install the Dhizuku app from the Play Store. This app will be used to gain device owner permissions.


2. Set up ADB on your PC

Download and install the Android SDK Platform Tools (ADB) for your OS.

Enable Developer Options and USB Debugging on your device.



3. Connect your device to the PC via USB and open a terminal/command prompt
Make sure your PC detects your device:

adb devices

You should see your device listed.


4. Identify all accounts and apps creating accounts
If you don’t know all the apps that manage accounts on your device, run:

adb shell dumpsys account | grep "Account {"

This will list all active accounts so you can find which apps you need to disable.


5. Disable all existing accounts temporarily
The main restriction on setting device owner mode is that there can’t be any active accounts on the device. Instead of deleting, you disable all accounts and the apps that create them (like Google accounts).
Use these commands to disable accounts/apps:

adb shell pm disable-user --user 0 com.google.android.gsf
adb shell pm disable-user --user 0 com.google.android.gms
# Add other account-related apps as needed based on the previous step

Note: Adjust package names based on your accounts/apps.


6. Set Dhizuku as device owner
Run this command:

adb shell dpm set-device-owner com.rosan.dhizuku/.receivers.DPMReceiver

This will give Dhizuku device owner privileges without a factory reset.


7. Re-enable all the apps/accounts you disabled
After device owner is set, re-enable the apps:

adb shell pm enable com.google.android.gsf
adb shell pm enable com.google.android.gms
# Re-enable other disabled apps as needed


8. Reboot your device

adb reboot

After reboot, Dhizuku will have device owner mode enabled, and all your accounts and apps will be back intact.




---

Important disclaimer:

This process involves disabling system apps and accounts temporarily, which can cause issues if not done carefully. If you lose any accounts, apps, or data during this process, do not blame me — you are doing this at your own risk. Always back up your important data before attempting.


---

Notes:

This worked perfectly on my Galaxy Tab A8 running Android 14.

You don’t lose any data or have to factory reset.

Be careful disabling apps — don’t disable anything critical to your device’s basic functions.

You can automate the disabling/enabling commands via batch or shell scripts if you want to speed up the process.

