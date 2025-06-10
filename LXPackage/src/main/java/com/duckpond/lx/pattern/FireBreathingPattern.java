package com.duckpond.lx.pattern;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.model.LXPoint;
import com.duckpond.lx.Gradient;
import com.duckpond.lx.LXFloat4;

@LXCategory("DuckPond")
public class FireBreathingPattern extends UmbrellaPattern {
    
    private final Gradient emberGradient;
    private final Gradient flameGradient;
    
    public FireBreathingPattern(LX lx) {
        super(lx);
        
        this.emberGradient = new Gradient(new LXFloat4[] {
            new LXFloat4(0x1a0500, 0.0),
            new LXFloat4(0x4d1a00, 0.2),
            new LXFloat4(0x99330d, 0.4),
            new LXFloat4(0xcc661a, 0.6),
            new LXFloat4(0xff9933, 0.8),
            new LXFloat4(0xe64d00, 1.0)
        }, Gradient.ColorMode.RGB);
        
        this.flameGradient = new Gradient(new LXFloat4[] {
            new LXFloat4(0x661a00, 0.0),
            new LXFloat4(0xcc4d00, 0.25),
            new LXFloat4(0xffb31a, 0.5),
            new LXFloat4(0xffe666, 0.75),
            new LXFloat4(0xe66600, 1.0)
        }, Gradient.ColorMode.RGB);
    }
    
    @Override
    protected LXFloat4 calculatePointColor(LXPoint point, LXFloat4 globalPos, LXFloat4 localPos, double time) {
        double slowTime = time * 0.4;
        double flicker1 = Math.sin(localPos.x * 2.5 + slowTime * 1.3) * 0.7;
        double flicker2 = Math.cos(localPos.y * 1.8 + slowTime * 0.9) * 0.5;
        double flicker3 = Math.sin(localPos.len() * 1.5 + slowTime * 1.1) * 0.6;
        double flameMotion = (flicker1 + flicker2 + flicker3) / 2.8;
        double emberBreath = Math.sin(slowTime * 0.4) * 0.4 + 0.6;
        double distanceFromCenter = localPos.len();
        double emberCore = Math.exp(-distanceFromCenter * 1.5) * emberBreath;
        double flameRise = Math.sin(distanceFromCenter * 2.0 + slowTime + flameMotion) * 0.5 + 0.5;
        double flameMix = Math.pow(flameRise, 2.0) * (1.0 - distanceFromCenter * 0.3);
        LXFloat4 emberColor = emberGradient.reflect(emberCore + flameMotion * 0.3);
        LXFloat4 flameColor = flameGradient.reflect(flameRise);
        LXFloat4 finalColor = emberColor.lerp(flameColor, flameMix * 0.7);
        double intensity = 0.6 + emberBreath * 0.3 + Math.abs(flameMotion) * 0.1;
        double heatGlow = Math.max(0.0, 1.0 - distanceFromCenter * 0.8);
        return finalColor.mul(intensity * (0.7 + heatGlow * 0.3)).clamp().gamma();
    }
}