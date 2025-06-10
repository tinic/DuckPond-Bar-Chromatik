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
import heronarts.lx.model.LXPoint;
import com.duckpond.lx.LXFloat4;
import com.duckpond.lx.Gradient;

/**
 * Sunset/Sunrise effect - Happy gradient transitioning to evening
 */
@LXCategory("DuckPond")
public class SunsetSunrisePattern extends UmbrellaPattern {
  
  private Gradient happyGradient;
  private Gradient eveningGradient;
  
  public SunsetSunrisePattern(LX lx) {
    super(lx);
    initGradients();
  }
  
  private void initGradients() {
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
  }
  
  @Override
  protected LXFloat4 calculatePointColor(LXPoint point, LXFloat4 globalPos, LXFloat4 localPos, double time) {
    double a = Math.max(0.0, Math.cos(globalPos.x + Math.sin(time * 0.10))+Math.sin(globalPos.y + Math.cos(time* 0.10))-1.0);
    LXFloat4 pos = globalPos.rotate2d(time * 0.30).add(new LXFloat4(time * 0.30, 0.0, 0.0, 0.0)).mul(0.05);
    double l = 1.0 - localPos.len() + 0.5;
    LXFloat4 c0 = happyGradient.reflect(pos.x).mul(l).clamp().gamma();
    LXFloat4 c1 = eveningGradient.clamp(a);
    return LXFloat4.lerp(c0, c1, a);
  }
}