package com.duckpond.pattern;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.model.LXPoint;
import com.duckpond.Gradient;
import com.duckpond.LXFloat4;

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
        
        // Global fire wind patterns - different umbrellas have different wind directions - NOW DOMINANT
        double windAngle = Math.atan2(globalPos.y, globalPos.x);
        double windStrength = Math.sin(globalPos.len() * 0.4 + slowTime * 0.6) * 0.8 + 0.5;
        double globalWind = Math.sin(windAngle + slowTime * 0.8) * windStrength * 2.0;
        
        // Major flame zones that span across umbrellas
        double flameZone = Math.sin(globalPos.x * 0.3 + slowTime * 0.4) * Math.cos(globalPos.y * 0.25 + slowTime * 0.3);
        double fireRegion = Math.sin(globalPos.x * 0.4) * Math.cos(globalPos.y * 0.35) * 0.8 + 0.6;
        
        // Local flicker heavily influenced by global wind
        double flicker1 = Math.sin(localPos.x * 1.25 + slowTime * 1.3 + globalWind * 2.5 + flameZone * 4.0) * 0.35;
        double flicker2 = Math.cos(localPos.y * 0.9 + slowTime * 0.9 + windAngle * 2.0) * 0.25;
        double flicker3 = Math.sin(localPos.len() * 0.75 + slowTime * 1.1 + globalPos.len() * 1.0) * 0.3;
        double flameMotion = (flicker1 + flicker2 + flicker3) / 0.9 + globalWind * 0.6;
        
        // Breathing embers synchronized across regions
        double emberBreath = Math.sin(slowTime * 0.4 + globalPos.x * 0.2) * 0.4 + 0.6;
        emberBreath *= fireRegion + flameZone * 0.5;
        
        double distanceFromCenter = localPos.len();
        double emberCore = Math.exp(-distanceFromCenter * 1.5) * emberBreath;
        
        // Flame rise affected by global wind
        double flameRise = Math.sin(distanceFromCenter * 2.0 + slowTime + flameMotion + globalWind) * 0.5 + 0.5;
        double flameMix = Math.pow(flameRise, 2.0) * (1.0 - distanceFromCenter * 0.3) * fireRegion;
        
        LXFloat4 emberColor = emberGradient.reflect(emberCore + flameMotion * 0.3);
        LXFloat4 flameColor = flameGradient.reflect(flameRise);
        LXFloat4 finalColor = emberColor.lerp(flameColor, flameMix * 0.7);
        
        double intensity = (0.8 + emberBreath * 0.4 + Math.abs(flameMotion) * 0.15) * fireRegion * 1.4;
        double heatGlow = Math.max(0.0, 1.0 - distanceFromCenter * 0.8);
        
        // Heat shimmer varies by global position
        double heatShimmer = Math.sin(slowTime * 4.0 + globalPos.len() * 0.5) * 0.12 * fireRegion + 0.94;
        
        // Increase contrast - make fire core much brighter while keeping edges darker
        double fireIntensity = emberCore + Math.abs(flameMotion) * 0.5;
        double contrastBoost = Math.pow(fireIntensity, 0.4);
        finalColor = finalColor.mul(1.0 + contrastBoost * 1.8);
        
        // Apply contrast-enhanced brightness
        double finalBrightness = intensity * (0.8 + heatGlow * 0.4) * heatShimmer * (0.2 + contrastBoost * 0.8);
        return finalColor.mul(finalBrightness);
    }
}