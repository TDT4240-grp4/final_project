package com.tdt4240Grp04.clashofclaws.config;

import org.junit.Test;
import static org.junit.Assert.*;

public class GameConfigTest {

    @Test
    public void catZeroHasLowestMaxSpeed() {
        assertTrue(GameConfig.getMaxSpeed(0) < GameConfig.getMaxSpeed(2));
    }

    @Test
    public void catTwoHasHighestMaxSpeed() {
        assertTrue(GameConfig.getMaxSpeed(2) > GameConfig.getMaxSpeed(1));
    }

    @Test
    public void catOneHasHighestDrainRate() {
        assertTrue(GameConfig.getDrainRate(1) > GameConfig.getDrainRate(0));
        assertTrue(GameConfig.getDrainRate(1) > GameConfig.getDrainRate(2));
    }

    @Test
    public void catZeroHasHighestGrowthRate() {
        assertTrue(GameConfig.getGrowthRate(0) > GameConfig.getGrowthRate(1));
    }

    @Test
    public void outOfBoundsIndexClampsToValidCat() {
        assertEquals(GameConfig.getMaxSpeed(0), GameConfig.getMaxSpeed(-1), 0.001f);
        assertEquals(GameConfig.getMaxSpeed(2), GameConfig.getMaxSpeed(99), 0.001f);
    }

    @Test
    public void allDashMultipliersAboveOne() {
        for (int i = 0; i < 3; i++) {
            assertTrue("Cat " + i + " dash multiplier must be > 1", GameConfig.getDashMultiplier(i) > 1.0f);
        }
    }
}
