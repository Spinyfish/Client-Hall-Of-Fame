/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 *  org.lwjgl.input.Keyboard
 */
package wtf.monsoon.impl.ui.menu.windows;

import com.thealtening.api.response.Account;
import com.thealtening.auth.service.AlteningServiceType;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.manager.alt.Alt;
import wtf.monsoon.api.manager.alt.MicrosoftOAuth2Login;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.menu.MainMenu;
import wtf.monsoon.impl.ui.menu.windows.AltWindow;
import wtf.monsoon.impl.ui.menu.windows.ErrorWindow;
import wtf.monsoon.impl.ui.menu.windows.Window;
import wtf.monsoon.impl.ui.primitive.Click;

public class AltAddWindow
extends Window {
    private MainMenu menuContext;
    private final TextField email = new TextField("Email", this.getX() + 2.0f, this.getY() + 16.0f, this.getWidth() - 4.0f, 16.0f);
    private final TextField password = new TextField("Password", this.getX() + 2.0f, this.getY() + 34.0f, this.getWidth() - 4.0f, 16.0f);
    private Alt.Authenticator authenticator = Alt.Authenticator.MICROSOFT;
    private boolean waitingForApiKey = false;
    private final AltWindow.Button authButton = new AltWindow.Button("Auth: null", this.getX() + 2.0f, this.getY() + 52.0f, this.getWidth() - 4.0f, 16.0f, () -> {
        switch (this.authenticator) {
            case MICROSOFT: {
                Wrapper.getMonsoon().getAltManager().getAlteningAuthentication().updateService(AlteningServiceType.MOJANG);
                this.authenticator = Alt.Authenticator.CRACKED;
                break;
            }
            case CRACKED: {
                Wrapper.getMonsoon().getAltManager().getAlteningAuthentication().updateService(AlteningServiceType.THEALTENING);
                this.authenticator = Alt.Authenticator.ALTENING;
                break;
            }
            case ALTENING: {
                Wrapper.getMonsoon().getAltManager().getAlteningAuthentication().updateService(AlteningServiceType.MOJANG);
                this.authenticator = Alt.Authenticator.OAUTH;
                break;
            }
            case OAUTH: {
                Wrapper.getMonsoon().getAltManager().getAlteningAuthentication().updateService(AlteningServiceType.MOJANG);
                this.authenticator = Alt.Authenticator.MICROSOFT;
            }
        }
    });
    private final AltWindow.Button addButton = new AltWindow.Button("Add Alt", this.getX() + 2.0f, this.getY() + 70.0f, this.getWidth() - 4.0f, 16.0f, () -> {
        if (this.waitingForApiKey && this.authenticator == Alt.Authenticator.ALTENING) {
            if (!this.email.getText().trim().isEmpty()) {
                Wrapper.getMonsoon().getAltManager().setApiKey(this.email.getText());
                this.waitingForApiKey = false;
            }
        } else if (this.authenticator == Alt.Authenticator.OAUTH) {
            try {
                MicrosoftOAuth2Login microsoftOAuth2 = new MicrosoftOAuth2Login();
                microsoftOAuth2.getAccessToken();
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        } else {
            if (this.email.getText().trim().isEmpty()) {
                this.menuContext.getWindows().add(new ErrorWindow("Empty email / username field!", new String[]{"The email / username field cannot be empty!"}, (float)Minecraft.getMinecraft().displayWidth / 4.0f - 100.0f, 100.0f, 200.0f, 100.0f, 14.0f));
                return;
            }
            if (this.password.getText().trim().isEmpty() && this.authenticator.equals((Object)Alt.Authenticator.MICROSOFT)) {
                this.menuContext.getWindows().add(new ErrorWindow("Empty password field!", new String[]{"The password cannot be empty whilst using", "Microsoft authentication!"}, (float)Minecraft.getMinecraft().displayWidth / 4.0f - 100.0f, 100.0f, 200.0f, 100.0f, 14.0f));
                return;
            }
            Alt alt = new Alt(this.email.getText(), this.password.getText(), this.authenticator);
            Wrapper.getMonsoon().getAltManager().addAlt(alt);
            Wrapper.getMonsoon().getConfigSystem().saveAlts(Wrapper.getMonsoon().getAltManager());
        }
    });
    private final AltWindow.Button fromClipboard = new AltWindow.Button("Import from clipboard", this.getX() + 2.0f, this.getY() + 52.0f, this.getWidth() - 4.0f, 16.0f, () -> {
        try {
            String data = (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            if (data.contains(":")) {
                String[] combo = data.split(":");
                this.email.setText(combo[0]);
                this.password.setText(combo[1]);
            } else if (data.contains("|")) {
                String[] combo = data.split("\\|");
                this.email.setText(combo[0]);
                this.password.setText(combo[1]);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    });
    private final AltWindow.Button randomName = new AltWindow.Button("Generate Random Name", this.getX() + 2.0f, this.getY() + 52.0f, this.getWidth() - 4.0f, 16.0f, () -> {
        try {
            this.email.setText(StringUtil.getValidUsername());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    });
    private final AltWindow.Button generate = new AltWindow.Button("Generate Account", this.getX() + 2.0f, this.getY() + 52.0f, this.getWidth() - 4.0f, 16.0f, () -> {
        try {
            if (Wrapper.getMonsoon().getAltManager().getApiKey().equals("api-0000-0000-0000")) {
                System.out.println("Now listening for API key.");
                this.waitingForApiKey = true;
            } else {
                System.out.println("Generating acc");
                Account account = Wrapper.getMonsoon().getAltManager().getAlteningAltFetcher().getAccount();
                this.email.setText(account.getToken());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    });

    public AltAddWindow(MainMenu menuContext, float x, float y, float width, float height, float header) {
        super(x, y, width, height, header);
        this.menuContext = menuContext;
    }

    @Override
    public void render(float mouseX, float mouseY) {
        super.render(mouseX, mouseY);
        Wrapper.getFont().drawString("Add Alt", this.getX() + 4.0f, this.getY() + 1.0f, Color.WHITE, false);
        if (this.authenticator == Alt.Authenticator.CRACKED || this.authenticator == Alt.Authenticator.ALTENING) {
            this.password.focused = false;
        }
        this.authButton.setText("Auth: " + StringUtil.formatEnum(this.authenticator));
        if (this.waitingForApiKey && this.authenticator == Alt.Authenticator.ALTENING) {
            this.addButton.setText("Submit API Key");
        } else if (this.authenticator == Alt.Authenticator.OAUTH) {
            this.addButton.setText("Login with Microsoft");
        } else {
            this.addButton.setText("Add Alt");
        }
        this.email.setX(this.getX() + 2.0f);
        this.password.setX(this.getX() + 2.0f);
        this.authButton.setX(this.getX() + 2.0f);
        this.addButton.setX(this.getX() + 2.0f);
        this.fromClipboard.setX(this.getX() + 2.0f);
        this.generate.setX(this.getX() + 2.0f);
        this.randomName.setX(this.getX() + 2.0f);
        this.email.setY(this.getY() + 16.0f);
        this.password.setY(this.getY() + 34.0f);
        this.authButton.setY(this.getY() + (float)(this.authenticator == Alt.Authenticator.MICROSOFT ? 52 : (this.authenticator == Alt.Authenticator.OAUTH ? 16 : 34)));
        this.addButton.setY(this.getY() + (float)(this.authenticator == Alt.Authenticator.MICROSOFT ? 88 : (this.authenticator == Alt.Authenticator.OAUTH ? 34 : 70)));
        this.fromClipboard.setY(this.getY() + 70.0f);
        this.randomName.setY(this.getY() + 52.0f);
        this.generate.setY(this.getY() + 52.0f);
        this.email.emptyText = this.authenticator == Alt.Authenticator.MICROSOFT ? "Email" : (this.authenticator == Alt.Authenticator.ALTENING ? (this.waitingForApiKey ? "Input your API key here" : "Alt Token") : "Username");
        this.setHeight(this.authenticator == Alt.Authenticator.MICROSOFT ? 106.0f : (this.authenticator == Alt.Authenticator.OAUTH ? 58.0f : 90.0f));
        if (this.authenticator != Alt.Authenticator.OAUTH) {
            this.email.render(mouseX, mouseY);
        }
        if (this.authenticator == Alt.Authenticator.MICROSOFT) {
            this.password.render(mouseX, mouseY);
            this.fromClipboard.render(mouseX, mouseY);
        }
        if (this.authenticator == Alt.Authenticator.ALTENING) {
            this.generate.render(mouseX, mouseY);
        }
        if (this.authenticator == Alt.Authenticator.CRACKED) {
            this.randomName.render(mouseX, mouseY);
        }
        this.authButton.render(mouseX, mouseY);
        this.addButton.render(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, Click click) {
        super.mouseClicked(mouseX, mouseY, click);
        this.email.clicked(mouseX, mouseY, click);
        if (this.authenticator == Alt.Authenticator.MICROSOFT) {
            this.password.clicked(mouseX, mouseY, click);
            this.fromClipboard.onClick(mouseX, mouseY, click.getButton());
        }
        if (this.authenticator == Alt.Authenticator.CRACKED) {
            this.randomName.onClick(mouseX, mouseY, click.getButton());
        }
        this.authButton.onClick(mouseX, mouseY, click.getButton());
        this.addButton.onClick(mouseX, mouseY, click.getButton());
        this.generate.onClick(mouseX, mouseY, click.getButton());
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
        this.email.keyTyped(typedChar, keyCode);
        if (this.authenticator == Alt.Authenticator.MICROSOFT) {
            this.password.keyTyped(typedChar, keyCode);
        }
    }

    private class TextField {
        private String emptyText;
        private String text = "";
        private float x;
        private float y;
        private final float width;
        private final float height;
        private boolean focused;
        private final Animation hover = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.LINEAR);

        public TextField(String emptyText, float x, float y, float width, float height) {
            this.emptyText = emptyText;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public void render(float mouseX, float mouseY) {
            this.hover.setState(mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height);
            Color bg = ColorUtil.interpolate(Wrapper.getPallet().getBackground(), new Color(0, 0, 0, 0), 0.6 - this.hover.getAnimationFactor() * 0.3);
            RoundedUtils.round(this.x, this.y, this.width, this.height, 2.0f, bg);
            Keyboard.enableRepeatEvents((boolean)true);
            if (this.text.isEmpty() && !this.focused) {
                Wrapper.getFont().drawString(this.emptyText, this.x + 4.0f, this.y + 2.0f, Color.GRAY, false);
            } else {
                String old = this.text;
                if (this.emptyText.equalsIgnoreCase("Password")) {
                    this.text = this.text.replaceAll(".", "*");
                }
                Wrapper.getFont().drawString(this.text + (this.focused ? (System.currentTimeMillis() % 1000L >= 500L ? "_" : "") : ""), this.x + 4.0f, this.y + 2.0f, Color.WHITE, false);
                this.text = old;
            }
        }

        public void clicked(float mouseX, float mouseY, Click click) {
            this.focused = this.hover.getState() ? !this.focused : false;
        }

        public void keyTyped(char typedChar, int keyCode) {
            if (this.focused) {
                if (keyCode == 28) {
                    this.focused = false;
                } else if (this.focused && Keyboard.isKeyDown((int)14)) {
                    if (!this.text.isEmpty()) {
                        this.text = this.text.substring(0, this.text.length() - 1);
                    }
                } else if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                    this.text = this.text + typedChar;
                }
                if (GuiScreen.isCtrlKeyDown() && keyCode == 47) {
                    try {
                        this.text = (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                    }
                    catch (UnsupportedFlavorException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public String getText() {
            return this.text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public void setX(float x) {
            this.x = x;
        }

        public void setY(float y) {
            this.y = y;
        }
    }
}

