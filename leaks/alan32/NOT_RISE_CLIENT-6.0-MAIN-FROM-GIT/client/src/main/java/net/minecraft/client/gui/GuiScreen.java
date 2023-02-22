package net.minecraft.client.gui;

import com.alan.clients.Client;
import com.alan.clients.newevent.impl.input.GuiClickEvent;
import com.alan.clients.newevent.impl.input.GuiMouseReleaseEvent;
import com.alan.clients.util.font.impl.minecraft.FontRenderer;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityList;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class GuiScreen extends Gui implements GuiYesNoCallback, InstanceAccess {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<String> PROTOCOLS = Sets.newHashSet("http", "https");
    private static final Splitter NEWLINE_SPLITTER = Splitter.on('\n');

    /**
     * Reference to the Minecraft object.
     */

    /**
     * Holds a instance of RenderItem, used to draw the achievement icons on screen (is based on ItemStack)
     */
    protected RenderItem itemRender;

    /**
     * The width of the screen object.
     */
    public int width;

    /**
     * The height of the screen object.
     */
    public int height;
    protected List<GuiButton> buttonList = Lists.newArrayList();
    protected List<GuiLabel> labelList = Lists.newArrayList();
    public boolean allowUserInput;

    /**
     * The FontRenderer used by GuiScreen
     */
    protected FontRenderer fontRendererObj;

    /**
     * The button that was just pressed.
     */
    private GuiButton selectedButton;
    private int eventButton;
    private long lastMouseEvent;

    /**
     * Incremented when the game is in touchscreen mode and the screen is tapped, decremented if the screen isn't
     * tapped. Does not appear to be used.
     */
    private int touchValue;
    private URI clickedLinkURI;

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        Minecraft.getMinecraft().timeScreen.reset();

        // Do not replace with an enhanced for loop this can cause null pointer exceptions
        for (int i = 0; i < this.buttonList.size(); ++i) {
            this.buttonList.get(i).drawButton(this.mc, mouseX, mouseY);
        }

        for (int j = 0; j < this.labelList.size(); ++j) {
            this.labelList.get(j).drawLabel(this.mc, mouseX, mouseY);
        }
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);

            if (this.mc.currentScreen == null) {
                this.mc.setIngameFocus();
            }
        }
    }

    /**
     * Returns a string stored in the system clipboard.
     */
    public static String getClipboardString() {
        try {
            final Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) transferable.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (final Exception var1) {
        }

        return "";
    }

    /**
     * Stores the given string in the system clipboard
     */
    public static void setClipboardString(final String copyText) {
        if (!StringUtils.isEmpty(copyText)) {
            try {
                final StringSelection stringselection = new StringSelection(copyText);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringselection, null);
            } catch (final Exception var2) {
            }
        }
    }

    protected void renderToolTip(final ItemStack stack, final int x, final int y) {
        final List<String> list = stack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

        for (int i = 0; i < list.size(); ++i) {
            if (i == 0) {
                list.set(i, stack.getRarity().rarityColor + list.get(i));
            } else {
                list.set(i, EnumChatFormatting.GRAY + list.get(i));
            }
        }

        this.drawHoveringText(list, x, y);
    }

    /**
     * Draws the text when mouse is over creative inventory tab. Params: current creative tab to be checked, current
     * mouse x position, current mouse y position.
     */
    protected void drawCreativeTabHoveringText(final String tabName, final int mouseX, final int mouseY) {
        this.drawHoveringText(Collections.singletonList(tabName), mouseX, mouseY);
    }

    /**
     * Draws a List of strings as a tooltip. Every entry is drawn on a seperate line.
     */
    protected void drawHoveringText(final List<String> textLines, final int x, final int y) {
        if (!textLines.isEmpty()) {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int i = 0;

            for (final String s : textLines) {
                final int j = this.fontRendererObj.width(s);

                if (j > i) {
                    i = j;
                }
            }

            int l1 = x + 12;
            int i2 = y - 12;
            int k = 8;

            if (textLines.size() > 1) {
                k += 2 + (textLines.size() - 1) * 10;
            }

            if (l1 + i > this.width) {
                l1 -= 28 + i;
            }

            if (i2 + k + 6 > this.height) {
                i2 = this.height - k - 6;
            }

            zLevel = 300.0F;
            this.itemRender.zLevel = 300.0F;
            final int l = -267386864;
            this.drawGradientRect(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, l, l);
            this.drawGradientRect(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, l, l);
            this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, l, l);
            this.drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, l, l);
            this.drawGradientRect(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, l, l);
            final int i1 = 1347420415;
            final int j1 = (i1 & 16711422) >> 1 | i1 & -16777216;
            this.drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, i1, j1);
            this.drawGradientRect(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, i1, j1);
            this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, i1, i1);
            this.drawGradientRect(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, j1, j1);

            for (int k1 = 0; k1 < textLines.size(); ++k1) {
                final String s1 = textLines.get(k1);
                this.fontRendererObj.drawStringWithShadow(s1, (float) l1, (float) i2, -1);

                if (k1 == 0) {
                    i2 += 2;
                }

                i2 += 10;
            }

            zLevel = 0.0F;
            this.itemRender.zLevel = 0.0F;
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    /**
     * Draws the hover event specified by the given chat component
     */
    protected void handleComponentHover(final IChatComponent p_175272_1_, final int p_175272_2_, final int p_175272_3_) {
        if (p_175272_1_ != null && p_175272_1_.getChatStyle().getChatHoverEvent() != null) {
            final HoverEvent hoverevent = p_175272_1_.getChatStyle().getChatHoverEvent();

            if (hoverevent.getAction() == HoverEvent.Action.SHOW_ITEM) {
                ItemStack itemstack = null;

                try {
                    final NBTTagCompound nbtbase = JsonToNBT.getTagFromJson(hoverevent.getValue().getUnformattedText());

                    if (nbtbase != null) {
                        itemstack = ItemStack.loadItemStackFromNBT(nbtbase);
                    }
                } catch (final NBTException var11) {
                }

                if (itemstack != null) {
                    this.renderToolTip(itemstack, p_175272_2_, p_175272_3_);
                } else {
                    this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Item!", p_175272_2_, p_175272_3_);
                }
            } else if (hoverevent.getAction() == HoverEvent.Action.SHOW_ENTITY) {
                if (this.mc.gameSettings.advancedItemTooltips) {
                    try {
                        final NBTTagCompound nbtbase1 = JsonToNBT.getTagFromJson(hoverevent.getValue().getUnformattedText());

                        if (nbtbase1 != null) {
                            final List<String> list1 = Lists.newArrayList();
                            final NBTTagCompound nbttagcompound = nbtbase1;
                            list1.add(nbttagcompound.getString("name"));

                            if (nbttagcompound.hasKey("type", 8)) {
                                final String s = nbttagcompound.getString("type");
                                list1.add("Type: " + s + " (" + EntityList.getIDFromString(s) + ")");
                            }

                            list1.add(nbttagcompound.getString("id"));
                            this.drawHoveringText(list1, p_175272_2_, p_175272_3_);
                        } else {
                            this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", p_175272_2_, p_175272_3_);
                        }
                    } catch (final NBTException var10) {
                        this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", p_175272_2_, p_175272_3_);
                    }
                }
            } else if (hoverevent.getAction() == HoverEvent.Action.SHOW_TEXT) {
                this.drawHoveringText(NEWLINE_SPLITTER.splitToList(hoverevent.getValue().getFormattedText()), p_175272_2_, p_175272_3_);
            } else if (hoverevent.getAction() == HoverEvent.Action.SHOW_ACHIEVEMENT) {
                final StatBase statbase = StatList.getOneShotStat(hoverevent.getValue().getUnformattedText());

                if (statbase != null) {
                    final IChatComponent ichatcomponent = statbase.getStatName();
                    final IChatComponent ichatcomponent1 = new ChatComponentTranslation("stats.tooltip.type." + (statbase.isAchievement() ? "achievement" : "statistic"));
                    ichatcomponent1.getChatStyle().setItalic(true);
                    final String s1 = statbase instanceof Achievement ? ((Achievement) statbase).getDescription() : null;
                    final List<String> list = Lists.newArrayList(ichatcomponent.getFormattedText(), ichatcomponent1.getFormattedText());

                    if (s1 != null) {
                        list.addAll(this.fontRendererObj.listFormattedStringToWidth(s1, 150));
                    }

                    this.drawHoveringText(list, p_175272_2_, p_175272_3_);
                } else {
                    this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid statistic/achievement!", p_175272_2_, p_175272_3_);
                }
            }

            GlStateManager.disableLighting();
        }
    }

    /**
     * Sets the text of the chat
     *
     * @param newChatText     The new chat text to be set
     * @param shouldOverwrite Determines if the text currently in the chat should be overwritten or appended
     */
    protected void setText(final String newChatText, final boolean shouldOverwrite) {
    }

    /**
     * Executes the click event specified by the given chat component
     */
    protected boolean handleComponentClick(final IChatComponent p_175276_1_) {
        if (p_175276_1_ != null) {
            final ClickEvent clickevent = p_175276_1_.getChatStyle().getChatClickEvent();

            if (isShiftKeyDown()) {
                if (p_175276_1_.getChatStyle().getInsertion() != null) {
                    this.setText(p_175276_1_.getChatStyle().getInsertion(), false);
                }
            } else if (clickevent != null) {
                if (clickevent.getAction() == ClickEvent.Action.OPEN_URL) {
                    if (!this.mc.gameSettings.chatLinks) {
                        return false;
                    }

                    try {
                        final URI uri = new URI(clickevent.getValue());
                        final String s = uri.getScheme();

                        if (s == null) {
                            throw new URISyntaxException(clickevent.getValue(), "Missing protocol");
                        }

                        if (!PROTOCOLS.contains(s.toLowerCase())) {
                            throw new URISyntaxException(clickevent.getValue(), "Unsupported protocol: " + s.toLowerCase());
                        }

                        if (this.mc.gameSettings.chatLinksPrompt) {
                            this.clickedLinkURI = uri;
                            this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, clickevent.getValue(), 31102009, false));
                        } else {
                            this.openWebLink(uri);
                        }
                    } catch (final URISyntaxException urisyntaxexception) {
                        LOGGER.error("Can't open url for " + clickevent, urisyntaxexception);
                    }
                } else if (clickevent.getAction() == ClickEvent.Action.OPEN_FILE) {
                    final URI uri1 = (new File(clickevent.getValue())).toURI();
                    this.openWebLink(uri1);
                } else if (clickevent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
                    this.setText(clickevent.getValue(), true);
                } else if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                    this.sendChatMessage(clickevent.getValue(), false);
                } else {
                    LOGGER.error("Don't know how to handle " + clickevent);
                }

                return true;
            }

        }
        return false;
    }

    public void sendChatMessage(final String msg) {
        this.sendChatMessage(msg, true);
    }

    public void sendChatMessage(final String msg, final boolean addToChat) {
        if (addToChat) {
            this.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
        }

        this.mc.thePlayer.sendChatMessage(msg);
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        GuiClickEvent guiClickEvent = new GuiClickEvent(mouseX, mouseY, mouseButton);
        Client.INSTANCE.getEventBus().handle(guiClickEvent);

        if (mouseButton == 0) {
            for (int i = 0; i < this.buttonList.size(); ++i) {
                final GuiButton guibutton = this.buttonList.get(i);

                if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
                    this.selectedButton = guibutton;
                    guibutton.playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(guibutton);
                }
            }
        }
    }

    /**
     * Called when a mouse button is released.  Args : mouseX, mouseY, releaseButton
     */
    protected void mouseReleased(final int mouseX, final int mouseY, final int state) {
        GuiMouseReleaseEvent guiMouseReleaseEvent = new GuiMouseReleaseEvent();
        Client.INSTANCE.getEventBus().handle(guiMouseReleaseEvent);

        if (this.selectedButton != null && state == 0) {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }
    }

    /**
     * Called when a mouse button is pressed and the mouse is moved around. Parameters are : mouseX, mouseY,
     * lastButtonClicked & timeSinceMouseClick.
     */
    protected void mouseClickMove(final int mouseX, final int mouseY, final int clickedMouseButton, final long timeSinceLastClick) {
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(final GuiButton button) throws IOException {
    }

    /**
     * Causes the screen to lay out its subcomponents again. This is the equivalent of the Java call
     * Container.validate()
     */
    public void setWorldAndResolution(final Minecraft mc, final int width, final int height) {
        this.itemRender = mc.getRenderItem();
        this.fontRendererObj = mc.fontRendererObj;
        this.width = width;
        this.height = height;
        this.buttonList.clear();
        this.initGui();
    }

    public void func_183500_a(final int p_183500_1_, final int p_183500_2_) {
        this.width = p_183500_1_;
        this.height = p_183500_2_;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
    }

    /**
     * Delegates mouse and keyboard input.
     */
    public void handleInput() throws IOException {
        if (Mouse.isCreated()) {
            while (Mouse.next()) {
                this.handleMouseInput();
            }
        }

        if (Keyboard.isCreated()) {
            while (Keyboard.next()) {
                this.handleKeyboardInput();
            }
        }
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException {
        final int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
        final int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        final int k = Mouse.getEventButton();

        if (Mouse.getEventButtonState()) {
            if (this.mc.gameSettings.touchscreen && this.touchValue++ > 0) {
                return;
            }

            this.eventButton = k;
            this.lastMouseEvent = Minecraft.getSystemTime();
            this.mouseClicked(i, j, this.eventButton);
        } else if (k != -1) {
            if (this.mc.gameSettings.touchscreen && --this.touchValue > 0) {
                return;
            }

            this.eventButton = -1;
            this.mouseReleased(i, j, k);
        } else if (this.eventButton != -1 && this.lastMouseEvent > 0L) {
            final long l = Minecraft.getSystemTime() - this.lastMouseEvent;
            this.mouseClickMove(i, j, this.eventButton, l);
        }
    }

    /**
     * Handles keyboard input.
     */
    public void handleKeyboardInput() throws IOException {
        if (Keyboard.getEventKeyState()) {
            this.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
        }

        this.mc.dispatchKeypresses();
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed() {
    }

    /**
     * Draws either a gradient over the background screen (when it exists) or a flat gradient over background.png
     */
    public void drawDefaultBackground() {
        this.drawWorldBackground(0);
    }

    public void drawWorldBackground(final int tint) {
        if (this.mc.theWorld != null) {
            this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        } else {
            this.drawBackground(tint);
        }
    }

    /**
     * Draws the background (i is always 0 as of 1.2.2)
     */
    public void drawBackground(final int tint) {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        this.mc.getTextureManager().bindTexture(optionsBackground);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        worldrenderer.begin(7, DefaultVertexFormats.field_181709_i);
        worldrenderer.pos(0.0D, this.height, 0.0D).tex(0.0D, (float) this.height / 32.0F + (float) tint).func_181669_b(64, 64, 64, 255).endVertex();
        worldrenderer.pos(this.width, this.height, 0.0D).tex((float) this.width / 32.0F, (float) this.height / 32.0F + (float) tint).func_181669_b(64, 64, 64, 255).endVertex();
        worldrenderer.pos(this.width, 0.0D, 0.0D).tex((float) this.width / 32.0F, tint).func_181669_b(64, 64, 64, 255).endVertex();
        worldrenderer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, tint).func_181669_b(64, 64, 64, 255).endVertex();
        tessellator.draw();
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame() {
        return true;
    }

    public void confirmClicked(final boolean result, final int id) {
        if (id == 31102009) {
            if (result) {
                this.openWebLink(this.clickedLinkURI);
            }

            this.clickedLinkURI = null;
            this.mc.displayGuiScreen(this);
        }
    }

    private void openWebLink(final URI p_175282_1_) {
        try {
            final Class<?> oclass = Class.forName("java.awt.Desktop");
            final Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null);
            oclass.getMethod("browse", new Class[]{URI.class}).invoke(object, p_175282_1_);
        } catch (final Throwable throwable) {
            LOGGER.error("Couldn't open link", throwable);
        }
    }

    /**
     * Returns true if either windows ctrl key is down or if either mac meta key is down
     */
    public static boolean isCtrlKeyDown() {
        return Minecraft.isRunningOnMac ? Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220) : Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
    }

    /**
     * Returns true if either shift key is down
     */
    public static boolean isShiftKeyDown() {
        return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
    }

    /**
     * Returns true if either alt key is down
     */
    public static boolean isAltKeyDown() {
        return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
    }

    public static boolean isKeyComboCtrlX(final int p_175277_0_) {
        return p_175277_0_ == 45 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlV(final int p_175279_0_) {
        return p_175279_0_ == 47 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlC(final int p_175280_0_) {
        return p_175280_0_ == 46 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlA(final int p_175278_0_) {
        return p_175278_0_ == 30 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    /**
     * Called when the GUI is resized in order to update the world and the resolution
     */
    public void onResize(final Minecraft mcIn, final int p_175273_2_, final int p_175273_3_) {
        this.setWorldAndResolution(mcIn, p_175273_2_, p_175273_3_);
    }
}
