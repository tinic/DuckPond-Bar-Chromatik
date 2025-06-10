package com.duckpond.lx.pattern;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.model.LXPoint;
import com.duckpond.lx.Gradient;
import com.duckpond.lx.LXFloat4;

@LXCategory("DuckPond")
public class SunsetPlayaPattern extends UmbrellaPattern {
    
    private final Gradient desertSunsetGradient;
    private final Gradient dustStormGradient;
    
    public SunsetPlayaPattern(LX lx) {
        super(lx);
        
        this.desertSunsetGradient = new Gradient(new LXFloat4[] {
            new LXFloat4(0x331a4d, 0.0),
            new LXFloat4(0x993366, 0.2),
            new LXFloat4(0xe66633, 0.4),
            new LXFloat4(0xffb34d, 0.6),
            new LXFloat4(0xcc4d1a, 0.8),
            new LXFloat4(0x662633, 1.0)
        }, Gradient.ColorMode.RGB);
        
        this.dustStormGradient = new Gradient(new LXFloat4[] {
            new LXFloat4(0x4d4033, 0.0),
            new LXFloat4(0xb3804d, 0.25),
            new LXFloat4(0xe6b366, 0.5),
            new LXFloat4(0x996640, 0.75),
            new LXFloat4(0xcc9959, 1.0)
        }, Gradient.ColorMode.RGB);
    }
    
    @Override
    protected LXFloat4 calculatePointColor(LXPoint point, LXFloat4 globalPos, LXFloat4 localPos, double time) {
        double playaTime = time * 0.25;
        
        // Global sun position and atmospheric layers
        double sunAngle = Math.atan2(globalPos.y, globalPos.x);
        double sunDistance = globalPos.len() * 0.05;
        double sunHeight = Math.sin(playaTime * 0.1 + sunDistance) * 0.3 + 0.4;
        
        // Different atmospheric regions across the playa
        double atmosphericRegion = Math.sin(globalPos.x * 0.04) * Math.cos(globalPos.y * 0.03) * 0.3 + 0.8;
        
        // Horizon varies by global position
        double globalHorizon = Math.sin(sunAngle + playaTime * 0.05) * 0.1;
        double horizonLayer = localPos.y + globalHorizon + Math.sin(playaTime * 0.3 + globalPos.x * 0.02) * 0.2;
        
        // Heat waves intensified by global desert conditions
        double desertHeat = atmosphericRegion * (Math.sin(globalPos.len() * 0.06 + playaTime * 0.1) * 0.2 + 0.9);
        double heatWave1 = Math.sin(localPos.x * 3.0 + playaTime * 0.8 + sunAngle * 0.5) * 0.3;
        double heatWave2 = Math.cos(localPos.y * 2.5 + playaTime * 0.6 + globalPos.x * 0.1) * 0.2;
        double heatWave3 = Math.sin(localPos.len() * 1.5 + playaTime * 0.4 + sunDistance * 5.0) * 0.25;
        double heatDistortion = (heatWave1 + heatWave2 + heatWave3) / 2.75 * desertHeat;
        
        // Dust storms vary by wind patterns across the playa
        double windDirection = sunAngle + Math.sin(globalPos.len() * 0.03 + playaTime * 0.08) * 0.5;
        double dustSwirl1 = Math.sin(localPos.x * 1.2 + playaTime * 0.5 + windDirection) * 0.6;
        double dustSwirl2 = Math.cos(localPos.y * 0.8 + playaTime * 0.7 + globalPos.y * 0.05) * 0.4;
        double dustPattern = (dustSwirl1 + dustSwirl2) / 2.0;
        
        // Sun's influence varies across the installation
        double sunInfluence = Math.exp(-Math.abs(sunAngle - Math.atan2(localPos.y, localPos.x)) * 2.0) * sunHeight;
        double sunsetPosition = horizonLayer + heatDistortion + sunInfluence * 0.3;
        
        // Sunset phase synchronized but with regional variations
        double sunsetPhase = Math.sin(playaTime * 0.15 + globalPos.x * 0.01) * 0.5 + 0.5;
        sunsetPhase *= atmosphericRegion;
        
        LXFloat4 sunsetColor = desertSunsetGradient.reflect(sunsetPosition * 0.5 + 0.5);
        LXFloat4 dustColor = dustStormGradient.reflect(dustPattern * 0.5 + 0.5);
        
        double dustMix = Math.abs(dustPattern) * 0.4 + sunsetPhase * 0.3;
        LXFloat4 finalColor = sunsetColor.lerp(dustColor, dustMix);
        
        // Golden hour glow varies by proximity to "sun"
        double goldenGlow = (Math.sin(playaTime * 0.2 + globalPos.y * 0.01) * 0.2 + 0.8) * (0.7 + sunInfluence * 0.3);
        
        double distance = localPos.len();
        double atmosphere = Math.exp(-distance * 0.5) * 0.5 + 0.5;
        atmosphere *= atmosphericRegion;
        
        double intensity = goldenGlow * atmosphere * (0.7 + Math.abs(heatDistortion) * 0.2);
        return finalColor.mul(intensity).clamp().gamma();
    }
}
