#!/usr/bin/env bash
# Shim that wraps real SPIM and rewrites its startup banner to match the
# SPIM 8.0 banner the testers' fixtures expect (since they were captured
# on nova). Byte-exact for everything after the banner — no extra
# trailing newline added.
#
# Usage:
#   python3 run_all_suites.py --spim /Users/eladlandau/Desktop/TAU_Compilation/scripts/spim_shim.sh

set -e

# Find the real spim binary (not this shim).
REAL_SPIM=""
for cand in /opt/homebrew/bin/spim /usr/local/bin/spim /usr/bin/spim; do
    if [ -x "$cand" ] && [ "$cand" != "$0" ]; then
        REAL_SPIM="$cand"
        break
    fi
done
if [ -z "$REAL_SPIM" ]; then
    REAL_SPIM=$(command -v spim 2>/dev/null || true)
fi
if [ -z "$REAL_SPIM" ] || [ "$REAL_SPIM" = "$0" ]; then
    echo "spim_shim: no real spim found on PATH" >&2
    exit 127
fi

# Banner that the testers' fixtures expect.
printf 'SPIM Version 8.0 of January 8, 2010\nCopyright 1990-2010, James R. Larus.\nAll Rights Reserved.\nSee the file README for a full copyright notice.\nLoaded: /usr/lib/spim/exceptions.s\n'

# Run real spim, then drop ITS first "Loaded: ..." line (1 line) byte-by-byte
# without otherwise touching the stream, so trailing-newline-or-not is preserved.
"$REAL_SPIM" "$@" 2> >(cat >&2) | python3 -c '
import sys
data = sys.stdin.buffer.read()
nl = data.find(b"\n")
if nl >= 0 and data[:nl].startswith(b"Loaded:"):
    data = data[nl+1:]
sys.stdout.buffer.write(data)
'
