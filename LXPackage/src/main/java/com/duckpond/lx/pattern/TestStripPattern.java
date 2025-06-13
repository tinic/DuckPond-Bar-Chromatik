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
package com.duckpond.lx.pattern;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;
import com.duckpond.lx.LXFloat4;
import com.duckpond.lx.Gradient;

/**
 * Test Strip pattern - Cycles through LEDs one at a time for testing
 */
@LXCategory("DuckPond")
public class TestStripPattern extends UmbrellaPattern {
  
  private int currentUmbrella = 0;
  private int currentLED = 0;
  private double lastTime = 0;
  
  public TestStripPattern(LX lx) {
    super(lx);
  }
  
  @Override
  protected void run(double deltaMs) {
    // Update animation time based on speed
    double tmFactor = Math.pow(1024, speed.getNormalized() - 0.5);
    runTime += (deltaMs * (1.0 / 1000.0)) * tmFactor;
    
    // Clear all points first
    for (int i = 0; i < colors.length; i++) {
      colors[i] = LX.rgb(0, 0, 0);
    }
    
    // Process all umbrellas by tag
    int umbrellaIndex = 0;
    for (LXModel umbrella : model.sub("umbrella")) {
      if (umbrellaIndex == currentUmbrella) {
        processTestUmbrella(umbrella);
      }
      umbrellaIndex++;
    }
    
    // Update LED position based on time
    if (runTime - lastTime > 0.1) { // Move to next LED every 0.1 seconds
      lastTime = runTime;
      currentLED++;
      
      // Check if we need to move to next umbrella
      int umbrellaCount = 0;
      for (LXModel umbrella : model.sub("umbrella")) {
        if (umbrellaCount == currentUmbrella) {
          if (currentLED >= umbrella.points.length) {
            currentLED = 0;
            currentUmbrella++;
          }
          break;
        }
        umbrellaCount++;
      }
      
      // Reset if we've gone through all umbrellas
      int totalUmbrellas = 0;
      for (LXModel umbrella : model.sub("umbrella")) {
        totalUmbrellas++;
      }
      if (currentUmbrella >= totalUmbrellas) {
        currentUmbrella = 0;
      }
    }
  }
  
  protected void processTestUmbrella(LXModel umbrella) {
    // Light up only the current LED
    if (currentLED < umbrella.points.length) {
      LXPoint point = umbrella.points[currentLED];
      colors[point.index] = LX.rgb(255, 255, 255);
    }
  }
  
  @Override
  protected LXFloat4 calculatePointColor(LXPoint point, LXFloat4 globalPos, LXFloat4 localPos, double time) {
    // Not used in test pattern
    return Gradient.rgbToOklab(0, 0, 0);
  }
}