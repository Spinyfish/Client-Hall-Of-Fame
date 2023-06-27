package rip.athena.client.theme;

import rip.athena.client.theme.impl.*;

public class ThemeManager
{
    private AccentTheme theme;
    private PrimaryTheme primaryTheme;
    
    public ThemeManager() {
        this.theme = AccentTheme.ATHENA;
        this.primaryTheme = PrimaryTheme.DARK;
    }
    
    public AccentTheme getTheme() {
        return this.theme;
    }
    
    public PrimaryTheme getPrimaryTheme() {
        return this.primaryTheme;
    }
    
    public void setTheme(final AccentTheme theme) {
        this.theme = theme;
    }
    
    public void setPrimaryTheme(final PrimaryTheme primaryTheme) {
        this.primaryTheme = primaryTheme;
    }
}
