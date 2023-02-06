package dev.elysium.client.ui.gui;


import dev.elysium.client.Elysium;
import dev.elysium.client.ui.font.TTFFontRenderer;
import dev.elysium.client.ui.gui.components.Button;
import dev.elysium.client.utils.render.ColorAnimation;
import dev.elysium.client.utils.render.RenderUtils;
import dev.elysium.client.utils.render.Stencil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public final class MainMenu extends GuiScreen {
    List<Button> buttons = new ArrayList<Button>();
    ScaledResolution sr;
    Button hovering;

    TTFFontRenderer fr;
    TTFFontRenderer frSmall;
    TTFFontRenderer choco;
    //TTFFontRenderer elyX;
    TTFFontRenderer elyY;

    //BlurUtil blur = new BlurUtil();

    public MainMenu() {

    }

    @Override
    protected void actionPerformed(GuiButton button) {

    }

    void initButtons() {
        buttons.clear();


    }

    long last = System.currentTimeMillis();

    @Override
    public void drawScreen(int x2, int y2, float z2) {
        float delta = System.currentTimeMillis() - last;

        //animation

        last = System.currentTimeMillis();

        GlStateManager.pushMatrix();
        mc.getTextureManager().bindTexture(new ResourceLocation("Elysium/alt_bg.png"));
        //GlStateManager.color(0.8f, 0.8f, 0.8f, 1);
        drawModalRectWithCustomSizedTexture(0, 0, 0, 0, width, height, width, height);
        GlStateManager.popMatrix();
        //blur.blurArea(width / 2 - 135, height / 2 - 140, width / 2 + 135, height / 2 + 140, 5, 10);
        //blur.blur(1);

        RenderUtils.drawDiagnonalGradientRoundedRectOutline(width / 2 - 135, height / 2 - 140, width / 2 + 135, height / 2 + 140, 0xff221D31, 0xff422F63, 14);
        RenderUtils.drawGradientRoundedRect(width / 2 - 135 + 1, height / 2 - 140 + 1, width / 2 + 135 - 1, height / 2 + 140 - 1, 14, 0x3527155E, 0x3527155E);

        String elysium = "elysium";
        float index = 0;
        for(char c : elysium.toCharArray()) {
            int color = ColorAnimation.getColor(0xff9E59E9, 0xff5F45AA, index / 81f);
            if(c == 'y') {
                elyY.drawString("b", width / 2 - choco.getStringWidth(elysium) / 2 + index - elyY.getStringWidth("y") + 2, height / 2 - 90, color);
            } else {
                choco.drawString(String.valueOf(c), width / 2 - choco.getStringWidth(elysium) / 2 + (index), height / 2 - 90, color);
            }

            index += choco.getStringWidth(c + "") - 2f;
        }

        RenderUtils.drawHorizontalGradientRoundedRectOutline(width / 2 - 115, height / 2 - 10, width / 2 + 115, height / 2 + 20,0xff5900B9, 0xff6D6BA6, 5);

        for(Button b : buttons) {
            if(b.color == 2) continue;
            int textColor = ColorAnimation.getColor(0xff7D65D3, 0xff6A7AA3, 1 - (b.timer / 1000f));
            int borderColor = ColorAnimation.getColor(0x00000000, 0xff4c326e, b.timer / 1000f);
            int borderColor2 = ColorAnimation.getColor(0x00000000, 0xff4c326e, b.timer / 1000f);
            if(borderColor != 0x00000000) {
                Stencil.INSTANCE.start();
                Stencil.INSTANCE.setBuffer(true);
                RenderUtils.drawARoundedRect(b.x, b.y, b.x2, b.y2, 5, -1);
                Stencil.INSTANCE.cropOutside();
                double w = 1;
                RenderUtils.drawGradientRoundedRect(b.x - w, b.y - w , b.x2 + w, b.y2 + w, 5, borderColor, borderColor2);
                Stencil.getInstance().stopLayer();
            }

            RenderUtils.drawGradientRoundedRect(b.x, b.y, b.x2, b.y2, 5, 0x1a634284, 0x1a7D64a7);

            b.font.drawCenteredString(b.idname, (float) (b.x + (b.x2 - b.x) / 2), (float) (b.y + (b.y2 - b.y) / 2 - b.font.getHeight(b.idname) / 2), textColor);
        }
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        sr = new ScaledResolution(mc);
        fr = Elysium.getInstance().getFontManager().getFont("POPPINS-SEMIBOLD 20");
        frSmall = Elysium.getInstance().getFontManager().getFont("POPPINS-SEMIBOLD 16");
        choco = Elysium.getInstance().getFontManager().getFont("CHOCOLATE 48");
        elyY = Elysium.getInstance().getFontManager().getFont("ELY 68");
        initButtons();
        mc.displayGuiScreen(new GuiMainMenu());
    }

    public void keyTyped(char character, int key) {

    }

    public void mouseClicked(int x2, int y2, int button) {
        buttons.stream().filter(b -> b.isMouseOverMe(x2, y2)).forEach(b -> {
            switch(b.idname) {
                case "close":
                    mc.shutdown();
                    break;
            }
        });
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
}

