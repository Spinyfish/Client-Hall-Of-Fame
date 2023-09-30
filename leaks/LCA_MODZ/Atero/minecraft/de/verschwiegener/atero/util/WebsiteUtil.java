package de.verschwiegener.atero.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;



import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class WebsiteUtil {
    
    public static String getYoutubeImageURL(String URL) {
	try {
	    String[] args = URL.split("watch\\?v=");
	    return "https://img.youtube.com/vi/" + args[1] + "/hqdefault.jpg";
	}catch(ArrayIndexOutOfBoundsException ex) {
	    
	}
	return "";
    }
    
    public static ResourceLocation readImage(String URL) throws IOException {
	URL url = new URL(getYoutubeImageURL(URL));
	BufferedImage image = ImageIO.read(url.openStream());
	DynamicTexture texture = new DynamicTexture(image);
	ResourceLocation location = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("YoutubeLink", texture);
	return location;
    }
    public static String getURLHeadder(String URL) throws IOException {
	Document doc = Jsoup.connect(URL).get();
	return doc.title();
    }
    //Fix youtube favicon bug
    public static ResourceLocation getFavicon(String URL) throws IOException {
	Document doc = Jsoup.connect(URL).get();
	Element element = doc.head().select("link[href~=.*\\.(ico|png)]").first();
	DynamicTexture texture = new DynamicTexture(ImageIO.read(new URL(element.attr("href"))));
	ResourceLocation location = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("FaviconLink", texture);
	return location;
    }

}
