package de.fanta.utils;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

public class Stream {

    public enum Provider {
	ILoveMusik
    }

    private final Provider provider;
    private final String channelName;
    private final String fullChannelName;
    private final String channelURL;
    private String title;
    private String fulltitle;
    private String artist;
    private String coverURL;
    private BufferedImage image;
    private DynamicTexture texture;

    private ResourceLocation location;

    public Stream(final Provider provider, final String channelName, final String channelURL, final String title,
	    final String artist, final String coverURL) throws MalformedURLException, IOException {
	this.provider = provider;
	this.channelName = shortenString(channelName, 23);
	this.fullChannelName = channelName;
	this.channelURL = channelURL;
	this.title = shortenString(title, 17);
	this.artist = shortenString(artist, 17);
	this.coverURL = coverURL;
	this.fulltitle = title;
	image = ImageIO.read(new URL(coverURL));
	if (image != null) {
	    texture = new DynamicTexture(image);
	    //if(Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation(getChannelName(), texture) != null) {
		
	    //}
	    System.out.println("NEw");
	    Minecraft.getMinecraft().getTextureManager().deleteTexture(location);
	    location = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation(getChannelName(), texture);
	} else {
	   // Management.instance.streamManager.getStreams().remove(this);
	}
    }

    public String getArtist() {
	return artist;
    }

    public String getChannelName() {
	return channelName;
    }

    public String getChannelURL() {
	return channelURL;
    }

    public String getCoverURL() {
	return coverURL;
    }

    public BufferedImage getImage() {
	return image;
    }

    public ResourceLocation getLocation() {
	return location;
    }

    public Provider getProvider() {
	return provider;
    }

    public DynamicTexture getTexture() {
	return texture;
    }

    public String getTitle() {
	return title;
    }

    public void setArtist(final String artist) {
	this.artist = artist;
    }

    public void setCoverURL(final String coverURL) {
	this.coverURL = coverURL;
    }

    public void setTitle(final String title) {
	this.title = shortenString(title, 17);
	this.fulltitle = title;
    }
    public String getFulltitle() {
	return fulltitle;
    }
    public String getFullChannelName() {
	return fullChannelName;
    }
    public void setTexture(DynamicTexture texture) {
	this.texture = texture;
    }
    public void setLocation(ResourceLocation location) {
	this.location = location;
    }
    public void updateStream(String title, String artist, String coverURL) throws MalformedURLException, IOException {
	this.title = shortenString(title, 17);
	this.artist = shortenString(artist, 17);
	this.fulltitle = title;
	if(!(this.coverURL == coverURL)) {
	    this.coverURL = coverURL;
	    image = ImageIO.read(new URL(coverURL));
	    if (image == null) {
	//	Management.instance.streamManager.getStreams().remove(this);
	    }
	}
    }

    private String shortenString(final String input, final int length) {
	if((input.length() > length)) {
	    final String[] args = input.split(" ");
		final StringBuilder builder = new StringBuilder(args[0]);
		for (int i = 1; i < args.length; i++) {
		    if ((builder.toString().length() + args[i].length()) < length && !args[i].equals("&")) {
			builder.append(" " + args[i]);
		    }
		}
		return builder.toString();
	}
	return input;
    }
}
