package me.jinthium.straight.impl.ui;

import best.azura.irc.utils.Wrapper;
import me.jinthium.straight.api.client.User;
import me.jinthium.straight.api.irc.IRCClient;
import me.jinthium.straight.api.shader.ShaderUtil;
import me.jinthium.straight.api.util.Util;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.ui.components.Button;
import me.jinthium.straight.impl.utils.animation.Animation;
import me.jinthium.straight.impl.utils.animation.impl.DecelerateAnimation;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import obfuscation.NativeLib;

import java.io.IOException;

public class UserLogin extends GuiScreen implements Util {


    private long initTime;
    private final Animation imageAnimation = new DecelerateAnimation(400, 1);
    private final ShaderUtil shaderUtil = new ShaderUtil("backgroundShader");

    private String status = "Not Logged In";

    private GuiTextField username, password;
    public static boolean triedLogin = false;

    public void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if(button.id == 0){
            try {
                Client.INSTANCE.getKeyAuth().login(username.getText(), password.getText());
                if(Client.INSTANCE.getKeyAuth().isInitialized() && Client.INSTANCE.getKeyAuth().getUserData() != null) {
                    Client.INSTANCE.setUser(new User(Client.INSTANCE.getKeyAuth().getUserData().getUsername(), "0000"));
                    System.out.printf("Success! Logged in as: %s!%n", Client.INSTANCE.getUser().getUsername());
                    IRCClient.INSTANCE.connect();
                    mc.displayGuiScreen(new MainMenu());
                    Wrapper.getIRCConnector().username = Client.INSTANCE.getUser().getUsername();
                    Wrapper.getIRCConnector().password = password.getText();
                    triedLogin = true;
//                    Wrapper.getIRCConnector().sendPacket(new C0LoginRequestPacket(0));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//        this.drawDefaultBackground();

        ScaledResolution scaledResolution = ScaledResolution.fetchResolution(mc);
        shaderUtil.init();
        setupUniforms(initTime);
        ShaderUtil.drawQuads();
        shaderUtil.unload();
        normalFont22.drawStringWithShadow(status, (float) scaledResolution.getScaledWidth() / 2 - 45, (float) scaledResolution.getScaledHeight() / 2 - 25, -1);

        if(Client.INSTANCE.getUser() != null)
            status = String.format("Logged in as: %s!", Client.INSTANCE.getUser().getUsername());

        username.drawTextBox();
        password.drawTextBox();

        if (this.username.getText().isEmpty()) {
            this.drawString(this.mc.fontRendererObj, "Username / E-Mail", this.width / 2 - 96, scaledResolution.getScaledHeight() / 2 - 86, -7829368);
        }
        if (this.password.getText().isEmpty()) {
            this.drawString(this.mc.fontRendererObj, "Password", width / 2 - 96, scaledResolution.getScaledHeight() / 2 - 56, -7829368);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        username.updateCursorCounter();
        password.updateCursorCounter();
        super.updateScreen();
    }

    public void initGui() {
//        Display.setTitle(Client.IN);
        //encodedString = SecureStringEncoderDecoder.encrypt("https://raw.githubusercontent.com/Jinthium/testsite/main/cookingrecipesformama.txt");
        ScaledResolution scaledResolution = ScaledResolution.fetchResolution(mc);
        this.buttonList.add(new Button(0, scaledResolution.getScaledWidth() / 2 - 80, scaledResolution.getScaledHeight() / 2 - 10, 150, 40, "Login"));
        initTime = System.currentTimeMillis();

        this.username = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 100, scaledResolution.getScaledHeight() / 2 - 90, 200, 20);
        this.username.setFocused(true);
        this.username.setMaxStringLength(200);

        this.password = new GuiTextField(1, this.fontRendererObj, this.width / 2 - 100, scaledResolution.getScaledHeight() / 2 - 60, 200, 20, true);
        this.password.setMaxStringLength(200);
//        this.buttonList.add(new Button(2, ))
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        initTime = System.currentTimeMillis() - 10;
        buttonList.forEach(button -> button.animation.reset());
        imageAnimation.reset();
    }

    public void setupUniforms(long initTime){
        shaderUtil.setUniformf("time", (System.currentTimeMillis() - initTime) / 1000f);
        shaderUtil.setUniformf("resolution", mc.displayWidth, mc.displayHeight);
//        shaderUtil.setUniformf("mouse", Mouse.getX(), Mouse.getY());
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        this.username.textboxKeyTyped(typedChar, keyCode);
        this.password.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.username.mouseClicked(mouseX, mouseY, mouseButton);
        this.password.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
