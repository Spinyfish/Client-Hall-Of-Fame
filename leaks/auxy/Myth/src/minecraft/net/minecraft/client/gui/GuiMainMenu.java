package net.minecraft.client.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import com.wrapper.spotify.model_objects.specification.Track;
import dev.myth.api.utils.SpotifyAPI;
import dev.myth.api.utils.font.FontLoaders;
import dev.myth.api.utils.mouse.MouseUtil;
import dev.myth.api.utils.render.ColorUtil;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.api.utils.render.animation.Animation;
import dev.myth.api.utils.render.animation.Easings;
import dev.myth.api.utils.render.shader.ShaderProgram;
import dev.myth.api.utils.render.shader.list.DropShadowUtil;
import dev.myth.main.ClientMain;
import dev.myth.managers.ShaderManager;
import dev.myth.ui.AltLoginScreen;
import dev.myth.ui.BetterGuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;

public class GuiMainMenu extends GuiScreen {

    public boolean showShader;

    public static int bgId = 0;
    public static boolean shaders = true;
    public final Animation iconAnimation = new Animation();
    public final Animation buttonAnimation = new Animation();
    public ArrayList<String> changelog = new ArrayList<>();

    private ShaderProgram[] backgroundShaders = new ShaderProgram[]{
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BACKGROUND_SHADER1,
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BACKGROUND_SHADER2,
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BACKGROUND_SHADER3,
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BACKGROUND_SHADER4
    };

    public ResourceLocation currentAlbumCover;
    public BufferedImage coverImageBuffer;
    private Track downloadedCoverTrack;

    public GuiMainMenu() {
        changelog.add("+ Complete Client Recode");
        changelog.add("+ New Clickguis");
        changelog.add("+ Completly new Bypasses");
    }

    @Override
    public void initGui() {
        super.initGui();
        showShader = false;

        buttonList.clear();

        buttonList.add(new BetterGuiButton(0, width / 2 - 75, height / 2 - 75, 150, 25, "Singleplayer", () -> mc.displayGuiScreen(new GuiSelectWorld(this))));
        buttonList.add(new BetterGuiButton(1, width / 2 - 75, height / 2 - 45, 150, 25, "Multiplayer", () -> mc.displayGuiScreen(new GuiMultiplayer(this))));
        buttonList.add(new BetterGuiButton(2, width / 2 - 75, height / 2 - 15, 150, 25, "Alt Login", () -> mc.displayGuiScreen(new AltLoginScreen(this))));
        buttonList.add(new BetterGuiButton(3, width / 2 - 75, height / 2 + 15, 150, 25, "Settings", () -> mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings))));
        buttonList.add(new BetterGuiButton(4, width / 2 - 75, height / 2 + 45, 150, 25, "Quit", () -> mc.shutdown()));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        backgroundShaders[bgId].doRender();
        ScaledResolution sr = new ScaledResolution(mc);

        double width = sr.getScaledWidth_double(), height = sr.getScaledHeight_double();

        iconAnimation.updateAnimation();
        iconAnimation.animate(MouseUtil.isHovered(mouseX, mouseY, width - 20, height - 30 - 10 * iconAnimation.getValue(), 24, 24) ? 1 : 0, 800, Easings.CIRC_OUT);
        RenderUtil.drawImage(width - 40, height - 30 - 10 * iconAnimation.getValue(), 24, 24, new ResourceLocation("myth/icons/shaderselection.png"), Color.WHITE);
        if (showShader) {
            drawShaderTab(sr, mouseX, mouseY);
        } else {
            super.drawScreen(mouseX, mouseY, partialTicks);
        }
        if (!shaders) {
            RenderUtil.drawRect(width / 8 - 100, height / 5 - 50, 140, 150, new Color(0, 0, 0, 130));
            FontLoaders.NOVO_FONT_22.drawString("Changelog", (float) (width / 8 - 95), (float) (height / 5 - 45), -1);
            int y = 0;
            for (String s : changelog) {
                final Color hardcodedColor = s.startsWith("+") ? Color.GREEN : s.startsWith("#") ? Color.YELLOW.brighter() : s.startsWith("-") ? Color.RED : Color.WHITE;
                RenderUtil.drawRect(width / 8 - 95, height / 5 - 25 + y, 3, 3, new Color(hardcodedColor.getRed(), hardcodedColor.getGreen(), hardcodedColor.getBlue(), 200));
                final String finalLog = s.replace("#", "").replace("+", "").replace("-", "");
                FontLoaders.NOVO_FONT_16.drawString(finalLog, (float) (width / 8 - 88), (float) (height / 5 - 25 + y), -1);
                y = y + 20;
            }

            RenderUtil.drawRect(width / 8 - 100, height / 5 + 130, 140, 100, new Color(0, 0, 0, 130));
            FontLoaders.NOVO_FONT_22.drawString("Info", (float) (width / 8 - 95), (float) (height / 5 + 135), -1);

            FontLoaders.NOVO_FONT_16.drawString("Username" + EnumChatFormatting.WHITE + ": " + ClientMain.INSTANCE.getUsername(), (float) (width / 8 - 95), (float) (height / 5 + 135 + 20), Color.YELLOW.brighter().getRGB());
            FontLoaders.NOVO_FONT_16.drawString("UID" + EnumChatFormatting.WHITE + ": " + ClientMain.INSTANCE.getUid(), (float) (width / 8 - 95), (float) (height / 5 + 135 + 30), Color.RED.brighter().getRGB());
        } else {
            DropShadowUtil.start();
            RenderUtil.drawRect(width / 8 - 100, height / 5 - 50, 140, 150, new Color(0, 0, 0, 130));
            RenderUtil.drawRect(width / 8 - 100, height / 5 + 130, 140, 100, new Color(0, 0, 0, 130));
            RenderUtil.drawRect(width - 180, height / 5 - 50, 140, 70, new Color(0, 0, 0, 130));
            DropShadowUtil.stop(11, Color.BLACK);

            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.startBlur();
            RenderUtil.drawRect(width / 8 - 100, height / 5 - 50, 140, 150, new Color(0, 0, 0, 130));
            RenderUtil.drawRect(width / 8 - 100, height / 5 + 130, 140, 100, new Color(0, 0, 0, 130));
            RenderUtil.drawRect(width - 180, height / 5 - 50, 140, 70, new Color(0, 0, 0, 130));
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.stopBlur();

            FontLoaders.NOVO_FONT_22.drawString("Changelog", (float) (width / 8 - 95), (float) (height / 5 - 45), -1);
            int y = 0;
            for (String s : changelog) {
                final Color hardcodedColor = s.startsWith("+") ? Color.GREEN : s.startsWith("#") ? Color.YELLOW.brighter() : s.startsWith("-") ? Color.RED : Color.WHITE;
                DropShadowUtil.start();
                RenderUtil.drawRect(width / 8 - 95, height / 5 - 25 + y, 3, 3, hardcodedColor);
                DropShadowUtil.stop(8, hardcodedColor);
                RenderUtil.drawRect(width / 8 - 95, height / 5 - 25 + y, 3, 3, new Color(hardcodedColor.getRed(), hardcodedColor.getGreen(), hardcodedColor.getBlue(), 200));

                final String finalLog = s.replace("#", "").replace("+", "").replace("-", "");
                FontLoaders.NOVO_FONT_16.drawString(finalLog, (float) (width / 8 - 88), (float) (height / 5 - 25 + y), -1);
                y = y + 20;
            }

            FontLoaders.NOVO_FONT_22.drawString("Info", (float) (width / 8 - 95), (float) (height / 5 + 135), -1);
            FontLoaders.NOVO_FONT_16.drawString("Username" + EnumChatFormatting.WHITE + ": " + ClientMain.INSTANCE.getUsername(), (float) (width / 8 - 95), (float) (height / 5 + 135 + 20), Color.YELLOW.brighter().getRGB());
            FontLoaders.NOVO_FONT_16.drawString("UID" + EnumChatFormatting.WHITE + ": " + ClientMain.INSTANCE.getUid(), (float) (width / 8 - 95), (float) (height / 5 + 135 + 30), Color.RED.brighter().getRGB());

            if (ClientMain.INSTANCE.spotifyAPI == null) {
                GlStateManager.color(0, 1, 0, 1);
                RenderUtil.drawImage(width - 155, height / 5 - 27, 24, 24, new ResourceLocation("myth/icons/spotify.png"));
                FontLoaders.NOVO_FONT_30.drawString(" ", 0, 0, Color.RED.brighter().getRGB());
                FontLoaders.NOVO_FONT_30.drawString("Connect", (float) (width - 130), (float) (height / 5 - 22), Color.WHITE.darker().darker().getRGB());
            } else {
                Track currentTrack = ClientMain.INSTANCE.spotifyAPI.getCurrentTrack();
                if (currentTrack != null) {
                    if (downloadedCoverTrack == null || !Objects.equals(downloadedCoverTrack.getAlbum().getImages()[0].getUrl(), currentTrack.getAlbum().getImages()[0].getUrl())) {
                        currentAlbumCover = null;
                        downloadedCoverTrack = currentTrack;
                        mc.addScheduledTask(() -> {
                            try {
                                coverImageBuffer = ImageIO.read(new URL(currentTrack.getAlbum().getImages()[0].getUrl()));
                                DynamicTexture dynamicTexture = new DynamicTexture(coverImageBuffer);
                                currentAlbumCover = mc.getTextureManager().getDynamicTextureLocation("cover.jpg", dynamicTexture);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }

                    FontLoaders.NOVO_FONT_30.drawString(" ", 0, 0, Color.RED.brighter().getRGB());
                    FontLoaders.NOVO_FONT_20.drawString(currentTrack.getName(), (float) (width - 175), (float) (height / 5 - 5), -1);
                    RenderUtil.drawImage(width - 78, height / 5 - 45, 32, 32, currentAlbumCover);
                    StringBuilder artists = new StringBuilder();

                    for (int i = 0; i < currentTrack.getArtists().length; i++) {
                        artists.append(currentTrack.getArtists()[i].getName());
                        if (i < currentTrack.getArtists().length - 2) artists.append(", ");
                        else if (i < currentTrack.getArtists().length - 1) artists.append(" and ");
                    }
                    FontLoaders.NOVO_FONT_18.drawString("by " + artists, (float) (width - 175), (float) (height / 5 + 5), Color.WHITE.darker().darker().getRGB());
                }
            }
        }
        GlStateManager.color(1, 1, 1);
    }

    public void drawShaderTab(final ScaledResolution sr, final float mouseX, final float mouseY) {
        final float x = sr.getScaledWidth() / 2 - 125;
        final float y = sr.getScaledHeight() / 2 - 125;
        RenderUtil.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), new Color(0, 0, 0, 130));
        GlStateManager.pushMatrix();
        RenderUtil.drawRoundedRect(x, y, 250, 250, 5, new Color(16, 16, 16).getRGB(), 0, 0);
        FontLoaders.SFUI_22.drawString("Shader Selection", x + 80, y + 5, -1);
        RenderUtil.drawRoundedRect(x + 10, y + 100, 230, 20, 3, MouseUtil.isHovered(mouseX, mouseY, sr.getScaledWidth() / 2 - 125 + 10, sr.getScaledHeight() / 2 - 125 + 100, 230, 20) ? new Color(25, 25, 25).getRGB() : new Color(21, 21, 21).getRGB(), 0, 0);
        FontLoaders.SFUI_20.drawString("Change Shader", x + 86, y + 107, -1);
        RenderUtil.drawRoundedRect(x + 10, y + 130, 230, 20, 3, new Color(21, 21, 21).getRGB(), 0, 0);
        FontLoaders.SFUI_20.drawString("Load Custom Shader", x + 80, y + 137, -1);
        buttonAnimation.updateAnimation();
        buttonAnimation.animate(MouseUtil.isHovered(mouseX, mouseY, x + 10, y + 220 - 10 * buttonAnimation.getValue(), 230, 20) ? 1 : 0, 800, Easings.CIRC_OUT);
        RenderUtil.drawRect(x + 10, y + 220 - 10 * buttonAnimation.getValue(), 230, 20, new Color(42, 82, 190).getRGB());
        FontLoaders.SFUI_20.drawString("Apply / Close", x + 95, (float) (y + 226 - 10 * buttonAnimation.getValue()), -1);
        GlStateManager.popMatrix();
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution sr = new ScaledResolution(mc);

        if (mouseButton == 0) {
            if (showShader) {
                if (MouseUtil.isHovered(mouseX, mouseY, sr.getScaledWidth() / 2 - 125 + 10, sr.getScaledHeight() / 2 - 125 + 220 - 10 * buttonAnimation.getValue(), 230, 20)) {
                    showShader = false;
                }
            } else {
                if (MouseUtil.isHovered(mouseX, mouseY, sr.getScaledWidth() - 40, sr.getScaledHeight() - 30 - 10 * iconAnimation.getValue(), 24, 24)) {
                    showShader = true;
                }
            }
        }

        if (MouseUtil.isHovered(mouseX, mouseY, sr.getScaledWidth() - 180, sr.getScaledHeight() / 5 - 50, 140, 70) && mouseButton == 0) {
            if (ClientMain.INSTANCE.spotifyAPI == null || !ClientMain.INSTANCE.spotifyAPI.isAuthenticated()) {
                ClientMain.INSTANCE.spotifyAPI = new SpotifyAPI();
            } else {
                ClientMain.INSTANCE.spotifyAPI.skip();
            }
        }

        if (MouseUtil.isHovered(mouseX, mouseY, sr.getScaledWidth() / 2 - 125 + 10, sr.getScaledHeight() / 2 - 125 + 100, 230, 20) && mouseButton == 0 && showShader) {
            bgId = (bgId + 1) % 4;
        }

        if (!showShader) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

}
