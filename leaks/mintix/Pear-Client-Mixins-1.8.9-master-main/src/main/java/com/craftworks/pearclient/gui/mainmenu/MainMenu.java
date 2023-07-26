package com.craftworks.pearclient.gui.mainmenu;

import com.craftworks.pearclient.animation.Animate;
import com.craftworks.pearclient.gui.mainmenu.button.IButton;
import com.craftworks.pearclient.gui.mainmenu.button.TextButton;
import com.craftworks.pearclient.gui.mainmenu.button.button;
import com.craftworks.pearclient.gui.mainmenu.button.button2;
import com.craftworks.pearclient.util.GLRectUtils;
import com.craftworks.pearclient.util.font.FontUtil;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class MainMenu extends GuiScreen {

    private final Animate Pp = new Animate();

    private final ArrayList<TextButton> PP = new ArrayList<>();
    private final ArrayList<button> PPp = new ArrayList<>();
    private final ArrayList<button2> PpP = new ArrayList<>();
    private final ArrayList<IButton> PPP = new ArrayList<>();
    private static final ResourceLocation background = new ResourceLocation("pearclient/background/PearBackground.png");

    public MainMenu() {
        PP.add(new TextButton("Singleplayer", width / 2 - 75, height / 2));
        PP.add(new TextButton("Multiplayer", width / 2 - 75, height / 2 + 25));
        PPp.add(new button("Options", width / 2 - 75, height / 2 + 50));
        PpP.add(new button2("Exit", width / 2, height / 2 + 75));
        PPP.add(new IButton("pearclient/icon/Settings.png", width / 2, height / 2 + 100));
        PPP.add(new IButton("pearclient/icon/Settings.png", width / 2, height / 2 + 100));
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        ScaledResolution sr = new ScaledResolution(mc);
        mc.getTextureManager().bindTexture(background);
        this.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, this.width, this.height, this.width, this.height);
        FontUtil.bold_large.drawString("Pear Client", (int) (width / 2f - fontRendererObj.getStringWidth("Pear Client") + 20), height / 2 - 60, -1);

        int y = 0;
        for (TextButton textButton : PP) {
            textButton.renderButton(width / 2 - 75, height / 2 + y++ * 25, mouseX, mouseY);
        }
        for (button btn : PPp) {
            btn.renderButton(width / 2 - 75, height / 2 + 50, mouseX, mouseY);
        }
        for (button2 btn : PpP) {
            btn.renderButton(width / 2 + 2, height / 2 + 50, mouseX, mouseY);
        }
        GLRectUtils.drawRoundedRect(width * 1.001f - 120, height * 1.002f - 70 ,width * 1.001f - 10, height * 1.001f - 10, 4.0f, new Color(0, 0, 0).getRGB());
        if(mouseX >= width * 1.001f - 120 && mouseX <= width * 1.001f - 10 && mouseY >= height * 1.002f - 70 && mouseY <= height * 1.001f - 10) {
            Pp.update().setReversed(true);
            GLRectUtils.drawRoundedOutline(width * 1.001f - 120, height * 1.002f - 70, width * 1.001f - 10, height * 1.001f - 10, 4.0f, 2.0f, new Color(60, 232, 118, Pp.getValueI() + 200).getRGB());
        } else {
            Pp.update().setReversed(true);
            GLRectUtils.drawRoundedOutline(width * 1.001f - 120, height * 1.002f - 70, width * 1.001f - 10, height * 1.001f - 10, 4.0f, 2.0f, new Color(60, 232, 118, Pp.getValueI() + 100).getRGB());
        }
        for (IButton ibtn : PPP) {
            if (ibtn.getIcon().equals("pearclient/icon/Settings.png")) {
                ibtn.renderButton(width / 2 + 10, height * 1 - 25, mouseX, mouseY);
            }
        }
        FontUtil.bold_small.drawString("OFFICIAL PARTNERED WITH", width * 1.001f - 112, height * 1.002f - 60, -1);
        FontUtil.bold_small.drawString("Tick Hosting", width * 1.001f - 85, height * 1.002f - 50, -1);
        FontUtil.bold.drawString("tickhosting.com", width * 1.001f - 100, height * 1.002f - 35, new Color(60, 232, 118).getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (TextButton textButton : PP) {
            if (textButton.isHovered(mouseX, mouseY)) {
                switch (textButton.getText()) {
                    case "Singleplayer":
                        mc.displayGuiScreen(new GuiSelectWorld(this));
                        break;
                    case "Multiplayer":
                        mc.displayGuiScreen(new GuiMultiplayer(this));
                        break;
                }
            }
            for (button btn : PPp) {
                if (btn.isHovered(mouseX, mouseY)) {
                    switch (btn.getText()) {
                        case "Options":
                        mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                        break;
                    }
                }
            }
            for (button2 btn : PpP) {
                if (btn.isHovered(mouseX, mouseY)) {
                    switch (btn.getText()) {
                        case "Exit":
                            mc.shutdown();
                            break;
                    }
                }
            }
        }
    }
}