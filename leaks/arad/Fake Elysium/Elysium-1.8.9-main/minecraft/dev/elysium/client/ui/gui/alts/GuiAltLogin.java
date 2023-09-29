package dev.elysium.client.ui.gui.alts;


import dev.elysium.client.Elysium;
import dev.elysium.client.extensions.ConfigManager;
import dev.elysium.client.extensions.RandomName;
import dev.elysium.client.ui.font.TTFFontRenderer;
import dev.elysium.client.ui.gui.components.Button;
import dev.elysium.client.ui.gui.components.TextBox;
import dev.elysium.client.utils.api.Hypixel;
import dev.elysium.client.utils.render.BlurUtil;
import dev.elysium.client.utils.render.ColorAnimation;
import dev.elysium.client.utils.render.RenderUtils;
import dev.elysium.client.utils.render.Stencil;
import me.yarukon.oauth.OAuthService;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public final class GuiAltLogin extends GuiScreen {
    private final GuiScreen previousScreen;
    private AltLoginThread thread;
    public String status = "Idle";
    List<Button> buttons = new ArrayList<Button>();
    ScaledResolution sr;
    Button hovering;

    TextBox username;

    boolean setOffline = false; //because nigger -arad 24/08 21:17

    TTFFontRenderer fr;
    TTFFontRenderer frSmall;
    TTFFontRenderer choco;
    TTFFontRenderer elyX;
    TTFFontRenderer elyY;

    BlurUtil blur = new BlurUtil();

    public GuiAltLogin(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    protected void actionPerformed(GuiButton button) {

    }

    void initButtons() {
        buttons.clear();
        buttons.add(new Button("MICROSOFT LOGIN", width / 2 - 104, height / 2 - 45, width / 2 + 104, height / 2 - 15));
        buttons.get(0).font = fr;
        buttons.add(new Button("SIGN OUT", width / 2 - 104, height / 2 - 45 + 40, width / 2 + 104, height / 2 - 15 + 40));
        buttons.get(1).font = fr;

        buttons.add(new Button("Last Used", width / 2 - 35 + 3.5, height / 2 + 85, width / 2 + 35 - 3.5, height / 2 + 112));
        buttons.add(new Button("Gen Offline", width / 2 - 35 - 69, height / 2 + 85, width / 2 + 35 - 69 - 7, height / 2 + 112));
        buttons.add(new Button("Set Offline", width / 2 - 35 + 69 + 7, height / 2 + 85, width / 2 + 35 + 69, height / 2 + 112));

        buttons.add(new Button("Set Offline", width / 2 + 60, height / 2 + 157, width / 2 + 125, height / 2 + 183));

        buttons.get(5).custom = 1;

        buttons.add(new Button("close", width / 2 + 85 + 1, height / 2 - 110 - 3 + 1, width / 2 + 105 + 3 - 1, height / 2 - 90 - 1, 2));

        for(int i = 2; i < buttons.size(); i++) buttons.get(i).font = frSmall;

        username = new TextBox("POPPINS-SEMIBOLD 20", "Username", width / 2 - 120, height / 2 + 164, 250, 0xff57668B, 0xff57668B);
    }

    long last = System.currentTimeMillis();

    @Override
    public void drawScreen(int x2, int y2, float z2) {
        float delta = System.currentTimeMillis() - last;

        buttons.stream().filter(button -> !button.isMouseOverMe(x2, y2)).forEach(button -> {
            button.timer -= 4 * delta;
            if(button.timer < 0) button.timer = 0;
        });
        buttons.stream().filter(button -> button.isMouseOverMe(x2, y2)).forEach(button -> {
           hovering = button;
           button.timer += 4 * delta;
           if(button.timer > 1000) button.timer = 1000;
        });

        last = System.currentTimeMillis();

        mc.fontRendererObj.drawString(delta + "", 5, 5, -1);

        GlStateManager.pushMatrix();
        mc.getTextureManager().bindTexture(new ResourceLocation("Elysium/alt_bg.png"));
        //GlStateManager.color(0.8f, 0.8f, 0.8f, 1);
        drawModalRectWithCustomSizedTexture(0, 0, 0, 0, width, height, width, height);
        GlStateManager.popMatrix();
        //blur.blurArea(width / 2 - 135, height / 2 - 140, width / 2 + 135, height / 2 + 140, 5, 10);
        //blur.blur(1);

        RenderUtils.drawDiagnonalGradientRoundedRectOutline(width / 2 - 135, height / 2 - 140, width / 2 + 135, height / 2 + 140, 0xff221D31, 0xff422F63, 14);
        RenderUtils.drawGradientRoundedRect(width / 2 - 135 + 1, height / 2 - 140 + 1, width / 2 + 135 - 1, height / 2 + 140 - 1, 14, 0x3527155E, 0x3527155E);

        Stencil.getInstance().start();
        Stencil.getInstance().setBuffer(true);
        RenderUtils.drawARoundedRect(width / 2 + 85 + 1, height / 2 - 110 - 3 + 1, width / 2 + 105 + 3 - 1, height / 2 - 90 - 1, 5, -1);
        Stencil.getInstance().cropOutside();
        RenderUtils.drawARoundedRect(width / 2 + 85, height / 2 - 110 - 3, width / 2 + 105 + 3, height / 2 - 90, 5, 0xff332262);
        Stencil.getInstance().stopLayer();

        elyX.drawString("a", width / 2 + 92.5f, height / 2 - 108 + 4.5f, ColorAnimation.getColor(0xff777993, 0xffa7a9c3, buttons.get(6).timer / 1000f));

        choco.drawCenteredString("el sium", width / 2, height / 2 - 110, 0xff6041BF);

        elyY.drawString("b", width / 2 - 36, height / 2 - 110, 0xff6041BF);

        String s = "Currently ";
        if(mc.session.getToken().equalsIgnoreCase("0")) // s += mc.session.getToken().isEmpty() ? "Offline:" : "Online:"
            s += "Offline:";
        else
            s += "Online:";

        frSmall.drawString(s, width / 2 - frSmall.getStringWidth(s + mc.session.getUsername()) / 2, height / 2 + 42, 0xff6c678e);
        frSmall.drawString(mc.session.getUsername(), width / 2 - frSmall.getStringWidth(s + mc.session.getUsername()) / 2 + frSmall.getStringWidth(s), height / 2 + 42, 0xff796CD4);
        frSmall.drawString(status,width/2 - frSmall.getStringWidth(status)/2, height / 2 + 55, 0x90796CD4);
        if(setOffline) {
            RenderUtils.drawDiagnonalGradientRoundedRectOutline(width / 2 - 135, height / 2 + 150, width / 2 + 135, height / 2 + 190, 0xff221D31, 0xff422F63, 5);
            username.drawText();
            RenderUtils.drawGradientRoundedRect(width / 2 - 128, height / 2 + 157, width / 2 + 50, height / 2 + 183, 5, 0x1a634284, 0x1a7D64a7);
        }

        for(Button b : buttons) {
            if(b.custom == 1 && !setOffline) continue;
            if(b.color == 2) continue;
            int textColor = ColorAnimation.getColor(0xff7D65D3, 0xff6A7AA3, 1 - (b.timer / 1000f));
            int borderColor = ColorAnimation.getColor(0x00000000, 0xff4c326e, b.timer / 1000f);
            int borderColor2 = ColorAnimation.getColor(0x00000000, 0xff4c326e, b.timer / 1000f);
            if(borderColor != 0x00000000) {
                Stencil.INSTANCE.start();
                Stencil.INSTANCE.setBuffer(true);
                RenderUtils.drawARoundedRect(b.x, b.y, b.x2, b.y2, 5, -1);
                Stencil.INSTANCE.cropOutside();
                double w = 1;
                RenderUtils.drawGradientRoundedRect(b.x - w, b.y - w , b.x2 + w, b.y2 + w, 5, borderColor, borderColor2);
                Stencil.getInstance().stopLayer();
            }

            RenderUtils.drawGradientRoundedRect(b.x, b.y, b.x2, b.y2, 5, 0x1a634284, 0x1a7D64a7);

            b.font.drawCenteredString(b.idname, (float) (b.x + (b.x2 - b.x) / 2), (float) (b.y + (b.y2 - b.y) / 2 - b.font.getHeight(b.idname) / 2), textColor);
        }
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        sr = new ScaledResolution(mc);
        fr = Elysium.getInstance().getFontManager().getFont("POPPINS-SEMIBOLD 20");
        frSmall = Elysium.getInstance().getFontManager().getFont("POPPINS-SEMIBOLD 16");
        choco = Elysium.getInstance().getFontManager().getFont("CHOCOLATE 48");
        elyX = Elysium.getInstance().fm.getFont("ELY 16");
        elyY = Elysium.getInstance().getFontManager().getFont("ELY 68");
        blur.init();
        initButtons();
    }

    public void keyTyped(char character, int key) {
        if(key == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(previousScreen);
        } else if(key == Keyboard.KEY_RETURN) {
            mc.session.username = username.getText();
        }
        if(setOffline) { username.key(key); username.type(character); } else if(key == Keyboard.KEY_S){
            setOffline = true;
            username.focused = true;
        }
    }

    public void mouseClicked(int x2, int y2, int button) {
        if(setOffline) username.mouseClicked(x2, y2);
        buttons.stream().filter(b -> b.isMouseOverMe(x2, y2)).forEach(b -> {
            switch(b.idname) {
                case "MICROSOFT LOGIN":
                    OAuthService service = new OAuthService(this);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            service.authWithNoRefreshToken();
                        }
                    }).start();
                    break;
                case "SIGN OUT":
                    OAuthService oa = new OAuthService(this);
                    oa.openUrl("https://login.live.com/logout.srf",false);
                    break;
                case "Gen Offline":
                    mc.session.token = "0";
                    mc.session.username = "Generating...";
                    new Thread(new Runnable(){
                        public void run(){
                            for(int i = 0; i < 5; i++) {
                                String name = RandomName.getRandomName();
                                if(Hypixel.getInfo(name) != null) {
                                    name = RandomName.getRandomName();
                                } else {
                                    mc.session.username = name;
                                    ConfigManager.saveLastUser(mc.session.username, "", "");
                                    return;
                                }
                            }
                            mc.session.username = "_EpicGamer69_";
                        }
                    }).start();

                    ConfigManager.saveLastUser(mc.session.username, "", "");

                    break;
                case "close":
                    mc.displayGuiScreen(previousScreen);
                case "Last Used":
                    String[] data = ConfigManager.getData();
                    if(data.length != 3) return;
                    if(data[1].equalsIgnoreCase("") && data[2].equalsIgnoreCase("")) {
                        mc.session.username = data[0];
                    } else {
                        mc.session.username = data[0];
                        mc.session.token = data[1];
                        mc.session.playerID = data[2];
                        mc.session.sessionType = Session.Type.MOJANG;
                    }

                    break;
                case "Set Offline":
                    if(b.custom == 1) {
                        mc.session.username = username.getText();
                        ConfigManager.saveLastUser(mc.session.username, "", "");
                    } else if(b.custom == 0) {
                        setOffline = !setOffline;
                        username.focused = !username.focused;
                    }
                    break;
            }
        });
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
}

