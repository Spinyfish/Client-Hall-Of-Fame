package com.alan.clients.ui.click.standard.components.value.impl;

import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.value.Value;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.render.RenderUtil;
import util.time.StopWatch;

public class BooleanValueComponent extends ValueComponent {

    private final StopWatch stopwatch = new StopWatch();
    private double scale;

    public BooleanValueComponent(final Value<?> value) {
        super(value);
    }

    @Override
    public void draw(final Vector2d position, final int mouseX, final int mouseY, final float partialTicks) {
        this.position = position;
        final BooleanValue booleanValue = (BooleanValue) value;

        // Draws name
        this.nunitoSmall.drawString(this.value.getName(), this.position.x, this.position.y, this.getStandardClickGUI().fontDarkColor.hashCode());
        final double positionX = this.position.x + this.nunitoSmall.width(this.value.getName()) + 3;

        if (booleanValue.getValue()) {
            scale = Math.min(5, scale + stopwatch.getElapsedTime() / 20f);
        } else {
            scale = Math.max(0, scale - stopwatch.getElapsedTime() / 20f);
        }

        RenderUtil.roundedRectangle(positionX - 5f / 2f + 5, this.position.y - 5f / 2f + 2.5, 5, 5, 2.5F, getStandardClickGUI().backgroundColor);

        if (scale != 0) {
            RenderUtil.roundedRectangle(positionX - scale / 2 + 4, this.position.y - scale / 2 + 2.5, scale, scale, scale / 2.0F, this.getTheme().getFirstColor());
        }

        stopwatch.reset();
    }

    @Override
    public void click(final int mouseX, final int mouseY, final int mouseButton) {
        if (this.position == null) {
            return;
        }

        final BooleanValue booleanValue = (BooleanValue) value;

        if (GUIUtil.mouseOver(position.x, this.position.y - 3.5f, getStandardClickGUI().width - 70, this.height, mouseX, mouseY)) {
            booleanValue.setValue(!booleanValue.getValue());
        }
    }

    @Override
    public void released() {

    }

    @Override
    public void bloom() {
        if (this.position == null) {
            return;
        }

        final double positionX = this.position.x + this.nunitoSmall.width(this.value.getName()) + 2;
        RenderUtil.circle(positionX - scale / 2 + 5, this.position.y - scale / 2 + 2.5, scale, getTheme().getFirstColor());
    }

    @Override
    public void key(final char typedChar, final int keyCode) {

    }
}
