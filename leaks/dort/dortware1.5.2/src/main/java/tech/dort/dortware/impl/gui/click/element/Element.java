package tech.dort.dortware.impl.gui.click.element;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import tech.dort.dortware.Client;
import tech.dort.dortware.impl.managers.ValueManager;

public abstract class Element {
    protected Minecraft mc = Minecraft.getMinecraft();
    protected ValueManager valueManager = Client.INSTANCE.getValueManager();
    protected ResourceLocation expandIcon = new ResourceLocation("meme/expand.png");
    protected ResourceLocation collapseIcon = new ResourceLocation("meme/collapse.png");

    protected static final int PADDING = 5;
    protected int width;
    protected int height;
    protected int posX;
    protected int posY;

    public int getPosX() {
        return posX;
    }

    public void setPos(int x, int y) {
        this.posX = x;
        this.posY = y;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public abstract void onDraw(int mouseX, int mouseY, float partialTicks);

    public abstract void mouseClicked(int mouseX, int mouseY, int mouseButton);

    public boolean mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick, boolean otherSelected) {
        return false;
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
    }
}
