package me.eldodebug.soar.management.image;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import me.eldodebug.soar.Soar;

public class ImageManager {

	private ArrayList<Image> images = new ArrayList<Image>();
	private Image currentImage;
	
	public ImageManager() {
		this.loadImage();
	}
	
	public void loadImage() {
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File file, String str){
				if (str.endsWith("png")){
					return true;
				}else{
					return false;
				}
			}
		};
		
		File fileArray[] = Soar.instance.fileManager.getImageDir().listFiles(filter);

		for(File f : fileArray) {
			images.add(new Image(f.getName(), new File(Soar.instance.fileManager.getImageDir(), f.getName())));
		}
	}

	public ArrayList<Image> getImages() {
		return images;
	}

	public Image getCurrentImage() {
		return currentImage;
	}

	public void setCurrentImage(Image currentImage) {
		this.currentImage = currentImage;
	}
	
	public Image getImageByName(String name) {
		return images.stream().filter(image -> image.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
}
