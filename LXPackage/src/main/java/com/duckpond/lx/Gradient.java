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
package com.duckpond.lx;

public class Gradient {

  private LXFloat4[] gradient;
  private int gradientCount = 256;

  public enum ColorMode {
    RGB,
    HSV
  }

  public Gradient(LXFloat4[] g, ColorMode mode) {
    gradient = new LXFloat4[gradientCount];
    for (int c = 0; c < gradientCount; c++) {
      double pos = (double)c / (double)(gradientCount - 1);
      int s0 = 0;
      while (s0 < (g.length - 1)) {
        if (g[s0].w <= pos && g[s0 + 1].w >= pos) {
          break;
        }
        s0++;
      }
      double a = (pos - g[s0].w) / (g[s0 + 1].w - g[s0].w);
      if (mode == ColorMode.RGB) {
        gradient[c] = LXFloat4.lerp(g[s0], g[s0 + 1], a);
      } else if (mode == ColorMode.HSV) {
        LXFloat4 ga = hsv2rgb(g[s0]);
        LXFloat4 gb = hsv2rgb(g[s0 + 1]);
        gradient[c] = LXFloat4.lerp(ga, gb, a);
      }
    }
  }

  private static LXFloat4 hsv2rgb(LXFloat4 hsv) {
    double h = hsv.x;
    double s = hsv.y;
    double v = hsv.z;
    double r = 0, g = 0, b = 0;
    if (s == 0.0) {
      r = g = b = v;
    } else {
      int i = (int)(h * 6.0); 
      double f = (h * 6.0) - i;
      double p = v * (1.0 - s);
      double q = v * (1.0 - s * f);
      double t = v * (1.0 - s * (1.0 - f));
      i = i % 6;
      switch (i) {
        case 0: r = v; g = t; b = p; break;
        case 1: r = q; g = v; b = p; break;
        case 2: r = p; g = v; b = t; break;
        case 3: r = p; g = q; b = v; break;
        case 4: r = t; g = p; b = v; break;
        case 5: r = v; g = p; b = q; break;
      }
    }
    return new LXFloat4(r, g, b, hsv.w);
  }

  public LXFloat4 clamp(double pos) {
    int posInt = (int)((Math.max(0.0, Math.min(1.0, pos))) * (double)(gradientCount - 1));
    return gradient[posInt];
  }

  public LXFloat4 repeat(double pos) {
    double pp = pos - Math.floor(pos);
    int posInt = (int)(pp * (double)(gradientCount - 1));
    return gradient[posInt];
  }

  public LXFloat4 reflect(double pos) {
    double pp = pos - Math.floor(pos);
    pp = (((int)Math.floor(pos) & 1) == 1) ? (1.0 - pp) : pp;
    int posInt = (int)(pp * (double)(gradientCount - 1));
    return gradient[posInt];
  }
}