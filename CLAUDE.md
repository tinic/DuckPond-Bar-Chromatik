# DuckPond Bar Chromatik LED Art Installation

A headless LED art installation system for DuckPond Bar, migrated to the modern LX framework. This project controls LED lighting for umbrella fixtures and bar-top strips, providing atmospheric lighting effects for the venue.

## Project Overview

**License:** MIT License  
**Author:** Tinic Uro  
**Copyright:** 2019 Tinic Uro  
**Framework:** LX LED Framework v1.1.0  
**Language:** Java  
**Build System:** Apache Maven  

## Architecture

This is a headless Java application that runs lighting patterns on physical LED fixtures using the LX Package system:

- **11 Umbrella fixtures** - Each with configurable spokes and LEDs per spoke (default: 8 spokes, 10 LEDs/spoke)
- **Bar top fixture** - Front and back LED strips following the bar contour (disabled by default)
- **Network output** - ArtNet protocol to individual fixture IP addresses
- **LXPackage integration** - Modular pattern system using LX framework

## Key Components

### LXPackage System (`LXPackage/`)
- **Individual Pattern Classes** - Each effect is now a separate LXPattern:
  - `SpringPattern`, `SummerPattern`, `AutumnPattern`, `WinterPattern` (seasonal themes)
  - `AfterRainPattern`, `SunsetSunrisePattern` (atmospheric effects)
  - `DesertDreamPattern`, `InTheJunglePattern` (environmental themes)
  - `TestStripPattern` (debugging/testing)
- **Base Classes** - `UmbrellaPattern` provides common functionality and coordinate transformation
- **Fixture Definitions** - LXF files included in package resources
- **Build System** - Maven-based with proper LXPackage metadata

### Main Application
- `DuckPondBarHeadless` - Shell script launcher
- Uses `heronarts.lx.headless.LXHeadless` with LXPackage on classpath
- Loads `DuckPond.lxp` project file by default
- Automatically builds LXPackage if needed

### Legacy Components (preserved for reference)
- Original headless implementation in LX submodule
- Build system updated to use LXPackage approach

### Utility Classes (in LXPackage)
- `LXFloat4.java` - 4D vector math for color and position calculations
- `Gradient.java` - Color gradient system with RGB/HSV support

## Build Instructions

### Prerequisites
- Java 8 or higher
- Apache Maven 3.6+

### Available Build Targets

```bash
# Build entire project
mvn clean package

# Build only LXPackage
mvn clean package -pl LXPackage

# Run the application with default project
mvn exec:java

# Run with custom project file
mvn exec:java -Prun-project -Dproject.file=myproject.lxp

# Create distribution packages
mvn package assembly:single

# Clean build
mvn clean
```

### Convenience Build Script

```bash
# Build project
./build.sh

# Run application  
./build.sh run

# Build distribution
./build.sh distribution

# Run with custom project
./build.sh --project-file=myproject.lxp
```

### Runtime Usage

```bash
# Run with default project file (DuckPond.lxp)
./DuckPondBarHeadless

# Run with custom project file
./DuckPondBarHeadless myproject.lxp

# Or use Maven directly
mvn exec:java

# Or use build script
./build.sh run
```

### LXPackage Development

```bash
# Build the LXPackage
mvn clean package -pl LXPackage

# Install in LX Studio (copy JAR to packages directory)
cp LXPackage/target/duckpond-lx-1.0.0.jar /path/to/lx/packages/

# Create distribution for deployment
./build.sh distribution
```

## Physical Setup

### Umbrella Layout
```
Physical umbrella positions (11 total):
    11    13    15    17    19
 10    12    14    16    18    20
```

### Network Configuration
- **Protocol:** ArtNet (UDP port 6454)
- **Umbrella fixtures:** IP addresses `10.10.33.1` through `10.10.33.11`
- **Bar top fixture:** `10.10.33.20` (if enabled)
- **Universe mapping:** Universe 0 for umbrellas, Universe 0+1 for bar top
- **Data splitting:** Up to 170 LEDs per ArtNet universe (510 bytes)

### Fixture Specifications
- **Umbrella fixtures:** 8 spokes × 10 LEDs = 80 LEDs each
- **Bar top fixture:** 400+ LEDs in front strip, 400+ LEDs in back strip
- **Total LEDs:** ~1,680 LEDs across all fixtures

## Pattern Development

### Effect Calculation
Each fixture implements `calc(Effect effect, int index, double time, LXFloat4 position)`:
- **effect** - Selected visual effect enum
- **index** - LED index within fixture
- **time** - Current time for animation
- **position** - 3D world position of LED

### Adding New Effects
1. Add effect to `DuckPondBarPattern.Effect` enum
2. Implement effect case in each fixture's `calc()` method
3. Use gradient system for smooth color transitions

### Gradient System
- Supports RGB and HSV color modes
- Methods: `reflect()`, `repeat()`, `clamp()`
- Pre-built gradients for seasonal and atmospheric themes

## Configuration

### Umbrella Parameters
- `spokes` - Number of radial spokes (3-16, default: 8)
- `ledsPerSpoke` - LEDs per spoke (1-20, default: 10)
- `spokeLength` - Physical spoke length in meters (default: 0.305m)
- `centerOffset` - Center offset for LED positioning (default: 0.045m)

### Pattern Parameters
- `visualMode` - Selected visual effect (renamed from `effect` to avoid LX framework conflicts)
- `speed` - Animation speed multiplier (-10 to +10)

## Development Notes

### Build System
- Uses Apache Ant with standard Java project structure
- Framework JAR included in `lib/` directory
- All dependencies self-contained

### Testing
- `TestStrip` effect cycles through individual LEDs for fixture verification
- Use Wireshark or tcpdump to monitor ArtNet packets: `sudo tcpdump -i any -n udp port 6454`
- Verify packets are sent to correct fixture IP addresses

### Performance
- Headless operation for production deployment
- Optimized vector math in `LXFloat4` class
- Efficient gradient lookups with 256-point resolution

## Dependencies

- **LX Framework:** `lib/lx-1.1.0-jar-with-dependencies.jar`
- **Java Standard Library:** Math utilities, collections, networking

## Migration Notes

This project has been migrated from the original `heronarts.lx.headless.duckpond` package structure to the simplified `duckpond` package for easier maintenance. Key improvements:

- **Simplified package structure:** All classes now in `duckpond` package
- **Modern LX API usage:** Updated to LX framework v1.1.0 APIs with proper parameter registration
- **Parameter naming:** Renamed `effect` parameter to `visualMode` to avoid LX framework conflicts
- **Deprecation fixes:** Updated from deprecated `addParameter(parameter)` to modern `addParameter(path, parameter)`
- **ArtNet output:** Properly configured for individual fixture IP addresses
- **Convenience script:** `DuckPondBarHeadless` executable for easy deployment
- **Clean build system:** Standard Maven-style directory structure with Ant
- **LX Framework source:** Added as git submodule for development reference

## Troubleshooting

**Version warnings:** When loading old project files, you may see:
```
[LX] Failed to parse LX version identifier: Couldn't parse: 0.1
```
This is expected and non-fatal - the project will still load successfully.

**Network verification:** To verify ArtNet packets are being sent:
```bash
# Monitor all ArtNet traffic
sudo tcpdump -i any -n udp port 6454

# Test fixture connectivity
ping 10.10.33.1  # Test umbrella 1
ping 10.10.33.20 # Test bar top
```

This project represents a complete LED art installation system, providing both the technical infrastructure and artistic content for atmospheric venue lighting.