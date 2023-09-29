package de.verschwiegener.atero.ui.clickgui.component;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.settings.SettingsItem;
import de.verschwiegener.atero.ui.clickgui.ClickGUIPanel;
import de.verschwiegener.atero.ui.clickgui.component.components.*;
import de.verschwiegener.atero.util.render.RenderUtil;

import java.awt.*;
import java.util.ArrayList;

public class PanelExtendet {

    private final String name;
    private final ClickGUIPanel panel;
    private final int width;
    private final int y;
    ArrayList<Component> components = new ArrayList<>();
    private boolean animate, isEmpty;
    private int state, animationX, height;

    public PanelExtendet(final String ModuleName, final int y, final ClickGUIPanel p) {
        this.y = y;
        panel = p;
        name = ModuleName;
        state = 1;
        animate = true;
        width = 100;
        int yoffset = 8;
        if (Management.instance.settingsmgr.getSettingByName(ModuleName) != null
                && !Management.instance.settingsmgr.getSettingByName(ModuleName).getItems().isEmpty()) {
            final int size = Management.instance.settingsmgr.getSettingByName(ModuleName).getItems().size();
            for (int i = 0; i < size; i++) {
                final SettingsItem si = Management.instance.settingsmgr.getSettingByName(ModuleName).getItems().get(i);
                switch (si.getCategory()) {
                    case CHECKBOX:
                        components.add(new ComponentCheckBox(si.getName(), yoffset, this));
                        yoffset += 13;
                        break;

                    case COMBO_BOX:
                        components.add(new ComponentCombobox(si.getName(), yoffset, this));
                        yoffset += 13;
                        break;

                    case SLIDER:
                        components.add(new ComponentSlider(si.getName(), yoffset, this));
                        yoffset += 15;
                        break;
                    case TEXT_FIELD:
                        components.add(new ComponentTextField(si.getName(), yoffset, this));
                        yoffset += 13;
                        break;
                    case COLOR_PICKER:
                        components.add(new ComponentColorPicker(si.getName(), yoffset, this));
                        yoffset += 85;
                        break;
                }
            }
            height = yoffset - 4;
        } else {
            isEmpty = true;
        }
    }

    public void collapsePanelByItemName(final String name) {
        final Component component = getComponentByName(name);
        final int count = components.indexOf(component);
        int offset = 0;
        switch (component.getItem().getCategory()) {
            case CHECKBOX:
                offset = 13;
                break;
            case COMBO_BOX:
                offset = 13;
                break;
            case SLIDER:
                offset = 15;
                break;
            case TEXT_FIELD:
                offset = 13;
                break;
            case COLOR_PICKER:
        	offset = 85;
        	break;

        }
        for (int i = count; i < components.size(); i++) {
            try {
                final Component c = components.get(i);
                c.y -= offset;
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        height -= offset;
    }

    public void collapsePanelByYOffset(final int yoffset, final String name) {
        final int count = components.indexOf(getComponentByName(name));
        for (int i = count + 1; i < components.size(); i++) {
            try {
                final Component c = components.get(i);
                c.y -= yoffset;
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        height -= yoffset;
    }

    public void drawScreen(final int mouseX, final int mouseY) {
        if (!isEmpty) {
            if ((animate || state != 1) && panel.getState() == 1) {
                if(Management.instance.modulemgr.getModuleByName("Design").isEnabled()) {
                    RenderUtil.fillRect(panel.getX() + panel.getWidth() + 1, panel.getY() + y, animationX, height,
                            new Color(0, 0, 0, 120));
                }else{
                    RenderUtil.fillRect(panel.getX() + panel.getWidth() + 1, panel.getY() + y, animationX, height,
                           Management.instance.colorBlack);
                }
            }

            if (getState() == 2 && !isAnimate() && getPanel().getState() == 1) {
                components.forEach(component -> component.drawComponent(mouseX, mouseY));
            }
        }
    }

    public void extendPanelByItemName(final String name) {
        final Component component = getComponentByName(name);
        final int count = components.indexOf(component);
        int offset = 0;
        switch (component.getItem().getCategory()) {
            case CHECKBOX:
                offset = 13;
                break;
            case COMBO_BOX:
                offset = 13;
                break;
            case SLIDER:
                offset = 15;
                break;
            case TEXT_FIELD:
                offset = 13;
                break;
            case COLOR_PICKER:
        	offset = 85;
        	break;
        }
        for (int i = count; i < components.size(); i++) {
            try {
                final Component c = components.get(i);
                c.y += offset;
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        height += offset;

    }

    public void extendPanelByYOffset(final int yoffset, final String name) {
        final int count = components.indexOf(getComponentByName(name));
        for (int i = count + 1; i < components.size(); i++) {
            try {
                final Component c = components.get(i);
                c.y += yoffset;
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        height += yoffset;
    }

    public int getAnimationX() {
        return animationX;
    }

    public void setAnimationX(final int animationX) {
        this.animationX = animationX;
    }

    public Component getComponentByName(final String name) {
        return components.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public int getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public ClickGUIPanel getPanel() {
        return panel;
    }

    public int getState() {
        return state;
    }

    public void setState(final int state) {
        this.state = state;
    }

    public int getWidth() {
        return width;
    }

    public int getY() {
        return y;
    }

    public boolean isAnimate() {
        return animate;
    }

    public void setAnimate(final boolean animate) {
        this.animate = animate;
    }

    public void onKeyTyped(final char typedChar, final int keyCode) {
        components.forEach(component -> component.onKeyTyped(typedChar, keyCode));
    }

    public void onMouseClicked(final int x, final int y, final int mousebutton) {
        if (!isEmpty && getState() == 2 && !isAnimate() && getPanel().getState() == 1) {
            for (final Component c : components) {
                c.onMouseClicked(x, y, mousebutton);
            }
        }
    }

    public void onMouseReleased(final int mouseX, final int mouseY, final int state) {
        for (final Component c : components) {
            c.onMouseReleased(mouseX, mouseY, state);
        }
    }

    public void switchState() {
        animate = true;
    }

}
