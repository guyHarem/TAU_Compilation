#!/bin/bash

# AST dump script - shows textual representation of AST

if [ $# -ne 1 ]; then
    echo "Usage: $0 <test_file.txt>"
    exit 1
fi

BASEDIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TEST_FILE="$1"
OUTPUT_FILE="${BASEDIR}/output/ParseStatus.txt"
AST_DOT="${BASEDIR}/output/AST_IN_GRAPHVIZ_DOT_FORMAT.txt"

echo "Parsing ${TEST_FILE}..."
java -jar "${BASEDIR}/PARSER" "${TEST_FILE}" "${OUTPUT_FILE}"

if [ -f "${AST_DOT}" ]; then
    echo ""
    echo "=== AST in DOT format ==="
    cat "${AST_DOT}"
else
    echo "No AST generated (parsing failed or syntax error)"
fi
