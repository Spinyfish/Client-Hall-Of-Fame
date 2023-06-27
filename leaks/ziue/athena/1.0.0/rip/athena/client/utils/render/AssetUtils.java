package rip.athena.client.utils.render;

import net.minecraft.util.*;
import rip.athena.client.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.*;
import java.nio.file.*;

public class AssetUtils
{
    public static ResourceLocation getResourceFromFile(final File file) {
        return getResourceFromPath(file);
    }
    
    public static ResourceLocation getResource(final String... strings) {
        final File file = buildStringFile(strings);
        return getResourceFromPath(file);
    }
    
    public static File getResourceAsFile(final String... strings) {
        return buildStringFile(strings);
    }
    
    public static InputStream getResourceAsStream(final String... strings) {
        final File file = buildStringFile(strings);
        try {
            return new FileInputStream(file);
        }
        catch (FileNotFoundException e) {
            Athena.INSTANCE.getLog().error("Failed to load stream outside namespace (" + file.getAbsolutePath() + ")." + e);
            return null;
        }
    }
    
    public static BufferedImage getResourceAsImage(final String... strings) {
        final File file = buildStringFile(strings);
        try {
            return ImageIO.read(file);
        }
        catch (IOException e) {
            Athena.INSTANCE.getLog().error("Failed to load raw image resource outside namespace (" + file.getAbsolutePath() + ")." + e);
            return null;
        }
    }
    
    private static ResourceLocation getResourceFromPath(final File file) {
        DynamicTexture texture;
        try {
            texture = new DynamicTexture(ImageIO.read(file));
        }
        catch (IOException e) {
            Athena.INSTANCE.getLog().error("Failed to load resource outside namespace (" + file.getAbsolutePath() + ")." + e);
            return null;
        }
        return Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation(file.getName(), texture);
    }
    
    private static File buildStringFile(final String... strings) {
        final StringBuilder builder = new StringBuilder("assets/minecraft/Athena");
        for (int i = 0; i < strings.length; ++i) {
            String string = strings[i];
            if (i == 0) {
                string = string.replace(':', '/');
            }
            builder.append("/" + string);
        }
        final File file = Paths.get(Minecraft.getMinecraft().mcDataDir.getPath(), builder.toString().split("/")).toFile();
        return file;
    }
}
