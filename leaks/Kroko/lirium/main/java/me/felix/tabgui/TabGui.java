package me.felix.tabgui;

import com.fasterxml.jackson.databind.Module;
import de.lirium.Client;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.render.FontRenderer;
import de.lirium.util.render.RenderUtil;
import de.lirium.util.render.StencilUtil;
import god.buddy.aot.BCompiler;
import me.felix.tabgui.handlers.TabGUICategory;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TabGui {

    private float deltaAnimation = 0;

    public final int width = 80;

    private ModuleFeature selectedFeature = null;

    private final ArrayList<TabGUICategory> categories = new ArrayList<>();

    public ModuleFeature.Category selectedCategory;

    public boolean expanded;

    public TabGui() {
        this.selectedCategory = ModuleFeature.Category.COMBAT;

        for (ModuleFeature.Category category : ModuleFeature.Category.values()) {
            categories.add(new TabGUICategory(category));
        }
    }

    private List<ModuleFeature> modules = new ArrayList<>();

    private FontRenderer fontRenderer;

    private final Color color = new Color(45, 45, 45), color2 = new Color(24, 24, 26);

    public void renderTabGui(final int x, final int y) {
        final AtomicInteger yAxis = new AtomicInteger(0);

        final AtomicInteger height = new AtomicInteger(0);

        categories.forEach(moduleFeature -> height.addAndGet(20));

        RenderUtil.drawRoundedRect(x, y + 17, width, height.get(), 3, color2);

        for (TabGUICategory category : categories)
            category.doRender(x + 3, y + yAxis.addAndGet(20) + 1);


        if (selectedFeature == null || selectedFeature.getCategory() != selectedCategory) {
            modules.clear();
            modules.addAll(Client.INSTANCE.getModuleManager().getFeatures().stream().filter(moduleFeature -> moduleFeature.getCategory() == selectedCategory).collect(Collectors.toList()));
        }

        if (expanded && modules.isEmpty()) {
            expanded = false;
        }

        final AtomicInteger heightModules = new AtomicInteger(0);

        modules.forEach(moduleFeature -> heightModules.addAndGet(20));


        if ((selectedFeature == null || selectedFeature.getCategory() != selectedCategory) && expanded)
            selectedFeature = modules.get(0);

        deltaAnimation = (float) RenderUtil.getAnimationState(deltaAnimation, expanded && !modules.isEmpty() ? heightModules.get() + 4 : 0, 510 * (modules.size() / 4f));

        if (deltaAnimation == 0.0)
            this.expanded = false;

        if (deltaAnimation == 0.0 || deltaAnimation == 0) return;

        final int xModule = x - width - 7;

        RenderUtil.drawRoundedRect(xModule, y + 17, width, deltaAnimation, 3, color2);
        StencilUtil.init();
        RenderUtil.drawRoundedRect(xModule, y + 17, width, deltaAnimation, 3, color2);
        StencilUtil.readBuffer(1);
        final AtomicInteger moduleYAdditional = new AtomicInteger(17);

        if (fontRenderer == null)
            fontRenderer = Client.INSTANCE.getFontLoader().get("arial", 19);

        for (ModuleFeature feature : modules) {
            if (selectedFeature == feature)
                RenderUtil.drawRoundedRect(xModule + 4, y + moduleYAdditional.get() + 4, width - 8, 16, 3, color);

            if (!feature.isEnabled())
                fontRenderer.drawString(feature.getName(), calculateMiddle(feature.getName(), fontRenderer, xModule, width - 2), y + 6 + moduleYAdditional.get(), -1);
            else {
                fontRenderer.drawRainbowString(feature.getName(), calculateMiddle(feature.getName(), fontRenderer, xModule, width - 2), y + 6 + moduleYAdditional.get());
            }
            moduleYAdditional.addAndGet(20);
        }
        StencilUtil.uninit();
    }

    public int calculateMiddle(String text, FontRenderer fontRenderer, double x, double width) {
        return (int) ((float) (x + width) - (fontRenderer.getStringWidth(text) / 2f) - (float) width / 2);
    }


    public void handleKeys(final int key) {
        if (key != Keyboard.KEY_DOWN && key != Keyboard.KEY_UP && key != Keyboard.KEY_LEFT && key != Keyboard.KEY_RIGHT && key != Keyboard.KEY_RETURN)
            return;

        switch (key) {
            case Keyboard.KEY_DOWN: {
                if (!expanded)
                    selectedCategory = (ModuleFeature.Category.values()[(selectedCategory.ordinal() + 1) % ModuleFeature.Category.values().length]);
                else
                    selectedFeature = (getFeaturesInCategory(selectedCategory).get((getFeaturesInCategory(selectedCategory).indexOf(selectedFeature) + 1) % getFeaturesInCategory(selectedCategory).size()));
                break;
            }
            case Keyboard.KEY_UP: {
                if (!expanded)
                    selectedCategory = (ModuleFeature.Category.values()[(selectedCategory.ordinal() - 1) < 0 ? ModuleFeature.Category.values().length - 1 : (selectedCategory.ordinal() - 1)]);
                else
                    selectedFeature = getFeaturesInCategory(selectedCategory).get((getFeaturesInCategory(selectedCategory).indexOf(selectedFeature) - 1) < 0 ? getFeaturesInCategory(selectedCategory).size() - 1 : (getFeaturesInCategory(selectedCategory).indexOf(selectedFeature) - 1));
                break;
            }
            case Keyboard.KEY_LEFT: {
                if (selectedCategory != null)
                    expanded = true;
                break;
            }

            case Keyboard.KEY_RIGHT: {
                if (expanded) {
                    this.expanded = false;
                }
            }

            case Keyboard.KEY_RETURN: {
                if (expanded)
                    this.selectedFeature.setEnabled(!selectedFeature.isEnabled());
                break;
            }
        }
    }

    public List<ModuleFeature> getFeaturesInCategory(ModuleFeature.Category category) {
        return Client.INSTANCE.getModuleManager().getFeatures().stream().filter(moduleFeature -> moduleFeature.getCategory() == category).collect(Collectors.toList());
    }

}
