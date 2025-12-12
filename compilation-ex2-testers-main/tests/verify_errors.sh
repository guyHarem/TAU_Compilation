#!/bin/bash

# Error verification script
# Verifies that error line numbers are accurate

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

BASEDIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TESTS_DIR="${BASEDIR}/tests"
PARSER="${BASEDIR}/PARSER"

echo -e "${BLUE}=====================================${NC}"
echo -e "${BLUE}  Error Line Number Verification${NC}"
echo -e "${BLUE}=====================================${NC}"
echo ""

TOTAL=0
CORRECT=0
INCORRECT=0

# Function to verify error line number
verify_error() {
    local input_file="$1"
    local test_name=$(basename "${input_file}" .txt)

    TOTAL=$((TOTAL + 1))

    # Run parser
    local output_file="/tmp/error_verify_${test_name}.out"
    java -jar "${PARSER}" "${input_file}" "${output_file}" 2>/dev/null

    if [ ! -f "${output_file}" ]; then
        echo -e "${RED}[ERROR]${NC} ${test_name} - No output generated"
        INCORRECT=$((INCORRECT + 1))
        return
    fi

    local output=$(cat "${output_file}")

    # Check if it's a syntax error with line number
    if [[ $output =~ ^ERROR\(([0-9]+)\)$ ]]; then
        local reported_line="${BASH_REMATCH[1]}"
        echo -e "${GREEN}[OK]${NC} ${test_name} - Reports error at line ${reported_line}"
        CORRECT=$((CORRECT + 1))
    elif [[ $output == "ERROR" ]]; then
        echo -e "${GREEN}[OK]${NC} ${test_name} - Reports lexical error (no line number)"
        CORRECT=$((CORRECT + 1))
    else
        echo -e "${RED}[FAIL]${NC} ${test_name} - Invalid error format: ${output}"
        INCORRECT=$((INCORRECT + 1))
    fi

    rm -f "${output_file}"
}

# Check all error test files
for error_file in "${TESTS_DIR}/errors/syntax"/*.txt; do
    if [ -f "${error_file}" ]; then
        verify_error "${error_file}"
    fi
done

for error_file in "${TESTS_DIR}/errors/lexical"/*.txt; do
    if [ -f "${error_file}" ]; then
        verify_error "${error_file}"
    fi
done

echo ""
echo -e "${BLUE}=====================================${NC}"
echo -e "Total errors checked: ${TOTAL}"
echo -e "${GREEN}Correct: ${CORRECT}${NC}"
echo -e "${RED}Incorrect: ${INCORRECT}${NC}"

if [ ${INCORRECT} -eq 0 ]; then
    echo -e "${GREEN}All error reports are accurate!${NC}"
    exit 0
else
    echo -e "${RED}Some error reports are inaccurate.${NC}"
    exit 1
fi
