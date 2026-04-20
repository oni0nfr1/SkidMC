#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
KEY_DIR="${SKIDMC_SIGNING_DIR:-$HOME/.config/skidmc}"
KEY_FILE="${SKIDMC_SIGNING_KEY_FILE:-$KEY_DIR/signing-key.asc}"
PASSWORD_FILE="${SKIDMC_SIGNING_PASSWORD_FILE:-$KEY_DIR/signing-passphrase}"
usage() {
  cat <<'EOF'
Usage:
  scripts/publish-signed.sh local
  scripts/publish-signed.sh central

Required secret files by default:
  ~/.config/skidmc/signing-key.asc
  ~/.config/skidmc/signing-passphrase

Optional overrides:
  SKIDMC_SIGNING_DIR
  SKIDMC_SIGNING_KEY_FILE
  SKIDMC_SIGNING_PASSWORD_FILE
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
    TASK=":skid:publishToMavenLocal"
    ;;
  central)
    TASK=":skid:publishAndReleaseToMavenCentral"
    ;;
  *)
    usage
    exit 1
    ;;
esac

cd "$ROOT_DIR"
exec ./gradlew "$TASK"
