package dev.echo.module.impl.misc;

import dev.echo.Echo;
import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.game.WorldEvent;
import dev.echo.listener.event.impl.player.ChatReceivedEvent;
import dev.echo.listener.event.impl.player.MotionEvent;
import dev.echo.listener.event.impl.render.Render2DEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.impl.render.HUDMod;
import dev.echo.module.settings.impl.NumberSetting;
import dev.echo.module.settings.impl.StringSetting;
import dev.echo.utils.animations.Animation;
import dev.echo.utils.animations.Direction;
import dev.echo.utils.animations.impl.EaseInOutQuad;
import dev.echo.utils.player.ChatUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;

public class AutoAuthenticate extends Module {

    private final NumberSetting delay = new NumberSetting("Delay", 2000, 5000, 0, 100);
    private final StringSetting password = new StringSetting("Password", "dog123");
    private final Animation animation = new EaseInOutQuad(500, 1);
    private final String[] PASSWORD_PLACEHOLDERS = {"password", "pass", "contrasena", "contraseña"};
    private long runAt, startAt;
    private String runCommand;
    private HUDMod hudMod;

    public AutoAuthenticate() {
        super("Auto Authenticate", Category.MISC, "Auto login/register on cracked servers");
        this.addSettings(delay, password);
    }

    @Link
    public Listener<MotionEvent> motionEventListener = e -> {
        if (this.runAt < System.currentTimeMillis() && this.runCommand != null) {
            animation.setDirection(Direction.BACKWARDS);
            ChatUtil.send(runCommand);
            reset();
        }
    };

    @Link
    public Listener<ChatReceivedEvent> onChatReceivedEvent = e -> {
        String msg = e.message.getUnformattedText();
        String password = this.password.getString();
        int passCount = count(msg);
        if (passCount > 0) {
            if (msg.contains("/register ")) {
                setRun("/register " + StringUtils.repeat(password + " ", passCount));
            } else if (msg.contains("/login ")) {
                setRun("/login " + StringUtils.repeat(password + " ", passCount));
            }
        }
    };

    @Link
    public Listener<Render2DEvent> onRender2D = e -> {
        if ((this.runAt > System.currentTimeMillis() && this.runCommand != null) || !animation.isDone()) {
            if (hudMod == null) {
                hudMod = Echo.INSTANCE.getModuleCollection().getModule(HUDMod.class);
            }
            ScaledResolution sr = new ScaledResolution(mc);
            float width = 120, height = 5, width2 = width / 2.0F;
            float calc = runAt == 0 ? 1 : (float) (System.currentTimeMillis() - startAt) / (float) (runAt - startAt),
                    scale = (float) animation.getOutput().floatValue(),
                    left = (sr.getScaledWidth() / 2.0F) / scale - width2,
                    top = sr.getScaledHeight() / 2.0F + 30,
                    bottom = (sr.getScaledHeight() / 2.0F + 30) / scale + height,
                    sw2 = sr.getScaledWidth() / 2.0F;
            top /= scale;
            sw2 /= scale;

            GlStateManager.pushMatrix();
            GlStateManager.scale(scale, scale, scale);
            Color color = HUDMod.getClientColors().getFirst();
            Gui.drawRect(left, top, sw2 + width2, bottom, color.darker().darker().getRGB());
            Gui.drawRect(left, top, sw2 - width2 + (width * calc), bottom, color.getRGB());
            GlStateManager.popMatrix();
        }
    };

    @Link
    public Listener<WorldEvent> onWorldEvent = e -> {
        reset();
    };

    @Override
    public void onEnable() {
        reset();
        super.onEnable();
    }

    private int count(String data) {
        int count = 0;
        data = data.toLowerCase();
        for (String pass : PASSWORD_PLACEHOLDERS) {
            count += StringUtils.countMatches(data, pass);
        }
        return count;
    }

    private void setRun(String runCommand) {
        long currentTimeMillis = System.currentTimeMillis();
        this.animation.setDirection(Direction.FORWARDS);
        this.startAt = currentTimeMillis;
        this.runAt = currentTimeMillis + delay.getValue().longValue();
        this.runCommand = runCommand.trim();
    }

    private void reset() {
        this.animation.setDirection(Direction.BACKWARDS);
        this.startAt = this.runAt = 0;
        this.runCommand = null;
    }

}
