package me.eldodebug.soar.gui.screenshot;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import me.eldodebug.soar.utils.GlUtils;
import me.eldodebug.soar.utils.animation.normal.Animation;
import me.eldodebug.soar.utils.animation.normal.Direction;
import me.eldodebug.soar.utils.animation.normal.impl.EaseBackIn;
import me.eldodebug.soar.utils.animation.simple.SimpleAnimation;
import me.eldodebug.soar.utils.color.ColorUtils;
import me.eldodebug.soar.utils.font.FontUtils;
import me.eldodebug.soar.utils.mouse.MouseUtils;
import me.eldodebug.soar.utils.render.RoundedUtils;
import me.eldodebug.soar.utils.render.StencilUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

public class GuiScreenshotViewer extends GuiScreen{

	private ArrayList<String> screenshots = new ArrayList<String>();
	
	private File screenshotDir;
	
	private int prevScreenshots;
	
	private Animation introAnimation;
	private boolean close;
	
	private ResourceLocation image;
	private String selectedImage;
	private String prevSelectedImage;
	private int selected = 0;

	private float translate = 0;
	private SimpleAnimation changeAnimation = new SimpleAnimation(0.0F);
	
	@Override
	public void initGui() {
		close = false;
		screenshotDir = new File(mc.mcDataDir, "screenshots");
		
		if(!screenshotDir.exists()) {
			screenshotDir.mkdir();
		}
		
        introAnimation = new EaseBackIn(450, 1, 2);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		ScaledResolution sr = new ScaledResolution(mc);
		
		int addX = 170;
		int addY = 110;
		int x = sr.getScaledWidth() / 2 - addX;
		int y = sr.getScaledHeight() / 2 - addY;
		
		this.loadScreenshot();
		
		if(close) {
			introAnimation.setDirection(Direction.BACKWARDS);
			if(introAnimation.isDone(Direction.BACKWARDS)) {
				mc.displayGuiScreen(null);
			}
		}
		
		GlUtils.startScale(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, (float) introAnimation.getValue());
		
		//Draw background
		RoundedUtils.drawRound(x, y, addX * 2, addY * 2, 6, ColorUtils.getBackgroundColor(2));
		
		if(!screenshots.isEmpty()) {
			selectedImage = screenshots.get(selected);
			
			if(prevSelectedImage != selectedImage) {
				
				prevSelectedImage = selectedImage;
				
				try {
					BufferedImage t = ImageIO.read(new File(screenshotDir, selectedImage));
		            DynamicTexture nibt = new DynamicTexture(t);
		            
		            image = mc.getTextureManager().getDynamicTextureLocation("Image", nibt);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(image != null) {
				
				changeAnimation.setAnimation(translate, 18);
				
				FontUtils.regular_bold24.drawString(selectedImage, x + 20, y + 10, ColorUtils.getFontColor(2).getRGB());
				mc.getTextureManager().bindTexture(image);
				GlStateManager.enableBlend();
				
		        StencilUtils.initStencilToWrite();
				RoundedUtils.drawRound(x, y, addX * 2, addY * 2, 6, ColorUtils.getBackgroundColor(2));
		        StencilUtils.readStencilBuffer(1);
		        
				GlUtils.startTranslate(0, changeAnimation.getValue());
				RoundedUtils.drawRoundTextured(x + 20, y + 30, 300, 180, 6, 1.0F);
				GlUtils.stopTranslate();
				
				StencilUtils.uninitStencilBuffer();
				
				FontUtils.icon24.drawString("W", x + 3, y + 107, ColorUtils.getFontColor(2).getRGB());
				FontUtils.icon24.drawString("V", (x + addX * 2 ) - 15, y + 107, ColorUtils.getFontColor(2).getRGB());
				
				RoundedUtils.drawRound((x + addX * 2) - 70, y + 4, 50, 20, 6, ColorUtils.getBackgroundColor(4));
				FontUtils.regular20.drawString("Open", (x + addX * 2) - 57, y + 11, ColorUtils.getFontColor(2).getRGB());
			}
		}else {
			FontUtils.regular_bold24.drawString("You haven't taken screenshots yet", x + 20, y + 10, ColorUtils.getFontColor(2).getRGB());
		}
		
		GlUtils.stopScale();
		
		if(translate == 90) {
			translate = 0;
		}
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		
		ScaledResolution sr = new ScaledResolution(mc);
		
		int addX = 170;
		int addY = 110;
		int x = sr.getScaledWidth() / 2 - addX;
		int y = sr.getScaledHeight() / 2 - addY;
		
		if(mouseButton == 0) {
			
			//Left
			if(MouseUtils.isInside(mouseX, mouseY, x + 2, y + 106, 15, 15)) {
				if(selected != 0) {
					translate = 90;
					selected--;
				}
			}
			
			//Right
			if(MouseUtils.isInside(mouseX, mouseY, (x + addX * 2 ) - 17, y + 106, 15, 15)) {
				if(selected < screenshots.size() - 1) {
					translate = 90;
					selected++;
				}
			}
			
			//Open
			if(MouseUtils.isInside(mouseX, mouseY, (x + addX * 2) - 70, y + 4, 50, 20)) {
				Desktop desktop = Desktop.getDesktop();
				
				try {
					desktop.browse(new File(screenshotDir, screenshots.get(selected)).toURI());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void keyTyped(char typedChar, int keyCode) {
		if(keyCode == 1) {
			close = true;
		}
	}
	
	private void loadScreenshot() {
		if(prevScreenshots != screenshotDir.listFiles().length) {
			
			prevScreenshots = screenshotDir.listFiles().length;
			
			screenshots.clear();
			
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File file, String str){
					if (str.endsWith("png")){
						return true;
					}else{
						return false;
					}
				}
			};
			
			File fileArray[] = screenshotDir.listFiles(filter);
			
			for(File f : fileArray) {
				screenshots.add(f.getName());
			}
		}
	}
	
    @Override
    public boolean doesGuiPauseGame(){
        return false;
    }
}
