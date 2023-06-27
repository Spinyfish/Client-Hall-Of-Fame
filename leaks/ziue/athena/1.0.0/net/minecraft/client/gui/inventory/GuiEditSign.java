package net.minecraft.client.gui.inventory;

import net.minecraft.client.gui.*;
import org.lwjgl.input.*;
import net.minecraft.client.resources.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import net.minecraft.client.network.*;
import java.io.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.*;
import net.minecraft.init.*;
import net.minecraft.client.renderer.tileentity.*;
import net.minecraft.tileentity.*;
import net.minecraft.block.*;

public class GuiEditSign extends GuiScreen
{
    private TileEntitySign tileSign;
    private int updateCounter;
    private int editLine;
    private GuiButton doneBtn;
    
    public GuiEditSign(final TileEntitySign teSign) {
        this.tileSign = teSign;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        this.buttonList.add(this.doneBtn = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120, I18n.format("gui.done", new Object[0])));
        this.tileSign.setEditable(false);
    }
    
    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        final NetHandlerPlayClient nethandlerplayclient = this.mc.getNetHandler();
        if (nethandlerplayclient != null) {
            nethandlerplayclient.addToSendQueue(new C12PacketUpdateSign(this.tileSign.getPos(), this.tileSign.signText));
        }
        this.tileSign.setEditable(true);
    }
    
    @Override
    public void updateScreen() {
        ++this.updateCounter;
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.enabled && button.id == 0) {
            this.tileSign.markDirty();
            this.mc.displayGuiScreen(null);
        }
    }
    
    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        if (keyCode == 200) {
            this.editLine = (this.editLine - 1 & 0x3);
        }
        if (keyCode == 208 || keyCode == 28 || keyCode == 156) {
            this.editLine = (this.editLine + 1 & 0x3);
        }
        String s = this.tileSign.signText[this.editLine].getUnformattedText();
        if (keyCode == 14 && s.length() > 0) {
            s = s.substring(0, s.length() - 1);
        }
        if (ChatAllowedCharacters.isAllowedCharacter(typedChar) && this.fontRendererObj.getStringWidth(s + typedChar) <= 90) {
            s += typedChar;
        }
        this.tileSign.signText[this.editLine] = new ChatComponentText(s);
        if (keyCode == 1) {
            this.actionPerformed(this.doneBtn);
        }
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, I18n.format("sign.edit", new Object[0]), this.width / 2, 40, 16777215);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)(this.width / 2), 0.0f, 50.0f);
        final float f = 93.75f;
        GlStateManager.scale(-f, -f, -f);
        GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
        final Block block = this.tileSign.getBlockType();
        if (block == Blocks.standing_sign) {
            final float f2 = this.tileSign.getBlockMetadata() * 360 / 16.0f;
            GlStateManager.rotate(f2, 0.0f, 1.0f, 0.0f);
            GlStateManager.translate(0.0f, -1.0625f, 0.0f);
        }
        else {
            final int i = this.tileSign.getBlockMetadata();
            float f3 = 0.0f;
            if (i == 2) {
                f3 = 180.0f;
            }
            if (i == 4) {
                f3 = 90.0f;
            }
            if (i == 5) {
                f3 = -90.0f;
            }
            GlStateManager.rotate(f3, 0.0f, 1.0f, 0.0f);
            GlStateManager.translate(0.0f, -1.0625f, 0.0f);
        }
        if (this.updateCounter / 6 % 2 == 0) {
            this.tileSign.lineBeingEdited = this.editLine;
        }
        TileEntityRendererDispatcher.instance.renderTileEntityAt(this.tileSign, -0.5, -0.75, -0.5, 0.0f);
        this.tileSign.lineBeingEdited = -1;
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
