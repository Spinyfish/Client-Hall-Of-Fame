package dev.elysium.client.ui.elements;

import dev.elysium.client.Elysium;
import dev.elysium.client.ui.font.FontManager;
import dev.elysium.client.ui.font.TTFFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.EntityLivingBase;

public class Element {
    public Minecraft mc = Minecraft.getMinecraft();
    public FontRenderer mcfr = mc.fontRendererObj;
    public String name;
    public float width;
    public float height;
    public float smoothvalue1;
    public float smoothvalue2;

    public Element(String name, float width, float height) {
        this.name = name;
        this.width = width;
        this.height = height;
    }

    public void draw(float x, float y) {

    }
}
