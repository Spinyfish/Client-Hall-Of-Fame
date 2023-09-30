/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 */
package wtf.monsoon.impl.ui.menu.windows;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import wtf.monsoon.Wrapper;
import wtf.monsoon.impl.ui.menu.windows.Window;
import wtf.monsoon.impl.ui.primitive.Click;

public class WelcomeWindow
extends Window {
    private final ArrayList<String> content = new ArrayList<String>(Arrays.asList("Monsoon is a 1.8 Minecraft client intended", "for servers such as Hypixel and Funcraft.", "It features many features such as", "simple but good looking GUIs, bypassing", "modules, and much, much more!", "", "- Changelog -", ""));

    public WelcomeWindow(float x, float y, float width, float height, float header) {
        super(x, y, width, height, header);
        this.setHeight(this.getHeader() + 2.0f + (float)(this.content.size() * Wrapper.getFont().getHeight()));
        try {
            InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("monsoon/changelog.txt")).getInputStream();
            String streamContent = IOUtils.toString((InputStream)stream);
            Collections.addAll(this.content, streamContent.split(System.lineSeparator()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(float mouseX, float mouseY) {
        super.render(mouseX, mouseY);
        Wrapper.getFont().drawString("Welcome to Monsoon " + Wrapper.getMonsoon().getVersion() + "!", this.getX() + 4.0f, this.getY() + 1.0f, Color.WHITE, false);
        float y = this.getY() + this.getHeader() + 2.0f;
        for (String line : this.content) {
            Wrapper.getFont().drawString(line, this.getX() + 4.0f, y, Color.WHITE, false);
            y += (float)Wrapper.getFont().getHeight();
        }
        this.setHeight(y - this.getY() + 4.0f);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, Click click) {
        super.mouseClicked(mouseX, mouseY, click);
    }
}

