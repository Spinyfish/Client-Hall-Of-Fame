/**
 * @project hakarware
 * @author CodeMan
 * @at 25.07.23, 16:28
 */

package cc.swift.module.impl.render;

import cc.swift.Swift;
import cc.swift.module.Module;
import cc.swift.value.impl.BooleanValue;
import cc.swift.value.impl.DoubleValue;

public final class ClickGuiModule extends Module {
    public final DoubleValue opacity = new DoubleValue("Opacity", 255D, 0, 255, 1);
    public final BooleanValue blur = new BooleanValue("Blur", false);

    public ClickGuiModule() {
        super("ClickGui", Category.RENDER);
        registerValues(opacity, blur);
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(Swift.INSTANCE.getClickGui());
        toggle();
    }
}
