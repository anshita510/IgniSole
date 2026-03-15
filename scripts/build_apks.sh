#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
APP_DIR="$REPO_ROOT/IgniSoleApplication"
RELEASES_DIR="$REPO_ROOT/releases"

if [[ ! -d "$APP_DIR" ]]; then
  echo "Error: missing app directory at $APP_DIR" >&2
  exit 1
fi

if [[ -z "${JAVA_HOME:-}" ]]; then
  if [[ -d "/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home" ]]; then
    JAVA_HOME="/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
  fi
fi

if [[ -n "${JAVA_HOME:-}" ]]; then
  export JAVA_HOME
  export PATH="$JAVA_HOME/bin:$PATH"
fi

if ! command -v java >/dev/null 2>&1; then
  echo "Error: java not found. Install Java 17+." >&2
  exit 1
fi

JAVA_VERSION="$(java -version 2>&1 | awk -F\" '/version/ {print $2}')"
JAVA_MAJOR="${JAVA_VERSION%%.*}"
if [[ "$JAVA_MAJOR" == "1" ]]; then
  JAVA_MAJOR="$(echo "$JAVA_VERSION" | cut -d. -f2)"
fi
if [[ "${JAVA_MAJOR:-0}" -lt 17 ]]; then
  echo "Error: Java 17+ required. Detected Java $JAVA_VERSION." >&2
  exit 1
fi

ANDROID_SDK_ROOT="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-$HOME/Library/Android/Sdk}}"
if [[ ! -d "$ANDROID_SDK_ROOT" ]]; then
  echo "Error: Android SDK not found at $ANDROID_SDK_ROOT" >&2
  exit 1
fi
export ANDROID_SDK_ROOT
export ANDROID_HOME="$ANDROID_SDK_ROOT"

mkdir -p "$RELEASES_DIR"

cd "$APP_DIR"
GRADLE_USER_HOME="$APP_DIR/.gradle-local" ./gradlew :app:assembleDebug :app:assembleRelease

cp "$APP_DIR/app/build/outputs/apk/debug/app-debug.apk" "$RELEASES_DIR/IgniSole-debug.apk"
cp "$APP_DIR/app/build/outputs/apk/release/app-release-unsigned.apk" "$RELEASES_DIR/IgniSole-release-unsigned.apk"

(
  cd "$RELEASES_DIR"
  LC_ALL=C LANG=C shasum -a 256 IgniSole-debug.apk IgniSole-release-unsigned.apk > SHA256SUMS.txt
)

echo "APKs refreshed in: $RELEASES_DIR"
