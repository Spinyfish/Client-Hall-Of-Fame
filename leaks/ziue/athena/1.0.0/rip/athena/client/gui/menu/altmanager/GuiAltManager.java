package rip.athena.client.gui.menu.altmanager;

import rip.athena.client.gui.menu.altmanager.helpers.*;
import rip.athena.client.gui.menu.altmanager.panels.*;
import rip.athena.client.gui.menu.*;
import rip.athena.client.gui.screen.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.gui.*;
import net.minecraft.util.*;
import rip.athena.client.utils.render.*;
import java.util.*;

public class GuiAltManager extends GuiScreen
{
    private final AltManagerUtils utils;
    private List<Panel> panels;
    public final TextField searchField;
    
    public GuiAltManager() {
        this.utils = new AltManagerUtils();
        this.searchField = new TextField();
        if (this.panels == null) {
            (this.panels = new ArrayList<Panel>()).add(new LoginPanel());
        }
    }
    
    @Override
    protected void keyTyped(final char typedChar, final int keyCode) {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(new AthenaMenu());
            this.searchField.setFocused(false);
        }
        this.searchField.keyTyped(typedChar, keyCode);
        this.panels.forEach(panel -> panel.keyTyped(typedChar, keyCode));
    }
    
    @Override
    public void initGui() {
        if (this.mc.gameSettings.guiScale != 2) {
            this.mc.gameSettings.guiScale = 2;
        }
        this.panels.forEach(Screen::initGui);
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        final ScaledResolution sr = new ScaledResolution(this.mc);
        final int screenWidth = sr.getScaledWidth();
        final int screenHeight = sr.getScaledHeight();
        final int xOffset = (screenWidth - 325) / 2;
        final int yOffset = (screenHeight - this.getTotalPanelsHeight()) / 2;
        final int width = screenWidth - 2 * xOffset;
        DrawUtils.drawImage(new ResourceLocation("Athena/menu/wallpaper3.png"), 0, 0, screenWidth, screenHeight);
        RoundedUtils.drawGradientRound((float)(xOffset + 15), (float)(yOffset - 1), (float)(width + 1), (float)(this.getTotalPanelsHeight() - 23), 6.0f, ColorUtil.getClientColor(0, 255), ColorUtil.getClientColor(90, 255), ColorUtil.getClientColor(180, 255), ColorUtil.getClientColor(270, 255));
        int count = 0;
        int separation = 0;
        for (final Panel panel : this.panels) {
            panel.setX((float)(xOffset + 16));
            panel.setY((float)(yOffset + separation));
            panel.setWidth(325.0f);
            panel.drawScreen(mouseX, mouseY);
            separation += (int)(panel.getHeight() + ((count >= 1) ? 10 : 25));
            ++count;
        }
    }
    
    private int getTotalPanelsHeight() {
        int totalHeight = 0;
        for (final Panel panel : this.panels) {
            totalHeight += (int)(panel.getHeight() + ((totalHeight >= 1) ? 10 : 25));
        }
        return totalHeight;
    }
    
    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
        this.panels.forEach(panel -> panel.mouseClicked(mouseX, mouseY, mouseButton));
    }
    
    @Override
    protected void mouseReleased(final int mouseX, final int mouseY, final int state) {
        this.panels.forEach(panel -> panel.mouseReleased(mouseX, mouseY, state));
    }
    
    @Override
    public void onGuiClosed() {
    }
    
    public AltManagerUtils getUtils() {
        return this.utils;
    }
}
