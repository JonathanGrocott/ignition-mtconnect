#!/usr/bin/env bash

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

SIGNING_DIR="${MODULE_SIGNING_DIR:-$REPO_ROOT/certs}"
KEYSTORE="${MODULE_SIGNING_KEYSTORE:-$SIGNING_DIR/module-signing-keystore.jks}"
CERT_PEM="${MODULE_SIGNING_CERT_PEM:-$SIGNING_DIR/module-signing-cert.pem}"
CERT_CHAIN="${MODULE_SIGNING_CHAIN:-$SIGNING_DIR/module-signing-chain.p7b}"
ALIAS="${MODULE_SIGNING_ALIAS:-ignition-mtconnect-self-signed}"
PASSWORD="${MODULE_SIGNING_PASSWORD:-ignition-mtconnect}"
DNAME="${MODULE_SIGNING_DNAME:-CN=Ignition MTConnect Module, OU=Ignition MTConnect, O=J.Grocott, L=San Francisco, ST=CA, C=US}"
VALIDITY_DAYS="${MODULE_SIGNING_VALIDITY_DAYS:-3650}"

mkdir -p "$SIGNING_DIR"

if [ -f "$KEYSTORE" ]; then
    echo "Using existing module signing keystore: $KEYSTORE"
else
    echo "Creating self-signed module signing keystore: $KEYSTORE"
    keytool -genkeypair \
        -alias "$ALIAS" \
        -keyalg RSA \
        -keysize 2048 \
        -validity "$VALIDITY_DAYS" \
        -keystore "$KEYSTORE" \
        -storepass "$PASSWORD" \
        -keypass "$PASSWORD" \
        -dname "$DNAME" \
        -ext KeyUsage=digitalSignature \
        -ext ExtendedKeyUsage=codeSigning
fi

echo "Exporting PEM certificate: $CERT_PEM"
keytool -exportcert \
    -alias "$ALIAS" \
    -keystore "$KEYSTORE" \
    -storepass "$PASSWORD" \
    -rfc \
    -file "$CERT_PEM"

if ! command -v openssl >/dev/null 2>&1; then
    echo "openssl is required to create the PKCS7 chain file expected by module-signer." >&2
    exit 1
fi

echo "Exporting PKCS7 certificate chain: $CERT_CHAIN"
openssl crl2pkcs7 \
    -nocrl \
    -certfile "$CERT_PEM" \
    -out "$CERT_CHAIN" \
    -outform DER

echo "Self-signed module signing certificate is ready."
echo "  Keystore: $KEYSTORE"
echo "  Alias: $ALIAS"
echo "  Chain: $CERT_CHAIN"
