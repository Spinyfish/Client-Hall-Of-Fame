package dev.africa.pandaware.impl.ui.clickgui.setting.impl;

import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.font.Fonts;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.ui.clickgui.setting.api.Element;
import dev.africa.pandaware.utils.client.MouseUtils;
import dev.africa.pandaware.utils.math.vector.Vec2i;
import dev.africa.pandaware.utils.render.RenderUtils;
import dev.africa.pandaware.utils.render.animator.Animator;
import dev.africa.pandaware.utils.render.animator.Easing;

import java.awt.*;

public class BooleanElement extends Element<BooleanSetting> {
    public BooleanElement(Module module, ModuleMode<?> moduleMode, BooleanSetting setting) {
        super(module, moduleMode, setting);
    }

    private final Animator animator = new Animator();
    private final Animator clickAnimator = new Animator();

    @Override
    public void handleRender(Vec2i mousePosition, float pTicks) {
        Fonts.getInstance().getComfortaMedium().drawString(
                this.getSetting().getName(),
                this.getPosition().getX(),
                this.getPosition().getY(),
                -1
        );

        boolean hovered = MouseUtils.isMouseInBounds(mousePosition, this.getPosition(), this.getSize());

        this.animator.setEase(Easing.LINEAR).setMin(0).setMax(1).setSpeed(6).setReversed(!hovered).update();

        RenderUtils.drawRoundedRect(
                this.getPosition().getX() + this.getSize().getX() - 20,
                this.getPosition().getY() - 2,
                12, 12, 3,
                new Color(120, 120, 120, (int) (255 * this.animator.getValue()))
        );

        RenderUtils.drawRoundedRectOutline(
                this.getPosition().getX() + this.getSize().getX() - 20,
                this.getPosition().getY() - 2,
                12, 12, 3,
                new Color(255, 255, 255, 255)
        );

        this.clickAnimator.setEase(Easing.LINEAR).setMin(0).setMax(1)
                .setSpeed(8).setReversed(!this.getSetting().getValue()).update();

        RenderUtils.drawCheck(
                this.getPosition().getX() + this.getSize().getX() - 18,
                this.getPosition().getY() + 3.5, new Color(128, 255, 120,
                        (int) (255 * this.clickAnimator.getValue()))
        );
    }

    @Override
    public void handleClick(Vec2i mousePosition, int button) {
        if (this.getPosition() != null && this.getSize() != null) {
            if (MouseUtils.isMouseInBounds(mousePosition,
                    new Vec2i(this.getPosition().getX() + this.getSize().getX() - 25,
                            this.getPosition().getY() - 2 - 5),
                    new Vec2i(12 + 9, 12 + 9))) {
                this.getSetting().setValue(!this.getSetting().getValue());
            }
        }
    }
}
