/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 */
package wtf.monsoon.impl.ui.panel.elements;

import java.awt.Color;
import java.util.ArrayList;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.util.MathHelper;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.module.visual.ClickGUI;
import wtf.monsoon.impl.ui.panel.elements.ElementModule;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;

public class Panel
extends Drawable {
    private final Category category;
    private final Animation expandAnimation = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.CUBIC_IN_OUT);
    private final ArrayList<ElementModule> modules = new ArrayList();
    private float scissorHeight = 0.0f;
    private float scroll = 0.0f;
    private float real = 0.0f;
    private final Animation scrollAnimation = new Animation(() -> Float.valueOf(400.0f), false, () -> Easing.CIRC_OUT);
    private int categorySize;

    public Panel(Category category, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.category = category;
        this.loadModules();
        this.expandAnimation.setState(true);
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        float moduleHeight = 0.0f;
        if (Wrapper.getMonsoon().getModuleManager().getModulesByCategory(this.category).size() != this.categorySize) {
            this.loadModules();
        }
        for (ElementModule module : this.modules) {
            moduleHeight += module.getOffset();
        }
        this.scissorHeight = MathHelper.clamp_float(moduleHeight, 0.0f, 340.0f);
        if (mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() + this.getHeight() && mouseY <= this.getY() + this.getHeight() + this.scissorHeight) {
            this.real = (float)((double)this.real + (double)mouseDelta * Wrapper.getModule(ClickGUI.class).scrollSpeed.getValue());
        }
        this.scrollAnimation.setState(this.scroll != this.real);
        if (this.scroll > this.real) {
            float scrollDiff = this.scroll - this.real;
            this.scroll = (float)((double)this.scroll - (double)scrollDiff / Wrapper.getModule(ClickGUI.class).scrollDivider.getValue() * this.scrollAnimation.getAnimationFactor());
        }
        if (this.scroll < this.real) {
            float scrollDiff = this.real - this.scroll;
            this.scroll = (float)((double)this.scroll + (double)scrollDiff / Wrapper.getModule(ClickGUI.class).scrollDivider.getValue() * this.scrollAnimation.getAnimationFactor());
        }
        this.scroll = MathHelper.clamp_float(this.scroll, -Math.max(0.0f, moduleHeight - this.scissorHeight), 0.0f);
        this.real = MathHelper.clamp_float(this.real, -Math.max(0.0f, moduleHeight - this.scissorHeight), 0.0f);
        RoundedUtils.shadow(this.getX(), this.getY(), this.getWidth(), (float)((double)this.getHeight() + (double)this.scissorHeight * this.expandAnimation.getAnimationFactor()) - 0.5f, 5.0f, 22.0f, Color.BLACK);
        RenderUtil.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), new Color(0x131313));
        Wrapper.getFontUtil().productSansSmall.drawCenteredString(StringUtil.formatEnum(this.category), this.getX() + this.getWidth() / 2.0f, this.getY() + this.getHeight() / 2.0f - (float)Wrapper.getFont().getHeight() / 2.0f, Color.WHITE, false);
        RenderUtil.pushScissor(this.getX(), this.getY() + this.getHeight(), this.getWidth(), (double)this.scissorHeight * this.expandAnimation.getAnimationFactor());
        float moduleOffset = this.getY() + this.getHeight() + this.scroll;
        for (ElementModule moduleElement : this.modules) {
            moduleElement.setX(this.getX());
            moduleElement.setY(moduleOffset);
            moduleElement.draw(mouseX, mouseY, mouseDelta);
            moduleOffset += moduleElement.getOffset();
        }
        RenderUtil.popScissor();
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (this.hovered(mouseX, mouseY) && click.equals((Object)Click.RIGHT)) {
            this.expandAnimation.setState(!this.expandAnimation.getState());
        }
        if (this.expandAnimation.getState() && mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() + this.getHeight() && mouseY <= this.getY() + this.getHeight() + this.scissorHeight) {
            for (ElementModule module : this.modules) {
                module.mouseClicked(mouseX, mouseY, click);
            }
        }
        return false;
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, Click click) {
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        for (ElementModule module : this.modules) {
            module.keyTyped(typedChar, keyCode);
        }
    }

    private void loadModules() {
        this.modules.clear();
        this.categorySize = Wrapper.getMonsoon().getModuleManager().getModulesByCategory(this.category).size();
        for (Module module : Wrapper.getMonsoon().getModuleManager().getModulesByCategory(this.category)) {
            this.modules.add(new ElementModule(module, -2000.0f, -2000.0f, this.getWidth(), this.getHeight()));
        }
    }
}

