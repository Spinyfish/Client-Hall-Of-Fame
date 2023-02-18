/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.layout.dropdown.components.impl.slider.BasicSlider
 *  vip.astroline.client.layout.dropdown.panel.Panel
 *  vip.astroline.client.service.module.value.FloatValue
 */
package vip.astroline.client.layout.dropdown.components.impl.slider;

import vip.astroline.client.layout.dropdown.components.impl.slider.BasicSlider;
import vip.astroline.client.layout.dropdown.panel.Panel;
import vip.astroline.client.service.module.value.FloatValue;

public class Slider
extends BasicSlider {
    public FloatValue storage;
    public String setting;

    public Slider(FloatValue value, Panel panel, int offX, int offY, String title) {
        super(panel, value, offX, offY, title, value.getMin(), value.getMax(), value.getIncrement());
        this.unit = value.getUnit();
        this.storage = value;
        this.setting = "WTF";
        this.type = "Slider";
    }

    public void update(int mouseX, int mouseY) {
        super.update(mouseX, mouseY);
        if (this.isDragging) {
            this.storage.setValue(this.value);
        } else {
            this.value = this.storage.getValue().floatValue();
        }
    }
}
