#!/bin/bash

# Test runner script for L parser
# Usage: ./run_tests.sh [category]
# Categories: lexer, parser, precedence, errors, edge, ast, all (default)

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Get the base directory
BASEDIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TESTS_DIR="${BASEDIR}/tests"
PARSER="${BASEDIR}/PARSER"
RESULTS_DIR="${TESTS_DIR}/results"

# Counters
TOTAL=0
PASSED=0
FAILED=0

# Clean results directory
mkdir -p "${RESULTS_DIR}"
rm -f "${RESULTS_DIR}"/*.out 2>/dev/null

# Function to run a single test
run_test() {
    local input_file="$1"
    local expected_file="$2"
    local test_name="$3"
    local category="$4"

    TOTAL=$((TOTAL + 1))

    # Create output file path
    local output_file="${RESULTS_DIR}/${test_name}.out"

    # Run the parser
    java -jar "${PARSER}" "${input_file}" "${output_file}" 2>/dev/null

    # Check if output file was created
    if [ ! -f "${output_file}" ]; then
        echo -e "${RED}[FAIL]${NC} ${category}/${test_name} - No output file generated"
        FAILED=$((FAILED + 1))
        return
    fi

    # Compare output with expected
    if diff -q "${output_file}" "${expected_file}" > /dev/null 2>&1; then
        echo -e "${GREEN}[PASS]${NC} ${category}/${test_name}"
        PASSED=$((PASSED + 1))
    else
        echo -e "${RED}[FAIL]${NC} ${category}/${test_name}"
        echo "  Expected: $(cat "${expected_file}")"
        echo "  Got:      $(cat "${output_file}")"
        FAILED=$((FAILED + 1))
    fi
}

# Function to run tests in a category
run_category() {
    local category="$1"
    local test_dir="$2"
    local expected_dir="$3"

    echo -e "${BLUE}=== Running ${category} tests ===${NC}"

    # Find all .txt files in the test directory
    for input_file in "${test_dir}"/*.txt; do
        if [ -f "${input_file}" ]; then
            local test_name=$(basename "${input_file}" .txt)
            local expected_file="${expected_dir}/${test_name}.out"

            # Check if expected output exists
            if [ ! -f "${expected_file}" ]; then
                echo -e "${YELLOW}[SKIP]${NC} ${category}/${test_name} - No expected output file"
                continue
            fi

            run_test "${input_file}" "${expected_file}" "${test_name}" "${category}"
        fi
    done

    echo ""
}

# Function to run all tests in a directory with subdirectories
run_category_with_subdirs() {
    local category="$1"
    local test_base="$2"
    local expected_base="$3"

    for subdir in "${test_base}"/*; do
        if [ -d "${subdir}" ]; then
            local subdir_name=$(basename "${subdir}")
            local expected_subdir="${expected_base}/${subdir_name}"
            run_category "${category}/${subdir_name}" "${subdir}" "${expected_subdir}"
        fi
    done
}

# Main execution
CATEGORY="${1:-all}"

echo -e "${BLUE}=====================================${NC}"
echo -e "${BLUE}  L Parser Test Suite${NC}"
echo -e "${BLUE}=====================================${NC}"
echo ""

case "${CATEGORY}" in
    lexer)
        run_category_with_subdirs "lexer" "${TESTS_DIR}/lexer" "${TESTS_DIR}/expected/lexer"
        ;;
    parser)
        run_category_with_subdirs "parser" "${TESTS_DIR}/parser" "${TESTS_DIR}/expected/parser"
        ;;
    precedence)
        run_category "precedence" "${TESTS_DIR}/precedence" "${TESTS_DIR}/expected/precedence"
        ;;
    errors)
        run_category_with_subdirs "errors" "${TESTS_DIR}/errors" "${TESTS_DIR}/expected/errors"
        ;;
    edge)
        run_category "edge_cases" "${TESTS_DIR}/edge_cases" "${TESTS_DIR}/expected/edge_cases"
        ;;
    ast)
        run_category "ast" "${TESTS_DIR}/ast" "${TESTS_DIR}/expected/ast"
        ;;
    all)
        run_category_with_subdirs "lexer" "${TESTS_DIR}/lexer" "${TESTS_DIR}/expected/lexer"
        run_category_with_subdirs "parser" "${TESTS_DIR}/parser" "${TESTS_DIR}/expected/parser"
        run_category "precedence" "${TESTS_DIR}/precedence" "${TESTS_DIR}/expected/precedence"
        run_category_with_subdirs "errors" "${TESTS_DIR}/errors" "${TESTS_DIR}/expected/errors"
        run_category "edge_cases" "${TESTS_DIR}/edge_cases" "${TESTS_DIR}/expected/edge_cases"
        run_category "ast" "${TESTS_DIR}/ast" "${TESTS_DIR}/expected/ast"
        ;;
    *)
        echo -e "${RED}Unknown category: ${CATEGORY}${NC}"
        echo "Usage: $0 [lexer|parser|precedence|errors|edge|ast|all]"
        exit 1
        ;;
esac

# Print summary
echo -e "${BLUE}=====================================${NC}"
echo -e "${BLUE}  Test Summary${NC}"
echo -e "${BLUE}=====================================${NC}"
echo -e "Total:  ${TOTAL}"
echo -e "${GREEN}Passed: ${PASSED}${NC}"
echo -e "${RED}Failed: ${FAILED}${NC}"

if [ ${FAILED} -eq 0 ]; then
    echo -e "${GREEN}All tests passed!${NC}"
    exit 0
else
    echo -e "${RED}Some tests failed.${NC}"
    exit 1
fi
