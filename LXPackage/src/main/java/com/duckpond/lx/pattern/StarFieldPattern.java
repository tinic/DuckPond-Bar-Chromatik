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
            new LXFloat4(0x00000d, 0.0),
            new LXFloat4(0x0d0d26, 0.2),
            new LXFloat4(0x26264d, 0.4),
            new LXFloat4(0x666699, 0.6),
            new LXFloat4(0xcccce6, 0.8),
            new LXFloat4(0x99b3e6, 1.0)
        }, Gradient.ColorMode.RGB);
        
        this.twinkleGradient = new Gradient(new LXFloat4[] {
            new LXFloat4(0x000005, 0.0),
            new LXFloat4(0xe6e6f2, 0.2),
            new LXFloat4(0xcce6ff, 0.4),
            new LXFloat4(0xfff2cc, 0.6),
            new LXFloat4(0xe6ccff, 0.8),
            new LXFloat4(0xb3cce6, 1.0)
        }, Gradient.ColorMode.RGB);
    }
    
    @Override
    protected LXFloat4 calculatePointColor(LXPoint point, LXFloat4 globalPos, LXFloat4 localPos, double time) {
        double stellarTime = time * 0.15;
        
        // Global star field coordinates - different sky regions for each umbrella
        double skyRegionX = globalPos.x * 0.02;
        double skyRegionY = globalPos.y * 0.02;
        double stellarDensity = Math.sin(globalPos.x * 0.05) * Math.cos(globalPos.y * 0.04) * 0.3 + 0.8;
        
        // Star seeds incorporate global position for unique star patterns per umbrella
        double starSeed1 = Math.sin((localPos.x + skyRegionX) * 17.31 + (localPos.y + skyRegionY) * 23.47) * 43758.5453;
        double starSeed2 = Math.sin((localPos.x + skyRegionX) * 29.17 + (localPos.y + skyRegionY) * 31.23) * 12345.6789;
        double starSeed3 = Math.sin((localPos.len() + globalPos.len() * 0.1) * 19.83 + stellarTime * 0.1) * 98765.4321;
        starSeed1 = starSeed1 - Math.floor(starSeed1);
        starSeed2 = starSeed2 - Math.floor(starSeed2);
        starSeed3 = starSeed3 - Math.floor(starSeed3);
        
        // Star visibility varies by region
        double brightStars = (starSeed1 > 0.85 ? (starSeed1 - 0.85) * 6.67 : 0.0) * stellarDensity;
        double mediumStars = (starSeed2 > 0.7 ? (starSeed2 - 0.7) * 3.33 : 0.0) * stellarDensity;
        double dimStars = (starSeed3 > 0.5 ? (starSeed3 - 0.5) * 2.0 : 0.0) * stellarDensity;
        
        // Atmospheric twinkling varies by global position
        double atmosphericTurbulence = Math.sin(globalPos.len() * 0.08 + stellarTime * 0.2) * 0.2 + 0.8;
        double twinkle1 = Math.sin(stellarTime * 2.1 + starSeed1 * 100.0 + skyRegionX * 50.0) * 0.3 + 0.7;
        double twinkle2 = Math.cos(stellarTime * 1.7 + starSeed2 * 80.0 + skyRegionY * 40.0) * 0.25 + 0.75;
        double twinkle3 = Math.sin(stellarTime * 1.3 + starSeed3 * 60.0 + globalPos.len() * 10.0) * 0.2 + 0.8;
        
        twinkle1 *= atmosphericTurbulence;
        twinkle2 *= atmosphericTurbulence;
        twinkle3 *= atmosphericTurbulence;
        
        double twinklingBright = brightStars * twinkle1;
        double twinklingMedium = mediumStars * twinkle2;
        double twinklingDim = dimStars * twinkle3;
        double totalStarlight = twinklingBright + twinklingMedium * 0.6 + twinklingDim * 0.3;
        
        // Nebula clouds vary across the sky
        double nebula1 = Math.sin((localPos.x + skyRegionX) * 0.8 + stellarTime * 0.3) * 0.1;
        double nebula2 = Math.cos((localPos.y + skyRegionY) * 0.6 + stellarTime * 0.2) * 0.08;
        double nebulaGlow = (nebula1 + nebula2) * 0.5 + 0.05;
        
        // Milky Way orientation varies by global position
        double milkyWayAngle = Math.atan2(localPos.y, localPos.x) + Math.atan2(globalPos.y, globalPos.x) * 0.3;
        double milkyWayIntensity = Math.sin(globalPos.x * 0.03) * Math.cos(globalPos.y * 0.025) * 0.4 + 0.7;
        double milkyWayBand = Math.cos(milkyWayAngle * 2.0 + stellarTime * 0.1) * 0.15 + 0.85;
        milkyWayBand = Math.pow(Math.max(0.0, milkyWayBand - 0.7), 2.0) * milkyWayIntensity;
        
        LXFloat4 starColor = starFieldGradient.reflect(totalStarlight);
        LXFloat4 twinkleColor = twinkleGradient.reflect(twinklingBright);
        double starBrightness = Math.max(twinklingBright, Math.max(twinklingMedium, twinklingDim));
        LXFloat4 finalColor = starColor.lerp(twinkleColor, starBrightness * 0.8);
        
        finalColor = finalColor.add(new LXFloat4(nebulaGlow * 0.3, nebulaGlow * 0.2, nebulaGlow * 0.5, 1.0));
        finalColor = finalColor.add(new LXFloat4(milkyWayBand * 0.15, milkyWayBand * 0.15, milkyWayBand * 0.2, 1.0));
        
        double atmosphericShimmer = Math.sin(stellarTime * 5.0 + localPos.len() * 10.0 + globalPos.len() * 2.0) * 0.05 + 0.95;
        double nightSkyBreath = Math.sin(stellarTime * 0.15 + globalPos.x * 0.01) * 0.1 + 0.9;
        
        double brightness = (totalStarlight + nebulaGlow + milkyWayBand) * nightSkyBreath * atmosphericShimmer * stellarDensity;
        return finalColor.mul(brightness).clamp().gamma();
    }
}