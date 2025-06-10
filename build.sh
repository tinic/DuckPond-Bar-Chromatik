#!/bin/bash

# DuckPond Bar Chromatik Build Script
# Maven-based build system

echo "Building DuckPond Bar Chromatik..."

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed or not in PATH"
    exit 1
fi

# Parse command line arguments
GOAL="package"
PROFILE=""

while [[ $# -gt 0 ]]; do
    case $1 in
        clean)
            GOAL="clean"
            shift
            ;;
        compile)
            GOAL="compile"
            shift
            ;;
        package)
            GOAL="package"
            shift
            ;;
        install)
            GOAL="install"
            shift
            ;;
        run)
            GOAL="exec:java"
            shift
            ;;
        distribution)
            GOAL="package"
            PROFILE="-Pdistribution"
            shift
            ;;
        --project-file=*)
            PROJECT_FILE="${1#*=}"
            PROFILE="-Prun-project -Dproject.file=$PROJECT_FILE"
            GOAL="exec:java"
            shift
            ;;
        *)
            echo "Unknown option: $1"
            echo "Usage: $0 [clean|compile|package|install|run|distribution] [--project-file=file.lxp]"
            exit 1
            ;;
    esac
done

# Run Maven
echo "Running: mvn $GOAL $PROFILE"
mvn $GOAL $PROFILE

if [ $? -eq 0 ]; then
    echo ""
    echo "✓ Build successful!"
    
    if [ "$GOAL" = "package" ]; then
        echo ""
        echo "Generated files:"
        echo "  - LXPackage/target/duckpond-lx-1.0.0.jar (LX Package)"
        if [ -f "target/duckpond-bar-chromatik-1.0.0-distribution.zip" ]; then
            echo "  - target/duckpond-bar-chromatik-1.0.0-distribution.zip (Distribution)"
        fi
        echo ""
        echo "Usage:"
        echo "  ./DuckPondBarHeadless                    # Run with default project"
        echo "  ./DuckPondBarHeadless myproject.lxp      # Run with custom project"
        echo "  mvn exec:java                            # Run via Maven"
    fi
    
    if [ "$GOAL" = "exec:java" ]; then
        echo ""
        echo "DuckPond application started successfully"
    fi
    
else
    echo "✗ Build failed"
    exit 1
fi