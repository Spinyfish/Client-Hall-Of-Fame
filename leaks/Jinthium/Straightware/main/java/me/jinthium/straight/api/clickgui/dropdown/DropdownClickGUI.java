package me.jinthium.straight.api.clickgui.dropdown;

import me.jinthium.straight.api.config.LocalConfig;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.utils.animation.Animation;
import me.jinthium.straight.impl.utils.animation.Direction;
import me.jinthium.straight.impl.utils.animation.impl.EaseBackIn;
import me.jinthium.straight.impl.utils.render.ColorUtil;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import me.jinthium.straight.impl.utils.tuples.Pair;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjglx.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DropdownClickGUI extends GuiScreen {

    private final Pair<Animation, Animation> openingAnimations = Pair.of(
            new EaseBackIn(400, 1, 2f),
            new EaseBackIn(400, .4f, 2f));


    private List<CategoryPanel> categoryPanels;

    public boolean binding;


    public static boolean gradient;

    @Override
    public void onDrag(int mouseX, int mouseY) {
        for (CategoryPanel catPanels : categoryPanels) {
            catPanels.onDrag(mouseX, mouseY);
        }
        super.onDrag(mouseX, mouseY);
    }

    @Override
    public void initGui() {
        openingAnimations.use((fade, opening) -> {
            fade.setDirection(Direction.FORWARDS);
            opening.setDirection(Direction.FORWARDS);
        });


        if (categoryPanels == null) {
            categoryPanels = new ArrayList<>();
            for (Module.Category category : Module.Category.values()) {
                categoryPanels.add(new CategoryPanel(category, openingAnimations));
            }
        }

        for (CategoryPanel catPanels : categoryPanels) {
            catPanels.initGui();
        }
        super.initGui();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE && !binding) {
            openingAnimations.use((fade, opening) -> {
                fade.setDirection(Direction.BACKWARDS);
                opening.setDirection(Direction.BACKWARDS);
            });
        }
        categoryPanels.forEach(categoryPanel -> categoryPanel.keyTyped(typedChar, keyCode));
        try{
            super.keyTyped(typedChar, keyCode);
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        binding = categoryPanels.stream().anyMatch(CategoryPanel::isTyping);


        // If the closing animation finished then change the gui screen to null
        if (openingAnimations.getSecond().finished(Direction.BACKWARDS)) {
            mc.displayGuiScreen(null);
            return;
        }

        gradient = false;


        int fakeMouseX = mouseX, fakeMouseY =  mouseY;
        ScaledResolution sr = ScaledResolution.fetchResolution(mc);


        RenderUtil.scaleStart(sr.getScaledWidth() / 2f, sr.getScaledHeight() / 2f, openingAnimations.getSecond().getOutput().floatValue() + .6f);

        for (CategoryPanel catPanels : categoryPanels) {
            catPanels.drawScreen(fakeMouseX, fakeMouseY);
        }
        RenderUtil.scaleEnd();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void renderEffects() {
        ScaledResolution sr = ScaledResolution.fetchResolution(mc);
        RenderUtil.scaleStart(sr.getScaledWidth() / 2f, sr.getScaledHeight() / 2f, openingAnimations.getSecond().getOutput().floatValue() + .6f);
        for (CategoryPanel catPanels : categoryPanels) {
            catPanels.renderEffects();
        }
        RenderUtil.scaleEnd();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        categoryPanels.forEach(cat -> cat.mouseClicked(mouseX, mouseY, mouseButton));
       try{
           super.mouseClicked(mouseX, mouseY, mouseButton);
       }catch (IOException ex){
           ex.printStackTrace();
       }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        categoryPanels.forEach(cat -> cat.mouseReleased(mouseX, mouseY, state));
        super.mouseReleased(mouseX, mouseY, state);
    }
}
