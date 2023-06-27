package rip.athena.client.gui.clickgui.pages;

import rip.athena.client.gui.clickgui.components.waypoints.*;
import rip.athena.client.gui.clickgui.components.macros.*;
import net.minecraft.client.*;
import rip.athena.client.gui.clickgui.*;
import java.awt.*;
import rip.athena.client.utils.*;
import rip.athena.client.*;
import rip.athena.client.modules.impl.other.*;
import rip.athena.client.font.*;
import rip.athena.client.gui.framework.draw.*;
import rip.athena.client.gui.clickgui.components.mods.*;
import rip.athena.client.gui.framework.*;

public class WaypointsPage extends Page
{
    private MacroTextfield name;
    private MacroTextfield description;
    private WaypointTextBarrier xBarrier;
    private MacroTextfield x;
    private WaypointTextBarrier yBarrier;
    private MacroTextfield y;
    private WaypointTextBarrier zBarrier;
    private MacroTextfield z;
    private MenuModColorPicker color;
    private MacroButton button;
    private MacroButton delete;
    private ModScrollPane scrollPane;
    private boolean editing;
    
    public WaypointsPage(final Minecraft mc, final Menu menu, final IngameMenu parent) {
        super(mc, menu, parent);
    }
    
    @Override
    public void onInit() {
        final int width = 300;
        final int x = this.menu.getWidth() - width + 20;
        final int y = 59;
        final int compWidth = width - 6 - 40;
        this.name = new MacroTextfield(TextPattern.NONE, x, y + 85, compWidth, 22, "...");
        this.description = new MacroTextfield(TextPattern.NONE, x, y + 155, compWidth, 22, "...");
        this.xBarrier = new WaypointTextBarrier("X", x, y + 195 - 1, 25, 24);
        this.x = new MacroTextfield(TextPattern.NUMBERS_ONLY, x + 30, y + 195, compWidth - 30, 22, "...") {
            @Override
            public void onAction() {
                WaypointsPage.this.editing = true;
            }
        };
        this.yBarrier = new WaypointTextBarrier("Y", x, y + 225 - 1, 25, 24);
        this.y = new MacroTextfield(TextPattern.NUMBERS_ONLY, x + 30, y + 225, compWidth - 30, 22, "...") {
            @Override
            public void onAction() {
                WaypointsPage.this.editing = true;
            }
        };
        this.zBarrier = new WaypointTextBarrier("Z", x, y + 255 - 1, 25, 24);
        this.z = new MacroTextfield(TextPattern.NUMBERS_ONLY, x + 30, y + 255, compWidth - 30, 22, "...") {
            @Override
            public void onAction() {
                WaypointsPage.this.editing = true;
            }
        };
        this.color = new MenuModColorPicker(x, this.menu.getHeight() - 22 - 20 - 40 - 35, compWidth, 22, Color.RED.getRGB());
        final int acceptWidth = compWidth - 40;
        this.button = new MacroButton("ADD", x - 21 + width / 2 - acceptWidth / 2, this.menu.getHeight() - 22 - 20 - 40, acceptWidth, 22, true) {
            @Override
            public void onAction() {
                this.setActive(false);
                if (WaypointsPage.this.name.getText().isEmpty()) {
                    return;
                }
                if (WaypointsPage.this.description.getText().isEmpty()) {
                    return;
                }
                final String xText = WaypointsPage.this.x.getText();
                final String yText = WaypointsPage.this.y.getText();
                final String zText = WaypointsPage.this.z.getText();
                if (xText.isEmpty()) {
                    return;
                }
                if (yText.isEmpty()) {
                    return;
                }
                if (zText.isEmpty()) {
                    return;
                }
                if (!StringUtils.isInteger(xText) || !StringUtils.isInteger(yText) || !StringUtils.isInteger(zText)) {
                    return;
                }
                final int xPos = Integer.parseInt(xText);
                final int yPos = Integer.parseInt(yText);
                final int zPos = Integer.parseInt(zText);
                WaypointsPage.this.name.setText("");
                WaypointsPage.this.description.setText("");
                WaypointsPage.this.x.setText("");
                WaypointsPage.this.y.setText("");
                WaypointsPage.this.z.setText("");
                WaypointsPage.this.editing = false;
                WaypointsPage.this.populateScrollPane();
            }
        };
        this.delete = new MacroButton("CLEAR ALL WAYPOINTS", x - 21 + width / 2 - compWidth / 2, this.menu.getHeight() - 22 - 20, compWidth, 22, false) {
            @Override
            public void onAction() {
                this.setActive(false);
                WaypointsPage.this.populateScrollPane();
            }
        };
        this.scrollPane = new ModScrollPane(31, 140, this.menu.getWidth() - width - 62, this.menu.getHeight() - 141, false);
        this.populateScrollPane();
    }
    
    private void populateScrollPane() {
        this.scrollPane.getComponents().clear();
        final int spacing = 15;
        final int height = 30;
        final int y = spacing;
        final int x = spacing;
        final int width = this.scrollPane.getWidth() - spacing * 2;
    }
    
    @Override
    public void onRender() {
        final int width = 300;
        final int x = this.menu.getX() + this.menu.getWidth() - width + 20;
        int y = this.menu.getY() + 59;
        final int height = 32;
        this.drawVerticalLine(this.menu.getX() + 215, y + height - 30, height + 432, 3, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor());
        if (Settings.customGuiFont) {
            FontManager.vision16.drawString("WAYPOINTS", (float)(this.menu.getX() + 235), (float)(this.menu.getY() + 80), IngameMenu.MENU_HEADER_TEXT_COLOR);
        }
        else {
            this.mc.fontRendererObj.drawString("WAYPOINTS", this.menu.getX() + 235, this.menu.getY() + 80, IngameMenu.MENU_HEADER_TEXT_COLOR);
        }
        DrawImpl.drawRect(this.menu.getX() + this.menu.getWidth() - width, this.menu.getY() + 58, width, this.menu.getHeight() - 58, MacrosPage.MENU_SIDE_BG_COLOR);
        DrawImpl.drawRect(this.menu.getX() + this.menu.getWidth() - width, this.menu.getY() + 58, width, height + 1, ModCategoryButton.MAIN_COLOR);
        this.drawShadowDown(this.menu.getX() + this.menu.getWidth() - width, y + height, width);
        Minecraft.getMinecraft().fontRendererObj.drawString("ADD NEW WAYPOINT", this.menu.getX() + this.menu.getWidth() - width / 2 - Minecraft.getMinecraft().fontRendererObj.getStringWidth("ADD NEW WAYPOINT") / 2, y + height / 2 - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT / 2, IngameMenu.MENU_HEADER_TEXT_COLOR);
        this.drawShadowDown(this.menu.getX() + this.menu.getWidth() - width, y - 1, width);
        y += 60;
        Minecraft.getMinecraft().fontRendererObj.drawString("ENTER NAME", x, y, IngameMenu.MENU_HEADER_TEXT_COLOR);
        y += 70;
        Minecraft.getMinecraft().fontRendererObj.drawString("DESCRIPTION", x, y, IngameMenu.MENU_HEADER_TEXT_COLOR);
        y += 70;
        y += 30;
        y += 30;
        y += 30;
        Minecraft.getMinecraft().fontRendererObj.drawString("COLOR", x, y, IngameMenu.MENU_HEADER_TEXT_COLOR);
    }
    
    @Override
    public void onLoad() {
        this.editing = false;
        this.onOpen();
        this.menu.addComponent(this.name);
        this.menu.addComponent(this.description);
        this.menu.addComponent(this.x);
        this.menu.addComponent(this.xBarrier);
        this.menu.addComponent(this.y);
        this.menu.addComponent(this.yBarrier);
        this.menu.addComponent(this.z);
        this.menu.addComponent(this.zBarrier);
        this.menu.addComponent(this.color);
        this.menu.addComponent(this.button);
        this.menu.addComponent(this.delete);
        this.menu.addComponent(this.scrollPane);
    }
    
    @Override
    public void onUnload() {
    }
    
    @Override
    public void onOpen() {
        if (!this.editing) {
            this.x.setText(this.mc.thePlayer.getPosition().getX() + "");
            this.y.setText(this.mc.thePlayer.getPosition().getY() + "");
            this.z.setText(this.mc.thePlayer.getPosition().getZ() + "");
        }
    }
    
    @Override
    public void onClose() {
    }
}
