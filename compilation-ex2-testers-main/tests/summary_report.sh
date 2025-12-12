#!/bin/bash

# Test summary report generator

# Colors
BLUE='\033[0;34m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

BASEDIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TESTS_DIR="${BASEDIR}/tests"

echo -e "${BLUE}=====================================${NC}"
echo -e "${BLUE}  Test Suite Summary Report${NC}"
echo -e "${BLUE}=====================================${NC}"
echo ""

# Function to count test files
count_tests() {
    local dir="$1"
    local count=0

    if [ -d "${dir}" ]; then
        count=$(find "${dir}" -type f -name "*.txt" | wc -l)
    fi

    echo ${count}
}

# Count tests by category
LEXER_VALID=$(count_tests "${TESTS_DIR}/lexer/valid")
LEXER_ERRORS=$(count_tests "${TESTS_DIR}/lexer/errors")
LEXER_TOTAL=$((LEXER_VALID + LEXER_ERRORS))

PARSER_VALID=$(count_tests "${TESTS_DIR}/parser/valid")
PARSER_COMPLETE=$(count_tests "${TESTS_DIR}/parser/complete")
PARSER_TOTAL=$((PARSER_VALID + PARSER_COMPLETE))

PRECEDENCE_TOTAL=$(count_tests "${TESTS_DIR}/precedence")

ERRORS_SYNTAX=$(count_tests "${TESTS_DIR}/errors/syntax")
ERRORS_LEXICAL=$(count_tests "${TESTS_DIR}/errors/lexical")
ERRORS_TOTAL=$((ERRORS_SYNTAX + ERRORS_LEXICAL))

EDGE_TOTAL=$(count_tests "${TESTS_DIR}/edge_cases")

AST_TOTAL=$(count_tests "${TESTS_DIR}/ast")

GRAND_TOTAL=$((LEXER_TOTAL + PARSER_TOTAL + PRECEDENCE_TOTAL + ERRORS_TOTAL + EDGE_TOTAL + AST_TOTAL))

echo -e "${GREEN}Test Files by Category:${NC}"
echo "  Lexical Analysis:"
echo "    - Valid tokens:    ${LEXER_VALID}"
echo "    - Lexical errors:  ${LEXER_ERRORS}"
echo "    - Total:           ${LEXER_TOTAL}"
echo ""
echo "  Syntax Analysis:"
echo "    - Valid syntax:    ${PARSER_VALID}"
echo "    - Complete programs: ${PARSER_COMPLETE}"
echo "    - Total:           ${PARSER_TOTAL}"
echo ""
echo "  Operator Precedence: ${PRECEDENCE_TOTAL}"
echo ""
echo "  Error Detection:"
echo "    - Syntax errors:   ${ERRORS_SYNTAX}"
echo "    - Lexical errors:  ${ERRORS_LEXICAL}"
echo "    - Total:           ${ERRORS_TOTAL}"
echo ""
echo "  Edge Cases:          ${EDGE_TOTAL}"
echo ""
echo "  AST Verification:    ${AST_TOTAL}"
echo ""
echo -e "${YELLOW}=====================================${NC}"
echo -e "${YELLOW}GRAND TOTAL:         ${GRAND_TOTAL} test files${NC}"
echo -e "${YELLOW}=====================================${NC}"
echo ""

# Count expected output files
EXPECTED_TOTAL=$(find "${TESTS_DIR}/expected" -type f -name "*.out" 2>/dev/null | wc -l)
echo "Expected output files: ${EXPECTED_TOTAL}"

if [ ${EXPECTED_TOTAL} -lt ${GRAND_TOTAL} ]; then
    echo -e "${YELLOW}Warning: Missing expected output files!${NC}"
    echo "         ${GRAND_TOTAL} test files but only ${EXPECTED_TOTAL} expected outputs"
fi

echo ""
echo "Test Infrastructure:"
echo "  - Test runner:      tests/run_tests.sh"
echo "  - Error verifier:   tests/verify_errors.sh"
echo "  - AST helper:       tests/verify_ast.sh"
echo "  - Summary report:   tests/summary_report.sh"
echo ""
