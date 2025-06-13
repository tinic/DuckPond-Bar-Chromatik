package com.duckpond.pattern;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.model.LXPoint;
import com.duckpond.Gradient;
import com.duckpond.Float4;
import com.duckpond.ColorSpace;

@LXCategory("DuckPond")
public class StarFieldPattern extends UmbrellaPattern {
    
    private final Gradient starFieldGradient;
    private final Gradient twinkleGradient;
    
    public StarFieldPattern(LX lx) {
        super(lx);
        
        this.starFieldGradient = new Gradient(new Float4[] {
            new Float4(0x00000d, 0.0),
            new Float4(0x0d0d26, 0.2),
            new Float4(0x26264d, 0.4),
            new Float4(0x666699, 0.6),
            new Float4(0xcccce6, 0.8),
            new Float4(0x99b3e6, 1.0)
        }, Gradient.ColorMode.RGB);
        
        this.twinkleGradient = new Gradient(new Float4[] {
            new Float4(0x000005, 0.0),
            new Float4(0xe6e6f2, 0.2),
            new Float4(0xcce6ff, 0.4),
            new Float4(0xfff2cc, 0.6),
            new Float4(0xe6ccff, 0.8),
            new Float4(0xb3cce6, 1.0)
        }, Gradient.ColorMode.RGB);
    }
    
    @Override
    protected Float4 calculatePointColor(LXPoint point, Float4 globalPos, Float4 localPos, double time) {
        double stellarTime = time * 0.15;
        
        // Global star field coordinates - different sky regions for each umbrella - NOW DOMINANT
        double skyRegionX = globalPos.x * 0.1;
        double skyRegionY = globalPos.y * 0.08;
        double stellarDensity = Math.sin(globalPos.x * 0.25) * Math.cos(globalPos.y * 0.2) * 0.8 + 0.4;
        
        // Major constellation patterns that span across umbrellas
        double constellationZone = Math.sin(globalPos.x * 0.15 + stellarTime * 0.05) * Math.cos(globalPos.y * 0.12 + stellarTime * 0.04);
        
        // Star seeds heavily incorporate global position for unique star patterns per umbrella
        double starSeed1 = Math.sin((localPos.x * 0.5 + skyRegionX * 5.0) * 17.31 + (localPos.y * 0.5 + skyRegionY * 5.0) * 23.47 + constellationZone * 100.0) * 43758.5453;
        double starSeed2 = Math.sin((localPos.x * 0.5 + skyRegionX * 4.0) * 29.17 + (localPos.y * 0.5 + skyRegionY * 4.0) * 31.23 + globalPos.len() * 50.0) * 12345.6789;
        double starSeed3 = Math.sin((localPos.len() * 0.5 + globalPos.len() * 0.8) * 19.83 + stellarTime * 0.1 + constellationZone * 80.0) * 98765.4321;
        starSeed1 = starSeed1 - Math.floor(starSeed1);
        starSeed2 = starSeed2 - Math.floor(starSeed2);
        starSeed3 = starSeed3 - Math.floor(starSeed3);
        
        // Star visibility varies by region
        double brightStars = (starSeed1 > 0.85 ? (starSeed1 - 0.85) * 6.67 : 0.0) * stellarDensity;
        double mediumStars = (starSeed2 > 0.7 ? (starSeed2 - 0.7) * 3.33 : 0.0) * stellarDensity;
        double dimStars = (starSeed3 > 0.5 ? (starSeed3 - 0.5) * 2.0 : 0.0) * stellarDensity;
        
        // Atmospheric twinkling heavily varies by global position
        double atmosphericTurbulence = Math.sin(globalPos.len() * 0.3 + stellarTime * 0.6) * 0.6 + 0.6;
        double twinkle1 = Math.sin(stellarTime * 2.1 + starSeed1 * 100.0 + skyRegionX * 200.0 + constellationZone * 150.0) * 0.15 + 0.35;
        double twinkle2 = Math.cos(stellarTime * 1.7 + starSeed2 * 80.0 + skyRegionY * 160.0) * 0.125 + 0.375;
        double twinkle3 = Math.sin(stellarTime * 1.3 + starSeed3 * 60.0 + globalPos.len() * 40.0) * 0.1 + 0.4;
        
        twinkle1 *= atmosphericTurbulence;
        twinkle2 *= atmosphericTurbulence;
        twinkle3 *= atmosphericTurbulence;
        
        double twinklingBright = brightStars * twinkle1;
        double twinklingMedium = mediumStars * twinkle2;
        double twinklingDim = dimStars * twinkle3;
        double totalStarlight = twinklingBright + twinklingMedium * 0.6 + twinklingDim * 0.3;
        
        // Nebula clouds heavily vary across the sky
        double nebula1 = Math.sin((localPos.x * 0.4 + skyRegionX * 2.0) * 0.8 + stellarTime * 0.3 + constellationZone * 2.0) * 0.05;
        double nebula2 = Math.cos((localPos.y * 0.3 + skyRegionY * 2.0) * 0.6 + stellarTime * 0.2) * 0.04;
        double nebulaGlow = (nebula1 + nebula2) * 0.25 + 0.025 + constellationZone * 0.1;
        
        // Milky Way orientation heavily varies by global position
        double milkyWayAngle = Math.atan2(localPos.y * 0.5, localPos.x * 0.5) + Math.atan2(globalPos.y, globalPos.x) * 1.5;
        double milkyWayIntensity = Math.sin(globalPos.x * 0.15) * Math.cos(globalPos.y * 0.12) * 0.8 + 0.4;
        double milkyWayBand = Math.cos(milkyWayAngle * 2.0 + stellarTime * 0.1 + globalPos.len() * 0.5) * 0.075 + 0.425;
        milkyWayBand = Math.pow(Math.max(0.0, milkyWayBand - 0.35), 2.0) * milkyWayIntensity + constellationZone * 0.3;
        
        Float4 starColor = starFieldGradient.reflect(totalStarlight);
        Float4 twinkleColor = twinkleGradient.reflect(twinklingBright);
        double starBrightness = Math.max(twinklingBright, Math.max(twinklingMedium, twinklingDim));
        Float4 finalColor = starColor.lerp(twinkleColor, starBrightness * 0.8);
        
        finalColor = finalColor.add(ColorSpace.rgbToOklab(nebulaGlow * 0.3, nebulaGlow * 0.2, nebulaGlow * 0.5, 1.0));
        finalColor = finalColor.add(ColorSpace.rgbToOklab(milkyWayBand * 0.15, milkyWayBand * 0.15, milkyWayBand * 0.2, 1.0));
        
        double atmosphericShimmer = Math.sin(stellarTime * 5.0 + localPos.len() * 10.0 + globalPos.len() * 2.0) * 0.05 + 0.95;
        double nightSkyBreath = Math.sin(stellarTime * 0.15 + globalPos.x * 0.01) * 0.1 + 0.9;
        
        double brightness = (totalStarlight + nebulaGlow + milkyWayBand) * nightSkyBreath * atmosphericShimmer * stellarDensity * 1.75;
        
        // Increase contrast - make bright stars much brighter while keeping space very dark
        double starIntensity = Math.max(twinklingBright, Math.max(twinklingMedium * 0.8, twinklingDim * 0.6));
        double contrastBoost = Math.pow(starIntensity, 0.3);
        finalColor = finalColor.mul(0.6 + contrastBoost * 2.4);
        
        // Extra dramatic boost for very bright stars
        if (twinklingBright > 0.4) {
            finalColor = finalColor.mul(1.0 + (twinklingBright - 0.4) * 3.0);
        }
        
        // Apply contrast-enhanced brightness
        double finalBrightness = brightness * (0.05 + contrastBoost * 0.95);
        return finalColor.mul(finalBrightness);
    }
}