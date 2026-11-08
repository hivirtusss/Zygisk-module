#!/system/bin/sh
# Run early boot — strip wildcard before any app injection decisions.
MODDIR=${0%/*}
TARGET="$MODDIR/target_packages.txt"

sh "$MODDIR/refresh_pkglist.sh" "$MODDIR"

if [ ! -f "$TARGET" ]; then
  : > "$TARGET"
  exit 0
fi

if grep -qx '*' "$TARGET" 2>/dev/null; then
  grep -v '^\*$' "$TARGET" > "$TARGET.tmp" 2>/dev/null || : > "$TARGET.tmp"
  mv "$TARGET.tmp" "$TARGET"
fi

# Empty = hook too much on some devices (Xiaomi launcher). Require explicit WebUI list.
if ! grep -q '[^[:space:]]' "$TARGET" 2>/dev/null; then
  printf '%s\n' 'none' > "$TARGET"
fi

# Drop unsafe packages that crash when hooked (root / system UI / launchers including MI)
BLOCKED="me.weishu.kernelsu com.vvb2060.kernelsu com.rifsxd.ksunext me.rifsxds.ksunext com.sukisu.ultra com.topjohnwu.magisk me.bmax.apatch io.github.mmrl com.android.systemui com.android.settings com.android.vending com.google.android.gms com.google.android.gsf com.android.launcher com.android.launcher3 com.miui.home com.mi.android.globallauncher com.miui.systemui com.google.android.apps.nexuslauncher com.oppo.launcher com.coloros.launcher com.oneplus.launcher net.oneplus.launcher com.oplus.launcher com.android.keyguard"
for pkg in $BLOCKED; do
  grep -vx "$pkg" "$TARGET" > "$TARGET.tmp" 2>/dev/null && mv "$TARGET.tmp" "$TARGET"
done
