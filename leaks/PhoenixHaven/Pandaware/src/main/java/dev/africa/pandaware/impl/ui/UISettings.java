package dev.africa.pandaware.impl.ui;

import lombok.experimental.UtilityClass;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

@UtilityClass
public class UISettings {
    public boolean WAVEY_CAPES;

    public final Color DEFAULT_FIRST_COLOR = new Color(123, 136, 220);
    public final Color DEFAULT_SECOND_COLOR = new Color(134, 127, 213);
    public final Color INTERNAL_COLOR = new Color(0, 0, 0, 80);

    public Color FIRST_COLOR = DEFAULT_FIRST_COLOR;
    public Color SECOND_COLOR = DEFAULT_SECOND_COLOR;

    public Color CURRENT_COLOR = FIRST_COLOR;
}
