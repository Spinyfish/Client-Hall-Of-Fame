package net.minecraft.viamcp.gui;

import com.alan.clients.util.font.impl.minecraft.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;

import java.io.IOException;

public class GuiCredits extends GuiScreen {
    private final GuiScreen parent;

    public GuiCredits(final GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new GuiButton(1, width / 2 - 100, height - 25, 200, 20, "Back"));
    }

    @Override
    protected void actionPerformed(final GuiButton guiButton) throws IOException {
        if (guiButton.id == 1) {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        drawDefaultBackground();

        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0, 2.0, 2.0);
        final String title = EnumChatFormatting.BOLD + "Credits";
        drawString(this.fontRendererObj, title, (this.width - (this.fontRendererObj.width(title) * 2)) / 4, 5, -1);
        GlStateManager.popMatrix();

        final int fixedHeight = ((5 + FontRenderer.FONT_HEIGHT) * 2) + 2;

        final String viaVerTeam = EnumChatFormatting.GRAY + (EnumChatFormatting.BOLD + "ViaVersion Team");
        final String florMich = EnumChatFormatting.GRAY + (EnumChatFormatting.BOLD + "FlorianMichael");
        final String laVache = EnumChatFormatting.GRAY + (EnumChatFormatting.BOLD + "LaVache-FR");
        final String hideri = EnumChatFormatting.GRAY + (EnumChatFormatting.BOLD + "Hiderichan / Foreheadchan");
        final String contactInfo = EnumChatFormatting.GRAY + (EnumChatFormatting.BOLD + "Contact Info");

        drawString(this.fontRendererObj, viaVerTeam, (this.width - this.fontRendererObj.width(viaVerTeam)) / 2, fixedHeight, -1);
        drawString(this.fontRendererObj, "ViaVersion", (this.width - this.fontRendererObj.width("ViaVersion")) / 2, fixedHeight + FontRenderer.FONT_HEIGHT, -1);
        drawString(this.fontRendererObj, "ViaBackwards", (this.width - this.fontRendererObj.width("ViaBackwards")) / 2, fixedHeight + FontRenderer.FONT_HEIGHT * 2, -1);
        drawString(this.fontRendererObj, "ViaRewind", (this.width - this.fontRendererObj.width("ViaRewind")) / 2, fixedHeight + FontRenderer.FONT_HEIGHT * 3, -1);

        drawString(this.fontRendererObj, florMich, (this.width - this.fontRendererObj.width(florMich)) / 2, fixedHeight + FontRenderer.FONT_HEIGHT * 5, -1);
        drawString(this.fontRendererObj, "ViaForge", (this.width - this.fontRendererObj.width("ViaForge")) / 2, fixedHeight + FontRenderer.FONT_HEIGHT * 6, -1);

        drawString(this.fontRendererObj, laVache, (this.width - this.fontRendererObj.width(laVache)) / 2, fixedHeight + FontRenderer.FONT_HEIGHT * 8, -1);
        drawString(this.fontRendererObj, "Original ViaMCP", (this.width - this.fontRendererObj.width("Original ViaMCP")) / 2, fixedHeight + FontRenderer.FONT_HEIGHT * 9, -1);

        drawString(this.fontRendererObj, hideri, (this.width - this.fontRendererObj.width(hideri)) / 2, fixedHeight + FontRenderer.FONT_HEIGHT * 11, -1);
        drawString(this.fontRendererObj, "ViaMCP Reborn", (this.width - this.fontRendererObj.width("ViaMCP Reborn")) / 2, fixedHeight + FontRenderer.FONT_HEIGHT * 12, -1);

        drawString(this.fontRendererObj, contactInfo, (this.width - this.fontRendererObj.width(contactInfo)) / 2, fixedHeight + FontRenderer.FONT_HEIGHT * 14, -1);
        drawString(this.fontRendererObj, "Discord: Hideri#9003", (this.width - this.fontRendererObj.width("Discord: Hideri#9003")) / 2, fixedHeight + FontRenderer.FONT_HEIGHT * 15, -1);

        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);
        drawString(this.fontRendererObj, EnumChatFormatting.GRAY + "(https://github.com/ViaVersion/ViaVersion)", (this.width + this.fontRendererObj.width("ViaVersion ")), (fixedHeight + FontRenderer.FONT_HEIGHT) * 2 + FontRenderer.FONT_HEIGHT / 2, -1);
        drawString(this.fontRendererObj, EnumChatFormatting.GRAY + "(https://github.com/ViaVersion/ViaBackward)", (this.width + this.fontRendererObj.width("ViaBackwards ")), (fixedHeight + FontRenderer.FONT_HEIGHT * 2) * 2 + FontRenderer.FONT_HEIGHT / 2, -1);
        drawString(this.fontRendererObj, EnumChatFormatting.GRAY + "(https://github.com/ViaVersion/ViaRewind)", (this.width + this.fontRendererObj.width("ViaRewind ")), (fixedHeight + FontRenderer.FONT_HEIGHT * 3) * 2 + FontRenderer.FONT_HEIGHT / 2, -1);
        drawString(this.fontRendererObj, EnumChatFormatting.GRAY + "(https://github.com/FlorianMichael/ViaForge)", (this.width + this.fontRendererObj.width("ViaForge ")), (fixedHeight + FontRenderer.FONT_HEIGHT * 6) * 2 + FontRenderer.FONT_HEIGHT / 2, -1);
        drawString(this.fontRendererObj, EnumChatFormatting.GRAY + "(https://github.com/LaVache-FR/ViaMCP)", (this.width + this.fontRendererObj.width("Original ViaMCP ")), (fixedHeight + FontRenderer.FONT_HEIGHT * 9) * 2 + FontRenderer.FONT_HEIGHT / 2, -1);
        drawString(this.fontRendererObj, EnumChatFormatting.GRAY + "(https://github.com/Foreheadchann/ViaMCP-Reborn)", (this.width + this.fontRendererObj.width("ViaMCP Reborn ")), (fixedHeight + FontRenderer.FONT_HEIGHT * 12) * 2 + FontRenderer.FONT_HEIGHT / 2, -1);
        GlStateManager.popMatrix();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
