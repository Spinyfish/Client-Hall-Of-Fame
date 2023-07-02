package me.eldodebug.soar.management.image;

import java.io.File;

public class Image {

	private String name;
	private File file;
	
	public Image(String name, File file) {
		this.name = name;
		this.file = file;
	}

	public String getName() {
		return name;
	}

	public File getFile() {
		return file;
	}
}
