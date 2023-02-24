package me.kansio.client.gui.clickgui.frame.components.configs;


import me.kansio.client.Client;
import me.kansio.client.config.Config;
import me.kansio.client.gui.clickgui.frame.components.configs.impl.CreateButton;
import me.kansio.client.gui.clickgui.frame.components.configs.impl.DeleteButton;
import me.kansio.client.gui.clickgui.frame.components.configs.impl.RenameConfig;
import me.kansio.client.gui.clickgui.frame.Values;
import me.kansio.client.gui.clickgui.utils.render.animation.easings.Animate;
import me.kansio.client.gui.clickgui.utils.render.animation.easings.Easing;
import me.kansio.client.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;

public class FrameConfig implements Values {
    public final Config config;
    private final ArrayList<ConfigComponent> components;

    private final ConfigCategory owner;

    private final Animate moduleAnimation;

    private int x, y;
    private int offset;

    private boolean opened;
    private boolean listening;

    public FrameConfig(Config module, ConfigCategory owner, int x, int y) {
        this.config = module;
        this.components = new ArrayList<>();
        this.owner = owner;
        this.moduleAnimation = new Animate();
        moduleAnimation.setMin(0).setMax(255).setReversed(false).setEase(Easing.LINEAR);
        this.opened = config == null;
        this.listening = false;
        this.x = x;
        this.y = y;

        if (config != null) {
            components.add(new DeleteButton(0, 0, this));
            components.add(new RenameConfig(0, 0, this));
        } else {
            components.add(new CreateButton(0, 0, this));
        }
    }

    public void drawScreen(int mouseX, int mouseY) {
        if (RenderUtils.hover(x, y, mouseX, mouseY, defaultWidth, moduleHeight) && hoveredColor) {
            GuiScreen.drawRect(x, y, x + defaultWidth, y + moduleHeight, darkerMainColor);
        }

        Minecraft.getMinecraft().fontRendererObj.drawString(config != null ? config.getName() : "Options:", x + 3, y + (moduleHeight / 2F - (Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT / 2F)), stringColor, true);

        int offset = 0;

        if (opened) {
            for (ConfigComponent component : this.components) { // using for loop because continue isn't supported on foreach

                component.setHidden(false);

                if (component.isHidden()) continue;
                component.setX(x);
                component.setY(y + moduleHeight + offset);

                component.drawScreen(mouseX, mouseY);

                offset += component.getOffset();
            }
        }

        this.setOffset(moduleHeight + offset);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtils.hover(x, y, mouseX, mouseY, defaultWidth, moduleHeight) && RenderUtils.hover(owner.getX(), owner.getY(), mouseX, mouseY, defaultWidth, owner.getHeight())) {
            if (mouseButton == 0) {
                if (config != null) {

                    if (config.getName().equalsIgnoreCase("")) return true;

                    Client.getInstance().getConfigManager().loadConfig(config.getName(), false);
                }
            }

            if (config != null) {

                if (config.getName().equalsIgnoreCase(""))
                    return true;


                if (mouseButton == 1)
                    opened = !opened;
            }


            return true;
        }

        if (RenderUtils.hover(owner.getX(), owner.getY(), mouseX, mouseY, defaultWidth, owner.getHeight()) && opened) {
            for (ConfigComponent component : this.components) {
                if (component.isHidden()) continue;

                if (!opened) continue;

                if (component.mouseClicked(mouseX, mouseY, mouseButton))
                    return true;
            }
        }

        return false;
    }

    public int getOffset() {
        offset = 0;
        if (opened) {
            for (ConfigComponent component : this.components) { // using for loop because continue isn't supported on foreach
                //component.getSetting().constantCheck();
                //if(component.getSetting().isHide()) continue;
                if (component.isHidden()) continue;

                offset += component.getOffset();
            }
        }

        this.setOffset(moduleHeight + offset);
        return offset;
    }


    public void keyTyped(char key, int keycode) {
        for (ConfigComponent component : this.components) {
            if (component.isHidden()) continue;

            if (config == null) {
                component.keyTyped(key, keycode);
                return;
            }

            if (opened) component.keyTyped(key, keycode);

        }
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
