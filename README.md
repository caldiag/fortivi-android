# Fortivi
Full app for automatically logging Android devices into networks captive by FortiGate.
This runs in the background and listens for network changes, generating authentication payloads and sending them over to FortiGate to authenticate your device on your behalf.

### Features
- UI for setting, saving and clearing credentials
- Starts on boot
- Logs up to last 500 events
- Notification service
- Persistent service, allowing app to be closed and authentication to continue as normal.
- Support for Android 7.0 Nougat and up

### TODO
- Add support for custom FortiGate portal addresses (currently hardcoded for testing purposes)
