## MTConnect Module for Ignition 8.3

Read-only MTConnect integration that polls MTConnect agents and exposes data as
Ignition tags via a managed tag provider.

### Features
- Self-contained Gateway UI under Connections > MTConnect
- Create, update, and delete MTConnect connections
- Status page with connection health, backoff, and observed tag counts
- Device discovery and one-click "Use Device" selection
- Managed tag provider per connection

### Installation
1. Build the module locally with `./gradlew clean :mtconnect-build:build`.
2. Sign the generated module with `./scripts/sign-module.sh`.
3. Install the signed `.modl` from `mtconnect-build/build/` in the Ignition Gateway.

### Configuration
1. Go to Connections > MTConnect > Connections.
2. Create a connection with:
	- Agent URL (for example `http://mtconnect.mazakcorp.com:5610`)
	- Optional Device Name (leave blank to load devices)
	- Poll Interval (ms)
	- Tag Provider name
3. Go to Connections > MTConnect > Status.
4. Click "Load Devices" and then "Use <Device>" to filter to a specific device.

### Notes
- Tags are created from `/probe`; values update only for data items reported in `/current`.
- The status UI shows observed vs total tags to help validate data coverage.

### Development
Requires JDK 17.

Build:
```
./gradlew clean :mtconnect-build:build
./scripts/sign-module.sh
```

The Gradle build creates `mtconnect-build/build/mtconnect.unsigned.modl`; the signing script creates `mtconnect-build/build/mtconnect.modl`. If signing material does not exist yet, the script creates a simple self-signed certificate under `certs/`; those files are ignored by git. Keep the same generated files if you want future builds to use the same certificate, so Ignition users only need to accept that certificate once.

Verify the module contains the signing payload:
```
unzip -l mtconnect-build/build/mtconnect.modl | grep -E 'certificates.p7b|signatures.properties'
```

Run tests (none currently):
```
./gradlew test
```

### Release (GitHub Actions)
- Push a tag like `v0.1.2` to publish a release with the signed `.modl` artifact.
- Workflows are in `.github/workflows`.

### Contributing
See `CONTRIBUTING.md`.

### License
Licensed under the MIT License. See `LICENSE`.
