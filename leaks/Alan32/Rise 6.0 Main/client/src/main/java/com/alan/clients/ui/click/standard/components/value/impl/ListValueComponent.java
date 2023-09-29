package com.alan.clients.ui.click.standard.components.value.impl;

import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.value.Value;
import com.alan.clients.value.impl.ListValue;
import lombok.Getter;

/**
 * @author Strikeless
 * @since 02.07.2022
 */
@Getter
public class ListValueComponent extends ValueComponent {

    public ListValueComponent(final Value<?> value) {
        super(value);
    }

    @Override
    public void draw(final Vector2d position, final int mouseX, final int mouseY, final float partialTicks) {
        final ListValue<?> listValue = (ListValue<?>) value;
        this.position = position;

        final String prefix = this.value.getName() + ":";

        this.nunitoSmall.drawString(prefix, this.position.x, this.position.y, this.getStandardClickGUI().fontDarkColor.hashCode());
        this.nunitoSmall.drawString(listValue.getValue().toString(), this.position.x + this.nunitoSmall.width(prefix) + 2, this.position.y, this.getStandardClickGUI().fontDarkColor.hashCode());
    }

    @Override
    public void click(final int mouseX, final int mouseY, final int mouseButton) {
        if (this.position == null) {
            return;
        }

        final ListValue<?> listValue = (ListValue<?>) value;

        final boolean left = mouseButton == 0;
        final boolean right = mouseButton == 1;

        if (GUIUtil.mouseOver(this.position.x, this.position.y - 3.5f, getStandardClickGUI().width - 70, this.height, mouseX, mouseY)) {
            final int currentIndex = listValue.getModes().indexOf(listValue.getValue());

            Object value = null;
            if (left) {
                if (listValue.getModes().size() <= currentIndex + 1) {
                    value = listValue.getModes().get(0);
                } else {
                    value = listValue.getModes().get(currentIndex + 1);
                }
            } else if (right) {
                if (0 > currentIndex - 1) {
                    value = listValue.getModes().get(listValue.getModes().size() - 1);
                } else {
                    value = listValue.getModes().get(currentIndex - 1);
                }
            }

            if (value != null) {
                listValue.setValueAsObject(value);
            }
        }
    }

    @Override
    public void released() {

    }

    @Override
    public void bloom() {

    }

    @Override
    public void key(final char typedChar, final int keyCode) {

    }
}

