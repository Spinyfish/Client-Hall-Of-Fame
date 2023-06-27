package rip.athena.client.gui.framework.components;

import rip.athena.client.gui.framework.*;

public class MenuColumn extends MenuComponent
{
    protected int column;
    
    public MenuColumn(final int column) {
        super(0, 0, 0, 0);
        this.column = 0;
        this.column = column;
    }
    
    public int getColumn() {
        return this.column;
    }
    
    public void setColumn(final int column) {
        this.column = column;
    }
}
