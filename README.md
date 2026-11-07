# Zygisk Mode Menu — Virtus V3 (@Hivirtus)

Magisk / KernelSU / SukiSU compatible **Zygisk** module with **WebUI** app picker and floating-menu runtime (`classes.dex`).

## Current release (from Drive)

| Field | Value |
|--------|--------|
| ID | `zygisk_floating_menu` |
| Version | **v25** (`versionCode=25`) |
| WebUI | `webroot/index.html` |

Flash the built ZIP in your root manager (Magisk, KernelSU, SukiSU, MMRL, etc.). Use **WebUI** or **Action** on the module page to open the menu.

## Build flashable ZIP

```bash
./build.sh
```

Output: `dist/zygisk_floating_menu-v25.zip`

## Layout

- `zygisk/*.so` — Zygisk injection (arm64 / armeabi-v7a)
- `classes.dex` — overlay / poller daemon (`service.sh`)
- `target_packages.txt` — apps to hook (managed via WebUI)
- `webroot/` — module WebUI assets
- `post-fs-data.sh` / `service.sh` — boot + daemon

## Rules

- **ZIP flash only** — no separate APK install
- **Local config** — package list on device via WebUI
- JNI / overlay **source** for this v25 build is not in this repo (prebuilt `.so` + `classes.dex` only). Older source commits exist in git history before the clean skeleton.
