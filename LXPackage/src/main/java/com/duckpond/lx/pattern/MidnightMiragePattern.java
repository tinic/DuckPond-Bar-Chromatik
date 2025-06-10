package com.duckpond.lx.pattern;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.model.LXPoint;
import com.duckpond.lx.Gradient;
import com.duckpond.lx.LXFloat4;

@LXCategory("DuckPond")
public class MidnightMiragePattern extends UmbrellaPattern {
    
    private final Gradient deepNightGradient;
    private final Gradient mirageShimmer;
    
    public MidnightMiragePattern(LX lx) {
        super(lx);
        
        this.deepNightGradient = new Gradient(new LXFloat4[] {
            new LXFloat4(0x0d0d26, 0.0),
            new LXFloat4(0x1a0d4d, 0.2),
            new LXFloat4(0x261a66, 0.4),
            new LXFloat4(0x4d4099, 0.6),
            new LXFloat4(0x9999cc, 0.8),
            new LXFloat4(0x332680, 1.0)
        }, Gradient.ColorMode.RGB);
        
        this.mirageShimmer = new Gradient(new LXFloat4[] {
            new LXFloat4(0x1a1a33, 0.0),
            new LXFloat4(0x664db3, 0.33),
            new LXFloat4(0xb3b3e6, 0.67),
            new LXFloat4(0x4d3399, 1.0)
        }, Gradient.ColorMode.RGB);
    }
    
    @Override
    protected LXFloat4 calculatePointColor(LXPoint point, LXFloat4 globalPos, LXFloat4 localPos, double time) {
        double slowTime = time * 0.015;
        double wave1 = Math.sin(localPos.x * 1.2 + slowTime);
        double wave2 = Math.cos(localPos.y * 0.8 + slowTime * 0.7);
        double wave3 = Math.sin(localPos.len() * 2.0 + slowTime * 0.5);
        double wavePattern = (wave1 + wave2 * 0.7 + wave3 * 0.5) / 2.2;
        double radialPulse = Math.sin(slowTime * 0.3) * 0.3;
        double distance = localPos.len() + radialPulse;
        double shimmer = Math.sin(distance * 3.0 + slowTime * 1.2) * 0.4 + 0.6;
        LXFloat4 baseColor = deepNightGradient.reflect(wavePattern * 0.5 + 0.5);
        LXFloat4 shimmerColor = mirageShimmer.reflect(shimmer);
        double mixRatio = Math.pow(1.0 - Math.min(distance, 1.0), 2.0);
        LXFloat4 finalColor = baseColor.lerp(shimmerColor, mixRatio * 0.6);
        double brightness = 0.7 + Math.sin(slowTime * 0.4) * 0.2;
        return finalColor.mul(brightness).clamp().gamma();
    }
}