/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.hud;

import java.awt.Color;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.Minecraft;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.HUDModule;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.font.FontUtil;
import wtf.monsoon.api.util.font.impl.FontRenderer;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.DrawUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;

public class SessionInfo
extends HUDModule {
    private Setting<SessionInfoTheme> theme = new Setting<SessionInfoTheme>("Theme", SessionInfoTheme.NEW).describedBy("Them of the SessionInfo");
    public int kills = 0;
    public int deaths = 0;
    private boolean hasRecordedDeath;

    public SessionInfo() {
        super("Session Info", "Shows information about your current session", 4.0f, 24.0f);
    }

    @Override
    public void render() {
        switch (this.theme.getValue()) {
            case NEW: {
                this.renderNewSessionInfo("render");
                break;
            }
            case OLD: {
                this.renderOldSessionInfo("render");
            }
        }
    }

    @Override
    public void blur() {
        switch (this.theme.getValue()) {
            case NEW: {
                this.renderNewSessionInfo("blur");
                break;
            }
            case OLD: {
                this.renderOldSessionInfo("blur");
            }
        }
    }

    private void renderOldSessionInfo(String stage) {
        switch (stage) {
            case "render": {
                long currentTime = System.currentTimeMillis() - Wrapper.getSessionTime();
                long hours = TimeUnit.MILLISECONDS.toHours(currentTime);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(currentTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(currentTime));
                long seconds = TimeUnit.MILLISECONDS.toSeconds(currentTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentTime));
                StringBuilder stringBuilder = new StringBuilder();
                if (hours > 0L) {
                    stringBuilder.append(hours).append("h ");
                }
                if (minutes > 0L) {
                    stringBuilder.append(minutes).append("m ");
                }
                if (seconds > 0L) {
                    stringBuilder.append(seconds).append("s");
                }
                String time = stringBuilder.toString();
                FontRenderer fr = Wrapper.getFontUtil().productSansSmall;
                HashMap<String, String> elements = new HashMap<String, String>();
                elements.put("Welcome, " + Wrapper.getMonsoonAccount().getUsername() + "!", "");
                elements.put("Username", this.mc.thePlayer.getGameProfile().getName());
                elements.put("Kills", this.kills + "");
                elements.put("Deaths", this.deaths + "");
                elements.put("KDR", Math.max(1, this.kills) / Math.max(1, this.deaths) + "");
                elements.put("Session Time", time);
                AtomicReference<Float> y = new AtomicReference<Float>(Float.valueOf(this.getY() + 5.5f));
                DrawUtil.drawRect(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), new Color(0, 0, 0, 100).getRGB());
                DrawUtil.drawRect(this.getX() + 2.0f, this.getY() + 2.0f, this.getX() + this.getWidth() - 1.0f, this.getY() + 3.0f, Wrapper.getPallet().getMain().getRGB());
                elements.forEach((key, value) -> {
                    fr.drawString((String)key, this.getX() + 4.0f, ((Float)y.get()).floatValue(), Color.WHITE, false);
                    fr.drawString((String)value, this.getX() + this.getWidth() - (float)fr.getStringWidth((String)value) - 4.0f, ((Float)y.get()).floatValue(), Color.WHITE, false);
                    y.set(Float.valueOf(((Float)y.get()).floatValue() + 10.0f));
                });
                break;
            }
        }
        if (this.mc.thePlayer.getHealth() <= 0.0f || this.mc.thePlayer.isDead) {
            if (!this.hasRecordedDeath) {
                ++this.deaths;
                this.hasRecordedDeath = true;
            }
        } else if (this.mc.thePlayer.ticksExisted < 10) {
            this.hasRecordedDeath = false;
        }
    }

    private void renderNewSessionInfo(String stage) {
        switch (stage) {
            case "render": {
                RenderUtil.getDefaultHudRenderer(this);
                long currentTime = System.currentTimeMillis() - Wrapper.getSessionTime();
                long hours = TimeUnit.MILLISECONDS.toHours(currentTime);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(currentTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(currentTime));
                long seconds = TimeUnit.MILLISECONDS.toSeconds(currentTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentTime));
                StringBuilder stringBuilder = new StringBuilder();
                if (hours > 0L) {
                    stringBuilder.append(hours).append("h ");
                }
                if (minutes > 0L) {
                    stringBuilder.append(minutes).append("m ");
                }
                if (seconds > 0L) {
                    stringBuilder.append(seconds).append("s");
                }
                String time = stringBuilder.toString();
                HashMap<String, String> elements = new HashMap<String, String>();
                elements.put(FontUtil.UNICODES_UI.CLOCK, time);
                elements.put(FontUtil.UNICODES_UI.TAG, Wrapper.getMinecraft().getSession().getUsername());
                elements.put(FontUtil.UNICODES_UI.USER, this.mc.getCurrentServerData() != null ? this.mc.getCurrentServerData().pingToServer + "ms" : "-1ms");
                elements.put(FontUtil.UNICODES_UI.PLUS, this.kills + (this.kills == 1 ? " Kill" : " Kills"));
                AtomicReference<Float> y = new AtomicReference<Float>(Float.valueOf(this.getY() + 3.0f));
                elements.forEach((icon, value) -> {
                    Wrapper.getFontUtil().productSans.drawString((String)value, this.getX() + this.getWidth() - (float)Wrapper.getFontUtil().productSans.getStringWidth((String)value) - 8.0f, ((Float)y.get()).floatValue(), Color.WHITE, false);
                    Wrapper.getFontUtil().entypo18.drawString((String)icon, this.getX() + 8.0f, ((Float)y.get()).floatValue() + 2.0f, Color.WHITE, false);
                    y.set(Float.valueOf(((Float)y.get()).floatValue() + (this.getHeight() - 6.0f) / (float)elements.size()));
                });
                break;
            }
            case "blur": {
                RoundedUtils.glRound(this, 7.0f, ColorUtil.interpolate(Wrapper.getPallet().getBackground(), ColorUtil.TRANSPARENT, 0.2f));
            }
        }
    }

    @Override
    public float getWidth() {
        switch (this.theme.getValue()) {
            case NEW: {
                float userWidth = Wrapper.getFontUtil().productSans.getStringWidth(Minecraft.getMinecraft().getSession().getUsername());
                return userWidth + 40.0f > 100.0f ? userWidth + 40.0f : 80.0f;
            }
        }
        return 116.0f;
    }

    @Override
    public float getHeight() {
        switch (this.theme.getValue()) {
            case NEW: {
                return 60.0f;
            }
        }
        return 67.5f;
    }

    static enum SessionInfoTheme {
        NEW,
        OLD;

    }
}

