/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 */
package wtf.monsoon.impl.ui.panel.elements.config;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.panel.elements.config.ConfigPanel;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;

public class ConfigPane
extends Drawable {
    private final File config;
    private boolean deleted = false;
    private final ConfigPanel parent;
    private final ArrayList<ConfigPanel.Button> buttons = new ArrayList();
    private final Animation hover = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.LINEAR);
    private final Animation deleteScaleDown = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.CUBIC_IN_OUT);

    public ConfigPane(ConfigPanel parent, File config, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.parent = parent;
        this.config = config;
        this.buttons.add(new ConfigPanel.Button("Load", () -> {
            String configName = config.getName().split("\\.")[0];
            if (Wrapper.getMonsoon().getConfigSystem().configExists(configName)) {
                Wrapper.getMonsoon().getConfigSystem().load(configName, true);
            }
        }, this.getX(), this.getY(), this.getWidth() / 2.0f - 5.0f, 8.0f, false));
        this.buttons.add(new ConfigPanel.Button("Delete", () -> {
            String configName = config.getName().split("\\.")[0];
            if (Wrapper.getMonsoon().getConfigSystem().configExists(configName) && !configName.equals("current")) {
                this.deleted = new File("monsoon" + File.separator + "configs" + File.separator + configName + ".json").delete();
            }
        }, this.getX(), this.getY(), this.getWidth() / 2.0f - 5.0f, 8.0f, config.getName().split("\\.")[0].equals("current")));
        this.deleteScaleDown.setState(true);
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        Color cc4;
        Color cc3;
        Color cc2;
        Color cc1;
        Color c4;
        if (this.deleted && !this.deleteScaleDown.getState() && this.deleteScaleDown.getAnimationFactor() == 0.0) {
            this.parent.getPanes().remove(this);
        }
        this.hover.setState(this.hovered(mouseX, mouseY));
        this.deleteScaleDown.setState(!this.deleted);
        String[] split = this.config.getName().split("\\.");
        String configName = split[0];
        Color c1 = ColorUtil.getClientAccentTheme()[0];
        Color c2 = ColorUtil.getClientAccentTheme()[1];
        Color c3 = ColorUtil.getClientAccentTheme().length > 2 ? ColorUtil.getClientAccentTheme()[2] : ColorUtil.getClientAccentTheme()[0];
        Color color = c4 = ColorUtil.getClientAccentTheme().length > 3 ? ColorUtil.getClientAccentTheme()[3] : ColorUtil.getClientAccentTheme()[1];
        if (ColorUtil.getClientAccentTheme().length > 3) {
            cc1 = c1;
            cc2 = c2;
            cc3 = c3;
            cc4 = c4;
        } else {
            cc1 = ColorUtil.fadeBetween(10, 270, c1, c2);
            cc2 = ColorUtil.fadeBetween(10, 0, c1, c2);
            cc3 = ColorUtil.fadeBetween(10, 180, c1, c2);
            cc4 = ColorUtil.fadeBetween(10, 90, c1, c2);
        }
        RenderUtil.scaleXY(this.getX() + this.getWidth() / 2.0f, this.getY() + this.getHeight() / 2.0f, this.deleteScaleDown, () -> {
            RoundedUtils.round(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 4.0f, new Color(0x191919));
            RoundedUtils.outline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 4.0f, 0.5f, 1.0f, cc1, cc2, cc3, cc4);
            Wrapper.getFontUtil().productSansBold.drawCenteredString(configName, this.getX() + this.getWidth() / 2.0f, this.getY() + 2.0f, new Color(-7368817), false);
            float x = this.getX() + (this.getWidth() / 2.0f - (float)this.buttons.size() / 2.0f * this.buttons.get(0).getWidth());
            for (ConfigPanel.Button button : this.buttons) {
                button.setX(x);
                button.setY(this.getY() + this.getHeight() - 12.0f);
                button.draw(mouseX, mouseY, mouseDelta);
                x += button.getWidth();
            }
        });
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (!this.deleted) {
            for (ConfigPanel.Button button : this.buttons) {
                button.mouseClicked(mouseX, mouseY, click);
            }
        }
        return false;
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, Click click) {
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    public File getConfig() {
        return this.config;
    }

    public Animation getDeleteScaleDown() {
        return this.deleteScaleDown;
    }
}

