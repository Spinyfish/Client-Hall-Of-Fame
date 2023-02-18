/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.Display
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL20
 *  vip.astroline.client.Astroline
 *  vip.astroline.client.layout.menu.AstrolineMenu
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.service.module.impl.render.Hud
 *  vip.astroline.client.storage.utils.gui.buttons.AstroButton
 *  vip.astroline.client.storage.utils.gui.fields.PasswordField
 *  vip.astroline.client.storage.utils.gui.fields.UsernameField
 *  vip.astroline.client.storage.utils.render.render.RenderUtil
 */
package vip.astroline.client.layout.login;

import java.awt.Color;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import vip.astroline.client.Astroline;
import vip.astroline.client.layout.menu.AstrolineMenu;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.service.module.impl.render.Hud;
import vip.astroline.client.storage.utils.gui.buttons.AstroButton;
import vip.astroline.client.storage.utils.gui.fields.PasswordField;
import vip.astroline.client.storage.utils.gui.fields.UsernameField;
import vip.astroline.client.storage.utils.render.render.RenderUtil;

public class LoginMenu
extends GuiScreen {
    public static String status;
    private final Color purpleish = new Color(230, 0, 255);
    private final Color purple = new Color(10551551);
    private final Color black = new Color(40, 46, 51);
    private float hHeight = 540.0f;
    private float hWidth = 960.0f;
    private float errorBoxHeight = 0.0f;
    AstroButton button = new AstroButton(0, (int)(this.hWidth - 70.0f), (int)(this.hHeight + 50.0f), 140, 30, "Log In");
    UsernameField field;
    PasswordField field2;

    public LoginMenu() {
        status = "Idle";
    }

    public void initGui() {
        Display.setTitle((String)"Astroline - Not logged in");
        this.buttonList.add(this.button);
        this.field = new UsernameField(1, this.mc.fontRendererObj, (int)this.hWidth - 70, (int)this.hHeight - 35, 140, 35, "idk");
        this.field2 = new PasswordField(2, this.mc.fontRendererObj, (int)this.hWidth - 70, (int)this.hHeight - 35, 140, 35, "idk");
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.disableCull();
        GL11.glBegin((int)7);
        GL11.glVertex2f((float)-1.0f, (float)-1.0f);
        GL11.glVertex2f((float)-1.0f, (float)1.0f);
        GL11.glVertex2f((float)1.0f, (float)1.0f);
        GL11.glVertex2f((float)1.0f, (float)-1.0f);
        GL11.glEnd();
        GL20.glUseProgram((int)0);
        Color white = Color.WHITE;
        Color shitGray = new Color(150, 150, 150);
        this.button.setColor(this.interpolateColor(this.button.hovered(mouseX, mouseY) ? Hud.hudColor1.getColor().brighter() : Hud.hudColor1.getColor(), this.button.hovered(mouseX, mouseY) ? Hud.hudColor1.getColor().brighter() : Hud.hudColor1.getColor(), 0.1f));
        this.field.setColor(this.interpolateColor(white, this.black, 0.1f));
        this.field.setTextColor(this.interpolateColor(shitGray, white, 0.1f));
        this.field2.setColor(this.interpolateColor(white, this.black, 0.1f));
        this.field2.setTextColor(this.interpolateColor(shitGray, white, 0.1f));
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        this.button.updateCoordinates(this.hWidth - 70.0f, this.hHeight + 50.0f);
        this.field.updateCoordinates(this.hWidth - 70.0f, this.hHeight - 35.0f);
        this.field2.updateCoordinates(this.hWidth - 70.0f, this.hHeight + 5.0f);
        int scaledWidthScaled = scaledResolution.getScaledWidth();
        int scaledHeightScaled = scaledResolution.getScaledHeight();
        this.hHeight += (float)scaledHeightScaled / 2.0f - this.hHeight;
        this.hWidth = (float)scaledWidthScaled / 2.0f;
        RenderUtil.drawImg((ResourceLocation)new ResourceLocation("astroline/images/bg.jpg"), (double)0.0, (double)0.0, (double)this.width, (double)this.height);
        RenderUtil.drawBorderedRect((float)(this.hWidth - 90.0f), (float)(this.hHeight - 55.0f), (float)(this.hWidth + 90.0f), (float)(this.hHeight + 125.0f), (float)0.3f, (int)new Color(0, 0, 0, 80).getRGB(), (int)new Color(0, 0, 0, 100).getRGB());
        FontManager.vision30.drawString(Astroline.INSTANCE.getCLIENT().toUpperCase(), this.hWidth - FontManager.vision30.getStringWidth(Astroline.INSTANCE.getCLIENT().toUpperCase()) / 2.0f, (float)((int)(this.hHeight - 90.0f)), Hud.isLightMode.getValue() != false ? -11204908 : new Color(255, 255, 255).getRGB());
        this.button.drawButton(this.mc, mouseX, mouseY);
        if (status.startsWith("Idle") || status.startsWith("Initializing") || status.startsWith("Logging")) {
            FontManager.normal2.drawString(status, (float)((int)(this.hWidth - FontManager.normal2.getStringWidth(status) / 2.0f)), (float)((int)(this.hHeight + 105.0f)), this.interpolateColor(new Color(150, 150, 150), white, 0.1f));
        } else if (status.equals("Success")) {
            RenderUtil.drawBorderedRect((float)(this.hWidth - FontManager.normal2.getStringWidth(status) / 2.0f - 10.0f), (float)this.errorBoxHeight, (float)(this.hWidth + FontManager.normal2.getStringWidth(status) / 2.0f + 10.0f), (float)(this.errorBoxHeight + 12.0f), (float)1.0f, (int)new Color(170, 253, 126).getRGB(), (int)this.interpolateColor(new Color(232, 255, 213), new Color(232, 255, 213).darker().darker(), 0.1f));
            FontManager.normal2.drawString(status, (float)((int)this.hWidth) - FontManager.normal2.getStringWidth(status) / 2.0f, (float)((int)this.errorBoxHeight + 7) - FontManager.normal2.getHeight(status) / 2.0f, new Color(201, 255, 167).darker().getRGB());
            Display.setTitle((String)("Astroline | Version: " + Astroline.INSTANCE.getVERSION() + " | User: " + Astroline.INSTANCE.keyAuth.getUserData().getUsername() + " | Subscription: " + Astroline.INSTANCE.keyAuth.getUserData().getSubscription()));
            this.mc.displayGuiScreen((GuiScreen)new AstrolineMenu());
        } else {
            RenderUtil.drawBorderedRect((float)(this.hWidth - FontManager.normal2.getStringWidth(status) / 2.0f - 10.0f), (float)this.errorBoxHeight, (float)(this.hWidth + FontManager.normal2.getStringWidth(status) / 2.0f + 10.0f), (float)(this.errorBoxHeight + 12.0f), (float)1.0f, (int)-664863, (int)this.interpolateColor(new Color(-465432), new Color(-465432).darker().darker(), 0.1f));
            FontManager.normal2.drawString(status, (float)((int)this.hWidth) - FontManager.normal2.getStringWidth(status) / 2.0f, (float)((int)this.errorBoxHeight + 7) - FontManager.normal2.getHeight(status) / 2.0f, -1347963);
        }
        this.field.drawTextBox();
        this.field2.drawTextBox();
        FontManager.normal2.drawString("Astroline Development", (float)((int)(this.hWidth - FontManager.normal2.getStringWidth("Astroline Development") / 2.0f)), (float)(scaledHeightScaled - 20), new Color(150, 150, 150).getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.field.textboxKeyTyped(typedChar, keyCode);
        this.field2.textboxKeyTyped(typedChar, keyCode);
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            button.enabled = true;
            try {
                status = "Logging in";
                Astroline.INSTANCE.keyAuth.login(this.field.getText(), this.field2.getText());
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }
        super.actionPerformed(button);
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.field.mouseClicked(mouseX, mouseY, mouseButton);
        this.field2.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private int interpolateColor(Color color1, Color color2, float fraction) {
        int red = (int)((float)color1.getRed() + (float)(color2.getRed() - color1.getRed()) * fraction);
        int green = (int)((float)color1.getGreen() + (float)(color2.getGreen() - color1.getGreen()) * fraction);
        int blue = (int)((float)color1.getBlue() + (float)(color2.getBlue() - color1.getBlue()) * fraction);
        int alpha = (int)((float)color1.getAlpha() + (float)(color2.getAlpha() - color1.getAlpha()) * fraction);
        try {
            return new Color(red, green, blue, alpha).getRGB();
        }
        catch (Exception ex) {
            return -1;
        }
    }
}
