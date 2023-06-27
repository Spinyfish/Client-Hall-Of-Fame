package rip.athena.client.gui.screen;

public interface Screen
{
    void initGui();
    
    void keyTyped(final char p0, final int p1);
    
    void drawScreen(final int p0, final int p1);
    
    void mouseClicked(final int p0, final int p1, final int p2);
    
    void mouseReleased(final int p0, final int p1, final int p2);
}
