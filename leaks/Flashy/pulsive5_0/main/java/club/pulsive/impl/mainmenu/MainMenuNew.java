package club.pulsive.impl.mainmenu;

import club.pulsive.altmanager.GuiAltManager;
import club.pulsive.altmanager.GuiAltManagerNew;
import club.pulsive.api.font.Fonts;
import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.mainmenu.impl.MainMenuButton;
import club.pulsive.impl.mainmenu.impl.MainMenuInterface;

import club.pulsive.impl.module.impl.visual.HUD;
import club.pulsive.impl.module.impl.visual.Shaders;
import club.pulsive.impl.util.render.RenderUtil;
import club.pulsive.impl.util.render.RoundedUtil;
import club.pulsive.impl.util.render.StencilUtil;
import club.pulsive.impl.util.render.animations.Animation;
import club.pulsive.impl.util.render.animations.Direction;
import club.pulsive.impl.util.render.animations.SmoothStep;
import club.pulsive.impl.util.render.shaders.Blur;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;

public class MainMenuNew extends GuiScreen implements MainMenuInterface {

    private float scale;
    public static Animation ease = new SmoothStep(500, 1);
    public MainMenuNew() {
        ease.setDirection(Direction.FORWARDS);
        buttons.clear();
        addButton(new MainMenuButton(0, "Upgrade Skills", 125, 30, true));
        addButton(new MainMenuButton(1, "Singleplayer", 100,30, false));
        addButton(new MainMenuButton(2, "Alt Manager", 230,30, false));
        addButton(new MainMenuButton(3, "Settings", 112.5,30, false));
        addButton(new MainMenuButton(4, "Quit", 112.5,30, false));
        ease.reset();
    }

    private long initTime;

    @Override
    public void initGui() {
        initTime = System.currentTimeMillis();
        ease.reset();
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //this.scale = (height >= 1080 /1.5f ? (this.height) : this.height * 1.5f) / 1920f;
        boolean shouldCenter = this.width < 900;
        club.pulsive.impl.util.render.shaders.MainMenu.setTime((System.currentTimeMillis() - initTime) / 1000);
        club.pulsive.impl.util.render.shaders.MainMenu.nigger2();
        Gui.drawRect(0,0,this.width,this.height, new Color(31,31,31, 200).getRGB());
        Blur.renderBlur(30);
        int count = 0;
        int width = 0;
        double center = (shouldCenter ? this.width /2f : 70 + (230 * 1.25)/2f);
        RenderUtil.scaleStart((0 + this.width / 2), (0 + this.height / 2), (float) ease.getOutput());

        for(MainMenuButton btn : buttons) {
            btn.setSizeBigger(!shouldCenter);
            btn.drawButton((shouldCenter  ? this.width /2f - 230/2f : 70) + width,height / 2f - 30 + (count * (btn.getHeight() + 5)), mouseX, mouseY);
            width += btn.getWidth() + 5;
            if(width > 225) {
                count++;
                width = 0;
            }
        }
       
        if(!shouldCenter) {
            count = 0;
            for(int i = 0; i < 3; i++) {
                //RoundedUtil.drawRoundedRect(this.width - 155 - 70 - (count * 170f), height /2f - 100, this.width - 155 - 70 - (count * 170f) + 150, height /2f - 100 + 200, 8,0x80000000);
                count++;
            }
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        Pulsive.INSTANCE.getBlurrer().bloom((int) ((shouldCenter  ? this.width /2f - 230/2f - (596 / 2) /6 + 10000: this.width - this.width / 2 - 40) + width - 1), (int) (height /2f / 2 - 1), 1920 / 4, 1080 / 4, 15, 150);

        StencilUtil.initStencilToWrite();
        RoundedUtil.drawRoundedRect((shouldCenter  ? this.width /2f - 230/2f - (596 / 2) /6 + 10000: this.width - this.width / 2 - 40) + width - 1, (height /2f / 2 - 1), (shouldCenter  ? this.width /2f - 230/2f - (596 / 2) /6 + 10000: this.width - this.width / 2 - 40) + width - 1 + 1920 / 4, (height /2f / 2 - 1) + 1080 / 4, 15, new Color(HUD.getColor()).getRGB());
        StencilUtil.readStencilBuffer(1);
        RenderUtil.drawImage(new ResourceLocation("pulsabo/images/mainMenuSS.png"), (shouldCenter  ? this.width /2f - 230/2f - (596 / 2) /6 + 10000: this.width - this.width / 2 - 40) + width - 1, (height /2f / 2 - 1), 1920 / 4, 1080 / 4);
        
        StencilUtil.uninitStencilBuffer();

        RenderUtil.color(0xFFDC666D);
        RenderUtil.drawImage(true, new ResourceLocation("pulsabo/images/testtest4.png"), (shouldCenter  ? this.width /2f - 230/2f - (596 / 2) /6: 70 - 9) + width - 1, (height /2f + 80) / 2 - 1, 596 / 2, 83 / 2);
        RenderUtil.drawImage(new ResourceLocation("pulsabo/images/testtest4.png"), (shouldCenter  ? this.width /2f - 230/2f - (596 / 2) /6 : 70 - 9) + width, (height /2f + 80) / 2, 596 / 2, 83 / 2);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        
        GlStateManager.popMatrix();
        RenderUtil.scaleEnd();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        onMouseClick(mouseX, mouseY);
      
    }

    
    public void onButtonClicked(MainMenuButton btn) {
        switch (btn.getId()) {
            case 0:
                this.mc.displayGuiScreen(new GuiMultiplayer(this));
                break;

            case 1:
                this.mc.displayGuiScreen(new GuiSelectWorld(this));
                break;

            case 2:
                this.mc.displayGuiScreen(new GuiAltManagerNew(this));
                break;

            case 3:
                this.mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                break;

            case 4:
                mc.shutdown();
                break;
        }
    }
}
