/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Mouse
 */
package wtf.monsoon.impl.ui.windowgui;

import java.awt.Color;
import java.io.IOException;
import org.lwjgl.input.Mouse;
import wtf.monsoon.Wrapper;
import wtf.monsoon.impl.ui.ScalableScreen;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.windowgui.window.Window;

public class WindowGUI
extends ScalableScreen {
    public static Color BACKGROUND = new Color(20, 20, 24);
    public static Color LAYER_ONE = new Color(30, 30, 34);
    public static Color INTERACTABLE = new Color(50, 50, 54);
    public static Color HOVER = new Color(60, 60, 64);
    private Window window = new Window(50.0f, 50.0f, 500.0f, 400.0f);

    @Override
    public void init() {
        if (this.window == null) {
            this.window = new Window(50.0f, 50.0f, 500.0f, 400.0f);
        }
    }

    @Override
    public void render(float mouseX, float mouseY) {
        int mouseDelta = Mouse.getDWheel();
        Wrapper.getMonsoon().getCharacterManager().draw((int)mouseX, (int)mouseY, this.mc.thePlayer.ticksExisted, this);
        if (this.window != null) {
            this.window.render(mouseX, mouseY, mouseDelta);
        }
    }

    @Override
    public void click(float mouseX, float mouseY, int mouseButton) {
        Wrapper.getMonsoon().getCharacterManager().onClick((int)mouseX, (int)mouseY, mouseButton, this);
        if (this.window != null) {
            this.window.mouseClicked(mouseX, mouseY, Click.getClick(mouseButton));
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.window != null) {
            this.window.mouseReleased(mouseX, mouseY, Click.getClick(state));
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.window != null) {
            this.window.keyTyped(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        Wrapper.getMonsoon().getConfigSystem().save("current");
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

