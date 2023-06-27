package rip.athena.client.gui.clickgui.pages;

import rip.athena.client.gui.clickgui.components.mods.*;
import net.minecraft.client.*;
import rip.athena.client.gui.clickgui.*;
import rip.athena.client.*;
import rip.athena.client.cosmetics.cape.*;
import rip.athena.client.gui.clickgui.components.capes.*;
import rip.athena.client.gui.framework.*;
import java.util.*;

public class CapesPage extends Page
{
    private ModScrollPane scrollPane;
    
    public CapesPage(final Minecraft mc, final Menu menu, final IngameMenu parent) {
        super(mc, menu, parent);
    }
    
    @Override
    public void onInit() {
        final int width = 300;
        this.scrollPane = new ModScrollPane(260, 100, this.menu.getWidth() - width, this.menu.getHeight() - 101, false);
        this.populateScrollPane();
    }
    
    private void populateScrollPane() {
        this.scrollPane.getComponents().clear();
        final int spacing = 15;
        final int height = 250;
        final int defaultX = spacing;
        int y = spacing;
        int x = spacing;
        final int width = 190;
        final int maxWidth = this.scrollPane.getWidth() - spacing * 2;
        for (final Cape cape : Athena.INSTANCE.getCosmeticsController().getCapeManager().getCapes()) {
            final CapeButton capeButton = new CapeButton(cape, x, y, width, height) {
                @Override
                public void onAction() {
                    this.setActive(false);
                    Athena.INSTANCE.getCosmeticsController().getCapeManager().setSelectedCape(this.cape);
                    CapesPage.this.populateScrollPane();
                }
            };
            capeButton.setSelected(cape == Athena.INSTANCE.getCosmeticsController().getCapeManager().getSelectedCape());
            this.scrollPane.addComponent(capeButton);
            x += spacing + width;
            if (x + spacing + width > maxWidth) {
                x = defaultX;
                y += height + spacing;
            }
        }
    }
    
    @Override
    public void onRender() {
        final int width = 300;
        final int x = this.menu.getX() + this.menu.getWidth() - width + 20;
        final int y = this.menu.getY() + 59;
        final int height = 32;
        this.drawVerticalLine(this.menu.getX() + 215, y + height - 30, height + 432, 3, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor());
    }
    
    @Override
    public void onLoad() {
        this.menu.addComponent(this.scrollPane);
    }
    
    @Override
    public void onUnload() {
    }
    
    @Override
    public void onOpen() {
    }
    
    @Override
    public void onClose() {
    }
}
