# DuckPond LX Package

An LX Package containing lighting patterns for the DuckPond Bar umbrella installation.

## Overview

This package provides 9 different lighting patterns optimized for umbrella fixtures with spoke-based LED arrays. Each pattern is designed to work with fixtures tagged as "umbrella" in the LX model.

## Patterns

### Seasonal Effects
- **Spring** - Rainbow gradient with local coordinate modulation
- **Summer** - Rainbow gradient with global coordinate sparkles  
- **Autumn** - Rainy gradient with autumn colors
- **Winter** - Winter gradient with rainy overlay

### Atmospheric Effects
- **After Rain** - Rainbow gradient with rotating motion
- **Sunset/Sunrise** - Happy gradient transitioning to evening colors

### Environmental Themes
- **Desert Dream** - Desert colors with local and global movement
- **In The Jungle** - Jungle colors with dark light overlay

### Utility
- **Test Strip** - Cycles through LEDs one at a time for testing

## Installation

This package is designed to work with the LX Package system. To use:

1. Build the package: `mvn clean package`
2. The JAR will be created in the `target/` directory
3. Install the JAR in your LX installation

## Requirements

- LX Framework v1.5.0 or higher
- Java 8 or higher
- Model with fixtures tagged as "umbrella"

## Fixture Requirements

The patterns expect fixtures to be tagged with "umbrella" and work best with:
- Radial spoke-based LED arrangements
- 3D positioned models where local coordinates matter
- Models with proper center and bounds calculation

## Building

```bash
cd DuckPondPackage
mvn clean package
```

The built JAR will include all necessary metadata for LX Package recognition.

## Original Implementation

These patterns are based on the original DuckPond Bar lighting system, refactored to work as individual LX patterns using the modern LX framework's model hierarchy and tagging system.

## License

MIT License - See LICENSE file for details.

## Author

Tinic Uro, 2019