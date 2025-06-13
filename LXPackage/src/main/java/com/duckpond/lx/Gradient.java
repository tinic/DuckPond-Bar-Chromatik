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
    HSV,
    OKLAB
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
        LXFloat4 labA = srgb2oklab(g[s0]);
        LXFloat4 labB = srgb2oklab(g[s0 + 1]);
        gradient[c] = LXFloat4.lerp(labA, labB, a);
      } else if (mode == ColorMode.HSV) {
        LXFloat4 ga = hsv2rgb(g[s0]);
        LXFloat4 gb = hsv2rgb(g[s0 + 1]);
        LXFloat4 labA = srgb2oklab(ga);
        LXFloat4 labB = srgb2oklab(gb);
        gradient[c] = LXFloat4.lerp(labA, labB, a);
      } else if (mode == ColorMode.OKLAB) {
        LXFloat4 labA = srgb2oklab(g[s0]);
        LXFloat4 labB = srgb2oklab(g[s0 + 1]);
        gradient[c] = LXFloat4.lerp(labA, labB, a);
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

  private static double linearize_srgb(double x) {
    if (x <= 0.04045) {
      return x / 12.92;
    } else {
      return Math.pow((x + 0.055) / 1.055, 2.4);
    }
  }

  private static double delinearize_srgb(double x) {
    if (x <= 0.0031308) {
      return x * 12.92;
    } else {
      return 1.055 * Math.pow(x, 1.0 / 2.4) - 0.055;
    }
  }

  private static LXFloat4 srgb2oklab(LXFloat4 srgb) {
    double r = linearize_srgb(srgb.x);
    double g = linearize_srgb(srgb.y);
    double b = linearize_srgb(srgb.z);

    double l = 0.4122214708 * r + 0.5363325363 * g + 0.0514459929 * b;
    double m = 0.2119034982 * r + 0.6806995451 * g + 0.1073969566 * b;
    double s = 0.0883024619 * r + 0.2817188376 * g + 0.6299787005 * b;

    double l_ = Math.cbrt(l);
    double m_ = Math.cbrt(m);
    double s_ = Math.cbrt(s);

    double L = 0.2104542553 * l_ + 0.7936177850 * m_ - 0.0040720468 * s_;
    double a = 1.9779984951 * l_ - 2.4285922050 * m_ + 0.4505937099 * s_;
    double b_ = 0.0259040371 * l_ + 0.7827717662 * m_ - 0.8086757660 * s_;

    return new LXFloat4(L, a, b_, srgb.w);
  }

  private static LXFloat4 oklab2srgb(LXFloat4 oklab) {
    double L = oklab.x;
    double a = oklab.y;
    double b = oklab.z;

    double l_ = L + 0.3963377774 * a + 0.2158037573 * b;
    double m_ = L - 0.1055613458 * a - 0.0638541728 * b;
    double s_ = L - 0.0894841775 * a - 1.2914855480 * b;

    double l = l_ * l_ * l_;
    double m = m_ * m_ * m_;
    double s = s_ * s_ * s_;

    double r = +4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s;
    double g = -1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s;
    double b_ = -0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s;

    r = delinearize_srgb(r);
    g = delinearize_srgb(g);
    b_ = delinearize_srgb(b_);

    r = Math.max(0.0, Math.min(1.0, r));
    g = Math.max(0.0, Math.min(1.0, g));
    b_ = Math.max(0.0, Math.min(1.0, b_));

    return new LXFloat4(r, g, b_, oklab.w);
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
  
  public static LXFloat4 toOklab(LXFloat4 srgb) {
    return srgb2oklab(srgb);
  }
  
  public static LXFloat4 toSrgb(LXFloat4 oklab) {
    return oklab2srgb(oklab);
  }
  
  public static LXFloat4 rgbToOklab(double r, double g, double b) {
    return srgb2oklab(new LXFloat4(r, g, b, 1.0));
  }
  
  public static LXFloat4 rgbToOklab(double r, double g, double b, double a) {
    return srgb2oklab(new LXFloat4(r, g, b, a));
  }
}