package dev.elysium.client.ui.elements.targethud;

import dev.elysium.client.ui.elements.Element;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.EntityLivingBase;

public class TargetInfo extends Element {
    public float followx;
    public float followy;
    public TargetInfo(String name, float width, float height) {
        super(name,width,height);
    }

    public void draw(float x, float y, EntityLivingBase en) {

    }

}
