/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 *  org.lwjgl.input.Mouse
 *  org.lwjgl.opengl.GL11
 */
package wtf.monsoon.impl.ui.menu.windows;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Session;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.manager.alt.Alt;
import wtf.monsoon.api.util.font.FontUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.menu.MainMenu;
import wtf.monsoon.impl.ui.menu.windows.AltAddWindow;
import wtf.monsoon.impl.ui.menu.windows.Window;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.misc.server.packet.impl.MPacketUpdateUsername;

public class AltWindow
extends Window {
    private MainMenu menuInstance;
    private final Button add = new Button("Add Alt", this.getX(), this.getY(), this.getWidth() - 4.0f, 16.0f, () -> {
        if (this.menuInstance.getWindows().stream().anyMatch(window -> window instanceof AltAddWindow)) {
            this.menuInstance.getWindows().removeIf(window -> window instanceof AltAddWindow);
        } else {
            this.menuInstance.getWindows().add(new AltAddWindow(this.menuInstance, this.getX(), this.getY() + this.getHeight() + 5.0f, this.getWidth(), 88.0f, this.getHeader()));
        }
    });
    private final List<AltButton> altButtons = new ArrayList<AltButton>();
    private float scroll = 0.0f;
    private static String status;

    public AltWindow(MainMenu mainMenuInstance, float x, float y, float width, float height, float header) {
        super(x, y, width, height, header);
        this.menuInstance = mainMenuInstance;
    }

    @Override
    public void render(float mouseX, float mouseY) {
        super.render(mouseX, mouseY);
        if (AltWindow.getStatus() == null) {
            AltWindow.setStatus("Logged in as " + Minecraft.getMinecraft().session.getUsername());
        }
        Wrapper.getFont().drawString("Alt Manager", this.getX() + 4.0f, this.getY() + 1.0f, Color.WHITE, false);
        Wrapper.getFont().drawString(AltWindow.getStatus(), this.getX() + this.getWidth() - (float)Wrapper.getFont().getStringWidth(AltWindow.getStatus()) - 14.0f - 4.0f, this.getY() + 1.0f, Color.WHITE, false);
        this.refreshAlts();
        float altHeight = this.altButtons.size() * 18;
        int mouseDelta = Mouse.getDWheel();
        if (mouseDelta != 0 && mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() + this.getHeader() && mouseY <= this.getY() + this.getHeight() - 18.0f) {
            this.scroll += (float)mouseDelta * 0.05f;
        }
        this.scroll = MathHelper.clamp_float(this.scroll, -Math.max(0.0f, altHeight - (this.getHeight() - this.getHeader() - 18.0f)), 0.0f);
        RenderUtil.pushScissor(this.getX(), this.getY() + this.getHeader() + 2.0f, this.getWidth(), this.getHeight() - this.getHeader() - 20.0f);
        float y = this.getY() + this.getHeader() + 2.0f;
        for (AltButton altButton : this.altButtons) {
            altButton.setX(this.getX() + 2.0f);
            altButton.setY(y + this.scroll);
            altButton.render(mouseX, mouseY);
            y += 18.0f;
        }
        RenderUtil.popScissor();
        this.add.x = this.getX() + 2.0f;
        this.add.y = this.getY() + this.getHeight() - 18.0f;
        this.add.render(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, Click click) {
        super.mouseClicked(mouseX, mouseY, click);
        if (mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() + this.getHeader() && mouseY <= this.getY() + this.getHeight() - 18.0f) {
            this.altButtons.forEach(button -> button.onClick(mouseX, mouseY, click.getButton()));
        }
        this.add.onClick(mouseX, mouseY, click.getButton());
    }

    public void refreshAlts() {
        ArrayList<AltButton> deleteBuffer = new ArrayList<AltButton>();
        for (AltButton altButton : this.altButtons) {
            if (Wrapper.getMonsoon().getAltManager().getAlts().contains(altButton.alt)) continue;
            deleteBuffer.add(altButton);
        }
        this.altButtons.removeAll(deleteBuffer);
        for (Alt alt : Wrapper.getMonsoon().getAltManager().getAlts()) {
            if (!this.altButtons.stream().noneMatch(button -> ((AltButton)button).alt == alt)) continue;
            this.altButtons.add(new AltButton(alt, this.getX(), this.getY(), this.getWidth() - 4.0f, 16.0f));
        }
    }

    public static String getStatus() {
        return status;
    }

    public static void setStatus(String status) {
        AltWindow.status = status;
    }

    static class AltButton
    extends Button {
        private final Alt alt;

        public AltButton(Alt alt, float x, float y, float width, float height) {
            super(alt.getAuthenticator().equals((Object)Alt.Authenticator.CRACKED) ? alt.getEmail() + " (Cracked)" : (alt.getUsername().equals("") ? (alt.getEmail().contains("@") ? alt.getEmail().substring(0, 3) + alt.getEmail().substring(3, alt.getEmail().indexOf(64)).replaceAll(".", "*") + alt.getEmail().substring(alt.getEmail().indexOf(64)) : alt.getEmail()) : alt.getEmail()), x, y, width, height, () -> new Thread(() -> {
                AltWindow.setStatus("Logging in...");
                Session session = null;
                try {
                    session = alt.getSession();
                }
                catch (Exception exception) {
                    AltWindow.setStatus("Login failed!");
                    exception.printStackTrace();
                    return;
                }
                if (session != null) {
                    Minecraft.getMinecraft().session = session;
                }
                AltWindow.setStatus("Logged in as " + Minecraft.getMinecraft().session.getUsername());
                Wrapper.getMonsoon().getServer().sendPacket(new MPacketUpdateUsername(Minecraft.getMinecraft().session.getUsername()));
            }).start());
            this.alt = alt;
        }

        @Override
        public void render(float mouseX, float mouseY) {
            super.render(mouseX, mouseY);
            GL11.glScalef((float)1.8f, (float)1.8f, (float)1.8f);
            float factor = 0.5555556f;
            float side = (this.getX() + this.getWidth() - (float)Wrapper.getFontUtil().entypo14.getStringWidth(FontUtil.UNICODES_UI.TRASH) * 1.8f - 6.0f) * factor;
            Wrapper.getFontUtil().entypo14.drawString(FontUtil.UNICODES_UI.TRASH, side, (this.getY() + 2.0f) * factor, this.hover.getState() && mouseX >= this.getX() + this.getWidth() - 20.0f ? Color.WHITE : Color.GRAY, false);
            GL11.glScalef((float)factor, (float)factor, (float)factor);
        }

        @Override
        public void onClick(float mouseX, float mouseY, int button) {
            if (this.hover.getState()) {
                if (button == 0 && mouseX <= this.getX() + this.getWidth() - 20.0f) {
                    super.onClick(mouseX, mouseY, button);
                } else if (button == 0 && mouseX >= this.getX() + this.getWidth() - 20.0f) {
                    Wrapper.getMonsoon().getAltManager().removeAlt(this.alt);
                }
            }
        }
    }

    static class Button {
        private String text;
        private float x;
        private float y;
        private final float width;
        private final float height;
        private final Runnable onClick;
        public final Animation hover = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.LINEAR);

        public Button(String text, float x, float y, float width, float height, Runnable onClick) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.onClick = onClick;
        }

        public void render(float mouseX, float mouseY) {
            this.hover.setState(mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height);
            Color bg = ColorUtil.interpolate(Wrapper.getPallet().getBackground(), new Color(0, 0, 0, 0), 0.6 - this.hover.getAnimationFactor() * 0.3);
            RoundedUtils.round(this.x, this.y, this.width, this.height, 4.0f, bg);
            Wrapper.getFont().drawString(this.text, this.x + 4.0f, this.y + 2.0f, Color.WHITE, true);
        }

        public void onClick(float mouseX, float mouseY, int button) {
            if (this.hover.getState()) {
                this.onClick.run();
            }
        }

        public void setText(String text) {
            this.text = text;
        }

        public float getX() {
            return this.x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return this.y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public float getWidth() {
            return this.width;
        }
    }
}

