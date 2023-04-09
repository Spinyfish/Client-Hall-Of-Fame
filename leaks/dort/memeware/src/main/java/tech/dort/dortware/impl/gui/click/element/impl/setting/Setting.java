package tech.dort.dortware.impl.gui.click.element.impl.setting;

import tech.dort.dortware.api.property.Value;
import tech.dort.dortware.impl.gui.click.element.Element;

public abstract class Setting extends Element {
    protected Value value;
    public static int SETTING_PADDING = PADDING;

    @Override
    public int getHeight() {
        return 16;
    }
}
