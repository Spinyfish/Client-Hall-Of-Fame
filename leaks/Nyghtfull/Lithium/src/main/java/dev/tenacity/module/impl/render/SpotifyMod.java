package dev.tenacity.module.impl.render;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;
import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.render.Render2DEvent;
import dev.tenacity.event.impl.render.ShaderEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.StringSetting;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.misc.IOUtils;
import dev.tenacity.utils.objects.Dragging;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import dev.tenacity.utils.spotify.SpotifyAPI;
import dev.tenacity.utils.tuples.Pair;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class SpotifyMod extends Module {

    private final StringSetting clientID = new StringSetting("Client ID");
    private final ModeSetting style = new ModeSetting("Style", "Lithium", "Lithium", "Dark", "Jello");
    private final Dragging drag = Tenacity.INSTANCE.createDrag(this, "spotify", 5, 150);
    public final float height = 40;
    public final float albumCoverSize = height - 10;
    private final float playerWidth = 135;

    private final Animation scrollTrack = new DecelerateAnimation(10000, 1, Direction.BACKWARDS);
    private final Animation scrollArtist = new DecelerateAnimation(10000, 1, Direction.BACKWARDS);

    public String[] buttons = {
            FontUtil.SKIP_LEFT,
            FontUtil.SKIP_RIGHT,
            FontUtil.SHUFFLE
    };

    public HashMap < String, Animation > buttonAnimations;

    public SpotifyAPI api;
    private CurrentlyPlayingContext currentPlayingContext;
    private Track currentTrack;
    public boolean playingMusic;
    public boolean hoveringPause;
    private boolean downloadedCover;
    private ResourceLocation currentAlbumCover;

    public SpotifyMod() {
        super("Spotify", Category.RENDER, "UI for spotify");
        addSettings(clientID, style);
    }

    @Override
    public void onShaderEvent(ShaderEvent event) {
        if (api.currentTrack == null || api.currentPlayingContext == null)
            return;

        float x = drag.getX(), y = drag.getY();
        float width = albumCoverSize + playerWidth;

        if (event.isBloom()) {
            if (event.isBloom()) {
                switch (style.getMode()) {
                    case "Lithium":
                        Pair < Color, Color > colors = HUDMod.getClientColors();
                        RoundedUtil.drawGradientCornerLR(x + (albumCoverSize - 15), y, playerWidth + 15, height, 6, colors.getFirst(), colors.getSecond());
                        break;

                    case "Dark":
                        RoundedUtil.drawRound(x + (albumCoverSize - 15), y, playerWidth + 15, height, 6, new Color(0, 0, 0, 125));
                        break;

                    case "Jello":
                        RoundedUtil.drawRound(x + (albumCoverSize - 15), y, playerWidth + 15, height, 6, new Color(255, 255, 255, 25));
                        break;
                }


                if (currentAlbumCover != null && downloadedCover) {
                    RenderUtil.resetColor();
                    mc.getTextureManager().bindTexture(currentAlbumCover);

                    RoundedUtil.drawRoundTextured(x + 5, y + 5, albumCoverSize, albumCoverSize, 7.5f, 1);
                }

            } else {
                RoundedUtil.drawRound(x, y, width, height, 6, Color.BLACK);
            }
        }
    }

    @Override
    public void onRender2DEvent(Render2DEvent event) {
        if (api.currentTrack == null || api.currentPlayingContext == null)
            return;

        if (currentTrack != api.currentTrack || currentPlayingContext != api.currentPlayingContext) {
            this.currentTrack = api.currentTrack;
            this.currentPlayingContext = api.currentPlayingContext;
        }

        playingMusic = currentPlayingContext.getIs_playing();

        float x = drag.getX(), y = drag.getY();
        float width = albumCoverSize + playerWidth;

        drag.setWidth(width);
        drag.setHeight(height);

        switch (style.getMode()) {
            case "Lithium":
                Pair < Color, Color > colors = HUDMod.getClientColors();
                RoundedUtil.drawGradientCornerLR(x, y, width, height, 6, colors.getFirst().darker(), colors.getSecond().darker());
                break;

            case "Dark":
                RoundedUtil.drawRound(x, y, width, height, 6, new Color(0, 0, 0, 125));
                break;

            case "Jello":
                RoundedUtil.drawRound(x, y, width, height, 6, new Color(255, 255, 255, 35));
                break;
        }

        final int diff = currentTrack.getDurationMs() - currentPlayingContext.getProgress_ms();
        final long diffSeconds = TimeUnit.MILLISECONDS.toSeconds(diff) % 60;
        final long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60;

        final String trackRemaining = String.format("-%s:%s", diffMinutes < 10 ? "0" + diffMinutes : diffMinutes, diffSeconds < 10 ? "0" + diffSeconds : diffSeconds);

        RenderUtil.scissor(x + albumCoverSize, y, playerWidth, height, () -> {
            final StringBuilder artistsDisplay = new StringBuilder();

            for (int artistIndex = 0; artistIndex < currentTrack.getArtists().length; artistIndex++) {
                final ArtistSimplified artist = currentTrack.getArtists()[artistIndex];
                artistsDisplay.append(artist.getName()).append(artistIndex + 1 == currentTrack.getArtists().length ? '.' : ", ");
            }

            if (scrollTrack.finished(Direction.BACKWARDS)) {
                scrollTrack.reset();
            }

            if (scrollArtist.finished(Direction.BACKWARDS)) {
                scrollArtist.reset();
            }

            boolean needsToScrollTrack = lithiumBoldFont26.getStringWidth(currentTrack.getName()) > playerWidth;
            boolean needsToScrollArtist = lithiumFont22.getStringWidth(artistsDisplay.toString()) > playerWidth;

            float trackX = (float)(((x + albumCoverSize) - lithiumBoldFont22.getStringWidth(currentTrack.getName())) + ((lithiumBoldFont22.getStringWidth(currentTrack.getName()) + playerWidth) * scrollTrack.getLinearOutput()));

            lithiumBoldFont22.drawString(currentTrack.getName(), needsToScrollTrack ? trackX : x + albumCoverSize + 10, y + 5, -1);

            float artistX = (float) (((x + albumCoverSize) - lithiumFont18.getStringWidth(artistsDisplay.toString())) +
                    ((lithiumFont18.getStringWidth(artistsDisplay.toString()) + playerWidth) * scrollArtist.getLinearOutput()));

            lithiumFont16.drawString(artistsDisplay.toString(), needsToScrollArtist ? artistX : x + albumCoverSize + 10, y + 18, -1);
        });

        lithiumFont16.drawString(trackRemaining, x + width - (lithiumFont16.getStringWidth(trackRemaining) + 3), y + height - (lithiumFont16.getHeight() + 5), -1);

        float progressBarWidth = (playerWidth - 40);
        float progressBarHeight = 4;
        float progress = progressBarWidth * (currentPlayingContext.getProgress_ms() / (float) currentTrack.getDurationMs());
        Color progressColor;

        switch (style.getMode()) {
            case "Lithium":
                progressColor = HUDMod.getClientColors().getFirst().brighter().brighter().brighter();
                break;
            case "Jello":
                progressColor = new Color(255, 255, 255, 65);
                break;
            default:
                progressColor = Color.WHITE;
                break;
        }

        RoundedUtil.drawRound(x + albumCoverSize + 10.5F, y + height - (progressBarHeight + 6), progressBarWidth, progressBarHeight, 1.5f, progressColor.darker().darker());
        RoundedUtil.drawRound(x + albumCoverSize + 10.5F, y + height - (progressBarHeight + 6), progress, progressBarHeight, 1.5f, progressColor);

        if (currentAlbumCover != null && downloadedCover) {
            mc.getTextureManager().bindTexture(currentAlbumCover);
            GlStateManager.color(1, 1, 1);
            GL11.glEnable(GL11.GL_BLEND);
            RoundedUtil.drawRoundTextured(x + 5, y + 5, albumCoverSize, albumCoverSize, 6, 1);
        }

        if ((currentAlbumCover == null || !currentAlbumCover.getResourcePath().contains(currentTrack.getAlbum().getId()))) {
            downloadedCover = false;

            ThreadDownloadImageData albumCover = new ThreadDownloadImageData(null, currentTrack.getAlbum().getImages()[1].getUrl(), null, new IImageBuffer() {
                @Override
                public BufferedImage parseUserSkin(BufferedImage image) {
                    downloadedCover = true;
                    return image;
                }

                @Override public void skinAvailable() { /* */ }
            });

            mc.getTextureManager().loadTexture(currentAlbumCover = new ResourceLocation("spotifyAlbums/" + currentTrack.getAlbum().getId()), albumCover);

        }

    }


    @Override
    public void onEnable() {
        if (mc.thePlayer == null) {
            toggle();
            return;
        }
        if (buttonAnimations == null) {
            buttonAnimations = new HashMap < > ();
            for (String button: buttons) {
                buttonAnimations.put(button, new DecelerateAnimation(250, 1, Direction.BACKWARDS));
            }
        }

        String clientID = this.clientID.getString();
        if (api == null) api = new SpotifyAPI();

        if (clientID.equals("")) {
            clientID = getClientIDFromJson();
            if (clientID.equals("")) {
                toggleSilent();
                return;
            }
        }

        api.build(clientID);
        setClientID(clientID);
        api.startConnection();

        super.onEnable();
    }


    public void setClientID(String clientID) {
        JsonObject keyObject = new JsonObject();
        keyObject.addProperty("clientID", clientID);
        try {
            Writer writer = new BufferedWriter(new FileWriter(SpotifyAPI.CLIENT_ID_DIR));
            SpotifyAPI.GSON.toJson(keyObject, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getClientIDFromJson() {
        JsonObject fileContent;
        try {
            fileContent = JsonParser.parseReader(new FileReader(SpotifyAPI.CLIENT_ID_DIR)).getAsJsonObject();
            if (fileContent.has("clientID")) {
                return fileContent.get("clientID").getAsString();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        openYoutubeTutorial();
        NotificationManager.post(NotificationType.WARNING, "Error", "No Client ID found");
        return "";
    }

    public void openYoutubeTutorial() {
        IOUtils.openLink("https://www.youtube.com/watch?v=3jOR29h1i40");
    }


}