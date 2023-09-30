package dev.africa.pandaware.impl.ui.clickgui.setting.impl;

import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.font.Fonts;
import dev.africa.pandaware.impl.setting.EnumSetting;
import dev.africa.pandaware.impl.ui.clickgui.setting.api.Element;
import dev.africa.pandaware.utils.client.MouseUtils;
import dev.africa.pandaware.utils.math.vector.Vec2i;
import dev.africa.pandaware.utils.render.RenderUtils;
import dev.africa.pandaware.utils.render.animator.Animator;
import dev.africa.pandaware.utils.render.animator.Easing;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EnumElement extends Element<EnumSetting> {
    public EnumElement(Module module, ModuleMode<?> moduleMode, EnumSetting setting) {
        super(module, moduleMode, setting);
    }

    private Vec2i clone = new Vec2i();
    private boolean extended;

    private double height;
    private int width;

    private final Animator animator = new Animator();

    @Override
    public void handleRender(Vec2i mousePosition, float pTicks) {
        Fonts.getInstance().getComfortaMedium().drawString(
                this.getSetting().getName(),
                this.getPosition().getX(),
                this.getPosition().getY(),
                -1
        );

        this.animator.setMin(0).setMax(1).setSpeed(7).setEase(this.extended ? Easing.QUINTIC_OUT : Easing.QUINTIC_IN)
                .setReversed(!this.extended).update();

        Enum max = this.getLongest(this.getSetting());

        if (max == null) {
            return;
        }

        this.width = (int) Fonts.getInstance().getProductSansMedium().getStringWidth(
                this.getSetting().getLabel(max)) + 5;
        this.width = Math.max(95, width);

        this.clone = this.getPosition().copy();
        this.clone.setY(this.clone.getY() - 4);
        this.clone.setX(this.clone.getX() + this.getSize().getX() - (this.width + 8));

        this.height = this.animator.getValue() > 0 ? (int)
                (((this.getSetting().getValues().length * 15) * this.animator.getValue()) + 15) : 15;

        RenderUtils.drawRoundedRect(this.clone.getX(), this.clone.getY(), this.width, this.height, 3,
                new Color(42, 42, 42, 255));
        RenderUtils.drawRoundedRectOutline(this.clone.getX(), this.clone.getY(), this.width, this.height, 3,
                new Color(255, 255, 255, 255));

        Fonts.getInstance().getProductSansMedium().drawCenteredStringWithShadow(this.getSetting().getLabel(this.getSetting().getValue()),
                this.clone.getX() + (this.width / 2), this.clone.getY() + 3, -1);

        this.getSize().setY((int) (this.getSize().getY() + (this.height - 15)));

        if (this.animator.getValue() > 0) {
            int add = 0;
            for (Enum value : this.getSetting().getValues()) {
                String label = this.getSetting().getLabel(value);

                if ((this.height + this.getPosition().getY() - 15) >= (this.clone.getY() + 3 + 15 + add)) {
                    Fonts.getInstance().getProductSansMedium().drawCenteredStringWithShadow(
                            (value.equals(this.getSetting().getValue()) ? "§l" : "") + label,
                            this.clone.getX() + (this.width / 2), this.clone.getY() + 3 + 15 + add, -1);
                }

                add += 15;
            }
        }
    }

    @Override
    public void handleClick(Vec2i mousePosition, int button) {
        if (button == 0) {
            if (this.hovered(mousePosition, this.clone)) {
                this.extended = !this.extended;
            }

            if (this.extended) {
                int add = 0;
                for (Enum value : this.getSetting().getValues()) {
                    boolean hover = MouseUtils.isMouseInBounds(mousePosition,
                            new Vec2i(this.clone.getX(), this.clone.getY() + 3 + 15 + add),
                            new Vec2i(this.width, 15));

                    if (hover) {
                        this.getSetting().setValue(value);
                    }

                    add += 15;
                }
            }
        }
    }

    boolean hovered(Vec2i mousePosition, Vec2i position) {
        return MouseUtils.isMouseInBounds(mousePosition, position, new Vec2i(this.width, 15));
    }

    Enum getLongest(EnumSetting setting) {
        List<Enum> enumList = Arrays.stream(setting.getValues()).collect(Collectors.toList());
        Enum maxMode = enumList.stream().max(Comparator.comparingDouble(mode ->
                        Fonts.getInstance().getProductSansMedium().getStringWidth(setting.getLabel(mode))))
                .orElse(null);

        return maxMode;
    }
}
