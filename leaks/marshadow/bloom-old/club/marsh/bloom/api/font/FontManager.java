package club.marsh.bloom.api.font;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import club.marsh.bloom.impl.utils.render.FontRenderer;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/*
 * Custom font manager for the font renderer.
 *
 * @author darraghd493
 */
public class FontManager
{
    public static Logger logger = LogManager.getFormatterLogger("Minecraft Font Renderer");
    private Map<String, FontRenderer> fonts;
    public FontRenderer defaultFont = null;
    public FontRenderer quickSandBig;
    private String domain;
    
    public static Font getFontFromTTF(ResourceLocation fontLocation, float fontSize, int fontType)
    {
        Font output = null;
        
        try
        {
            output = Font.createFont(fontType, Minecraft.getMinecraft().getResourceManager().getResource(fontLocation).getInputStream());
            output = output.deriveFont(fontSize);
        }
        
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return output;
    }

    public FontRenderer getFont(final String name, int size)
    {
        String key = String.format("%s %s", name, size);

        if (this.fonts.containsKey(key))
        {
            return this.fonts.get(key);
        }
        
        else
        {
        	try
            {
                InputStream inputStream = this.getClass().getResourceAsStream(String.format("/assets/minecraft/%s/%s.ttf", domain, name));
                this.generateFont(name, inputStream, size);
                logger.info("Generated font " + name + " with size " + size + "!");
                return this.fonts.get(key);
            }
            
            catch (final IOException | FontFormatException e)
            {
                e.printStackTrace();
                logger.error("Failed to generate font " + key + "!");
            }
        }

        return this.defaultFont;
    }

    private void generateFont(final String name, InputStream inputStream, int size) throws IOException, FontFormatException
    {
        Font font = Font.createFont(0, inputStream);
        font = font.deriveFont(Font.PLAIN, (float) size);
        this.fonts.put(String.format("%s %s", name, size), new FontRenderer(font));
    }
    
    public FontManager(final String domain)
    {
        logger.info("Initializing font manager");
        logger.info("Generating default font");
        this.domain = domain;
        this.defaultFont = new FontRenderer(new Font("Sans", Font.PLAIN, 18));
        this.quickSandBig = defaultFont;
        this.fonts = new HashMap<>();
        
        try
        {
            defaultFont = new FontRenderer(getFontFromTTF(new ResourceLocation("Bloom/quicksand.ttf"),18,Font.PLAIN));
            quickSandBig = new FontRenderer(getFontFromTTF(new ResourceLocation("Bloom/quicksand.ttf"),22,Font.PLAIN));
        }
        
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        logger.info("Font manager initialized successfully");
    }
    
    public String find(final FontRenderer fontRenderer)
    {
        for (final Map.Entry<String, FontRenderer> entry : this.fonts.entrySet())
        {
        	if (entry.getValue().equals(fontRenderer))
        	{
        		return entry.getKey();
        	}
        }
        
        return null;
    }

    public TruePair<String, Integer> unparse(final String string)
    {
        String[] split = string.split(" ");
        String name = String.join(" ", split).substring(0, string.length() - split[split.length - 1].length() - 1);
        int size = Integer.parseInt(split[split.length - 1]);
        return new TruePair<>(name, size);
    }
    
    public class TruePair<T, D>
    {
        private T first;
        private D second;
        
        public TruePair(T first, D second)
        {
        	this.first = first;
        	this.second = second;
        }
    }
}