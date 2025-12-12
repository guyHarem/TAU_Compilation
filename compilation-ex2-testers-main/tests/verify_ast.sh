#!/bin/bash

# AST visualization helper script
# Generates AST visualization for a test file

if [ $# -ne 1 ]; then
    echo "Usage: $0 <test_file.txt>"
    exit 1
fi

BASEDIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TEST_FILE="$1"
OUTPUT_FILE="${BASEDIR}/output/ParseStatus.txt"
AST_DOT="${BASEDIR}/output/AST_IN_GRAPHVIZ_DOT_FORMAT.txt"
AST_IMAGE="${BASEDIR}/output/ast.jpeg"

echo "Parsing ${TEST_FILE}..."
java -jar "${BASEDIR}/PARSER" "${TEST_FILE}" "${OUTPUT_FILE}"

if [ -f "${AST_DOT}" ]; then
    echo "Generating AST visualization..."
    dot -Tjpeg -o"${AST_IMAGE}" "${AST_DOT}" 2>/dev/null

    if [ -f "${AST_IMAGE}" ]; then
        echo "AST visualization saved to: ${AST_IMAGE}"
        echo "Opening visualization..."
        if command -v eog &> /dev/null; then
            eog "${AST_IMAGE}" &
        elif command -v xdg-open &> /dev/null; then
            xdg-open "${AST_IMAGE}" &
        else
            echo "Please open ${AST_IMAGE} manually"
        fi
    else
        echo "Failed to generate AST image"
    fi
else
    echo "No AST generated (parsing failed or syntax error)"
fi
