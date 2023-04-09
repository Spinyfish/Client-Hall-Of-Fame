package tech.dort.dortware.impl.gui.vaziakclick.element.impl;

import net.minecraft.client.gui.Gui;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.font.CustomFontRenderer;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.impl.gui.vaziakclick.GuiUtils;
import tech.dort.dortware.impl.gui.vaziakclick.element.Element;
import tech.dort.dortware.impl.gui.vaziakclick.element.impl.setting.SettingPane;
import tech.dort.dortware.impl.modules.render.ClickGUI;
import tech.dort.dortware.impl.utils.player.ChatUtil;

import java.awt.*;

public class ModulePane extends Element {

    private final Module module;
    public static final int modButtonHeight = 18;
    private boolean expanded;
    private final boolean canExpand;
    private boolean bindingMode;

    public SettingPane getSettingPane() {
        return settingPane;
    }

    private final SettingPane settingPane;

    //no way to grab atm with normal mc font renderer
    public ModulePane(Module module, int width) {
        this.module = module;
        this.width = width;
        this.height = modButtonHeight;
        this.canExpand = valueManager.getValuesFromOwner(module).size() > 0;
        settingPane = new SettingPane(module, this.width);
    }

    @Override
    public void onDraw(int mouseX, int mouseY, float partialTicks) {
        //BUTTON
        final CustomFontRenderer font = Client.INSTANCE.getFontManager().getFont("Large").getRenderer();
        GuiUtils.glResets();
        if (GuiUtils.isHovering(mouseX, mouseY, this.posX, this.posY, this.width, modButtonHeight)) {
            Gui.drawRect(posX, posY, posX + width, posY + modButtonHeight, new Color(33, 66, 255).getRGB());
        }
        GuiUtils.glStartFontRenderer();
        ClickGUI clickGUI = Client.INSTANCE.getModuleManager().get(ClickGUI.class);
        int color = new Color(0, 67, 55).getRGB();
        String modName = module.getModuleData().getName();
        float centerAlignment = ((modButtonHeight - font.getHeight(modName)) / 2);
        font.drawString(bindingMode ? "Press any key..." : modName, posX + PADDING, posY + centerAlignment, module.isToggled() ? color : -1);

        //render expansion icon
        if (canExpand) {
            int iconWidth = 8;
            int iconHeight = 5;
            int testPadding = ((modButtonHeight - (iconHeight)) / 2);
            GuiUtils.drawImage(expanded ? expandIcon : collapseIcon, posX + width + 65, posY + testPadding, iconWidth, iconHeight, module.isToggled() ? color : -1);
        }
        if (expanded) {
            //contrast effect
            //Gui.drawRect(posX, posY, posX + width, posY + modButtonHeight + settingPane.calculateHeight(), new Color(0, 0, 0, 50).getRGB());
            settingPane.setPos(this.posX, this.posY + modButtonHeight);
            settingPane.onDraw(mouseX, mouseY, partialTicks);
        }
        int size = settingPane.calculateHeight();
        height = modButtonHeight + (expanded ? size : 0);

    }

    public void keyTyped(char typedChar, int keyCode) {
        if (bindingMode) {
            if (keyCode == 1) {
                bindingMode = false;
                return;
            }
            module.setKeyBind(keyCode);
            ChatUtil.displayChatMessage(String.format("%s was successfully bound to %s", module.getModuleData().getName(), String.valueOf(typedChar).toUpperCase()));
            bindingMode = false;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (expanded) {
            settingPane.mouseClicked(mouseX, mouseY, mouseButton);
        }
        if (GuiUtils.isHovering(mouseX, mouseY, this.posX, this.posY, this.width, modButtonHeight)) {
            switch (mouseButton) {
                case 0:
                    module.toggle();
                    break;
                case 1:
                    if (canExpand) {
                        expanded = !expanded;
                        int size = settingPane.calculateHeight();
                        height = modButtonHeight + (expanded ? size : 0);
                    }
                    break;
                case 2:
                    bindingMode = true;
                    break;
            }
        }

    }
}
