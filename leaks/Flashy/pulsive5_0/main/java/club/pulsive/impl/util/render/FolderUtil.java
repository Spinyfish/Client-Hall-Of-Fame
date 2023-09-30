package club.pulsive.impl.util.render;

import net.minecraft.util.Util;

import java.io.File;
import java.io.IOException;

public class FolderUtil {
    public static boolean openFolder(File file) {
        String path = file.getAbsolutePath();
        try {
            switch (Util.getOSType()) {
                case OSX:
                    Runtime.getRuntime().exec(new String[]{"/usr/bin/open", path});
                    break;
                case WINDOWS:
                    Runtime.getRuntime().exec(String.format("cmd.exe /C start \"Open file\" \"%s\"", path));
                    break;
            }
            return true;
        } catch (IOException e) {
            System.out.println("Couldn't open folder.\n" + e.getStackTrace());
        }
        return false;
    }
    
}
