package me.felix.clickgui.abstracts;

public abstract class ClickGUIHandler extends ClickGuiUtils {

    public int width = 100, height = 16;

    /***
     * @param mouseX The given mouseX when you release the mouse.
     * @param mouseY The given mouseY when you release the mouse.
     */
    public abstract void onMouseReleased(int mouseX, int mouseY);

    public abstract void onMouseClicked(int mouseX, int mouseY, int mouseKey);

    public abstract void drawScreen(int mouseX, int mouseY, int x, int y);

    //Method should be called on onGuiClose or something
    public void onResetAnimation() {
    }

    //From Element.class
    public boolean isHovered(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

}
