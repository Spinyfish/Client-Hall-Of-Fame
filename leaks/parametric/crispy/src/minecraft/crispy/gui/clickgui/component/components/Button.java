package crispy.gui.clickgui.component.components;

import arithmo.gui.altmanager.Colors;
import crispy.Crispy;
import crispy.gui.clickgui.component.Component;
import crispy.gui.clickgui.component.Frame;
import crispy.gui.clickgui.component.components.sub.Checkbox;
import crispy.gui.clickgui.component.components.sub.Keybind;
import crispy.gui.clickgui.component.components.sub.ModeButton;
import crispy.gui.clickgui.component.components.sub.Slider;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.impl.render.ClickGui;
import crispy.fonts.greatfont.TTFFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.superblaubeere27.valuesystem.BooleanValue;
import net.superblaubeere27.valuesystem.ModeValue;
import net.superblaubeere27.valuesystem.NumberValue;
import net.superblaubeere27.valuesystem.Value;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;


public class Button extends Component {

    public Hack mod;
    public Frame parent;
    public int offset;
    public boolean open;
    private boolean isHovered;
    private final ArrayList<Component> subcomponents;
    private final int height;

    public Button(Hack mod, Frame parent, int offset) {
        this.mod = mod;
        this.parent = parent;
        this.offset = offset;
        this.subcomponents = new ArrayList<Component>();
        this.open = false;
        height = 12;
        int opY = offset + 12;
        if (Crispy.INSTANCE.getValueManager().getAllValuesFrom(mod.getName()) != null) {

            for (Value s : Crispy.INSTANCE.getValueManager().getAllValuesFrom(mod.getName())) {

                if (s instanceof ModeValue) {
                    ModeValue mode = (ModeValue) s;
                    this.subcomponents.add(new ModeButton((ModeValue) s, this, mod, opY));

                    for (Map.Entry<NumberValue, Integer> values : mode.getNumberComp().entrySet()) {

                        if (mode.getObject().intValue() == values.getValue()) {

                            this.subcomponents.add(new Slider(values.getKey(), this, opY + 12));
                        }
                    }
                    for (Map.Entry<ModeValue, Integer> values : mode.getModeComp().entrySet()) {
                        if (mode.getObject().intValue() == values.getValue()) {
                            this.subcomponents.add(new ModeButton(values.getKey(), this, mod, opY + 12));
                        }
                    }
                    for (Map.Entry<BooleanValue, Integer> values : mode.getBooleanComp().entrySet()) {
                        if (mode.getObject().intValue() == values.getValue()) {
                            this.subcomponents.add(new Checkbox(values.getKey(), this, opY + 12));
                        }
                    }
                    opY += 12;
                }
                if (s instanceof NumberValue) {
                    NumberValue numberValue = (NumberValue) s;

                    this.subcomponents.add(new Slider((NumberValue) s, this, opY));
                    opY += 12;
                }
                if (s instanceof BooleanValue) {
                    this.subcomponents.add(new Checkbox((BooleanValue) s, this, opY));
                    opY += 12;
                }
            }
        }
        this.subcomponents.add(new Keybind(this, opY));
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
        int opY = offset + 12;
        for (Component comp : this.subcomponents) {
            comp.setOff(opY);
            opY += 12;
        }
    }

    public void updateComponents() {
        this.subcomponents.clear();
        this.parent.refresh();
        int opY = offset + 12;
        if (Crispy.INSTANCE.getValueManager().getAllValuesFrom(mod.getName()) != null) {

            for (Value s : Crispy.INSTANCE.getValueManager().getAllValuesFrom(mod.getName())) {
                if(s.isVisible()) {
                    if (s instanceof ModeValue) {
                        ModeValue mode = (ModeValue) s;
                        this.subcomponents.add(new ModeButton((ModeValue) s, this, mod, opY));

                        for (Map.Entry<NumberValue, Integer> values : mode.getNumberComp().entrySet()) {

                            if (mode.getObject().intValue() == values.getValue()) {

                                this.subcomponents.add(new Slider(values.getKey(), this, opY + 12));
                            }
                        }
                        for (Map.Entry<ModeValue, Integer> values : mode.getModeComp().entrySet()) {
                            if (mode.getObject().intValue() == values.getValue()) {
                                this.subcomponents.add(new ModeButton(values.getKey(), this, mod, opY + 12));
                            }
                        }
                        for (Map.Entry<BooleanValue, Integer> values : mode.getBooleanComp().entrySet()) {
                            if (mode.getObject().intValue() == values.getValue()) {
                                this.subcomponents.add(new Checkbox(values.getKey(), this, opY + 12));
                            }
                        }
                        opY += 12;
                    }
                    if (s instanceof NumberValue) {
                        NumberValue numberValue = (NumberValue) s;

                        this.subcomponents.add(new Slider((NumberValue) s, this, opY));
                        opY += 12;
                    }
                    if (s instanceof BooleanValue) {
                        this.subcomponents.add(new Checkbox((BooleanValue) s, this, opY));
                        opY += 12;
                    }
                }
            }
        }
        this.subcomponents.add(new Keybind(this, opY));
    }

    @Override
    public void renderComponent(Category category) {
        TTFFontRenderer clean = Crispy.INSTANCE.getFontManager().getFont("clean 32");
        if (ClickGui.modeValue.getObject() == 0) {
            Gui.drawRect(parent.getX() - 1, this.parent.getY() + this.offset, parent.getX() + parent.getWidth() + 1, this.parent.getY() + 12 + this.offset + 1, category.getColor().getRGB());
            Gui.drawRect(parent.getX(), this.parent.getY() + this.offset, parent.getX() + parent.getWidth(), this.parent.getY() + 12 + this.offset, this.isHovered ? (this.mod.isEnabled() ? category.getColor().darker().getRGB() : 0xFF222222) : (this.mod.isEnabled() ? category.getColor().getRGB() : 0xFF111111));
        } else if (ClickGui.modeValue.getObject() == 1) {
            Gui.drawRect(parent.getX(), this.parent.getY() + this.offset, parent.getX() + parent.getWidth(), this.parent.getY() + 12 + this.offset, this.isHovered ? (this.mod.isEnabled() ? Colors.getColor(0, 0, 0, 170) : Colors.getColor(0, 0, 0, 170)) : (this.mod.isEnabled() ? Colors.getColor(0, 0, 0, 140) : Colors.getColor(0, 0, 0, 140)));
        }
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        if (ClickGui.modeValue.getObject() == 0) {
            Minecraft.fontRendererObj.drawStringWithShadow(this.mod.getName(), (parent.getX() + 2) * 2, (parent.getY() + offset + 2) * 2 + 4, -1);
        } else if (ClickGui.modeValue.getObject() == 1) {

            clean.drawStringWithShadow(this.mod.getName(), (parent.getX() + 2) * 2, (parent.getY() + offset + 2) * 2 + 4, mod.isEnabled() ? new Color(255, 56, 56).getRGB() : new Color(255, 255, 255).getRGB());
        }
        if (this.subcomponents.size() > 1) {
            if (ClickGui.modeValue.getObject() == 0) {
                Minecraft.fontRendererObj.drawStringWithShadow(this.open ? "-" : "+", (parent.getX() + parent.getWidth() - 10) * 2, (parent.getY() + offset + 2) * 2 + 4, -1);
            } else if (ClickGui.modeValue.getObject() == 1) {
                ResourceLocation down = new ResourceLocation("Client/gui/down.png");
                ResourceLocation redDown = new ResourceLocation("Client/gui/reddown.png");
                ResourceLocation up = new ResourceLocation("Client/gui/up.png");
                ResourceLocation redUp = new ResourceLocation("Client/gui/redup.png");
                Minecraft.getMinecraft().getTextureManager().bindTexture(this.mod.isEnabled() ? (open ? redDown : redUp) : (open ? down : up));
                Gui.drawModalRectWithCustomSizedTexture((parent.getX() + parent.getWidth() - 10) * 2, (parent.getY() + offset + 2) * 2 + 4, 17, 17, 17, 17, 17, 17);
            }
        }
        GL11.glPopMatrix();
        if (this.open) {
            if (!this.subcomponents.isEmpty()) {
                for (Component comp : this.subcomponents) {
                    GlStateManager.color(1, 1, 1);
                    Gui.drawRect(0, 0, 0, 0, new Color(1, 1, 1).getRGB());

                    comp.renderComponent(category);
                }
                if (ClickGui.modeValue.getObject() == 0) {
                    Gui.drawRect(parent.getX() + 2, parent.getY() + this.offset + 12, parent.getX() + 3, parent.getY() + this.offset + ((this.subcomponents.size() + 1) * 12), category.getColor().getRGB());
                }
            }
        }
    }

    @Override
    public int getHeight() {
        if (this.open) {
            return (12 * (this.subcomponents.size() + 1));
        }
        return 12;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.isHovered = isMouseOnButton(mouseX, mouseY);
        if (!this.subcomponents.isEmpty()) {
            for (Component comp : this.subcomponents) {
                comp.updateComponent(mouseX, mouseY);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {

        if (isMouseOnButton(mouseX, mouseY) && button == 0) {
            this.mod.toggle();
        }
        if (isMouseOnButton(mouseX, mouseY) && button == 1) {
            this.open = !this.open;
            this.parent.refresh();
        }
        for (Component comp : this.subcomponents) {
            comp.mouseClicked(mouseX, mouseY, button);
        }

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        for (Component comp : this.subcomponents) {
            comp.mouseReleased(mouseX, mouseY, mouseButton);
        }
        this.updateComponents();
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        for (Component comp : this.subcomponents) {
            comp.keyTyped(typedChar, key);
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > parent.getX() && x < parent.getX() + parent.getWidth() && y > this.parent.getY() + this.offset && y < this.parent.getY() + 12 + this.offset;
    }
}
