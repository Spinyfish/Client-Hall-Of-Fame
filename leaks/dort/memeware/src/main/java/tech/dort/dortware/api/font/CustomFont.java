package tech.dort.dortware.api.font;

public class CustomFont {

    private final String name;
    private final CustomFontRenderer renderer;

    public CustomFont(String name, CustomFontRenderer renderer) {
        this.name = name;
        this.renderer = renderer;
    }

    public String getName() {
        return name;
    }

    public CustomFontRenderer getRenderer() {
        return renderer;
    }
}
