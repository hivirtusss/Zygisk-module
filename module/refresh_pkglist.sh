#!/system/bin/sh
# Cache user-installed phone apps only (no system/accessibility packages).
MODDIR="${1:-${0%/*}}"
PKGFILE="$MODDIR/pkglist.txt"
WEBFILE="$MODDIR/webroot/pkglist.txt"

mkdir -p "$MODDIR/webroot"

{
  pm list packages -3 2>/dev/null
  pm list packages -3 -u 2>/dev/null
  cmd package list packages --user 0 -3 2>/dev/null
} | sed 's/^package://g' | sort -u | grep -v '^\s*$' > "$PKGFILE" 2>/dev/null || : > "$PKGFILE"

cp "$PKGFILE" "$WEBFILE" 2>/dev/null || cat "$PKGFILE" > "$WEBFILE" 2>/dev/null || : > "$WEBFILE"
chmod 644 "$PKGFILE" "$WEBFILE" 2>/dev/null
