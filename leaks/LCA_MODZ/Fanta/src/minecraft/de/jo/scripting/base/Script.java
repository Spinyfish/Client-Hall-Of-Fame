package de.jo.scripting.base;

import java.io.File;
import java.util.List;

import de.jo.scripting.utils.FileUtil;

public class Script {
	
	private File file;
	private String name, author, description;
	private float version;
	
	private List<String> lines;
	
	public static final String FILE_TYPE = ".js";
	
	public Script(File file) {
		if(!file.getName().endsWith(FILE_TYPE)) {
			System.err.println("Invalid file");
			return;
		}
		try {
			this.file = file;
			this.lines = FileUtil.readFile(file);
			if(this.lines.size() < 3) {
				System.err.println("Couldn't load Script");
				return;
			}
			this.name = "Default";
			this.author = "None";
			this.version = 1.0F;
			for(int i = 0; i < 3; i++) {
				String line = this.lines.get(i);
				if(line.toLowerCase().startsWith("//name")) {
					this.name = line.split(" ")[1].trim();
				}
				if(line.toLowerCase().startsWith("//author")) {
					this.author = line.split(" ")[1].trim();
				}
				if(line.toLowerCase().startsWith("//version")) {
					this.version = Float.valueOf(line.split(" ")[1].trim());
				}
			}
			
			
		} catch (Exception e) {
			System.err.println("Couldn't load Script");
		}
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getDescription() {
		return description;
	}
	
	public File getFile() {
		return file;
	}
	
	public List<String> getLines() {
		return lines;
	}
	
	public String getName() {
		return name;
	}
	
	public float getVersion() {
		return version;
	}
	
}
