/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 *  org.lwjgl.input.Mouse
 */
package wtf.monsoon.impl.ui.character;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.awt.Color;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.api.util.render.AnimatedResourceLocation;
import wtf.monsoon.api.util.render.DrawUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.event.EventUpdateEnumSetting;
import wtf.monsoon.impl.module.visual.CharacterRenderer;
import wtf.monsoon.impl.ui.ScalableScreen;

public class CharacterManager {
    private boolean dragging;
    private float dragX;
    private float dragY;
    private float x;
    private float y;
    private float width;
    private float height;
    private CharacterRenderer configModule;
    private AnimatedResourceLocation configIssueGif;
    private float animTime = 150.0f;
    private final Animation toggleHudAnimation = new Animation(() -> Float.valueOf(this.animTime), false, () -> Easing.CUBIC_IN_OUT);
    private boolean shouldSetValue = false;
    private CharacterRenderer.Image newValue = null;
    private Timer animResetTimer = new Timer();
    @EventLink
    private Listener<EventUpdateEnumSetting> eventUpdateEnumSettingListener = e -> {
        if (e.getSetting().equals(this.configModule.getImage())) {
            this.getToggleHudAnimation().setState(false);
            this.shouldSetValue = true;
            this.newValue = (CharacterRenderer.Image)e.getNewValue();
            this.animResetTimer.reset();
            e.setCancelled(true);
        }
    };
    @EventLink
    private Listener<EventPreMotion> eventPreMotionListener = e -> {
        if (Minecraft.getMinecraft().thePlayer.ticksExisted < 10) {
            this.updateWidthHeight(true, this.configModule.getImage().getValue());
        }
        if (Minecraft.getMinecraft().currentScreen == null) {
            this.getToggleHudAnimation().setState(false);
        }
    };

    public CharacterManager() {
        Wrapper.getEventBus().subscribe(this);
        this.configModule = Wrapper.getModule(CharacterRenderer.class);
        this.configIssueGif = new AnimatedResourceLocation("monsoon/config_issue", 107, 3);
    }

    public void initGui(GuiScreen guiScreen) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        if (this.isValidGuiScreen(guiScreen)) {
            this.updateWidthHeight(false, this.configModule.getImage().getValue());
        }
        this.getToggleHudAnimation().setState(true);
    }

    public void draw(int mouseX, int mouseY, float partialTicks, GuiScreen guiScreen) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        float scaleFactor = 1.0f / ((float)sr.getScaleFactor() * 0.5f);
        if (this.isValidGuiScreen(guiScreen)) {
            if (!Mouse.isButtonDown((int)0)) {
                this.dragging = false;
            }
            if (this.shouldSetValue && this.newValue != null && this.animResetTimer.hasTimeElapsed(this.animTime, false)) {
                this.configModule.getImage().setValueSilent(this.newValue);
                this.updateWidthHeight(true, this.newValue);
                this.getToggleHudAnimation().setState(true);
                this.shouldSetValue = false;
                this.newValue = null;
            }
            this.drag((float)mouseX / scaleFactor, (float)mouseY / scaleFactor);
            RenderUtil.scaleXY(this.getX() + this.getWidth() / 2.0f, this.getY() + this.getHeight() / 2.0f, this.getToggleHudAnimation(), this::renderImage);
        }
    }

    private void renderImage() {
        switch (this.configModule.getImage().getValue()) {
            case ASTOLFO: {
                this.setWidth(175.0f);
                this.setHeight(250.0f);
                DrawUtil.draw2DImage(new ResourceLocation("monsoon/characters/astolfo_1.png"), this.getX(), this.getY(), this.getWidth(), this.getHeight(), Color.WHITE);
                break;
            }
            case FELIX: {
                this.setWidth(175.0f);
                this.setHeight(250.0f);
                DrawUtil.draw2DImage(new ResourceLocation("monsoon/characters/felix.png"), this.getX(), this.getY(), this.getWidth(), this.getHeight(), Color.WHITE);
                break;
            }
            case SIMON: {
                this.setWidth(175.0f);
                this.setHeight(250.0f);
                DrawUtil.draw2DImage(new ResourceLocation("monsoon/characters/simon.png"), this.getX(), this.getY(), this.getWidth(), this.getHeight(), Color.WHITE);
                break;
            }
            case BARRY: {
                this.setWidth(225.0f);
                this.setHeight(226.0f);
                DrawUtil.draw2DImage(new ResourceLocation("monsoon/characters/barry.png"), this.getX(), this.getY(), this.getWidth(), this.getHeight(), Color.WHITE);
                break;
            }
            case BLAHAJ: {
                this.setWidth(175.0f);
                this.setHeight(250.0f);
                DrawUtil.draw2DImage(new ResourceLocation("monsoon/characters/blahaj.png"), this.getX(), this.getY(), this.getWidth(), this.getHeight(), Color.WHITE);
                break;
            }
            case TEE_GRIZZLY: {
                this.setWidth(175.0f);
                this.setHeight(250.0f);
                DrawUtil.draw2DImage(new ResourceLocation("monsoon/characters/retard.png"), this.getX(), this.getY(), this.getWidth(), this.getHeight(), Color.WHITE);
                break;
            }
            case KOBLEY: {
                this.setWidth(240.0f);
                this.setHeight(200.0f);
                DrawUtil.draw2DImage(new ResourceLocation("monsoon/characters/kobley.png"), this.getX(), this.getY(), this.getWidth(), this.getHeight(), Color.WHITE);
                break;
            }
            case CONFIG_ISSUE: {
                this.setWidth(172.5f);
                this.setHeight(250.0f);
                DrawUtil.draw2DImage(this.configIssueGif.getTexture(), this.getX(), this.getY(), this.getWidth(), this.getHeight(), Color.WHITE);
                this.configIssueGif.update();
                break;
            }
            case MR_WOOD: {
                this.setWidth(172.5f);
                this.setHeight(250.0f);
                DrawUtil.draw2DImage(new ResourceLocation("monsoon/characters/wood.png"), this.getX(), this.getY(), this.getWidth(), this.getHeight(), Color.WHITE);
                break;
            }
            case SKEPPY: {
                this.setWidth(260.0f);
                this.setHeight(140.0f);
                DrawUtil.draw2DImage(new ResourceLocation("monsoon/characters/nathan.png"), this.getX(), this.getY(), this.getWidth(), this.getHeight(), Color.WHITE);
                break;
            }
            case HAIKU: {
                this.setWidth(175.0f);
                this.setHeight(250.0f);
                DrawUtil.draw2DImage(new ResourceLocation("monsoon/characters/haiku.png"), this.getX(), this.getY(), this.getWidth(), this.getHeight(), Color.WHITE);
            }
        }
    }

    public void drag(float mx, float my) {
        if (!this.dragging && Mouse.isButtonDown((int)0)) {
            this.dragging = false;
        }
        if (this.dragging) {
            this.setX(mx + this.dragX);
            this.setY(my + this.dragY);
            if (!Mouse.isButtonDown((int)0)) {
                this.dragging = false;
            }
        }
        if (this.isHovered(mx, my) && Mouse.isButtonDown((int)0) && !this.dragging) {
            this.dragX = this.getX() - mx;
            this.dragY = this.getY() - my;
            this.dragging = true;
        }
    }

    public void onClick(int mouseX, int mouseY, int mouseButton, GuiScreen guiScreen) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        float scaleFactor = 1.0f / ((float)sr.getScaleFactor() * 0.5f);
        if (this.isValidGuiScreen(guiScreen) && mouseButton == 0 && this.isHovered(mouseX, mouseY)) {
            this.dragging = true;
        }
    }

    private void updateWidthHeight(boolean updateXY, CharacterRenderer.Image image) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        switch (image) {
            case ASTOLFO: 
            case FELIX: 
            case SIMON: 
            case TEE_GRIZZLY: {
                if (updateXY) {
                    this.setX((float)sr.getScaledWidth() - 185.0f);
                    this.setY((float)sr.getScaledHeight() - 250.0f);
                }
                this.setWidth(175.0f);
                this.setHeight(250.0f);
                break;
            }
            case BLAHAJ: 
            case HAIKU: {
                if (updateXY) {
                    this.setX((float)sr.getScaledWidth() - 185.0f);
                    this.setY((float)sr.getScaledHeight() - 260.0f);
                }
                this.setWidth(175.0f);
                this.setHeight(250.0f);
                break;
            }
            case KOBLEY: {
                if (updateXY) {
                    this.setX((float)sr.getScaledWidth() - 250.0f);
                    this.setY((float)sr.getScaledHeight() - 210.0f);
                }
                this.setWidth(240.0f);
                this.setHeight(200.0f);
                break;
            }
            case CONFIG_ISSUE: 
            case MR_WOOD: {
                if (updateXY) {
                    this.setX((float)sr.getScaledWidth() - 182.5f);
                    this.setY((float)sr.getScaledHeight() - 260.0f);
                }
                this.setWidth(172.5f);
                this.setHeight(250.0f);
                break;
            }
            case BARRY: {
                if (updateXY) {
                    this.setX((float)sr.getScaledWidth() - 235.0f);
                    this.setY((float)sr.getScaledHeight() - 236.0f);
                }
                this.setWidth(225.0f);
                this.setHeight(226.0f);
                break;
            }
            case SKEPPY: {
                if (updateXY) {
                    this.setX((float)sr.getScaledWidth() - 260.0f);
                    this.setY((float)sr.getScaledHeight() - 140.0f);
                }
                this.setWidth(260.0f);
                this.setHeight(140.0f);
            }
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state, GuiScreen guiScreen) {
        this.dragging = false;
    }

    public boolean isHovered(float mouseX, float mouseY) {
        return mouseX >= this.getX() && mouseY >= this.getY() && mouseX <= this.getX() + this.getWidth() && mouseY <= this.getY() + this.getHeight();
    }

    private boolean isValidGuiScreen(GuiScreen guiScreen) {
        if (guiScreen == null) {
            return false;
        }
        if (!this.configModule.isEnabled()) {
            return false;
        }
        if (Minecraft.getMinecraft().thePlayer == null || Minecraft.getMinecraft().theWorld == null) {
            return false;
        }
        if (Minecraft.getMinecraft().thePlayer.ticksExisted < 20) {
            return false;
        }
        if (!Minecraft.getMinecraft().getNetHandler().doneLoadingTerrain) {
            return false;
        }
        if (guiScreen instanceof GuiChat) {
            return false;
        }
        return guiScreen instanceof ScalableScreen || this.configModule.renderInMinecraftGuis();
    }

    public boolean isDragging() {
        return this.dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public float getDragX() {
        return this.dragX;
    }

    public float getDragY() {
        return this.dragY;
    }

    public void setDragX(float dragX) {
        this.dragX = dragX;
    }

    public void setDragY(float dragY) {
        this.dragY = dragY;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public Animation getToggleHudAnimation() {
        return this.toggleHudAnimation;
    }
}

