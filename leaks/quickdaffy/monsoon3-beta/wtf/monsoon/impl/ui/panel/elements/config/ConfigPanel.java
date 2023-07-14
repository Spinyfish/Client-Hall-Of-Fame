/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 *  org.lwjgl.input.Keyboard
 */
package wtf.monsoon.impl.ui.panel.elements.config;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.misc.MathUtils;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.panel.elements.config.ConfigPane;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;

public class ConfigPanel
extends Drawable {
    private final Animation panelToggleAnim = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.SINE_IN_OUT);
    private final CopyOnWriteArrayList<ConfigPane> panes = new CopyOnWriteArrayList();
    private final Animation sizeAnimation = new Animation(200.0f, false, () -> Easing.CUBIC_IN_OUT);
    private final NameField field = new NameField(this.getX(), this.getY(), this.getWidth() / 2.0f - 5.0f, 8.0f);
    private final Button save = new Button("Save", () -> {
        if (!this.field.text.isEmpty()) {
            Wrapper.getMonsoon().getConfigSystem().save(this.field.getText());
            this.field.text = "";
            for (ConfigPane pane : this.panes) {
                if (!pane.getConfig().getPath().contains("monsoon/configs/" + this.field.getText() + ".json")) continue;
                return;
            }
            this.panes.add(new ConfigPane(this, new File("monsoon/configs/" + this.field.getText() + ".json"), this.getX(), this.getY(), 70.0f, 30.0f));
        }
    }, this.getX(), this.getY(), this.getWidth() / 2.0f - 5.0f, 8.0f, false);
    private float scroll = 0.0f;

    public ConfigPanel(float x, float y, float width, float height) {
        super(x, y, width, height);
        for (File config : Wrapper.getMonsoon().getConfigSystem().getDirectory("configs").listFiles()) {
            if (!config.isFile() || !config.getName().endsWith(".json")) continue;
            this.panes.add(new ConfigPane(this, config, this.getX(), this.getY(), 70.0f, 30.0f));
        }
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        RenderUtil.scaleXY(this.getX() + this.getWidth() / 2.0f, this.getY() + this.getHeight() / 2.0f, this.panelToggleAnim, () -> {
            GlStateManager.pushMatrix();
            this.render(mouseX, mouseY, mouseDelta);
            GlStateManager.popMatrix();
        });
    }

    public void render(float mouseX, float mouseY, int mouseDelta) {
        RoundedUtils.shadow(this.getX() + 1.0f, this.getY() + 1.0f, this.getWidth() - 2.0f, this.getHeight() - 2.0f, 6.0f, 15.0f, Color.BLACK);
        RoundedUtils.shadow(this.getX() + 1.0f, this.getY() + 1.0f, this.getWidth() - 2.0f, this.getHeight() - 2.0f, 6.0f, 10.0f, Color.BLACK);
        RoundedUtils.round(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 6.0f, new Color(0x131313));
        RoundedUtils.round(this.getX() + 2.0f, this.getY() + 15.0f, this.getWidth() - 4.0f, this.getHeight() - 17.0f, 4.0f, new Color(0x1F1F1F));
        Wrapper.getFont().drawCenteredString("Configs", this.getX() + this.getWidth() / 2.0f, this.getY() + 2.0f, Color.WHITE, false);
        this.scroll = (float)((double)this.scroll + (double)mouseDelta * 0.1);
        this.scroll = MathHelper.clamp_float(this.scroll, -Math.max(0.0f, this.getPaneHeight() - (this.getHeight() - 20.0f)), 0.0f);
        RenderUtil.pushScissor(this.getX(), this.getY() + 16.0f, this.getWidth(), this.getHeight() - 18.0f);
        float y = this.getY() + 23.0f + this.scroll;
        for (ConfigPane pane : this.panes) {
            pane.setX(this.getX() + this.getWidth() / 2.0f - pane.getWidth() / 2.0f);
            pane.setY(y);
            pane.draw(mouseX, mouseY, mouseDelta);
            y = (float)((double)y + (double)(pane.getHeight() + 2.0f) * pane.getDeleteScaleDown().getAnimationFactor());
        }
        RenderUtil.popScissor();
        this.field.setX(this.getX() + (this.getWidth() / 2.0f - this.field.getWidth()));
        this.field.setY(this.getY() + this.getHeight() - 13.0f);
        this.save.setX(this.getX() + this.getWidth() / 2.0f);
        this.save.setY(this.getY() + this.getHeight() - 13.0f);
        this.field.draw(mouseX, mouseY, mouseDelta);
        this.save.draw(mouseX, mouseY, mouseDelta);
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (this.panelToggleAnim.getState()) {
            this.panes.forEach(pane -> pane.mouseClicked(mouseX, mouseY, click));
        }
        this.save.mouseClicked(mouseX, mouseY, click);
        this.field.mouseClicked(mouseX, mouseY, click);
        return false;
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, Click click) {
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        this.field.keyTyped(typedChar, keyCode);
    }

    private float getPaneHeight() {
        float paneHeight = 11.0f;
        for (ConfigPane pane : this.panes) {
            paneHeight = (float)((double)paneHeight + (double)(pane.getHeight() + 2.0f) * pane.getDeleteScaleDown().getAnimationFactor());
        }
        return paneHeight;
    }

    @Override
    public float getHeight() {
        return MathHelper.clamp_float(20.0f + this.getPaneHeight(), 0.0f, 180.0f) + 10.0f;
    }

    public boolean dragHovered(float mouseX, float mouseY) {
        return MathUtils.within(this.getX(), this.getY(), this.getWidth(), this.getHeight(), mouseX, mouseY) && this.panelToggleAnim.getState();
    }

    public Animation getPanelToggleAnim() {
        return this.panelToggleAnim;
    }

    public CopyOnWriteArrayList<ConfigPane> getPanes() {
        return this.panes;
    }

    public Animation getSizeAnimation() {
        return this.sizeAnimation;
    }

    public NameField getField() {
        return this.field;
    }

    public Button getSave() {
        return this.save;
    }

    public static class NameField
    extends Drawable {
        private String text = "";
        private final Animation hover = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.LINEAR);
        private boolean listening;

        public NameField(float x, float y, float width, float height) {
            super(x, y, width, height);
        }

        @Override
        public void draw(float mouseX, float mouseY, int mouseDelta) {
            this.hover.setState(this.hovered(mouseX, mouseY));
            Keyboard.enableRepeatEvents((boolean)true);
            RoundedUtils.round(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 2.0f, ColorUtil.interpolate(new Color(0x343434), new Color(0x444444), this.hover.getAnimationFactor()));
            if (!this.text.isEmpty() || this.listening) {
                Wrapper.getFontUtil().productSansSmaller.drawString(this.text + (this.listening ? "_" : ""), this.getX() + 2.0f, this.getY(), Color.WHITE, false);
            } else {
                Wrapper.getFontUtil().productSansSmaller.drawString("Name", this.getX() + 2.0f, this.getY(), Color.GRAY, false);
            }
        }

        @Override
        public boolean mouseClicked(float mouseX, float mouseY, Click click) {
            this.listening = this.hovered(mouseX, mouseY) ? !this.listening : false;
            return false;
        }

        @Override
        public void mouseReleased(float mouseX, float mouseY, Click click) {
        }

        @Override
        public void keyTyped(char typedChar, int keyCode) {
            if (this.listening) {
                if (keyCode == 28) {
                    this.listening = false;
                } else if (this.listening && Keyboard.isKeyDown((int)14)) {
                    if (!this.text.isEmpty()) {
                        this.text = this.text.substring(0, this.text.length() - 1);
                    }
                } else if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                    this.text = this.text + typedChar;
                }
                if (GuiScreen.isCtrlKeyDown() && keyCode == 47) {
                    try {
                        this.text = (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                    }
                    catch (UnsupportedFlavorException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public String getText() {
            return this.text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class Button
    extends Drawable {
        private final String text;
        private final Animation hover = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.LINEAR);
        private final Runnable clicked;
        private final boolean disabled;

        public Button(String text, Runnable clicked, float x, float y, float width, float height, boolean disabled) {
            super(x, y, width, height);
            this.clicked = clicked;
            this.text = text;
            this.disabled = disabled;
        }

        @Override
        public void draw(float mouseX, float mouseY, int mouseDelta) {
            this.hover.setState(this.hovered(mouseX, mouseY));
            RoundedUtils.round(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 2.0f, this.disabled ? new Color(0x212121) : ColorUtil.interpolate(new Color(0x343434), new Color(0x444444), this.hover.getAnimationFactor()));
            Wrapper.getFontUtil().productSansSmaller.drawCenteredString(this.text, this.getX() + this.getWidth() / 2.0f, this.getY(), Color.WHITE, false);
        }

        @Override
        public boolean mouseClicked(float mouseX, float mouseY, Click click) {
            if (this.hovered(mouseX, mouseY)) {
                this.clicked.run();
            }
            return false;
        }

        @Override
        public void mouseReleased(float mouseX, float mouseY, Click click) {
        }

        @Override
        public void keyTyped(char typedChar, int keyCode) {
        }
    }
}

