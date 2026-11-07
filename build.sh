#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
MOD="$ROOT/module"
OUT="$ROOT/dist"
PROP="$MOD/module.prop"

id=$(grep '^id=' "$PROP" | cut -d= -f2)
ver=$(grep '^version=' "$PROP" | cut -d= -f2)
mkdir -p "$OUT"
name="${id}-${ver}.zip"
rm -f "$OUT/$name"
(cd "$MOD" && zip -r9 "$OUT/$name" . -x '*.DS_Store')
echo "Built: $OUT/$name"
