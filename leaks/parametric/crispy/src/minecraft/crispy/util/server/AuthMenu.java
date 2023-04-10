package crispy.util.server;

import arithmo.gui.altmanager.Translate;
import crispy.Crispy;
import crispy.util.render.particle.ParticleUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.io.IOException;

public class AuthMenu extends GuiScreen {

    private boolean authing;
    private boolean finishedAuth;

    private ParticleUtil particleUtil;
    private final Translate translat = new Translate(0, -10);
    private float currentX, targetX, currentY, targetY;

    public void initGui() {

        particleUtil = new ParticleUtil(200, width, height);
        new Thread(() -> {

            int i = 24;
            int j = height / 4 + 70;
            authing = true;


            System.out.println("Finished authing");

            finishedAuth = true;


        }).start();


    }

    public void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {


        }

    }

    private void addAuth(int p_73969_1_, int p_73969_2_) {


    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int j = height / 4 + 30;
        if (authing) {

            drawCenteredString(Minecraft.fontRendererObj, "AUTHORIZING", width / 2, j, Color.green.getRGB());

        } else {
            drawCenteredString(Minecraft.fontRendererObj, "Getting things ready please wait...", width / 2, j, Color.green.getRGB());
        }
        if (finishedAuth) {
            finishedAuth = false;
            Crispy.INSTANCE.loadHandles();
        }
        int w = ScaledResolution.getScaledWidth();
        int h = ScaledResolution.getScaledHeight();
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);

        GlStateManager.pushMatrix();
        float xDiff = ((mouseX - ScaledResolution.getScaledWidth() / 2) - currentX) / sr.getScaleFactor();
        float yDiff = ((mouseY - ScaledResolution.getScaledHeight() / 2) - currentY) / sr.getScaleFactor();
        mouseY += translat.getY();
        float slide = translat.getY();
        currentX += xDiff * 0.3F;
        currentY += yDiff * 0.3F;
        GlStateManager.translate(currentX / 50, currentY / 50, 0);
        particleUtil.renderParticles();
        GlStateManager.bindTexture(0);
        GlStateManager.translate(currentX / 15, currentY / 15, 0);
        GlStateManager.popMatrix();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
