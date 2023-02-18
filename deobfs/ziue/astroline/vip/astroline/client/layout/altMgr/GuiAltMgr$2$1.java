/*
 * Decompiled with CFR 0.152.
 */
package vip.astroline.client.layout.altMgr;

import java.awt.Component;
import java.awt.HeadlessException;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

class GuiAltMgr.1
extends JFileChooser {
    GuiAltMgr.1() {
    }

    @Override
    protected JDialog createDialog(Component parent) throws HeadlessException {
        JDialog dialog = super.createDialog(parent);
        dialog.setAlwaysOnTop(true);
        return dialog;
    }
}
