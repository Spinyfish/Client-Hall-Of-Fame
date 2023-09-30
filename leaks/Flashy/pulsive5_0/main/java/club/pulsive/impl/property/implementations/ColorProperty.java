package club.pulsive.impl.property.implementations;


import club.pulsive.impl.property.Property;

import java.awt.*;
import java.util.function.Supplier;

public class ColorProperty extends Property<Color> {

    public ColorProperty(String label, Color value, Supplier<Boolean> dependency) {
        super(label, value, dependency);
    }

    public ColorProperty(String label, Color value) {
        this(label, value, () -> true);
    }

}
