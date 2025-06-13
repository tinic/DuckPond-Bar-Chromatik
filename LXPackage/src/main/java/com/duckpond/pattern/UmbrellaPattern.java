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
package com.duckpond.pattern;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.pattern.LXPattern;
import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BoundedParameter;
import heronarts.lx.parameter.LXParameter;
import com.duckpond.Float4;

/**
 * Base class for umbrella patterns. Provides common functionality
 * for coordinate transformation and timing.
 */
@LXCategory("DuckPond")
public abstract class UmbrellaPattern extends LXPattern {
  
  protected double runTime = 0.0;
  
  public final BoundedParameter speed = 
      new BoundedParameter("Speed", 0, -10, 10)
      .setDescription("Animation speed")
      .setUnits(LXParameter.Units.NONE);

  protected UmbrellaPattern(LX lx) {
    super(lx);
    addParameter("speed", this.speed);
  }

  @Override
  protected void run(double deltaMs) {
    // Update animation time based on speed
    double tmFactor = Math.pow(1024, speed.getNormalized() - 0.5);
    runTime += (deltaMs * (1.0 / 1000.0)) * tmFactor;
    
    // Process all umbrellas by tag
    for (LXModel umbrella : model.sub("umbrella")) {
      processUmbrella(umbrella);
    }
  }
  
  /**
   * Process a single umbrella fixture
   */
  protected void processUmbrella(LXModel umbrella) {
    // Calculate center and bounds for this umbrella
    Float4 center = calculateCenter(umbrella);
    Float4 size = calculateSize(umbrella);
    Float4 factor = calculateNormalizationFactor(size);
    
    // Process each point in the umbrella
    for (LXPoint point : umbrella.points) {
      Float4 globalPos = new Float4(point.x, point.y, point.z);
      Float4 localPos = toLocal(globalPos, center, factor);
      
      Float4 colorOklab = calculatePointColor(point, globalPos, localPos, runTime);
      Float4 colorRgb = com.duckpond.ColorSpace.toSrgb(colorOklab);
      int r = (int) Math.max(0, Math.min(255, colorRgb.x * 255.0));
      int g = (int) Math.max(0, Math.min(255, colorRgb.y * 255.0));
      int b = (int) Math.max(0, Math.min(255, colorRgb.z * 255.0));
      
      colors[point.index] = LX.rgb(r, g, b);
    }
  }
  
  /**
   * Calculate the color for a specific point. To be implemented by subclasses.
   */
  protected abstract Float4 calculatePointColor(LXPoint point, Float4 globalPos, Float4 localPos, double time);
  
  /**
   * Transform global coordinates to local normalized coordinates
   */
  protected Float4 toLocal(Float4 globalPos, Float4 center, Float4 factor) {
    return new Float4(
      (globalPos.x - center.x) * factor.x,
      (globalPos.y - center.y) * factor.y,
      (globalPos.z - center.z) * factor.z
    );
  }
  
  /**
   * Calculate the center of an umbrella model
   */
  protected Float4 calculateCenter(LXModel umbrella) {
    if (umbrella.points.length == 0) {
      return new Float4(0, 0, 0);
    }
    
    Float4 min = new Float4(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
    Float4 max = new Float4(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
    
    for (LXPoint point : umbrella.points) {
      min = min.min(new Float4(point));
      max = max.max(new Float4(point));
    }
    
    return new Float4(
      (max.x + min.x) * 0.5,
      (max.y + min.y) * 0.5,
      (max.z + min.z) * 0.5
    );
  }
  
  /**
   * Calculate the size of an umbrella model
   */
  protected Float4 calculateSize(LXModel umbrella) {
    if (umbrella.points.length == 0) {
      return new Float4(1, 1, 1);
    }
    
    Float4 min = new Float4(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
    Float4 max = new Float4(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
    
    for (LXPoint point : umbrella.points) {
      min = min.min(new Float4(point));
      max = max.max(new Float4(point));
    }
    
    return new Float4(
      max.x - min.x,
      max.y - min.y,
      max.z - min.z
    );
  }
  
  /**
   * Calculate normalization factor for coordinate transformation
   */
  protected Float4 calculateNormalizationFactor(Float4 size) {
    double xf = size.x != 0 ? 2.0 / size.x : 1.0;
    double yf = size.y != 0 ? 2.0 / size.y : 1.0;
    double zf = size.z != 0 ? 2.0 / size.z : 1.0;
    return new Float4(xf, yf, zf);
  }
}