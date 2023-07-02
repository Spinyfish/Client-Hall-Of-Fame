package me.eldodebug.soar.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.util.UUIDTypeAdapter;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class SkinUtils {

	private static Gson GSON = new Gson();
	
    public static void loadSkin(Minecraft mc, String name, UUID uuid) {
        loadSkin(mc, name, uuid, new File(new File(mc.mcDataDir, "soar/cachedImages/models"), name + ".png"), new File(new File(mc.mcDataDir, "soar/cachedImages/faces"), name + ".png"));
    }
    
	public static void loadSkin(Minecraft mc, String username, UUID uuid, File modelF, File faceF) {
        if (!modelF.exists()) {
            boolean slimSkin = false;
            BufferedImage img;
            try {
                modelF.getParentFile().mkdirs();
                if (uuid == null) {
                    try (final InputStreamReader isr = new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/" + username).openStream(), StandardCharsets.UTF_8)) {
                        uuid = UUIDTypeAdapter.fromString(((JsonObject)GSON.fromJson((Reader)isr, (Class<?>)JsonObject.class)).get("id").getAsString());
                    }
                }
                String base64Data;
                try (final InputStreamReader isr2 = new InputStreamReader(new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + UUIDTypeAdapter.fromUUID(uuid)).openStream(), StandardCharsets.UTF_8)) {
                    base64Data = ((JsonObject)GSON.fromJson((Reader)isr2, (Class<?>)JsonObject.class)).getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();
                }
                final JsonObject jo = ((JsonObject)GSON.fromJson(new String(Base64.getDecoder().decode(base64Data), StandardCharsets.UTF_8), (Class<?>)JsonObject.class)).getAsJsonObject("textures").getAsJsonObject("SKIN");
                if (jo.has("metadata") && jo.getAsJsonObject("metadata").has("model") && jo.getAsJsonObject("metadata").get("model").getAsString().equalsIgnoreCase("slim")) {
                    slimSkin = true;
                }
                final String skinUrl = jo.get("url").getAsString();
                img = ImageIO.read(new URL(skinUrl));
            }
            catch (Throwable t) {
                try (final InputStream is = mc.getResourceManager().getResource(new ResourceLocation("textures/entity/steve.png")).getInputStream()) {
                    img = ImageIO.read(is);
                }
                catch (Throwable th) {
                    th.addSuppressed(t);
                    return;
                }
            }
            try {
                final BufferedImage model = new BufferedImage(16, 32, 2);
                if (img.getWidth() != 64 && (img.getHeight() != 64 || img.getHeight() != 32)) {
                    throw new IllegalStateException("Invalid skin size: " + img.getWidth() + "x" + img.getHeight());
                }
                final boolean oldSkin = img.getHeight() == 32;
                if (oldSkin) {
                    slimSkin = false;
                }
                model.getGraphics().drawImage(img.getSubimage(8, 8, 8, 8), 4, 0, null);
                model.getGraphics().drawImage(img.getSubimage(20, 20, 8, 12), 4, 8, null);
                model.getGraphics().drawImage(img.getSubimage(44, 20, slimSkin ? 3 : 4, 12), slimSkin ? 1 : 0, 8, null);
                model.getGraphics().drawImage(img.getSubimage(oldSkin ? 44 : 36, oldSkin ? 20 : 52, slimSkin ? 3 : 4, 12), 12, 8, null);
                model.getGraphics().drawImage(img.getSubimage(4, 20, 4, 12), 4, 20, null);
                model.getGraphics().drawImage(img.getSubimage(oldSkin ? 4 : 20, oldSkin ? 20 : 52, 4, 12), 8, 20, null);
                model.getGraphics().drawImage(img.getSubimage(40, 8, 8, 8), 4, 0, null);
                if (!oldSkin) {
                    model.getGraphics().drawImage(img.getSubimage(20, 36, 8, 12), 4, 8, null);
                    model.getGraphics().drawImage(img.getSubimage(44, 36, 4, 12), 0, 8, null);
                    model.getGraphics().drawImage(img.getSubimage(52, 52, 4, 12), 12, 8, null);
                    model.getGraphics().drawImage(img.getSubimage(4, 36, 4, 12), 4, 20, null);
                    model.getGraphics().drawImage(img.getSubimage(4, 52, 4, 12), 8, 20, null);
                }
                ImageIO.write(model, "png", modelF);
            }
            catch (Throwable t) {
            	t.printStackTrace();
            }
        }
        if (!faceF.exists()) {
            faceF.getParentFile().mkdirs();
            try {
                ImageIO.write(ImageIO.read(modelF).getSubimage(4, 0, 8, 8), "png", faceF);
            }
            catch (Throwable t2) {
            	t2.printStackTrace();
            }
        }
	}
}
