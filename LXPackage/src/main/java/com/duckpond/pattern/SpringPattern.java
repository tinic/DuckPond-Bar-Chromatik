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
 * Spring effect - Rainbow gradient with local coordinate modulation
 */
@LXCategory("DuckPond")
public class SpringPattern extends UmbrellaPattern {
  
  private Gradient rainbowGradientBright;
  
  public SpringPattern(LX lx) {
    super(lx);
    initGradients();
  }
  
  private void initGradients() {
    Float4[] rainbowGradientBright = {
       new Float4(0xff0000, 0.00),
       new Float4(0xffbd96, 0.10),
       new Float4(0xffff00, 0.17),
       new Float4(0xc3ffa9, 0.25),
       new Float4(0x00ff00, 0.33),
       new Float4(0xd1ffbf, 0.38),
       new Float4(0xaffff3, 0.44),
       new Float4(0x29fefe, 0.50),
       new Float4(0x637eff, 0.59),
       new Float4(0x0000ff, 0.67),
       new Float4(0x9c3fff, 0.75),
       new Float4(0xff00ff, 0.83),
       new Float4(0xffc2b0, 0.92),
       new Float4(0xff0000, 1.00)
    };

    this.rainbowGradientBright = new Gradient(rainbowGradientBright, Gradient.ColorMode.RGB);
  }
  
  @Override
  protected Float4 calculatePointColor(LXPoint point, Float4 globalPos, Float4 localPos, double time) {
    double x = Math.sin((localPos.x + 1.0) * 0.25 + time * 0.050);
    double y = Math.cos((localPos.y + 1.0) * 0.25 + time * 0.055);
    double l = 1.0 - localPos.len() + 0.5;
    return rainbowGradientBright.reflect(x * y).mul(l);
  }
}