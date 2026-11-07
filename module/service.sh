#!/system/bin/sh
MODDIR=${0%/*}

setenforce 0

while [ "$(getprop sys.boot_completed)" != "1" ]; do
  sleep 2
done

# Refresh full app list after boot so WebUI shows every installed app
sh "$MODDIR/refresh_pkglist.sh" "$MODDIR"

while true; do
  CLASSPATH="$MODDIR/classes.dex" app_process /system/bin com.floatingmenu.VercelPoller >/dev/null 2>&1
  sleep 5
done &
