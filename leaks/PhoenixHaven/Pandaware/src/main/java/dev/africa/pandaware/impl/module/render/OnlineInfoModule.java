package dev.africa.pandaware.impl.module.render;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.game.MouseEvent;
import dev.africa.pandaware.impl.event.game.ServerJoinEvent;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.event.render.RenderEvent;
import dev.africa.pandaware.impl.font.Fonts;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.impl.ui.UISettings;
import dev.africa.pandaware.impl.ui.clickgui.ClickGUI;
import dev.africa.pandaware.utils.client.MouseUtils;
import dev.africa.pandaware.utils.math.MathUtils;
import dev.africa.pandaware.utils.network.GameUtils;
import dev.africa.pandaware.utils.render.RenderUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.server.S02PacketChat;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@ModuleInfo(name = "Online Info", category = Category.VISUAL)
public class OnlineInfoModule extends Module {
    private final Info info = new Info(0, 0, 0, 0, 0);
    private boolean dragging;
    private int draggingX, draggingY, width, height;

    private final NumberSetting posX = new NumberSetting("Pos X",
            Toolkit.getDefaultToolkit().getScreenSize().width / 2, 0, 0, 1);
    private final NumberSetting posY = new NumberSetting("Pos Y",
            Toolkit.getDefaultToolkit().getScreenSize().height / 2, 0, 0, 1);

    public OnlineInfoModule() {
        this.registerSettings(this.posX, this.posY);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() ->
                this.info.setSeconds(this.info.getSeconds() + 1), 1L, 1L, TimeUnit.SECONDS);
    }

    @EventHandler
    EventCallback<RenderEvent> onRender = event -> {
        if (event.getType() == RenderEvent.Type.RENDER_2D) {
            if (mc.currentScreen instanceof ClickGUI) return;
            this.width = 160;
            this.height = 58;

            if (this.dragging) {
                if (!(mc.currentScreen instanceof GuiChat)) {
                    this.dragging = false;
                } else {
                    this.posX.setValue(this.draggingX + event.getMousePosition().getX());
                    this.posY.setValue(this.draggingY + event.getMousePosition().getY());
                }
            }

            this.posX.setValue(Math.min(this.posX.getValue().doubleValue(), event.getResolution().getScaledWidth() - this.width));
            this.posY.setValue(Math.min(this.posY.getValue().doubleValue(), event.getResolution().getScaledHeight() - this.height));
            this.posX.setValue(Math.max(this.posX.getValue().doubleValue(), 1));
            this.posY.setValue(Math.max(this.posY.getValue().doubleValue(), 0.5));

            GlStateManager.pushAttribAndMatrix();

            HUDModule hud = Client.getInstance().getModuleManager().getByClass(HUDModule.class);
            if (hud.getHudMode().getValue() == HUDModule.HUDMode.ROUNDED) {
                RenderUtils.drawRoundedRect(this.posX.getValue().doubleValue(), this.posY.getValue().doubleValue(),
                        this.width, this.height, 10.5, UISettings.INTERNAL_COLOR);

                RenderUtils.drawRoundedRectOutline(this.posX.getValue().doubleValue(), this.posY.getValue().doubleValue(),
                        this.width, this.height, 10.5, UISettings.CURRENT_COLOR);
            } else {
                RenderUtils.drawVerticalGradientRect(this.posX.getValue().doubleValue(), this.posY.getValue().doubleValue(),
                        this.width, this.height, UISettings.INTERNAL_COLOR, UISettings.INTERNAL_COLOR);

                RenderUtils.drawBorderedRect(this.posX.getValue().doubleValue(), this.posY.getValue().doubleValue(),
                        this.posX.getValue().doubleValue() + this.width, this.posY.getValue().doubleValue() + this.height,
                        1, new Color(0, 0, 0, 0).getRGB(), UISettings.CURRENT_COLOR.getRGB());
            }
            Fonts.getInstance().getArialBdBig().drawCenteredStringWithShadow("Online info",
                    (float) (this.posX.getValue().doubleValue() + (this.width / 2f)),
                    (float) (this.posY.getValue().doubleValue() + 2), -1
            );

            String text = "K / D: §7" + MathUtils.roundToDecimal(this.info.getKd(), 2);
            Fonts.getInstance().getArialBdNormal().drawStringWithShadow(text,
                    this.posX.getValue().doubleValue() + this.width -
                            (Fonts.getInstance().getArialBdNormal().getStringWidth(text) + 4),
                    this.posY.getValue().doubleValue() + 24,
                    -1
            );

            text = "Wins: §7" + this.info.getWins();
            Fonts.getInstance().getArialBdNormal().drawStringWithShadow(text,
                    this.posX.getValue().doubleValue() + this.width -
                            (Fonts.getInstance().getArialBdNormal().getStringWidth(text) + 4),
                    this.posY.getValue().doubleValue() + 34,
                    -1
            );

            text = "Kills: §7" + Math.round(this.info.getKills());
            Fonts.getInstance().getArialBdNormal().drawStringWithShadow(text,
                    this.posX.getValue().doubleValue() + this.width -
                            (Fonts.getInstance().getArialBdNormal().getStringWidth(text) + 4),
                    this.posY.getValue().doubleValue() + 44,
                    -1
            );

            text = "Playtime: §7" + MathUtils.formatSeconds(this.info.getSeconds());
            Fonts.getInstance().getArialBdNormal().drawStringWithShadow(text,
                    this.posX.getValue().doubleValue() + 4,
                    this.posY.getValue().doubleValue() + 24,
                    -1
            );

            Date date = Calendar.getInstance().getTime();

            text = "Time: §7" + new SimpleDateFormat("HH:mm").format(date);
            Fonts.getInstance().getArialBdNormal().drawStringWithShadow(text,
                    this.posX.getValue().doubleValue() + 4,
                    this.posY.getValue().doubleValue() + 34,
                    -1
            );

            text = "Date: §7" + new SimpleDateFormat("MM/dd/yy").format(date);
            Fonts.getInstance().getArialBdNormal().drawStringWithShadow(text,
                    this.posX.getValue().doubleValue() + 4,
                    this.posY.getValue().doubleValue() + 44,
                    -1
            );

            GlStateManager.popAttribAndMatrix();
        }
    };

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (event.getPacket() instanceof S02PacketChat && mc.theWorld != null && mc.thePlayer != null) {
            S02PacketChat packet = event.getPacket();
            String message = packet.getChatComponent().getUnformattedText();

            if (message.toLowerCase().contains(mc.thePlayer.getName().toLowerCase())) {
                for (String announcement : GameUtils.KILL_ANNOUNCEMENTS) {
                    if (message.toLowerCase().contains(announcement.toLowerCase()
                            .replace("%player%", mc.thePlayer.getName().toLowerCase()))) {
                        this.info.setKills(this.info.getKills() + 1);
                        if (this.info.getDeaths() > 0) {
                            this.info.setKd(this.info.getKills() / this.info.getDeaths());
                        } else {
                            this.info.setKd(this.info.getKills());
                        }
                        break;
                    }
                }
            }

            if (message.toLowerCase().contains("you won! want to play again?") ||
                    message.toLowerCase().contains("ha ganado " + mc.thePlayer.getName().toLowerCase())) {
                this.info.setWins(this.info.getWins() + 1);
            } else if (message.toLowerCase().contains("you died! want to play again? click here!") ||
                    message.toLowerCase().contains(mc.thePlayer.getName().toLowerCase() + " ha muerto")) {
                this.info.setDeaths(this.info.getDeaths() + 1);

                if (this.info.getDeaths() > 0) {
                    this.info.setKd(this.info.getKills() / this.info.getDeaths());
                }
            }
        }
    };

    @EventHandler
    EventCallback<ServerJoinEvent> onServerJoin = event -> this.info.reset();

    @EventHandler
    EventCallback<MouseEvent> onMouse = event -> {
        if (event.getType() == MouseEvent.Type.CLICK && event.getMouseButton() == 0) {
            if (MouseUtils.isMouseInBounds(event.getMouseX(), event.getMouseY(),
                    this.posX.getValue().intValue(), this.posY.getValue().intValue(),
                    this.posX.getValue().intValue() + this.width,
                    this.posY.getValue().intValue() + this.height)) {
                this.dragging = true;
                this.draggingX = this.posX.getValue().intValue() - event.getMouseX();
                this.draggingY = this.posY.getValue().intValue() - event.getMouseY();
            }
        }

        if (event.getType() == MouseEvent.Type.RELEASED) {
            this.dragging = false;
        }
    };

    @Setter
    @Getter
    @AllArgsConstructor
    private static class Info {
        private long seconds;
        private float kills;
        private float deaths;
        private float kd;
        private int wins;

        public void reset() {
            this.seconds = 0;
            this.kills = 0;
            this.deaths = 0;
            this.kd = 0;
            this.wins = 0;
        }
    }
}