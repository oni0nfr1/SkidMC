#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
KEY_DIR="${SKIDMC_SIGNING_DIR:-$HOME/.config/skidmc}"
KEY_FILE="${SKIDMC_SIGNING_KEY_FILE:-$KEY_DIR/signing-key.asc}"
PASSWORD_FILE="${SKIDMC_SIGNING_PASSWORD_FILE:-$KEY_DIR/signing-passphrase}"
CENTRAL_USERNAME_FILE="${SKIDMC_MAVEN_CENTRAL_USERNAME_FILE:-$KEY_DIR/maven-central-username}"
CENTRAL_PASSWORD_FILE="${SKIDMC_MAVEN_CENTRAL_PASSWORD_FILE:-$KEY_DIR/maven-central-password}"
usage() {
  cat <<'EOF'
Usage:
  scripts/publish-signed.sh local
  scripts/publish-signed.sh central

Required secret files by default for both modes:
  ~/.config/skidmc/signing-key.asc
  ~/.config/skidmc/signing-passphrase

Required for central publishing:
  ORG_GRADLE_PROJECT_mavenCentralUsername and ORG_GRADLE_PROJECT_mavenCentralPassword

  If those environment variables are not set, this script reads these files by default:
  ~/.config/skidmc/maven-central-username
  ~/.config/skidmc/maven-central-password

Optional overrides:
  SKIDMC_SIGNING_DIR
  SKIDMC_SIGNING_KEY_FILE
  SKIDMC_SIGNING_PASSWORD_FILE
  SKIDMC_MAVEN_CENTRAL_USERNAME_FILE
  SKIDMC_MAVEN_CENTRAL_PASSWORD_FILE
  GRADLE_USER_HOME
EOF
}

if [[ $# -ne 1 ]]; then
  usage
  exit 1
fi

if [[ ! -f "$KEY_FILE" ]]; then
  echo "Signing key file not found: $KEY_FILE" >&2
  exit 1
fi

if [[ ! -f "$PASSWORD_FILE" ]]; then
  echo "Signing password file not found: $PASSWORD_FILE" >&2
  exit 1
fi

export ORG_GRADLE_PROJECT_signingInMemoryKey="$(<"$KEY_FILE")"
export ORG_GRADLE_PROJECT_signingInMemoryKeyPassword="$(<"$PASSWORD_FILE")"

case "$1" in
  local)
    TASK=":skid-api:publishToMavenLocal"
    ;;
  central)
    if [[ -z "${ORG_GRADLE_PROJECT_mavenCentralUsername:-}" ]]; then
      if [[ ! -f "$CENTRAL_USERNAME_FILE" ]]; then
        echo "Maven Central username not found: set ORG_GRADLE_PROJECT_mavenCentralUsername or create $CENTRAL_USERNAME_FILE" >&2
        exit 1
      fi
      export ORG_GRADLE_PROJECT_mavenCentralUsername="$(<"$CENTRAL_USERNAME_FILE")"
    fi

    if [[ -z "${ORG_GRADLE_PROJECT_mavenCentralPassword:-}" ]]; then
      if [[ ! -f "$CENTRAL_PASSWORD_FILE" ]]; then
        echo "Maven Central password not found: set ORG_GRADLE_PROJECT_mavenCentralPassword or create $CENTRAL_PASSWORD_FILE" >&2
        exit 1
      fi
      export ORG_GRADLE_PROJECT_mavenCentralPassword="$(<"$CENTRAL_PASSWORD_FILE")"
    fi

    TASK=":skid-api:publishAndReleaseToMavenCentral"
    ;;
  *)
    usage
    exit 1
    ;;
esac

cd "$ROOT_DIR"
exec ./gradlew "$TASK"
