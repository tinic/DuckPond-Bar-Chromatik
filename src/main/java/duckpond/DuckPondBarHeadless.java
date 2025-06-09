/*
Copyright 2019 Tinic Uro

Permission is hereby granted, free of charge, to any person obtaining a
copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package duckpond;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import heronarts.lx.LX;
import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;
import heronarts.lx.transform.LXMatrix;
import heronarts.lx.pattern.LXPattern;

/**
 * DuckPond Bar Headless - Migrated to modern LX framework
 * 
 * This is a complete reimplementation of the DuckPond Bar lighting system
 * using the modern LX framework. It maintains the same visual effects and
 * lighting patterns as the original 2018 version.
 */
public class DuckPondBarHeadless {

  // Store references to our fixtures for pattern calculation
  private static List<UmbrellaFixture> umbrellaFixtures = new ArrayList<>();
  private static BarTopFixture barTopFixture = null;
  private static Map<Integer, DuckPondFixture> pointToFixture = new HashMap<>();

  public static LXModel buildModel(LX lx) {
    // Create a simple model with umbrellas
    
    // Physical layout of umbrellas (same as original):
    //     11    13    15    17    19
    //  10    12    14    16    18    20
    
    UmbrellaFixture[] umbrellas = {
      new UmbrellaFixture(lx, 0, "10.10.33.1", -2.000, -0.500 + 1.000, 3.0000),
      new UmbrellaFixture(lx, 1, "10.10.33.2", -1.600,  0.500 + 1.000, 3.0000),
      new UmbrellaFixture(lx, 2, "10.10.33.3", -1.200, -0.500 + 1.000, 3.0000),
      new UmbrellaFixture(lx, 3, "10.10.33.4", -0.800,  0.500 + 1.000, 3.0000),
      new UmbrellaFixture(lx, 4, "10.10.33.5", -0.400, -0.500 + 1.000, 3.0000),
      new UmbrellaFixture(lx, 5, "10.10.33.6",  0.000,  0.500 + 1.000, 3.0000),
      new UmbrellaFixture(lx, 6, "10.10.33.7",  0.400, -0.500 + 1.000, 3.0000),
      new UmbrellaFixture(lx, 7, "10.10.33.8",  0.800,  0.500 + 1.000, 3.0000),
      new UmbrellaFixture(lx, 8, "10.10.33.9",  1.200, -0.500 + 1.000, 3.0000),
      new UmbrellaFixture(lx, 9, "10.10.33.10", 1.600,  0.500 + 1.000, 3.0000),
      new UmbrellaFixture(lx, 10, "10.10.33.11", 2.000, -0.500 + 1.000, 3.0000)
    };

    // Store umbrella fixtures for pattern calculations
    for (UmbrellaFixture umbrella : umbrellas) {
      umbrellaFixtures.add(umbrella);
    }

    // Add bar top fixture (commented out for now like in original)
    // barTopFixture = new BarTopFixture(lx, "10.10.33.20");

    // Build a simple model from the fixtures
    List<LXPoint> allPoints = new ArrayList<>();
    for (UmbrellaFixture umbrella : umbrellas) {
      // Generate points for this fixture
      List<LXPoint> fixturePoints = new ArrayList<>();
      for (int i = 0; i < umbrella.size(); i++) {
        fixturePoints.add(new LXPoint());
      }
      LXMatrix transform = new LXMatrix();
      transform.translate((float)umbrella.x.getValue(), (float)umbrella.y.getValue(), (float)umbrella.z.getValue());
      umbrella.computePointGeometry(transform, fixturePoints);
      allPoints.addAll(fixturePoints);
    }
    
    LXModel model = new LXModel(allPoints);
    
    // Build point-to-fixture mapping for pattern calculations
    buildPointMapping(model);
    
    return model;
  }

  private static void buildPointMapping(LXModel model) {
    pointToFixture.clear();
    
    // Map each point to its fixture (simple sequential mapping)
    int currentIndex = 0;
    for (UmbrellaFixture fixture : umbrellaFixtures) {
      for (int i = 0; i < fixture.size(); i++) {
        pointToFixture.put(currentIndex + i, fixture);
      }
      currentIndex += fixture.size();
    }
    
    if (barTopFixture != null) {
      for (int i = 0; i < barTopFixture.size(); i++) {
        pointToFixture.put(currentIndex + i, barTopFixture);
      }
    }
  }

  // Getter methods for NetworkSetup
  public static List<UmbrellaFixture> getUmbrellaFixtures() {
    return umbrellaFixtures;
  }
  
  public static BarTopFixture getBarTopFixture() {
    return barTopFixture;
  }
  
  public static Map<Integer, DuckPondFixture> getPointToFixture() {
    return pointToFixture;
  }

  /**
   * Enhanced DuckPond Bar Pattern that uses the fixture mapping
   */
  public static class DuckPondBarPatternEnhanced extends DuckPondBarPattern {
    
    public DuckPondBarPatternEnhanced(LX lx) {
      super(lx);
    }

    @Override
    protected void run(double deltaMs) {
      double tmFactor = Math.pow(1024, speed.getNormalized() - 0.5);
      double tm = this.runMs * (1.0 / 1000.0) * tmFactor;
      
      // Iterate through all points in the model
      for (int i = 0; i < colors.length; i++) {
        colors[i] = LX.rgb(0, 0, 0); // Default to black
        
        if (i < model.points.length) {
          DuckPondFixture fixture = pointToFixture.get(i);
          if (fixture != null) {
            // Calculate local index within the fixture (simplified)
            int localIndex = 0;
            int currentIndex = 0;
            for (UmbrellaFixture uf : umbrellaFixtures) {
              if (uf == fixture) {
                localIndex = i - currentIndex;
                break;
              }
              currentIndex += uf.size();
            }
            
            // Get the point position
            heronarts.lx.model.LXPoint point = model.points[i];
            LXFloat4 globPos = new LXFloat4(point.x, point.y, point.z);
            
            // Calculate the effect color
            LXFloat4 result = fixture.calc(effect.getEnum(), localIndex, tm, globPos);
            
            // Convert to RGB color with proper clamping
            int r = (int) Math.max(0, Math.min(255, result.x * 255.0));
            int g = (int) Math.max(0, Math.min(255, result.y * 255.0));
            int b = (int) Math.max(0, Math.min(255, result.z * 255.0));
            
            colors[i] = LX.rgb(r, g, b);
          }
        }
      }
    }
  }

  public static void main(String[] args) {
    try {
      System.out.println("Starting DuckPond Bar Headless...");
      
      // Build the model with all fixtures first
      LX lx = new LX();
      LXModel model = buildModel(lx);
      
      // Initialize LX engine with the model
      lx = new LX(model);
      System.out.println("Model built with " + model.points.length + " points");
      
      
      // Add ArtNet network output
      NetworkSetup.setup(lx);
      
      // Load project file if specified
      if (args.length > 0) {
        File projectFile = new File(args[0]);
        if (projectFile.exists()) {
          System.out.println("Loading project file: " + args[0]);
          lx.openProject(projectFile);
        } else {
          System.err.println("Project file not found: " + args[0]);
        }
      } else {
        // Set up default patterns
        System.out.println("Setting up default DuckPond Bar pattern");
        lx.setPatterns(new LXPattern[] {
          new DuckPondBarPatternEnhanced(lx)
        });
      }

      // Start the engine
      System.out.println("Starting LX engine...");
      lx.engine.start();
      
      System.out.println("DuckPond Bar Headless is running!");
      System.out.println("- Model: " + model.points.length + " points across " + umbrellaFixtures.size() + " umbrella fixtures");
      System.out.println("- Output: ArtNet to fixture IP addresses");
      System.out.println("- Press Ctrl+C to exit");
      
      // Keep the application running
      while (true) {
        Thread.sleep(100);
      }
      
    } catch (Exception x) {
      System.err.println("Error starting DuckPond Bar Headless: " + x.getMessage());
      x.printStackTrace();
      LX.error(x);
    }
  }
}