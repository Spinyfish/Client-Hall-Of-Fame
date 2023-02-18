/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.EnumChatFormatting
 *  vip.astroline.client.layout.altMgr.GuiAltMgr
 *  vip.astroline.client.layout.altMgr.dialog.impl.ConfirmDialog
 */
package vip.astroline.client.layout.altMgr;

import net.minecraft.util.EnumChatFormatting;
import vip.astroline.client.layout.altMgr.GuiAltMgr;
import vip.astroline.client.layout.altMgr.dialog.impl.ConfirmDialog;

/*
 * Exception performing whole class analysis ignored.
 */
public class GuiAltMgr.ClearAllDialog
extends ConfirmDialog {
    public GuiAltMgr.ClearAllDialog() {
        super(0.0f, 0.0f, 200.0f, 60.0f, "Are you sure you want to remove EVERY alt?", EnumChatFormatting.RED + "All alt" + EnumChatFormatting.WHITE + " will be lost forever!");
    }

    public void acceptAction() {
        GuiAltMgr.alts.clear();
        GuiAltMgr.sortAlts();
        super.acceptAction();
    }
}
