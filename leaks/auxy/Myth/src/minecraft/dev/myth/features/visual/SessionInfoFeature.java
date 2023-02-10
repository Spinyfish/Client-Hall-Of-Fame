/**
 * @project mythrecode
 * @prod HardcodeProductions
 * @author Auxy
 * @at 04.09.22, 20:48
 */
package dev.myth.features.visual;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.draggable.DraggableComponent;
import dev.myth.api.feature.Feature;
import dev.myth.api.utils.font.FontLoaders;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.api.utils.render.shader.list.DropShadowUtil;
import dev.myth.events.PacketEvent;
import dev.myth.events.Render2DEvent;
import dev.myth.events.TickEvent;
import dev.myth.events.UpdateEvent;
import dev.myth.features.display.HUDFeature;
import dev.myth.main.ClientMain;
import dev.myth.managers.DraggableManager;
import dev.myth.managers.FeatureManager;
import dev.myth.managers.ShaderManager;
import dev.myth.settings.ColorSetting;
import dev.myth.settings.EnumSetting;
import dev.myth.settings.NumberSetting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S45PacketTitle;

import java.awt.*;

@Feature.Info(name = "SessionInfo", description = "Session Info lOl", category = Feature.Category.VISUAL)
public class SessionInfoFeature extends Feature {

    public final ScaledResolution sr = new ScaledResolution(MC);
    public final EnumSetting<Type> mode = new EnumSetting<>("Type", Type.MYTH);
    public final NumberSetting xPos = new NumberSetting("X", 20, 0, 1000, 10);
    public final NumberSetting yPos = new NumberSetting("Y", 60, 0, 1000, 10);
    public final ColorSetting color1 = new ColorSetting("Color 1", Color.CYAN);
    public final ColorSetting color2 = new ColorSetting("Color 2", Color.GREEN.darker()).addDependency(() -> mode.is(Type.NOVOLINE) || mode.is(Type.MYTH));

    public int kills = 0, deaths = 0, gamesPlayed = 0, wins = 0;
    public int playTimeSeconds = 0, playTimeMinutes = 0, playTimeHours = 0;
    public double lastTime;

    private DraggableComponent component;

    @Override
    public void onEnable() {
        super.onEnable();

        ScaledResolution sr = new ScaledResolution(MC);

        if(component == null) {
            component = new DraggableComponent("Session Info", xPos.getValue(), yPos.getValue(), 150, 67, sr);
        }
        ClientMain.INSTANCE.manager.getManager(DraggableManager.class).registerDraggable(component);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        ClientMain.INSTANCE.manager.getManager(DraggableManager.class).unregisterDraggable(component);
    }

    @Handler
    public final Listener<Render2DEvent> render2DEventListener = event -> {
        ScaledResolution sr = event.getScaledResolution();
        final double x = component.getX(sr), y = component.getY(sr);

        final BlurFeature blurFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(BlurFeature.class);
        final GlowFeature glowFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(GlowFeature.class);
        final ShadowFeature shadowFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(ShadowFeature.class);

        final int radius = shadowFeature.isEnabled() && shadowFeature.modules.isEnabled("Session Info") ? 13 : glowFeature.glowRadius.getValue().intValue();
        final Color glowColor = shadowFeature.isEnabled() && shadowFeature.modules.isEnabled("Session Info") ? Color.BLACK : glowFeature.glowColor.getValue();

        switch (mode.getValue()) {
            case NOVOLINE:

                component.setWidth(150);
                component.setHeight(67);

                GlStateManager.disableBlend();
                if (shadowFeature.isEnabled() && shadowFeature.modules.isEnabled("Session Info") || glowFeature.isEnabled() && glowFeature.modules.isEnabled("Session Info")) {
                    DropShadowUtil.start();
                    RenderUtil.drawRect(x, y, 150,67,  new Color(0, 0, 0, 140).getRGB());
                    DropShadowUtil.stop(radius, glowColor);
                }

                if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("Session Info"))
                    ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.startBlur();

                RenderUtil.drawRect(x, y, 150,67, new Color(0, 0, 0, 140).getRGB());

                if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("Session Info"))
                    ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.stopBlur(blurFeature.blurSigma.getValue().intValue(), blurFeature.blurRadius.getValue().intValue(), 1);
                FontLoaders.SFUI_12.drawString(" ", 1, 0, -1); // Glow Bug Fix
                FontLoaders.SFUI_22.drawString("Session Info", (float) (x + 45), (float) (y + 7), -1);
                Gui.drawGradientRectSideways(x + 4, y + 20, x + 146, y + 22, color1.getValue().getRGB(), color2.getValue().getRGB());

                FontLoaders.SFUI_20.drawString("Play Time:", (float) (x + 4), (float) (y + 28), -1);
                FontLoaders.SFUI_20.drawString(playTimeHours + "h, " + playTimeMinutes + "m, " + playTimeSeconds + "s", (float) (x + 148 - FontLoaders.SFUI_20.getStringWidth(playTimeHours + "h, " + playTimeMinutes + "m, " + playTimeSeconds + "s")), (float) (y + 28), -1);

                FontLoaders.SFUI_20.drawString("Played Games:", (float) (x + 4), (float) (y + 40), -1);
                FontLoaders.SFUI_20.drawString(String.valueOf(gamesPlayed), (float) (x + 148 - FontLoaders.SFUI_20.getStringWidth(String.valueOf(gamesPlayed))), (float) (y + 40), -1);

                FontLoaders.SFUI_20.drawString("Players Killed:", (float) (x + 4), (float) (y + 52), -1);
                FontLoaders.SFUI_20.drawString(String.valueOf(kills), (float) (x + 148 - FontLoaders.SFUI_20.getStringWidth(String.valueOf(kills))), (float) (y + 52), -1);
                break;
            case MYTH:

                component.setWidth(150);
                component.setHeight(67);

                GlStateManager.disableBlend();
                if (shadowFeature.isEnabled() && shadowFeature.modules.isEnabled("Session Info") || glowFeature.isEnabled() && glowFeature.modules.isEnabled("Session Info")) {
                    DropShadowUtil.start();
                    RenderUtil.drawRect(x, y, 150,67,  new Color(0, 0, 0, 140).getRGB());
                    DropShadowUtil.stop(radius, glowColor);
                }

                if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("Session Info"))
                    ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.startBlur();

                RenderUtil.drawRect(x, y, 150,67, new Color(0, 0, 0, 140).getRGB());

                if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("Session Info"))
                    ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.stopBlur(blurFeature.blurSigma.getValue().intValue(), blurFeature.blurRadius.getValue().intValue(), 1);
                Gui.drawGradientRectSideways(x, y, x + 150,y + 1.5, color1.getColor(), color2.getColor());
                FontLoaders.SFUI_12.drawString(" ", 1, 0, -1); // Glow Bug Fix
                FontLoaders.BOLD_22.drawString("Session Information", (float) (x + 20), (float) (y + 7), -1);

                FontLoaders.SFUI_20.drawString("Session Time:", (float) (x + 4), (float) (y + 28), -1);
                FontLoaders.SFUI_20.drawString(playTimeHours + "h, " + playTimeMinutes + "m, " + playTimeSeconds + "s", (float) (x + 148 - FontLoaders.SFUI_20.getStringWidth(playTimeHours + "h, " + playTimeMinutes + "m, " + playTimeSeconds + "s")), (float) (y + 28), -1);

                FontLoaders.SFUI_20.drawString("Games Played:", (float) (x + 4), (float) (y + 40), -1);
                FontLoaders.SFUI_20.drawString(String.valueOf(gamesPlayed), (float) (x + 148 - FontLoaders.SFUI_20.getStringWidth(String.valueOf(gamesPlayed))), (float) (y + 40), -1);

                FontLoaders.SFUI_20.drawString("Kills:", (float) (x + 4), (float) (y + 52), -1);
                FontLoaders.SFUI_20.drawString(String.valueOf(kills), (float) (x + 148 - FontLoaders.SFUI_20.getStringWidth(String.valueOf(kills))), (float) (y + 52), -1);
                break;

            case SIMPLE:
                final HUDFeature hudFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(HUDFeature.class);

                String playTime = "Playtime: " + playTimeHours + "h, " + playTimeMinutes + "m, " + playTimeSeconds + "s";

                component.setWidth(hudFeature.getStringWidth(playTime));
                component.setHeight(hudFeature.getFontHeight());

                hudFeature.drawString(playTime, (float) x, (float) y, -1);
                break;

        }

    };

    @Handler
    public final Listener<PacketEvent> packetEventListener = event -> {
        if (getPlayer() == null)
            return;
        if (event.getPacket() instanceof S02PacketChat) {
            S02PacketChat s02 = event.getPacket();
            String xd = s02.getChatComponent().getUnformattedText();
            if (xd.contains(" was killed by " + MC.thePlayer.getName()))
                this.kills++;
            if (xd.contains("Sending you to"))
                this.gamesPlayed++;
            if (!MC.thePlayer.isEntityAlive())
                this.deaths++;
            if (xd.contains("Winner - " + MC.thePlayer.getName()))
                this.wins++;
        }

        if (event.getPacket() instanceof S45PacketTitle) {
            S45PacketTitle packet = event.getPacket();
            if (packet.getMessage() == null) {
                return;
            }
            if (packet.getMessage().getUnformattedText().equals("YOU DIED!") || packet.getMessage().getUnformattedText().equals("You are now a spectator!")) {
                deaths++;
            }
            if (packet.getMessage().getUnformattedText().equals("GAME END") || packet.getMessage().getUnformattedText().equals("YOU DIED!") || packet.getMessage().getUnformattedText().equals("VICTORY!") || packet.getMessage().getUnformattedText().equals("You are now a spectator!")) {
                gamesPlayed++;
            }
            if (packet.getMessage().getUnformattedText().equals("VICTORY!")) {
                wins++;
            }

        }
    };

    @Handler
    public final Listener<TickEvent> tickEventListener = event -> {
        final boolean shouldRevert = MC.thePlayer == null || MC.theWorld == null || MC.getCurrentServerData() == null || MC.getCurrentServerData().serverIP == null || MC.currentScreen instanceof GuiMultiplayer;

        if (shouldRevert) {
            playTimeSeconds = 0;
            playTimeMinutes = 0;
            playTimeHours = 0;
            kills = 0;
            wins = 0;
            gamesPlayed = 0;
            deaths = 0;
        }
    };

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        if (System.currentTimeMillis() - lastTime > 1000) {
            playTimeSeconds = playTimeSeconds + 1;
            lastTime = System.currentTimeMillis();
        }
        if (playTimeSeconds == 60) {
            playTimeSeconds = 0;
            playTimeMinutes = playTimeMinutes + 1;
        }
        if (playTimeMinutes == 60) {
            playTimeMinutes = 0;
            playTimeHours = 1;
        }
    };

    public enum Type {
        NOVOLINE("Novoline"),
        MYTH("Myth"),
        SIMPLE("Simple");

        private final String name;

        Type(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }


}
