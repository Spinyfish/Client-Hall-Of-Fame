package frapppyz.cutefurry.pics.guis;

import frapppyz.cutefurry.pics.alts.AltLoginThread;
import frapppyz.cutefurry.pics.alts.CookieLoginTYBabyPlus;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import java.io.IOException;

public final class GuiLogin
        extends GuiScreen {
    public AltLoginThread thread;
    private GuiTextField combo;
    public static String uuid, name;

    public void login() {
            String combo = this.combo.getText();

            /*if(combo.contains("cookielogin")){
                try {
                    new CookieLoginTYBabyPlus().login();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{

             */
                try {
                    String[] seperated = combo.split(":");
                    String email = seperated[0];
                    String pass = seperated[1];
                    this.thread = new AltLoginThread(email, pass);
                    this.thread.start();
                } catch (Exception e) {
                    this.thread = new AltLoginThread(combo, "");
                    this.thread.start();
                }
            //}

    }

    @Override
    public void drawScreen(int x2, int y2, float z2) {
        this.drawDefaultBackground();
        this.combo.drawTextBox();
        this.drawCenteredString(this.mc.fontRendererObj, this.thread == null ? (Object)((Object)EnumChatFormatting.GRAY) + "Please login." : this.thread.getStatus(), width / 2, 29, -1);
        if (this.combo.getText().isEmpty()) {
            this.drawString(this.mc.fontRendererObj, "Email:Pass", width / 2 - 96,  (int) (mc.displayHeight/9.5), -7829368);
        }
        super.drawScreen(x2, y2, z2);
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 14) {
            try {
                new CookieLoginTYBabyPlus().login();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initGui() {
        int var3 = height / 4 + 24;

        this.combo = new GuiTextField(var3, this.mc.fontRendererObj, width / 2 - 100, (int) (mc.displayHeight/10), 200, 20);
        this.buttonList.add(new GuiButton(14, mc.displayWidth / 4 - 100, mc.displayHeight/6, "Cookie Login"));
        this.combo.setFocused(true);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    protected void keyTyped(char character, int key) {
        try {
            super.keyTyped(character, key);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (character == '\t') {
            if (!this.combo.isFocused()) {
                this.combo.setFocused(true);
            }
        }
        if (character == '\r') {
            login();
        }
        this.combo.textboxKeyTyped(character, key);
    }

    @Override
    public void mouseClicked(int x2, int y2, int button) {
        try {
            super.mouseClicked(x2, y2, button);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.combo.mouseClicked(x2, y2, button);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        this.combo.updateCursorCounter();
    }
}
