package me.eldodebug.soar.management.mods.impl;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventRender2D;
import me.eldodebug.soar.management.image.Image;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import me.eldodebug.soar.utils.GlUtils;
import me.eldodebug.soar.utils.render.RenderUtils;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

public class ImageDisplayMod extends Mod{

    private ResourceLocation image;
    private int imageWidth, imageHeight;
    private Image prevImage;
    
	public ImageDisplayMod() {
		super("Image Display", "Display image", ModCategory.HUD);
	}
	
	@Override
	public void setup() {
		ArrayList<String> options = new ArrayList<String>();
		
		if(Soar.instance.imageManager.getImages().isEmpty()) {
			options.add("None");
		}else {
			for(Image i : Soar.instance.imageManager.getImages()) {
				options.add(i.getName());
			}
		}
		
		this.addModeSetting("Image", this, "None", options);
		this.addSliderSetting("Opacity", this, 1.0, 0.1, 1.0, false);
		this.addSliderSetting("Scale", this, 1.0, 0.1, 3.0, false);
	}
	
	@EventTarget
	public void onRender2D(EventRender2D event) {
		
		String mode = Soar.instance.settingsManager.getSettingByName(this, "Image").getValString();
		float opacity = Soar.instance.settingsManager.getSettingByName(this, "Opacity").getValFloat();
		float scale = Soar.instance.settingsManager.getSettingByName(this, "Scale").getValFloat();
		
		if(mode.equals("None")) {
			return;
		}
		
		Soar.instance.imageManager.setCurrentImage(Soar.instance.imageManager.getImageByName(mode));
		
		if(prevImage != Soar.instance.imageManager.getCurrentImage()) {
			prevImage = Soar.instance.imageManager.getCurrentImage();
	        try {
	            BufferedImage t = ImageIO.read(Soar.instance.imageManager.getCurrentImage().getFile());
	            DynamicTexture nibt = new DynamicTexture(t);

	            imageWidth = t.getWidth();
	            imageHeight = t.getHeight();
	            
	            this.image = mc.getTextureManager().getDynamicTextureLocation("Image", nibt);
	        } catch (Throwable e) {
	        	e.printStackTrace();
	        }
		}
        
        if(image != null) {
        	GlUtils.startScale(this.getX(), this.getY(), scale);
        	RenderUtils.drawImage(image, this.getX(), this.getY(), imageWidth / 2, imageHeight / 2, opacity);
        	GlUtils.stopScale();
        }
        
        this.setWidth((int) ((imageWidth / 2) * (scale)));
        this.setHeight((int) ((imageHeight / 2) * (scale)));
	}
}
