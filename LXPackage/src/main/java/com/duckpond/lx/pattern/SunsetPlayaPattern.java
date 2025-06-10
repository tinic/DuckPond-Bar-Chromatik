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
        double playaTime = time * 0.01;
        double horizonLayer = localPos.y + Math.sin(playaTime * 0.3) * 0.2;
        double heatWave1 = Math.sin(localPos.x * 3.0 + playaTime * 0.8) * 0.3;
        double heatWave2 = Math.cos(localPos.y * 2.5 + playaTime * 0.6) * 0.2;
        double heatWave3 = Math.sin(localPos.len() * 1.5 + playaTime * 0.4) * 0.25;
        double heatDistortion = (heatWave1 + heatWave2 + heatWave3) / 2.75;
        double dustSwirl1 = Math.sin(localPos.x * 1.2 + playaTime * 0.5) * 0.6;
        double dustSwirl2 = Math.cos(localPos.y * 0.8 + playaTime * 0.7) * 0.4;
        double dustPattern = (dustSwirl1 + dustSwirl2) / 2.0;
        double sunsetPosition = horizonLayer + heatDistortion;
        double sunsetPhase = Math.sin(playaTime * 0.15) * 0.5 + 0.5;
        LXFloat4 sunsetColor = desertSunsetGradient.reflect(sunsetPosition * 0.5 + 0.5);
        LXFloat4 dustColor = dustStormGradient.reflect(dustPattern * 0.5 + 0.5);
        double dustMix = Math.abs(dustPattern) * 0.4 + sunsetPhase * 0.3;
        LXFloat4 finalColor = sunsetColor.lerp(dustColor, dustMix);
        double goldenGlow = Math.sin(playaTime * 0.2) * 0.2 + 0.8;
        double distance = localPos.len();
        double atmosphere = Math.exp(-distance * 0.5) * 0.5 + 0.5;
        double intensity = goldenGlow * atmosphere * (0.7 + Math.abs(heatDistortion) * 0.2);
        return finalColor.mul(intensity).clamp().gamma();
    }
}
