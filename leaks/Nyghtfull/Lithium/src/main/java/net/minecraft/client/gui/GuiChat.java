package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import dev.tenacity.utils.tuples.Pair;
import dev.tenacity.Tenacity;
import dev.tenacity.config.DragManager;
import dev.tenacity.module.impl.render.ArrayListMod;
import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.module.impl.render.SpotifyMod;
import dev.tenacity.utils.misc.HoveringUtil;
import dev.tenacity.utils.misc.Multithreading;
import dev.tenacity.utils.objects.Dragging;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RoundedUtil;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.font.AbstractFontRenderer;
import dev.tenacity.utils.font.CustomFont;
import dev.tenacity.utils.font.FontUtil;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class GuiChat extends GuiScreen {
    private static final Logger logger = LogManager.getLogger();
    private String historyBuffer = "";

    /**
     * keeps position of which chat message you will select when you press up, (does not increase for duplicated
     * messages sent immediately after each other)
     */
    private int sentHistoryCursor = -1;
    private boolean playerNamesFound;
    private boolean waitingOnAutocomplete;
    private int autocompleteIndex;
    private List<String> foundPlayerNames = Lists.<String>newArrayList();

    /**
     * Chat entry field
     */
    protected GuiTextField inputField;

    /**
     * is the text that appears when you press the chat key and the input box appears pre-filled
     */
    private String defaultInputFieldText = "";

    public GuiChat() {
    }

    public GuiChat(String defaultText) {
        this.defaultInputFieldText = defaultText;
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed() {
        openingAnimation.setDirection(Direction.BACKWARDS);
        Keyboard.enableRepeatEvents(false);
        this.mc2.ingameGUI.getChatGUI().resetScroll();
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {
        this.inputField.updateCursorCounter();
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.waitingOnAutocomplete = false;

        if (keyCode == 15) {
            this.autocompletePlayerNames();
        } else {
            this.playerNamesFound = false;
        }

        if (keyCode == 1) {
            openingAnimation.setDirection(Direction.BACKWARDS);

        } else if (keyCode != 28 && keyCode != 156) {
            if (keyCode == 200) {
                this.getSentHistory(-1);
            } else if (keyCode == 208) {
                this.getSentHistory(1);
            } else if (keyCode == 201) {
                this.mc2.ingameGUI.getChatGUI().scroll(this.mc2.ingameGUI.getChatGUI().getLineCount() - 1);
            } else if (keyCode == 209) {
                this.mc2.ingameGUI.getChatGUI().scroll(-this.mc2.ingameGUI.getChatGUI().getLineCount() + 1);
            } else {
                this.inputField.textboxKeyTyped(typedChar, keyCode);
            }
        } else {
            String s = this.inputField.getText().trim();

            if (s.length() > 0) {
                this.sendChatMessage(s);
            }
            openingAnimation.setDirection(Direction.BACKWARDS);
        }
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();

        if (i != 0) {
            if (i > 1) {
                i = 1;
            }

            if (i < -1) {
                i = -1;
            }

            if (!isShiftKeyDown()) {
                i *= 7;
            }

            this.mc2.ingameGUI.getChatGUI().scroll(i);
        }
    }

    Animation resetButtonHover;

    SpotifyMod spotifyMod;
    ArrayListMod arraylistMod;

    public static Animation openingAnimation = new DecelerateAnimation(175, 1, Direction.BACKWARDS);


    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {

        if (spotifyMod == null) {
            spotifyMod = (SpotifyMod) Tenacity.INSTANCE.getModuleCollection().get(SpotifyMod.class);
            arraylistMod = (ArrayListMod) Tenacity.INSTANCE.getModuleCollection().get(ArrayListMod.class);
        }

        for (Dragging dragging : DragManager.draggables.values()) {
            if (!dragging.hoverAnimation.getDirection().equals(Direction.BACKWARDS)) {
                dragging.hoverAnimation.setDirection(Direction.BACKWARDS);
            }
        }

        openingAnimation = new DecelerateAnimation(175, 1);


        resetButtonHover = new DecelerateAnimation(250, 1);

        Keyboard.enableRepeatEvents(true);
        this.sentHistoryCursor = this.mc2.ingameGUI.getChatGUI().getSentMessages().size();
        this.inputField = new GuiTextField(0, this.fontRendererObj, 4, this.height - 12, this.width - 4, 12);
        this.inputField.setMaxStringLength(100);
        this.inputField.setEnableBackgroundDrawing(false);
        this.inputField.setFocused(true);
        this.inputField.setText(this.defaultInputFieldText);
        this.inputField.setCanLoseFocus(false);
    }


    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (openingAnimation.finished(Direction.BACKWARDS)) {
            this.mc2.displayGuiScreen((GuiScreen) null);
            return;
        }

        Gui.drawRect2(2, this.height - (14 * openingAnimation.getOutput().floatValue()), this.width - 4, 12, Integer.MIN_VALUE);

        AbstractFontRenderer abstractFontRenderer = this.mc2.fontRendererObj;
        if (HUDMod.customFont.isEnabled()) {
            abstractFontRenderer = FontUtil.lithiumFont20;
        }
        inputField.font = abstractFontRenderer;

        inputField.yPosition = (float) (this.height - (12 * openingAnimation.getOutput().floatValue()));
        this.inputField.drawTextBox();
        IChatComponent ichatcomponent = this.mc2.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());

        if (ichatcomponent != null && ichatcomponent.getChatStyle().getChatHoverEvent() != null) {
            this.handleComponentHover(ichatcomponent, mouseX, mouseY);
        }

        DragManager.draggables.values().forEach(dragging -> {
            if (dragging.getModule().isEnabled()) {
                if (dragging.getModule().equals(arraylistMod)) {
                    dragging.onDrawArraylist(arraylistMod, mouseX, mouseY);
                } else {
                    dragging.onDraw(mouseX, mouseY);
                }
            }
        });

        HUDMod hudMod = (HUDMod) Tenacity.INSTANCE.getModuleCollection().get(HUDMod.class);

        Pair<Color, Color> colors = HUDMod.getClientColors();

        boolean hovering = HoveringUtil.isHovering(width / 2f - 50, 20, 100, 20, mouseX, mouseY);

        resetButtonHover.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);

        float alpha = (float) (.5f + (.10 * resetButtonHover.getOutput().floatValue()));
        Color color = ColorUtil.interpolateColorsBackAndForth(15, 1, colors.getFirst(), colors.getSecond(), false);
        RoundedUtil.drawRound(width / 2f - 50, 20, 100, 20, 10,
                 color);

        FontUtil.lithiumFont24.drawCenteredString("Reset", width / 2f, 20 + FontUtil.lithiumBoldFont20.getMiddleOfBox(20), -1);


        if (spotifyMod.isEnabled()) {
            float spacing = 0;
            Dragging spotifyDrag = DragManager.draggables.get("spotify");
            for (String button : spotifyMod.buttons) {
                CustomFont iconFont = FontUtil.FontType.ICON.size(20);
                boolean hover = HoveringUtil.isHovering(spotifyDrag.getX() + spotifyMod.albumCoverSize + 6 + spacing, spotifyDrag.getY() + spotifyMod.height - (19),
                        (float) iconFont.getStringWidth(button), iconFont.getHeight(), mouseX, mouseY);

                spotifyMod.buttonAnimations.get(button).setDirection(hover ? Direction.FORWARDS : Direction.BACKWARDS);
                spacing += 15;
            }

            spotifyMod.hoveringPause = HoveringUtil.isHovering(spotifyDrag.getX(), spotifyDrag.getY(), spotifyMod.albumCoverSize, spotifyMod.height, mouseX, mouseY);
        }


        super.drawScreen(mouseX, mouseY, partialTicks);
    }


    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            IChatComponent ichatcomponent = this.mc2.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());

            if (this.handleComponentClick(ichatcomponent)) {
                return;
            }
        }

        this.inputField.mouseClicked(mouseX, mouseY, mouseButton);

        if (spotifyMod.isEnabled()) {
            float spacing = 0;
            for (String button : spotifyMod.buttons) {
                Dragging spotifyDrag = DragManager.draggables.get("spotify");
                CustomFont iconFont = FontUtil.FontType.ICON.size(20);
                if (HoveringUtil.isHovering(spotifyDrag.getX() + spotifyMod.albumCoverSize + 6 + spacing, spotifyDrag.getY() + spotifyMod.height - (19),
                        (float) iconFont.getStringWidth(button), iconFont.getHeight(), mouseX, mouseY)) {
                    if (mouseButton == 0) {
                        Multithreading.runAsync(() -> {
                            switch (button) {
                                case FontUtil.SKIP_LEFT:
                                    spotifyMod.api.skipToPreviousTrack();
                                    break;
                                case FontUtil.SKIP_RIGHT:
                                    spotifyMod.api.skipTrack();
                                    break;
                                case FontUtil.SHUFFLE:
                                    spotifyMod.api.toggleShuffleState();
                                    break;
                            }
                        });
                        return;
                    }
                }
                spacing += 15;
            }

            if (spotifyMod.hoveringPause) {
                Multithreading.runAsync(() -> {
                    if (spotifyMod.api.isPlaying()) {
                        spotifyMod.api.pausePlayback();
                    } else {
                        spotifyMod.api.resumePlayback();
                    }
                });
            }
        }


        boolean hoveringResetButton = HoveringUtil.isHovering(width / 2f - 100, 20, 200, 20, mouseX, mouseY);
        if (hoveringResetButton && mouseButton == 0) {
            for (Dragging dragging : DragManager.draggables.values()) {
                dragging.setX(dragging.initialXVal);
                dragging.setY(dragging.initialYVal);
            }
            return;
        }


        DragManager.draggables.values().forEach(dragging -> {
            if (dragging.getModule().isEnabled()) {
                if (dragging.getModule().equals(arraylistMod)) {
                    dragging.onClickArraylist(arraylistMod, mouseX, mouseY, mouseButton);
                } else {
                    dragging.onClick(mouseX, mouseY, mouseButton);
                }
            }
        });


        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        DragManager.draggables.values().forEach(dragging -> {
            if (dragging.getModule().isEnabled()) {
                dragging.onRelease(state);
            }
        });

    }

    /**
     * Sets the text of the chat
     */
    protected void setText(String newChatText, boolean shouldOverwrite) {
        if (shouldOverwrite) {
            this.inputField.setText(newChatText);
        } else {
            this.inputField.writeText(newChatText);
        }
    }

    public void autocompletePlayerNames() {
        if (this.playerNamesFound) {
            this.inputField.deleteFromCursor(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());

            if (this.autocompleteIndex >= this.foundPlayerNames.size()) {
                this.autocompleteIndex = 0;
            }
        } else {
            int i = this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false);
            this.foundPlayerNames.clear();
            this.autocompleteIndex = 0;
            String s = this.inputField.getText().substring(i).toLowerCase();
            String s1 = this.inputField.getText().substring(0, this.inputField.getCursorPosition());
            this.sendAutocompleteRequest(s1, s);

            if (this.foundPlayerNames.isEmpty()) {
                return;
            }

            this.playerNamesFound = true;
            this.inputField.deleteFromCursor(i - this.inputField.getCursorPosition());
        }

        if (this.foundPlayerNames.size() > 1) {
            StringBuilder stringbuilder = new StringBuilder();

            for (String s2 : this.foundPlayerNames) {
                if (stringbuilder.length() > 0) {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(s2);
            }

            this.mc2.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new ChatComponentText(stringbuilder.toString()), 1);
        }

        this.inputField.writeText((String) this.foundPlayerNames.get(this.autocompleteIndex++));
    }

    private void sendAutocompleteRequest(String p_146405_1_, String p_146405_2_) {
        if (p_146405_1_.length() >= 1) {
            BlockPosition blockpos = null;

            if (this.mc2.objectMouseOver != null && this.mc2.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                blockpos = this.mc2.objectMouseOver.getBlockPos();
            }

            this.mc2.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete(p_146405_1_, blockpos));
            this.waitingOnAutocomplete = true;
        }
    }

    /**
     * input is relative and is applied directly to the sentHistoryCursor so -1 is the previous message, 1 is the next
     * message from the current cursor position
     */
    public void getSentHistory(int msgPos) {
        int i = this.sentHistoryCursor + msgPos;
        int j = this.mc2.ingameGUI.getChatGUI().getSentMessages().size();
        i = MathHelper.clamp_int(i, 0, j);

        if (i != this.sentHistoryCursor) {
            if (i == j) {
                this.sentHistoryCursor = j;
                this.inputField.setText(this.historyBuffer);
            } else {
                if (this.sentHistoryCursor == j) {
                    this.historyBuffer = this.inputField.getText();
                }

                this.inputField.setText((String) this.mc2.ingameGUI.getChatGUI().getSentMessages().get(i));
                this.sentHistoryCursor = i;
            }
        }
    }

    public void onAutocompleteResponse(String[] p_146406_1_) {
        if (this.waitingOnAutocomplete) {
            this.playerNamesFound = false;
            this.foundPlayerNames.clear();

            for (String s : p_146406_1_) {
                if (s.length() > 0) {
                    this.foundPlayerNames.add(s);
                }
            }

            String s1 = this.inputField.getText().substring(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false));
            String s2 = StringUtils.getCommonPrefix(p_146406_1_);

            if (s2.length() > 0 && !s1.equalsIgnoreCase(s2)) {
                this.inputField.deleteFromCursor(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());
                this.inputField.writeText(s2);
            } else if (this.foundPlayerNames.size() > 0) {
                this.playerNamesFound = true;
                this.autocompletePlayerNames();
            }
        }
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame() {
        return false;
    }
}
