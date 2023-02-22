package com.alan.clients.ui.click.standard.components;


import com.alan.clients.module.Module;
import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import com.alan.clients.ui.click.standard.components.value.impl.*;
import com.alan.clients.ui.click.standard.screen.impl.SearchScreen;
import com.alan.clients.util.animation.Animation;
import com.alan.clients.util.animation.Easing;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.localization.Localization;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.value.Value;
import com.alan.clients.value.impl.*;
import lombok.Getter;
import util.time.StopWatch;

import java.awt.*;
import java.util.ArrayList;

@Getter
public class ModuleComponent implements InstanceAccess {

    public Module module;
    public Vector2f scale = getStandardClickGUI().getModuleDefaultScale();
    public boolean expanded;
    public ArrayList<ValueComponent> valueList = new ArrayList<>();
    public Vector2d position;
    public double opacity;
    public StopWatch stopwatch = new StopWatch();
    public Animation hoverAnimation = new Animation(Easing.LINEAR, 50);
    public boolean mouseDown;

    public ModuleComponent(final Module module) {
        this.module = module;

        for (final Value<?> value : module.getAllValues()) {
            if (value instanceof ModeValue) {
                valueList.add(new ModeValueComponent(value));
            } else if (value instanceof BooleanValue) {
                valueList.add(new BooleanValueComponent(value));
            } else if (value instanceof StringValue) {
                valueList.add(new StringValueComponent(value));
            } else if (value instanceof NumberValue) {
                valueList.add(new NumberValueComponent(value));
            } else if (value instanceof BoundsNumberValue) {
                valueList.add(new BoundsNumberValueComponent(value));
            } else if (value instanceof DragValue) {
                valueList.add(new PositionValueComponent(value));
            } else if (value instanceof ListValue<?>) {
                valueList.add(new ListValueComponent(value));
            } else if (value instanceof ColorValue) {
                valueList.add(new ColorValueComponent(value));
            }
        }
    }

    public void draw(final Vector2d position, final int mouseX, final int mouseY, final float partialTicks) {
        this.position = position;

        if (position == null || position.y + scale.y < getStandardClickGUI().position.y || position.y > getStandardClickGUI().position.y + getStandardClickGUI().scale.y)
            return;


        final RiseClickGUI clickGUI = this.getStandardClickGUI();

//        RenderUtil.dropShadow(3, (float) position.x, (float) position.y, scale.x, scale.y, 42, 1);

        // Main module background
        RenderUtil.roundedRectangle(position.x, position.y, scale.x, scale.y, 6, ColorUtil.withAlpha(Color.BLACK, 50));
        final Color fontColor = new Color(clickGUI.fontColor.getRed(), clickGUI.fontColor.getGreen(), clickGUI.fontColor.getBlue(), module.isEnabled() ? 255 : 200);

        // Hover animation
        final boolean overModule = GUIUtil.mouseOver(position.x, position.y, scale.x, this.scale.y, mouseX, mouseY);

        hoverAnimation.run(overModule ? mouseDown ? 50 : 30 : 0);

        // Main module background HOVER OVERLAY
        RenderUtil.roundedRectangle(position.x, position.y, scale.x, scale.y, 6,
                new Color(0, 0, 0, (int) hoverAnimation.getValue()));

        // Draw the module's category if the user is searching
        if (clickGUI.getSelectedScreen() instanceof SearchScreen) {
            FontManager.getNunito(15).drawString("(" + Localization.get(module.getModuleInfo().category().getName()) + ")",
                    (float) (position.getX() + FontManager.getNunito(20).width(Localization.get(this.module.getModuleInfo().name())) + 10F),
                    (float) position.getY() + 10, ColorUtil.withAlpha(fontColor, 64).hashCode());
        }

        // Draw module name
        FontManager.getNunito(20).drawString(Localization.get(this.module.getModuleInfo().name()), (float) position.x + 6f, (float) position.y + 8,
                module.isEnabled() ? getTheme().getAccentColor(new Vector2d(0, position.y / 5)).getRGB() : fontColor.getRGB());

        // Draw module category
        FontManager.getNunito(15).drawString(Localization.get(module.getModuleInfo().description()), (float) position.x + 6f,
                (float) position.y + 25, ColorUtil.withAlpha(fontColor, 100).hashCode());

//        if (module.getKeyCode() != Keyboard.KEY_NONE) {
//            double keyBindScale = 10;
//            String key = Keyboard.getKeyName(module.getKeyCode());
//
//            RenderUtil.roundedRectangle(position.x + 222f, position.y + 10 - keyBindScale / 2, keyBindScale, keyBindScale, 1.5f,
//                    clickGUI.sidebarColor.darker());
//
//            FontRenderer nunitoExtraSmall = FontManager.getNunito(12);
//            FontManager.getNunito(12).drawString(key, position.x + 222f + keyBindScale / 2 - nunitoExtraSmall.width(key) / 2f,
//                    position.y + 10 + keyBindScale / 2 - nunitoExtraSmall.height() / 2f - 1, clickGUI.fontColor.hashCode());
//        }

        /* Allows for settings to be drawn */
        scale = new Vector2f(getStandardClickGUI().moduleDefaultScale.x, 38);
        if (this.isExpanded()) {
            for (final ValueComponent valueComponent : this.getValueList()) {
                if (valueComponent.getValue() != null && valueComponent.getValue().getHideIf() != null && valueComponent.getValue().getHideIf().getAsBoolean()) {
                    continue;
                }

                valueComponent.draw(new Vector2d(position.x + 6f +
                        (valueComponent.getValue().getHideIf() == null ? 0 : 10),
                        (float) (position.y + scale.y + 1)), mouseX, mouseY, partialTicks);

                scale.y = (float) (scale.y + valueComponent.getHeight());
            }

            // This makes the expanded category look better
            scale.y = scale.y - 2;
        }

//        double range = 14;
//        double padding = 4;
//        String name = Keyboard.getKeyName(module.getKeyCode());
//        Font font;
//
//        if (name.length() == 1) {
//            RenderUtil.roundedRectangle(position.x + scale.x - range - padding, position.y + padding, range, range, 4, clickGUI.sidebarColor.darker().darker());
//
//            font = FontManager.getProductSansLight(18);
//            font.drawCenteredString(name, position.x - range / 2 - padding + scale.x - 0.5, position.y + padding + range / 2 - 2.5, ColorUtil.withAlpha(clickGUI.fontColor, 100).getRGB());
//        }

        stopwatch.reset();
    }

    public void key(final char typedChar, final int keyCode) {
        if (position == null || position.y + scale.y < getStandardClickGUI().position.y || position.y > getStandardClickGUI().position.y + getStandardClickGUI().scale.y)
            return;

        if (this.isExpanded()) {
            for (final ValueComponent valueComponent : this.getValueList()) {
                valueComponent.key(typedChar, keyCode);
            }
        }

//        final boolean overModule = GUIUtil.mouseOver(position.x, position.y, scale.x, getStandardClickGUI().moduleDefaultScale.getY() - 3, Mouse.getMouse().x, Mouse.getMouse().y);
//        if (overModule) {
//            module.setKeyCode(keyCode);
//        }
    }

    public void click(final int mouseX, final int mouseY, final int mouseButton) {
        /* Allows the module to be toggled */
        if (position == null || position.y + scale.y < getStandardClickGUI().position.y || position.y > getStandardClickGUI().position.y + getStandardClickGUI().scale.y)
            return;

        final Vector2f clickGUIPosition = getStandardClickGUI().position;
        final Vector2f clickGUIScale = getStandardClickGUI().scale;

        final boolean left = mouseButton == 0;
        final boolean right = mouseButton == 1;
        final boolean overClickGUI = GUIUtil.mouseOver(clickGUIPosition.x, clickGUIPosition.y, clickGUIScale.x, clickGUIScale.y, mouseX, mouseY);
        final boolean overModule = GUIUtil.mouseOver(position.x, position.y, scale.x, getStandardClickGUI().moduleDefaultScale.getY() - 3, mouseX, mouseY);

        if (overModule && getStandardClickGUI().overlayPresent == null) {
            mouseDown = true;

            double valueSize = 0;
            for (ValueComponent valueComponent : valueList) valueSize += valueComponent.getHeight();

            if (left) {
                if (overClickGUI) {
                    this.module.toggle();
                }
            } else if (right && this.module.getValues().size() != 0 && valueSize != 0) {
                this.expanded = !this.expanded;

                for (final ValueComponent valueComponent : this.getValueList()) {
                    if (valueComponent instanceof BoundsNumberValueComponent) {
                        final BoundsNumberValueComponent boundsNumberValueComponent = ((BoundsNumberValueComponent) valueComponent);
                        boundsNumberValueComponent.grabbed1 = boundsNumberValueComponent.grabbed2 = false;
                    } else if (valueComponent instanceof NumberValueComponent) {
                        ((NumberValueComponent) valueComponent).grabbed = false;
                    }
                }
            }
        }

        if (this.isExpanded()) {
            for (final ValueComponent valueComponent : this.getValueList()) {
                valueComponent.click(mouseX, mouseY, mouseButton);
            }
        }
    }

    public void bloom() {
        if (position == null) {
            return;
        }

        if (position.y + scale.y < getStandardClickGUI().position.y || position.y > getStandardClickGUI().position.y + getStandardClickGUI().scale.y)
            return;

        if (this.isExpanded()) {
            for (final ValueComponent valueComponent : this.getValueList()) {
                if (valueComponent.getValue() != null && valueComponent.getValue().getHideIf() != null && valueComponent.getValue().getHideIf().getAsBoolean()) {
                    continue;
                }

                valueComponent.bloom();
            }
        }
    }

    public void released() {
        mouseDown = false;

        if (this.isExpanded()) {
            for (final ValueComponent valueComponent : this.getValueList()) {
                valueComponent.released();
            }
        }
    }
}