package rip.athena.client.gui.framework.components;

import rip.athena.client.gui.framework.*;

public class MenuPage extends MenuComponent
{
    protected int page;
    
    public MenuPage(final int page) {
        super(0, 0, 0, 0);
        this.page = 0;
        this.page = page;
    }
    
    public int getPage() {
        return this.page;
    }
    
    public void setPage(final int page) {
        this.page = page;
    }
}
