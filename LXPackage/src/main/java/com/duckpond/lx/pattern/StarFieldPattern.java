package com.duckpond.lx.pattern;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.model.LXPoint;
import com.duckpond.lx.Gradient;
import com.duckpond.lx.LXFloat4;

@LXCategory("DuckPond")
public class StarFieldPattern extends UmbrellaPattern {
    
    private final Gradient starFieldGradient;
    private final Gradient twinkleGradient;
    
    public StarFieldPattern(LX lx) {
        super(lx);
        
        this.starFieldGradient = new Gradient(new LXFloat4[] {
            new LXFloat4(0.0, 0.0, 0.05, 1.0),
            new LXFloat4(0.05, 0.05, 0.15, 1.0),
            new LXFloat4(0.15, 0.15, 0.3, 1.0),
            new LXFloat4(0.4, 0.4, 0.6, 1.0),
            new LXFloat4(0.8, 0.8, 0.9, 1.0),
            new LXFloat4(0.6, 0.7, 0.9, 1.0)
        }, Gradient.ColorMode.RGB);
        
        this.twinkleGradient = new Gradient(new LXFloat4[] {
            new LXFloat4(0.0, 0.0, 0.02, 1.0),
            new LXFloat4(0.9, 0.9, 0.95, 1.0),
            new LXFloat4(0.8, 0.9, 1.0, 1.0),
            new LXFloat4(1.0, 0.95, 0.8, 1.0),
            new LXFloat4(0.9, 0.8, 1.0, 1.0),
            new LXFloat4(0.7, 0.8, 0.9, 1.0)
        }, Gradient.ColorMode.RGB);
    }
    
    @Override
    protected LXFloat4 calculatePointColor(LXPoint point, LXFloat4 globalPos, LXFloat4 localPos, double time) {
        double stellarTime = time * 0.005;
        double starSeed1 = Math.sin(localPos.x * 17.31 + localPos.y * 23.47) * 43758.5453;
        double starSeed2 = Math.sin(localPos.x * 29.17 + localPos.y * 31.23) * 12345.6789;
        double starSeed3 = Math.sin(localPos.len() * 19.83 + stellarTime * 0.1) * 98765.4321;
        starSeed1 = starSeed1 - Math.floor(starSeed1);
        starSeed2 = starSeed2 - Math.floor(starSeed2);
        starSeed3 = starSeed3 - Math.floor(starSeed3);
        double brightStars = starSeed1 > 0.85 ? (starSeed1 - 0.85) * 6.67 : 0.0;
        double mediumStars = starSeed2 > 0.7 ? (starSeed2 - 0.7) * 3.33 : 0.0;
        double dimStars = starSeed3 > 0.5 ? (starSeed3 - 0.5) * 2.0 : 0.0;
        double twinkle1 = Math.sin(stellarTime * 2.1 + starSeed1 * 100.0) * 0.3 + 0.7;
        double twinkle2 = Math.cos(stellarTime * 1.7 + starSeed2 * 80.0) * 0.25 + 0.75;
        double twinkle3 = Math.sin(stellarTime * 1.3 + starSeed3 * 60.0) * 0.2 + 0.8;
        double twinklingBright = brightStars * twinkle1;
        double twinklingMedium = mediumStars * twinkle2;
        double twinklingDim = dimStars * twinkle3;
        double totalStarlight = twinklingBright + twinklingMedium * 0.6 + twinklingDim * 0.3;
        double nebula1 = Math.sin(localPos.x * 0.8 + stellarTime * 0.3) * 0.1;
        double nebula2 = Math.cos(localPos.y * 0.6 + stellarTime * 0.2) * 0.08;
        double nebulaGlow = (nebula1 + nebula2) * 0.5 + 0.05;
        double milkyWayAngle = Math.atan2(localPos.y, localPos.x);
        double milkyWayBand = Math.cos(milkyWayAngle * 2.0 + stellarTime * 0.1) * 0.15 + 0.85;
        milkyWayBand = Math.pow(Math.max(0.0, milkyWayBand - 0.7), 2.0);
        LXFloat4 starColor = starFieldGradient.reflect(totalStarlight);
        LXFloat4 twinkleColor = twinkleGradient.reflect(twinklingBright);
        double starBrightness = Math.max(twinklingBright, Math.max(twinklingMedium, twinklingDim));
        LXFloat4 finalColor = starColor.lerp(twinkleColor, starBrightness * 0.8);
        finalColor = finalColor.add(new LXFloat4(nebulaGlow * 0.3, nebulaGlow * 0.2, nebulaGlow * 0.5, 1.0));
        finalColor = finalColor.add(new LXFloat4(milkyWayBand * 0.15, milkyWayBand * 0.15, milkyWayBand * 0.2, 1.0));
        double atmosphericShimmer = Math.sin(stellarTime * 5.0 + localPos.len() * 10.0) * 0.05 + 0.95;
        double nightSkyBreath = Math.sin(stellarTime * 0.15) * 0.1 + 0.9;
        double brightness = (totalStarlight + nebulaGlow + milkyWayBand) * nightSkyBreath * atmosphericShimmer;
        return finalColor.mul(brightness).clamp().gamma();
    }
}