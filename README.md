# Zygisk Mode Menu — Virtus V3 (@Hivirtus)

Magisk / KernelSU / SukiSU compatible **Zygisk** module with **WebUI** app picker and floating-menu runtime (`classes.dex`).

## Current release (from Drive)

| Field | Value |
|--------|--------|
| ID | `zygisk_floating_menu` |
| Version | **v26** (`versionCode=26`) |
| WebUI | `webroot/index.html` |

Flash the built ZIP in your root manager (Magisk, KernelSU, SukiSU, MMRL, etc.). Use **WebUI** or **Action** on the module page to open the menu.

## Build flashable ZIP

```bash
./build.sh
```

Output: `dist/zygisk_floating_menu-v26.zip`

### SIM + mobile data mock (v26)

In the floating **SIM settings** panel:

1. Turn on **Enable SIM 1 Mocking** and enter your number (e.g. `8273202234`).
2. Turn on **Mock mobile data (SIM 1)**.
3. Tap **Save SIM Settings**.

Target apps then see SIM 1 as the default **mobile data** subscription (`getDefaultDataSubscriptionId` / related APIs), aligned with the mocked number and subscription id `1`.

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
