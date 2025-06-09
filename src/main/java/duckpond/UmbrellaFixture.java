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
import heronarts.lx.transform.LXMatrix;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BoundedParameter;
import java.util.List;

public class UmbrellaFixture extends DuckPondFixture {

  public final BoundedParameter spokes = 
    new BoundedParameter("Spokes", 8, 3, 16)
    .setDescription("Number of spokes in the umbrella");

  public final BoundedParameter ledsPerSpoke = 
    new BoundedParameter("LEDs/Spoke", 10, 1, 20)
    .setDescription("Number of LEDs per spoke");

  public final BoundedParameter spokeLength = 
    new BoundedParameter("Length", 0.305, 0.1, 1.0)
    .setDescription("Length of each spoke in meters");

  public final BoundedParameter centerOffset = 
    new BoundedParameter("Offset", 0.045, 0.0, 0.2)
    .setDescription("Center offset of spokes in meters");

  private final int umbrellaId;
  
  private Gradient rainbowGradient;
  private Gradient rainbowGradientBright;
  private Gradient rainyGradient;
  private Gradient autumGradient;
  private Gradient winterGradient;
  private Gradient happyGradient;
  private Gradient eveningGradient;
  private Gradient desertDream;
  private Gradient inTheJungle;
  private Gradient darkLight;

  public UmbrellaFixture(LX lx, int id, String ip, double x, double y, double z) {
    super(lx, "Umbrella-" + id, ip);
    
    this.umbrellaId = id;
    
    // Set position
    this.x.setValue(x);
    this.y.setValue(y);
    this.z.setValue(z);
    
    // Add metrics parameters (changes to these rebuild the fixture)
    addMetricsParameter("spokes", this.spokes);
    addMetricsParameter("ledsPerSpoke", this.ledsPerSpoke);
    
    // Add geometry parameters (changes to these update positions)
    addGeometryParameter("spokeLength", this.spokeLength);
    addGeometryParameter("centerOffset", this.centerOffset);
    
    initGradients();
  }

  @Override
  public int size() {
    return (int)(this.spokes.getValue() * this.ledsPerSpoke.getValue());
  }

  @Override
  protected void computePointGeometry(LXMatrix transform, List<LXPoint> points) {
    int numSpokes = (int) this.spokes.getValue();
    int numLedsPerSpoke = (int) this.ledsPerSpoke.getValue();
    double length = this.spokeLength.getValue();
    double offset = this.centerOffset.getValue();
    
    int pointIndex = 0;
    for (int spoke = 0; spoke < numSpokes; spoke++) {
      double spokeAngle = (2.0 * Math.PI / numSpokes) * spoke + Math.PI / 2.0;
      double xm = Math.sin(spokeAngle);
      double ym = Math.cos(spokeAngle);
      
      for (int led = 0; led < numLedsPerSpoke; led++) {
        // Alternate direction for even/odd spokes for better wiring
        double ledPos = ((spoke & 1) == 1) ? 
          (double)(numLedsPerSpoke - 1 - led) / (numLedsPerSpoke - 1) :
          (double)led / (numLedsPerSpoke - 1);
        
        double x = xm * (ledPos * length) + xm * offset; 
        double y = ym * (ledPos * length) + ym * offset; 
        double z = 0.0;
        
        LXPoint point = points.get(pointIndex++);
        // Apply transform manually: translation
        float tx = (float)(x + this.x.getValue());
        float ty = (float)(y + this.y.getValue());
        float tz = (float)(z + this.z.getValue());
        point.set(tx, ty, tz);
      }
    }
    
    invalidateBounds();
  }

  @Override
  public LXFloat4 calc(DuckPondBarPattern.Effect effect, int LEDindex, double time, LXFloat4 globPos) { 
    switch (effect) {
      case Spring: {
        double x = Math.sin((toLocal(globPos).x + 1.0) * 0.25 + time * 0.050);
        double y = Math.cos((toLocal(globPos).y + 1.0) * 0.25 + time * 0.055);
        double l = 1.0 - toLocal(globPos).len() + 0.5;
        return rainbowGradientBright.reflect(x * y).mul(l).clamp().gamma();
      }
      case Summer: {
        double x0 = Math.sin((globPos.x + 1.0) * 0.5 + time * 0.050);
        double y0 = Math.cos((globPos.y + 1.0) * 0.5 + time * 0.055);
        double x1 = Math.sin((globPos.x + 1.0) * 10 + time * 0.50);
        double y1 = Math.cos((globPos.y + 1.0) * 10 + time * 0.55);
        return rainbowGradient.reflect(x0 * y0).add(new LXFloat4(1.0,1.0,1.0).mul(x1 * y1).clamp()).clamp();
      } 
      case Autumn: {
        double x0 = Math.sin((globPos.x + 1.0) * 0.5 + time * 0.050);
        double y0 = Math.cos((globPos.y + 1.0) * 0.5 + time * 0.055);
        double x1 = Math.sin((globPos.x + 1.0) * 15 + time * 0.50);
        double y1 = Math.cos((globPos.y + 1.0) * 15 + time * 0.55);
        return rainyGradient.clamp(x1 * y1).add(autumGradient.reflect(x0 * y0).mul(new LXFloat4(0.5,0.5,0.5))).clamp();
      } 
      case Winter: {
        double x0 = Math.sin((globPos.x + 1.0) * 0.5 + time * 0.050);
        double y0 = Math.cos((globPos.y + 1.0) * 0.5 + time * 0.055);
        double x1 = Math.sin((toLocal(globPos).x + 1.0) * 0.25 + time * 0.050);
        double y1 = Math.cos((toLocal(globPos).y + 1.0) * 0.25 + time * 0.055);
        double l = 1.0 - toLocal(globPos).len() + 0.5;
        return winterGradient.reflect(x1 * y1).mul(l).mul(rainyGradient.reflect(x0 * y0)).clamp().gamma();
      } 
      case AfterRain: {
        double b = (Math.sin(globPos.x * 4.0 + time * 0.20) + Math.cos(globPos.y * 4.0 + time * 0.20)) * 0.25;
        LXFloat4 pos = globPos.rotate2d(time * 0.20).add(new LXFloat4(time * 0.20, 0.0, 0.0, 0.0)).mul(0.05);
        return rainbowGradientBright.repeat(pos.x).add(new LXFloat4(b,b,b,b)).clamp().gamma();
      }
      case SunsetSunrise: {
        double a = Math.max(0.0, Math.cos(globPos.x + Math.sin(time * 0.10))+Math.sin(globPos.y + Math.cos(time* 0.10))-1.0);
        LXFloat4 pos = globPos.rotate2d(time * 0.30).add(new LXFloat4(time * 0.30, 0.0, 0.0, 0.0)).mul(0.05);
        double l = 1.0 - toLocal(globPos).len() + 0.5;
        LXFloat4 c0 = happyGradient.reflect(pos.x).mul(l).clamp().gamma();
        LXFloat4 c1 = eveningGradient.clamp(a);
        return LXFloat4.lerp(c0, c1, a);
      }
      case DesertDream: {
        double x0 = Math.sin((globPos.x + 1.0) * 0.5 + time * 0.050);
        double y0 = Math.cos((globPos.y + 1.0) * 0.5 + time * 0.055);
        double x1 = Math.sin((toLocal(globPos).x + 1.0) * 0.25 + time * 0.050);
        double y1 = Math.cos((toLocal(globPos).y + 1.0) * 0.25 + time * 0.055);
        double l = 1.0 - toLocal(globPos).len() + 0.5;
        return desertDream.reflect(x1 * y1).mul(l).add(desertDream.reflect(x0 * y0)).clamp().gamma();
      } 
      case InTheJungle: {
        double a = Math.max(0.0, Math.cos(globPos.x + Math.sin(time * 0.10))+Math.sin(globPos.y + Math.cos(time* 0.10))-1.0);
        LXFloat4 pos = globPos.rotate2d(time * 0.30).add(new LXFloat4(time * 0.30, 0.0, 0.0, 0.0)).mul(0.05);
        double l = 1.0 - toLocal(globPos).len() + 0.5;
        LXFloat4 c0 = inTheJungle.reflect(pos.x).mul(l).clamp().gamma();
        LXFloat4 c1 = darkLight.clamp(a);
        return LXFloat4.lerp(c0, c1, a);
      }
      case TestStrip: {
        int led = (int)(time * 10.0);
        led %= size();
        return new LXFloat4(1.0, 1.0, 1.0).mul(led == LEDindex ? 1.0 : 0.0);
      }
    }
    return globPos;    
  }

  private void initGradients() {
    
    LXFloat4[] rainbowGradient = {
       new LXFloat4(0.0, 1.0, 1.0, 0.00),
       new LXFloat4(1.0, 1.0, 1.0, 1.00)
    };

    this.rainbowGradient = new Gradient(rainbowGradient, Gradient.ColorMode.HSV);

    LXFloat4[] rainbowGradientBright = {
       new LXFloat4(0xff0000, 0.00),
       new LXFloat4(0xffbd96, 0.10),
       new LXFloat4(0xffff00, 0.17),
       new LXFloat4(0xc3ffa9, 0.25),
       new LXFloat4(0x00ff00, 0.33),
       new LXFloat4(0xd1ffbf, 0.38),
       new LXFloat4(0xaffff3, 0.44),
       new LXFloat4(0x29fefe, 0.50),
       new LXFloat4(0x637eff, 0.59),
       new LXFloat4(0x0000ff, 0.67),
       new LXFloat4(0x9c3fff, 0.75),
       new LXFloat4(0xff00ff, 0.83),
       new LXFloat4(0xffc2b0, 0.92),
       new LXFloat4(0xff0000, 1.00)
    };

    this.rainbowGradientBright = new Gradient(rainbowGradientBright, Gradient.ColorMode.RGB);

    LXFloat4[] rainyGradient = {
       new LXFloat4(0x000000, 0.00),
       new LXFloat4(0x413a40, 0.20),
       new LXFloat4(0x65718a, 0.40),
       new LXFloat4(0x6985b9, 0.53),
       new LXFloat4(0xffffff, 1.00)
    };

    this.rainyGradient = new Gradient(rainyGradient, Gradient.ColorMode.RGB);

    LXFloat4[] autumGradient = {
       new LXFloat4(0x000000, 0.00),
       new LXFloat4(0x351e10, 0.13),
       new LXFloat4(0x58321a, 0.25),
       new LXFloat4(0x60201e, 0.41),
       new LXFloat4(0x651420, 0.56),
       new LXFloat4(0x7b5a54, 0.70),
       new LXFloat4(0x9abf9e, 0.83),
       new LXFloat4(0xffffff, 1.00)
    };

    this.autumGradient = new Gradient(autumGradient, Gradient.ColorMode.RGB);

    LXFloat4[] winterGradient = {
       new LXFloat4(0xa3eed6,0.00),
       new LXFloat4(0xdcbcd4,0.21),
       new LXFloat4(0xff96d0,0.39),
       new LXFloat4(0xcb81d6,0.65),
       new LXFloat4(0x4b51f5,1.00)
    };

    this.winterGradient = new Gradient(winterGradient, Gradient.ColorMode.RGB);

    LXFloat4[] happyGradient = {
       new LXFloat4(0x22c1c3,0.00),
       new LXFloat4(0x4387c0,0.33),
       new LXFloat4(0xbb6161,0.66),
       new LXFloat4(0xfdbb2d,1.00)
    };

    this.happyGradient = new Gradient(happyGradient, Gradient.ColorMode.RGB);

    LXFloat4[] eveningGradient = {
       new LXFloat4(0x000000,0.00),
       new LXFloat4(0x4387c0,0.80),
       new LXFloat4(0xbb6161,0.90),
       new LXFloat4(0xff9500,0.95),
       new LXFloat4(0xffffff,1.00)
    };

    this.eveningGradient = new Gradient(eveningGradient, Gradient.ColorMode.RGB);

    LXFloat4[] desertDream = {
       new LXFloat4(0x4d5951,0.00),
       new LXFloat4(0x372a25,0.19),
       new LXFloat4(0x863c25,0.41),
       new LXFloat4(0xa15123,0.63),
       new LXFloat4(0xd6aa68,0.84),
       new LXFloat4(0xf7d6b4,1.00)
    };

    this.desertDream = new Gradient(desertDream, Gradient.ColorMode.RGB);

    LXFloat4[] inTheJungle = {
       new LXFloat4(0x135e46,0.00),
       new LXFloat4(0x478966,0.20),
       new LXFloat4(0x73a788,0.40),
       new LXFloat4(0xe3c6ad,0.70),
       new LXFloat4(0xd09d7b,0.90),
       new LXFloat4(0xb67b65,1.00)
    };

    this.inTheJungle = new Gradient(inTheJungle, Gradient.ColorMode.RGB);

    LXFloat4[] darkLight = {
       new LXFloat4(0x000000,0.00),
       new LXFloat4(0x135e46,0.50),
       new LXFloat4(0x2ea61b,0.65),
       new LXFloat4(0x478966,0.70),
       new LXFloat4(0x000000,1.00)
    };

    this.darkLight = new Gradient(darkLight, Gradient.ColorMode.RGB);
  }

  public int getUmbrellaId() {
    return this.umbrellaId;
  }
}