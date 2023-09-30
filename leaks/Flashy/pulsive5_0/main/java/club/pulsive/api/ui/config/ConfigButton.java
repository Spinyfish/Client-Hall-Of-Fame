package club.pulsive.api.ui.config;

import club.pulsive.api.config.Config;
import club.pulsive.api.font.Fonts;
import club.pulsive.client.ui.clickgui.clickgui.component.Component;
import club.pulsive.impl.module.impl.visual.HUD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;


import java.awt.*;

public class ConfigButton extends Component {

    public int x, y, width, height;
    public Config config;
    public boolean selected = false, hovered = false;

    public ConfigButton(int x, int y, int width, int height, Config config) {
        super(x, y, width, height);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.config = config;
    }

    public void draw(int mouseX, int mouseY) {

        Color color = selected ? new Color(30, 30, 30, 200) : new Color(40, 40, 40, 200);
        Color color2 = new Color(20, 20, 20, 170);
        Gui.drawRect(x - 21, y, x+width + 1, y+height, 0x70121212);
        Gui.drawRect(x - 20, y, x+width, y+height, 0x70171717);
        //Gui.drawRect(x - 20, y, x+width, y+1, HUD.getColor());

        this.hovered = isHovered(x, y, x+width, y+height, mouseX, mouseY);
        Fonts.fontforflashy.drawStringWithShadow(config.getName(), x- 16, y + height / 2 - Fonts.fontforflashy.getHeight() / 2, HUD.getColor());

        Fonts.fontforflashy.drawStringWithShadow(config.getName(), x- 15, y + height / 2 - Fonts.fontforflashy.getHeight() / 2, -1);
        Fonts.moonSmall.drawString("by, You", x + Fonts.fontforflashy.getStringWidth(config.getName()) - 8, y + height / 2 -Fonts.moonSmall.getHeight() / 2, -1);

        int saveX = x + Fonts.fontforflashy.getStringWidth(config.getName()) + Fonts.moonSmall.getStringWidth("by, You") + 5;
        int saveY = y + height / 2 - Fonts.fontforflashy.getHeight() / 2 + 2 - 5;
        int saveWidth = 30;
        int saveHeight = 10;
        Gui.drawRect(saveX, saveY, saveX+saveWidth + 1, saveY+saveHeight + 2, 0xFF121212);
        Fonts.moonSmall.drawCenteredString("Save", saveX + saveWidth / 2, saveY + 4, -1);

        int loadX = (int) (x + Fonts.fontforflashy.getStringWidth(config.getName()) + Fonts.moonSmall.getStringWidth("by, You") + 5 + (saveWidth * 1.5));
        int loadY = y + height / 2 - Fonts.fontforflashy.getHeight() / 2 + 2 - 5;
        int loadWidth = 30;
        int loadHeight = 10;
        Gui.drawRect(loadX, loadY, loadX+loadWidth + 1, loadY+loadHeight + 2, 0xFF121212);
        Fonts.moonSmall.drawCenteredString("Load", loadX + loadWidth / 2, loadY + 4, -1);

        int delX = (int) (x + Fonts.fontforflashy.getStringWidth(config.getName()) + Fonts.moonSmall.getStringWidth("by, You") + 5 + (saveWidth * 1.5) + (loadWidth * 1.5));
        int delY = y + height / 2 - Fonts.fontforflashy.getHeight() / 2 + 2 - 5;
        int delWidth = 30;
        int delHeight = 10;
        Gui.drawRect(delX, delY, delX+delWidth + 1, delY+delHeight + 2, 0xFF121212);
        Fonts.moonSmall.drawCenteredString("Delete", delX + delWidth / 2, delY + 4, -1);
    }

    public boolean mouseClicked(int mouseX, int mouseY) {
        int saveX = x + Fonts.fontforflashy.getStringWidth(config.getName()) + Fonts.moonSmall.getStringWidth("by, You") + 5;
        int saveY = y + height / 2 - Fonts.fontforflashy.getHeight() / 2 + 2 - 5;
        int saveWidth = 30;
        int saveHeight = 10;
        if(isHovered2(saveX,saveY,saveX + saveWidth,saveY + saveHeight,mouseX,mouseY)) {
            System.out.println("Save");
            config.save();
        }
        System.out.println("Load");
        int loadX = (int) (x + Fonts.fontforflashy.getStringWidth(config.getName()) + Fonts.moonSmall.getStringWidth("by, You") + 5 + (saveWidth * 1.5));
        int loadY = y + height / 2 - Fonts.fontforflashy.getHeight() / 2 + 2 - 5;
        int loadWidth = 30;
        int loadHeight = 10;
        if(isHovered2(loadX,loadY,loadX + loadWidth,loadY + loadHeight,mouseX,mouseY)) {
            System.out.println("Load");
            config.load();
        }
        int delX = (int) (x + Fonts.fontforflashy.getStringWidth(config.getName()) + Fonts.moonSmall.getStringWidth("by, You") + 5 + (saveWidth * 1.5) + (loadWidth * 1.5));
        int delY = y + height / 2 - Fonts.fontforflashy.getHeight() / 2 + 2 - 5;
        int delWidth = 30;
        int delHeight = 10;
        if(isHovered2(delX,delY,delX + delWidth,delY + delHeight,mouseX,mouseY)) {
            System.out.println("Delete");
            config.delete();
        }
        return isHovered(x, y, x+width, y+height, mouseX, mouseY);
    }

    public boolean isHovered(double x, double y, double width, double height, int mouseX, int mouseY) {
        return mouseX > x - 20 && mouseY > y && mouseX < width && mouseY < height;
    }
    public boolean isHovered2(double x, double y, double width, double height, int mouseX, int mouseY) {
        return mouseX > x && mouseY > y && mouseX < width && mouseY < height;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        draw(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        mouseClicked(mouseX, mouseY);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }
}