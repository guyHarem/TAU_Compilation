#!/bin/bash

# Combined test runner for both official and unofficial test suites
# Takes a submission zip file as input, extracts it, builds it, and runs all tests
#
# Usage: ./run_all_tests.sh <submission.zip>
#   where submission.zip has the structure:
#     - ids.txt (at top level)
#     - ex3/ folder containing source code
#     - ex3/Makefile to build the project
#     - ex3/SEMANT (created after make)

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Check if submission zip file is provided
if [ $# -ne 1 ]; then
    echo -e "${RED}ERROR: Missing submission zip file${NC}"
    echo "Usage: $0 <submission.zip>"
    echo ""
    echo "Expected zip structure:"
    echo "  submission.zip"
    echo "    ├── ids.txt"
    echo "    └── ex3/"
    echo "        ├── Makefile"
    echo "        └── ... (source files)"
    exit 1
fi

ZIP_FILE="$1"

# Check if zip file exists
if [ ! -f "$ZIP_FILE" ]; then
    echo -e "${RED}ERROR: Zip file not found: $ZIP_FILE${NC}"
    exit 1
fi

# Get absolute path to zip file
ZIP_FILE="$(cd "$(dirname "$ZIP_FILE")" && pwd)/$(basename "$ZIP_FILE")"

echo "=========================================="
echo "Exercise 3 - Combined Test Runner"
echo "=========================================="
echo -e "${CYAN}Submission: $ZIP_FILE${NC}"
echo "=========================================="
echo ""

# Create temporary directory for extraction
TEMP_DIR=$(mktemp -d)
trap "rm -rf $TEMP_DIR" EXIT

echo -e "${BLUE}[1/5]${NC} Extracting submission..."
unzip -q "$ZIP_FILE" -d "$TEMP_DIR"

# Check for ids.txt
if [ ! -f "$TEMP_DIR/ids.txt" ]; then
    echo -e "${RED}ERROR: ids.txt not found in submission${NC}"
    exit 1
fi

echo -e "${GREEN}✓${NC} Found ids.txt"
echo "Student IDs:"
while IFS= read -r line; do
    echo "  - $line"
done < "$TEMP_DIR/ids.txt"
echo ""

# Check for ex3 directory
if [ ! -d "$TEMP_DIR/ex3" ]; then
    echo -e "${RED}ERROR: ex3/ directory not found in submission${NC}"
    exit 1
fi

echo -e "${GREEN}✓${NC} Found ex3/ directory"
echo ""

# Check for Makefile
if [ ! -f "$TEMP_DIR/ex3/Makefile" ]; then
    echo -e "${RED}ERROR: Makefile not found in ex3/ directory${NC}"
    exit 1
fi

echo -e "${GREEN}✓${NC} Found Makefile"
echo ""

# Build the project
echo -e "${BLUE}[2/5]${NC} Building project with make..."
cd "$TEMP_DIR/ex3"
if make > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} Build successful"
else
    echo -e "${RED}ERROR: Build failed${NC}"
    echo "Running make with verbose output:"
    make
    exit 1
fi
echo ""

# Check if SEMANT was created
if [ ! -f "$TEMP_DIR/ex3/SEMANT" ]; then
    echo -e "${RED}ERROR: SEMANT not found after build${NC}"
    echo "Expected location: ex3/SEMANT"
    exit 1
fi

echo -e "${GREEN}✓${NC} SEMANT created successfully"
echo ""

# Export SEMANT path for test scripts
SEMANT_PATH="$TEMP_DIR/ex3/SEMANT"
export SEMANT_PATH

# Run official tests
echo "=========================================="
echo -e "${BLUE}[3/5]${NC} Running Official Tests (20 tests)"
echo "=========================================="
echo ""

INPUT_DIR="$SCRIPT_DIR/official/input"
EXPECTED_DIR="$SCRIPT_DIR/official/expected_output"
OUTPUT_DIR="$TEMP_DIR/official_test_output"

mkdir -p "$OUTPUT_DIR"

official_total=0
official_passed=0
official_failed=0
declare -a official_failed_tests

for test_file in "$INPUT_DIR"/TEST_*.txt; do
    if [ ! -f "$test_file" ]; then
        continue
    fi

    official_total=$((official_total + 1))
    test_name=$(basename "$test_file" .txt)
    output_file="$OUTPUT_DIR/${test_name}.txt"
    expected_file="$EXPECTED_DIR/${test_name}_Expected_Output.txt"

    java -jar "$SEMANT_PATH" "$test_file" "$output_file" 2>/dev/null

    if [ ! -f "$output_file" ]; then
        echo -e "${RED}✗  $test_name - No output file created${NC}"
        official_failed=$((official_failed + 1))
        official_failed_tests+=("$test_name - No output file created")
        continue
    fi

    if [ ! -f "$expected_file" ]; then
        echo -e "${YELLOW}⚠  $test_name - No expected output${NC}"
        official_failed=$((official_failed + 1))
        official_failed_tests+=("$test_name - No expected output")
        continue
    fi

    if diff -q "$output_file" "$expected_file" >/dev/null 2>&1; then
        echo -e "${GREEN}✓  $test_name${NC}"
        official_passed=$((official_passed + 1))
    else
        echo -e "${RED}✗  $test_name${NC}"
        echo -e "   ${BLUE}Expected: $(cat "$expected_file")${NC}"
        echo -e "   ${BLUE}Got:      $(cat "$output_file")${NC}"
        official_failed=$((official_failed + 1))
        official_failed_tests+=("$test_name")
    fi
done

echo ""
echo "Official Tests: $official_passed/$official_total passed"
echo ""

# Run unofficial tests
echo "=========================================="
echo -e "${BLUE}[4/5]${NC} Running Unofficial Tests (159 tests)"
echo "=========================================="
echo ""

INPUT_DIR="$SCRIPT_DIR/unofficial/input"
EXPECTED_DIR="$SCRIPT_DIR/unofficial/expected_output"
OUTPUT_DIR="$TEMP_DIR/unofficial_test_output"

mkdir -p "$OUTPUT_DIR"

unofficial_total=0
unofficial_passed=0
unofficial_failed=0
declare -a unofficial_failed_tests

for test_file in "$INPUT_DIR"/**/*.txt; do
    if [ ! -f "$test_file" ]; then
        continue
    fi

    unofficial_total=$((unofficial_total + 1))
    test_name=$(basename "$test_file" .txt)
    category=$(basename "$(dirname "$test_file")")
    output_file="$OUTPUT_DIR/${test_name}.txt"
    expected_file="$EXPECTED_DIR/${test_name}_Expected_Output.txt"

    java -jar "$SEMANT_PATH" "$test_file" "$output_file" >/dev/null 2>&1

    if [ ! -f "$expected_file" ]; then
        echo -e "${YELLOW}⚠  [$category] $test_name - No expected output${NC}"
        unofficial_failed=$((unofficial_failed + 1))
        unofficial_failed_tests+=("$category/$test_name - No expected output")
        continue
    fi

    if diff -q "$output_file" "$expected_file" >/dev/null 2>&1; then
        echo -e "${GREEN}✓  [$category] $test_name${NC}"
        unofficial_passed=$((unofficial_passed + 1))
    else
        echo -e "${RED}✗  [$category] $test_name${NC}"
        unofficial_failed=$((unofficial_failed + 1))
        unofficial_failed_tests+=("$category/$test_name")
    fi
done

echo ""
echo "Unofficial Tests: $unofficial_passed/$unofficial_total passed"
echo ""

# Final summary
echo "=========================================="
echo -e "${BLUE}[5/5]${NC} Final Summary"
echo "=========================================="
echo ""
echo "Official Tests:"
echo -e "  Total:  $official_total"
echo -e "  Passed: ${GREEN}$official_passed${NC}"
echo -e "  Failed: ${RED}$official_failed${NC}"
if [ $official_total -gt 0 ]; then
    official_percentage=$((official_passed * 100 / official_total))
    echo "  Pass rate: $official_percentage%"
fi
echo ""
echo "Unofficial Tests:"
echo -e "  Total:  $unofficial_total"
echo -e "  Passed: ${GREEN}$unofficial_passed${NC}"
echo -e "  Failed: ${RED}$unofficial_failed${NC}"
if [ $unofficial_total -gt 0 ]; then
    unofficial_percentage=$((unofficial_passed * 100 / unofficial_total))
    echo "  Pass rate: $unofficial_percentage%"
fi
echo ""

total_tests=$((official_total + unofficial_total))
total_passed=$((official_passed + unofficial_passed))
total_failed=$((official_failed + unofficial_failed))

echo "Combined Results:"
echo -e "  Total:  $total_tests"
echo -e "  Passed: ${GREEN}$total_passed${NC}"
echo -e "  Failed: ${RED}$total_failed${NC}"
if [ $total_tests -gt 0 ]; then
    total_percentage=$((total_passed * 100 / total_tests))
    echo "  Pass rate: $total_percentage%"
fi
echo "=========================================="

# Show failed tests if any
if [ $total_failed -gt 0 ]; then
    echo ""
    if [ ${#official_failed_tests[@]} -gt 0 ]; then
        echo "Failed Official Tests:"
        for test in "${official_failed_tests[@]}"; do
            echo -e "  ${RED}✗${NC} $test"
        done
        echo ""
    fi

    if [ ${#unofficial_failed_tests[@]} -gt 0 ]; then
        echo "Failed Unofficial Tests:"
        for test in "${unofficial_failed_tests[@]}"; do
            echo -e "  ${RED}✗${NC} $test"
        done
        echo ""
    fi
fi

echo ""
if [ $total_failed -eq 0 ]; then
    echo -e "${GREEN}All tests passed! 🎉${NC}"
    exit 0
else
    echo -e "${RED}$total_failed test(s) failed.${NC}"
    exit 1
fi
