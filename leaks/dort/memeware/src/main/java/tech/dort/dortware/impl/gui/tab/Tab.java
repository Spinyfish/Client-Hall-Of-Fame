package tech.dort.dortware.impl.gui.tab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.font.CustomFontRenderer;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.enums.ModuleCategory;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.impl.events.KeyboardEvent;
import tech.dort.dortware.impl.modules.render.Hud;
import tech.dort.dortware.impl.utils.render.ColorUtil;

import java.awt.*;
import java.util.List;

/**
 * @author Auth
 * @author Intent (friend helped me and made me fucking use intent without me knowing)
 */

public class Tab {
    final CustomFontRenderer font1 = Client.INSTANCE.getFontManager().getFont("Chat").getRenderer();
    final Minecraft mc = Minecraft.getMinecraft();

    private int tab, index;
    private boolean expanded;

    public void render() {
        final List<Module> modules = Client.INSTANCE.getModuleManager().getAllInCategory(ModuleCategory.values()[tab]);
        final Hud hud = Client.INSTANCE.getModuleManager().get(Hud.class);
        int offset = hud.mpStatus.getValue() ? 14 : 0;
        final BooleanValue tabGui = hud.tabGui;
        Gui.drawRect(0, offset + 23, 70, offset + 6 + ModuleCategory.values().length * 16, new Color(0, 0, 0, 100).getRGB());
        Gui.drawRect(0, offset + 23 + tab * 16, 70, offset + 28 + tab * 16 + 10, ColorUtil.getModeColor());

        int y = 0;
        for (ModuleCategory category : ModuleCategory.values()) {
            if (category.equals(ModuleCategory.HIDDEN))
                continue;

            font1.drawStringWithShadow(category.getName(), category == ModuleCategory.values()[tab] ? 16 : 6, offset + 26 + y * 16, -1);

            y++;
        }

        // BELOW IS MODULES

        if (expanded) {
            Gui.drawRect(72, offset + 23, 162, offset + 6 + (modules.size() + 1) * 16, new Color(0, 0, 0, 100).getRGB());
            Gui.drawRect(72, offset + 23 + index * 16, 162, offset + 28 + index * 16 + 10, ColorUtil.getModeColor());

            y = 0;
            for (Module module : modules) {
                font1.drawStringWithShadow(module.getModuleData().getName(), module == modules.get(index) ? 85 : 75, 26 + y * 16, module.isToggled() ? -1 : Color.LIGHT_GRAY.getRGB());

                y++;
            }
        }
    }

    public void updateKeys(KeyboardEvent event) {
        final int keyCode = event.getKey();

        final Hud hud = Client.INSTANCE.getModuleManager().get(Hud.class);
        final BooleanValue tabGui = hud.tabGui;

        final List<Module> modules = Client.INSTANCE.getModuleManager().getAllInCategory(ModuleCategory.values()[tab]);

        if (!tabGui.getValue())
            return;

        switch (keyCode) {
            case Keyboard.KEY_UP:
                if (expanded) {
                    if (index <= 0) {
                        index = modules.size() - 1;
                    } else {
                        index--;
                    }
                } else {
                    if (tab <= 0) {
                        tab = ModuleCategory.values().length - 2;
                    } else {
                        tab--;
                    }
                }
                break;

            case Keyboard.KEY_DOWN:
                if (expanded) {
                    if (index >= modules.size() - 1) {
                        index = 0;
                    } else {
                        index++;
                    }
                } else {
                    if (tab >= ModuleCategory.values().length - 2) {
                        tab = 0;
                    } else {
                        tab++;
                    }
                }
                break;

            case Keyboard.KEY_RETURN:
            case Keyboard.KEY_RIGHT:
                if (expanded) {
                    modules.get(index).toggle();
                } else {
                    index = 0;
                    expanded = true;
                }
                break;


            case Keyboard.KEY_LEFT:
                expanded = false;
                break;
        }
    }
}
