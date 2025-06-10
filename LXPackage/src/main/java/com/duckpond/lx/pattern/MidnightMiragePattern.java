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
        
        // Global desert heat patterns - mirages appear at different locations - NOW DOMINANT
        double desertAngle = Math.atan2(globalPos.y, globalPos.x);
        double heatDistortion = Math.sin(globalPos.len() * 0.2 + slowTime * 0.4) * 1.2;
        double globalMirage = Math.sin(desertAngle + slowTime * 0.6) * Math.cos(globalPos.x * 0.15) * 1.0;
        
        // Major heat zones that span across umbrellas
        double heatZone = Math.sin(globalPos.x * 0.25 + slowTime * 0.3) * Math.cos(globalPos.y * 0.2 + slowTime * 0.25);
        double atmosphericLayer = Math.sin(globalPos.y * 0.2 + slowTime * 0.4) * 0.6 + 0.6;
        
        // Local waves heavily influenced by global heat patterns
        double wave1 = Math.sin(localPos.x * 0.6 + slowTime + globalMirage * 3.0 + heatZone * 4.0) * 0.5;
        double wave2 = Math.cos(localPos.y * 0.4 + slowTime * 0.7 + heatDistortion * 2.0) * 0.35;
        double wave3 = Math.sin(localPos.len() * 1.0 + slowTime * 0.5 + globalPos.len() * 0.4) * 0.25;
        double wavePattern = (wave1 + wave2 + wave3) / 1.1 + globalMirage * 0.8;
        
        // Radial pulse heavily varies across the desert
        double radialPulse = Math.sin(slowTime * 0.3 + globalPos.x * 0.1) * 0.6;
        double distance = localPos.len() + radialPulse;
        
        // Shimmer intensity heavily varies by global temperature regions
        double temperatureRegion = Math.sin(globalPos.x * 0.4) * Math.cos(globalPos.y * 0.3) * 0.7 + 0.5;
        double shimmer = Math.sin(distance * 1.5 + slowTime * 1.2 + globalMirage * 5.0) * 0.2 + 0.3;
        shimmer = shimmer * temperatureRegion + heatZone * 0.8;
        
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
        
        double brightness = (1.0 + Math.sin(slowTime * 0.4 + globalPos.y * 0.02) * 0.3) * atmosphericLayer * 1.4;
        
        // Increase contrast - make mirages much brighter while keeping night areas very dark
        double mirageIntensity = Math.max(Math.abs(shimmer - 0.6), Math.abs(heatWave)) * temperatureRegion;
        double contrastBoost = Math.pow(mirageIntensity, 0.4);
        finalColor = finalColor.mul(0.7 + contrastBoost * 2.0);
        
        // Apply contrast-enhanced brightness
        double finalBrightness = brightness * (0.1 + contrastBoost * 0.9);
        return finalColor.mul(finalBrightness).clamp().gamma();
    }
}