package com.duckpond.pattern;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.model.LXPoint;
import com.duckpond.Gradient;
import com.duckpond.Float4;

@LXCategory("DuckPond")
public class AuroraFlowPattern extends UmbrellaPattern {
    
    private final Gradient auroraGradient;
    private final Gradient polarGradient;
    
    public AuroraFlowPattern(LX lx) {
        super(lx);
        
        this.auroraGradient = new Gradient(new Float4[] {
            new Float4(0x001a33, 0.0),
            new Float4(0x006699, 0.2),
            new Float4(0x1ab34d, 0.4),
            new Float4(0x4de680, 0.6),
            new Float4(0x80cce6, 0.8),
            new Float4(0x3399cc, 1.0)
        }, Gradient.ColorMode.RGB);
        
        this.polarGradient = new Gradient(new Float4[] {
            new Float4(0x0d0d26, 0.0),
            new Float4(0x334db3, 0.25),
            new Float4(0x66cc99, 0.5),
            new Float4(0x99e6cc, 0.75),
            new Float4(0x4d80e6, 1.0)
        }, Gradient.ColorMode.RGB);
    }
    
    @Override
    protected Float4 calculatePointColor(LXPoint point, Float4 globalPos, Float4 localPos, double time) {
        double auroraTime = time * 0.35;
        
        // Global wave patterns that vary across the installation - NOW DOMINANT
        double globalWave1 = Math.sin(globalPos.x * 0.8 + globalPos.y * 0.6 + auroraTime * 0.8);
        double globalWave2 = Math.cos(globalPos.y * 1.2 - globalPos.x * 0.9 + auroraTime * 0.6);
        double globalPhase = (globalWave1 + globalWave2) * 1.2;
        
        // Global aurora bands that sweep across umbrellas
        double auroraZone = Math.sin(globalPos.x * 0.4 + auroraTime * 0.3) * Math.cos(globalPos.y * 0.3 + auroraTime * 0.4);
        
        // Local curtain effects now heavily modulated by global position
        double curtain1 = Math.sin(localPos.x * 1.0 + auroraTime * 1.1 + globalPhase * 2.0 + auroraZone * 3.0) * 0.4;
        double curtain2 = Math.cos(localPos.y * 0.8 + auroraTime * 0.7 + globalPos.x * 2.0) * 0.3;
        double curtain3 = Math.sin((localPos.x + localPos.y) * 0.6 + auroraTime * 0.9 + globalPos.y * 1.5) * 0.35;
        double auroraFlow = (curtain1 + curtain2 + curtain3) / 1.05 + globalPhase * 0.8;
        
        // Vertical streams heavily influenced by global positioning
        double stream1 = Math.sin(localPos.y * 2.0 + auroraTime * 1.3 + globalPos.x * 2.5) * 0.25;
        double stream2 = Math.cos(localPos.x * 1.5 + auroraTime * 0.8 + globalPos.y * 2.0) * 0.2;
        double verticalFlow = (stream1 + stream2) / 0.45 + auroraZone * 0.6;
        
        // Shimmer with strong global variation
        double shimmer1 = Math.sin(localPos.len() * 4.0 + auroraTime * 2.0 + globalPhase * 4.0) * 0.15;
        double shimmer2 = Math.cos(localPos.x * 3.0 + localPos.y * 2.5 + auroraTime * 1.5 + globalPos.len() * 1.2) * 0.125;
        double particleShimmer = (shimmer1 + shimmer2) / 0.275 + auroraZone * 0.5;
        
        double auroraPattern = auroraFlow + verticalFlow * 0.6 + particleShimmer * 0.4;
        double atmosphericPulse = Math.sin(auroraTime * 0.25 + globalPos.x * 0.1) * 0.3 + 0.7;
        double distance = localPos.len();
        double intensityFade = Math.exp(-distance * 0.6) * 0.7 + 0.3;
        
        Float4 mainAurora = auroraGradient.reflect(auroraPattern * 0.5 + 0.5);
        Float4 polarGlow = polarGradient.reflect(verticalFlow * 0.5 + 0.5);
        double auroraActivity = Math.abs(auroraFlow) * 0.7 + Math.abs(particleShimmer) * 0.3;
        Float4 finalColor = mainAurora.lerp(polarGlow, auroraActivity * 0.6);
        
        double brightness = atmosphericPulse * intensityFade * (1.1 + auroraActivity * 0.3) * 1.4;
        double flicker = Math.sin(auroraTime * 3.2 + globalPos.y * 0.2) * 0.12 + 0.94;
        
        // Increase contrast - make bright areas brighter and dark areas darker
        double contrastBoost = Math.pow(auroraActivity * 0.5 + 0.5, 0.6); // Enhance contrast curve
        finalColor = finalColor.mul(1.2 + contrastBoost * 0.8);
        
        // Apply contrast-enhanced brightness
        double finalBrightness = brightness * flicker * (0.3 + contrastBoost * 0.7);
        return finalColor.mul(finalBrightness);
    }
}