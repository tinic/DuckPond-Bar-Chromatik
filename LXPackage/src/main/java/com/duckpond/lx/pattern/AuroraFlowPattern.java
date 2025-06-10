package com.duckpond.lx.pattern;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.model.LXPoint;
import com.duckpond.lx.Gradient;
import com.duckpond.lx.LXFloat4;

@LXCategory("DuckPond")
public class AuroraFlowPattern extends UmbrellaPattern {
    
    private final Gradient auroraGradient;
    private final Gradient polarGradient;
    
    public AuroraFlowPattern(LX lx) {
        super(lx);
        
        this.auroraGradient = new Gradient(new LXFloat4[] {
            new LXFloat4(0x001a33, 0.0),
            new LXFloat4(0x006699, 0.2),
            new LXFloat4(0x1ab34d, 0.4),
            new LXFloat4(0x4de680, 0.6),
            new LXFloat4(0x80cce6, 0.8),
            new LXFloat4(0x3399cc, 1.0)
        }, Gradient.ColorMode.RGB);
        
        this.polarGradient = new Gradient(new LXFloat4[] {
            new LXFloat4(0x0d0d26, 0.0),
            new LXFloat4(0x334db3, 0.25),
            new LXFloat4(0x66cc99, 0.5),
            new LXFloat4(0x99e6cc, 0.75),
            new LXFloat4(0x4d80e6, 1.0)
        }, Gradient.ColorMode.RGB);
    }
    
    @Override
    protected LXFloat4 calculatePointColor(LXPoint point, LXFloat4 globalPos, LXFloat4 localPos, double time) {
        double auroraTime = time * 0.012;
        double curtain1 = Math.sin(localPos.x * 2.0 + auroraTime * 1.1) * 0.8;
        double curtain2 = Math.cos(localPos.y * 1.5 + auroraTime * 0.7) * 0.6;
        double curtain3 = Math.sin((localPos.x + localPos.y) * 1.2 + auroraTime * 0.9) * 0.7;
        double auroraFlow = (curtain1 + curtain2 + curtain3) / 2.9;
        double stream1 = Math.sin(localPos.y * 4.0 + auroraTime * 1.3) * 0.5;
        double stream2 = Math.cos(localPos.x * 3.0 + auroraTime * 0.8) * 0.4;
        double verticalFlow = (stream1 + stream2) / 1.9;
        double shimmer1 = Math.sin(localPos.len() * 8.0 + auroraTime * 2.0) * 0.3;
        double shimmer2 = Math.cos(localPos.x * 6.0 + localPos.y * 5.0 + auroraTime * 1.5) * 0.25;
        double particleShimmer = (shimmer1 + shimmer2) / 2.25;
        double auroraPattern = auroraFlow + verticalFlow * 0.6 + particleShimmer * 0.4;
        double atmosphericPulse = Math.sin(auroraTime * 0.25) * 0.3 + 0.7;
        double distance = localPos.len();
        double intensityFade = Math.exp(-distance * 0.6) * 0.7 + 0.3;
        LXFloat4 mainAurora = auroraGradient.reflect(auroraPattern * 0.5 + 0.5);
        LXFloat4 polarGlow = polarGradient.reflect(verticalFlow * 0.5 + 0.5);
        double auroraActivity = Math.abs(auroraFlow) * 0.7 + Math.abs(particleShimmer) * 0.3;
        LXFloat4 finalColor = mainAurora.lerp(polarGlow, auroraActivity * 0.6);
        double brightness = atmosphericPulse * intensityFade * (0.8 + auroraActivity * 0.2);
        double flicker = Math.sin(auroraTime * 3.2) * 0.1 + 0.9;
        return finalColor.mul(brightness * flicker).clamp().gamma();
    }
}