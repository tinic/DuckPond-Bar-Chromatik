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
 * Summer effect - Rainbow gradient with global coordinate sparkles
 */
@LXCategory("DuckPond")
public class SummerPattern extends UmbrellaPattern {
  
  private Gradient rainbowGradient;
  
  public SummerPattern(LX lx) {
    super(lx);
    initGradients();
  }
  
  private void initGradients() {
    Float4[] rainbowGradient = {
       new Float4(0.0, 1.0, 1.0, 0.00),
       new Float4(1.0, 1.0, 1.0, 1.00)
    };

    this.rainbowGradient = new Gradient(rainbowGradient, Gradient.ColorMode.HSV);
  }
  
  @Override
  protected Float4 calculatePointColor(LXPoint point, Float4 globalPos, Float4 localPos, double time) {
    double x0 = Math.sin((globalPos.x + 1.0) * 0.5 + time * 0.050);
    double y0 = Math.cos((globalPos.y + 1.0) * 0.5 + time * 0.055);
    double x1 = Math.sin((globalPos.x + 1.0) * 10 + time * 0.50);
    double y1 = Math.cos((globalPos.y + 1.0) * 10 + time * 0.55);
    return rainbowGradient.reflect(x0 * y0).add(com.duckpond.Gradient.rgbToOklab(1.0,1.0,1.0).mul(x1 * y1)).clamp();
  }
}