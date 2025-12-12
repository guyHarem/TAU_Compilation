#!/bin/bash

# create_submission.sh
# Creates a submission zip file for Exercise 3 according to ex3.md requirements
#
# Submission structure:
# <ID>.zip
# ├── ids.txt          (one ID per line)
# └── ex3/             (source code)
#     ├── Makefile
#     └── SEMANT       (runnable jar, created by make)

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Print colored message
print_msg() {
    local color=$1
    shift
    echo -e "${color}$@${NC}"
}

print_error() { print_msg "$RED" "ERROR: $@"; }
print_success() { print_msg "$GREEN" "✓ $@"; }
print_warning() { print_msg "$YELLOW" "⚠ $@"; }
print_info() { print_msg "$BLUE" "ℹ $@"; }

# Print usage
usage() {
    cat <<EOF
Usage: $0 [OPTIONS]

Creates a submission zip file for Exercise 3 according to ex3.md requirements.

Options:
    -i, --id ID              Student ID for zip filename (required)
    -s, --source DIR         Source directory containing ex3 code (default: ../ex3)
    -o, --output DIR         Output directory for zip file (default: current directory)
    -t, --team ID1,ID2,...   Team member IDs (comma-separated, default: same as --id)
    -c, --clean              Clean build before creating zip
    -n, --no-build           Skip building SEMANT (assume it exists)
    -h, --help               Show this help message

Examples:
    # Create submission for single student
    $0 --id 123456789

    # Create submission for team
    $0 --id 123456789 --team 123456789,987654321,555555555

    # Use custom source directory
    $0 --id 123456789 --source /path/to/my/ex3

    # Clean build before submission
    $0 --id 123456789 --clean

Required structure in source directory:
    ex3/
    ├── Makefile
    └── src/ (and other source files)

After running 'make', should produce:
    ex3/SEMANT (runnable jar file)

EOF
    exit 0
}

# Parse command line arguments
STUDENT_ID=""
SOURCE_DIR="../ex3"
OUTPUT_DIR="."
TEAM_IDS=""
CLEAN_BUILD=false
NO_BUILD=false

while [[ $# -gt 0 ]]; do
    case $1 in
        -i|--id)
            STUDENT_ID="$2"
            shift 2
            ;;
        -s|--source)
            SOURCE_DIR="$2"
            shift 2
            ;;
        -o|--output)
            OUTPUT_DIR="$2"
            shift 2
            ;;
        -t|--team)
            TEAM_IDS="$2"
            shift 2
            ;;
        -c|--clean)
            CLEAN_BUILD=true
            shift
            ;;
        -n|--no-build)
            NO_BUILD=true
            shift
            ;;
        -h|--help)
            usage
            ;;
        *)
            print_error "Unknown option: $1"
            echo "Use --help for usage information"
            exit 1
            ;;
    esac
done

# Validate required arguments
if [[ -z "$STUDENT_ID" ]]; then
    print_error "Student ID is required. Use --id <ID>"
    echo "Use --help for usage information"
    exit 1
fi

# Validate student ID format (9 digits)
if ! [[ "$STUDENT_ID" =~ ^[0-9]{9}$ ]]; then
    print_warning "Student ID should be 9 digits, got: $STUDENT_ID"
    read -p "Continue anyway? (y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# Set team IDs to student ID if not provided
if [[ -z "$TEAM_IDS" ]]; then
    TEAM_IDS="$STUDENT_ID"
fi

# Resolve absolute paths
SOURCE_DIR=$(cd "$SOURCE_DIR" 2>/dev/null && pwd || echo "$SOURCE_DIR")
OUTPUT_DIR=$(cd "$OUTPUT_DIR" 2>/dev/null && pwd || echo "$OUTPUT_DIR")

print_info "=========================================="
print_info "Exercise 3 Submission Creator"
print_info "=========================================="
echo ""
print_info "Student ID:     $STUDENT_ID"
print_info "Source Dir:     $SOURCE_DIR"
print_info "Output Dir:     $OUTPUT_DIR"
print_info "Team Members:   $TEAM_IDS"
print_info "Clean Build:    $CLEAN_BUILD"
print_info "Skip Build:     $NO_BUILD"
echo ""

# Check source directory exists
if [[ ! -d "$SOURCE_DIR" ]]; then
    print_error "Source directory does not exist: $SOURCE_DIR"
    exit 1
fi

# Check Makefile exists
if [[ ! -f "$SOURCE_DIR/Makefile" ]]; then
    print_error "Makefile not found in source directory: $SOURCE_DIR/Makefile"
    print_info "The source directory must contain a Makefile at the top level"
    exit 1
fi

# Create temporary working directory
TEMP_DIR=$(mktemp -d)
trap "rm -rf $TEMP_DIR" EXIT

print_info "Creating temporary directory: $TEMP_DIR"

# Create ids.txt
IDS_FILE="$TEMP_DIR/ids.txt"
print_info "Creating ids.txt..."
IFS=',' read -ra IDS_ARRAY <<< "$TEAM_IDS"
for id in "${IDS_ARRAY[@]}"; do
    # Trim whitespace
    id=$(echo "$id" | xargs)
    echo "$id" >> "$IDS_FILE"
done
print_success "Created ids.txt with ${#IDS_ARRAY[@]} team member(s)"

# Copy source directory to temp
print_info "Copying source files..."
mkdir -p "$TEMP_DIR/ex3"

# Copy only necessary directories and files
# Required: Makefile, src/, and dependency directories (jflex/, cup/, external_jars/, manifest/)
# Note: bin/ is NOT needed as compiled classes are in SEMANT jar
for item in Makefile src jflex cup external_jars manifest; do
    if [[ -e "$SOURCE_DIR/$item" ]]; then
        cp -r "$SOURCE_DIR/$item" "$TEMP_DIR/ex3/"
    fi
done

print_success "Source files copied"

# Navigate to ex3 directory
cd "$TEMP_DIR/ex3"

# Clean build if requested
if [[ "$CLEAN_BUILD" == true ]]; then
    print_info "Cleaning previous build..."
    if make clean >/dev/null 2>&1; then
        print_success "Clean completed"
    else
        print_warning "Clean command failed or not supported"
    fi
fi

# Build SEMANT if not skipped (to verify the build works)
if [[ "$NO_BUILD" == false ]]; then
    print_info "Building SEMANT to verify Makefile works..."
    echo ""

    if make; then
        echo ""
        print_success "Build completed successfully"
    else
        echo ""
        print_error "Build failed!"
        print_info "Check the error messages above"
        print_info "You can use --no-build to skip building if you've already verified it works"
        exit 1
    fi

    # Verify SEMANT was created
    if [[ ! -f "$TEMP_DIR/ex3/SEMANT" ]]; then
        print_error "SEMANT file not found after build"
        print_info "Expected location: $TEMP_DIR/ex3/SEMANT"
        print_info "Make sure your Makefile creates a file named SEMANT (no extension)"
        exit 1
    fi

    print_success "Build verification successful"
else
    print_warning "Skipping build verification (--no-build flag set)"
fi

# Clean up build artifacts before zipping
print_info "Removing build artifacts (SEMANT will be built from source during grading)..."
cd "$TEMP_DIR/ex3"

# Remove SEMANT jar file - it will be rebuilt from source
rm -f SEMANT

# Remove bin/ directory with compiled classes
rm -rf bin/

# Remove generated parser/lexer files from src/ (they'll be regenerated by make)
rm -f src/Lexer.java src/Parser.java src/TokenNames.java

print_success "Build artifacts removed - submission contains only source code"

# Create zip file
cd "$TEMP_DIR"
ZIP_FILE="$OUTPUT_DIR/${STUDENT_ID}.zip"

print_info "Creating submission zip file..."

# Remove existing zip if present
if [[ -f "$ZIP_FILE" ]]; then
    print_warning "Removing existing zip file: $ZIP_FILE"
    rm -f "$ZIP_FILE"
fi

# Create zip
if zip -r "$ZIP_FILE" ids.txt ex3/ -q; then
    print_success "Zip file created: $ZIP_FILE"
else
    print_error "Failed to create zip file"
    exit 1
fi

# Get zip file size
ZIP_SIZE=$(du -h "$ZIP_FILE" | cut -f1)

echo ""
print_info "=========================================="
print_success "Submission created successfully!"
print_info "=========================================="
echo ""
print_info "Zip file:       $ZIP_FILE"
print_info "Size:           $ZIP_SIZE"
echo ""

# Verify zip contents
print_info "Zip file contents:"
unzip -l "$ZIP_FILE" | head -n 20
if [[ $(unzip -l "$ZIP_FILE" | wc -l) -gt 25 ]]; then
    echo "  ... (showing first entries only)"
fi
echo ""

# Check for common issues
print_info "Checking for common issues..."
ISSUES_FOUND=false

# Check ids.txt in root
if ! unzip -l "$ZIP_FILE" | grep -q "^.*ids.txt$"; then
    print_warning "ids.txt not found at root level of zip"
    ISSUES_FOUND=true
fi

# Check ex3/ directory
if ! unzip -l "$ZIP_FILE" | grep -q "ex3/"; then
    print_warning "ex3/ directory not found in zip"
    ISSUES_FOUND=true
fi

# Check ex3/Makefile
if ! unzip -l "$ZIP_FILE" | grep -q "ex3/Makefile$"; then
    print_warning "ex3/Makefile not found in zip"
    ISSUES_FOUND=true
fi

# Check ex3/src/ directory
if ! unzip -l "$ZIP_FILE" | grep -q "ex3/src/"; then
    print_warning "ex3/src/ directory not found in zip"
    ISSUES_FOUND=true
fi

# Verify SEMANT is NOT in the zip (it should be built from source)
if unzip -l "$ZIP_FILE" | grep -q "ex3/SEMANT$"; then
    print_warning "ex3/SEMANT found in zip - it should NOT be included (will be built from source)"
    ISSUES_FOUND=true
fi

if [[ "$ISSUES_FOUND" == false ]]; then
    print_success "All structure checks passed"
fi

echo ""
print_info "Next steps:"
print_info "1. Test your submission with the self-check script:"
print_info "   unzip self-check-ex3.zip"
print_info "   python self-check.py"
print_info ""
print_info "2. Upload ${STUDENT_ID}.zip to Moodle"
echo ""
print_success "Done!"
