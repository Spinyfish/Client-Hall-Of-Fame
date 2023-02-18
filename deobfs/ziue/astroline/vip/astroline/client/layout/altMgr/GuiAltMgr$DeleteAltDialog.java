/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.layout.altMgr.Alt
 *  vip.astroline.client.layout.altMgr.GuiAltMgr
 *  vip.astroline.client.layout.altMgr.dialog.impl.ConfirmDialog
 *  vip.astroline.client.service.font.FontManager
 */
package vip.astroline.client.layout.altMgr;

import vip.astroline.client.layout.altMgr.Alt;
import vip.astroline.client.layout.altMgr.GuiAltMgr;
import vip.astroline.client.layout.altMgr.dialog.impl.ConfirmDialog;
import vip.astroline.client.service.font.FontManager;

/*
 * Exception performing whole class analysis ignored.
 */
public class GuiAltMgr.DeleteAltDialog
extends ConfirmDialog {
    public Alt targetAlt;

    public GuiAltMgr.DeleteAltDialog(Alt targetAlt) {
        super(0.0f, 0.0f, 200.0f, 60.0f, "Are you sure to delete this alt?", "\"" + targetAlt.getNameOrEmail() + "\" will be lost forever!");
        float stringWid = FontManager.sans16.getStringWidth(this.message) + 20.0f;
        this.width = stringWid < 140.0f ? 140.0f : (float)((int)stringWid);
        this.targetAlt = targetAlt;
    }

    public void acceptAction() {
        GuiAltMgr.alts.remove(this.targetAlt);
        GuiAltMgr.sortAlts();
        super.acceptAction();
    }
}
