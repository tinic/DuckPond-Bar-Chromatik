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
 * Winter effect - Winter gradient with rainy overlay
 */
@LXCategory("DuckPond")
public class WinterPattern extends UmbrellaPattern {
  
  private Gradient winterGradient;
  private Gradient rainyGradient;
  
  public WinterPattern(LX lx) {
    super(lx);
    initGradients();
  }
  
  private void initGradients() {
    LXFloat4[] winterGradient = {
       new LXFloat4(0xa3eed6,0.00),
       new LXFloat4(0xdcbcd4,0.21),
       new LXFloat4(0xff96d0,0.39),
       new LXFloat4(0xcb81d6,0.65),
       new LXFloat4(0x4b51f5,1.00)
    };

    this.winterGradient = new Gradient(winterGradient, Gradient.ColorMode.OKLAB);

    LXFloat4[] rainyGradient = {
       new LXFloat4(0x000000, 0.00),
       new LXFloat4(0x413a40, 0.20),
       new LXFloat4(0x65718a, 0.40),
       new LXFloat4(0x6985b9, 0.53),
       new LXFloat4(0xffffff, 1.00)
    };

    this.rainyGradient = new Gradient(rainyGradient, Gradient.ColorMode.OKLAB);
  }
  
  @Override
  protected LXFloat4 calculatePointColor(LXPoint point, LXFloat4 globalPos, LXFloat4 localPos, double time) {
    double x0 = Math.sin((globalPos.x + 1.0) * 0.5 + time * 0.050);
    double y0 = Math.cos((globalPos.y + 1.0) * 0.5 + time * 0.055);
    double x1 = Math.sin((localPos.x + 1.0) * 0.25 + time * 0.050);
    double y1 = Math.cos((localPos.y + 1.0) * 0.25 + time * 0.055);
    double l = 1.0 - localPos.len() + 0.5;
    return winterGradient.reflect(x1 * y1).mul(l).mul(rainyGradient.reflect(x0 * y0));
  }
}