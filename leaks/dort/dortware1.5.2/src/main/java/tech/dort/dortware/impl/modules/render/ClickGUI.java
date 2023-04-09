package tech.dort.dortware.impl.modules.render;

import net.minecraft.util.ResourceLocation;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.impl.gui.click.ClickGuiScreen;
import tech.dort.dortware.impl.gui.click.PaneState;
import tech.dort.dortware.impl.gui.click.element.impl.CategoryPane;
import tech.dort.dortware.impl.utils.render.ColorUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClickGUI extends Module {

    private ClickGuiScreen uiScreen;

    private final List<PaneState> preloadPaneStates = new ArrayList<>();

    public final NumberValue red = new NumberValue("Red", this, 255, 0, 255, true);
    public final NumberValue green = new NumberValue("Green", this, 50, 0, 255, true);
    public final NumberValue blue = new NumberValue("Blue", this, 50, 0, 255, true);
    public final NumberValue alpha = new NumberValue("Gradient Alpha", this, 150, 0, 255, true);
    public final BooleanValue booleanValue2 = new BooleanValue("Gradient", this, false);
    private final BooleanValue booleanValue1 = new BooleanValue("Rainbow", this, false);
    public final BooleanValue booleanValue = new BooleanValue("Blur", this, true);

    public ClickGUI(ModuleData moduleData) {
        super(moduleData);
        register(red, green, blue, alpha, booleanValue2, booleanValue1, booleanValue);
    }

    public int getGuiColor() {
        if (booleanValue1.getValue()) {
            return ColorUtil.rainbow(-6000, 0);
        }

        double red1 = red.getCastedValue();
        double green1 = green.getCastedValue();
        double blue1 = blue.getCastedValue();

        Color color = new Color((float) red1 / 255.0f, (float) green1 / 255.0f, (float) blue1 / 255.0f);

        return color.getRGB();
    }

    @Override
    public void onEnable() {
        if (uiScreen == null) {
            uiScreen = new ClickGuiScreen();
            preloadPaneStates.forEach(this::setPaneState);
        }

        if (booleanValue.getValue()) {
            mc.entityRenderer.theShaderGroup = null;
            mc.entityRenderer.func_175069_a(new ResourceLocation("shaders/post/blur.json"));
        }

        mc.displayGuiScreen(uiScreen);
        toggle();
    }

    public void addPreloadPaneState(PaneState paneState) {
        this.preloadPaneStates.add(paneState);
    }

    public void setPaneState(PaneState paneState) {
        for (CategoryPane pane : uiScreen.getCategoryPanes()) {
            if (pane.getCategory().getName().equalsIgnoreCase(paneState.getName())) {
                pane.setPos(paneState.getPosX(), paneState.getPosY());
                pane.setExpanded(paneState.isExpanded());
            }
        }
    }

    public List<PaneState> getPaneStates() {
        final List<PaneState> paneStates = new ArrayList<>();
        if (uiScreen == null) {
            return null;
        }
        uiScreen.getCategoryPanes().forEach(pane -> paneStates.add(new PaneState(pane.getCategory().getName(), pane.getPosX(), pane.getPosY(), pane.isExpanded())));
        return paneStates;
    }

}