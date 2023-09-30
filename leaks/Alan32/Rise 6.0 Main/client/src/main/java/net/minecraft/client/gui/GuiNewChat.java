package net.minecraft.client.gui;

import com.alan.clients.Client;
import com.alan.clients.module.Module;
import com.alan.clients.module.impl.render.Interface;
import com.alan.clients.module.impl.render.UnlimitedChat;
import com.alan.clients.ui.ingame.GuiIngameCache;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.List;

public class GuiNewChat extends Gui {
    private static final Logger logger = LogManager.getLogger();
    private final Minecraft mc;
    private final List<String> sentMessages = Lists.newArrayList();
    private final List<ChatLine> chatLines = Lists.newArrayList();
    private final List<ChatLine> field_146253_i = Lists.newArrayList();
    private int scrollPos;
    private boolean isScrolled;

    public GuiNewChat(final Minecraft mcIn) {
        this.mc = mcIn;
    }

    public void drawChat(final int p_146230_1_) {
        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            final int i = this.getLineCount();
            boolean flag = false;
            int j = 0;
            final int k = this.field_146253_i.size();
            final float f = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;

            if (k > 0) {
                if (this.getChatOpen()) {
                    flag = true;
                }

                final float f1 = this.getChatScale();
                final int l = MathHelper.ceiling_float_int((float) this.getChatWidth() / f1);
                GlStateManager.pushMatrix();
                GlStateManager.translate(2.0F, 20.0F, 0.0F);
                GlStateManager.scale(f1, f1, 1.0F);

                for (int i1 = 0; i1 + this.scrollPos < this.field_146253_i.size() && i1 < i; ++i1) {
                    final ChatLine chatline = this.field_146253_i.get(i1 + this.scrollPos);

                    if (chatline != null) {
                        final int j1 = p_146230_1_ - chatline.getUpdatedCounter();

                        if (j1 < 200 || flag) {
                            double d0 = (double) j1 / 200.0D;
                            d0 = 1.0D - d0;
                            d0 = d0 * 10.0D;
                            d0 = MathHelper.clamp_double(d0, 0.0D, 1.0D);
                            d0 = d0 * d0;
                            int l1 = (int) (255.0D * d0);

                            if (flag) {
                                l1 = 255;
                            }

                            l1 = (int) ((float) l1 * f);
                            ++j;

                            if (l1 > 3) {
                                final Interface interfaceModule = Client.INSTANCE.getModuleManager().get(Interface.class);
                                final int j2 = -i1 * 9;
                                final String s = chatline.getChatComponent().getFormattedText();
                                drawRect(0, j2 - 9, interfaceModule != null && interfaceModule.isEnabled() && interfaceModule.limitChatWidth.getValue() ? Math.min(l + 4, this.mc.fontRendererObj.width(s) + 2) : l + 4, j2, 2130706432);
                            }
                        }
                    }
                }

                if (flag) {
                    final int k2 = this.mc.fontRendererObj.FONT_HEIGHT;
                    GlStateManager.translate(-3.0F, 0.0F, 0.0F);
                    final int l2 = k * k2 + k;
                    final int i3 = j * k2 + j;
                    final int j3 = this.scrollPos * i3 / k;
                    final int k1 = i3 * i3 / l2;

                    if (l2 != i3) {
                        final int l3 = this.isScrolled ? 13382451 : 3355562;
                        drawRect(0, -j3, 2, -j3 - k1, l3);
                        drawRect(2, -j3, 1, -j3 - k1, 13421772);
                    }
                }

                GlStateManager.popMatrix();
            }
        }
    }

    public void drawChatLimitedFrameRate(final int p_146230_1_) {
        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            final int i = this.getLineCount();
            boolean flag = false;
            final int k = this.field_146253_i.size();
            final float f = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;

            if (k > 0) {
                if (this.getChatOpen()) {
                    flag = true;
                }

                final float f1 = this.getChatScale();
                GlStateManager.pushMatrix();
                GlStateManager.translate(2.0F, 20.0F, 0.0F);
                GlStateManager.scale(f1, f1, 1.0F);

                for (int i1 = 0; i1 + this.scrollPos < this.field_146253_i.size() && i1 < i; ++i1) {
                    final ChatLine chatline = this.field_146253_i.get(i1 + this.scrollPos);

                    if (chatline != null) {
                        final int j1 = p_146230_1_ - chatline.getUpdatedCounter();

                        if (j1 < 200 || flag) {
                            double d0 = (double) j1 / 200.0D;
                            d0 = 1.0D - d0;
                            d0 = d0 * 10.0D;
                            d0 = MathHelper.clamp_double(d0, 0.0D, 1.0D);
                            d0 = d0 * d0;
                            int l1 = (int) (255.0D * d0);

                            if (flag) {
                                l1 = 255;
                            }

                            l1 = (int) ((float) l1 * f);

                            if (l1 > 4) {
                                final int j2 = -i1 * 9;
                                final String s = chatline.getChatComponent().getFormattedText();
                                GlStateManager.enableBlend();
                                this.mc.fontRendererObj.drawStringWithShadow(s, 0, (float) (j2 - 7), 16777215);
                                GlStateManager.disableAlpha();
                            }
                        }
                    }
                }

                GlStateManager.popMatrix();
            }
        }
    }

    /**
     * Clears the chat.
     */
    public void clearChatMessages() {
        this.field_146253_i.clear();
        this.chatLines.clear();
        this.sentMessages.clear();
    }

    public void printChatMessage(final IChatComponent p_146227_1_) {
        this.printChatMessageWithOptionalDeletion(p_146227_1_, 0);
    }

    /**
     * prints the ChatComponent to Chat. If the ID is not 0, deletes an existing Chat Line of that ID from the GUI
     */
    public void printChatMessageWithOptionalDeletion(final IChatComponent p_146234_1_, final int p_146234_2_) {
        this.setChatLine(p_146234_1_, p_146234_2_, this.mc.ingameGUI.getUpdateCounter(), false);
        logger.info("[CHAT] " + p_146234_1_.getUnformattedText());
        GuiIngameCache.dirty = true;
    }

    private void setChatLine(final IChatComponent p_146237_1_, final int p_146237_2_, final int p_146237_3_, final boolean p_146237_4_) {
        if (p_146237_2_ != 0) {
            this.deleteChatLine(p_146237_2_);
        }

        final int i = MathHelper.floor_float((float) this.getChatWidth() / this.getChatScale());
        final List<IChatComponent> list = GuiUtilRenderComponents.func_178908_a(p_146237_1_, i, this.mc.fontRendererObj, false, false);
        final boolean flag = this.getChatOpen();

        for (final IChatComponent ichatcomponent : list) {
            if (flag && this.scrollPos > 0) {
                this.isScrolled = true;
                this.scroll(1);
            }

            this.field_146253_i.add(0, new ChatLine(p_146237_3_, ichatcomponent, p_146237_2_));
        }

        final Module unlimitedChat = Client.INSTANCE.getModuleManager().get(UnlimitedChat.class);
        int maxSize = unlimitedChat == null || !unlimitedChat.isEnabled() ? 100 : 10000;

        while (this.field_146253_i.size() > maxSize) {
            this.field_146253_i.remove(this.field_146253_i.size() - 1);
        }

        if (!p_146237_4_) {
            this.chatLines.add(0, new ChatLine(p_146237_3_, p_146237_1_, p_146237_2_));

            while (this.chatLines.size() > maxSize) {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
        }
    }

    public void refreshChat() {
        this.field_146253_i.clear();
        this.resetScroll();

        for (int i = this.chatLines.size() - 1; i >= 0; --i) {
            final ChatLine chatline = this.chatLines.get(i);
            this.setChatLine(chatline.getChatComponent(), chatline.getChatLineID(), chatline.getUpdatedCounter(), true);
        }
    }

    public List<String> getSentMessages() {
        return this.sentMessages;
    }

    /**
     * Adds this string to the list of sent messages, for recall using the up/down arrow keys
     */
    public void addToSentMessages(final String p_146239_1_) {
        if (this.sentMessages.isEmpty() || !this.sentMessages.get(this.sentMessages.size() - 1).equals(p_146239_1_)) {
            this.sentMessages.add(p_146239_1_);
        }
    }

    /**
     * Resets the chat scroll (executed when the GUI is closed, among others)
     */
    public void resetScroll() {
        this.scrollPos = 0;
        this.isScrolled = false;
    }

    /**
     * Scrolls the chat by the given number of lines.
     */
    public void scroll(final int p_146229_1_) {
        this.scrollPos += p_146229_1_;
        final int i = this.field_146253_i.size();

        if (this.scrollPos > i - this.getLineCount()) {
            this.scrollPos = i - this.getLineCount();
        }

        if (this.scrollPos <= 0) {
            this.scrollPos = 0;
            this.isScrolled = false;
        }
    }

    /**
     * Gets the chat component under the mouse
     */
    public IChatComponent getChatComponent(final int p_146236_1_, final int p_146236_2_) {
        if (!this.getChatOpen()) {
            return null;
        } else {
            final ScaledResolution scaledresolution = new ScaledResolution(this.mc);
            final int i = scaledresolution.getScaleFactor();
            final float f = this.getChatScale();
            int j = p_146236_1_ / i - 3;
            int k = p_146236_2_ / i - 27;
            j = MathHelper.floor_float((float) j / f);
            k = MathHelper.floor_float((float) k / f);

            if (j >= 0 && k >= 0) {
                final int l = Math.min(this.getLineCount(), this.field_146253_i.size());

                if (j <= MathHelper.floor_float((float) this.getChatWidth() / this.getChatScale()) && k < this.mc.fontRendererObj.FONT_HEIGHT * l + l) {
                    final int i1 = k / this.mc.fontRendererObj.FONT_HEIGHT + this.scrollPos;

                    if (i1 >= 0 && i1 < this.field_146253_i.size()) {
                        final ChatLine chatline = this.field_146253_i.get(i1);
                        int j1 = 0;

                        for (final IChatComponent ichatcomponent : chatline.getChatComponent()) {
                            if (ichatcomponent instanceof ChatComponentText) {
                                j1 += this.mc.fontRendererObj.width(GuiUtilRenderComponents.func_178909_a(((ChatComponentText) ichatcomponent).getChatComponentText_TextValue(), false));

                                if (j1 > j) {
                                    return ichatcomponent;
                                }
                            }
                        }
                    }

                }
                return null;
            } else {
                return null;
            }
        }
    }

    /**
     * Returns true if the chat GUI is open
     */
    public boolean getChatOpen() {
        return this.mc.currentScreen instanceof GuiChat;
    }

    /**
     * finds and deletes a Chat line by ID
     */
    public void deleteChatLine(final int p_146242_1_) {
        Iterator<ChatLine> iterator = this.field_146253_i.iterator();

        while (iterator.hasNext()) {
            final ChatLine chatline = iterator.next();

            if (chatline.getChatLineID() == p_146242_1_) {
                iterator.remove();
            }
        }

        iterator = this.chatLines.iterator();

        while (iterator.hasNext()) {
            final ChatLine chatline1 = iterator.next();

            if (chatline1.getChatLineID() == p_146242_1_) {
                iterator.remove();
                break;
            }
        }
    }

    public int getChatWidth() {
        return calculateChatboxWidth(this.mc.gameSettings.chatWidth);
    }

    public int getChatHeight() {
        return calculateChatboxHeight(this.getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused);
    }

    /**
     * Returns the chatscale from mc.gameSettings.chatScale
     */
    public float getChatScale() {
        return this.mc.gameSettings.chatScale;
    }

    public static int calculateChatboxWidth(final float p_146233_0_) {
        final int i = 320;
        final int j = 40;
        return MathHelper.floor_float(p_146233_0_ * (float) (i - j) + (float) j);
    }

    public static int calculateChatboxHeight(final float p_146243_0_) {
        final int i = 180;
        final int j = 20;
        return MathHelper.floor_float(p_146243_0_ * (float) (i - j) + (float) j);
    }

    public int getLineCount() {
        return this.getChatHeight() / 9;
    }
}
