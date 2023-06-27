package rip.athena.client.gui.menu.altmanager.button;

import rip.athena.client.gui.screen.*;
import java.awt.*;
import rip.athena.client.utils.animations.*;
import rip.athena.client.utils.render.*;
import rip.athena.client.utils.font.*;
import rip.athena.client.utils.animations.impl.*;

public class AltButton implements Screen
{
    private float x;
    private float y;
    private float width;
    private float height;
    private float alpha;
    private boolean bypass;
    private final String name;
    private boolean bold;
    private Color color;
    private Runnable clickAction;
    private final Animation hoverAnimation;
    
    @Override
    public void initGui() {
    }
    
    @Override
    public void keyTyped(final char typedChar, final int keyCode) {
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY) {
        final boolean hovering = HoveringUtil.isHovering(this.x, this.y, this.width, this.height, mouseX, mouseY);
        this.hoverAnimation.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);
        final Color rectColor = ColorUtil.interpolateColorC(this.color, this.color.brighter(), this.hoverAnimation.getOutput().floatValue());
        RoundedUtils.drawRound(this.x, this.y, this.width, this.height, 5.0f, ColorUtil.applyOpacity(rectColor, this.alpha));
        FontManager.getProductSansRegular(22).drawCenteredString(this.name, this.x + this.width / 2.0f, this.y + this.height - FontManager.getProductSansRegular(25).height(), ColorUtil.applyOpacity(-1, this.alpha));
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int button) {
        final boolean hovering = HoveringUtil.isHovering(this.x, this.y, this.width, this.height, mouseX, mouseY);
        if (hovering && button == 0 && this.clickAction != null) {
            this.clickAction.run();
        }
    }
    
    @Override
    public void mouseReleased(final int mouseX, final int mouseY, final int state) {
    }
    
    public void setX(final float x) {
        this.x = x;
    }
    
    public void setY(final float y) {
        this.y = y;
    }
    
    public void setWidth(final float width) {
        this.width = width;
    }
    
    public void setHeight(final float height) {
        this.height = height;
    }
    
    public void setAlpha(final float alpha) {
        this.alpha = alpha;
    }
    
    public void setBypass(final boolean bypass) {
        this.bypass = bypass;
    }
    
    public void setBold(final boolean bold) {
        this.bold = bold;
    }
    
    public void setColor(final Color color) {
        this.color = color;
    }
    
    public void setClickAction(final Runnable clickAction) {
        this.clickAction = clickAction;
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
    
    public float getAlpha() {
        return this.alpha;
    }
    
    public boolean isBypass() {
        return this.bypass;
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean isBold() {
        return this.bold;
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public Runnable getClickAction() {
        return this.clickAction;
    }
    
    public Animation getHoverAnimation() {
        return this.hoverAnimation;
    }
    
    public AltButton(final String name) {
        this.bypass = false;
        this.bold = false;
        this.color = ColorUtil.tripleColor(55);
        this.hoverAnimation = new DecelerateAnimation(250, 1.0);
        this.name = name;
    }
}
