package tech.dort.dortware.impl.gui.click;

/**
 * @author - Aidan#1337
 * @created 12/24/2020 at 3:04 PM
 * Do not distribute this code without credit
 * or ill get final on ur ass
 */

public class PaneState {

    private final String name;
    private int posX, posY;
    private boolean expanded;

    public PaneState(String name, int posX, int posY, boolean expanded) {
        this.name = name;
        this.posX = posX;
        this.posY = posY;
        this.expanded = expanded;
    }

    public String getName() {
        return name;
    }

    public int getPosX() {
        return posX;
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

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
