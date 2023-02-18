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

public class Spacer
extends Component {
    public Spacer(Panel panel, int offX, int offY, int height) {
        super(panel, offX, offY, "");
        this.width = ClickGUI.settingsWidth;
        this.height = height;
        this.type = "Spacer";
    }

    public void render(int var1, int var2) {
    }

    public void mouseUpdates(int var1, int var2, boolean var3) {
    }
}
