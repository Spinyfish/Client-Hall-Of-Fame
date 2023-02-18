/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.thealtening.auth.TheAlteningAuthentication
 *  com.thealtening.auth.service.AlteningServiceType
 *  net.minecraft.client.Minecraft
 *  vip.astroline.client.layout.altMgr.Alt
 *  vip.astroline.client.layout.altMgr.GuiAltMgr
 *  vip.astroline.client.layout.altMgr.dialog.impl.AltInputDialog
 *  vip.astroline.client.storage.utils.login.LoginUtils
 */
package vip.astroline.client.layout.altMgr;

import com.thealtening.auth.TheAlteningAuthentication;
import com.thealtening.auth.service.AlteningServiceType;
import net.minecraft.client.Minecraft;
import vip.astroline.client.layout.altMgr.Alt;
import vip.astroline.client.layout.altMgr.GuiAltMgr;
import vip.astroline.client.layout.altMgr.dialog.impl.AltInputDialog;
import vip.astroline.client.storage.utils.login.LoginUtils;

/*
 * Exception performing whole class analysis ignored.
 */
public class GuiAltMgr.AddAltDialog
extends AltInputDialog {
    public String displayText;

    public GuiAltMgr.AddAltDialog() {
        super(0.0f, 0.0f, 200.0f, 80.0f, "Add an alt", Minecraft.getMinecraft().getSession().getUsername(), "Username / Email:Password");
        this.displayText = null;
    }

    public void acceptAction() {
        new Thread(() -> {
            if (this.cef.getText().contains("@alt.com")) {
                TheAlteningAuthentication.theAltening().updateService(AlteningServiceType.THEALTENING);
                this.displayText = LoginUtils.login((String)this.cef.getText().split(":")[0], (String)"Astroline");
                if (this.displayText == null) {
                    GuiAltMgr.alts.add(0, new Alt(this.cef.getText().split(":")[0], "Astroline", GuiAltMgr.access$000((GuiAltMgr)GuiAltMgr.this).getSession().getUsername()));
                    GuiAltMgr.sortAlts();
                    this.destroy();
                } else {
                    this.title = "[ERR TheAltening]" + this.displayText;
                }
                return;
            }
            if (this.cef.getText().split(":").length <= 1) {
                GuiAltMgr.alts.add(0, new Alt(this.cef.getText(), null, null));
                this.displayText = null;
            } else {
                String email = this.cef.getText().split(":")[0];
                String password = this.cef.getText().split(":")[1];
                this.displayText = LoginUtils.login((String)email, (String)password);
                if (this.displayText == null) {
                    GuiAltMgr.alts.add(0, new Alt(email, password, GuiAltMgr.access$100((GuiAltMgr)GuiAltMgr.this).getSession().getUsername()));
                } else {
                    this.title = "[ERR] " + this.displayText;
                }
            }
            if (this.displayText != null) return;
            GuiAltMgr.sortAlts();
            this.destroy();
        }).start();
    }
}
