/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.ColourAnimation
 *  me.surge.animation.Easing
 */
package wtf.monsoon.api.module;

import java.awt.Color;
import me.surge.animation.Animation;
import me.surge.animation.ColourAnimation;
import me.surge.animation.Easing;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.util.render.ColorUtil;

public abstract class HUDModule
extends Module {
    private float x;
    private float defaultX;
    private float y;
    private float defaultY;
    private float width;
    private float height;
    private final Animation hoverAnimation = new Animation(() -> Float.valueOf(300.0f), false, () -> Easing.SINE_IN_OUT);
    private final ColourAnimation linearHoverAnimation = new ColourAnimation(ColorUtil.TRANSPARENT, Color.WHITE, () -> Float.valueOf(300.0f), false, () -> Easing.LINEAR);

    public HUDModule(String name, String description) {
        super(name, description, Category.HUD);
        this.x = 30.0f;
        this.y = 30.0f;
        this.defaultX = 30.0f;
        this.defaultY = 30.0f;
    }

    public HUDModule(String name, String description, float x, float y) {
        super(name, description, Category.HUD);
        this.x = x;
        this.y = y;
        this.defaultX = x;
        this.defaultY = y;
    }

    public abstract void render();

    public abstract void blur();

    public boolean hovered(float mouseX, float mouseY) {
        return mouseX >= this.getX() && mouseY >= this.getY() && mouseX <= this.getX() + this.getWidth() && mouseY <= this.getY() + this.getHeight();
    }

    public float getX() {
        return this.x;
    }

    public float getDefaultX() {
        return this.defaultX;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setDefaultX(float defaultX) {
        this.defaultX = defaultX;
    }

    public float getY() {
        return this.y;
    }

    public float getDefaultY() {
        return this.defaultY;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setDefaultY(float defaultY) {
        this.defaultY = defaultY;
    }

    public float getWidth() {
        return this.width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public Animation getHoverAnimation() {
        return this.hoverAnimation;
    }

    public ColourAnimation getLinearHoverAnimation() {
        return this.linearHoverAnimation;
    }
}

