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
 * Autumn effect - Rainy gradient with autumn colors
 */
@LXCategory("DuckPond")
public class AutumnPattern extends UmbrellaPattern {
  
  private Gradient rainyGradient;
  private Gradient autumGradient;
  
  public AutumnPattern(LX lx) {
    super(lx);
    initGradients();
  }
  
  private void initGradients() {
    Float4[] rainyGradient = {
       new Float4(0x000000, 0.00),
       new Float4(0x413a40, 0.20),
       new Float4(0x65718a, 0.40),
       new Float4(0x6985b9, 0.53),
       new Float4(0xffffff, 1.00)
    };

    this.rainyGradient = new Gradient(rainyGradient, Gradient.ColorMode.RGB);

    Float4[] autumGradient = {
       new Float4(0x000000, 0.00),
       new Float4(0x351e10, 0.13),
       new Float4(0x58321a, 0.25),
       new Float4(0x60201e, 0.41),
       new Float4(0x651420, 0.56),
       new Float4(0x7b5a54, 0.70),
       new Float4(0x9abf9e, 0.83),
       new Float4(0xffffff, 1.00)
    };

    this.autumGradient = new Gradient(autumGradient, Gradient.ColorMode.RGB);
  }
  
  @Override
  protected Float4 calculatePointColor(LXPoint point, Float4 globalPos, Float4 localPos, double time) {
    double x0 = Math.sin((globalPos.x + 1.0) * 0.5 + time * 0.050);
    double y0 = Math.cos((globalPos.y + 1.0) * 0.5 + time * 0.055);
    double x1 = Math.sin((globalPos.x + 1.0) * 15 + time * 0.50);
    double y1 = Math.cos((globalPos.y + 1.0) * 15 + time * 0.55);
    return rainyGradient.clamp(x1 * y1).add(autumGradient.reflect(x0 * y0).mul(Gradient.rgbToOklab(0.5,0.5,0.5))).clamp();
  }
}