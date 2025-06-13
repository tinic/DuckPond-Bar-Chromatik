package com.duckpond.lx.pattern;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.model.LXPoint;
import com.duckpond.lx.Gradient;
import com.duckpond.lx.LXFloat4;

@LXCategory("DuckPond")
public class LavaDreamsPattern extends UmbrellaPattern {
    
    private final Gradient moltenGradient;
    private final Gradient crustGradient;
    
    public LavaDreamsPattern(LX lx) {
        super(lx);
        
        this.moltenGradient = new Gradient(new LXFloat4[] {
            new LXFloat4(0x260500, 0.0),
            new LXFloat4(0x661a00, 0.2),
            new LXFloat4(0xcc3300, 0.4),
            new LXFloat4(0xff801a, 0.6),
            new LXFloat4(0xffcc4d, 0.8),
            new LXFloat4(0xe64d0d, 1.0)
        }, Gradient.ColorMode.OKLAB);
        
        this.crustGradient = new Gradient(new LXFloat4[] {
            new LXFloat4(0x140d05, 0.0),
            new LXFloat4(0x331a0d, 0.25),
            new LXFloat4(0x66260d, 0.5),
            new LXFloat4(0x99401a, 0.75),
            new LXFloat4(0x4d1f08, 1.0)
        }, Gradient.ColorMode.OKLAB);
    }
    
    @Override
    protected LXFloat4 calculatePointColor(LXPoint point, LXFloat4 globalPos, LXFloat4 localPos, double time) {
        double geologicalTime = time * 0.2;
        
        // Global volcanic activity - different regions have different activity levels - NOW DOMINANT
        double volcanicRegion = Math.sin(globalPos.x * 0.3) * Math.cos(globalPos.y * 0.25) * 0.7 + 0.5;
        double globalFlow = Math.sin(globalPos.len() * 0.3 + geologicalTime * 0.3) * volcanicRegion * 1.5;
        
        // Major volcanic features that span across umbrellas
        double volcanicZone = Math.sin(globalPos.x * 0.15 + geologicalTime * 0.2) * Math.cos(globalPos.y * 0.12 + geologicalTime * 0.15);
        double magmaChamber = Math.exp(-globalPos.len() * 0.2) * 1.2;
        
        // Lava flows heavily influenced by global topography
        double slope = Math.atan2(globalPos.y, globalPos.x) * 1.2;
        double flow1 = Math.sin(localPos.x * 0.75 + geologicalTime * 0.7 + slope * 2.0 + volcanicZone * 3.0) * 0.4;
        double flow2 = Math.cos(localPos.y * 0.9 + geologicalTime * 0.5 + globalFlow * 2.0) * 0.35;
        double flow3 = Math.sin(localPos.len() * 1.1 + geologicalTime * 0.6 + globalPos.len() * 0.5) * 0.3;
        double lavaFlow = (flow1 + flow2 + flow3) / 1.05 + globalFlow * 0.8 + magmaChamber * 0.6;
        
        // Bubble activity heavily varies across the volcanic field
        double bubbleActivity = volcanicRegion * (Math.sin(globalPos.x * 0.5) * 0.6 + 0.6);
        double bubble1 = Math.sin(localPos.x * 2.0 + geologicalTime * 1.5 + globalPos.y * 1.0) * 0.2;
        double bubble2 = Math.cos(localPos.y * 2.5 + geologicalTime * 1.8 + globalPos.x * 0.8) * 0.15;
        double bubble3 = Math.sin((localPos.x + localPos.y) * 1.75 + geologicalTime * 1.2 + globalFlow * 5.0) * 0.175;
        double bubbling = (bubble1 + bubble2 + bubble3) / 0.525 * bubbleActivity + volcanicZone * 0.7;
        
        // Crust formation varies by global elevation
        double elevation = globalPos.len() * 0.1;
        double crust1 = Math.sin(localPos.len() * 2.5 + geologicalTime * 0.3 + elevation * 3.0) * 0.5;
        double crust2 = Math.cos(localPos.x * 1.2 - geologicalTime * 0.4 + globalPos.y * 0.1) * 0.4;
        double crustPattern = (crust1 + crust2) / 1.9;
        
        double distance = localPos.len();
        double heatIntensity = Math.exp(-distance * 1.2);
        
        // Volcanic pulse synchronized across regions but with different intensities
        double volcanicPulse = Math.sin(geologicalTime * 0.3 + globalPos.x * 0.02) * 0.4 + 0.6;
        volcanicPulse *= volcanicRegion;
        
        double coreTemp = heatIntensity * volcanicPulse;
        double surfaceTemp = (1.0 - heatIntensity) * 0.7;
        
        LXFloat4 moltenColor = moltenGradient.reflect(lavaFlow * 0.5 + 0.5);
        LXFloat4 crustColor = crustGradient.reflect(crustPattern * 0.5 + 0.5);
        double moltenMix = coreTemp * (0.7 + Math.abs(bubbling) * 0.3);
        LXFloat4 baseColor = moltenColor.lerp(crustColor, 1.0 - moltenMix);
        
        double heatGlow = Math.max(0.0, coreTemp - 0.3) * 1.5 * volcanicRegion;
        if (heatGlow > 0.0) {
            LXFloat4 glowColor = new LXFloat4(1.0, 0.6, 0.2, 1.0);
            baseColor = baseColor.lerp(glowColor, heatGlow * 0.4);
        }
        
        double bubbleHighlight = Math.max(0.0, bubbling) * coreTemp;
        if (bubbleHighlight > 0.3) {
            LXFloat4 bubbleColor = new LXFloat4(1.0, 0.8, 0.4, 1.0);
            baseColor = baseColor.lerp(bubbleColor, (bubbleHighlight - 0.3) * 0.5);
        }
        
        double intensity = (0.6 + coreTemp * 0.7 + Math.abs(lavaFlow) * 0.15) * volcanicRegion * 1.4;
        double thermalRadiation = Math.sin(geologicalTime * 2.5 + distance * 8.0 + globalFlow * 10.0) * 0.06 + 0.97;
        
        // Increase contrast - make molten core extremely bright while keeping crust very dark
        double moltenIntensity = Math.max(coreTemp, Math.abs(lavaFlow) * 0.5);
        double contrastBoost = Math.pow(moltenIntensity, 0.3);
        baseColor = baseColor.mul(0.8 + contrastBoost * 2.2);
        
        // Apply contrast-enhanced brightness
        double finalBrightness = intensity * thermalRadiation * (0.1 + contrastBoost * 0.9);
        return baseColor.mul(finalBrightness).clamp().gamma();
    }
}