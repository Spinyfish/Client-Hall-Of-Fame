package crispy.gui.clickgui.component.components.sub;

import crispy.Crispy;
import crispy.gui.clickgui.component.Component;
import crispy.gui.clickgui.component.components.Button;
import crispy.features.hacks.Category;
import crispy.features.hacks.impl.render.ClickGui;
import crispy.fonts.greatfont.TTFFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.superblaubeere27.valuesystem.NumberValue;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;


public class Slider extends Component {

    private final NumberValue set;
    private final Button parent;
    private boolean hovered;
    private int offset;
    private int x;
    private int y;
    private boolean dragging = false;

    private double renderWidth;

    public Slider(NumberValue value, Button button, int offset) {
        this.set = value;
        this.parent = button;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.offset = offset;
    }

    private static double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public void renderComponent(Category category) {
        TTFFontRenderer clean = Crispy.INSTANCE.getFontManager().getFont("clean 28");
        if (ClickGui.modeValue.getObject() == 0) {
            Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + parent.parent.getWidth(), parent.parent.getY() + offset + 12, this.hovered ? 0xFF222222 : 0xFF111111);

            Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + (int) renderWidth, parent.parent.getY() + offset + 12, hovered ? 0xFF555555 : 0xFF444444);

            Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, new Color(10, 10, 10, 255).getRGB());

        } else {
            Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + parent.parent.getWidth(), parent.parent.getY() + offset + 12, this.hovered ? new Color(0, 0, 0, 170).getRGB() : new Color(0, 0, 0, 140).getRGB());

            Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset + 10, parent.parent.getX() + (int) renderWidth, parent.parent.getY() + offset + 12, hovered ? new Color(255, 56, 56).darker().getRGB() : new Color(255, 56, 56).getRGB());

        }
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        if (ClickGui.modeValue.getObject() == 0) {
            Minecraft.fontRendererObj.drawStringWithShadow(this.set.getName() + ": " + this.set.getObject(), (parent.parent.getX() * 2 + 15), (parent.parent.getY() + offset + 2) * 2 + 5, -1);

        } else if (ClickGui.modeValue.getObject() == 1) {
            clean.drawStringWithShadow(this.set.getName() + ": " + this.set.getObject(), (parent.parent.getX() * 2 + 15), (parent.parent.getY() + offset + 2) * 2 + 5, -1);
        }
        GL11.glPopMatrix();
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.hovered = isMouseOnButtonD(mouseX, mouseY) || isMouseOnButtonI(mouseX, mouseY);
        this.y = parent.parent.getY() + offset;
        this.x = parent.parent.getX();

        double diff = Math.min(88, Math.max(0, mouseX - this.x));

        Number min = set.getMin();
        Number max = set.getMax();
        Number val = ((Number) set.getObject());
        renderWidth = (88) * (val.doubleValue() - min.doubleValue()) / (max.doubleValue() - min.doubleValue());


        if (dragging) {
            if (diff == 0) {
                set.setObject(set.getMin());
            } else {
                if (set.getObject() instanceof Integer) {
                    int value = (int) set.getObject();
                    int newValue = (int) roundToPlace(((diff / 88) * (max.intValue() - min.intValue()) + min.intValue()), 2);

                    set.setObject(newValue);
                }
                if (set.getObject() instanceof Float) {

                    double newValue = roundToPlace(((diff / 88) * (max.floatValue() - min.floatValue()) + min.floatValue()), 2);
                    set.setObject((float) newValue);
                }
                if (set.getObject() instanceof Long) {
                    double newValue = roundToPlace(((diff / 88) * (max.longValue() - min.longValue()) + min.longValue()), 2);
                    set.setObject((long) newValue);

                }
                if (set.getObject() instanceof Double) {
                    double newValue = roundToPlace(((diff / 88) * (max.doubleValue() - min.doubleValue()) + min.doubleValue()), 2);
                    set.setObject(newValue);

                }

            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButtonD(mouseX, mouseY) && button == 0 && this.parent.open) {
            dragging = true;
        }
        if (isMouseOnButtonI(mouseX, mouseY) && button == 0 && this.parent.open) {
            dragging = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        dragging = false;
    }

    public boolean isMouseOnButtonD(int x, int y) {
        return x > this.x && x < this.x + (parent.parent.getWidth() / 2 + 1) && y > this.y && y < this.y + 12;
    }

    public boolean isMouseOnButtonI(int x, int y) {
        return x > this.x + parent.parent.getWidth() / 2 && x < this.x + parent.parent.getWidth() && y > this.y && y < this.y + 12;
    }
}
