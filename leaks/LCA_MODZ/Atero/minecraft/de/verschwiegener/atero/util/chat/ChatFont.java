package de.verschwiegener.atero.util.chat;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;


import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.util.GlyphMetrics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.MathHelper;

public class ChatFont {
    
    private DynamicTexture fontTexture;
    //increase size for more glyphs
    //HashMap<Character, GlyphMetrics> glyphMap = new HashMap<>();
    GlyphMetrics[] glyphMap;
    Double[] heightMap;
    double BASE_HEIGHT;
    
    public ChatFont() {
	try {
	    Font f = getFontByName("Inter-ExtraLight");
	    generateMap(f, 32, 255, 24F, 1F, false, false);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
    
    private void generateMap(Font font, int startChar, int stopChar, float size, float AA, boolean bold, boolean italic) throws IOException {
	glyphMap = new GlyphMetrics[stopChar];
	heightMap = new Double[stopChar];
	Map attributes = font.getAttributes();
	attributes.put(TextAttribute.SIZE, new Float(size * AA));
	attributes.put(TextAttribute.WEIGHT, bold ? TextAttribute.WEIGHT_BOLD : TextAttribute.WEIGHT_REGULAR);
	attributes.put(TextAttribute.POSTURE, italic ? TextAttribute.POSTURE_OBLIQUE : TextAttribute.POSTURE_REGULAR);
	try {
	    attributes.put(TextAttribute.class.getDeclaredField("KERNING").get(null),
		    TextAttribute.class.getDeclaredField("KERNING_ON").get(null));
	} catch (Exception ignored) {
	}
	Font scaledfont = font.deriveFont(attributes);
	
	Graphics2D g2 = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
	g2.setFont(scaledfont);
	FontMetrics metrics = g2.getFontMetrics();
	
	Graphics2D g2s = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
	g2s.setFont(font.deriveFont(size * 2));
	FontMetrics metricss = g2s.getFontMetrics();
	
	for (int i = startChar; i < stopChar; i++) {
	    char glyph = (char) i;
	    
	    Rectangle2D rect = metrics.getStringBounds(Character.toString(glyph), g2);
	    Rectangle2D recto = metricss.getStringBounds(Character.toString(glyph), g2s);
	    double width = rect.getWidth() + 8.0D;
	    double height = rect.getHeight() + 4.0D;
	    
	    //System.out.println("Char: " + (char) i);
	    
	    BufferedImage fontImage = new BufferedImage(MathHelper.ceiling_double_int(width), MathHelper.ceiling_double_int(height), BufferedImage.TYPE_INT_ARGB);
	    Graphics2D fontGraphic = fontImage.createGraphics();
	    
	    fontGraphic.setFont(scaledfont);
	    fontGraphic.setColor(new Color(255, 255, 255, 0));
	    fontGraphic.fillRect(0, 0, fontImage.getWidth(), fontImage.getHeight());
	    fontGraphic.setColor(Color.WHITE);
	    fontGraphic.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
	    fontGraphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    fontGraphic.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    fontGraphic.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
	    fontGraphic.drawString(Character.toString(glyph), 4, metrics.getAscent());
	    fontGraphic.dispose();
	    
	    DynamicTexture fontTexture = new DynamicTexture(fontImage);
	    //glyphMap.putIfAbsent(glyph, new GlyphMetrics(width, height, fontTexture.getGlTextureId(), recto.getWidth() + 8.0D, recto.getHeight() + 4.0D));
	    glyphMap[i] = new GlyphMetrics(width, height, fontTexture.getGlTextureId());
	    heightMap[i] = height;
	    BASE_HEIGHT += height;
	}
	BASE_HEIGHT = BASE_HEIGHT / (stopChar - startChar);
    }
    public DynamicTexture getFontTexture() {
	return fontTexture;
    }
    public double getBASE_HEIGHT() {
	return BASE_HEIGHT;
    }

    public static Font getFontByName(final String name) {
	try {
	    return Font.createFont(Font.TRUETYPE_FONT, Management.class.getResourceAsStream(
		    "/assets/minecraft/" + Management.instance.CLIENT_NAME.toLowerCase() + "/fonts/" + name + ".ttf"));
	} catch (FontFormatException | IOException e) {
	    e.printStackTrace();
	}
	return null;
    }

}
