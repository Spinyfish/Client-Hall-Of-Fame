package club.pulsive.api.ui;

import club.pulsive.api.config.Config;
import club.pulsive.api.font.CFont;
import club.pulsive.api.font.FontRenderer;
import club.pulsive.api.font.Fonts;
import club.pulsive.api.main.Pulsive;
import club.pulsive.api.ui.config.CustomButton;
import club.pulsive.impl.module.impl.visual.Shaders;
import club.pulsive.impl.util.customui.TextField;
import club.pulsive.api.ui.config.ConfigElement;
import club.pulsive.client.ui.clickgui.clickgui.component.Component;
import club.pulsive.client.ui.clickgui.clickgui.panel.Panel;
import club.pulsive.impl.module.impl.visual.HUD;
import club.pulsive.impl.util.client.Logger;
import club.pulsive.impl.util.math.MathUtil;
import club.pulsive.impl.util.render.RenderUtil;
import club.pulsive.impl.util.render.RoundedUtil;
import club.pulsive.impl.util.render.StencilUtil;
import club.pulsive.impl.util.render.secondary.ShaderRound;
import club.pulsive.impl.util.render.secondary.ShaderUtil;
import club.pulsive.impl.util.render.shaders.Blur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConfigPanel extends GuiScreen {
    private ConfigElement configElement;
    public TextField textField;
    public static boolean shouldReload, extended;
    int countY = 11, configAmount;
    public List<club.pulsive.api.ui.ConfigButton> buttons;
    
    public float x, y, width, height;
    
    public ConfigPanel(float x, float y, float width, float height) {
        buttons = new ArrayList<>();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        ScaledResolution sc = new ScaledResolution(Minecraft.getMinecraft());
        Set<Config> configs = Pulsive.INSTANCE.getConfigManager().getConfigs().keySet();
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

    }
        
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
       //w this.drawDefaultBackground();
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
          
            StencilUtil.initStencilToWrite();
        RoundedUtil.drawRoundedRect(scaledResolution.getScaledWidth() / 3 - 40, 80, scaledResolution.getScaledWidth() / 3 - 40 + 400,
                80 + 300, 3,  new Color(40, 40, 40, 220).getRGB());
            StencilUtil.readStencilBuffer(1);
        Blur.renderBlur(15);
            StencilUtil.uninitStencilBuffer();
            GL11.glPushMatrix();
        RoundedUtil.drawRoundedRect(scaledResolution.getScaledWidth() / 3 - 40, 80, scaledResolution.getScaledWidth() / 3 - 40 + 400,
                80 + 300, 3,  new Color(40, 40, 40, 220).getRGB());
            GL11.glPopMatrix();
            RoundedUtil.drawRoundedOutline(scaledResolution.getScaledWidth() / 3 - 40, 80, scaledResolution.getScaledWidth() / 3 - 40 + 400,
                80 + 300, 8,3, HUD.getColor());

           // Gui.drawRect(50, 20, 80, 40, new Color(60, 60, 60).getRGB());
            //Pulsive.INSTANCE.getConfigManager().init();
            //Gui.drawRect();
            configElement.draw(mouseX, mouseY);
            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            if(shouldReload){
               // Pulsive.INSTANCE.getConfigManager().init();
                configElement = new ConfigElement(scaledResolution.getScaledWidth() / 3 - 16,115, 370, 270);
               // configElement = new ConfigElement(40,30, (int) sr.getScaledWidth() - 40, (int) (int) sr.getScaledHeight() - 30);
                shouldReload = false;
            }
        textField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    public void initGui() {
        //int y = 10;
        int counter = 0; ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
//        if(shouldReload){
//            Pulsive.INSTANCE.getConfigManager().init();
//            configElement = new ConfigElement(40,30, (int) sr.getScaledWidth() - 40, (int) (int) sr.getScaledHeight() - 30);
//            shouldReload = false;
//        }
        this.textField = new TextField(5, this.mc.fontRendererObj, sr.getScaledWidth() / 3 - 35, 85, 60, 17);
        this.buttonList.add(new CustomButton(2, sr.getScaledWidth() / 3 + 30, 85, 60, 17, "Create"));
        configElement = new ConfigElement(sr.getScaledWidth() / 3 - 16,115, 370, 270);
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        //config
       
        ScaledResolution sc = new ScaledResolution(Minecraft.getMinecraft());
        configElement.mouseClicked(mouseX, mouseY);
        if(mouseButton == 0) {
            configAmount++;
            if(configAmount == 1) {
                //Pulsive.INSTANCE.getConfigManager().add(new Config(randomFlashy));
               // buttons.add(new ConfigButton(10, countY, 100, 20, randomFlashy));
                configAmount = 0;
            }
        }
        textField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        configElement.onMouse();
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if(RenderUtil.isHovered(x, y, width, height, mouseX, mouseY) && mouseButton == 0) {
            extended = !extended;
           //
        }
        if(mouseButton == 0){
            //shouldReload = true;
        }
       // super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
       
        if(button.id == 2) {
            if(textField.getText().length() != 0) {
                Logger.printSysLog("Create");
                Pulsive.INSTANCE.getConfigManager().add(new Config(textField.getText()));
                shouldReload = true;
                // configElement = new ConfigElement(sr.getScaledWidth()-100, sr.getScaledHeight()-200, 100, 100);
            }
        }
    }
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == Keyboard.KEY_ESCAPE && extended) {
            extended = false;
           // focused = false;
        }
        textField.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }
}
