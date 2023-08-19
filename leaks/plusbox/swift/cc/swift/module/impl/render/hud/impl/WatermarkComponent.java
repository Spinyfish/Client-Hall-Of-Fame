package cc.swift.module.impl.render.hud.impl;

import cc.swift.Swift;
import cc.swift.events.RenderOverlayEvent;
import cc.swift.module.impl.render.HudModule;
import cc.swift.module.impl.render.hud.HudFont;
import cc.swift.module.impl.render.hud.HudComponent;
import cc.swift.util.ColorUtil;
import cc.swift.util.render.RenderUtil;
import cc.swift.util.render.font.CFontRenderer;
import cc.swift.util.render.font.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.security.SecureRandom;
import java.util.Random;

public class WatermarkComponent extends HudComponent {

    public WatermarkComponent(float x, float y){
        super(x, y);
    }

    @Override
    public void render( HudFont font) {
        switch (getParentModule().watermarkMode.getValue()) {
            /*case MODERNIZED: {
                String text = "S" + EnumChatFormatting.WHITE + "wift - " + Swift.INSTANCE.VERSION + " - Plusbox";
                
                int opacity = Swift.INSTANCE.getModuleManager().getModule(HudModule.class).arrayListBGOpacity.getValue().intValue();
                int color = new Color(0, 0, 0, opacity).getRGB();

                Gui.drawRect(getPosX(), getPosY(), getPosX() + font.getStringWidth(text) + 5, getPosY() + font.getFontHeight() + 7, color);

                for (double i = getPosX() + 0.5; i < getPosX() + getPosX() + font.getStringWidth(text); i++) {
                    Gui.drawRect(i, getPosY() + 0.5, i + 1, getPosY() + 2, ColorUtil.rainbow((int) i * 8, 2));
                }
                
                font.drawStringWithShadow(text, getPosX() + 3, getPosY() + 5.5f, ColorUtil.rainbow(0, 2));
                break;
            }*/ // THIS IS NOT MODERN BRUH
            case BASIC: {
                CFontRenderer vietnamFont = FontRenderer.getFont("vietnam.ttf", 18);
                mc.fontRendererObj.drawStringWithShadow("s\247fwift.cc", 4, 4, Color.MAGENTA.getRGB());
                break;
            }
            case BIGGER_BASIC: {
                GlStateManager.pushMatrix();
                GlStateManager.scale(2.0f, 2.0f, 2.0f);
                // Create a Color object with the random components
                mc.fontRendererObj.drawStringWithShadow("s\247fwift.cc", 4, 4, Color.MAGENTA.getRGB());
                GlStateManager.popMatrix();
                break;
            }
            case SWIFT: {
                CFontRenderer iconsFont = FontRenderer.getFont("icons.ttf", 23);
                CFontRenderer vietnamFont = FontRenderer.getFont("vietnam.ttf", 18);

                String text = EnumChatFormatting.BOLD + Swift.INSTANCE.NAME + EnumChatFormatting.RESET + " | " + Swift.INSTANCE.VERSION; //+ " | Plusbox";

                double iconWidth = iconsFont.getStringWidth("i");

                setWidth((float) iconWidth + (float) vietnamFont.getStringWidth(text) + 12);
                setHeight((float) vietnamFont.getHeight() + 10);

                RenderUtil.drawRoundedRect(getPosX(), getPosY(), getWidth(), getHeight(), 4.0, ColorUtil.DARK_GRAY.getRGB(), 0, 0);

                iconsFont.drawStringWithShadow("i", getPosX() + 3.5, getPosY() + 5.5, -1);
                vietnamFont.drawStringWithShadow(text, getPosX() + iconWidth + 6, getPosY() + 6, -1);
                break;
            }
            case PEAK: {

                // true east coast american codez
                GlStateManager.pushMatrix();
                GlStateManager.scale(2.0f, 2.0f, 2.0f);
                CFontRenderer vietnamFont = FontRenderer.getFont("vietnam.ttf", 18);

                Random random = new SecureRandom(); // CRYPTO STRONG :muscle:
                int red = random.nextInt(256);
                int green = random.nextInt(256);
                int blue = random.nextInt(256);

                Color randomColor = new Color(red, green, blue);

                // Create a Color object with the random components
                if (random.nextBoolean())
                    vietnamFont.drawStringWithShadow("s\247fwift.cc", 4, 4, randomColor.getRGB());
                else
                    mc.fontRendererObj.drawStringWithShadow("s\247fwift.cc", 4, 4, randomColor.getRGB());
                GlStateManager.popMatrix();
                break;
            }
        }
    }
}
