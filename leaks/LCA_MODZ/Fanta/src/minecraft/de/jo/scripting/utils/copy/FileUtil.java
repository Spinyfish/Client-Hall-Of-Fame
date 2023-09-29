package de.jo.scripting.utils.copy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

	public static void writeToFile(File file, String text) {
		List<String> text1 = new ArrayList<String>();

		text1.clear();

		text1.add(text);

		writeToFile(file, text1);

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

	public static synchronized List<String> readFile(File file) {
		FileReader fileReader;
		List<String> tempList = new ArrayList<String>();
		try {
			fileReader = new FileReader(file);
		} catch (FileNotFoundException e) {
			return tempList;
		}
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(fileReader);
			String line;
			while ((line = reader.readLine()) != null)
				tempList.add(line);
		} catch (Exception e) {
			String line;
			e.printStackTrace();
			if (reader != null)
				try {
					reader.close();
				} catch (IOException ex) {
					e.printStackTrace();
				}
		}
		return tempList;
	}

}
