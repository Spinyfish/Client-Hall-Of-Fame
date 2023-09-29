package net.optifine.player;

import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.http.HttpPipeline;
import net.optifine.http.HttpUtils;
import net.optifine.util.Json;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PlayerConfigurationParser {
    private String player = null;
    public static final String CONFIG_ITEMS = "items";
    public static final String ITEM_TYPE = "type";
    public static final String ITEM_ACTIVE = "active";

    public PlayerConfigurationParser(final String player) {
        this.player = player;
    }

    public PlayerConfiguration parsePlayerConfiguration(final JsonElement je) {
        if (je == null) {
            throw new JsonParseException("JSON object is null, player: " + this.player);
        } else {
            final JsonObject jsonobject = (JsonObject) je;
            final PlayerConfiguration playerconfiguration = new PlayerConfiguration();
            final JsonArray jsonarray = (JsonArray) jsonobject.get("items");

            if (jsonarray != null) {
                for (int i = 0; i < jsonarray.size(); ++i) {
                    final JsonObject jsonobject1 = (JsonObject) jsonarray.get(i);
                    final boolean flag = Json.getBoolean(jsonobject1, "active", true);

                    if (flag) {
                        final String s = Json.getString(jsonobject1, "type");

                        if (s == null) {
                            Config.warn("Item type is null, player: " + this.player);
                        } else {
                            String s1 = Json.getString(jsonobject1, "model");

                            if (s1 == null) {
                                s1 = "items/" + s + "/model.cfg";
                            }

                            final PlayerItemModel playeritemmodel = this.downloadModel(s1);

                            if (playeritemmodel != null) {
                                if (!playeritemmodel.isUsePlayerTexture()) {
                                    String s2 = Json.getString(jsonobject1, "texture");

                                    if (s2 == null) {
                                        s2 = "items/" + s + "/users/" + this.player + ".png";
                                    }

                                    final BufferedImage bufferedimage = this.downloadTextureImage(s2);

                                    if (bufferedimage == null) {
                                        continue;
                                    }

                                    playeritemmodel.setTextureImage(bufferedimage);
                                    final ResourceLocation resourcelocation = new ResourceLocation("optifine.net", s2);
                                    playeritemmodel.setTextureLocation(resourcelocation);
                                }

                                playerconfiguration.addPlayerItemModel(playeritemmodel);
                            }
                        }
                    }
                }
            }

            return playerconfiguration;
        }
    }

    private BufferedImage downloadTextureImage(final String texturePath) {
        final String s = HttpUtils.getPlayerItemsUrl() + "/" + texturePath;

        try {
            final byte[] abyte = HttpPipeline.get(s, Minecraft.getMinecraft().getProxy());
            final BufferedImage bufferedimage = ImageIO.read(new ByteArrayInputStream(abyte));
            return bufferedimage;
        } catch (final IOException ioexception) {
            Config.warn("Error loading item texture " + texturePath + ": " + ioexception.getClass().getName() + ": " + ioexception.getMessage());
            return null;
        }
    }

    private PlayerItemModel downloadModel(final String modelPath) {
        final String s = HttpUtils.getPlayerItemsUrl() + "/" + modelPath;

        try {
            final byte[] abyte = HttpPipeline.get(s, Minecraft.getMinecraft().getProxy());
            final String s1 = new String(abyte, StandardCharsets.US_ASCII);
            final JsonParser jsonparser = new JsonParser();
            final JsonObject jsonobject = (JsonObject) jsonparser.parse(s1);
            final PlayerItemModel playeritemmodel = PlayerItemParser.parseItemModel(jsonobject);
            return playeritemmodel;
        } catch (final Exception exception) {
            Config.warn("Error loading item model " + modelPath + ": " + exception.getClass().getName() + ": " + exception.getMessage());
            return null;
        }
    }
}
