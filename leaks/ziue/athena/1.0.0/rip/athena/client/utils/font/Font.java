package rip.athena.client.utils.font;

public abstract class Font
{
    public abstract int drawString(final String p0, final double p1, final double p2, final int p3, final boolean p4);
    
    public abstract int drawString(final String p0, final double p1, final double p2, final int p3);
    
    public abstract int drawStringWithShadow(final String p0, final double p1, final double p2, final int p3);
    
    public abstract int width(final String p0);
    
    public abstract int drawCenteredString(final String p0, final double p1, final double p2, final int p3);
    
    public abstract int drawRightString(final String p0, final double p1, final double p2, final int p3);
    
    public abstract float height();
}
