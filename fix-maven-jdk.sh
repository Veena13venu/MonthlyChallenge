#!/usr/bin/env bash
# fix-maven-jdk.sh
# Forces IntelliJ's Maven runner to use Java 21 for this project.
# Run with IntelliJ CLOSED, then reopen.
set -euo pipefail

JDK21="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"
WORKSPACE="/Users/veena.v/Documents/MonthlyChallenge/.idea/workspace.xml"

echo "Java 21 binary: $($JDK21/bin/java -version 2>&1 | head -1)"

# IntelliJ uses a special prefix "#" for absolute JRE paths in MavenRunner
# The format is:  <option name="jreName" value="#/absolute/path/to/jdk" />
python3 - "$WORKSPACE" "$JDK21" <<'PYEOF'
import sys, re, xml.etree.ElementTree as ET

ws_path = sys.argv[1]
jdk21   = sys.argv[2]

with open(ws_path, 'r') as f:
    content = f.read()

# Replace any existing jreName value with the absolute path form
content = re.sub(
    r'<option name="jreName" value="[^"]*"\s*/>',
    f'<option name="jreName" value="#{jdk21}" />',
    content
)

# If MavenRunner component doesn't exist at all, add it before </project>
if 'MavenRunner' not in content:
    runner = f'''  <component name="MavenRunner">
    <option name="jreName" value="#{jdk21}" />
    <option name="vmOptions" value="--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED" />
  </component>
'''
    content = content.replace('</project>', runner + '</project>')

with open(ws_path, 'w') as f:
    f.write(content)

print(f"  workspace.xml updated — Maven runner JRE: #{jdk21}")
PYEOF

echo ""
echo "Verifying..."
grep "jreName" "$WORKSPACE"

echo ""
echo "Done. Reopen IntelliJ — Maven will now compile with Java 21."
