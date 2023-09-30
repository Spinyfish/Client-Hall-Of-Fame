package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wtf.automn.Automn;
import wtf.automn.fontrenderer.ClientFont;
import wtf.automn.fontrenderer.GlyphPageFontRenderer;
import wtf.automn.module.ModuleManager;
import wtf.automn.module.impl.visual.ModuleBloom;
import wtf.automn.module.impl.visual.ModuleBlur;
import wtf.automn.module.impl.visual.ModuleShadow;
import wtf.automn.module.settings.SettingBoolean;
import wtf.automn.utils.render.RenderUtils;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

public class GuiNewChat extends Gui {
    private final GlyphPageFontRenderer chatFont = ClientFont.font(20, "Calibri", true);
    private static final Logger logger = LogManager.getLogger();
    private final Minecraft mc;
    private final List<String> sentMessages = Lists.newArrayList();
    private final List<ChatLine> chatLines = Lists.newArrayList();
    private final List<ChatLine> drawnChatLines = Lists.newArrayList();
    private int scrollPos;
    private boolean isScrolled;

    public GuiNewChat(final Minecraft mcIn) {
        this.mc = mcIn;
    }

    public static int calculateChatboxWidth(final float scale) {
        final int i = 320;
        final int j = 40;
        return MathHelper.floor_float(scale * (float) (i - j) + (float) j);
    }

    public static int calculateChatboxHeight(final float scale) {
        final int i = 180;
        final int j = 20;
        return MathHelper.floor_float(scale * (float) (i - j) + (float) j);
    }

    public void drawChat(final int updateCounter) {
        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            final int i = this.getLineCount();
            boolean flag = false;
            int j = 0;
            final int k = this.drawnChatLines.size();
            final float f = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;

            if (k > 0) {
                if (this.getChatOpen()) flag = true;

                final float f1 = this.getChatScale();
                final int l = MathHelper.ceiling_float_int((float) this.getChatWidth() / f1);
                GlStateManager.pushMatrix();
                GlStateManager.translate(2.0F, 20.0F, 0.0F);
                GlStateManager.scale(f1, f1, 1.0F);

                for (int i1 = 0; i1 + this.scrollPos < this.drawnChatLines.size() && i1 < i; ++i1) {
                    final ChatLine chatline = this.drawnChatLines.get(i1 + this.scrollPos);

                    if (chatline != null) {
                        final int j1 = updateCounter - chatline.getUpdatedCounter();

                        if (j1 < 200 || flag) {
                            double d0 = (double) j1 / 200.0D;
                            d0 = 1.0D - d0;
                            d0 = d0 * 10.0D;
                            d0 = MathHelper.clamp_double(d0, 0.0D, 1.0D);
                            d0 = d0 * d0;
                            int l1 = (int) (255.0D * d0);

                            if (flag) l1 = 255;

                            l1 = (int) ((float) l1 * f);
                            ++j;

                            if (l1 > 3) {
                                final int i2 = 0;
                                final int j2 = -i1 * 9;
                                final int finalL = l1;
                                ModuleManager mm = Automn.instance().moduleManager();
                               if(mm.customChat.enabled()){
                                   if(mm.customChat.shadow.getBoolean() && mm.shadow.enabled()) {
                                       ModuleShadow.drawShadow(() -> RenderUtils.drawRoundedRect(i2, j2 - 14, i2 + l + 4, 18, 3, finalL / 2 << 24), false);
                                   }
                                   if(mm.customChat.glow.getBoolean() && mm.glow.enabled()){
                                       ModuleBloom.drawGlow(() -> RenderUtils.drawRoundedRect(i2, j2 - 14, i2 + l + 4, 18, 3, Color.blue.getRGB()), false);

                                   }
                                }
                               if(mm.customChat.rounded.getBoolean()) {
                                   ModuleBlur.drawBlurred(() -> RenderUtils.drawRoundedRect(i2, j2 - 14, i2 + l + 4, 18, 3, finalL / 2 << 24), false);
                               }else{
                                   drawRect(i2, j2 - 5, i2 + l + 4, j2 + 4, l1 / 2 << 24);
                               }


                                final String s = chatline.getChatComponent().getFormattedText();
                                GlStateManager.enableBlend();
                                if (((SettingBoolean) (Automn.instance().moduleManager().getModule("hud").getSetting("chatfont"))).getTrueValue())
                                    this.chatFont.drawString(s, (float) i2, (float) (j2 - 8), 16777215 + (l1 << 24));
                                else
                                    this.mc.fontRendererObj.drawStringWithShadow(s, (float) i2, (float) (j2 - 8), 16777215 + (l1 << 24));
                                GlStateManager.disableAlpha();
                                GlStateManager.disableBlend();
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
                        final int k3 = j3 > 0 ? 170 : 96;
                        final int l3 = this.isScrolled ? 13382451 : 3355562;
                        ModuleBlur.drawBlurred(() -> drawRect(0, -j3, 2, -j3 - k1, l3 + (k3 << 24)), false);
                        ModuleBlur.drawBlurred(() -> drawRect(2, -j3, 1, -j3 - k1, 13421772 + (k3 << 24)), false);
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
        this.drawnChatLines.clear();
        this.chatLines.clear();
        this.sentMessages.clear();
    }

    public void printChatMessage(final IChatComponent chatComponent) {
        this.printChatMessageWithOptionalDeletion(chatComponent, 0);
    }

    /**
     * prints the ChatComponent to Chat. If the ID is not 0, deletes an existing Chat Line of that ID from the GUI
     */
    public void printChatMessageWithOptionalDeletion(final IChatComponent chatComponent, final int chatLineId) {
        this.setChatLine(chatComponent, chatLineId, this.mc.ingameGUI.getUpdateCounter(), false);
        logger.info("[CHAT] " + chatComponent.getUnformattedText());
    }

    private void setChatLine(final IChatComponent chatComponent, final int chatLineId, final int updateCounter, final boolean displayOnly) {
        if (chatLineId != 0) this.deleteChatLine(chatLineId);

        final int i = MathHelper.floor_float((float) this.getChatWidth() / this.getChatScale());
        final List<IChatComponent> list = GuiUtilRenderComponents.splitText(chatComponent, i, this.mc.fontRendererObj, false, false);
        final boolean flag = this.getChatOpen();

        for (final IChatComponent ichatcomponent : list) {
            if (flag && this.scrollPos > 0) {
                this.isScrolled = true;
                this.scroll(1);
            }

            this.drawnChatLines.add(0, new ChatLine(updateCounter, ichatcomponent, chatLineId));
        }

        while (this.drawnChatLines.size() > 100) this.drawnChatLines.remove(this.drawnChatLines.size() - 1);

        if (!displayOnly) {
            this.chatLines.add(0, new ChatLine(updateCounter, chatComponent, chatLineId));

            while (this.chatLines.size() > 100) this.chatLines.remove(this.chatLines.size() - 1);
        }
    }

    public void refreshChat() {
        this.drawnChatLines.clear();
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
     *
     * @param message The message to add in the sendMessage List
     */
    public void addToSentMessages(final String message) {
        if (this.sentMessages.isEmpty() || !this.sentMessages.get(this.sentMessages.size() - 1).equals(message))
            this.sentMessages.add(message);
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
     *
     * @param amount The amount to scroll
     */
    public void scroll(final int amount) {
        this.scrollPos += amount;
        final int i = this.drawnChatLines.size();

        if (this.scrollPos > i - this.getLineCount()) this.scrollPos = i - this.getLineCount();

        if (this.scrollPos <= 0) {
            this.scrollPos = 0;
            this.isScrolled = false;
        }
    }

    /**
     * Gets the chat component under the mouse
     *
     * @param mouseX The x position of the mouse
     * @param mouseY The y position of the mouse
     */
    public IChatComponent getChatComponent(final int mouseX, final int mouseY) {
        if (!this.getChatOpen()) return null;
        else {
            final ScaledResolution scaledresolution = new ScaledResolution(this.mc);
            final int i = scaledresolution.getScaleFactor();
            final float f = this.getChatScale();
            int j = mouseX / i - 3;
            int k = mouseY / i - 27;
            j = MathHelper.floor_float((float) j / f);
            k = MathHelper.floor_float((float) k / f);

            if (j >= 0 && k >= 0) {
                final int l = Math.min(this.getLineCount(), this.drawnChatLines.size());

                if (j <= MathHelper.floor_float((float) this.getChatWidth() / this.getChatScale()) && k < this.mc.fontRendererObj.FONT_HEIGHT * l + l) {
                    final int i1 = k / this.mc.fontRendererObj.FONT_HEIGHT + this.scrollPos;

                    if (i1 >= 0 && i1 < this.drawnChatLines.size()) {
                        final ChatLine chatline = this.drawnChatLines.get(i1);
                        int j1 = 0;

                        for (final IChatComponent ichatcomponent : chatline.getChatComponent())
                            if (ichatcomponent instanceof ChatComponentText) {
                                j1 += this.mc.fontRendererObj.getStringWidth(GuiUtilRenderComponents.func_178909_a(((ChatComponentText) ichatcomponent).getChatComponentText_TextValue(), false));

                                if (j1 > j) return ichatcomponent;
                            }
                    }

                    return null;
                } else return null;
            } else return null;
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
     *
     * @param id The ChatLine's id to delete
     */
    public void deleteChatLine(final int id) {
        Iterator<ChatLine> iterator = this.drawnChatLines.iterator();

        while (iterator.hasNext()) {
            final ChatLine chatline = iterator.next();

            if (chatline.getChatLineID() == id) iterator.remove();
        }

        iterator = this.chatLines.iterator();

        while (iterator.hasNext()) {
            final ChatLine chatline1 = iterator.next();

            if (chatline1.getChatLineID() == id) {
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

    public int getLineCount() {
        return this.getChatHeight() / 9;
    }
}
