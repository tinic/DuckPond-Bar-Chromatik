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

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.pattern.LXPattern;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.EnumParameter;
import heronarts.lx.parameter.BoundedParameter;
import heronarts.lx.structure.LXFixture;

@LXCategory("DuckPond")
public class DuckPondBarPattern extends LXPattern {
  
  private double tm = 0.0;

  public enum Effect {
    Spring,
    Summer,
    Autumn,
    Winter,
    AfterRain,
    SunsetSunrise,
    DesertDream,
    InTheJungle,
    TestStrip,
  };

  public final EnumParameter<Effect> effect =
    new EnumParameter<Effect>("Effect", Effect.Spring)
    .setDescription("Which built-in Effect?");

  public final BoundedParameter speed = 
      new BoundedParameter("Speed", 0, -10, 10)
      .setDescription("Speed")
      .setUnits(LXParameter.Units.NONE);

  public DuckPondBarPattern(LX lx) {
    super(lx);
    addParameter(this.effect);
    addParameter(this.speed);
  }

  @Override
  protected void run(double deltaMs) {
    double tmFactor = Math.pow(1024, speed.getNormalized() - 0.5);
    tm += (deltaMs * (1.0 / 1000.0)) * tmFactor;
    
    // Iterate through all fixtures in the model
    if (model instanceof heronarts.lx.model.LXModel) {
      // Check if we have DuckPond fixtures
      for (LXPoint point : model.points) {
        // Default to black
        int color = LX.rgb(0, 0, 0);
        
        // Find which fixture this point belongs to and calculate its effect
        DuckPondFixture fixture = findFixtureForPoint(point);
        if (fixture != null) {
          int localIndex = getLocalIndexForPoint(fixture, point);
          LXFloat4 globPos = new LXFloat4(point.x, point.y, point.z);
          LXFloat4 result = fixture.calc(effect.getEnum(), localIndex, tm, globPos);
          
          // Convert LXFloat4 to RGB color
          int r = (int) Math.max(0, Math.min(255, result.x * 255.0));
          int g = (int) Math.max(0, Math.min(255, result.y * 255.0));
          int b = (int) Math.max(0, Math.min(255, result.z * 255.0));
          color = LX.rgb(r, g, b);
        }
        
        colors[point.index] = color;
      }
    }
  }
  
  private DuckPondFixture findFixtureForPoint(LXPoint point) {
    // This is a simplified approach - in a real implementation, we'd need
    // to properly track which points belong to which fixtures through the model hierarchy
    // For now, we'll assume all points belong to the first DuckPond fixture we find
    
    // In the headless app, we'll need to maintain a reference to our fixtures
    // This is a placeholder that would be filled in by the actual implementation
    return null;
  }
  
  private int getLocalIndexForPoint(DuckPondFixture fixture, LXPoint point) {
    // Calculate the local index of this point within its fixture
    // This would need to be implemented based on how the fixtures are indexed
    return 0;
  }
}