import dev.africa.pandaware.utils.OsUtils;
import net.minecraft.client.main.Main;

import java.io.File;
import java.util.Arrays;

public class Start {
    public static void main(String[] args) {
        String userHome = System.getProperty("user.home", ".");
        File workingDirectory;
        switch (OsUtils.getOsType()) {
            case LINUX:
                workingDirectory = new File(userHome, ".minecraft/");
                break;
            case WINDOWS:
                String applicationData = System.getenv("APPDATA");
                String folder = applicationData != null ? applicationData : userHome;
                workingDirectory = new File(folder, ".minecraft/");
                break;
            case MAC:
                workingDirectory = new File(userHome, "Library/Application Support/minecraft");
                break;
            default:
                workingDirectory = new File(userHome, "minecraft/");
        }

        Main.main(concat(new String[]{
                "--version", "Pandaware",
                "--accessToken", "0",
                "--assetIndex", "1.8",
                "--userProperties", "{}",
                "--assetsDir", new File(workingDirectory, "assets/").getAbsolutePath()}, args));
    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
