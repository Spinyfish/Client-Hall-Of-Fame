/**
 * @project Myth
 * @author CodeMan
 * @at 25.09.22, 16:51
 */
package dev.myth.ui.clickgui.skeetgui;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.myth.api.utils.font.FontLoaders;
import dev.myth.api.utils.mouse.MouseUtil;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.ui.clickgui.ChildComponent;
import dev.myth.ui.clickgui.Component;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DropdownComponent extends ChildComponent {

    private String text;
    private Supplier<Enum<?>> value;
    private Supplier<Enum<?>[]> getValues;
    private Supplier<ArrayList<Enum<?>>> getMultiValues;
    private Consumer<Integer> setValue;
    private boolean multiselect, clicked;
    private double fullHeight;

    public DropdownComponent(Component parent, String text, double x, double y, boolean multiselect, Supplier<Enum<?>> value, Consumer<Integer> setValue, Supplier<Enum<?>[]> getValues, Supplier<ArrayList<Enum<?>>> getMultiValues, Supplier<Boolean> visible) {
        super(parent, x, y, parent.getWidth() - 30, 16);
        this.multiselect = multiselect;
        this.text = text;
        this.value = value;
        this.setValue = setValue;
        this.getValues = getValues;
        this.getMultiValues = getMultiValues;
        setVisible(visible);
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        double x = getFullX(), y = getFullY(), width = getWidth(), height = 10;

        setWidth(getParent().getWidth() - 30);

        Gui.drawRect(x, y + 6, x + width, y + 6 + height, SkeetGui.INSTANCE.getColor(0x0C0C0C));

        int color = SkeetGui.INSTANCE.getColor(MouseUtil.isHovered(mouseX, mouseY, x + 0.5, y + 6 + 0.5, width - 0.5, height - 0.5) ? 0x292929 : 0x202020);

        Gui.drawGradientRect(x + 0.5, y + 6 + 0.5, x + width - 0.5, y + 6 + height - 0.5, color, RenderUtil.darker(color, 1.2f));

        StringBuilder valuetext = new StringBuilder();
        if (multiselect) {
            if (!getMultiValues.get().isEmpty()) {
                for (Enum<?> e : getMultiValues.get()) {
                    valuetext.append(e.toString()).append(", ");
                }
                if (valuetext.length() > 2) {
                    valuetext = new StringBuilder(valuetext.substring(0, valuetext.length() - 2));
                }
            } else {
                valuetext = new StringBuilder("-");
            }
        } else {
            valuetext = new StringBuilder(value.get().toString());
        }

        if (SkeetGui.INSTANCE.getFocusedComponent() == this) {
            Enum<?>[] values = getValues.get();
            fullHeight = 17.5 + (values.length * 10);

            GlStateManager.translate(0, 0, 2);

            Gui.drawRect(x, y + 17, x + width, y + fullHeight + 0.5, SkeetGui.INSTANCE.getColor(0x0D0D0D));

            for (int i = 0; i < values.length; i++) {
                Enum<?> e = values[i];
                boolean hovered = MouseUtil.isHovered(mouseX, mouseY, x, y + 17.5 + (i * 10), width, 10.1);
                boolean selected = multiselect ? getMultiValues.get().contains(e) : value.get() == e;
                Gui.drawRect(x + 0.5, y + 17.5 + (i * 10), x + width - 0.5, y + 17.5 + (i * 10) + 10, SkeetGui.INSTANCE.getColor(hovered ? 0x181818 : 0x202020));
                FontLoaders.TAHOMA_11.drawStringWithShadow((hovered || selected ? ChatFormatting.BOLD : "") + e.toString(), (float) (x + 5), (float) (y + 17.5 + (i * 10) + 4), SkeetGui.INSTANCE.getColor(selected ? SkeetGui.INSTANCE.getColor() : (hovered ? 0xCCCCCC : 0xA4A4A4)));
            }

            GlStateManager.translate(0, 0, -2);
        }

        double size = 4;

        GL11.glPushMatrix();
        GL11.glTranslated(x + width - 6, y + 6 + 4, 1);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GlStateManager.color(0.6f, 0.6f, 0.6f, SkeetGui.INSTANCE.getAlpha() / 255f);
        GL11.glBegin(GL11.GL_TRIANGLES);
        if (isExpanded()) {
            GL11.glVertex2d(size, size / 2);
            GL11.glVertex2d(size / 2, 0);
            GL11.glVertex2d(0, size / 2);
        } else {
            GL11.glVertex2d(0, 0);
            GL11.glVertex2d(size / 2, size / 2);
            GL11.glVertex2d(size, 0);
        }
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();

        if (SkeetGui.INSTANCE.getAlpha() > 60) {
            FontLoaders.TAHOMA_11.drawString(text, (float) (x + 2), (float) (y + 1.5), SkeetGui.INSTANCE.getColor(0xC9C9C9));

            String value = valuetext.toString();
            double maxLen = width - 12;
            double textLen = FontLoaders.TAHOMA_11.getStringWidth(value);
            while (textLen >= maxLen) {
                value = value.substring(0, value.length() - 1);
                textLen = FontLoaders.TAHOMA_11.getStringWidth(value);
            }

            FontLoaders.TAHOMA_11.drawString(value, (float) (x + 5), (float) (y + 10), SkeetGui.INSTANCE.getColor(0x959595));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        Enum<?>[] values = getValues.get();
        if(MouseUtil.isHovered(mouseX, mouseY, getFullX(), getFullY() + 6, getWidth(), 10 + (isExpanded() ? values.length * 10 : 0))) {
            clicked = true;
            return true;
        } else {
            close();
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY) {
        if(!clicked) return false;
        clicked = false;
        if (MouseUtil.isHovered(mouseX, mouseY, getFullX(), getFullY() + 6, getWidth(), 10)) {
            if(isExpanded()) {
                close();
            } else {
                SkeetGui.INSTANCE.setFocusedComponent(this);
            }
            return false;
        }

        if (isExpanded()) {
            double x = getFullX(), y = getFullY(), width = getWidth(), height = getHeight();
            Enum<?>[] values = getValues.get();
            for (int i = 0; i < values.length; i++) {
                if (MouseUtil.isHovered(mouseX, mouseY, x, y + 17.5 + (i * 10), width, 10.1)) {
                    setValue.accept(i);
                    if (!multiselect) close();
                    return true;
                }
            }
        }

        if (isExpanded() && !MouseUtil.isHovered(mouseX, mouseY, getFullX(), getFullY(), getWidth(), fullHeight)) {
            close();
        }

        return false;
    }

    private boolean isExpanded() {
        return SkeetGui.INSTANCE.getFocusedComponent() == this;
    }

    private void close() {
        if(SkeetGui.INSTANCE.getFocusedComponent() == this) {
            SkeetGui.INSTANCE.setFocusedComponent(null);
        }
    }
}
