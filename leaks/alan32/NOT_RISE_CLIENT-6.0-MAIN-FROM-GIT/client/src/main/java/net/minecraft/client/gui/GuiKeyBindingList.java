package net.minecraft.client.gui;

import com.alan.clients.util.font.impl.minecraft.FontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

public class GuiKeyBindingList extends GuiListExtended {
    private final GuiControls field_148191_k;
    private final Minecraft mc;
    private final GuiListExtended.IGuiListEntry[] listEntries;
    private int maxListLabelWidth = 0;

    public GuiKeyBindingList(final GuiControls controls, final Minecraft mcIn) {
        super(mcIn, controls.width, controls.height, 63, controls.height - 32, 20);
        this.field_148191_k = controls;
        this.mc = mcIn;
        final KeyBinding[] akeybinding = ArrayUtils.clone(mcIn.gameSettings.keyBindings);
        this.listEntries = new GuiListExtended.IGuiListEntry[akeybinding.length + KeyBinding.getKeybinds().size()];
        Arrays.sort(akeybinding);
        int i = 0;
        String s = null;

        for (final KeyBinding keybinding : akeybinding) {
            final String s1 = keybinding.getKeyCategory();

            if (!s1.equals(s)) {
                s = s1;
                this.listEntries[i++] = new GuiKeyBindingList.CategoryEntry(s1);
            }

            final int j = mcIn.fontRendererObj.width(I18n.format(keybinding.getKeyDescription()));

            if (j > this.maxListLabelWidth) {
                this.maxListLabelWidth = j;
            }

            this.listEntries[i++] = new GuiKeyBindingList.KeyEntry(keybinding);
        }
    }

    protected int getSize() {
        return this.listEntries.length;
    }

    /**
     * Gets the IGuiListEntry object for the given index
     */
    public GuiListExtended.IGuiListEntry getListEntry(final int index) {
        return this.listEntries[index];
    }

    protected int getScrollBarX() {
        return super.getScrollBarX() + 15;
    }

    /**
     * Gets the width of the list
     */
    public int getListWidth() {
        return super.getListWidth() + 32;
    }

    public class CategoryEntry implements GuiListExtended.IGuiListEntry {
        private final String labelText;
        private final int labelWidth;

        public CategoryEntry(final String p_i45028_2_) {
            this.labelText = I18n.format(p_i45028_2_);
            this.labelWidth = GuiKeyBindingList.this.mc.fontRendererObj.width(this.labelText);
        }

        public void drawEntry(final int slotIndex, final int x, final int y, final int listWidth, final int slotHeight, final int mouseX, final int mouseY, final boolean isSelected) {
            GuiKeyBindingList.this.mc.fontRendererObj.drawString(this.labelText, GuiKeyBindingList.this.mc.currentScreen.width / 2 - this.labelWidth / 2, y + slotHeight - GuiKeyBindingList.this.mc.fontRendererObj.FONT_HEIGHT - 1, 16777215);
        }

        public boolean mousePressed(final int slotIndex, final int p_148278_2_, final int p_148278_3_, final int p_148278_4_, final int p_148278_5_, final int p_148278_6_) {
            return false;
        }

        public void mouseReleased(final int slotIndex, final int x, final int y, final int mouseEvent, final int relativeX, final int relativeY) {
        }

        public void setSelected(final int p_178011_1_, final int p_178011_2_, final int p_178011_3_) {
        }
    }

    public class KeyEntry implements GuiListExtended.IGuiListEntry {
        private final KeyBinding keybinding;
        private final String keyDesc;
        private final GuiButton btnChangeKeyBinding;
        private final GuiButton btnReset;

        private KeyEntry(final KeyBinding p_i45029_2_) {
            this.keybinding = p_i45029_2_;
            this.keyDesc = I18n.format(p_i45029_2_.getKeyDescription());
            this.btnChangeKeyBinding = new GuiButton(0, 0, 0, 75, 20, I18n.format(p_i45029_2_.getKeyDescription()));
            this.btnReset = new GuiButton(0, 0, 0, 50, 20, I18n.format("controls.reset"));
        }

        public void drawEntry(final int slotIndex, final int x, final int y, final int listWidth, final int slotHeight, final int mouseX, final int mouseY, final boolean isSelected) {
            final boolean flag = GuiKeyBindingList.this.field_148191_k.buttonId == this.keybinding;
            GuiKeyBindingList.this.mc.fontRendererObj.drawString(this.keyDesc, x + 90 - GuiKeyBindingList.this.maxListLabelWidth, y + slotHeight / 2 - FontRenderer.FONT_HEIGHT / 2, 16777215);
            this.btnReset.xPosition = x + 190;
            this.btnReset.yPosition = y;
            this.btnReset.enabled = this.keybinding.getKeyCode() != this.keybinding.getKeyCodeDefault();
            this.btnReset.drawButton(GuiKeyBindingList.this.mc, mouseX, mouseY);
            this.btnChangeKeyBinding.xPosition = x + 105;
            this.btnChangeKeyBinding.yPosition = y;
            this.btnChangeKeyBinding.displayString = GameSettings.getKeyDisplayString(this.keybinding.getKeyCode());
            boolean flag1 = false;

            if (this.keybinding.getKeyCode() != 0) {
                for (final KeyBinding keybinding : GuiKeyBindingList.this.mc.gameSettings.keyBindings) {
                    if (keybinding != this.keybinding && keybinding.getKeyCode() == this.keybinding.getKeyCode()) {
                        flag1 = true;
                        break;
                    }
                }
            }

            if (flag) {
                this.btnChangeKeyBinding.displayString = EnumChatFormatting.WHITE + "> " + EnumChatFormatting.YELLOW + this.btnChangeKeyBinding.displayString + EnumChatFormatting.WHITE + " <";
            } else if (flag1) {
                this.btnChangeKeyBinding.displayString = EnumChatFormatting.RED + this.btnChangeKeyBinding.displayString;
            }

            this.btnChangeKeyBinding.drawButton(GuiKeyBindingList.this.mc, mouseX, mouseY);
        }

        public boolean mousePressed(final int slotIndex, final int p_148278_2_, final int p_148278_3_, final int p_148278_4_, final int p_148278_5_, final int p_148278_6_) {
            if (this.btnChangeKeyBinding.mousePressed(GuiKeyBindingList.this.mc, p_148278_2_, p_148278_3_)) {
                GuiKeyBindingList.this.field_148191_k.buttonId = this.keybinding;
                return true;
            } else if (this.btnReset.mousePressed(GuiKeyBindingList.this.mc, p_148278_2_, p_148278_3_)) {
                GuiKeyBindingList.this.mc.gameSettings.setOptionKeyBinding(this.keybinding, this.keybinding.getKeyCodeDefault());
                KeyBinding.resetKeyBindingArrayAndHash();
                return true;
            } else {
                return false;
            }
        }

        public void mouseReleased(final int slotIndex, final int x, final int y, final int mouseEvent, final int relativeX, final int relativeY) {
            this.btnChangeKeyBinding.mouseReleased(x, y);
            this.btnReset.mouseReleased(x, y);
        }

        public void setSelected(final int p_178011_1_, final int p_178011_2_, final int p_178011_3_) {
        }
    }
}
