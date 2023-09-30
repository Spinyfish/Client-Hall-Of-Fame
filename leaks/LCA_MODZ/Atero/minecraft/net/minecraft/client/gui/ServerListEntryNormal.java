package net.minecraft.client.gui;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.design.font.Font;
import de.verschwiegener.atero.font.FontManager;
//import de.verschwiegener.atero.util.chat.ChatRenderer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

public class ServerListEntryNormal implements GuiListExtended.IGuiListEntry {
    private static final Logger logger = LogManager.getLogger();
    private static final ThreadPoolExecutor field_148302_b = new ScheduledThreadPoolExecutor(5, (new ThreadFactoryBuilder()).setNameFormat("Server Pinger #%d").setDaemon(true).build());
    private static final ResourceLocation UNKNOWN_SERVER = new ResourceLocation("textures/misc/unknown_server.png");
    private static final ResourceLocation SERVER_SELECTION_BUTTONS = new ResourceLocation("textures/gui/server_selection.png");
    private final GuiMultiplayer field_148303_c;
    private final Minecraft mc;
    private final ResourceLocation field_148306_i;
    private ServerData serverData;
    private String field_148299_g;
    private DynamicTexture field_148305_h;
    private long field_148298_f;

    private Font font;
   // private ChatRenderer chatRenderer;

    protected ServerListEntryNormal(GuiMultiplayer p_i45048_1_, ServerData p_i45048_2_) {
        this.field_148303_c = p_i45048_1_;
        this.serverData = p_i45048_2_;
        this.mc = Minecraft.getMinecraft();
        this.field_148306_i = new ResourceLocation("servers/" + p_i45048_2_.serverIP + "/icon");
        this.field_148305_h = (DynamicTexture) this.mc.getTextureManager().getTexture(this.field_148306_i);
        font = Management.instance.font;
     //   chatRenderer = new ChatRenderer();
    }

    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
        if (!this.serverData.field_78841_f) {
            this.serverData.field_78841_f = true;
            this.serverData.pingToServer = -2L;
            this.serverData.serverMOTD = "";
            this.serverData.populationInfo = "";
            field_148302_b.submit(new Runnable() {
                public void run() {
                    try {
                        ServerListEntryNormal.this.field_148303_c.getOldServerPinger().ping(ServerListEntryNormal.this.serverData);
                    } catch (UnknownHostException var2) {
                        ServerListEntryNormal.this.serverData.pingToServer = -1L;
                        ServerListEntryNormal.this.serverData.serverMOTD = EnumChatFormatting.DARK_RED + "Can\'t resolve hostname";
                    } catch (Exception var3) {
                        ServerListEntryNormal.this.serverData.pingToServer = -1L;
                        ServerListEntryNormal.this.serverData.serverMOTD = EnumChatFormatting.DARK_RED + "Can\'t connect to server.";
                    }
                }
            });
        }

        boolean flag = this.serverData.version > 47;
        boolean flag1 = this.serverData.version < 47;
        boolean flag2 = flag || flag1;
        //this.mc.fontRendererObj.drawString(this.field_148301_e.serverName, x + 32 + 3, y + 1, 16777215);
        FontManager.ROBOTOTHIN_20.drawString(this.serverData.serverName, (x + 32 + 3) * 2, (y + 1) * 2, 16777215,false);
        List<String> list = this.mc.fontRendererObj.listFormattedStringToWidth(this.serverData.serverMOTD, listWidth - 32 - 2);

        for (int i = 0; i < Math.min(list.size(), 2); ++i) {
			FontManager.ROBOTOTHIN_20.drawString((String)list.get(i), x + 32 + 3, y + 12 + this.mc.fontRendererObj.FONT_HEIGHT * i, 8421504,false);
           // chatRenderer.drawchat2((String) list.get(i), (x + 32 + 3) * 2, (y + 12 + this.mc.fontRendererObj.FONT_HEIGHT * i) * 2);
        }

        String s2 = flag2 ? EnumChatFormatting.DARK_RED + this.serverData.gameVersion : this.serverData.populationInfo;
        int j = this.mc.fontRendererObj.getStringWidth(s2);
		FontManager.ROBOTOTHIN_20.drawString(s2, x + listWidth - j - 15 - 2, y + 1, 8421504,false);
        int k = 0;
        String s = null;
        int l;
        String s1;

        if (flag2) {
            l = 5;
            s1 = flag ? "Client out of date!" : "Server out of date!";
            s = this.serverData.playerList;
        } else if (this.serverData.field_78841_f && this.serverData.pingToServer != -2L) {
            if (this.serverData.pingToServer < 0L) {
                l = 5;
            } else if (this.serverData.pingToServer < 150L) {
                l = 0;
            } else if (this.serverData.pingToServer < 300L) {
                l = 1;
            } else if (this.serverData.pingToServer < 600L) {
                l = 2;
            } else if (this.serverData.pingToServer < 1000L) {
                l = 3;
            } else {
                l = 4;
            }

            if (this.serverData.pingToServer < 0L) {
                s1 = "(no connection)";
            } else {
                s1 = this.serverData.pingToServer + "ms";
                s = this.serverData.playerList;
            }
        } else {
            k = 1;
            l = (int) (Minecraft.getSystemTime() / 100L + (long) (slotIndex * 2) & 7L);

            if (l > 4) {
                l = 8 - l;
            }

            s1 = "Pinging...";
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(Gui.icons);
        Gui.drawModalRectWithCustomSizedTexture(x + listWidth - 15, y, (float) (k * 10), (float) (176 + l * 8), 10, 8, 256.0F, 256.0F);

        if (this.serverData.getBase64EncodedIconData() != null && !this.serverData.getBase64EncodedIconData().equals(this.field_148299_g)) {
            this.field_148299_g = this.serverData.getBase64EncodedIconData();
            this.prepareServerIcon();
            this.field_148303_c.getServerList().saveServerList();
        }

        if (this.field_148305_h != null) {
            this.func_178012_a(x, y, this.field_148306_i);
        } else {
            this.func_178012_a(x, y, UNKNOWN_SERVER);
        }

        int i1 = mouseX - x;
        int j1 = mouseY - y;

        if (i1 >= listWidth - 15 && i1 <= listWidth - 5 && j1 >= 0 && j1 <= 8) {
            this.field_148303_c.setHoveringText(s1);
        } else if (i1 >= listWidth - j - 15 - 2 && i1 <= listWidth - 15 - 2 && j1 >= 0 && j1 <= 8) {
            this.field_148303_c.setHoveringText(s);
        }

        if (this.mc.gameSettings.touchscreen || isSelected) {
            this.mc.getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);
            Gui.drawRect(x, y, x + 32, y + 32, -1601138544);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int k1 = mouseX - x;
            int l1 = mouseY - y;

            if (this.func_178013_b()) {
                if (k1 < 32 && k1 > 16) {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                } else {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            }

            if (this.field_148303_c.func_175392_a(this, slotIndex)) {
                if (k1 < 16 && l1 < 16) {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                } else {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            }

            if (this.field_148303_c.func_175394_b(this, slotIndex)) {
                if (k1 < 16 && l1 > 16) {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                } else {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            }
        }
    }

    public void drawEntryNoPing(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, boolean showHovered) {
        {
	        /*if (!this.serverData.field_78841_f)
	        {
	            this.serverData.field_78841_f = true;
	            this.serverData.pingToServer = -2L;
	            this.serverData.serverMOTD = "";
	            this.serverData.populationInfo = "";
	            field_148302_b.submit(new Runnable()
	            {
	                public void run()
	                {
	                    try
	                    {
	                        ServerListEntryNormal.this.field_148303_c.getOldServerPinger().ping(ServerListEntryNormal.this.serverData);
	                    }
	                    catch (UnknownHostException var2)
	                    {
	                        ServerListEntryNormal.this.serverData.pingToServer = -1L;
	                        ServerListEntryNormal.this.serverData.serverMOTD = EnumChatFormatting.DARK_RED + "Can\'t resolve hostname";
	                    }
	                    catch (Exception var3)
	                    {
	                        ServerListEntryNormal.this.serverData.pingToServer = -1L;
	                        ServerListEntryNormal.this.serverData.serverMOTD = EnumChatFormatting.DARK_RED + "Can\'t connect to server.";
	                    }
	                }
	            });
	        }*/

            boolean flag = this.serverData.version > 47;
            boolean flag1 = this.serverData.version < 47;
            boolean flag2 = flag || flag1;
			FontManager.ROBOTOTHIN_20.drawString(this.serverData.serverName, (x + 32 + 3), (y + 1), 16777215,false);
            // this.mc.fontRendererObj.drawString(this.field_148301_e.serverName, x + 32 + 3, y + 1, 16777215);
            List<String> list = this.mc.fontRendererObj.listFormattedStringToWidth(this.serverData.serverMOTD, listWidth - 32 - 2);

            for (int i = 0; i < Math.min(list.size(), 2); ++i) {
               // chatRenderer.drawchat2((String) list.get(i), (x + 32 + 3) * 2, (y + 12 + this.mc.fontRendererObj.FONT_HEIGHT * i) * 2);
                FontManager.ROBOTOTHIN_20.drawString((String)list.get(i), x + 32 + 3, y + 12 + this.mc.fontRendererObj.FONT_HEIGHT * i, 8421504,false);
            }

            String s2 = flag2 ? EnumChatFormatting.DARK_RED + this.serverData.gameVersion : this.serverData.populationInfo;
            int j = this.mc.fontRendererObj.getStringWidth(s2);
			FontManager.ROBOTOTHIN_20.drawString(s2, x + listWidth - j - 15 - 2, y + 1, 8421504,false);
            int k = 0;
            String s = null;
            int l;
            String s1;

            if (flag2) {
                l = 5;
                s1 = flag ? "Client out of date!" : "Server out of date!";
                s = this.serverData.playerList;
            } else if (this.serverData.field_78841_f && this.serverData.pingToServer != -2L) {
                if (this.serverData.pingToServer < 0L) {
                    l = 5;
                } else if (this.serverData.pingToServer < 150L) {
                    l = 0;
                } else if (this.serverData.pingToServer < 300L) {
                    l = 1;
                } else if (this.serverData.pingToServer < 600L) {
                    l = 2;
                } else if (this.serverData.pingToServer < 1000L) {
                    l = 3;
                } else {
                    l = 4;
                }

                if (this.serverData.pingToServer < 0L) {
                    s1 = "(no connection)";
                } else {
                    s1 = this.serverData.pingToServer + "ms";
                    s = this.serverData.playerList;
                }
            } else {
                k = 1;
                l = (int) (Minecraft.getSystemTime() / 100L + (long) (slotIndex * 2) & 7L);

                if (l > 4) {
                    l = 8 - l;
                }

                s1 = "Pinging...";
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(Gui.icons);
            //Gui.drawModalRectWithCustomSizedTexture(x + listWidth - 15, y, (float)(k * 10), (float)(176 + l * 8), 10, 8, 256.0F, 256.0F);

            if (this.serverData.getBase64EncodedIconData() != null && !this.serverData.getBase64EncodedIconData().equals(this.field_148299_g)) {
                this.field_148299_g = this.serverData.getBase64EncodedIconData();
                this.prepareServerIcon();
                //this.field_148303_c.getServerList().saveServerList();
            }

            if (this.field_148305_h != null) {
                this.func_178012_a(x, y, this.field_148306_i);
            } else {
                this.func_178012_a(x, y, UNKNOWN_SERVER);
            }

            int i1 = mouseX - x;
            int j1 = mouseY - y;

            if (i1 >= listWidth - 15 && i1 <= listWidth - 5 && j1 >= 0 && j1 <= 8) {
                this.field_148303_c.setHoveringText(s1);
            } else if (i1 >= listWidth - j - 15 - 2 && i1 <= listWidth - 15 - 2 && j1 >= 0 && j1 <= 8) {
                if (showHovered) {
                    this.field_148303_c.setHoveringText(s);
                }
            }

            if (this.mc.gameSettings.touchscreen || isSelected) {
                this.mc.getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);
                Gui.drawRect(x, y, x + 32, y + 32, -1601138544);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                int k1 = mouseX - x;
                int l1 = mouseY - y;

                if (this.func_178013_b()) {
                    if (k1 < 32 && k1 > 16) {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                    } else {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                    }
                }

                if (this.field_148303_c.func_175392_a(this, slotIndex)) {
                    if (k1 < 16 && l1 < 16) {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                    } else {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                    }
                }

                if (this.field_148303_c.func_175394_b(this, slotIndex)) {
                    if (k1 < 16 && l1 > 16) {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                    } else {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                    }
                }
            }
        }
    }

    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, boolean showHovered) {
        {
            if (!this.serverData.field_78841_f) {
                this.serverData.field_78841_f = true;
                this.serverData.pingToServer = -2L;
                this.serverData.serverMOTD = "";
                this.serverData.populationInfo = "";
                field_148302_b.submit(new Runnable() {
                    public void run() {
                        try {
                            ServerListEntryNormal.this.field_148303_c.getOldServerPinger().ping(ServerListEntryNormal.this.serverData);
                        } catch (UnknownHostException var2) {
                            ServerListEntryNormal.this.serverData.pingToServer = -1L;
                            ServerListEntryNormal.this.serverData.serverMOTD = EnumChatFormatting.DARK_RED + "Can\'t resolve hostname";
                        } catch (Exception var3) {
                            ServerListEntryNormal.this.serverData.pingToServer = -1L;
                            ServerListEntryNormal.this.serverData.serverMOTD = EnumChatFormatting.DARK_RED + "Can\'t connect to server.";
                        }
                    }
                });
            }

            boolean flag = this.serverData.version > 47;
            boolean flag1 = this.serverData.version < 47;
            boolean flag2 = flag || flag1;
			FontManager.ROBOTOTHIN_20.drawString(this.serverData.serverName, (x + 32 + 3), (y + 1), 16777215,false);
            // this.mc.fontRendererObj.drawString(this.field_148301_e.serverName, x + 32 + 3, y + 1, 16777215);
            List<String> list = this.mc.fontRendererObj.listFormattedStringToWidth(this.serverData.serverMOTD, listWidth - 32 - 2);

            for (int i = 0; i < Math.min(list.size(), 2); ++i) {
               // chatRenderer.drawchat2((String) list.get(i), (x + 32 + 3) * 2, (y + 12 + this.mc.fontRendererObj.FONT_HEIGHT * i) * 2);
                FontManager.ROBOTOTHIN_20.drawString((String)list.get(i), x + 32 + 3, y + 12 + this.mc.fontRendererObj.FONT_HEIGHT * i, 8421504,false);
            }

            String s2 = flag2 ? EnumChatFormatting.DARK_RED + this.serverData.gameVersion : this.serverData.populationInfo;
            int j = this.mc.fontRendererObj.getStringWidth(s2);
			FontManager.ROBOTOTHIN_20.drawString(s2, x + listWidth - j - 15 - 2, y + 1, 8421504,false);
            int k = 0;
            String s = null;
            int l;
            String s1;

            if (flag2) {
                l = 5;
                s1 = flag ? "Client out of date!" : "Server out of date!";
                s = this.serverData.playerList;
            } else if (this.serverData.field_78841_f && this.serverData.pingToServer != -2L) {
                if (this.serverData.pingToServer < 0L) {
                    l = 5;
                } else if (this.serverData.pingToServer < 150L) {
                    l = 0;
                } else if (this.serverData.pingToServer < 300L) {
                    l = 1;
                } else if (this.serverData.pingToServer < 600L) {
                    l = 2;
                } else if (this.serverData.pingToServer < 1000L) {
                    l = 3;
                } else {
                    l = 4;
                }

                if (this.serverData.pingToServer < 0L) {
                    s1 = "(no connection)";
                } else {
                    s1 = this.serverData.pingToServer + "ms";
                    s = this.serverData.playerList;
                }
            } else {
                k = 1;
                l = (int) (Minecraft.getSystemTime() / 100L + (long) (slotIndex * 2) & 7L);

                if (l > 4) {
                    l = 8 - l;
                }

                s1 = "Pinging...";
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(Gui.icons);
            Gui.drawModalRectWithCustomSizedTexture(x + listWidth - 15, y, (float) (k * 10), (float) (176 + l * 8), 10, 8, 256.0F, 256.0F);

            if (this.serverData.getBase64EncodedIconData() != null && !this.serverData.getBase64EncodedIconData().equals(this.field_148299_g)) {
                this.field_148299_g = this.serverData.getBase64EncodedIconData();
                this.prepareServerIcon();
                this.field_148303_c.getServerList().saveServerList();
            }

            if (this.field_148305_h != null) {
                this.func_178012_a(x, y, this.field_148306_i);
            } else {
                this.func_178012_a(x, y, UNKNOWN_SERVER);
            }

            int i1 = mouseX - x;
            int j1 = mouseY - y;

            if (i1 >= listWidth - 15 && i1 <= listWidth - 5 && j1 >= 0 && j1 <= 8) {
                this.field_148303_c.setHoveringText(s1);
            } else if (i1 >= listWidth - j - 15 - 2 && i1 <= listWidth - 15 - 2 && j1 >= 0 && j1 <= 8) {
                if (showHovered) {
                    this.field_148303_c.setHoveringText(s);
                }
            }

            if (this.mc.gameSettings.touchscreen || isSelected) {
                this.mc.getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);
                Gui.drawRect(x, y, x + 32, y + 32, -1601138544);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                int k1 = mouseX - x;
                int l1 = mouseY - y;

                if (this.func_178013_b()) {
                    if (k1 < 32 && k1 > 16) {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                    } else {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                    }
                }

                if (this.field_148303_c.func_175392_a(this, slotIndex)) {
                    if (k1 < 16 && l1 < 16) {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                    } else {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                    }
                }

                if (this.field_148303_c.func_175394_b(this, slotIndex)) {
                    if (k1 < 16 && l1 > 16) {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                    } else {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                    }
                }
            }
        }
    }

    protected void func_178012_a(int p_178012_1_, int p_178012_2_, ResourceLocation p_178012_3_) {
        this.mc.getTextureManager().bindTexture(p_178012_3_);
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(p_178012_1_, p_178012_2_, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
        GlStateManager.disableBlend();
    }

    private boolean func_178013_b() {
        return true;
    }

    private void prepareServerIcon() {
        if (this.serverData.getBase64EncodedIconData() == null) {
            this.mc.getTextureManager().deleteTexture(this.field_148306_i);
            this.field_148305_h = null;
        } else {
            ByteBuf bytebuf = Unpooled.copiedBuffer((CharSequence) this.serverData.getBase64EncodedIconData(), Charsets.UTF_8);
            ByteBuf bytebuf1 = Base64.decode(bytebuf);
            BufferedImage bufferedimage;
            label101:
            {
                try {
                    bufferedimage = TextureUtil.readBufferedImage(new ByteBufInputStream(bytebuf1));
                    Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                    Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                    break label101;
                } catch (Throwable throwable) {
                    logger.error("Invalid icon for server " + this.serverData.serverName + " (" + this.serverData.serverIP + ")", throwable);
                    this.serverData.setBase64EncodedIconData((String) null);
                } finally {
                    bytebuf.release();
                    bytebuf1.release();
                }

                return;
            }

            if (this.field_148305_h == null) {
                this.field_148305_h = new DynamicTexture(bufferedimage.getWidth(), bufferedimage.getHeight());
                this.mc.getTextureManager().loadTexture(this.field_148306_i, this.field_148305_h);
            }

            bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), this.field_148305_h.getTextureData(), 0, bufferedimage.getWidth());
            this.field_148305_h.updateDynamicTexture();
        }
    }

    /**
     * Returns true if the mouse has been pressed on this control.
     */
    public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_,
                                int p_148278_6_) {
        if (p_148278_5_ <= 32) {
            if (p_148278_5_ < 32 && p_148278_5_ > 16 && this.func_178013_b()) {
                this.field_148303_c.selectServer(slotIndex);
                this.field_148303_c.connectToSelected();
                return true;
            }

            if (p_148278_5_ < 16 && p_148278_6_ < 16 && this.field_148303_c.func_175392_a(this, slotIndex)) {
                System.err.println("Up");
                this.field_148303_c.func_175391_a(this, slotIndex, GuiScreen.isShiftKeyDown());
                return true;
            }

            if (p_148278_5_ < 16 && p_148278_6_ > 16 && this.field_148303_c.func_175394_b(this, slotIndex)) {
                System.err.println("Down");
                this.field_148303_c.func_175393_b(this, slotIndex, GuiScreen.isShiftKeyDown());
                return true;
            }
        }

        this.field_148303_c.selectServer(slotIndex);
        //CustomGUISlotRenderer.selectServer(slotIndex);

        if (Minecraft.getSystemTime() - this.field_148298_f < 250L) {
            this.field_148303_c.connectToSelected();
        }

        this.field_148298_f = Minecraft.getSystemTime();
        return false;
    }

    public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
    }

    /**
     * Fired when the mouse button is released. Arguments: index, x, y, mouseEvent, relativeX, relativeY
     */
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
    }

    public ServerData getServerData() {
        return this.serverData;
    }

    public void setServerData(ServerData serverData) {
        this.serverData = serverData;
    }
}
