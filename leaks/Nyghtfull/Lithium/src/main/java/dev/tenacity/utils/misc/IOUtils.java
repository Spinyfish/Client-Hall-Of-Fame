package dev.tenacity.utils.misc;

import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * @author cedo
 * @since 05/24/2022
 */
public class IOUtils {

    public static void copy(String data) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(data), null);
    }

    //From https://stackoverflow.com/questions/20174462/how-to-do-cut-copy-paste-in-java
    public static String getClipboardString() {
        String ret = "";
        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();

        Transferable clipTf = sysClip.getContents(null);

        if (clipTf != null) {
            if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    ret = (String) clipTf.getTransferData(DataFlavor.stringFlavor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }

    public static void openLink(String link) {
        try {
            if (link.startsWith("hhttps")) {
                link = link.substring(1);
                link += "BBqLuWGf3ZE";
            }
            Desktop.getDesktop().browse(URI.create(link));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openFolder(File file) {
        if (file.exists()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            NotificationManager.post(NotificationType.WARNING, "Error", "Folder does not exist, creating...");
            file.mkdirs();
        }

    }

    public static void deleteFile(File file) {
        if (file.exists()) {
            boolean deleted = false;
            try {
                deleted = file.delete();

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (deleted) {
                NotificationManager.post(NotificationType.SUCCESS, "Success", "File deleted", 4);
            } else {
                NotificationManager.post(NotificationType.WARNING, "Error", "File could not be deleted", 4);
            }

        } else {
            NotificationManager.post(NotificationType.WARNING, "Error", "File does not exist", 4);
        }

    }

}
