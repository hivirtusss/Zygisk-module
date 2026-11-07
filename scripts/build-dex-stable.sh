#!/usr/bin/env bash
# Rebuild classes.dex: user v25 base + Mock Net UI + data hooks + target-only init.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SMALI_JAR="${SMALI_JAR:-/tmp/smali.jar}"
BAKSMALI_JAR="${BAKSMALI_JAR:-/tmp/baksmali.jar}"
V25_DEX="${V25_DEX:-$ROOT/module/classes.dex.v25.baseline}"

if [[ ! -f "$SMALI_JAR" || ! -f "$BAKSMALI_JAR" ]]; then
  echo "Need smali.jar and baksmali.jar on PATH or /tmp" >&2
  exit 1
fi

WORKDIR="$(mktemp -d)"
trap 'rm -rf "$WORKDIR"' EXIT

java -jar "$BAKSMALI_JAR" d "$V25_DEX" -o "$WORKDIR/v25-base"
python3 "$ROOT/scripts/merge-mock-dex.py" "$WORKDIR/v25-base" "$WORKDIR/out"
java -jar "$SMALI_JAR" assemble "$WORKDIR/out" -o "$ROOT/module/classes.dex"
echo "Wrote $ROOT/module/classes.dex"
