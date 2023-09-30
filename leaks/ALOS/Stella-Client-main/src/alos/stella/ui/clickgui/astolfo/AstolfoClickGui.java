package alos.stella.ui.clickgui.astolfo;

import alos.stella.Stella;
import alos.stella.module.ModuleCategory;
import alos.stella.module.modules.visual.ClickGUI;
import alos.stella.module.modules.visual.HUD;
import alos.stella.ui.clickgui.astolfo.buttons.AstolfoButton;
import alos.stella.ui.clickgui.astolfo.buttons.AstolfoCategoryPanel;
import alos.stella.ui.clickgui.astolfo.buttons.AstolfoModuleButton;
import alos.stella.utils.ColorUtils;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class AstolfoClickGui extends GuiScreen {

    public ArrayList<AstolfoCategoryPanel> categoryPanels = new ArrayList<>();

    public AstolfoClickGui() {

        int count = 4;

        for(ModuleCategory cat : ModuleCategory.values()) {
            switch (cat) {
                case COMBAT:
                    categoryPanels.add(new AstolfoCategoryPanel(count, 4, 100, 18, cat, new Color(ClickGUI.colorRedValue.get(), ClickGUI.colorGreenValue.get(), ClickGUI.colorBlueValue.get())));
                    break;
                case MOVEMENT:
                    categoryPanels.add(new AstolfoCategoryPanel(count, 4, 100, 18, cat, new Color(ClickGUI.colorRedValue.get(), ClickGUI.colorGreenValue.get(), ClickGUI.colorBlueValue.get())));
                    break;
                case PLAYER:
                    categoryPanels.add(new AstolfoCategoryPanel(count, 4, 100, 18, cat, new Color(ClickGUI.colorRedValue.get(), ClickGUI.colorGreenValue.get(), ClickGUI.colorBlueValue.get())));
                    break;
                case WORLD:
                    categoryPanels.add(new AstolfoCategoryPanel(count, 4, 100, 18, cat, new Color(ClickGUI.colorRedValue.get(), ClickGUI.colorGreenValue.get(), ClickGUI.colorBlueValue.get())));
                    break;
                case MISC:
                    categoryPanels.add(new AstolfoCategoryPanel(count, 4, 100, 18, cat, new Color(ClickGUI.colorRedValue.get(), ClickGUI.colorGreenValue.get(), ClickGUI.colorBlueValue.get())));
                    break;
                case VISUAL:
                    categoryPanels.add(new AstolfoCategoryPanel(count, 4, 100, 18, cat, new Color(ClickGUI.colorRedValue.get(), ClickGUI.colorGreenValue.get(), ClickGUI.colorBlueValue.get())));
                    break;

            }

            count += 120;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //this spam module.json save
        switch (Objects.requireNonNull(Stella.moduleManager.getModule(ClickGUI.class)).backgroundValue.get()) {
            case "Gradient":
                drawGradientRect(0, 0, width, height,
                        ColorUtils.reAlpha(new HUD().getColor(1), Objects.requireNonNull(Stella.moduleManager.getModule(ClickGUI.class)).gradEndValue.get()).getRGB(),
                        ColorUtils.reAlpha(new HUD().getColor(1), Objects.requireNonNull(Stella.moduleManager.getModule(ClickGUI.class)).gradStartValue.get()).getRGB());
                break;
            default:
                break;
        }
        //this spam module.json save
        for(AstolfoCategoryPanel catPanel : categoryPanels) {
            catPanel.drawPanel(mouseX, mouseY);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
            for(AstolfoCategoryPanel catPan : categoryPanels) {
                catPan.mouseAction(mouseX, mouseY, true, mouseButton);

                if(catPan.open) {
                    for(AstolfoModuleButton modPan : catPan.moduleButtons) {
                        modPan.mouseAction(mouseX, mouseY, true, mouseButton);
                        if(modPan.extended) {
                            for(AstolfoButton pan : modPan.astolfoButtons) {
                                pan.mouseAction(mouseX, mouseY, true, mouseButton);
                        }
                    }
                }
            }
        };
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
            for(AstolfoCategoryPanel catPan : categoryPanels) {
                catPan.mouseAction(mouseX, mouseY, false, state);

                if(catPan.open) {
                    for(AstolfoModuleButton modPan : catPan.moduleButtons) {
                        modPan.mouseAction(mouseX, mouseY, false, state);

                        if(modPan.extended) {
                            for(AstolfoButton pan : modPan.astolfoButtons) {
                                pan.mouseAction(mouseX, mouseY, false, state);
                        }
                    }
                }
            }
        };
    }
}
