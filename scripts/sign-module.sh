#!/usr/bin/env bash

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

KEYSTORE="${MODULE_SIGNING_KEYSTORE:-$REPO_ROOT/certs/module-signing-keystore.jks}"
CERT_CHAIN="${MODULE_SIGNING_CHAIN:-$REPO_ROOT/certs/module-signing-chain.p7b}"
ALIAS="${MODULE_SIGNING_ALIAS:-ignition-mtconnect-self-signed}"
PASSWORD="${MODULE_SIGNING_PASSWORD:-ignition-mtconnect}"
MODULE_IN="${MODULE_IN:-$REPO_ROOT/mtconnect-build/build/mtconnect.unsigned.modl}"
MODULE_OUT="${MODULE_OUT:-$REPO_ROOT/mtconnect-build/build/mtconnect.modl}"

if [ ! -f "$KEYSTORE" ] || [ ! -f "$CERT_CHAIN" ]; then
    "$REPO_ROOT/scripts/create-self-signed-module-cert.sh"
fi

if [ ! -f "$MODULE_IN" ]; then
    echo "Missing unsigned module: $MODULE_IN" >&2
    exit 1
fi

SIGNER_JAR="$("$REPO_ROOT/scripts/download-module-signer.sh" | tail -n 1)"

echo "Signing $(basename "$MODULE_IN") -> $(basename "$MODULE_OUT")"
rm -f "$MODULE_OUT"
java -jar "$SIGNER_JAR" \
    -keystore="$KEYSTORE" \
    -keystore-pwd="$PASSWORD" \
    -alias="$ALIAS" \
    -alias-pwd="$PASSWORD" \
    -chain="$CERT_CHAIN" \
    -module-in="$MODULE_IN" \
    -module-out="$MODULE_OUT"
