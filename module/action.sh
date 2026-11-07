#!/system/bin/sh
ID=zygisk_floating_menu
URI="ksu://webui?id=${ID}"

echo "Virtus V3 — opening app picker..."

launch() {
  am start --user 0 "$@" 2>&1
}

# Background launch so SukiSU Action screen does not block WebUI
bg_launch() {
  (
    sleep 1
    am start --user 0 "$@" >/dev/null 2>&1
  ) &
}

# SukiSU Ultra — module id in URI ?id= is required
if pm path com.sukisu.ultra >/dev/null 2>&1; then
  bg_launch -a android.intent.action.VIEW \
    -d "${URI}" \
    -n com.sukisu.ultra/com.sukisu.ultra.ui.webui.WebUIActivity
  echo "WebUI opening... (also try WebUI button on module page)"
  exit 0
fi

# KsuWebUI Standalone
if pm path io.github.a13e300.ksuwebui >/dev/null 2>&1; then
  bg_launch -n io.github.a13e300.ksuwebui/.WebUIActivity -e id "${ID}"
  echo "WebUI opening via KsuWebUI..."
  exit 0
fi

# KernelSU
if pm path me.weishu.kernelsu >/dev/null 2>&1; then
  bg_launch -a android.intent.action.VIEW -d "${URI}" \
    -n me.weishu.kernelsu/me.weishu.kernelsu.ui.webui.WebUIActivity
  echo "WebUI opening..."
  exit 0
fi
if pm path com.vvb2060.kernelsu >/dev/null 2>&1; then
  bg_launch -a android.intent.action.VIEW -d "${URI}" \
    -n com.vvb2060.kernelsu/com.vvb2060.kernelsu.ui.webui.WebUIActivity
  exit 0
fi

# MMRL / WebUI X
for PKG in com.dergoogler.mmrl.wx com.dergoogler.mmrl io.github.mmrl; do
  pm path "$PKG" >/dev/null 2>&1 || continue
  bg_launch -n "${PKG}/.ui.activity.webui.WebUIActivity" -e MOD_ID "${ID}"
  echo "WebUI opening via MMRL..."
  exit 0
done

# KSU Next
if pm path com.rifsxd.ksunext >/dev/null 2>&1 || pm path me.rifsxds.ksunext >/dev/null 2>&1; then
  PKG=$(pm path com.rifsxd.ksunext 2>/dev/null | head -1 | cut -d: -f2 | cut -d/ -f1)
  [ -z "$PKG" ] && PKG=$(pm path me.rifsxds.ksunext 2>/dev/null | head -1 | cut -d: -f2 | cut -d/ -f1)
  bg_launch -a android.intent.action.VIEW -d "${URI}" \
    -n "${PKG}/${PKG}.ui.webui.WebUIActivity"
  exit 0
fi

if pm path me.bmax.apatch >/dev/null 2>&1; then
  bg_launch -a android.intent.action.VIEW -d "apatch://webui?id=${ID}"
  exit 0
fi
if pm path com.topjohnwu.magisk >/dev/null 2>&1; then
  bg_launch -a android.intent.action.VIEW -d "magisk://webui?id=${ID}"
  exit 0
fi

echo ""
echo "Use the WebUI button on the module page."
echo "Or install: KsuWebUI Standalone (io.github.a13e300.ksuwebui)"
exit 1
