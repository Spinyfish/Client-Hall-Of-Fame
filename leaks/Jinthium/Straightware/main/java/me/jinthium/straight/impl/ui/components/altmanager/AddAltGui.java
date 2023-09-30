package me.jinthium.straight.impl.ui.components.altmanager;

import me.jinthium.straight.api.util.Util;
import me.jinthium.straight.impl.ui.AltManager;
import me.jinthium.straight.impl.ui.components.Button;
import net.minecraft.client.gui.*;

import java.io.IOException;

public class AddAltGui extends GuiScreen implements Util {

    private GuiTextField emailAndPassword;
    private final AltManager altManager;

    public AddAltGui(AltManager altManager){
        this.altManager = altManager;
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        String[] combo = emailAndPassword.getText().split(":");
        if (button.id == 0) {
            this.altManager.alts.add(new Alt(combo[0], combo[1], Alt.ALT_TYPE.EMAIL_PASS));
            mc.displayGuiScreen(altManager);
        }
        super.actionPerformed(button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        //Gui.drawRect2((double) scaledResolution.getScaledWidth() / 2 - 50, (double) scaledResolution.getScaledHeight() / 2 - 50, 100, 100, 0x90000000);
        emailAndPassword.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        emailAndPassword.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    public void initGui() {
        ScaledResolution scaledResolution = ScaledResolution.fetchResolution(mc);
        this.buttonList.add(new Button(0, scaledResolution.getScaledWidth() / 2 - 40, scaledResolution.getScaledHeight() / 2 - 50, 80, 50, "Add"));
        this.emailAndPassword = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 100, scaledResolution.getScaledHeight() / 2 - 90, 200, 20);
        this.emailAndPassword.setFocused(true);
        this.emailAndPassword.setText("Insert email:pass");
        this.emailAndPassword.setMaxStringLength(200);
        super.initGui();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        this.emailAndPassword.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.emailAndPassword.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
