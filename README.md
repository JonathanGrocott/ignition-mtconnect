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
2. Install the generated `.modl` from `mtconnect-build/build/` in the Ignition Gateway.

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
```

Run tests (none currently):
```
./gradlew test
```

### Release (GitHub Actions)
- Push a tag like `v0.1.0` to publish a release with the `.modl` artifact.
- Workflows are in `.github/workflows`.

### Contributing
See `CONTRIBUTING.md`.

### License
Licensed under the MIT License. See `LICENSE`.
