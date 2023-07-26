package com.craftworks.pearclient.hud.mods.impl;

import java.awt.Color;

import com.craftworks.pearclient.event.EventTarget;
import com.craftworks.pearclient.event.impl.EventClientTick;
import com.craftworks.pearclient.gui.mainmenu.MainMenu;
import com.craftworks.pearclient.hud.mods.HudMod;
import com.craftworks.pearclient.util.blur.BlurUtils;
import com.craftworks.pearclient.util.draw.DrawUtil;

import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.main.Main;

public class ToggleSprintMod extends HudMod {
	
	private boolean toggled;
    private int wasPressed;
    private int keyHoldTicks;
    private static ToggleSprintMod instance;
    
    public ToggleSprintMod() {
        super("Toggle Sprint", "Running around...", 16, 16);
        this.keyHoldTicks = 7;
        ToggleSprintMod.instance = this;
    }
    
    @Override
    public void onRender2D() {
    	if(getDisplayText() != null) {
    		ScaledResolution sr = new ScaledResolution(mc);
    		DrawUtil.drawRoundedOutline(getX() - 2.4f, getY() - 2, fr.getStringWidth(getDisplayText()) + 3.3f, getHeight() + 3, 3, 0.35f, new Color(0, 0, 0, 0), new Color(0, 0, 0, 50));
    		//this.drawShadow(getX() - 2.4f, getY() - 2, getWidth() + 3.3f, getHeight() + 2, 4.0f);
    		//DrawUtil.drawRoundedRectangle(getX() - 1, getY() - 1, fr.getStringWidth(getDisplayText()) + 1, getHeight() + 1, 3, new Color(255, 255, 255, 50));

    		setonRenderBackground(getX() - 2, getY() - 1, fr.getStringWidth(getDisplayText()) + 2, getHeight() + 1, 3.0f, new Color(255, 255, 255, 5), new Color(255, 255, 255, 5), new Color(255, 255, 255, 5), new Color(255, 255, 255, 5));
    		BlurUtils.renderBlurredBackground(sr.getScaledWidth(), sr.getScaledHeight(), getX() - 2, getY() - 1, fr.getStringWidth(getDisplayText()) + 2, getHeight() + 1, 3);
    	}
        fr.drawString(getDisplayText(), getX(), getY(), -1);
    }
    
    @Override
    public void onRenderDummy() {
       fr.drawString("[Sprinting (Toggled)]", getX(), getY(), -1);
    }
    
    @EventTarget
    public void updateMovement(EventClientTick event) {
        if (this.isToggled()) {
            if (this.mc.gameSettings.keyBindSprint.isKeyDown()) {
                if (this.wasPressed == 0) {
                    if (this.toggled) {
                        this.wasPressed = -1;
                    }
                    else if (this.mc.thePlayer.capabilities.isFlying) {
                        this.wasPressed = this.keyHoldTicks + 1;
                    }
                    else {
                        this.wasPressed = 1;
                    }
                    this.toggled = !this.toggled;
                }
                else if (this.wasPressed > 0) {
                    ++this.wasPressed;
                }
            }
            else {
                if (this.keyHoldTicks > 0 && this.wasPressed > this.keyHoldTicks) {
                    this.toggled = false;
                }
                this.wasPressed = 0;
            }
        }
        else {
            this.toggled = false;
        }
        if (this.toggled) {
            this.mc.thePlayer.setSprinting(true);
        }
    }
    
    private String getDisplayText() {
        String displayText = null;
        final boolean isFlying = this.mc.thePlayer.capabilities.isFlying;
        final boolean isSprintHeld = this.mc.gameSettings.keyBindSprint.isKeyDown();
        final boolean isSprinting = this.mc.thePlayer.isSprinting();
        if (isFlying) {
            displayText = "[Flying]";
        }
        else if (this.toggled) {
            if (isSprintHeld) {
                displayText = "[Sprinting (Key Held)]";
            }
            else {
                displayText = "[Sprinting (Toggled)]";
            }
        }
        else if (isSprinting) {
            displayText = "[Sprinting (Vanilla)]";
        }
        return displayText;
    }
    
    public int getWidth() {
        return fr.getStringWidth("[Sprinting (Toggled)]");
    }

    public int getHeight() {
        return this.fr.FONT_HEIGHT;
    }

}
