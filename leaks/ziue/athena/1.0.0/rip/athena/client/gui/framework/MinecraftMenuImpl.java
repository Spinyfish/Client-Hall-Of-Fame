package rip.athena.client.gui.framework;

import rip.athena.client.modules.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.gui.*;
import org.lwjgl.input.*;
import rip.athena.client.utils.input.*;
import java.io.*;

public class MinecraftMenuImpl extends GuiScreen
{
    protected Module feature;
    protected Menu menu;
    protected boolean ready;
    protected float guiScale;
    
    public MinecraftMenuImpl(final Module feature, final Menu menu) {
        this.ready = false;
        this.guiScale = 1.0f;
        this.feature = feature;
        this.menu = menu;
    }
    
    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        GlStateManager.pushMatrix();
        final float value = this.guiScale / new ScaledResolution(this.mc).getScaleFactor();
        GlStateManager.scale(value, value, value);
        this.menu.onRender(Math.round(mouseX / value), Math.round(mouseY / value));
        GlStateManager.popMatrix();
        this.onMouseScroll(Mouse.getDWheel());
        if (this.feature != null && this.feature.isToggled() && this.feature.isBound() && this.feature.getBindType() == BindType.HOLD && !Keyboard.isKeyDown(this.feature.getKeyBind())) {
            this.mc.displayGuiScreen(null);
        }
    }
    
    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        this.menu.onMouseClick(mouseButton);
    }
    
    @Override
    protected void mouseClickMove(final int mouseX, final int mouseY, final int clickedMouseButton, final long timeSinceLastClick) {
        this.menu.onMouseClickMove(clickedMouseButton);
    }
    
    public void onMouseScroll(final int scroll) {
        this.menu.onScroll(scroll);
    }
    
    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        if (keyCode == 1) {
            if (this.menu.onMenuExit(keyCode)) {
                return;
            }
            this.mc.displayGuiScreen(null);
        }
        else {
            this.menu.onKeyDown(typedChar, keyCode);
        }
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    @Override
    public void onGuiClosed() {
        if (this.feature != null) {
            this.feature.setEnabled(false);
        }
        this.ready = false;
        super.onGuiClosed();
    }
    
    public Module getFeature() {
        return this.feature;
    }
}
