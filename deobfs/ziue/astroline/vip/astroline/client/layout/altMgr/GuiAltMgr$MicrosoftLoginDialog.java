/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.layout.altMgr.GuiAltMgr
 *  vip.astroline.client.layout.altMgr.dialog.impl.InfoDialog
 */
package vip.astroline.client.layout.altMgr;

import vip.astroline.client.layout.altMgr.GuiAltMgr;
import vip.astroline.client.layout.altMgr.dialog.impl.InfoDialog;

public class GuiAltMgr.MicrosoftLoginDialog
extends InfoDialog {
    public GuiAltMgr.MicrosoftLoginDialog() {
        super(0.0f, 0.0f, 200.0f, 80.0f, "Microsoft Login", "Unknown State!");
    }

    public void draw(int mouseX, int mouseY) {
        super.draw(mouseX, mouseY);
        this.message = GuiAltMgr.oAuthService.message;
        this.buttonText = this.message.contains("Successfully logged in with account") ? "OK" : "Cancel";
    }
}
