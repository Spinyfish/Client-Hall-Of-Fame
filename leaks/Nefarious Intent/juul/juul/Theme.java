package juul;

import java.awt.*;

public enum Theme {

    VIRGINIA_TOBACCO(0, new Color(93,50,54)),
    CLASSIC_TOBACCO(1, new Color(145,88,63)),
    MINT(2, new Color(86,192,166)),
    MANGO(3, new Color(247,152,82)),
    CREME(4, new Color(253,207,146)),
    FRUIT(5, new Color(237,45,55)),
    MENTHOL(6, new Color(69,191,181)),
    CUCUMBER(7, new Color(48, 172, 97));

    private final Color color;
    private final int id;

    Theme(int id, Color c)
    {
        this.id = id;
        this.color = c;
    }

    public int id() { return this.id; }
    public int rgb() { return this.color.getRGB(); }
    public Color color() { return this.color; }
}