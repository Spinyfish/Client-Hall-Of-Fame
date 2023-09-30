package dev.africa.pandaware.impl.ui.clickgui.module;

import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.screen.GUIRenderer;
import dev.africa.pandaware.impl.font.Fonts;
import dev.africa.pandaware.impl.ui.clickgui.panel.Panel;
import dev.africa.pandaware.impl.ui.clickgui.setting.SettingPanel;
import dev.africa.pandaware.utils.client.MouseUtils;
import dev.africa.pandaware.utils.math.vector.Vec2i;
import dev.africa.pandaware.utils.render.RenderUtils;
import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

@Getter
public class ModuleElement implements GUIRenderer, MinecraftInstance {
    private final Panel parent;
    private final Module module;

    private Vec2i position;
    private final Vec2i size;

    private final SettingPanel settingPanel;

    public ModuleElement(Panel parent, Module module, Vec2i position, Vec2i size) {
        this.parent = parent;
        this.module = module;
        this.position = position;
        this.size = size;

        ScaledResolution scaledResolution = new ScaledResolution(mc);
        this.settingPanel = new SettingPanel(
                this, this.module,
                new Vec2i(
                        scaledResolution.getScaledWidth() / 2 - (270 / 2),
                        scaledResolution.getScaledHeight() / 2 - (350 / 2)
                ), new Vec2i(270, 350)
        );
    }

    public void update(Vec2i position) {
        this.position = position;
    }

    @Override
    public void handleRender(Vec2i mousePosition, float pTicks) {
        Fonts.getInstance().getComfortaMedium().drawString(
                (this.module.getData().isEnabled() ? "§l" : "") + this.module.getData().getName(),
                this.position.getX() + 13, this.position.getY() + 5,
                -1
        );

        if (!this.module.getSettings().isEmpty()) {
            Fonts.getInstance().getIconsBig().drawString(
                    "g", this.getPosition().getX() + this.getSize().getX() - 15,
                    this.getPosition().getY() + 6.5f, -1
            );
        }

        if (this.module.getData().isEnabled()) {
            RenderUtils.drawCircleOutline(
                    this.position.getX() + 5, this.position.getY() + 7.5f,
                    2, 2, 3, Color.WHITE
            );
        }
    }

    @Override
    public void handleKeyboard(char typedChar, int keyCode) {
    }

    @Override
    public void handleClick(Vec2i mousePosition, int button) {
        if (this.parent.getParent().getOpenSettingPanel() == null &&
                MouseUtils.isMouseInBounds(mousePosition, this.position, this.size)) {
            switch (button) {
                case 0:
                    this.module.toggle();
                    break;
                case 1:
                    if (!this.module.getSettings().isEmpty()) {
                        if (!this.parent.getParent().isSettingPanelFirstOpen()) {
                            ScaledResolution scaledResolution = new ScaledResolution(mc);
                            this.settingPanel.setPosition(new Vec2i(
                                    scaledResolution.getScaledWidth() / 2 - (250 / 2),
                                    scaledResolution.getScaledHeight() / 2 - (350 / 2)
                            ));

                            this.parent.getParent().setSettingPanelPosition(
                                    this.settingPanel.getPosition()
                            );
                        } else {
                            this.settingPanel.setPosition(this.parent.getParent().getSettingPanelPosition());
                        }

                        this.parent.getParent().setOpenSettingPanel(this.settingPanel);
                        this.settingPanel.open();
                    }
                    break;
            }
        }
    }
}
