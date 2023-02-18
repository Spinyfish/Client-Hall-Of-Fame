/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.layout.dropdown.ClickGUI
 *  vip.astroline.client.layout.dropdown.components.Component
 *  vip.astroline.client.layout.dropdown.panel.Panel
 */
package vip.astroline.client.layout.dropdown.components.impl;

import vip.astroline.client.layout.dropdown.ClickGUI;
import vip.astroline.client.layout.dropdown.components.Component;
import vip.astroline.client.layout.dropdown.panel.Panel;

public class Placeholder
extends Component {
    public Placeholder(Panel panel, int offX, int offY, Component target) {
        super(panel, offX, offY, target.title);
        this.width = Math.max(ClickGUI.defaultWidth, panel.width);
        this.height = 0;
        this.type = "Placeholder";
    }

    public void render(int mouseX, int mouseY) {
    }

    public void mouseUpdates(int mouseX, int mouseY, boolean isPressed) {
    }
}
