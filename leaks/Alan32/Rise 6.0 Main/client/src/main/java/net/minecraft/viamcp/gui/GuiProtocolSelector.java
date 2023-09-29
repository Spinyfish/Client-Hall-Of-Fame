package net.minecraft.viamcp.gui;

import com.alan.clients.util.font.impl.minecraft.FontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.viamcp.ViaMCP;
import net.minecraft.viamcp.protocols.ProtocolCollection;

import java.io.IOException;

public class GuiProtocolSelector extends GuiScreen {
    private final GuiScreen parent;
    public SlotList list;

    public GuiProtocolSelector(final GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new GuiButton(1, width / 2 - 100, height - 25, 200, 20, "Back"));
        buttonList.add(new GuiButton(2, width / 2 - 180, height - 25, 75, 20, "Credits"));
        list = new SlotList(mc, width, height, 32, height - 32, 10);
    }

    @Override
    protected void actionPerformed(final GuiButton guiButton) throws IOException {
        list.actionPerformed(guiButton);

        if (guiButton.id == 1) {
            mc.displayGuiScreen(parent);
        }

        if (guiButton.id == 2) {
            mc.displayGuiScreen(new GuiCredits(this));
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        list.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        list.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0, 2.0, 2.0);
        final String title = EnumChatFormatting.BOLD + "ViaMCP Reborn";
        drawString(this.fontRendererObj, title, (this.width - (this.fontRendererObj.width(title) * 2)) / 4, 5, -1);
        GlStateManager.popMatrix();

        final String versionName = ProtocolCollection.getProtocolById(ViaMCP.getInstance().getVersion()).getName();
        final String versionCodeName = ProtocolCollection.getProtocolInfoById(ViaMCP.getInstance().getVersion()).getName();
        final String versionReleaseDate = ProtocolCollection.getProtocolInfoById(ViaMCP.getInstance().getVersion()).getReleaseDate();
        final String versionTitle = "Version: " + versionName + " - " + versionCodeName;
        final String versionReleased = "Released: " + versionReleaseDate;

        final int fixedHeight = ((5 + FontRenderer.FONT_HEIGHT) * 2) + 2;

        drawString(this.fontRendererObj, EnumChatFormatting.GRAY + (EnumChatFormatting.BOLD + "Version Information"), (width - this.fontRendererObj.width("Version Information")) / 2, fixedHeight, -1);
        drawString(this.fontRendererObj, versionTitle, (width - this.fontRendererObj.width(versionTitle)) / 2, fixedHeight + FontRenderer.FONT_HEIGHT, -1);
        drawString(this.fontRendererObj, versionReleased, (width - this.fontRendererObj.width(versionReleased)) / 2, fixedHeight + FontRenderer.FONT_HEIGHT * 2, -1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    class SlotList extends GuiSlot {
        public SlotList(final Minecraft mc, final int width, final int height, final int top, final int bottom, final int slotHeight) {
            super(mc, width, height, top + 30, bottom, 18);
        }

        @Override
        protected int getSize() {
            return ProtocolCollection.values().length;
        }

        @Override
        protected void elementClicked(final int i, final boolean b, final int i1, final int i2) {
            final int protocolVersion = ProtocolCollection.values()[i].getVersion().getVersion();
            ViaMCP.getInstance().setVersion(protocolVersion);
            ViaMCP.getInstance().asyncSlider.setVersion(protocolVersion);
        }

        @Override
        protected boolean isSelected(final int i) {
            return false;
        }

        @Override
        protected void drawBackground() {
            drawDefaultBackground();
        }

        @Override
        protected void drawSlot(final int i, final int i1, final int i2, final int i3, final int i4, final int i5) {
            drawCenteredString(mc.fontRendererObj, (ViaMCP.getInstance().getVersion() == ProtocolCollection.values()[i].getVersion().getVersion() ? EnumChatFormatting.GREEN.toString() + EnumChatFormatting.BOLD : EnumChatFormatting.GRAY.toString()) + ProtocolCollection.getProtocolById(ProtocolCollection.values()[i].getVersion().getVersion()).getName(), width / 2, i2 + 2, -1);
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5, 0.5, 0.5);
            drawCenteredString(mc.fontRendererObj, "PVN: " + ProtocolCollection.getProtocolById(ProtocolCollection.values()[i].getVersion().getVersion()).getVersion(), width, (i2 + 2) * 2 + 20, -1);
            GlStateManager.popMatrix();
        }
    }
}
