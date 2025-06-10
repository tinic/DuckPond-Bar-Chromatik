package com.duckpond.lx.pattern;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.model.LXPoint;
import com.duckpond.lx.Gradient;
import com.duckpond.lx.LXFloat4;

@LXCategory("DuckPond")
public class DeepOceanPattern extends UmbrellaPattern {
    
    private final Gradient oceanDepthGradient;
    private final Gradient bioluminescentGradient;
    
    public DeepOceanPattern(LX lx) {
        super(lx);
        
        this.oceanDepthGradient = new Gradient(new LXFloat4[] {
            new LXFloat4(0x000d26, 0.0),
            new LXFloat4(0x00264d, 0.2),
            new LXFloat4(0x0d4066, 0.4),
            new LXFloat4(0x1a6680, 0.6),
            new LXFloat4(0x338099, 0.8),
            new LXFloat4(0x1a4d73, 1.0)
        }, Gradient.ColorMode.RGB);
        
        this.bioluminescentGradient = new Gradient(new LXFloat4[] {
            new LXFloat4(0x001a33, 0.0),
            new LXFloat4(0x1a99cc, 0.2),
            new LXFloat4(0x33cc99, 0.4),
            new LXFloat4(0x66e6b3, 0.6),
            new LXFloat4(0x00b3e6, 0.8),
            new LXFloat4(0x4dcc80, 1.0)
        }, Gradient.ColorMode.RGB);
    }
    
    @Override
    protected LXFloat4 calculatePointColor(LXPoint point, LXFloat4 globalPos, LXFloat4 localPos, double time) {
        double oceanTime = time * 0.007;
        double current1 = Math.sin(localPos.x * 1.8 + oceanTime * 0.6) * 0.7;
        double current2 = Math.cos(localPos.y * 1.2 + oceanTime * 0.8) * 0.6;
        double current3 = Math.sin(localPos.len() * 2.5 + oceanTime * 0.4) * 0.5;
        double oceanFlow = (current1 + current2 + current3) / 2.8;
        double bio1 = Math.sin(localPos.x * 5.0 + oceanTime * 1.5) * 0.4;
        double bio2 = Math.cos(localPos.y * 6.0 + oceanTime * 1.8) * 0.3;
        double bio3 = Math.sin((localPos.x + localPos.y) * 4.0 + oceanTime * 1.2) * 0.35;
        double bioluminescence = (bio1 + bio2 + bio3) / 1.05;
        double depth = localPos.len();
        double depthPressure = Math.exp(-depth * 0.8);
        double thermalVent = Math.sin(oceanTime * 0.3 + depth * 2.0) * 0.2;
        thermalVent = Math.max(0.0, thermalVent - 0.15);
        double oceanBreath = Math.sin(oceanTime * 0.2) * 0.25 + 0.75;
        LXFloat4 oceanColor = oceanDepthGradient.reflect(oceanFlow * 0.5 + 0.5);
        LXFloat4 bioColor = bioluminescentGradient.reflect(bioluminescence * 0.5 + 0.5);
        double bioActivity = Math.abs(bioluminescence) * depthPressure;
        LXFloat4 finalColor = oceanColor.lerp(bioColor, bioActivity * 0.7);
        if (thermalVent > 0.0) {
            LXFloat4 thermalGlow = new LXFloat4(0.3, 0.6, 0.8, 1.0);
            finalColor = finalColor.lerp(thermalGlow, thermalVent * 0.4);
        }
        double intensity = oceanBreath * depthPressure * (0.6 + bioActivity * 0.3);
        double refraction = Math.sin(oceanTime * 2.0 + oceanFlow) * 0.05 + 0.95;
        return finalColor.mul(intensity * refraction).clamp().gamma();
    }
}