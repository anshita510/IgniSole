# Release Artifacts

- `IgniSole-debug.apk`: debug-signed APK for direct testing
- `IgniSole-release-unsigned.apk`: unsigned release APK
- `SHA256SUMS.txt`: checksums for verification

Verify:

```bash
LC_ALL=C LANG=C shasum -a 256 -c SHA256SUMS.txt
```
