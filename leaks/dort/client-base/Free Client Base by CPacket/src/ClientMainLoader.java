import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import net.minecraft.client.main.Main;

public class ClientMainLoader {
	public static String moduleFolder = "jars\\";

	public static void main(String[] args) {
		File f = new File("DLLs\\");
		for (File file : f.listFiles()) {
			if (!file.getName().endsWith(".dll"))
				continue;
			System.loadLibrary(file.getName());
		}
		try {
			Main.main(concat(new String[] { "--version", "Meme Client", "--accessToken", "0", "--gameDir", "GameData", "--assetsDir", "GameData\\assets",
					"--assetIndex", "1.8", "--userProperties", "{}" }, args));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
	private static ClassLoader sysloader;
	private static final Class[] parameters = new Class[] { URL.class };
	public static void addFile(File moduleFile) throws IOException {

		URL moduleURL = moduleFile.toURL();

		
		sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class sysclass = URLClassLoader.class;

		try {
			Method method = sysclass.getDeclaredMethod("addFile", parameters);
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] { moduleURL });
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException("Error, could not add URL to system classloader");
		}
	}
}
