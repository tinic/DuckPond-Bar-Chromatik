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

import java.io.File;
import heronarts.lx.LX;
import heronarts.lx.model.GridModel;

/**
 * DuckPond Bar Headless Application
 * 
 * Simple headless runner that loads LXP project files
 */
public class DuckPondHeadless {

  public static void main(String[] args) {
    try {
      System.out.println("Starting DuckPond Bar Headless...");
      
      // Create a simple grid model for now (will be replaced by project file)
      LX lx = new LX(new GridModel(30, 30));

      // Load project file if specified
      if (args.length > 0) {
        File projectFile = new File(args[0]);
        if (projectFile.exists()) {
          System.out.println("Loading project file: " + args[0]);
          lx.openProject(projectFile);
        } else {
          System.err.println("Project file not found: " + args[0]);
          System.exit(1);
        }
      } else {
        System.out.println("No project file specified, using default grid model");
      }

      // Start the engine
      System.out.println("Starting LX engine...");
      lx.engine.start();
      
      System.out.println("DuckPond Bar is running!");
      System.out.println("- Model: " + lx.getModel().points.length + " LED points");
      System.out.println("- Press Ctrl+C to exit");
      
      // Keep running
      while (true) {
        Thread.sleep(1000);
      }
      
    } catch (Exception x) {
      System.err.println("Error starting DuckPond Headless: " + x.getMessage());
      x.printStackTrace();
    }
  }
}