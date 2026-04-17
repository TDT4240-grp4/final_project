package com.tdt4240Grp04.clashofclaws.config;

public class GameConfig {

    // Cat index 0 = Fluffy, 1 = Shadow, 2 = Swift
    private static final float[] BASE_MAX_SPEED  = { 5.0f,  7.0f, 10.0f };
    private static final float[] BASE_MIN_SPEED  = { 1.5f,  2.0f,  3.0f };
    private static final float[] DASH_MULTIPLIER = { 1.4f,  1.5f,  1.3f };
    private static final float[] MAX_STAMINA     = { 100f,  100f,  100f  };
    private static final float[] DRAIN_RATE      = { 20f,   35f,   25f   };
    private static final float[] RECHARGE_RATE   = { 15f,   10f,   12f   };
    private static final float[] GROWTH_RATE     = { 7f,    3f,    5f    };

    public static float getMaxSpeed(int catIndex)       { return BASE_MAX_SPEED[clamp(catIndex)]; }
    public static float getMinSpeed(int catIndex)       { return BASE_MIN_SPEED[clamp(catIndex)]; }
    public static float getDashMultiplier(int catIndex) { return DASH_MULTIPLIER[clamp(catIndex)]; }
    public static float getMaxStamina(int catIndex)     { return MAX_STAMINA[clamp(catIndex)]; }
    public static float getDrainRate(int catIndex)      { return DRAIN_RATE[clamp(catIndex)]; }
    public static float getRechargeRate(int catIndex)   { return RECHARGE_RATE[clamp(catIndex)]; }
    public static float getGrowthRate(int catIndex)     { return GROWTH_RATE[clamp(catIndex)]; }

    private static int clamp(int catIndex) {
        return Math.max(0, Math.min(catIndex, BASE_MAX_SPEED.length - 1));
    }
}
