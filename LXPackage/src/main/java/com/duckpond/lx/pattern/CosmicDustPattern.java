package com.duckpond.lx.pattern;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.model.LXPoint;
import com.duckpond.lx.Gradient;
import com.duckpond.lx.LXFloat4;

@LXCategory("DuckPond")
public class CosmicDustPattern extends UmbrellaPattern {
    
    private final Gradient galaxyGradient;
    private final Gradient nebulaGradient;
    
    public CosmicDustPattern(LX lx) {
        super(lx);
        
        this.galaxyGradient = new Gradient(new LXFloat4[] {
            new LXFloat4(0x05050d, 0.0),
            new LXFloat4(0x1a0d33, 0.2),
            new LXFloat4(0x4d1a66, 0.4),
            new LXFloat4(0x334d99, 0.6),
            new LXFloat4(0x9933cc, 0.8),
            new LXFloat4(0x1a66b3, 1.0)
        }, Gradient.ColorMode.RGB);
        
        this.nebulaGradient = new Gradient(new LXFloat4[] {
            new LXFloat4(0x0d051a, 0.0),
            new LXFloat4(0x661a99, 0.2),
            new LXFloat4(0xb34de6, 0.4),
            new LXFloat4(0x4d99e6, 0.6),
            new LXFloat4(0xcc66e6, 0.8),
            new LXFloat4(0x3380cc, 1.0)
        }, Gradient.ColorMode.RGB);
    }
    
    @Override
    protected LXFloat4 calculatePointColor(LXPoint point, LXFloat4 globalPos, LXFloat4 localPos, double time) {
        double cosmicTime = time * 0.2;
        double angle = Math.atan2(localPos.y, localPos.x);
        double radius = localPos.len();
        double spiralArm1 = Math.sin(angle * 2.0 + radius * 4.0 + cosmicTime * 0.8);
        double spiralArm2 = Math.cos(angle * 3.0 - radius * 3.0 + cosmicTime * 0.6);
        double spiralArm3 = Math.sin(angle * 1.5 + radius * 5.0 - cosmicTime * 0.4);
        double spiralPattern = (spiralArm1 + spiralArm2 * 0.8 + spiralArm3 * 0.6) / 2.4;
        double dust1 = Math.sin(localPos.x * 1.5 + cosmicTime * 0.3) * 0.7;
        double dust2 = Math.cos(localPos.y * 2.2 + cosmicTime * 0.5) * 0.6;
        double dust3 = Math.sin(localPos.len() * 1.8 + cosmicTime * 0.2) * 0.5;
        double dustPattern = (dust1 + dust2 + dust3) / 2.8;
        double depthFade = Math.exp(-radius * 0.8);
        double cosmicPulse = Math.sin(cosmicTime * 0.2) * 0.3 + 0.7;
        LXFloat4 galaxyColor = galaxyGradient.reflect(spiralPattern * 0.5 + 0.5);
        LXFloat4 nebulaColor = nebulaGradient.reflect(dustPattern * 0.5 + 0.5);
        double spiralMix = Math.abs(spiralPattern) * depthFade;
        LXFloat4 finalColor = galaxyColor.lerp(nebulaColor, spiralMix * 0.8);
        double stellarDensity = 0.5 + Math.abs(spiralPattern) * 0.3 + Math.abs(dustPattern) * 0.2;
        double brightness = stellarDensity * cosmicPulse * (0.6 + depthFade * 0.4);
        return finalColor.mul(brightness).clamp().gamma();
    }
}