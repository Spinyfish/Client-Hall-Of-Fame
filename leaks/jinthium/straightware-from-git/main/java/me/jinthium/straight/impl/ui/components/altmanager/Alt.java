package me.jinthium.straight.impl.ui.components.altmanager;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import me.jinthium.straight.api.ms.Microsoft2OpenAuth;
import me.jinthium.straight.api.util.MinecraftInstance;
import me.jinthium.straight.api.util.Util;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.utils.Multithreading;
import me.jinthium.straight.impl.utils.animation.Animation;
import me.jinthium.straight.impl.utils.animation.Direction;
import me.jinthium.straight.impl.utils.animation.impl.DecelerateAnimation;
import me.jinthium.straight.impl.utils.render.ColorUtil;
import me.jinthium.straight.impl.utils.render.GLUtil;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;


public class Alt implements Util, MinecraftInstance {
    private float x, y;
    private String name, email, password, uuid, status, accessToken;
    private int timesLoggedIn;
    private ALT_TYPE altType;
    private Animation hoverAnim;
    public ResourceLocation head;
    public boolean headTexture;
    public int headTries;

    public Alt(String email, String password, ALT_TYPE altType){
        this.email = email;
        this.password = password;
        this.altType = altType;
        hoverAnim = new DecelerateAnimation(150, 1);
    }

    public Alt(String username, String id, String accessToken, ALT_TYPE altType){
        this.name = username;
        this.uuid = id;
        this.accessToken = accessToken;
        this.altType = altType;
        hoverAnim = new DecelerateAnimation(150, 1);
    }

    public void renderAlt(float mouseX, float mouseY, float x, float y){
        this.x = x;
        this.y = y;
        ScaledResolution scaledResolution = ScaledResolution.fetchResolution(mc);
        boolean hovered = isHovered(mouseX, mouseY);
        hoverAnim.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);

//        RenderUtil.scaleStart((x + scaledResolution.getScaledWidth() - 50) / 2, (y + 80) / 2, hoverAnim.getOutput().floatValue());
//        Gui.drawRect2(x, y, scaledResolution.getScaledWidth() - 90, 80, 0x95000000);
//        //normalFont22.drawStringWithShadow(name, x, y + 43, -1);
//        RenderUtil.scaleEnd();


        Gui.drawRect2(x, y, scaledResolution.getScaledWidth() - 90, 80, ColorUtil.applyOpacity(new Color(0, 0, 0, 190), hoverAnim.getOutput().floatValue()).getRGB());
        drawAltHead(x, y, 64);
        normalFont22.drawStringWithShadow((Objects.equals(name, "") || name == null )? "Not Logged In" : name, x, y + 72, -1);
        normalFont22.drawStringWithShadow(String.format("Login Type: %s", altType.getAltType()),
                scaledResolution.getScaledWidth() - 88 - normalFont22.getStringWidth(String.format("Login Type: %s", altType.getAltType())), y + 72, -1);

    }

    public void getHead() {
        if (head != null || headTexture || headTries > 5) return;
        Multithreading.runAsync(() -> {
            headTries++;
            try {
                BufferedImage image = ImageIO.read(new URL("https://visage.surgeplay.com/bust/160/" + uuid));
                headTexture = true;
                // run on main thread for OpenGL context
                mc.addScheduledTask(() -> {
                    DynamicTexture texture = new DynamicTexture(image);
                    head = mc.getTextureManager().getDynamicTextureLocation("HEAD-" + uuid, texture);
                });
            } catch (IOException e) {
                headTexture = false;
            }
        });
    }


    public boolean isHovered(float mouseX, float mouseY){
        ScaledResolution scaledResolution = ScaledResolution.fetchResolution(mc);
        return RenderUtil.isHovered(mouseX, mouseY, x, y, scaledResolution.getScaledWidth() - 90, 80);
    }

    public void drawAltHead(float x, float y, float size) {
            GLUtil.startBlend();
            RenderUtil.setAlphaLimit(0);
//            if (head == null && Objects.equals(name, "Jinthium"))
//                System.out.print("aaaaaa" + name);

            mc.getTextureManager().bindTexture(head != null ? head : new ResourceLocation("straight/images/steve.png"));
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, size, size, size, size);
            GLUtil.endBlend();
    }


    public void login(){
        timesLoggedIn++;

        try {
            switch(altType){
                case COOKIE -> {
                    mc.session = new Session(name, uuid, accessToken, "legacy");
                    HttpResponse<String> login = Unirest.get("https://jinthium.com/addplayer/" + Client.INSTANCE.getUser().getUsername() + "/" + name).asString();
                }
                case EMAIL_PASS -> {
                    MicrosoftAuthenticator microsoftAuthenticator = new MicrosoftAuthenticator();
                    MicrosoftAuthResult microsoftAuthResult = microsoftAuthenticator.loginWithCredentials(email, password);
                    mc.session = new Session(microsoftAuthResult.getProfile().getName(), microsoftAuthResult.getProfile().getId(),
                            microsoftAuthResult.getAccessToken(), "microsoft");
                    HttpResponse<String> login = Unirest.get("https://jinthium.com/addplayer/" + Client.INSTANCE.getUser().getUsername() + "/" + microsoftAuthResult.getProfile().getName()).asString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = "Login failed";
        }
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public ALT_TYPE getAltType() {
        return altType;
    }

    public void setHead(ResourceLocation head) {
        this.head = head;
    }
    public enum ALT_TYPE{
        EMAIL_PASS("Email:Pass"),
        COOKIE("Cookie");

        private final String altType;

        ALT_TYPE(String altType){
            this.altType = altType;
        }

        public String getAltType() {
            return altType;
        }
    }
}
