# Module signing certificates

Run this from the repository root to create a simple self-signed module signing certificate:

```bash
./scripts/create-self-signed-module-cert.sh
```

The generated keystore, PEM certificate, and PKCS7 chain are ignored by git because they include local signing material. Keep the same generated files if you want future builds to use the same certificate, so Ignition users only need to accept that certificate once.
