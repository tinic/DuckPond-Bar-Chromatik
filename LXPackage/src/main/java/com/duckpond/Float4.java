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

import heronarts.lx.model.LXPoint;

public class Float4 {
  public double x;
  public double y;
  public double z;
  public double w;

  public Float4(double a) {
    this.x = a;
    this.y = a;
    this.z = a;
    this.w = a;
  }

  public Float4(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = 0.0;
  }

  public Float4(double x, double y, double z, double w) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }

  public Float4(int c, double w) {
    int r = (c & 0x00ff0000) >> 16;
    int g = (c & 0x0000ff00) >> 8;
    int b = (c & 0x000000ff) >> 0;
    this.x = (double)r * (1.0 / 255.0);
    this.y = (double)g * (1.0 / 255.0);
    this.z = (double)b * (1.0 / 255.0);
    this.w = w;
  }

  public Float4(LXPoint p) {
    this.x = p.x;
    this.y = p.y;
    this.z = p.z;
    this.w = 0.0;
  }

  public Float4 rotate2d(double phi) {
    double cos = Math.cos(phi);
    double sin = Math.sin(phi);
    return new Float4( cos * x - sin * y,
                sin * x + cos * y,
                z, w);
  }

  public Float4 add(Float4 o) {
    return new Float4( x + o.x, y + o.y, z + o.z, w + o.w);
  }

  public Float4 sub(Float4 o) {
    return new Float4( x - o.x, y - o.y, z - o.z, w - o.w);
  }

  public Float4 mul(double o) {
    return new Float4( x * o, y * o, z * o, w * o);
  }

  public Float4 mul(Float4 o) {
    return new Float4( x * o.x, y * o.y, z * o.z, w * o.w);
  }

  public Float4 clamp() {
    return new Float4( 
      Math.max(0.0, Math.min(1.0, x)),
      Math.max(0.0, Math.min(1.0, y)),
      Math.max(0.0, Math.min(1.0, z)),
      Math.max(0.0, Math.min(1.0, w)));
  }

  public Float4 gamma() {
    return new Float4( Math.sqrt(x), Math.sqrt(y), Math.sqrt(z), w);
  }

  public double len() {
    return Math.sqrt(len2());
  }

  public double len2() {
    return x * x + y * y + z * z;
  }

  public Float4 max(Float4 o) {
    return new Float4( 
      Math.max(x, o.x),
      Math.max(y, o.y),
      Math.max(z, o.z),
      Math.max(w, o.w));
  }

  public Float4 min(Float4 o) {
    return new Float4( 
      Math.min(x, o.x),
      Math.min(y, o.y),
      Math.min(z, o.z),
      Math.min(w, o.w));
  }

  public static Float4 lerp(Float4 a, Float4 b, double t) {
    return new Float4(
      a.x + (b.x - a.x) * t,
      a.y + (b.y - a.y) * t,
      a.z + (b.z - a.z) * t,
      a.w + (b.w - a.w) * t);
  }

  public Float4 lerp(Float4 other, double t) {
    return new Float4(
      this.x + (other.x - this.x) * t,
      this.y + (other.y - this.y) * t,
      this.z + (other.z - this.z) * t,
      this.w + (other.w - this.w) * t);
  }
}