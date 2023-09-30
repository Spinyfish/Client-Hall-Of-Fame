/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 */
package wtf.monsoon.impl.ui.windowgui.drawables;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.util.MathHelper;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.util.misc.MathUtils;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;
import wtf.monsoon.impl.ui.windowgui.WindowGUI;
import wtf.monsoon.impl.ui.windowgui.drawables.ModuleDrawable;
import wtf.monsoon.impl.ui.windowgui.window.Window;

public class CategoryDrawable
extends Drawable {
    private final Window parent;
    private final Category category;
    private float scroll;
    private final Animation hover = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.LINEAR);
    private final Animation selected = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.LINEAR);
    private final List<ModuleDrawable> moduleDrawables = new ArrayList<ModuleDrawable>();

    public CategoryDrawable(Window parent, Category category, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.parent = parent;
        this.category = category;
        Wrapper.getMonsoon().getModuleManager().getModulesByCategory(category).forEach(module -> this.moduleDrawables.add(new ModuleDrawable(this, (Module)module, this.getX(), this.getY(), (this.getParent().getWidth() - 90.0f) / 3.0f - 4.0f, 30.0f)));
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        this.hover.setState(MathUtils.within(this.getX(), this.getY(), this.getWidth(), this.getHeight(), mouseX, mouseY));
        this.selected.setState(this.parent.getSelected() == this);
        RenderUtil.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight() + 1.5f, this.selected.getState() ? ColorUtil.interpolate(ColorUtil.interpolate(WindowGUI.INTERACTABLE, ColorUtil.fadeBetween(10, 270, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)), this.selected.getAnimationFactor()), ColorUtil.interpolate(WindowGUI.HOVER, ColorUtil.fadeBetween(10, 270, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)).darker(), this.selected.getAnimationFactor()), this.hover.getAnimationFactor()) : WindowGUI.INTERACTABLE);
        Wrapper.getFontUtil().greycliff26.drawString(StringUtil.formatEnum(this.category), this.getX() + 5.0f, this.getY() + 2.0f, Color.WHITE, false);
        if (this.selected.getState()) {
            float leftTotal = 0.0f;
            for (Object moduleDrawable2 : this.moduleDrawables.stream().filter(moduleDrawable -> moduleDrawable.getColumn() != null && moduleDrawable.getColumn().equals((Object)Column.LEFT)).collect(Collectors.toList())) {
                leftTotal += ((Drawable)moduleDrawable2).getHeight() + ((ModuleDrawable)moduleDrawable2).getOffset() + 2.0f;
            }
            float middleTotal = 0.0f;
            for (Object moduleDrawable3 : this.moduleDrawables.stream().filter(moduleDrawable -> moduleDrawable.getColumn() != null && moduleDrawable.getColumn().equals((Object)Column.MIDDLE)).collect(Collectors.toList())) {
                middleTotal += ((Drawable)moduleDrawable3).getHeight() + ((ModuleDrawable)moduleDrawable3).getOffset() + 2.0f;
            }
            float rightTotal = 0.0f;
            for (ModuleDrawable moduleDrawable4 : this.moduleDrawables.stream().filter(moduleDrawable -> moduleDrawable.getColumn() != null && moduleDrawable.getColumn().equals((Object)Column.RIGHT)).collect(Collectors.toList())) {
                rightTotal += moduleDrawable4.getHeight() + moduleDrawable4.getOffset() + 2.0f;
            }
            float longest = leftTotal;
            if (middleTotal > longest) {
                longest = middleTotal;
            }
            if (rightTotal > longest) {
                longest = rightTotal;
            }
            this.scroll = (float)((double)this.scroll + (double)mouseDelta * 0.5);
            this.scroll = MathHelper.clamp_float(this.scroll, -Math.max(0.0f, longest - (this.getParent().getHeight() - 87.0f)), 0.0f);
            float x = this.getParent().getX() + 90.0f;
            Column column = Column.LEFT;
            float leftY = this.getParent().getY() + 29.0f + this.scroll;
            float middleY = this.getParent().getY() + 29.0f + this.scroll;
            float rightY = this.getParent().getY() + 29.0f + this.scroll;
            RenderUtil.pushScissor(x, this.getParent().getY() + 26.0f, this.getParent().getWidth() - 90.0f, this.getParent().getHeight() - 81.0f);
            for (ModuleDrawable moduleDrawable5 : this.moduleDrawables) {
                moduleDrawable5.setColumn(column);
                switch (column) {
                    case LEFT: {
                        moduleDrawable5.setX(x);
                        x += (this.getParent().getWidth() - 98.0f) / 3.0f;
                        moduleDrawable5.setY(leftY);
                        leftY += moduleDrawable5.getHeight() + moduleDrawable5.getOffset() + 2.0f;
                        column = Column.MIDDLE;
                        break;
                    }
                    case MIDDLE: {
                        moduleDrawable5.setX(x);
                        x += (this.getParent().getWidth() - 98.0f) / 3.0f;
                        moduleDrawable5.setY(middleY);
                        middleY += moduleDrawable5.getHeight() + moduleDrawable5.getOffset() + 2.0f;
                        column = Column.RIGHT;
                        break;
                    }
                    case RIGHT: {
                        moduleDrawable5.setX(x);
                        x = this.getParent().getX() + 90.0f;
                        moduleDrawable5.setY(rightY);
                        rightY += moduleDrawable5.getHeight() + moduleDrawable5.getOffset() + 2.0f;
                        column = Column.LEFT;
                    }
                }
                moduleDrawable5.draw(mouseX, mouseY, mouseDelta);
            }
            RenderUtil.popScissor();
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (this.hover.getState()) {
            this.parent.setSelected(this);
        }
        if (MathUtils.within(this.getParent().getX() + 85.0f, this.getParent().getY() + 25.0f, this.getParent().getWidth() - 90.0f, this.getParent().getHeight() - 80.0f, mouseX, mouseY)) {
            this.moduleDrawables.forEach(moduleDrawable -> moduleDrawable.mouseClicked(mouseX, mouseY, click));
        }
        return false;
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, Click click) {
        this.moduleDrawables.forEach(moduleDrawable -> moduleDrawable.mouseReleased(mouseX, mouseY, click));
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        this.moduleDrawables.forEach(moduleDrawable -> moduleDrawable.keyTyped(typedChar, keyCode));
    }

    public Window getParent() {
        return this.parent;
    }

    public Category getCategory() {
        return this.category;
    }

    public float getScroll() {
        return this.scroll;
    }

    public static enum Column {
        LEFT,
        MIDDLE,
        RIGHT;

    }
}

