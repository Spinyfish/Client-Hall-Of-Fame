/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.layout.altMgr.Alt
 *  vip.astroline.client.layout.altMgr.GuiAltMgr
 *  vip.astroline.client.layout.altMgr.dialog.impl.AltInputDialog
 *  vip.astroline.client.storage.utils.login.LoginUtils
 */
package vip.astroline.client.layout.altMgr;

import vip.astroline.client.layout.altMgr.Alt;
import vip.astroline.client.layout.altMgr.GuiAltMgr;
import vip.astroline.client.layout.altMgr.dialog.impl.AltInputDialog;
import vip.astroline.client.storage.utils.login.LoginUtils;

/*
 * Exception performing whole class analysis ignored.
 */
public class GuiAltMgr.EditAltDialog
extends AltInputDialog {
    public Alt targetAlt;
    String displayText;

    public GuiAltMgr.EditAltDialog(Alt targetAlt) {
        super(0.0f, 0.0f, 200.0f, 80.0f, "Edit this alt", targetAlt.isCracked() ? targetAlt.getNameOrEmail() : targetAlt.getEmail() + ":" + targetAlt.getPassword(), "Username / Email:Password");
        this.targetAlt = targetAlt;
        this.acceptButtonText = "Save";
    }

    public void acceptAction() {
        if (this.cef.getText().split(":").length <= 1) {
            GuiAltMgr.alts.set(GuiAltMgr.alts.indexOf(this.targetAlt), new Alt(this.cef.getText(), null, null, this.targetAlt.isStarred()));
            this.displayText = null;
        } else {
            String email = this.cef.getText().split(":")[0];
            String password = this.cef.getText().split(":")[1];
            this.displayText = LoginUtils.login((String)email, (String)password);
            if (this.displayText == null) {
                GuiAltMgr.alts.set(GuiAltMgr.alts.indexOf(this.targetAlt), new Alt(email, password, GuiAltMgr.access$200((GuiAltMgr)GuiAltMgr.this).getSession().getUsername(), this.targetAlt.isStarred()));
            } else {
                this.title = "[ERR] " + this.displayText;
            }
        }
        if (this.displayText != null) return;
        GuiAltMgr.sortAlts();
        this.destroy();
    }
}
