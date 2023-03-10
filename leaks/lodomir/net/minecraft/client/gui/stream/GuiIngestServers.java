/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  tv.twitch.broadcast.IngestServer
 */
package net.minecraft.client.gui.stream;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.stream.IngestServerTester;
import net.minecraft.util.EnumChatFormatting;
import tv.twitch.broadcast.IngestServer;

public class GuiIngestServers
extends GuiScreen {
    private final GuiScreen field_152309_a;
    private String field_152310_f;
    private ServerList field_152311_g;

    public GuiIngestServers(GuiScreen p_i46312_1_) {
        this.field_152309_a = p_i46312_1_;
    }

    @Override
    public void initGui() {
        this.field_152310_f = I18n.format("options.stream.ingest.title", new Object[0]);
        this.field_152311_g = new ServerList(this.mc);
        if (!this.mc.getTwitchStream().func_152908_z()) {
            this.mc.getTwitchStream().func_152909_x();
        }
        this.buttonList.add(new GuiButton(1, width / 2 - 155, height - 24 - 6, 150, 20, I18n.format("gui.done", new Object[0])));
        this.buttonList.add(new GuiButton(2, width / 2 + 5, height - 24 - 6, 150, 20, I18n.format("options.stream.ingest.reset", new Object[0])));
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.field_152311_g.handleMouseInput();
    }

    @Override
    public void onGuiClosed() {
        if (this.mc.getTwitchStream().func_152908_z()) {
            this.mc.getTwitchStream().func_152932_y().func_153039_l();
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 1) {
                this.mc.displayGuiScreen(this.field_152309_a);
            } else {
                this.mc.gameSettings.streamPreferredServer = "";
                this.mc.gameSettings.saveOptions();
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.field_152311_g.drawScreen(mouseX, mouseY, partialTicks);
        GuiIngestServers.drawCenteredString(this.fontRendererObj, this.field_152310_f, width / 2, 20, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    class ServerList
    extends GuiSlot {
        public ServerList(Minecraft mcIn) {
            super(mcIn, width, height, 32, height - 35, (int)((double)mcIn.fontRendererObj.FONT_HEIGHT * 3.5));
            this.setShowSelectionBox(false);
        }

        @Override
        protected int getSize() {
            return this.mc.getTwitchStream().func_152925_v().length;
        }

        @Override
        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
            this.mc.gameSettings.streamPreferredServer = this.mc.getTwitchStream().func_152925_v()[slotIndex].serverUrl;
            this.mc.gameSettings.saveOptions();
        }

        @Override
        protected boolean isSelected(int slotIndex) {
            return this.mc.getTwitchStream().func_152925_v()[slotIndex].serverUrl.equals(this.mc.gameSettings.streamPreferredServer);
        }

        @Override
        protected void drawBackground() {
        }

        @Override
        protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn) {
            IngestServer ingestserver = this.mc.getTwitchStream().func_152925_v()[entryID];
            String s = ingestserver.serverUrl.replaceAll("\\{stream_key\\}", "");
            String s1 = (int)ingestserver.bitrateKbps + " kbps";
            String s2 = null;
            IngestServerTester ingestservertester = this.mc.getTwitchStream().func_152932_y();
            if (ingestservertester != null) {
                if (ingestserver == ingestservertester.func_153040_c()) {
                    s = (Object)((Object)EnumChatFormatting.GREEN) + s;
                    s1 = (int)(ingestservertester.func_153030_h() * 100.0f) + "%";
                } else if (entryID < ingestservertester.func_153028_p()) {
                    if (ingestserver.bitrateKbps == 0.0f) {
                        s1 = (Object)((Object)EnumChatFormatting.RED) + "Down!";
                    }
                } else {
                    s1 = (Object)((Object)EnumChatFormatting.OBFUSCATED) + "1234" + (Object)((Object)EnumChatFormatting.RESET) + " kbps";
                }
            } else if (ingestserver.bitrateKbps == 0.0f) {
                s1 = (Object)((Object)EnumChatFormatting.RED) + "Down!";
            }
            p_180791_2_ -= 15;
            if (this.isSelected(entryID)) {
                s2 = (Object)((Object)EnumChatFormatting.BLUE) + "(Preferred)";
            } else if (ingestserver.defaultServer) {
                s2 = (Object)((Object)EnumChatFormatting.GREEN) + "(Default)";
            }
            GuiIngestServers.this.drawString(GuiIngestServers.this.fontRendererObj, ingestserver.serverName, p_180791_2_ + 2, p_180791_3_ + 5, 0xFFFFFF);
            GuiIngestServers.this.drawString(GuiIngestServers.this.fontRendererObj, s, p_180791_2_ + 2, p_180791_3_ + ((GuiIngestServers)GuiIngestServers.this).fontRendererObj.FONT_HEIGHT + 5 + 3, 0x303030);
            GuiIngestServers.this.drawString(GuiIngestServers.this.fontRendererObj, s1, this.getScrollBarX() - 5 - GuiIngestServers.this.fontRendererObj.getStringWidth(s1), p_180791_3_ + 5, 0x808080);
            if (s2 != null) {
                GuiIngestServers.this.drawString(GuiIngestServers.this.fontRendererObj, s2, this.getScrollBarX() - 5 - GuiIngestServers.this.fontRendererObj.getStringWidth(s2), p_180791_3_ + 5 + 3 + ((GuiIngestServers)GuiIngestServers.this).fontRendererObj.FONT_HEIGHT, 0x808080);
            }
        }

        @Override
        protected int getScrollBarX() {
            return super.getScrollBarX() + 15;
        }
    }
}

