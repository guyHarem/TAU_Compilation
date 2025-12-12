#!/bin/bash

# Run AST Verifier on all AST test files
# This script implements T278: Run ast_verifier on all valid test programs

BASEDIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TESTS_DIR="${BASEDIR}/tests"
AST_DIR="${TESTS_DIR}/ast"

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}=====================================${NC}"
echo -e "${BLUE}  AST Verifier Test Suite${NC}"
echo -e "${BLUE}=====================================${NC}"
echo ""

# Compile AST verifier if needed
if [ ! -f "${TESTS_DIR}/AstVerifier.class" ]; then
    echo "Compiling AST verifier..."
    javac "${TESTS_DIR}/AstVerifier.java"
    if [ $? -ne 0 ]; then
        echo -e "${RED}Failed to compile AST verifier${NC}"
        exit 1
    fi
fi

TOTAL=0
PASSED=0
FAILED=0

# Test each AST test file
for test_file in "${AST_DIR}"/*.txt; do
    test_name=$(basename "$test_file" .txt)
    TOTAL=$((TOTAL + 1))

    echo -e "${BLUE}Testing: ${test_name}${NC}"

    # Determine check type based on file name
    if [[ "$test_name" == *"precedence"* ]]; then
        check_type="--check-precedence"
    elif [[ "$test_name" == *"associativity"* ]]; then
        check_type="--check-associativity"
    elif [[ "$test_name" == *"line_numbers"* ]]; then
        check_type="--check-line-numbers"
    elif [[ "$test_name" == *"nodes"* ]]; then
        check_type="--check-node-types"
    else
        check_type="--all"
    fi

    # Run verifier
    java -cp "${TESTS_DIR}" AstVerifier "$test_file" "$check_type" > /tmp/ast_verifier_output.txt 2>&1

    if [ $? -eq 0 ]; then
        echo -e "  ${GREEN}[PASS]${NC}"
        PASSED=$((PASSED + 1))
    else
        echo -e "  ${RED}[FAIL]${NC}"
        cat /tmp/ast_verifier_output.txt
        FAILED=$((FAILED + 1))
    fi
    echo ""
done

# Summary
echo -e "${BLUE}=====================================${NC}"
echo -e "${BLUE}  Summary${NC}"
echo -e "${BLUE}=====================================${NC}"
echo "Total:  $TOTAL"
echo -e "${GREEN}Passed: $PASSED${NC}"

if [ $FAILED -gt 0 ]; then
    echo -e "${RED}Failed: $FAILED${NC}"
    exit 1
else
    echo -e "${GREEN}All AST verification tests passed!${NC}"
    exit 0
fi
