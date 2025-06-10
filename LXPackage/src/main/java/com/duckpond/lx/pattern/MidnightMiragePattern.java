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
        double slowTime = time * 0.3;
        
        // Global desert heat patterns - mirages appear at different locations
        double desertAngle = Math.atan2(globalPos.y, globalPos.x);
        double heatDistortion = Math.sin(globalPos.len() * 0.05 + slowTime * 0.1) * 0.4;
        double globalMirage = Math.sin(desertAngle + slowTime * 0.2) * Math.cos(globalPos.x * 0.03) * 0.3;
        
        // Atmospheric layers at different global heights
        double atmosphericLayer = Math.sin(globalPos.y * 0.04 + slowTime * 0.15) * 0.2 + 0.8;
        
        // Local waves influenced by global heat patterns
        double wave1 = Math.sin(localPos.x * 1.2 + slowTime + globalMirage);
        double wave2 = Math.cos(localPos.y * 0.8 + slowTime * 0.7 + heatDistortion);
        double wave3 = Math.sin(localPos.len() * 2.0 + slowTime * 0.5 + globalPos.len() * 0.1);
        double wavePattern = (wave1 + wave2 * 0.7 + wave3 * 0.5) / 2.2;
        
        // Radial pulse varies across the desert
        double radialPulse = Math.sin(slowTime * 0.3 + globalPos.x * 0.02) * 0.3;
        double distance = localPos.len() + radialPulse;
        
        // Shimmer intensity varies by global temperature regions
        double temperatureRegion = Math.sin(globalPos.x * 0.08) * Math.cos(globalPos.y * 0.06) * 0.3 + 0.7;
        double shimmer = Math.sin(distance * 3.0 + slowTime * 1.2 + globalMirage * 2.0) * 0.4 + 0.6;
        shimmer *= temperatureRegion;
        
        // Heat waves create different mirage qualities
        double heatWave = Math.sin(globalPos.len() * 0.1 + slowTime * 0.25) * atmosphericLayer;
        
        LXFloat4 baseColor = deepNightGradient.reflect(wavePattern * 0.5 + 0.5);
        LXFloat4 shimmerColor = mirageShimmer.reflect(shimmer);
        
        double mixRatio = Math.pow(1.0 - Math.min(distance, 1.0), 2.0) * temperatureRegion;
        LXFloat4 finalColor = baseColor.lerp(shimmerColor, mixRatio * 0.6);
        
        // Add heat distortion effect
        if (Math.abs(heatWave) > 0.2) {
            LXFloat4 heatColor = new LXFloat4(0.6, 0.5, 0.9, 1.0);
            finalColor = finalColor.lerp(heatColor, Math.abs(heatWave - 0.2) * 0.3);
        }
        
        double brightness = (1.4 + Math.sin(slowTime * 0.4 + globalPos.y * 0.02) * 0.4) * atmosphericLayer * 2.0;
        
        // Increase contrast by enhancing the color values
        finalColor = finalColor.mul(1.3);
        return finalColor.mul(brightness).clamp().gamma();
    }
}