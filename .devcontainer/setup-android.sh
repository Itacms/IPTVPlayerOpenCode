#!/usr/bin/env bash
set -e
chmod +x gradlew
SDK=/opt/android-sdk
mkdir -p "$SDK/cmdline-tools"
cd /tmp
curl -fsSL https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -o /tmp/cmdline.zip
unzip -q cmdline.zip -d "$SDK/cmdline-tools"
mv "$SDK/cmdline-tools/cmdline-tools" "$SDK/cmdline-tools/latest"
export PATH="$SDK/cmdline-tools/latest/bin:$PATH"
yes | sdkmanager --licenses >/dev/null 2>&1 || true
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
echo "export ANDROID_HOME=$SDK" >> ~/.bashrc
echo "export PATH=\$ANDROID_HOME/cmdline-tools/latest/bin:\$ANDROID_HOME/platform-tools:\$PATH" >> ~/.bashrc
echo "Android SDK ready"