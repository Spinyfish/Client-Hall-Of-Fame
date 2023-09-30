package club.pulsive.api.ui.config;

import java.awt.Color;
import java.util.*;

import club.pulsive.api.config.Config;
import club.pulsive.api.font.Fonts;
import club.pulsive.api.main.Pulsive;
import club.pulsive.api.ui.ConfigPanel;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.util.render.RenderUtil;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import static club.pulsive.api.minecraft.MinecraftUtil.mc;

public class ConfigElement {

    public int x, y, width, height;
    public ConfigButton selected;
    private static int mouse = 0;
    private static boolean hovered = false;
    public ArrayList<ConfigButton> buttons = new ArrayList<>();

    public ConfigElement(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        int count = 0;
        List<Config> configs = new ArrayList<Config>(Pulsive.INSTANCE.getConfigManager().getConfigs().keySet());
        Collections.sort(configs, new Comparator<Config>() {

            @Override
            public int compare(Config o1, Config o2) {
                return String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
            }
        });
        for(Config config : configs) {
            buttons.add(new ConfigButton(x, y+25*count, width, 20, config));
            count++;
        }
    }

    public void draw(int mouseX, int mouseY) {

        // GL11.glPushMatrix();
        //GL11.glEnable(GL11.GL_SCISSOR_TEST);
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int scale = sr.getScaleFactor();
//        GL11.glScissor(sr.getScaledWidth()/2 < x ? 200 : 0,
//                Minecraft.getMinecraft().displayHeight - (y + this.height) * scale,
//                (this.width + x)*scale,
//                (this.height)*scale);
        RenderUtil.makeCropBox(sr.getScaledWidth() / 3 - 40, y, x + this.width, y + height - 10);



        int heightCalc = buttons.size()*30;
        int offset = height-heightCalc;
        if(this.mouse > 0) {
            this.mouse = 0;
        }
        if(offset >= 0) {
            this.mouse = 0;
        }
        if(this.mouse < offset && offset < 0) {
            this.mouse = offset;
        }

        int count = 0;
        for(ConfigButton button : buttons) {
            button.y = y+count*30+mouse;
            button.x = x;
            button.draw(mouseX, mouseY);
            count++;
        }
        this.hovered = isHovered(x, y, x+width, y+height, mouseX, mouseY);

        RenderUtil.destroyCropBox();
        //      GL11.glPopMatrix();

    }

    public boolean isHovered(double x, double y, double width, double height, int mouseX, int mouseY) {
        return mouseX > x- 20 && mouseY > y && mouseX < width && mouseY < height;
    }

    public void mouseClicked(int mouseX, int mouseY) {

        for(ConfigButton button : buttons) {
            int saveX = button.x + Fonts.fontforflashy.getStringWidth(button.config.getName()) + Fonts.moonSmall.getStringWidth("by, You") + 5;
            int saveY = button.y + button.height / 2 - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT / 2 + 2 - 5;
            int saveWidth = 30;
            int saveHeight = 10;
            if(isHovered2(saveX,saveY,saveX + saveWidth,saveY + saveHeight,mouseX,mouseY)) {
                System.out.println("Save");
                button.config.save();
            }
            int loadX = (int) (button.x + Fonts.fontforflashy.getStringWidth(button.config.getName()) + Fonts.moonSmall.getStringWidth("by, You") + 5 + (saveWidth * 1.5));
            int loadY = button.y + button.height / 2 - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT / 2 + 2 - 5;
            int loadWidth = 30;
            int loadHeight = 10;
            if(isHovered2(loadX,loadY,loadX + loadWidth,loadY + loadHeight,mouseX,mouseY)) {
                System.out.println("Load");
                button.config.load();
            }

            int delX = (int) (button.x + Fonts.fontforflashy.getStringWidth(button.config.getName()) + Fonts.moonSmall.getStringWidth("by, You") + 5 + (saveWidth * 1.5) + (loadWidth * 1.5));
            int delY = button.y + button.height / 2 - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT / 2 + 2 - 5;
            int delWidth = 30;
            int delHeight = 10;
            if(isHovered2(delX,delY,delX + delWidth,delY + delHeight,mouseX,mouseY)) {
                System.out.println("Delete");
                Pulsive.INSTANCE.getConfigManager().remove(button.config);
                ConfigPanel.shouldReload = true;
            }
        }
    }

    public boolean isHovered2(double x, double y, double width, double height, int mouseX, int mouseY) {
        return mouseX > x && mouseY > y && mouseX < width && mouseY < height;
    }

    public static void onMouse() {
        if(!hovered) return;
        int change = Mouse.getEventDWheel();
        if(change > 0) {
            mouse += 7;
        } else if(change < 0) {
            mouse -= 7;
        }
    }

}