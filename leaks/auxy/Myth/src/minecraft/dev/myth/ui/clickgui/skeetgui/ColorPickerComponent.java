/**
 * @project Myth
 * @author CodeMan
 * @at 23.10.22, 21:13
 */
package dev.myth.ui.clickgui.skeetgui;

import dev.myth.ui.clickgui.ChildComponent;
import dev.myth.ui.clickgui.Component;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ColorPickerComponent extends ChildComponent {

    private String text = "ColorPicker";
    private Supplier<Color> value;
    private Consumer<Color> action;

    public ColorPickerComponent(Component parent, String name, double x, double y, Supplier<Color> value, Consumer<Color> valueConsumer, Supplier<Boolean> isVisible) {
        super(parent, x, y, parent.getWidth() - 10, 10);

        this.text = name;
        this.value = value;
        this.action = valueConsumer;
        setVisible(isVisible);
    }
}
