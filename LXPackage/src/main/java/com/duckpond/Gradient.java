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
package com.duckpond;

public class Gradient {

  private Float4[] gradient;
  private int gradientCount = 256;

  public enum ColorMode {
    RGB,
    HSV
  }

  public Gradient(Float4[] g, ColorMode mode) {
    gradient = new Float4[gradientCount];
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
        Float4 labA = ColorSpace.srgb2oklab(g[s0]);
        Float4 labB = ColorSpace.srgb2oklab(g[s0 + 1]);
        gradient[c] = Float4.lerp(labA, labB, a);
      } else if (mode == ColorMode.HSV) {
        Float4 ga = ColorSpace.hsv2rgb(g[s0]);
        Float4 gb = ColorSpace.hsv2rgb(g[s0 + 1]);
        Float4 labA = ColorSpace.srgb2oklab(ga);
        Float4 labB = ColorSpace.srgb2oklab(gb);
        gradient[c] = Float4.lerp(labA, labB, a);
      }
    }
  }


  public Float4 clamp(double pos) {
    int posInt = (int)((Math.max(0.0, Math.min(1.0, pos))) * (double)(gradientCount - 1));
    return gradient[posInt];
  }

  public Float4 repeat(double pos) {
    double pp = pos - Math.floor(pos);
    int posInt = (int)(pp * (double)(gradientCount - 1));
    return gradient[posInt];
  }

  public Float4 reflect(double pos) {
    double pp = pos - Math.floor(pos);
    pp = (((int)Math.floor(pos) & 1) == 1) ? (1.0 - pp) : pp;
    int posInt = (int)(pp * (double)(gradientCount - 1));
    return gradient[posInt];
  }
  
  public static Float4 toOklab(Float4 srgb) {
    return ColorSpace.toOklab(srgb);
  }
  
  public static Float4 toSrgb(Float4 oklab) {
    return ColorSpace.toSrgb(oklab);
  }
  
  public static Float4 rgbToOklab(double r, double g, double b) {
    return ColorSpace.rgbToOklab(r, g, b);
  }
  
  public static Float4 rgbToOklab(double r, double g, double b, double a) {
    return ColorSpace.rgbToOklab(r, g, b, a);
  }
}