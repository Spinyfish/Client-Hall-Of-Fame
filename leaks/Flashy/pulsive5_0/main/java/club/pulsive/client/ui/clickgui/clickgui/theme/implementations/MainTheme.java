package club.pulsive.client.ui.clickgui.clickgui.theme.implementations;


import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.api.font.Fonts;
import club.pulsive.api.main.Pulsive;
import club.pulsive.client.ui.clickgui.clickgui.component.implementations.BooleanComponent;
import club.pulsive.client.ui.clickgui.clickgui.component.implementations.ColorPickerComponent;
import club.pulsive.client.ui.clickgui.clickgui.component.implementations.EnumComponent;
import club.pulsive.client.ui.clickgui.clickgui.component.implementations.SliderComponent;
import club.pulsive.client.ui.clickgui.clickgui.panel.implementations.CategoryPanel;
import club.pulsive.client.ui.clickgui.clickgui.panel.implementations.ModulePanel;
import club.pulsive.client.ui.clickgui.clickgui.panel.implementations.MultiSelectPanel;
import club.pulsive.client.ui.clickgui.clickgui.theme.Theme;
import club.pulsive.impl.event.client.ShaderEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.impl.misc.ClientSettings;
import club.pulsive.impl.module.impl.visual.HUD;
import club.pulsive.impl.util.render.RenderUtil;
import club.pulsive.impl.util.render.RoundedUtil;
import club.pulsive.impl.util.render.StencilUtil;
import club.pulsive.impl.util.render.animations.Animation;
import club.pulsive.impl.util.render.animations.Direction;
import club.pulsive.impl.util.render.animations.impl.EaseInOutRect;
import club.pulsive.impl.util.render.animations.impl.MainAnimations;
import club.pulsive.impl.util.render.secondary.ShaderRound;
import club.pulsive.impl.util.render.shaders.Bloom;
import club.pulsive.impl.util.render.shaders.Blur;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class MainTheme implements Theme {
    @Override
    public void drawCategory(CategoryPanel panel, float x, float y, float width, float height) {
        String name = StringUtils.upperSnakeCaseToPascal(panel.getCategory().name());
        StencilUtil.initStencilToWrite();
        RoundedUtil.drawRoundedRect(x, y,  x + width,  y + height -1, 5,new Color(0,0,0,100).getRGB());
        StencilUtil.readStencilBuffer(1);
        Blur.renderBlur(15);
        StencilUtil.uninitStencilBuffer();
        RoundedUtil.drawRoundedRect(x, y,  x + width,  y + height - 1, 5,new Color(0,0,0,100).getRGB());
        String categoryIcon = panel.getCategory() == Category.VISUALS ? "t" : panel.getCategory() == Category.COMBAT ? "s" : panel.getCategory() == Category.MOVEMENT ? "u" : panel.getCategory() == Category.PLAYER ? "o" : panel.getCategory() == Category.EXPLOIT ? "r" :
                panel.getCategory() == Category.CLIENT ? "p" : panel.getCategory() == Category.MISC ? "v" : "e";
        Fonts.googleMedium.drawString(name, x + 4, y + panel.height() / 2 - Fonts.moontitle.getHeight() / 2 + 2, new Color(220,220,220,255).getRGB());
        Fonts.undefeated.drawString(categoryIcon, x + 4 + Fonts.googleMedium.getStringWidth(name) + 3, y + panel.height() / 2 - Fonts.moontitle.getHeight() / 2 + 2, RenderUtil.applyOpacity(ClientSettings.mainColor.getValue(), 0.3f).getRGB());
        if(panel.isExtended())
        RenderUtil.drawGradientRect(x, y + 16 - 1, x + width, y + 16 + 1 - 1, new Color(30,30,30,50).getRGB(), new Color(30,30,30,30).getRGB(), false);
        Gui.drawRect(0, 0, 0, 0, -1);
//        Pulsive.INSTANCE.getBlurrer().bloom(Math.round(x) - 3, Math.round(y) - 3, Math.round(width) + 3, Math.round(height) + 3, 15, 100);
    }

    @Override
    public void drawModule(ModulePanel panel, float x, float y, float width, float height) {
       
        //ShaderRound.drawRound(x, y, width, height, 0,new Color(0xff212120));
        Animation hoverAnimation = panel.getAnimation();
       
        Color main = RenderUtil.applyOpacity(ClientSettings.mainColor.getValue(), (float) hoverAnimation.getOutput());
        Color second = RenderUtil.applyOpacity(ClientSettings.secondColor.getValue(), (float) hoverAnimation.getOutput());
        if(panel.isExtended()) {
            if(panel.getModule().isToggled()) {
                Gui.drawRect(x, y + panel.height() - 1, x + width, y + panel.totalHeight(), RenderUtil.applyOpacity(ClientSettings.mainColor.getValue(), 0.3f).darker().getRGB());
            } else {
                Gui.drawRect(x, y, x + width, y + panel.totalHeight(), new Color(0,0,0,100).getRGB());

            }
        }
        RoundedUtil.drawRoundedRect(x, y - 1,  x + width,  y + height - 1, 0,panel.getModule().isToggled() ? RenderUtil.applyOpacity(ClientSettings.mainColor.getValue(), 0.3f).getRGB() : new Color(0,0,0,0).getRGB());
        RenderUtil.color(panel.getModule().isToggled() ? -1 : new Color(220,220,220,255).getRGB());
        Fonts.google.drawString(panel.getModule().getName(), x + 4, y + panel.height() / 2 - Fonts.moon.getHeight() / 2 - 0.5f, -1);
    }



    @Override
    public void drawMulti(MultiSelectPanel panel, float x, float y, float width, float height) {
        if (panel.isExtended()) {
           // int count = 0;
            panel.count = 0;
            ShaderRound.drawRound(x, y, width, panel.height(), 0, new Color(21,21,21,70));
            for(int i = 0; i < panel.getProperty().getValues().length; i++){
                String enumName = panel.getProperty().getValues()[i].toString();
                Fonts.googleSmall.drawString(enumName, x + 4, y + Fonts.googleSmall.getHeight() * 3 + panel.count,
                        panel.getProperty().isSelected(panel.getProperty().getValues()[i]) ? HUD.getColor() : -1);
                panel.count += Fonts.googleSmall.getHeight() + 4;
            }
//            for (Enum e :panel.getProperty().getValues()){
//                GL11.glPushMatrix();
//
//                GL11.glPopMatrix();
//                GlStateManager.color(1, 1, 1, 1);
//                RenderUtil.color(-1);
//                Fonts.googleSmall.drawString(panel.getProperty().getValues(), x + 4, y + Fonts.googleSmall.getHeight() * 3 + panel.count, panel.getProperty().isSelected(e) ? HUD.getColor() : -1);
//                panel.count += Fonts.googleSmall.getHeight() + 4;
//            }
            panel.setHeight(Fonts.moon.getHeight() + panel.count * 1.3f);
        }
        else {
            panel.setHeight(panel.origHeight());
            Fonts.googleSmall.drawString("RBUTTON...", x + width - Fonts.sfSmall.getStringWidth("RBUTTON...") - 2, y + panel.height() / 2 - Fonts.sfSmall.getHeight() / 2 - 0.5f, -1);
        }
            Fonts.googleSmall.drawString(panel.getProperty().getLabel(), x + 4, y + 4, -1);
    }
    

    @Override
    public void drawBindComponent(Module module, float x, float y, float width, float height, boolean focused) {
        String text = "Bind: [" + (focused ? " " : Keyboard.getKeyName(module.getKeyBind())) + "]";
        Fonts.googleSmall.drawString(text, x + 4, y + height / 2 - Fonts.moon.getHeight() / 2, -1);
    }

    @Override
    public void drawBooleanComponent(BooleanComponent component, float x, float y, float width, float height, float settingWidth, float settingHeight, int opacity) {
        String label = component.getSetting().getLabel();
        Fonts.googleSmall.drawString(label, x + 4, y + component.height() / 2 - Fonts.moon.getHeight() / 2, -1);
        ShaderRound.drawRound(x + width - settingWidth + 1 - 3 , y + height / 2 - settingHeight / 2 + 3 - 3,6,6, 3, component.getSetting().getValue() ? new Color(-1) : new Color(0xff0F0F0F));
  
    }

    @Override
    public void drawEnumComponent(EnumComponent component, float x, float y, float width, float height) {
        String label = component.getSetting().getLabel();
        Fonts.googleSmall.drawString(label + ": " + component.getSetting().getValue().toString(), x + 4, y + component.height() / 2 - Fonts.moon.getHeight() / 2 - 0.5f, -1);
    }

    @Override
    public void drawSliderComponent(SliderComponent component, float x, float y, float width, float height, float length) {
        Gui.drawRect(x, y+ height- 6, x + length - 5, y + height - 3, -1);
       // Draw.drawBorderedCircle(x + length, y+ height - 5, 3, -1,-1);
        RenderUtil.drawCGuiCircle(x + length - 1.4f, y + height + 1 - 5.5f, 4f, Color.WHITE.getRGB());
        String rep = "";
        switch (component.getSetting().representation()) {
            case INT:
                rep = "";
            case DOUBLE:
                rep = "";
                break;
            case DISTANCE:
                rep = "m/s";
                break;
            case PERCENTAGE:
                rep = "%";
                break;
            case MILLISECONDS:
                rep = "ms";
                break;
        }
        Fonts.googleSmall.drawString(component.getSetting().getLabel() + ": " + component.getSetting().getDisplayString() + rep, x + 4, y + height / 2 - Fonts.moon.getHeight() / 2 - 2, -1);
    }

    @Override
    public void drawColorPickerComponent(ColorPickerComponent component, float x, float y, float width, float height) {
        Color color = component.getSetting().getValue();
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        float hue = hsb[0];
        RenderUtil.drawRect(x, y, x + width, y + height, Color.getHSBColor(hsb[0], 1, 1).getRGB());
        int brightnessMin = RenderUtil.toColorRGB(Color.HSBtoRGB(hue, 0, 1), 0).getRGB();
        int brightnessMax = RenderUtil.toColorRGB(Color.HSBtoRGB(hue, 0, 1), 255).getRGB();
        int saturationMin = RenderUtil.toColorRGB(Color.HSBtoRGB(hue, 1, 0), 0).getRGB();
        int saturationMax = RenderUtil.toColorRGB(Color.HSBtoRGB(hue, 1, 0), 255).getRGB();
        RenderUtil.drawGradientRect(x, y, x + width, y + height, brightnessMin, brightnessMax, true);
        Gui.drawGradientRect(x, y, x + width, y + height, saturationMin, saturationMax);
        Fonts.googleSmall.drawCenteredString(component.getSetting().getLabel(), x + width / 2, y - 6, new Color(240,240,240,250).getRGB());
    }

}
