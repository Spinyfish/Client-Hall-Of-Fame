package com.craftworks.pearclient.util.scroll;

import org.lwjgl.input.Mouse;

import com.craftworks.pearclient.util.math.MathHelper;

import net.minecraft.client.Minecraft;

public class ScrollHelper {
    private float step = 0,real = 0,scroll = 0,elementsHeight = 0,maxScroll = 0,speed = 300F;
    private boolean flag = true;

    /**
     Returns the smoothed scroll value, only call this once after you set all the setters
     */
    public float getScroll() {
        int wheel = Mouse.getDWheel();
        float delta = 1f/Minecraft.getDebugFPS();
        if(flag)
            real -= wheel / 120f * step;
        float divider = delta*2*Math.min(speed, elementsHeight);
        if(scroll > real) {
            scroll -= ((scroll - real) / 50) * divider;
        }
        if(scroll < real) {
            scroll += ((real - scroll) / 50) * divider;
        }

        real = MathHelper.clamp_float(real, 0, elementsHeight > maxScroll ? elementsHeight - maxScroll : 0);
        real = ((int)(real*100))/100f;
        return -scroll;
    }

    /**
     How much to increment each scroll
     */
    public void setStep(float step) {
        this.step = step;
    }

    /**
     Height and gaps of all elements to be scrolled combined
     */
    public void setElementsHeight(float elementsHeight) {
        this.elementsHeight = elementsHeight;
    }

    /**
     Maximum scroll, for example height of your scrollable panel
     */
    public void setMaxScroll(float maxScroll) {
        this.maxScroll = maxScroll;
    }

    /**
     Speed of the scrolling
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     Scrolling will only happen when this flag is true (be default its always true)
     */
    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}