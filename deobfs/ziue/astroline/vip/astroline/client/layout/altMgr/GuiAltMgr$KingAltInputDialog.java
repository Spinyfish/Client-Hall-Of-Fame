/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.layout.altMgr.dialog.impl.InputDialog
 *  vip.astroline.client.layout.altMgr.kingAlts.KingAlts
 */
package vip.astroline.client.layout.altMgr;

import vip.astroline.client.layout.altMgr.dialog.impl.InputDialog;
import vip.astroline.client.layout.altMgr.kingAlts.KingAlts;

public class GuiAltMgr.KingAltInputDialog
extends InputDialog {
    public GuiAltMgr.KingAltInputDialog() {
        super(0.0f, 0.0f, 200.0f, 80.0f, "King Alts", KingAlts.API_KEY, "API Key");
        this.acceptButtonText = "Save API Key";
    }

    public void acceptAction() {
        KingAlts.setApiKey((String)this.dtf.getText());
        super.acceptAction();
    }
}
