package me.jinthium.straight.impl.modules.visual;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.SpotifyAPI;
import me.jinthium.straight.api.dragging.Dragging;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.render.Render2DEvent;
import me.jinthium.straight.impl.utils.render.*;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

public class SpotifyHud extends Module {

    private final Dragging spotifyHudDraggable = Client.INSTANCE.createDrag(this, "spotify-hud", 50, 100);

    public SpotifyHud(){
        super("SpotifyHud", Category.VISUALS);
    }

    private boolean downloadedCover;
    private Color imageColor = Color.white;
    private ResourceLocation currentAlbumCover;
    private final SpotifyAPI spotifyAPI = new SpotifyAPI();
    private Track currentTrack;
    private CurrentlyPlayingContext currentPlayingContext;

    @Callback
    final EventCallback<Render2DEvent> render2DEventEventCallback = event -> {
        //If the user is not playing anything or if the user is not authenticated yet
        if (mc.thePlayer == null || spotifyAPI.currentTrack == null || spotifyAPI.currentPlayingContext == null) {
            return;
        }
        Hud hud = Client.INSTANCE.getModuleManager().getModule(Hud.class);
        //If the current track does not equal the track that is playing on spotify then it sets the variable to the current track
        if (currentTrack != spotifyAPI.currentTrack || currentPlayingContext != spotifyAPI.currentPlayingContext) {
            this.currentTrack = spotifyAPI.currentTrack;
            this.currentPlayingContext = spotifyAPI.currentPlayingContext;
        }

        // You can make these two customizable.
        final int albumCoverSize = 55;
        final int playerWidth = 150;

        final int diff = currentTrack.getDurationMs();
        final long diffSeconds = TimeUnit.MILLISECONDS.toSeconds(diff) % 60;
        final long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60;
        final String trackRemaining = String.format("%s:%s", diffMinutes < 10 ? "0" + diffMinutes : diffMinutes, diffSeconds < 10 ? "0" + diffSeconds : diffSeconds);

        final int trackProgress = currentPlayingContext.getProgress_ms();
        final long tpSeconds = TimeUnit.MILLISECONDS.toSeconds(trackProgress) % 60;
        final long tpMinutes = TimeUnit.MILLISECONDS.toMinutes(trackProgress) % 60;
        final String trackProgressRender = String.format("%s:%s", tpMinutes < 10 ? "0" + tpMinutes : tpMinutes, tpSeconds < 10 ? "0" + tpSeconds : tpSeconds);

        final String totalTrackTime = String.format("%s/%s", trackProgressRender, trackRemaining);

        try {
            /*For every artist, append them to a string builder to make them into a single string
            They are separated by commas unless there is only one Or if its the last one, then its a dot.*/
            final StringBuilder artistsDisplay = new StringBuilder();
            for (int artistIndex = 0; artistIndex < currentTrack.getArtists().length; artistIndex++) {
                final ArtistSimplified artist = currentTrack.getArtists()[artistIndex];
                artistsDisplay.append(artist.getName()).append(artistIndex + 1 == currentTrack.getArtists().length ? '.' : ", ");
            }

            final float progressBarWidth = (float) ((spotifyHudDraggable.getX() + spotifyHudDraggable.getWidth() - (spotifyHudDraggable.getX() + albumCoverSize + 2) - 2) * currentPlayingContext.getProgress_ms()) / currentTrack.getDurationMs();
            float posX = spotifyHudDraggable.getX();
            float posY = spotifyHudDraggable.getY();

            if(playerWidth + normalFont18.getStringWidth(artistsDisplay.toString()) > playerWidth + normalFont22.getStringWidth(currentTrack.getName()))
                spotifyHudDraggable.setWidth(albumCoverSize + normalFont18.getStringWidth(artistsDisplay.toString()) + 4);
            else
                spotifyHudDraggable.setWidth(albumCoverSize + normalFont22.getStringWidth(currentTrack.getName()) + 4);

//            spotifyHudDraggable.setWidth((posX + normalFont18.getStringWidth(artistsDisplay.toString()) > posX + normalFont22.getStringWidth(currentTrack.getName())) ? playerWidth - normalFont18.getStringWidth(artistsDisplay.toString()) : posX + normalFont22.getStringWidth(currentTrack.getName() > (playerWidth - albumCoverSize)
//                    ? albumCoverSize + normalFont22.getStringWidth(currentTrack.getName()) + 4 : (playerWidth - albumCoverSize));
            spotifyHudDraggable.setHeight(albumCoverSize);
            // The rect methods that have WH at the end means they use width & height instead of x2 and y2

            RoundedUtil.drawRound(posX, posY, spotifyHudDraggable.getWidth(), spotifyHudDraggable.getHeight(), 4, new Color(0, 0, 0, 150));
            normalFont22.drawStringWithShadow(currentTrack.getName(), posX + albumCoverSize + 2, posY + normalFont22.getHeight() - 3, -1);

            normalFont18.drawStringWithShadow(artistsDisplay.toString(), posX + albumCoverSize + 2, posY + normalFont22.getHeight() + normalFont18.getHeight() + 2, Color.lightGray);

            RoundedUtil.drawRound(posX + albumCoverSize + 2, posY + normalFont22.getHeight() + normalFont18.getHeight() + 9,
                    posX + spotifyHudDraggable.getWidth() - (posX + albumCoverSize + 2) - 2, 4, 2, new Color(50, 50, 50));
            RoundedUtil.drawRound(posX + albumCoverSize + 2, posY + normalFont22.getHeight() + normalFont18.getHeight() + 9, progressBarWidth, 4, 2, hud.getHudColor((float) System.currentTimeMillis() / -600));
            normalFont18.drawStringWithShadow(totalTrackTime, posX + albumCoverSize + 2, posY + normalFont22.getHeight() + normalFont18.getHeight() + 17, -1);
//            //Gradient Rect behind the text
//            GradientUtil.drawGradientLR(posX + albumCoverSize, posY, playerWidth, albumCoverSize, 1, imageColor, new Color(20, 20, 20));
//
//            //We scissor the text to be inside the box
//            RenderUtil.scissor(posX + albumCoverSize, posY, playerWidth, albumCoverSize);
//            GL11.glEnable(GL11.GL_SCISSOR_TEST);
//
//            // Display the current track name
//            // TODO: make the text of the current track and artist scroll back and forth, with a pause at each end.
//            mc.fontRendererObj.drawString("§l" + currentTrack.getName(), posX + albumCoverSize + 4, posY + 6, -1);
//
//            /*For every artist, append them to a string builder to make them into a single string
//            They are separated by commas unless there is only one Or if its the last one, then its a dot.*/
//            final StringBuilder artistsDisplay = new StringBuilder();
//            for (int artistIndex = 0; artistIndex < currentTrack.getArtists().length; artistIndex++) {
//                final ArtistSimplified artist = currentTrack.getArtists()[artistIndex];
//                artistsDisplay.append(artist.getName()).append(artistIndex + 1 == currentTrack.getArtists().length ? '.' : ", ");
//            }
//
//            mc.fontRendererObj.drawString(artistsDisplay.toString(), posX + albumCoverSize + 4, posY + 17, -1);
//            GL11.glDisable(GL11.GL_SCISSOR_TEST);
//
//            // Draw how much time until the track ends
//            mc.fontRendererObj.drawString(trackRemaining, posX + playerWidth + 8, posY + albumCoverSize - 11, -1);
//
//            //This is where we draw the progress bar
//            final int progressBarWidth = ((playerWidth - albumCoverSize) * currentPlayingContext.getProgress_ms()) / currentTrack.getDurationMs();
//            Gui.drawRect2(posX + albumCoverSize + 5, posY + albumCoverSize - 9, playerWidth - albumCoverSize, 4, new Color(50, 50, 50).getRGB());
//            Gui.drawRect2(posX + albumCoverSize + 5, posY + albumCoverSize - 9, progressBarWidth, 4, new Color(20, 200, 10).getRGB());

            if (currentAlbumCover != null && downloadedCover) {
                StencilUtil.initStencilToWrite();
                RoundedUtil.drawRound(posX, posY, albumCoverSize, spotifyHudDraggable.getHeight(), 4, Color.white);
                StencilUtil.readStencilBuffer(1);
                mc.getTextureManager().bindTexture(currentAlbumCover);
                GlStateManager.color(1,1,1);
                Gui.drawModalRectWithCustomSizedTexture(posX, posY, 0, 0, albumCoverSize, albumCoverSize, albumCoverSize, albumCoverSize);
                StencilUtil.uninitStencilBuffer();
            }
            if ((currentAlbumCover == null || !currentAlbumCover.getResourcePath().contains(currentTrack.getAlbum().getId()))) {
                downloadedCover = false;
                final ThreadDownloadImageData albumCover = new ThreadDownloadImageData(null, currentTrack.getAlbum().getImages()[1].getUrl(), null, new IImageBuffer() {
                    @Override
                    public BufferedImage parseUserSkin(BufferedImage image) {
                        imageColor = ColorUtil.averageColor(image, image.getWidth(), image.getHeight(), 1);
                        downloadedCover = true;
                        return image;
                    }

                    @Override
                    public void skinAvailable() {
                    }
                });
                GlStateManager.color(1, 1, 1);
                mc.getTextureManager().loadTexture(currentAlbumCover = new ResourceLocation("spotifyAlbums/" + currentTrack.getAlbum().getId()), albumCover);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    @Override
    public void onEnable() {
        if(mc.thePlayer == null){
            toggle();
            return;
        }
        spotifyAPI.init();
        super.onEnable();
    }
}
