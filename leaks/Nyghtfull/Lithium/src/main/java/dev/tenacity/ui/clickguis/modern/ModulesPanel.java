package dev.tenacity.ui.clickguis.modern;

import dev.tenacity.Tenacity;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.ModuleCollection;
import dev.tenacity.module.impl.render.ClickGUIMod;
import dev.tenacity.ui.clickguis.modern.components.ModuleRect;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.misc.HoveringUtil;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.objects.Scroll;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import lombok.Getter;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class ModulesPanel extends Panel {

    public List<ModuleRect> modules;
    public Animation expandAnim;
    public Animation expandAnim2;
    Scroll settingScroll = new Scroll();
    private HashMap<Module, SettingsPanel> settingsPanelHashMap;
    private HashMap<Category, Scroll> scrollHashMap;
    private boolean rightClicked = false;
    public Category currentCategory;
    @Getter
    private boolean typing = false;
    private ModuleRect currentlySelected;

    @Override
    public void initGui() {
        expandAnim2 = new DecelerateAnimation(300, 1, Direction.FORWARDS);
        expandAnim2.setDirection(Direction.BACKWARDS);
        if (scrollHashMap == null) {
            scrollHashMap = new HashMap<>();
            for (Category category : Category.values()) {
                scrollHashMap.put(category, new Scroll());
            }
            //this is so damn aids but it just might work
            scrollHashMap.put(null, new Scroll());
        }

        refreshSettingMap();

    }

    public void refreshSettingMap() {
        if (settingsPanelHashMap == null || ModuleCollection.reloadModules) {
            settingsPanelHashMap = new HashMap<>();
            for (Module module : Tenacity.INSTANCE.getModuleCollection().getModules()) {
                SettingsPanel settingsPanel = new SettingsPanel(module);
                settingsPanel.initGui();
                settingsPanelHashMap.put(module, settingsPanel);
            }
        } else {
            settingsPanelHashMap.forEach((m, p) -> p.initGui());
        }
    }


    @Override
    public void keyTyped(char typedChar, int keyCode) {
        modules.forEach(moduleRect -> moduleRect.keyTyped(typedChar, keyCode));

        if (currentlySelected != null) {
            settingsPanelHashMap.get(currentlySelected.module).keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        typing = false;
        if (ModuleCollection.reloadModules) {
            initGui();
            ModuleCollection.reloadModules = false;
            return;
        }


        expandAnim2.setDirection(rightClicked ? Direction.FORWARDS : Direction.BACKWARDS);
        int spacing = 0;
        Scroll scroll = scrollHashMap.get(currentCategory);

        if (!(HoveringUtil.isHovering(x + (250 + (55 * expandAnim.getOutput().floatValue())), y, 135, 250, mouseX, mouseY) && currentlySelected != null) &&
                !Tenacity.INSTANCE.getSideGui().isFocused()) {
            scroll.onScroll(25);
        }
        double scrollVal = MathUtils.roundToHalf(scroll.getScroll());

        lithiumFont18.drawCenteredString("Click your scroll wheel while hovering a module to change a keybind", x + 305 / 2f, (float) (y - 15 + scrollVal),
                new Color(128, 134, 141, 150));


        scroll.setMaxScroll(Math.max(0, (modules.size() - 4) * 50));
        for (ModuleRect module : modules) {
            module.rectWidth = (float) (305);
            module.x = this.x;
            module.y = (float) (this.y + spacing + scrollVal);
            module.bigRecty = this.bigRecty;

            if (!typing) {
                typing = module.binding != null;
            }

            module.drawSettingThing = currentlySelected == module;

            module.drawScreen(mouseX, mouseY);
            spacing += 50;


            if (module.rightClicked) {
                if (currentlySelected == module) {
                    rightClicked = false;
                    currentlySelected = null;
                    module.rightClicked = false;
                    continue;
                } else {
                    rightClicked = true;
                }
                settingScroll = new Scroll();
                currentlySelected = module;
                module.rightClicked = false;
            }
        }


        if (currentlySelected != null) {
            if (HoveringUtil.isHovering(x + (float) (305), y, 135, 250, mouseX, mouseY)) {
                settingScroll.onScroll(25);
            }

            //  settingScroll.setMaxScroll(Math.max(0,(Mo - 4) * 50));
            float newX = x + 5 + (float) (305);
            RoundedUtil.drawRound(newX, y - 20, 130, 255, 8, new Color(47, 49, 54));
            //       RenderUtil.renderRoundedRect(newX, y - 15, 130, 255, 10, new Color(47, 49, 54).getRGB());
            RenderUtil.setAlphaLimit(0);
            Gui.drawGradientRect2(newX - .5f, y, 130.5f, 8, new Color(0, 0, 0, 70).getRGB(), new Color(0, 0, 0, 0).getRGB());
            lithiumBoldFont22.drawCenteredString(currentlySelected.module.getName(), newX + 125 / 2f, y - 15, -1);

            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtil.scissor(newX, y + .5f, 135, 255);

            SettingsPanel settingsPanel = settingsPanelHashMap.get(currentlySelected.module);
            settingScroll.setMaxScroll(Math.max(0, settingsPanel.maxScroll - 100));
            settingsPanel.x = x + 305;
            settingsPanel.y = (float) (y - 20 + MathUtils.roundToHalf(settingScroll.getScroll()));
            settingsPanel.drawScreen(mouseX, mouseY);

            if (!typing) {
                typing = settingsPanel.typing;
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }

        ClickGUIMod.modernClickGui.adjustWidth((125 * expandAnim2.getOutput().floatValue()));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        modules.forEach(moduleRect -> moduleRect.mouseClicked(mouseX, mouseY, button));
        if (currentlySelected != null) {
            settingsPanelHashMap.get(currentlySelected.module).mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        modules.forEach(moduleRect -> moduleRect.mouseReleased(mouseX, mouseY, state));
        if (currentlySelected != null) {
            settingsPanelHashMap.get(currentlySelected.module).mouseReleased(mouseX, mouseY, state);
        }
    }
}
