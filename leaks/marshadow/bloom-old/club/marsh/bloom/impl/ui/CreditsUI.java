package club.marsh.bloom.impl.ui;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class CreditsUI extends GuiScreen {
    public String[] credits = new String[] {
            "Thank you marshadow for running the entire client!",
            "Thank you anviso1 for adding the bloom and the blur in the client!",
            "Thank you painsaga on Minecraft for the 10 german cheaters approved gcd fix!",
            "Thank you Appu26j for patching the Bloom Client crack and adding FPS improvements!"
    };

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int y = 45;
        this.drawCenteredString(this.fontRendererObj, "Credits", this.width / 2, 20, -1);
        
        for (int i = 0; i < credits.length; i++) {
        	String credit = credits[i];
        	this.drawCenteredString(this.fontRendererObj, credit, this.width / 2, y, -1);
            y += 15;
        }
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    	super.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    public boolean isInsideBox(int mouseX, int mouseY, int x, int y, int width, int height)
    {
    	return mouseX > x && mouseX < width && mouseY > y && mouseY < height;
    }
}