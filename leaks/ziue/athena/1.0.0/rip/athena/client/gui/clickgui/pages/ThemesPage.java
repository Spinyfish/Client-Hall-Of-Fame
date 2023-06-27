package rip.athena.client.gui.clickgui.pages;

import rip.athena.client.gui.clickgui.components.mods.*;
import net.minecraft.client.*;
import rip.athena.client.gui.clickgui.*;
import rip.athena.client.modules.impl.other.*;
import rip.athena.client.utils.font.*;
import rip.athena.client.*;
import rip.athena.client.utils.render.*;
import java.awt.*;
import rip.athena.client.theme.impl.*;
import rip.athena.client.gui.clickgui.components.themes.primary.*;
import rip.athena.client.gui.framework.*;
import rip.athena.client.gui.clickgui.components.themes.accent.*;

public class ThemesPage extends Page
{
    private ModScrollPane scrollPane2;
    private ModScrollPane scrollPane;
    private AccentTheme activeTheme;
    
    public ThemesPage(final Minecraft mc, final Menu menu, final IngameMenu parent) {
        super(mc, menu, parent);
    }
    
    @Override
    public void onInit() {
        final int width = 300;
        this.scrollPane2 = new ModScrollPane(260, 130, this.menu.getWidth() - width - 10, this.menu.getHeight() - 341, false);
        this.scrollPane = new ModScrollPane(260, 225, this.menu.getWidth() - width - 20, this.menu.getHeight() - 241, false);
        this.populateScrollPane();
    }
    
    @Override
    public void onRender() {
        final int y = this.menu.getY() + 59;
        final int height = 32;
        if (Settings.customGuiFont) {
            FontManager.getNunitoBold(30).drawString("THEMES | " + Athena.INSTANCE.getThemeManager().getTheme() + ", " + Athena.INSTANCE.getThemeManager().getPrimaryTheme(), this.menu.getX() + 235, this.menu.getY() + 80, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
        else {
            Minecraft.getMinecraft().fontRendererObj.drawString("THEMES", this.menu.getX() + 235, this.menu.getY() + 80, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
        RoundedUtils.drawGradientRound((float)(this.menu.getX() + 295), (float)(this.menu.getY() + 110), (float)(this.menu.getWidth() / 4), 20.0f, 6.0f, Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor());
        RoundedUtils.drawGradientRound((float)(this.menu.getX() + 595), (float)(this.menu.getY() + 110), (float)(this.menu.getWidth() / 4), 20.0f, 6.0f, new Color(Athena.INSTANCE.getThemeManager().getPrimaryTheme().getFirstColor()), new Color(Athena.INSTANCE.getThemeManager().getPrimaryTheme().getFirstColor()), new Color(Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor()), new Color(Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor()));
        this.drawVerticalLine(this.menu.getX() + 215, y + height - 30, height + 432, 3, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor());
    }
    
    private void populateScrollPane() {
        this.scrollPane2.getComponents().clear();
        this.scrollPane.getComponents().clear();
        final int spacing = 15;
        final int height = 70;
        int y = spacing;
        int x = spacing;
        final int y2 = spacing;
        int x2 = spacing;
        final int defaultX = spacing;
        final int defaultX2 = spacing;
        final int width = 190;
        final int width2 = 190;
        final int maxWidth = this.scrollPane.getWidth() - spacing * 2;
        final int maxWidth2 = this.scrollPane.getWidth() - spacing * 2;
        for (final PrimaryTheme primaryTheme : PrimaryTheme.values()) {
            this.scrollPane2.addComponent(new PrimaryGradientButton(primaryTheme, x2, y2, width2, height, false) {
                @Override
                public void onAction() {
                    this.setActive(false);
                    Athena.INSTANCE.getThemeManager().setPrimaryTheme(this.primaryTheme);
                    ThemesPage.this.populateScrollPane();
                }
            });
            x2 += spacing + width2;
            if (x2 + spacing + width2 > maxWidth2) {
                x2 = defaultX2;
            }
        }
        for (final AccentTheme theme : AccentTheme.values()) {
            if (theme.isTriColor()) {
                this.scrollPane.addComponent(new TriColorGradientButton(theme, x, y, width, height, false) {
                    @Override
                    public void onAction() {
                        this.setActive(false);
                        Athena.INSTANCE.getThemeManager().setTheme(this.theme);
                        ThemesPage.this.populateScrollPane();
                    }
                });
            }
            else {
                this.scrollPane.addComponent(new SimpleGradientButton(theme, x, y, width, height, false) {
                    @Override
                    public void onAction() {
                        this.setActive(false);
                        Athena.INSTANCE.getThemeManager().setTheme(this.theme);
                        ThemesPage.this.populateScrollPane();
                    }
                });
            }
            x += spacing + width;
            if (x + spacing + width > maxWidth) {
                x = defaultX;
                y += height + spacing;
            }
        }
    }
    
    @Override
    public void onLoad() {
        this.menu.addComponent(this.scrollPane2);
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
