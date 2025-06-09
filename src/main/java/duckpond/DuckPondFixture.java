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
package duckpond;

import heronarts.lx.LX;
import heronarts.lx.structure.LXFixture;
import heronarts.lx.parameter.StringParameter;
import heronarts.lx.transform.LXMatrix;
import heronarts.lx.model.LXPoint;
import java.net.InetAddress;
import java.util.List;

public abstract class DuckPondFixture extends LXFixture {
  
  public final StringParameter ipAddress = 
    new StringParameter("IP", "")
    .setDescription("IP address for network output");

  private LXFloat4 min;
  private LXFloat4 max;
  private LXFloat4 center;
  private LXFloat4 size;
  private LXFloat4 factor;
  private boolean boundsValid = false;
  private boolean centerValid = false;
  private boolean sizeValid = false;
  private boolean factorValid = false;

  protected DuckPondFixture(LX lx, String label, String ip) {
    super(lx, label);
    this.ipAddress.setValue(ip);
    addOutputParameter("ipAddress", this.ipAddress);
  }

  public abstract LXFloat4 calc(DuckPondBarPattern.Effect effect, int index, double time, LXFloat4 globPos);
  
  public void calcBounds() {
    if (boundsValid) {
        return;
    }

    min = new LXFloat4(+1000.0, +1000.0, +1000.0, 0.0);
    max = new LXFloat4(-1000.0, -1000.0, -1000.0, 0.0);
    
    for (LXPoint point : this.points) {
        min = min.min(new LXFloat4(point));
        max = max.max(new LXFloat4(point));
    }
    
    boundsValid = true;
  }

  public LXFloat4 center() {
    if (centerValid) {
        return this.center;
    }

    calcBounds();
    this.center = new LXFloat4(
      (max.x + min.x) * 0.5, 
      (max.y + min.y) * 0.5, 
      (max.z + min.z) * 0.5);
      
    centerValid = true;

    return this.center;
  }
  
  public LXFloat4 getSize() {
    if (sizeValid) {
        return this.size;
    }

    calcBounds();
    this.size = new LXFloat4(max.x - min.x, max.y - min.y, max.z - min.z);
    sizeValid = true;
    return this.size;
  }
  
  public LXFloat4 normalize(LXFloat4 pos) {
    if (factorValid) {
      return new LXFloat4(pos.x * this.factor.x,  pos.y * this.factor.y, pos.z * this.factor.z);
    }
    LXFloat4 size = getSize();
    double xf = 1.0;
    double yf = 1.0;
    double zf = 1.0;
    if (size.x != 0) xf = 2.0 / size.x;
    if (size.y != 0) yf = 2.0 / size.y;
    if (size.z != 0) zf = 2.0 / size.z;
    this.factor = new LXFloat4(xf, yf, zf);
    factorValid = true;
    return new LXFloat4(pos.x * this.factor.x,  pos.y * this.factor.y, pos.z * this.factor.z);
  }
  
  public LXFloat4 toLocal(LXFloat4 pos) {
      LXFloat4 center = center();
      return normalize(new LXFloat4(
        pos.x - center.x,
        pos.y - center.y,
        pos.z - center.z));
  }

  @Override
  protected void buildOutputs() {
    // TODO: Implement output definitions properly
    // For now, we'll handle outputs at the main application level
  }

  protected void invalidateBounds() {
    boundsValid = false;
    centerValid = false;
    sizeValid = false;
    factorValid = false;
  }

  @Override
  protected void computePointGeometry(LXMatrix transform, List<LXPoint> points) {
    // Subclasses will call invalidateBounds() after updating point positions
    // to ensure the bounds are recalculated
  }
}