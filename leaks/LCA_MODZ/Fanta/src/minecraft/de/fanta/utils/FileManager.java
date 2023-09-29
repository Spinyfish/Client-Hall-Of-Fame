package de.fanta.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;

public class FileManager{

	public static final File CLIENT_DIR = new File(Minecraft.getMinecraft().mcDataDir, "Fanta");
	
	public static void writeToFile(File file, String text) {
		List<String> text1 = new ArrayList<String>();
		text1.clear();
		text1.add(text);
		writeToFile(file, text1);
	}

	public static boolean createDirectory(String name) {
		File dir = new File(CLIENT_DIR, name);
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
			dir.mkdirs();
		}
		return dir.isDirectory();
	}

	public static File getDirectory(String name) {
		createDirectory(name);
		return new File(CLIENT_DIR, name.endsWith(File.separator) ? name : name+"/");
	}

	public static synchronized void writeToFile(File file, List<String> text) {
		writeToFile(file, text.<String>toArray(new String[text.size()]));
	}

	public static synchronized void writeToFile(File file, String[] text) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new FileWriter(file));
			byte b;
			int i;
			String[] arrayOfString;
			for (i = (arrayOfString = text).length, b = 0; b < i;) {
				String line = arrayOfString[b];
				writer.println(line);
				writer.flush();
				b++;
			}
		} catch (Exception localException) {
		} finally {
			if (writer != null)
				writer.close();
		}
	}
}
