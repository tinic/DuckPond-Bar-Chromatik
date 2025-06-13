package com.duckpond.pattern;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.model.LXPoint;
import com.duckpond.Gradient;
import com.duckpond.Float4;

@LXCategory("DuckPond")
public class CosmicDustPattern extends UmbrellaPattern {
    
    private final Gradient galaxyGradient;
    private final Gradient nebulaGradient;
    
    public CosmicDustPattern(LX lx) {
        super(lx);
        
        this.galaxyGradient = new Gradient(new Float4[] {
            new Float4(0x05050d, 0.0),
            new Float4(0x1a0d33, 0.2),
            new Float4(0x4d1a66, 0.4),
            new Float4(0x334d99, 0.6),
            new Float4(0x9933cc, 0.8),
            new Float4(0x1a66b3, 1.0)
        }, Gradient.ColorMode.RGB);
        
        this.nebulaGradient = new Gradient(new Float4[] {
            new Float4(0x0d051a, 0.0),
            new Float4(0x661a99, 0.2),
            new Float4(0xb34de6, 0.4),
            new Float4(0x4d99e6, 0.6),
            new Float4(0xcc66e6, 0.8),
            new Float4(0x3380cc, 1.0)
        }, Gradient.ColorMode.RGB);
    }
    
    @Override
    protected Float4 calculatePointColor(LXPoint point, Float4 globalPos, Float4 localPos, double time) {
        double cosmicTime = time * 0.2;
        
        // Global galactic coordinates - each umbrella represents a different region - NOW DOMINANT
        double globalGalacticAngle = Math.atan2(globalPos.y, globalPos.x);
        double globalRadius = globalPos.len() * 0.3;
        double galaxyRotation = cosmicTime * 0.15 + globalGalacticAngle * 1.2;
        
        // Major galactic features that span across umbrellas
        double galaxyArm = Math.sin(globalPos.x * 0.2 + cosmicTime * 0.1) * Math.cos(globalPos.y * 0.15 + cosmicTime * 0.08);
        double galaxyCore = Math.exp(-globalRadius * 0.8) * 1.5;
        
        double angle = Math.atan2(localPos.y, localPos.x);
        double radius = localPos.len();
        
        // Spiral arms heavily influenced by global position
        double spiralArm1 = Math.sin(angle * 1.0 + radius * 2.0 + cosmicTime * 0.8 + galaxyRotation * 2.0 + galaxyArm * 4.0) * 0.4;
        double spiralArm2 = Math.cos(angle * 1.5 - radius * 1.5 + cosmicTime * 0.6 + globalPos.x * 1.0) * 0.32;
        double spiralArm3 = Math.sin(angle * 0.75 + radius * 2.5 - cosmicTime * 0.4 + globalPos.y * 0.8) * 0.3;
        double spiralPattern = (spiralArm1 + spiralArm2 + spiralArm3) / 1.02 + galaxyArm * 0.8 + galaxyCore * 0.6;
        
        // Dust clouds heavily vary by global position
        double dust1 = Math.sin(localPos.x * 0.8 + cosmicTime * 0.3 + globalPos.x * 1.5) * 0.35;
        double dust2 = Math.cos(localPos.y * 1.1 + cosmicTime * 0.5 + globalPos.y * 1.2) * 0.3;
        double dust3 = Math.sin(localPos.len() * 0.9 + cosmicTime * 0.2 + globalRadius * 15.0) * 0.25;
        double dustPattern = (dust1 + dust2 + dust3) / 0.9 + galaxyArm * 0.7;
        
        double depthFade = Math.exp(-radius * 0.8);
        double cosmicPulse = Math.sin(cosmicTime * 0.2 + globalPos.len() * 0.1) * 0.3 + 0.7;
        
        // Stellar density varies across the installation
        double stellarRegion = Math.sin(globalPos.x * 0.2) * Math.cos(globalPos.y * 0.15) * 0.3 + 0.7;
        
        Float4 galaxyColor = galaxyGradient.reflect(spiralPattern * 0.5 + 0.5);
        Float4 nebulaColor = nebulaGradient.reflect(dustPattern * 0.5 + 0.5);
        double spiralMix = Math.abs(spiralPattern) * depthFade * stellarRegion;
        Float4 finalColor = galaxyColor.lerp(nebulaColor, spiralMix * 0.8);
        
        double stellarDensity = 0.7 + Math.abs(spiralPattern) * 0.4 + Math.abs(dustPattern) * 0.3;
        double brightness = stellarDensity * cosmicPulse * (0.8 + depthFade * 0.6) * stellarRegion * 1.4;
        
        // Increase contrast - enhance bright star regions while keeping dark space dark
        double starIntensity = Math.max(Math.abs(spiralPattern), Math.abs(dustPattern));
        double contrastBoost = Math.pow(starIntensity, 0.5); // Enhance contrast curve
        finalColor = finalColor.mul(1.1 + contrastBoost * 1.2);
        
        // Apply contrast-enhanced brightness
        double finalBrightness = brightness * (0.2 + contrastBoost * 0.8);
        return finalColor.mul(finalBrightness);
    }
}