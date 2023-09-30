package dev.tenacity.module.impl.misc;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.game.world.WorldEvent;
import dev.tenacity.event.impl.network.message.ChatReceivedEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.event.impl.render.Render2DEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.module.settings.impl.StringSetting;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.EaseInOutQuad;
import dev.tenacity.utils.player.ChatUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;

public class AutoLogin extends Module {

    private final NumberSetting delay = new NumberSetting("Delay", 2000, 5000, 0, 100);
    private final StringSetting password = new StringSetting("Password", "dog123");
    private final Animation animation = new EaseInOutQuad(500, 1);
    private final String[] PASSWORD_PLACEHOLDERS = {"password", "pass", "contrasena", "contraseña"};
    private long runAt, startAt;
    private String runCommand;
    private HUDMod hudMod;

    public AutoLogin() {
        super("AutoLogin", Category.MISC, "Auto login/register on cracked servers");
        this.addSettings(delay, password);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        if (this.runAt < System.currentTimeMillis() && this.runCommand != null) {
            animation.setDirection(Direction.BACKWARDS);
            ChatUtil.send(runCommand);
            reset();
        }
    }

    @Override
    public void onChatReceivedEvent(ChatReceivedEvent event) {
        String msg = event.message.getUnformattedText();
        String password = this.password.getString();
        int passCount = count(msg);
        if (passCount > 0) {
            if (msg.contains("/register ")) {
                setRun("/register " + StringUtils.repeat(password + " ", passCount));
            } else if (msg.contains("/login ")) {
                setRun("/login " + StringUtils.repeat(password + " ", passCount));
            }
        }
    }

    @Override
    public void onRender2DEvent(Render2DEvent event) {
        if ((this.runAt > System.currentTimeMillis() && this.runCommand != null) || !animation.isDone()) {
            if (hudMod == null) {
                hudMod = Tenacity.INSTANCE.getModuleCollection().getModule(HUDMod.class);
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
    }

    @Override
    public void onWorldEvent(WorldEvent event) {
        reset();
    }

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
