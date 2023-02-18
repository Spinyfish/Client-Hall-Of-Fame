/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.layout.altMgr.Alt
 *  vip.astroline.client.layout.altMgr.GuiAltMgr
 *  vip.astroline.client.layout.altMgr.GuiAltMgr$2$1
 */
package vip.astroline.client.layout.altMgr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;
import vip.astroline.client.layout.altMgr.Alt;
import vip.astroline.client.layout.altMgr.GuiAltMgr;

/*
 * Exception performing whole class analysis ignored.
 */
class GuiAltMgr.2
implements Runnable {
    GuiAltMgr.2() {
    }

    @Override
    public void run() {
        1 fileChooser = new /* Unavailable Anonymous Inner Class!! */;
        fileChooser.setFileSelectionMode(0);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Username:Password format (TXT)", "txt"));
        int action = fileChooser.showOpenDialog(null);
        if (action != 0) return;
        try {
            File file = fileChooser.getSelectedFile();
            BufferedReader load = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = load.readLine()) != null) {
                String[] data = line.split(":");
                GuiAltMgr.alts.add(new Alt(data[0], data[1], data.length == 3 ? data[2] : null));
            }
            load.close();
            GuiAltMgr.sortAlts();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
