#!/usr/bin/env python3
"""Merge Mock Net patches onto decompiled v25 classes.dex smali tree."""
import re
import shutil
import sys
from pathlib import Path

def main() -> None:
    if len(sys.argv) != 3:
        print("usage: merge-mock-dex.py <v25-base-smali-root> <out-smali-root>", file=sys.stderr)
        sys.exit(1)
    root = Path(__file__).resolve().parents[1]
    v25_base = Path(sys.argv[1]) / "com/floatingmenu"
    out_root = Path(sys.argv[2])
    out = out_root / "com/floatingmenu"
    dex = root / "dex-smali/com/floatingmenu"

    e171_ml = None
    e171_dir = Path("/tmp/e171-smali/com/floatingmenu")
    if not e171_dir.is_dir():
        print("Run baksmali on e171 dex first (build pipeline)", file=sys.stderr)
        sys.exit(1)

    out.parent.mkdir(parents=True, exist_ok=True)
    shutil.copytree(v25_base, out, dirs_exist_ok=True)

    for f in ("MenuLoader$18.smali", "MenuLoader$3.smali", "VercelPoller.smali"):
        shutil.copy2(e171_dir / f, out / f)
    for f in (
        "b.smali", "FloatingMenu.smali", "FloatingMenu$1.smali",
        "FloatingMenu$1$3.smali", "FloatingMenu$1$3$1.smali", "FloatingMenu$1$3$1$1.smali",
        "FloatingMenu$1$4.smali", "FloatingMenu$1$4$1.smali",
    ):
        shutil.copy2(dex / f, out / f)

    ml = (e171_dir / "MenuLoader.smali").read_text()
    dex_ml = (dex / "MenuLoader.smali").read_text()
    pat = (
        r"(\.method public static updateMockSimSettingsEx2\(ZLjava/lang/String;Ljava/lang/String;"
        r"ZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZ\)V.*?\.end method\n\n"
        r"\.method public static updateMockSimSettingsEx3\(.*?\.end method\n)"
    )
    ex_block = re.search(pat, dex_ml, re.S).group(1)
    pat_old = (
        r"\.method public static updateMockSimSettingsEx2\(ZLjava/lang/String;Ljava/lang/String;"
        r"ZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZ\)V.*?\.end method\n"
    )
    ml = re.sub(pat_old, ex_block + "\n", ml, count=1, flags=re.S)

    needle = "    :goto_138\n    if-nez v9, :cond_194"
    guard = """    :goto_138
    sget-boolean v13, Lcom/floatingmenu/MenuLoader;->sIsTargetPackage:Z

    if-nez v13, :cond_target_hooks_ok

    const-string v13, "Skipping hooks: package not in WebUI target list"

    invoke-static {v3, v13}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    return-void

    :cond_target_hooks_ok
    if-nez v9, :cond_194"""
    if needle not in ml:
        sys.exit("MenuLoader init anchor missing")
    ml = ml.replace(needle, guard, 1)
    (out / "MenuLoader.smali").write_text(ml)
    # copy rest of packages from v25-base
    for item in Path(sys.argv[1]).iterdir():
        if item.name == "com":
            continue
        dest = out_root / item.name
        if item.is_dir():
            shutil.copytree(item, dest, dirs_exist_ok=True)
        else:
            shutil.copy2(item, dest)

if __name__ == "__main__":
    main()
