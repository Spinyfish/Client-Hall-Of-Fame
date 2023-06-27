package rip.athena.client.gui.clickgui.components.fps;

import rip.athena.client.gui.framework.components.*;
import net.minecraft.util.*;
import rip.athena.client.gui.framework.draw.*;
import java.awt.image.*;
import java.awt.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.*;

public class MenuImageButton extends MenuButton
{
    private static Color DISABLED_COLOR;
    private static Color OVER_COLOR;
    private static Color ACTIVE_COLOR;
    private static Color ACTIVE_HOVER_COLOR;
    private String imageId;
    private ResourceLocation disabledImage;
    private ResourceLocation normalImage;
    private ResourceLocation hoverImage;
    private ResourceLocation hoverActiveImage;
    private ResourceLocation activeImage;
    
    public MenuImageButton(final int x, final int y, final int width, final int height, final BufferedImage image) {
        super("", x, y, width, height);
        this.imageId = image.toString() + ":image-button";
        this.loadImage(image);
    }
    
    @Override
    public void onPreSort() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int width = (this.width == -1 && this.height == -1) ? (this.getStringWidth(this.text) + this.minOffset * 2) : this.width;
        final int height = (this.width == -1 && this.height == -1) ? (this.getStringHeight(this.text) + this.minOffset * 2) : this.height;
        final int mouseX = this.parent.getMouseX();
        final int mouseY = this.parent.getMouseY();
        ButtonState state = this.active ? ButtonState.ACTIVE : ButtonState.NORMAL;
        if (!this.disabled) {
            if (mouseX >= x && mouseX <= x + width - 1 && mouseY >= y && mouseY <= y + height - 1) {
                state = ButtonState.HOVER;
                if (this.mouseDown) {
                    this.active = !this.active;
                    this.onAction();
                }
            }
        }
        else {
            state = ButtonState.DISABLED;
        }
        this.lastState = state;
    }
    
    public String getImageId() {
        return this.imageId;
    }
    
    @Override
    public void onRender() {
        if (this.isActive() && this.lastState == ButtonState.HOVER) {
            this.lastState = ButtonState.HOVERACTIVE;
        }
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        this.drawImage(this.getImage(), x, y, this.getWidth(), this.getHeight());
        this.mouseDown = false;
    }
    
    private void loadImage(final BufferedImage image) {
        this.loadDisabled(image);
        this.loadNormal(image);
        this.loadHover(image);
        this.loadActive(image);
        this.loadActiveHover(image);
    }
    
    private ResourceLocation getImage() {
        switch (this.lastState) {
            case ACTIVE: {
                return this.activeImage;
            }
            case DISABLED: {
                return this.disabledImage;
            }
            case HOVER: {
                return this.hoverImage;
            }
            case HOVERACTIVE: {
                return this.hoverActiveImage;
            }
            case NORMAL: {
                return this.normalImage;
            }
            case POPUP: {
                return this.normalImage;
            }
            default: {
                return this.normalImage;
            }
        }
    }
    
    private void loadDisabled(final BufferedImage image) {
        this.disabledImage = this.colorImage(image, MenuImageButton.DISABLED_COLOR, ButtonState.DISABLED);
    }
    
    private void loadNormal(final BufferedImage image) {
        this.normalImage = this.getResourceLocationFromImage(image, ButtonState.NORMAL);
    }
    
    private void loadHover(final BufferedImage image) {
        this.hoverImage = this.colorImage(image, MenuImageButton.OVER_COLOR, ButtonState.HOVER);
    }
    
    private void loadActive(final BufferedImage image) {
        this.activeImage = this.colorImage(image, MenuImageButton.ACTIVE_COLOR, ButtonState.ACTIVE);
    }
    
    private void loadActiveHover(final BufferedImage image) {
        this.hoverActiveImage = this.colorImage(image, MenuImageButton.ACTIVE_HOVER_COLOR, ButtonState.HOVERACTIVE);
    }
    
    private ResourceLocation colorImage(final BufferedImage src, final Color color, final ButtonState state) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        final BufferedImage tinted = new BufferedImage(width, height, 2);
        final Graphics2D graphics = tinted.createGraphics();
        final RescaleOp rop = new RescaleOp(new float[] { color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getRed() / 255.0f, color.getAlpha() / 255.0f }, new float[] { 0.0f, 0.0f, 0.0f, 0.0f }, null);
        graphics.drawImage(src, rop, 0, 0);
        return this.getResourceLocationFromImage(tinted, state);
    }
    
    private ResourceLocation getResourceLocationFromImage(final BufferedImage image, final ButtonState state) {
        final DynamicTexture dynamicTexture = new DynamicTexture(image);
        return Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation(this.imageId + ":" + state.toString(), dynamicTexture);
    }
    
    static {
        MenuImageButton.DISABLED_COLOR = new Color(255, 255, 255, 200);
        MenuImageButton.OVER_COLOR = new Color(255, 255, 255, 160);
        MenuImageButton.ACTIVE_COLOR = new Color(255, 255, 255, 180);
        MenuImageButton.ACTIVE_HOVER_COLOR = new Color(255, 255, 255, 150);
    }
}
