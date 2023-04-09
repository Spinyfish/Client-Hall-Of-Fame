package tech.dort.dortware.impl.gui.alt.element.impl;

import tech.dort.dortware.impl.gui.alt.element.Element;
import tech.dort.dortware.impl.gui.alt.impl.Alt;

public class AltButton extends Element {

    private final Alt credentials;

    public AltButton(Alt credentials, int x, int y) {
        super(x, y);
        this.credentials = credentials;
    }

    @Override
    public void draw(int x, int y, int mouseX, int mouseY) {

    }

    @Override
    public void click(int mouseX, int mouseY) {

    }


}
