/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Mouse
 */
package wtf.monsoon.impl.ui.panel;

import java.io.IOException;
import java.util.ArrayList;
import org.lwjgl.input.Mouse;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.opengui.TestOpenGuiScreen;
import wtf.monsoon.impl.ui.ScalableScreen;
import wtf.monsoon.impl.ui.panel.elements.Panel;
import wtf.monsoon.impl.ui.panel.elements.config.ConfigPanel;
import wtf.monsoon.impl.ui.primitive.Button;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;

public class PanelGUI
extends ScalableScreen {
    private final ArrayList<Drawable> drawables = new ArrayList();
    private Drawable draggingPanel;
    private float dragX;
    private float dragY;
    private boolean drag;

    public PanelGUI() {
        int x = 0;
        for (Category category : Category.values()) {
            this.drawables.add(new Panel(category, 20 + x, 20.0f, 90.0f, 17.0f));
            x += 100;
        }
        this.drawables.add(new Button(0.0f, 0.0f, Wrapper.getFontUtil().productSansSmallBold, "OpenGui test", 10.0f, button -> {
            if (button.equals((Object)Click.LEFT)) {
                this.mc.displayGuiScreen(new TestOpenGuiScreen());
            }
        }));
        ConfigPanel configPanel = new ConfigPanel(20.0f, 20.0f, 90.0f, 180.0f);
        this.drawables.add(configPanel);
        this.drawables.add(new Button(4.0f, this.getScaledHeight() - 24.0f, Wrapper.getFontUtil().productSansSmallBold, "Configs Window", 8.0f, button -> {
            if (button.equals((Object)Click.LEFT)) {
                ((ConfigPanel)this.drawables.get(this.drawables.size() - 2)).getPanelToggleAnim().setState(!((ConfigPanel)this.drawables.get(this.drawables.size() - 2)).getPanelToggleAnim().getState());
            }
        }));
    }

    @Override
    public void init() {
        ConfigPanel configPanel = new ConfigPanel(5.0f, this.getScaledHeight() - 200.0f, 90.0f, 180.0f);
        this.drawables.set(this.drawables.size() - 2, configPanel);
        this.drawables.get(this.drawables.size() - 1).setY(this.getScaledHeight() - 20.0f);
    }

    @Override
    public void render(float mouseX, float mouseY) {
        if (this.draggingPanel != null) {
            this.drag(this.draggingPanel, mouseX, mouseY);
        }
        int mouseDelta = Mouse.getDWheel();
        this.drawDefaultBackground();
        Wrapper.getMonsoon().getCharacterManager().draw((int)mouseX, (int)mouseY, this.mc.thePlayer.ticksExisted, this);
        this.drawables.forEach(drawable -> drawable.draw(mouseX, mouseY, mouseDelta));
    }

    @Override
    public void click(float mouseX, float mouseY, int mouseButton) {
        Wrapper.getMonsoon().getCharacterManager().onClick((int)mouseX, (int)mouseY, mouseButton, this);
        for (Drawable drawable : this.drawables) {
            if (drawable instanceof ConfigPanel && mouseButton == 0 && ((ConfigPanel)drawable).dragHovered(mouseX, mouseY)) {
                this.draggingPanel = drawable;
            } else if (mouseButton == 0 && drawable.hovered(mouseX, mouseY)) {
                this.draggingPanel = drawable;
            }
            if (drawable instanceof ConfigPanel || drawable instanceof Button) {
                drawable.mouseClicked(mouseX, mouseY, Click.getClick(mouseButton));
                continue;
            }
            if (this.drawables.get(this.drawables.size() - 2).hovered(mouseX, mouseY)) continue;
            drawable.mouseClicked(mouseX, mouseY, Click.getClick(mouseButton));
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.drag = false;
        this.draggingPanel = null;
        this.drawables.forEach(drawable -> drawable.mouseReleased(mouseX, mouseY, Click.getClick(state)));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.drawables.forEach(drawable -> drawable.keyTyped(typedChar, keyCode));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void drag(Drawable drawable, float mouseX, float mouseY) {
        if (!this.drag && Mouse.isButtonDown((int)0)) {
            this.drag = false;
        }
        if (this.drag) {
            drawable.setX(mouseX + this.dragX);
            drawable.setY(mouseY + this.dragY);
            if (!Mouse.isButtonDown((int)0)) {
                this.drag = false;
            }
        }
        if (drawable.hovered(mouseX, mouseY) && Mouse.isButtonDown((int)0) && !this.drag) {
            this.dragX = drawable.getX() - mouseX;
            this.dragY = drawable.getY() - mouseY;
            this.drag = true;
        }
    }
}

