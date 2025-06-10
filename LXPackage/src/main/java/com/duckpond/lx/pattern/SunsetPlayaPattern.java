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
        
        // Global sun position and atmospheric layers - NOW DOMINANT
        double sunAngle = Math.atan2(globalPos.y, globalPos.x);
        double sunDistance = globalPos.len() * 0.2;
        double sunHeight = Math.sin(playaTime * 0.3 + sunDistance) * 0.6 + 0.5;
        
        // Major atmospheric zones that span across umbrellas
        double atmosphericZone = Math.sin(globalPos.x * 0.2 + playaTime * 0.2) * Math.cos(globalPos.y * 0.15 + playaTime * 0.15);
        double atmosphericRegion = Math.sin(globalPos.x * 0.2) * Math.cos(globalPos.y * 0.15) * 0.7 + 0.5;
        
        // Horizon heavily varies by global position
        double globalHorizon = Math.sin(sunAngle + playaTime * 0.2) * 0.4;
        double horizonLayer = localPos.y * 0.5 + globalHorizon + Math.sin(playaTime * 0.3 + globalPos.x * 0.1) * 0.6;
        
        // Heat waves heavily intensified by global desert conditions
        double desertHeat = atmosphericRegion * (Math.sin(globalPos.len() * 0.25 + playaTime * 0.3) * 0.6 + 0.7);
        double heatWave1 = Math.sin(localPos.x * 1.5 + playaTime * 0.8 + sunAngle * 2.0 + atmosphericZone * 3.0) * 0.15;
        double heatWave2 = Math.cos(localPos.y * 1.25 + playaTime * 0.6 + globalPos.x * 0.5) * 0.1;
        double heatWave3 = Math.sin(localPos.len() * 0.75 + playaTime * 0.4 + sunDistance * 15.0) * 0.125;
        double heatDistortion = (heatWave1 + heatWave2 + heatWave3) / 0.375 * desertHeat + atmosphericZone * 0.8;
        
        // Dust storms heavily vary by wind patterns across the playa
        double windDirection = sunAngle + Math.sin(globalPos.len() * 0.15 + playaTime * 0.25) * 1.5;
        double dustSwirl1 = Math.sin(localPos.x * 0.6 + playaTime * 0.5 + windDirection * 2.0) * 0.3;
        double dustSwirl2 = Math.cos(localPos.y * 0.4 + playaTime * 0.7 + globalPos.y * 0.2) * 0.2;
        double dustPattern = (dustSwirl1 + dustSwirl2) / 0.5 + atmosphericZone * 0.6;
        
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
        
        double intensity = goldenGlow * atmosphere * (1.0 + Math.abs(heatDistortion) * 0.3) * 1.4;
        
        // Increase contrast - make sunset glow much brighter while keeping distant areas darker
        double sunsetIntensity = Math.max(sunInfluence, Math.abs(heatDistortion)) * sunsetPhase;
        double contrastBoost = Math.pow(sunsetIntensity, 0.4);
        finalColor = finalColor.mul(0.8 + contrastBoost * 1.7);
        
        // Apply contrast-enhanced brightness
        double finalBrightness = intensity * (0.2 + contrastBoost * 0.8);
        return finalColor.mul(finalBrightness).clamp().gamma();
    }
}
