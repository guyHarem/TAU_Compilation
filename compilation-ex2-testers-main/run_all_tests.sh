#!/bin/bash

# Test runner for ex2 submissions
# Usage: ./run_all_tests.sh <submission.zip>
# Or: ./run_all_tests.sh (will look for *.zip in current directory)

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TESTS_DIR="${SCRIPT_DIR}/tests"
OFFICIAL_DIR="${SCRIPT_DIR}/official"
WORK_DIR="${SCRIPT_DIR}/workspace"

# Counters
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# Cleanup function
cleanup() {
    if [ -d "${WORK_DIR}" ]; then
        echo -e "${YELLOW}Cleaning up workspace...${NC}"
        rm -rf "${WORK_DIR}"
    fi
}

# Initial cleanup
if [ -d "${WORK_DIR}" ]; then
    rm -rf "${WORK_DIR}"
fi

# Set trap to cleanup on exit
trap cleanup EXIT

# Function to extract and build submission
setup_submission() {
    local zip_file="$1"

    echo -e "${BLUE}=====================================${NC}"
    echo -e "${BLUE}  ex2 Submission Test Runner${NC}"
    echo -e "${BLUE}=====================================${NC}"
    echo ""
    echo -e "${CYAN}Submission file: ${zip_file}${NC}"
    echo ""

    # Create workspace
    mkdir -p "${WORK_DIR}"

    # Extract submission
    echo -e "${YELLOW}Extracting submission...${NC}"
    unzip -q -o "${zip_file}" -d "${WORK_DIR}"

    # Find ex2 directory
    EX2_DIR=$(find "${WORK_DIR}" -type d -name "ex2" | head -1)

    if [ -z "${EX2_DIR}" ]; then
        echo -e "${RED}ERROR: Could not find ex2/ directory in submission${NC}"
        exit 1
    fi

    echo -e "${GREEN}Found ex2 directory: ${EX2_DIR}${NC}"

    # Check for Makefile
    if [ ! -f "${EX2_DIR}/Makefile" ]; then
        echo -e "${RED}ERROR: Makefile not found in ${EX2_DIR}${NC}"
        exit 1
    fi

    # Build the parser
    echo -e "${YELLOW}Building parser...${NC}"
    cd "${EX2_DIR}"
    make clean >/dev/null 2>&1 || true

    if ! make 2>&1 | tee "${WORK_DIR}/build.log"; then
        echo -e "${RED}ERROR: Build failed. Check ${WORK_DIR}/build.log${NC}"
        exit 1
    fi

    # Check for PARSER
    if [ ! -f "${EX2_DIR}/PARSER" ]; then
        echo -e "${RED}ERROR: PARSER file not found after build${NC}"
        exit 1
    fi

    echo -e "${GREEN}Build successful!${NC}"
    echo ""
}

# Function to run a single test
run_test() {
    local input_file="$1"
    local expected_file="$2"
    local test_name="$3"

    TOTAL_TESTS=$((TOTAL_TESTS + 1))

    local output_file="${WORK_DIR}/test_output_${TOTAL_TESTS}.txt"

    # Run the parser
    cd "${EX2_DIR}"
    if ! java -jar PARSER "${input_file}" "${output_file}" 2>/dev/null; then
        # Parser crashed
        echo -e "${RED}[CRASH]${NC} ${test_name}"
        FAILED_TESTS=$((FAILED_TESTS + 1))
        return 1
    fi

    # Check if output file was created
    if [ ! -f "${output_file}" ]; then
        echo -e "${RED}[FAIL]${NC} ${test_name} - No output file generated"
        FAILED_TESTS=$((FAILED_TESTS + 1))
        return 1
    fi

    # Compare output with expected
    if diff -q "${output_file}" "${expected_file}" > /dev/null 2>&1; then
        echo -e "${GREEN}[PASS]${NC} ${test_name}"
        PASSED_TESTS=$((PASSED_TESTS + 1))
        return 0
    else
        echo -e "${RED}[FAIL]${NC} ${test_name}"
        echo "  Expected: $(cat "${expected_file}" | tr '\n' ' ')"
        echo "  Got:      $(cat "${output_file}" | tr '\n' ' ')"
        FAILED_TESTS=$((FAILED_TESTS + 1))
        return 1
    fi
}

# Function to run tests in a category
run_category() {
    local category="$1"
    local test_dir="$2"
    local expected_dir="$3"

    echo -e "${BLUE}=== Running ${category} ===${NC}"

    # Find all .txt files in the test directory
    local test_count=0
    for input_file in "${test_dir}"/*.txt; do
        if [ -f "${input_file}" ]; then
            local test_name=$(basename "${input_file}" .txt)
            local expected_file="${expected_dir}/${test_name}.out"

            # Check if expected output exists
            if [ ! -f "${expected_file}" ]; then
                echo -e "${YELLOW}[SKIP]${NC} ${category}/${test_name} - No expected output"
                continue
            fi

            run_test "${input_file}" "${expected_file}" "${category}/${test_name}"
            test_count=$((test_count + 1))
        fi
    done

    if [ $test_count -eq 0 ]; then
        echo -e "${YELLOW}No tests found in ${category}${NC}"
    fi
    echo ""
}

# Function to run tests with subdirectories
run_category_with_subdirs() {
    local category="$1"
    local test_base="$2"
    local expected_base="$3"

    for subdir in "${test_base}"/*; do
        if [ -d "${subdir}" ]; then
            local subdir_name=$(basename "${subdir}")
            local expected_subdir="${expected_base}/${subdir_name}"

            if [ -d "${expected_subdir}" ]; then
                run_category "${category}/${subdir_name}" "${subdir}" "${expected_subdir}"
            fi
        fi
    done
}

# Function to run official tests
run_official_tests() {
    echo -e "${BLUE}=== Running Official Tests ===${NC}"

    local input_dir="${OFFICIAL_DIR}/input"
    local expected_dir="${OFFICIAL_DIR}/expected_output"

    for input_file in "${input_dir}"/TEST_*.txt; do
        if [ -f "${input_file}" ]; then
            local base_name=$(basename "${input_file}" .txt)
            local expected_file="${expected_dir}/${base_name}_Expected_Output.txt"

            if [ -f "${expected_file}" ]; then
                run_test "${input_file}" "${expected_file}" "official/${base_name}"
            fi
        fi
    done

    echo ""
}

# Main execution
main() {
    local zip_file=""

    # Find zip file
    if [ $# -eq 1 ]; then
        zip_file="$1"
    else
        # Look for zip files in current directory
        local zip_files=(*.zip)
        if [ ${#zip_files[@]} -eq 1 ] && [ -f "${zip_files[0]}" ]; then
            zip_file="${zip_files[0]}"
            echo -e "${CYAN}Found submission: ${zip_file}${NC}"
        else
            echo -e "${RED}Usage: $0 <submission.zip>${NC}"
            echo -e "${RED}Or place a single .zip file in the current directory${NC}"
            exit 1
        fi
    fi

    # Check if zip file exists
    if [ ! -f "${zip_file}" ]; then
        echo -e "${RED}ERROR: File not found: ${zip_file}${NC}"
        exit 1
    fi

    # Convert to absolute path
    zip_file="$(cd "$(dirname "${zip_file}")" && pwd)/$(basename "${zip_file}")"

    # Setup submission
    setup_submission "${zip_file}"

    # Run comprehensive test suite
    echo -e "${CYAN}Running Comprehensive Test Suite (106 tests)${NC}"
    echo -e "${CYAN}=============================================${NC}"
    echo ""

    # Lexer tests
    if [ -d "${TESTS_DIR}/lexer" ]; then
        run_category_with_subdirs "lexer" "${TESTS_DIR}/lexer" "${TESTS_DIR}/expected/lexer"
    fi

    # Parser tests
    if [ -d "${TESTS_DIR}/parser" ]; then
        run_category_with_subdirs "parser" "${TESTS_DIR}/parser" "${TESTS_DIR}/expected/parser"
    fi

    # Precedence tests
    if [ -d "${TESTS_DIR}/precedence" ]; then
        run_category "precedence" "${TESTS_DIR}/precedence" "${TESTS_DIR}/expected/precedence"
    fi

    # Error tests
    if [ -d "${TESTS_DIR}/errors" ]; then
        run_category_with_subdirs "errors" "${TESTS_DIR}/errors" "${TESTS_DIR}/expected/errors"
    fi

    # Edge case tests
    if [ -d "${TESTS_DIR}/edge_cases" ]; then
        run_category "edge_cases" "${TESTS_DIR}/edge_cases" "${TESTS_DIR}/expected/edge_cases"
    fi

    # AST tests
    if [ -d "${TESTS_DIR}/ast" ]; then
        run_category "ast" "${TESTS_DIR}/ast" "${TESTS_DIR}/expected/ast"
    fi

    # Run official tests
    echo -e "${CYAN}Running Official Tests (10 tests)${NC}"
    echo -e "${CYAN}=================================${NC}"
    echo ""
    run_official_tests

    # Print summary
    echo -e "${BLUE}=====================================${NC}"
    echo -e "${BLUE}  Test Summary${NC}"
    echo -e "${BLUE}=====================================${NC}"
    echo -e "Total:  ${TOTAL_TESTS}"
    echo -e "${GREEN}Passed: ${PASSED_TESTS}${NC}"
    echo -e "${RED}Failed: ${FAILED_TESTS}${NC}"

    local pass_rate=0
    if [ ${TOTAL_TESTS} -gt 0 ]; then
        pass_rate=$((PASSED_TESTS * 100 / TOTAL_TESTS))
    fi
    echo -e "Pass Rate: ${pass_rate}%"
    echo ""

    if [ ${FAILED_TESTS} -eq 0 ]; then
        echo -e "${GREEN}✓ All tests passed!${NC}"
        exit 0
    else
        echo -e "${RED}✗ Some tests failed.${NC}"
        exit 1
    fi
}

# Run main
main "$@"
