package me.jinthium.straight.api.dragging;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.api.util.MinecraftInstance;
import me.jinthium.straight.api.util.Util;
import me.jinthium.straight.impl.modules.visual.Hud;
import me.jinthium.straight.impl.utils.animation.Animation;
import me.jinthium.straight.impl.utils.animation.Direction;
import me.jinthium.straight.impl.utils.animation.impl.DecelerateAnimation;
import me.jinthium.straight.impl.utils.misc.HoveringUtil;
import me.jinthium.straight.impl.utils.render.ColorUtil;
import me.jinthium.straight.impl.utils.render.RoundedUtil;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.List;

public class Dragging implements MinecraftInstance, Util {
    @Expose
    @SerializedName("x")
    private float xPos;
    @Expose
    @SerializedName("y")
    private float yPos;

    public float initialXVal;
    public float initialYVal;

    private float startX, startY;
    private boolean dragging;

    private float width, height;

    @Expose
    @SerializedName("name")
    private String name;

    private final Module module;

    public Animation hoverAnimation = new DecelerateAnimation(250, 1, Direction.BACKWARDS);

    public Dragging(Module module, String name, float initialXVal, float initialYVal) {
        this.module = module;
        this.name = name;
        this.xPos = initialXVal;
        this.yPos = initialYVal;
        this.initialXVal = initialXVal;
        this.initialYVal = initialYVal;
        this.width = 100;
        this.height = 50;
    }

    public Module getModule() {
        return module;
    }

    public String getName() {
        return name;
    }

    
    public float getWidth() {
        return width;
    }

    
    public void setWidth(float width) {
        this.width = width;
    }

    
    public float getHeight() {
        return height;
    }

    
    public void setHeight(float height) {
        this.height = height;
    }

    
    public float getX() {
        return xPos;
    }

    public void setX(float x) {
        this.xPos = x;
    }

    

    public float getY() {
        return yPos;
    }

    public void setY(float y) {
        this.yPos = y;
    }


    private String longestModule;

    public final void onDraw(int mouseX, int mouseY) {
        boolean hovering = HoveringUtil.isHovering(xPos, yPos, width, height, mouseX, mouseY);
        if (dragging) {
            xPos = mouseX - startX;
            yPos = mouseY - startY;
        }
        hoverAnimation.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);
        if (!hoverAnimation.isDone() || hoverAnimation.finished(Direction.FORWARDS)) {
            RoundedUtil.drawRoundOutline(xPos - 4, yPos - 4, width + 8, height + 8, 10, 2,
                    ColorUtil.applyOpacity(Color.WHITE, 0), ColorUtil.applyOpacity(Color.WHITE, (float) hoverAnimation.getOutput().floatValue()));
        }
    }


    public final void onClick(int mouseX, int mouseY, int button) {
        boolean canDrag = HoveringUtil.isHovering(xPos, yPos, width, height, mouseX, mouseY);
        if (button == 0 && canDrag) {
            dragging = true;
            startX = (int) (mouseX - xPos);
            startY = (int) (mouseY - yPos);
        }
    }

    public final void onRelease(int button) {
        if (button == 0) dragging = false;
    }


}
