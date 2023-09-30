/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.visual;

import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Bind;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.impl.module.annotation.DefaultBind;

@DefaultBind(code=54, device=Bind.Device.KEYBOARD)
public class ClickGUI
extends Module {
    private final Setting<Style> style = new Setting<Style>("Style", Style.PANEL).describedBy("The style of the GUI.");
    public final Setting<Double> scrollSpeed = new Setting<Double>("Scroll Speed", 0.2).minimum(0.1).maximum(1.0).incrementation(0.05).describedBy("How fast scrolling is").visibleWhen(() -> this.style.getValue().equals((Object)Style.PANEL));
    public final Setting<Double> scrollDivider = new Setting<Double>("Scroll Divider", 5.0).minimum(1.0).maximum(10.0).incrementation(0.1).describedBy("How much to divide the scroll difference by").visibleWhen(() -> this.style.getValue().equals((Object)Style.PANEL));
    public final Setting<Boolean> british = new Setting<Boolean>("British", false).describedBy("innit bruv");

    public ClickGUI() {
        super("Click GUI", "Configure the client.", Category.VISUAL);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        switch (this.style.getValue()) {
            case PANEL: {
                this.mc.displayGuiScreen(Wrapper.getMonsoon().getPanelGUI());
                break;
            }
            case WINDOW: {
                this.mc.displayGuiScreen(Wrapper.getMonsoon().getWindowGUI());
            }
        }
        this.toggle();
    }

    public static enum Style {
        PANEL,
        WINDOW;

    }
}

