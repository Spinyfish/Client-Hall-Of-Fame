package rip.athena.client.modules.impl.render;

import rip.athena.client.config.*;
import rip.athena.client.modules.*;
import org.lwjgl.opengl.*;

public class MotionBlur extends Module
{
    @ConfigValue.Integer(name = "Amount", min = 1, max = 10)
    public static int amount;
    public static float f;
    
    public MotionBlur() {
        super("Motion Blur", Category.RENDER, "Athena/gui/mods/motionblur.png");
    }
    
    public static void createAccumulation() {
        final float f = getAccumulationValue();
        GL11.glAccum(259, f);
        GL11.glAccum(256, 1.0f - f);
        GL11.glAccum(258, 1.0f);
    }
    
    public static float getMultiplier() {
        return (float)(MotionBlur.amount * 10);
    }
    
    public static float getAccumulationValue() {
        MotionBlur.f = getMultiplier() * 10.0f;
        final long lastTimestampInGame = System.currentTimeMillis();
        if (MotionBlur.f > 996.0f) {
            MotionBlur.f = 996.0f;
        }
        if (MotionBlur.f > 990.0f) {
            MotionBlur.f = 990.0f;
        }
        final long i = System.currentTimeMillis() - lastTimestampInGame;
        if (i > 10000L) {
            return 0.0f;
        }
        if (MotionBlur.f < 0.0f) {
            MotionBlur.f = 0.0f;
        }
        return MotionBlur.f / 1000.0f;
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
    }
    
    static {
        MotionBlur.amount = 1;
    }
}
