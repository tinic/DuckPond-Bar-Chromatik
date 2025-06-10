#!/bin/bash

# DuckPond LX Package Build Script

echo "Building DuckPond LX Package..."

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed or not in PATH"
    exit 1
fi

# Clean and build
echo "Running Maven clean package..."
mvn clean package

if [ $? -eq 0 ]; then
    echo ""
    echo "✓ Build successful!"
    echo ""
    echo "Generated files:"
    echo "  - target/duckpond-lx-1.0.0.jar (LX Package)"
    echo ""
    echo "Package contents:"
    echo "  ✓ 9 DuckPond lighting patterns"
    echo "  ✓ LXF fixture definitions (umbrella.lxf, umbrellas.lxf)"
    echo "  ✓ Utility classes (LXFloat4, Gradient)"
    echo ""
    echo "To install:"
    echo "  1. Copy the JAR to your LX installation's packages directory"
    echo "  2. Restart LX Studio"
    echo "  3. The DuckPond patterns and fixtures will be available"
    echo ""
else
    echo "✗ Build failed"
    exit 1
fi