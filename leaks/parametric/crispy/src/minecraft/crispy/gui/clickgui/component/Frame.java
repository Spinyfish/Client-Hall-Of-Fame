package crispy.gui.clickgui.component;


import arithmo.gui.altmanager.Colors;
import crispy.Crispy;
import crispy.gui.clickgui.component.components.Button;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.impl.render.ClickGui;
import crispy.util.animation.animations.Direction;
import crispy.util.animation.animations.impl.SmootherStepAnimation;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class Frame {

    public ArrayList<Component> components;
    public Category category;
    public int dragX;
    public int dragY;
    SmootherStepAnimation openingAnimation = new SmootherStepAnimation(335, 1, Direction.FORWARDS);
    private boolean open;
    private final int width;
    private int y;
    private int x;
    private final int barHeight;
    private boolean isDragging;


    /*
    Why do you reset vars every cat :/
     */
    public Frame(Category cat) {
        this.components = new ArrayList<Component>();
        this.category = cat;
        this.width = 88;
        this.x = 5;
        this.y = 5;
        this.barHeight = 13;
        this.dragX = 0;
        this.open = false;
        this.isDragging = false;
        int tY = this.barHeight;

        /**
         * 		public ArrayList<Module> getModulesInCategory(Category categoryIn){
         * 			ArrayList<Module> mods = new ArrayList<Module>();
         * 			for(Module m : this.modules){
         * 				if(m.getCategory() == categoryIn)
         * 					mods.add(m);
         *            }
         * 			return mods;
         *        }
         */

        for (Hack mod : Crispy.INSTANCE.getHackManager().getModules(category)) {
            Button modButton = new Button(mod, this, tY);
            this.components.add(modButton);
            tY += 12;
        }
    }

    public ArrayList<Component> getComponents() {
        return components;
    }

    public void setDrag(boolean drag) {
        this.isDragging = drag;
    }

    public boolean isOpen() {

        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void renderFrame(FontRenderer fontRenderer) {
        if (ClickGui.modeValue.getObject() == 0) {
            Gui.drawRect(this.x - 1, this.y - 1, this.x + (this.width + 1), this.y + this.barHeight + 1, category.getColor().getRGB());
            Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.barHeight, Colors.getColor(0, 0, 0));
        } else if (ClickGui.modeValue.getObject() == 1) {
            GlStateManager.color(255, 56, 56);
            Gui.drawRect(0, 0, 0, 0, new Color(255, 56, 56).getRGB());
            Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.barHeight, new Color(255, 56, 56).getRGB());

        }
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        if (ClickGui.modeValue.getObject() == 0) {
            fontRenderer.drawStringWithShadow(this.category.name(), (this.x + 2) * 2 + 5, (this.y + 2.5f) * 2 + 5, 0xFFFFFFFF);
        } else if (ClickGui.modeValue.getObject() == 1) {
            Gui.drawCenteredString(Crispy.INSTANCE.getFontManager().getFont("clean 28"), this.category.getName(), (this.x + 2) * 2 + 80, (int) ((this.y + 2.5f) * 2 + 5), 0xFFFFFFFF);

        }
        if (ClickGui.modeValue.getObject() == 0) {
            fontRenderer.drawStringWithShadow(this.open ? "-" : "+", (this.x + this.width - 10) * 2 + 5, (this.y + 2.5f) * 2 + 5, -1);
        }
        GL11.glPopMatrix();

        if (this.open) {
            if (!this.components.isEmpty()) {

                //Gui.drawRect(this.x, this.y + this.barHeight, this.x + 1, this.y + this.barHeight + (12 * components.size()), new Color(0, 200, 20, 150).getRGB());
                //Gui.drawRect(this.x, this.y + this.barHeight + (12 * components.size()), this.x + this.width, this.y + this.barHeight + (12 * components.size()) + 1, new Color(0, 200, 20, 150).getRGB());
                //Gui.drawRect(this.x + this.width, this.y + this.barHeight, this.x + this.width - 1, this.y + this.barHeight + (12 * components.size()), new Color(0, 200, 20, 150).getRGB());
                for (Component component : components) {
                    GlStateManager.color(1, 1, 1);
                    Gui.drawRect(0, 0, 0, 0, new Color(1, 1, 1).getRGB());
                    component.renderComponent(category);
                }
            }
        }


    }

    public void refresh() {
        int off = this.barHeight;
        for (Component comp : components) {
            comp.setOff(off);
            off += comp.getHeight();
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int newX) {
        this.x = newX;
    }

    public int getY() {
        return y;
    }

    public void setY(int newY) {
        this.y = newY;
    }

    public int getWidth() {
        return width;
    }

    public void updatePosition(int mouseX, int mouseY) {
        if (this.isDragging) {
            this.setX(mouseX - dragX);
            this.setY(mouseY - dragY);
        }
    }

    public boolean isWithinHeader(int x, int y) {
        return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.barHeight;
    }

}
