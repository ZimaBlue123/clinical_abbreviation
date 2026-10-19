"""
Automated Android Build and Verification Tool
"""

import os
import subprocess
import sys


def run_command(cmd: str, cwd: str, env: dict) -> int:
    print(f"[Build] Executing: {cmd}")
    process = subprocess.Popen(
        cmd,
        cwd=cwd,
        env=env,
        shell=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
        encoding="utf-8",
        errors="replace"
    )
    for line in process.stdout:
        print(line, end="")
    process.wait()
    return process.returncode


def main() -> int:
    base_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
    env = os.environ.copy()

    # Locate JDK if not configured
    if "JAVA_HOME" not in env or not os.path.exists(env["JAVA_HOME"]):
        candidate_jdks = [
            r"C:\Users\Administrator\jdk17\jdk-17",
            r"C:\Program Files\Java\jdk-17",
        ]
        for jdk in candidate_jdks:
            if os.path.exists(jdk):
                env["JAVA_HOME"] = jdk
                break

    print(f"[Build] JAVA_HOME: {env.get('JAVA_HOME', 'Not Set')}")

    # Build release APK
    gradle_cmd = ".\\gradlew.bat assembleRelease" if os.name == "nt" else "./gradlew assembleRelease"
    code = run_command(gradle_cmd, base_dir, env)
    if code != 0:
        print("[Build] ERROR: Gradle assembleRelease failed!")
        return code

    apk_path = os.path.join(base_dir, "app", "build", "outputs", "apk", "release", "app-release.apk")
    if not os.path.exists(apk_path):
        print(f"[Build] ERROR: APK output not found at {apk_path}")
        return 1

    size_mb = os.path.getsize(apk_path) / (1024 * 1024)
    print(f"[Build] SUCCESS: APK generated at {apk_path} ({size_mb:.2f} MB)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
