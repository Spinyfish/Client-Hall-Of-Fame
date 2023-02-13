import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FileUtils;

import net.minecraft.client.main.Main;
	public class InjectionAPI {
    public static void main(String[] args) {
        String userHome = System.getProperty("user.home", ".");
        File workingDirectory;
        switch(getPlatform()) {
            case LINUX:
                workingDirectory = new File(userHome, ".minecraft/");
                break;
            case WINDOWS:
                String applicationData = System.getenv("APPDATA");
                String folder = applicationData != null?applicationData:userHome;
                workingDirectory = new File(folder, ".minecraft/");
                break;
            case MACOS:
                workingDirectory = new File(userHome, "Library/Application Support/minecraft");
                break;
            default:
                workingDirectory = new File(userHome, "minecraft/");
        }
        File nativesPath = new File(workingDirectory, "/Juul/natives/");
        nativesPath.mkdirs();
        System.setProperty("java.library.path", new File(workingDirectory, "/Juul/natives/").getAbsolutePath());
        if(!new File(nativesPath, "lwjgl.dll").exists()){
			try {
				FileUtils.copyInputStreamToFile(ClassLoader.getSystemResourceAsStream("lwjgl.dll"), new File(nativesPath, "lwjgl.dll"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			if(!new File(nativesPath, "lwjgl64.dll").exists()){
			try {
				FileUtils.copyInputStreamToFile(ClassLoader.getSystemResourceAsStream("lwjgl64.dll"), new File(nativesPath, "lwjgl64.dll"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			if(!new File(nativesPath, "OpenAL32.dll").exists()){
			try {
				FileUtils.copyInputStreamToFile(ClassLoader.getSystemResourceAsStream("OpenAL32.dll"), new File(nativesPath, "OpenAL32.dll"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			if(!new File(nativesPath, "OpenAL64.dll").exists()){
			try {
				FileUtils.copyInputStreamToFile(ClassLoader.getSystemResourceAsStream("OpenAL64.dll"), new File(nativesPath, "OpenAL64.dll"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
	        System.setProperty("org.lwjgl.librarypath", new File(workingDirectory, "/Juul/natives/").getAbsolutePath());
        try {
        Main.main(new String[]{"--version", "Juul",
                        "--accessToken", "0",
                        "--assetIndex", "1.8",
                        "--userProperties", "{}",
                        "--gameDir", new File(workingDirectory, ".").getAbsolutePath(),
                        "--assetsDir", new File(workingDirectory, "assets/").getAbsolutePath()});
                        
        } catch (Exception e1) {
            
        }
    }

    public static OS getPlatform() {
        String s = System.getProperty("os.name").toLowerCase();
        return s.contains("win") ? OS.WINDOWS : (s.contains("mac") ? OS.MACOS : (s.contains("solaris") ? OS.SOLARIS : (s.contains("sunos") ? OS.SOLARIS : (s.contains("linux") ? OS.LINUX : (s.contains("unix") ? OS.LINUX : OS.UNKNOWN)))));
    }

	public static enum OS {
			LINUX,
			SOLARIS,
			WINDOWS,
			MACOS,
			UNKNOWN;
		}
}