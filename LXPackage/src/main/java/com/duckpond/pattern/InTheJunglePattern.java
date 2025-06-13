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
import heronarts.lx.model.LXPoint;
import com.duckpond.Float4;
import com.duckpond.Gradient;

/**
 * In The Jungle effect - Jungle colors with dark light overlay
 */
@LXCategory("DuckPond")
public class InTheJunglePattern extends UmbrellaPattern {
  
  private Gradient inTheJungle;
  private Gradient darkLight;
  
  public InTheJunglePattern(LX lx) {
    super(lx);
    initGradients();
  }
  
  private void initGradients() {
    Float4[] inTheJungle = {
       new Float4(0x135e46,0.00),
       new Float4(0x478966,0.20),
       new Float4(0x73a788,0.40),
       new Float4(0xe3c6ad,0.70),
       new Float4(0xd09d7b,0.90),
       new Float4(0xb67b65,1.00)
    };

    this.inTheJungle = new Gradient(inTheJungle, Gradient.ColorMode.RGB);

    Float4[] darkLight = {
       new Float4(0x000000,0.00),
       new Float4(0x135e46,0.50),
       new Float4(0x2ea61b,0.65),
       new Float4(0x478966,0.70),
       new Float4(0x000000,1.00)
    };

    this.darkLight = new Gradient(darkLight, Gradient.ColorMode.RGB);
  }
  
  @Override
  protected Float4 calculatePointColor(LXPoint point, Float4 globalPos, Float4 localPos, double time) {
    double a = Math.max(0.0, Math.cos(globalPos.x + Math.sin(time * 0.10))+Math.sin(globalPos.y + Math.cos(time* 0.10))-1.0);
    Float4 pos = globalPos.rotate2d(time * 0.30).add(new Float4(time * 0.30, 0.0, 0.0, 0.0)).mul(0.05);
    double l = 1.0 - localPos.len() + 0.5;
    Float4 c0 = inTheJungle.reflect(pos.x).mul(l);
    Float4 c1 = darkLight.clamp(a);
    return Float4.lerp(c0, c1, a);
  }
}