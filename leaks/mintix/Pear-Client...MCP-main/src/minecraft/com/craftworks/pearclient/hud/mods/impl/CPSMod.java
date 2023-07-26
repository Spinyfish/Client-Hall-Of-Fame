package com.craftworks.pearclient.hud.mods.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import com.craftworks.pearclient.hud.mods.HudMod;
import com.craftworks.pearclient.util.blur.BlurUtils;
import com.craftworks.pearclient.util.draw.DrawUtil;

import net.minecraft.client.gui.ScaledResolution;

public class CPSMod extends HudMod {

    public CPSMod() {
        super("CPS", "Ye", 75, 75);
    }

    private List<Long> leftClicks = new ArrayList<Long>();
    private List<Long> rightClicks = new ArrayList<Long>();
    private boolean leftWasPressed;
    private boolean rightWasPressed;
    private long leftLastPress;
    private long rightLastPress;

    private String getStringToRender() {
        String bracket1 = null, bracket2 = null;
        String toReturn = "Error!";
                bracket1 = "";
                bracket2 = "";

        toReturn = bracket1 + this.getLeftCPS() + " |" + (" " + this.getRightCPS() + " " +  ("CPS") + bracket2);

        return toReturn;

    }

    @Override
    public int getHeight() {
        return fr.FONT_HEIGHT;
    }

    @Override
    public int getWidth() {
            return (int) fr.getStringWidth(getStringToRender());
    }


    @Override
    public void onRender2D() {
        final boolean leftPressed = Mouse.isButtonDown(0);
        final boolean rightPressed = Mouse.isButtonDown(1);
        if (leftPressed != this.leftWasPressed) {
            this.leftWasPressed = leftPressed;
            this.leftLastPress = System.currentTimeMillis();
            if (leftPressed) {
                this.leftClicks.add(this.leftLastPress);
            }
        }
        if (rightPressed != this.rightWasPressed) {
            this.rightWasPressed = rightPressed;
            this.rightLastPress = System.currentTimeMillis();
            if (rightPressed) {
                this.rightClicks.add(this.rightLastPress);
            }
        }
        ScaledResolution sr = new ScaledResolution(mc);
        DrawUtil.drawRoundedOutline(getX() - 3, getY() - 4, getWidth() + 5, getHeight() + 6, 3, 0.35f, new Color(0, 0, 0, 0), new Color(0, 0, 0, 50));
		BlurUtils.renderBlurredBackground(sr.getScaledWidth(), sr.getScaledHeight(), getX() - 2, getY() - 3, getWidth() + 3, getHeight() + 4, 3);
		setonRenderBackground(getX() - 2, getY() - 3, getWidth() + 2, getHeight() + 4, 3.0f, new Color(255, 255, 255, 5), new Color(255, 255, 255, 5), new Color(255, 255, 255, 5), new Color(255, 255, 255, 5));
            fr.drawString(getStringToRender(), this.getX(), this.getY(), -1);

            super.onRender2D();
    }

    @Override
    public void onRenderDummy() {
        final boolean leftPressed = Mouse.isButtonDown(0);
        final boolean rightPressed = Mouse.isButtonDown(1);
        if (leftPressed != this.leftWasPressed) {
            this.leftWasPressed = leftPressed;
            this.leftLastPress = System.currentTimeMillis();
            if (leftPressed) {
                this.leftClicks.add(this.leftLastPress);
            }
        }
        if (rightPressed != this.rightWasPressed) {
            this.rightWasPressed = rightPressed;
            this.rightLastPress = System.currentTimeMillis();
            if (rightPressed) {
                this.rightClicks.add(this.rightLastPress);
            }
        }
            fr.drawString(getStringToRender(), this.getX(), this.getY(), -1);

            super.onRenderDummy();
    }

    private int getLeftCPS() {
        final long time = System.currentTimeMillis();
        this.leftClicks.removeIf(aLong -> aLong + 1000L < time);
        return this.leftClicks.size();
    }

    private int getRightCPS() {
        final long time = System.currentTimeMillis();
        this.rightClicks.removeIf(aLong -> aLong + 1000L < time);
        return this.rightClicks.size();
    }
}