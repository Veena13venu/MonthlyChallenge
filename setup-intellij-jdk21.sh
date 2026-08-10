#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# setup-intellij-jdk21.sh
#
# Fixes IntelliJ's broken ms-21.0.12 SDK entry and registers the working
# Homebrew Java 21 SDK instead.
#
# USAGE (IntelliJ must be CLOSED):
#   chmod +x setup-intellij-jdk21.sh && ./setup-intellij-jdk21.sh
# ─────────────────────────────────────────────────────────────────────────────
set -euo pipefail

JDK21_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"
JDK_TABLE="$HOME/Library/Application Support/JetBrains/IdeaIC2025.2/options/jdk.table.xml"
BROKEN_HOME="$HOME/Library/Java/JavaVirtualMachines/ms-21.0.12/Contents/Home"

echo "==> Checking Java 21 at Homebrew path..."
if [ ! -f "$JDK21_HOME/bin/java" ]; then
  echo "ERROR: Java 21 not found at $JDK21_HOME"
  exit 1
fi
echo "    ✓ Found: $($JDK21_HOME/bin/java -version 2>&1 | head -1)"

echo ""
echo "==> Backing up jdk.table.xml..."
cp "$JDK_TABLE" "${JDK_TABLE}.bak.$(date +%Y%m%d%H%M%S)"
echo "    ✓ Backup created"

echo ""
echo "==> Patching jdk.table.xml with python3..."
python3 - "$JDK_TABLE" "$JDK21_HOME" "$BROKEN_HOME" <<'PYEOF'
import sys, re

table_path   = sys.argv[1]
good_home    = sys.argv[2]   # Homebrew Java 21 — fully installed
broken_home  = sys.argv[3]   # ms-21.0.12 — empty / broken

with open(table_path, 'r') as f:
    content = f.read()

# 1. Replace every occurrence of the broken ms-21 path with the good Homebrew path
patched = content.replace(broken_home, good_home)

# 2. Also fix the SDK name label if it still says ms-21 or ms-21.0.12
patched = re.sub(r'<name value="ms-21[^"]*"\s*/>', '<name value="21" />', patched)

# 3. Fix the version string for that entry
patched = re.sub(
    r'(<name value="21"\s*/>.*?<version value=")[^"]*(")',
    r'\1Homebrew OpenJDK 21.0.11 - aarch64\2',
    patched,
    flags=re.DOTALL
)

# 4. If SDK "21" is not present at all (fresh table), inject it
if good_home not in patched:
    modules_raw = sys.stdin  # not used here; we rely on the path replace above
    print("  Note: good_home not found after replacement — table may need manual review")

with open(table_path, 'w') as f:
    f.write(patched)

print("  ✓ jdk.table.xml patched successfully")
PYEOF

echo ""
echo "==> Verifying patch..."
if grep -q "$JDK21_HOME" "$JDK_TABLE"; then
  echo "    ✓ Homebrew Java 21 path is now in jdk.table.xml"
else
  echo "    ✗ Path not found — something went wrong. Restoring backup."
  cp "${JDK_TABLE}.bak."* "$JDK_TABLE" 2>/dev/null || true
  exit 1
fi

if grep -q "$BROKEN_HOME" "$JDK_TABLE"; then
  echo "    ✗ Broken ms-21 path still present — manual fix needed"
  exit 1
else
  echo "    ✓ Broken ms-21 path removed"
fi

echo ""
echo "==> Fixing empty ms-21.0.12 JDK directory..."
# Symlink the empty Home dir to the Homebrew one so any cached path also works
BROKEN_DIR="$HOME/Library/Java/JavaVirtualMachines/ms-21.0.12/Contents/Home"
if [ -d "$BROKEN_DIR" ] && [ -z "$(ls -A "$BROKEN_DIR")" ]; then
  rm -rf "$BROKEN_DIR"
  ln -s "$JDK21_HOME" "$BROKEN_DIR"
  echo "    ✓ Symlinked empty ms-21.0.12/Contents/Home → Homebrew Java 21"
else
  echo "    ✓ ms-21.0.12/Contents/Home already has content (skipped)"
fi

echo ""
echo "══════════════════════════════════════════════════════"
echo "  All done! Now:"
echo "  1. Open IntelliJ IDEA"
echo "  2. Press ⌘; → Project → SDK → select '21'"
echo "  3. Right-click pom.xml → Maven → Reload Project"
echo "══════════════════════════════════════════════════════"
