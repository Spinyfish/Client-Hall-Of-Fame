/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 */
package wtf.monsoon.impl.ui.windowgui.window;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.client.Minecraft;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.windowgui.WindowGUI;
import wtf.monsoon.impl.ui.windowgui.drawables.CategoryDrawable;

public class Window {
    private float x;
    private float y;
    private float width;
    private float height;
    private float lastX;
    private float lastY;
    private boolean dragging;
    private final List<CategoryDrawable> categoryDrawables = new ArrayList<CategoryDrawable>();
    private CategoryDrawable selected;
    private final Animation dragAnimation = new Animation(() -> Float.valueOf(400.0f), false, () -> Easing.CUBIC_IN_OUT);

    public Window(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        for (Category category : Category.values()) {
            this.categoryDrawables.add(new CategoryDrawable(this, category, this.getX(), this.getY(), 80.0f, 20.0f));
        }
        this.selected = this.categoryDrawables.get(0);
    }

    public void render(float mouseX, float mouseY, int mouseDelta) {
        if (this.dragAnimation.getState()) {
            this.setX(mouseX - this.lastX);
            this.setY(mouseY - this.lastY);
        }
        int alpha = 150;
        RoundedUtils.shadowGradient(this.getX() - 1.0f, this.getY() - 1.0f, this.getWidth() + 2.0f, this.getHeight() + 2.0f, 10.0f, 22.0f, 0.5f, ColorUtil.fadeBetween(10, 270, new Color(0, 238, 255, alpha), new Color(135, 56, 232, alpha)), ColorUtil.fadeBetween(10, 0, new Color(0, 238, 255, alpha), new Color(135, 56, 232, alpha)), ColorUtil.fadeBetween(10, 180, new Color(0, 238, 255, alpha), new Color(135, 56, 232, alpha)), ColorUtil.fadeBetween(10, 90, new Color(0, 238, 255, alpha), new Color(135, 56, 232, alpha)), false);
        RoundedUtils.outline(this.getX() - 1.0f, this.getY() - 1.0f, this.getWidth() + 2.0f, this.getHeight() + 2.0f, 10.0f, 1.0f, 1.0f, ColorUtil.fadeBetween(10, 270, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)), ColorUtil.fadeBetween(10, 0, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)), ColorUtil.fadeBetween(10, 180, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)), ColorUtil.fadeBetween(10, 90, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)));
        RoundedUtils.round(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 10.0f, WindowGUI.BACKGROUND);
        Wrapper.getFontUtil().greycliff40.drawStringWithGradient("Monsoon", this.getX() + 7.0f, this.getY() + 1.0f, ColorUtil.fadeBetween(10, 270, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)), ColorUtil.fadeBetween(10, 0, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)), false);
        Wrapper.getFont().drawString(Wrapper.getMonsoon().getVersion(), this.getX() + 88.0f, this.getY() + 2.0f, Color.WHITE, false);
        RoundedUtils.round(this.getX(), this.getY() + 25.0f, 80.0f, this.getHeight() - 80.0f, 5.0f, WindowGUI.LAYER_ONE);
        RenderUtil.drawRect((double)this.getX() + 0.5, (double)this.getY() + 25.5, 20.0, this.getHeight() - 81.0f, WindowGUI.LAYER_ONE.getRGB());
        RoundedUtils.round(this.getX() + 85.0f, this.getY() + 25.0f, this.getWidth() - 90.0f, this.getHeight() - 80.0f, 5.0f, WindowGUI.LAYER_ONE);
        float categoryY = this.getY() + 25.0f;
        for (CategoryDrawable categoryDrawable : this.categoryDrawables) {
            categoryDrawable.setX(this.getX());
            categoryDrawable.setY(categoryY);
            categoryDrawable.draw(mouseX, mouseY, mouseDelta);
            categoryY += categoryDrawable.getHeight();
        }
        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(mc.thePlayer.getLocationSkin());
        mc.currentScreen.drawTexturedModalRect(this.getX() + 12.0f, this.getY() + this.getHeight() - 44.0f, 32, 32, 32, 32);
        Wrapper.getFont().drawString(mc.getSession().getUsername(), this.getX() + 48.0f, this.getY() + this.getHeight() - 44.0f, Color.WHITE, false);
        RoundedUtils.round(this.getX() + 48.0f, this.getY() + this.getHeight() - 31.0f, 110.0f, 8.0f, 3.0f, WindowGUI.INTERACTABLE.darker());
        RoundedUtils.round(this.getX() + 48.0f, this.getY() + this.getHeight() - 31.0f, mc.thePlayer.getHealth() / mc.thePlayer.getMaxHealth() * 100.0f + mc.thePlayer.getAbsorptionAmount() / 4.0f * 10.0f, 8.0f, 3.0f, new Color(250, 250, 0));
        RoundedUtils.round(this.getX() + 48.0f, this.getY() + this.getHeight() - 31.0f, mc.thePlayer.getHealth() / mc.thePlayer.getMaxHealth() * 100.0f, 8.0f, 3.0f, Wrapper.getPallet().getMain());
        Wrapper.getFont().drawString(mc.thePlayer.getHealth() + mc.thePlayer.getAbsorptionAmount() + " / " + (mc.thePlayer.getMaxHealth() + 4.0f), this.getX() + 48.0f, this.getY() + this.getHeight() - 22.0f, Color.WHITE, false);
    }

    public void mouseClicked(float mouseX, float mouseY, Click click) {
        if (mouseX >= this.getX() && mouseY >= this.getY() && mouseX <= this.getX() + this.getWidth() && mouseY <= this.getY() + 25.0f) {
            this.lastX = mouseX - this.getX();
            this.lastY = mouseY - this.getY();
            this.dragAnimation.setState(true);
        }
        this.getCategoryDrawables().forEach(categoryDrawable -> categoryDrawable.mouseClicked(mouseX, mouseY, click));
    }

    public void mouseReleased(int mouseX, int mouseY, Click click) {
        this.dragAnimation.setState(false);
        this.getCategoryDrawables().forEach(categoryDrawable -> categoryDrawable.mouseReleased(mouseX, mouseY, click));
    }

    public void keyTyped(char typedChar, int keyCode) {
        this.getCategoryDrawables().forEach(categoryDrawable -> categoryDrawable.keyTyped(typedChar, keyCode));
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

    public float getLastX() {
        return this.lastX;
    }

    public float getLastY() {
        return this.lastY;
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

    public void setLastX(float lastX) {
        this.lastX = lastX;
    }

    public void setLastY(float lastY) {
        this.lastY = lastY;
    }

    public boolean isDragging() {
        return this.dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public List<CategoryDrawable> getCategoryDrawables() {
        return this.categoryDrawables;
    }

    public CategoryDrawable getSelected() {
        return this.selected;
    }

    public void setSelected(CategoryDrawable selected) {
        this.selected = selected;
    }
}

