#!/usr/bin/env bash

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

VERSION="${MODULE_SIGNER_VERSION:-0.0.1.ia}"
SIGNER_DIR="${MODULE_SIGNER_DIR:-$REPO_ROOT/tools/module-signer}"
SIGNER_JAR="${MODULE_SIGNER_JAR:-$SIGNER_DIR/module-signer-$VERSION-jar-with-dependencies.jar}"
SIGNER_URL="${MODULE_SIGNER_URL:-https://nexus.inductiveautomation.com/repository/inductiveautomation-releases/com/inductiveautomation/ignitionsdk/module-signer/$VERSION/module-signer-$VERSION-jar-with-dependencies.jar}"

mkdir -p "$SIGNER_DIR"

if [ ! -f "$SIGNER_JAR" ]; then
    echo "Downloading IA module-signer $VERSION..."
    curl -fL "$SIGNER_URL" -o "$SIGNER_JAR"
fi

echo "$SIGNER_JAR"
