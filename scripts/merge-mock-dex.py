#!/usr/bin/env python3
"""Virtus v25 classes.dex + Mock Net UI + data-subscription hooks only."""
import re
import shutil
import subprocess
import sys
from pathlib import Path

def main() -> None:
    root = Path(__file__).resolve().parents[1]
    v25_dex = Path(sys.argv[1]) if len(sys.argv) > 1 else root / "module" / "classes.dex.v25.baseline"
    out_dex = Path(sys.argv[2]) if len(sys.argv) > 2 else root / "module" / "classes.dex"
    baksmali = Path("/tmp/baksmali.jar")
    smali = Path("/tmp/smali.jar")
    e171_dex = Path("/tmp/e171.dex")

    work = Path("/tmp/merge-mock-work")
    if work.exists():
        shutil.rmtree(work)
    work.mkdir()

    for jar in (baksmali, smali):
        if not jar.is_file():
            sys.exit(f"Missing {jar}")

    subprocess.check_call(["java", "-jar", str(baksmali), "d", str(v25_dex), "-o", str(work / "v25-base")])
    if not e171_dex.is_file():
        subprocess.check_call(
            ["git", "-C", str(root), "show", "e171b56:module/classes.dex"],
            stdout=open(e171_dex, "wb"),
        )
    subprocess.check_call(["java", "-jar", str(baksmali), "d", str(e171_dex), "-o", str(work / "e171")])

    base = work / "v25-base/com/floatingmenu"
    mono = work / "out/com/floatingmenu"
    e171 = work / "e171/com/floatingmenu"
    dex = root / "dex-smali/com/floatingmenu"

    mono.parent.mkdir(parents=True)
    shutil.copytree(base, mono)
    for f in ("MenuLoader.smali", "MenuLoader$18.smali", "MenuLoader$3.smali", "VercelPoller.smali"):
        shutil.copy2(e171 / f, mono / f)
    for f in (
        "b.smali", "FloatingMenu.smali", "FloatingMenu$1.smali",
        "FloatingMenu$1$3.smali", "FloatingMenu$1$3$1.smali", "FloatingMenu$1$3$1$1.smali",
        "FloatingMenu$1$4.smali", "FloatingMenu$1$4$1.smali",
    ):
        shutil.copy2(dex / f, mono / f)

    ml = (mono / "MenuLoader.smali").read_text()
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
    (mono / "MenuLoader.smali").write_text(ml)

    shutil.copytree(work / "v25-base", work / "out-asm", dirs_exist_ok=True)
    subprocess.check_call(["java", "-jar", str(smali), "assemble", str(work / "out"), "-o", str(out_dex)])
    print(out_dex)

if __name__ == "__main__":
    main()
