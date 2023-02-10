package dev.myth.ui.clickgui.blubgui;

import dev.myth.api.feature.Feature;
import dev.myth.api.logger.Logger;
import dev.myth.api.setting.Setting;
import dev.myth.api.utils.StringUtil;
import dev.myth.api.utils.font.FontLoaders;
import dev.myth.api.utils.mouse.MouseUtil;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.api.utils.render.animation.Animation;
import dev.myth.api.utils.render.animation.Easings;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import dev.myth.ui.clickgui.blubgui.module.RectModule;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class BlubUI extends GuiScreen {


    public static BlubUI INSTANCE;

    public double width = 400, height = 270;
    public double x = 300, y = 200;

    public Animation animation;

    public Feature.Category category = Feature.Category.COMBAT;

    public ArrayList<RectModule> modulePanels = new ArrayList<>();

    public boolean barExtended;

    private double scrolled, totalHeight, scrollSpeed;
    public double modHeight = 37;

    public float extendedWidth = 18;

    public BlubUI() {
        this.INSTANCE = this;
        this.animation = new Animation();
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        ScaledResolution sr = new ScaledResolution(mc);
        animation.updateAnimation();

        /** Main Rect */
        RenderUtil.drawRect(x, y, width, height, new Color(23, 25,24).getRGB());

        /** Sidebar */
        RenderUtil.drawRect(x + 35 + extendedWidth * animation.getValue(), y, width, height, new Color(15, 17,16).getRGB());
        RenderUtil.drawRect(x + 35 + extendedWidth * animation.getValue(), y, 1f, height, new Color(32, 35,34).getRGB());

        /** Free Top G Text :scream: */
        FontLoaders.BOLD_22.drawString("#FreeTopG", (float) (x + 57), (float) y + 18, -1);

        /** Bar Circle */
        RenderUtil.drawCircle(x + 35 + extendedWidth * animation.getValue(), y + 32, 5, new Color(23, 25,24).getRGB(), new Color(32, 35,34).getRGB(), 0.5f);
        FontLoaders.BOLD_13.drawString(barExtended ? "<" : ">", (float) (x + 34 + extendedWidth * animation.getValue()), (float) y + 30, Color.GRAY.darker().getRGB());

        /** Other "Category" select bar */
        RenderUtil.drawRoundedRect((float) (x + 57), (float) y + 37, width - 40, 25, 5, new Color(23, 25,24).getRGB(), new Color(32, 35,34).getRGB(), 1f);
        FontLoaders.BOLD_13.drawString(category.getName().substring(0,1).toUpperCase() + category.getName().substring(1).toLowerCase(), (float) (x + 68), (float) y + 47, new Color(236, 58, 161).getRGB());
        RenderUtil.drawRect((float) (x + 68), (float) y + 61, FontLoaders.BOLD_13.getStringWidth(category.getName()), 0.7, new Color(236, 58, 161).getRGB());

        /** Draw Scrollbar Background */
        RenderUtil.drawRoundedRect(x + width + 23 , y + 70, 4, 180, 0, new Color(21, 25, 24).getRGB(), new Color(32, 35, 34).getRGB(), 1f);
        RenderUtil.drawRect(x + width + 24, y + 72, 2, 10, new Color(236, 58, 161));

        /** for calculation */
        double leftHeight = 0;
        double rightHeight = 0;

        /** Draw Icons at the Sidebar */
        AtomicInteger icoY = new AtomicInteger(0);
        for (Feature.Category cat : Feature.Category.values()) {

            /** Draws the icon */
            RenderUtil.drawImage(x + 12,y + 50 + icoY.get(), 12, 12, new ResourceLocation("myth/" + cat.getName() + ".png"), cat == category ? Color.WHITE : Color.WHITE.darker().darker());

            /** draw the name when the bar is extended*/
            if (barExtended)
                FontLoaders.BOLD_18.drawString(cat.getName().substring(0,1).toUpperCase(), (float) (x + 34), (float) (y + 52 + icoY.get()), cat == category ? Color.WHITE.getRGB() : Color.WHITE.darker().darker().getRGB());

            /** Select the category when click */
            if (MouseUtil.isHovered(mouseX, mouseY, x + 12, y + 50 + icoY.get(), 12, 12) && Mouse.isButtonDown(0)) { /** i will change it later since it dosent matter if i would put it in a event */
                category = cat;

                /** Add all features in the new current category to a button arraylist*/
                modulePanels.clear(); /** Clear the list since we only need the modules from the new category */
                for (Feature feature : ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeaturesInCategory(category)) {
                    float mHeight = 25;

                    for (Setting s : feature.getSettings()) {
                        
                    }



                    if (rightHeight < leftHeight) {
                        modulePanels.add(new RectModule(feature.getName(), (int) (x + 57 + 185), (int) ((float) y + 70 + rightHeight), 168, mHeight));
                        rightHeight += modHeight;
                    }else {
                        modulePanels.add(new RectModule(feature.getName(), (int) (x + 57), (int) ((float) y + 70 + leftHeight), 168, mHeight));
                        leftHeight += modHeight;
                    }
                }
            }

            /** Draw the line at the side when cat selected */
            if (cat == category)
                RenderUtil.drawRect(x + 34 + extendedWidth * animation.getValue(), y + 49 + icoY.get(), 1.3, 16, new Color(236, 58, 161).getRGB());

            icoY.set(icoY.get() + 27);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
        modulePanels.forEach(panel -> panel.drawComponent(mouseX, mouseY));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        /** Extend the bar when clicking on the circle */
        if (MouseUtil.isHovered(mouseX, mouseY, x + 32 + extendedWidth * animation.getValue(), y + 28, 10, 8) && mouseButton == 0) {
            barExtended = !barExtended;
            animation.animate(barExtended ? 1 : 0, 200, Easings.NONE);
        }
        modulePanels.forEach(panel -> panel.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        modulePanels.forEach(panel -> panel.mouseReleased(mouseX, mouseY));
    }
}
