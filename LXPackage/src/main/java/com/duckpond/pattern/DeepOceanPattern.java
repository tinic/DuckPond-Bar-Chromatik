package com.duckpond.pattern;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.model.LXPoint;
import com.duckpond.Gradient;
import com.duckpond.Float4;
import com.duckpond.ColorSpace;

@LXCategory("DuckPond")
public class DeepOceanPattern extends UmbrellaPattern {
    
    private final Gradient oceanDepthGradient;
    private final Gradient bioluminescentGradient;
    
    public DeepOceanPattern(LX lx) {
        super(lx);
        
        this.oceanDepthGradient = new Gradient(new Float4[] {
            new Float4(0x000d26, 0.0),
            new Float4(0x00264d, 0.2),
            new Float4(0x0d4066, 0.4),
            new Float4(0x1a6680, 0.6),
            new Float4(0x338099, 0.8),
            new Float4(0x1a4d73, 1.0)
        }, Gradient.ColorMode.RGB);
        
        this.bioluminescentGradient = new Gradient(new Float4[] {
            new Float4(0x001a33, 0.0),
            new Float4(0x1a99cc, 0.2),
            new Float4(0x33cc99, 0.4),
            new Float4(0x66e6b3, 0.6),
            new Float4(0x00b3e6, 0.8),
            new Float4(0x4dcc80, 1.0)
        }, Gradient.ColorMode.RGB);
    }
    
    @Override
    protected Float4 calculatePointColor(LXPoint point, Float4 globalPos, Float4 localPos, double time) {
        double oceanTime = time * 0.3;
        
        // Global ocean currents - creating different flow patterns across umbrellas - NOW DOMINANT
        double globalCurrentAngle = Math.atan2(globalPos.y, globalPos.x);
        double globalCurrent1 = Math.sin(globalCurrentAngle + oceanTime * 0.3) * 1.5;
        double globalCurrent2 = Math.cos(globalPos.x * 0.4 + globalPos.y * 0.35 + oceanTime * 0.4) * 1.2;
        double globalFlow = (globalCurrent1 + globalCurrent2) * 1.0;
        
        // Major ocean zones that span across umbrellas
        double oceanZone = Math.sin(globalPos.x * 0.25 + oceanTime * 0.2) * Math.cos(globalPos.y * 0.2 + oceanTime * 0.15);
        double thermalField = Math.sin(globalPos.len() * 0.15 + oceanTime * 0.1) * 0.8;
        
        // Local currents heavily modulated by global position
        double current1 = Math.sin(localPos.x * 0.9 + oceanTime * 0.6 + globalFlow * 2.0 + oceanZone * 3.0) * 0.35;
        double current2 = Math.cos(localPos.y * 0.6 + oceanTime * 0.8 + globalPos.x * 1.2) * 0.3;
        double current3 = Math.sin(localPos.len() * 1.25 + oceanTime * 0.4 + globalPos.y * 1.0) * 0.25;
        double oceanFlow = (current1 + current2 + current3) / 0.9 + globalFlow * 0.7;
        
        // Bioluminescence clusters heavily vary by global position
        double bioRegion = Math.sin(globalPos.x * 0.6) * Math.cos(globalPos.y * 0.5) * 0.8 + 0.4;
        double bio1 = Math.sin(localPos.x * 2.5 + oceanTime * 1.5 + globalPos.len() * 2.5) * 0.2;
        double bio2 = Math.cos(localPos.y * 3.0 + oceanTime * 1.8 + globalFlow * 5.0) * 0.15;
        double bio3 = Math.sin((localPos.x + localPos.y) * 2.0 + oceanTime * 1.2 + globalCurrentAngle * 3.0) * 0.175;
        double bioluminescence = (bio1 + bio2 + bio3) / 0.525 * bioRegion + oceanZone * 0.6 + thermalField * 0.4;
        
        double depth = localPos.len();
        double depthPressure = Math.exp(-depth * 0.8);
        
        // Thermal vents at specific global positions
        double ventRegion = Math.sin(globalPos.x * 0.2) * Math.sin(globalPos.y * 0.18);
        double thermalVent = Math.sin(oceanTime * 0.3 + depth * 2.0 + globalPos.len() * 0.5) * 0.2;
        thermalVent = Math.max(0.0, thermalVent - 0.15) * Math.max(0.0, ventRegion);
        
        double oceanBreath = Math.sin(oceanTime * 0.2 + globalPos.x * 0.05) * 0.25 + 0.75;
        
        Float4 oceanColor = oceanDepthGradient.reflect(oceanFlow * 0.5 + 0.5);
        Float4 bioColor = bioluminescentGradient.reflect(bioluminescence * 0.5 + 0.5);
        double bioActivity = Math.abs(bioluminescence) * depthPressure;
        Float4 finalColor = oceanColor.lerp(bioColor, bioActivity * 0.7);
        
        if (thermalVent > 0.0) {
            Float4 thermalGlow = ColorSpace.rgbToOklab(0.3, 0.6, 0.8, 1.0);
            finalColor = finalColor.lerp(thermalGlow, thermalVent * 0.4);
        }
        
        double intensity = oceanBreath * depthPressure * (0.8 + bioActivity * 0.4) * 1.4;
        double refraction = Math.sin(oceanTime * 2.0 + oceanFlow + globalFlow) * 0.06 + 0.97;
        
        // Increase contrast - make bioluminescent areas much brighter while keeping deep areas dark
        double luminescentBoost = Math.pow(Math.max(0.0, bioActivity), 0.4);
        finalColor = finalColor.mul(1.0 + luminescentBoost * 1.5);
        
        // Apply contrast-enhanced brightness
        double finalBrightness = intensity * refraction * (0.1 + luminescentBoost * 0.9);
        return finalColor.mul(finalBrightness);
    }
}