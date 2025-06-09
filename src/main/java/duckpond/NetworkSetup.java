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

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import heronarts.lx.LX;
import heronarts.lx.model.LXPoint;
import heronarts.lx.output.LXOutputGroup;
import heronarts.lx.output.LXDatagram;
import heronarts.lx.output.ArtNetDatagram;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkSetup {
  
  private static int[] getIndices(List<LXPoint> points) {
    int[] indices = new int[points.size()];
    for (int i = 0; i < points.size(); i++) {
      indices[i] = points.get(i).index;
    }
    return indices;
  }
  
  private static void addDatagram(LXOutputGroup output, int universe, int[] indices, String address) {
    try {
      int total = indices.length;
      int start = 0;
      while (total > 0) {
        int[] split = Arrays.copyOfRange(indices, start, start + Math.min(total, 170));
        ArtNetDatagram datagram = new ArtNetDatagram(output.getLX(), split, LXDatagram.ByteOrder.RGB, universe);
        try {
          datagram.setAddress(InetAddress.getByName(address));
        } catch (UnknownHostException e) {
          System.err.println("Invalid IP address: " + address + " - " + e.getMessage());
          continue;
        }
        datagram.setSequenceEnabled(true);
        output.addChild(datagram);
        total -= split.length;
        start += split.length;
        universe++;
      }
    } catch (Exception x) {
      x.printStackTrace();
    }
  }

  public static void setup(LX lx) {
    try {
      LXOutputGroup output = new LXOutputGroup(lx);
      
      // Get fixture references from the headless main class
      List<UmbrellaFixture> umbrellaFixtures = DuckPondBarHeadless.getUmbrellaFixtures();
      BarTopFixture barTopFixture = DuckPondBarHeadless.getBarTopFixture();
      
      if (barTopFixture != null) {
        // Get indices for bar top fixture points
        List<LXPoint> barPoints = new ArrayList<>();
        for (LXPoint point : lx.getModel().points) {
          // Check if this point belongs to the bar top fixture
          DuckPondFixture fixture = DuckPondBarHeadless.getPointToFixture().get(point.index);
          if (fixture == barTopFixture) {
            barPoints.add(point);
          }
        }
        
        if (!barPoints.isEmpty()) {
          // Split bar points into front and back (simplified approach)
          int halfSize = barPoints.size() / 2;
          List<LXPoint> frontPoints = barPoints.subList(0, halfSize);
          List<LXPoint> backPoints = barPoints.subList(halfSize, barPoints.size());
          
          // Port A - front
          addDatagram(output, 0, getIndices(frontPoints), barTopFixture.ipAddress.getString());
          // Port B - back  
          addDatagram(output, 1, getIndices(backPoints), barTopFixture.ipAddress.getString());
        }
      }
      
      // Set up umbrella fixtures
      for (UmbrellaFixture umbrella : umbrellaFixtures) {
        // Get points for this umbrella
        List<LXPoint> umbrellaPoints = new ArrayList<>();
        for (LXPoint point : lx.getModel().points) {
          DuckPondFixture fixture = DuckPondBarHeadless.getPointToFixture().get(point.index);
          if (fixture == umbrella) {
            umbrellaPoints.add(point);
          }
        }
        
        if (!umbrellaPoints.isEmpty()) {
          // Port A for umbrella
          addDatagram(output, 0, getIndices(umbrellaPoints), umbrella.ipAddress.getString());
        }
      }
  
      final double MAX_BRIGHTNESS = 1.0;
      output.brightness.setNormalized(MAX_BRIGHTNESS);
  
      // Add the datagram output to the LX engine
      lx.addOutput(output);
      
      System.out.println("ArtNet output configured for " + umbrellaFixtures.size() + " umbrellas");
      if (barTopFixture != null) {
        System.out.println("ArtNet output configured for bar top fixture");
      }
      
    } catch (Exception x) {
      System.err.println("Error setting up ArtNet output:");
      x.printStackTrace();
    }
  }
}