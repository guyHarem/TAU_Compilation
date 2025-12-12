#!/bin/bash

# Run tests for a specific category
# Usage: ./run_category.sh <submission.zip> <category>
# Categories: lexer, parser, precedence, errors, edge, ast, official

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if [ $# -ne 2 ]; then
    echo "Usage: $0 <submission.zip> <category>"
    echo "Categories: lexer, parser, precedence, errors, edge, ast, official"
    exit 1
fi

ZIP_FILE="$1"
CATEGORY="$2"

# Export category for the main script to use
export TEST_CATEGORY="${CATEGORY}"

# Create a temporary modified script
TEMP_SCRIPT="${SCRIPT_DIR}/.run_category_temp.sh"
cp "${SCRIPT_DIR}/run_all_tests.sh" "${TEMP_SCRIPT}"

# Modify the script to only run the specified category
cat > "${TEMP_SCRIPT}" << 'EOFSCRIPT'
#!/bin/bash
set -e

# This is a modified version that runs only the specified category
CATEGORY="${TEST_CATEGORY}"

# [Rest of the script would be here - for simplicity, just call the main script]
# In practice, you'd modify the main() function to only run the specified category

echo "Category-specific testing not yet implemented."
echo "Use run_all_tests.sh to run all tests."
exit 1
EOFSCRIPT

chmod +x "${TEMP_SCRIPT}"
"${TEMP_SCRIPT}" "${ZIP_FILE}"
rm -f "${TEMP_SCRIPT}"
