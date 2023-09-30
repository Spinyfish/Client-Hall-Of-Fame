package club.pulsive.api.ui;

import club.pulsive.altmanager.GuiAltManager;
import club.pulsive.api.font.Fonts;
import club.pulsive.impl.util.render.Draw;
import club.pulsive.impl.util.render.RoundedUtil;
import club.pulsive.impl.util.render.Shader;
import club.pulsive.impl.util.render.secondary.ShaderRound;
import club.pulsive.impl.util.render.secondary.ShaderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class MainMenu extends GuiScreen {
  //  private ShaderUtil shader = new ShaderUtil("mainMenu.frag");
    private ArrayList<String> changelog = new ArrayList<>();
    private long initTime;

    @Override
    public void initGui() {
        initTime = System.currentTimeMillis();
        changelog.add("Recoded");
        changelog.add("Better Speed");
        changelog.add("FPS Optimization");
        changelog.add("New Pulsive Developer! (Secret?!??)");
        changelog.add("New UI");
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution scaledRes = new ScaledResolution(mc);
        this.drawDefaultBackground();
        club.pulsive.impl.util.render.shaders.MainMenu.setTime((System.currentTimeMillis() - initTime) / 1000);
        club.pulsive.impl.util.render.shaders.MainMenu.nigger();
        GL11.glColor4f(1,1,1,0.5f);
       // ShaderRound.drawRound(scaledRes.getScaledWidth() / 2 - 100, scaledRes.getScaledHeight() / 2 - 80, 200, 180, 10,true,  new Color(12, 12, 12, 40));

        ShaderRound.drawRoundOutline(scaledRes.getScaledWidth() / 2 - 100, scaledRes.getScaledHeight() / 2 - 80, 200, 180, 10,1, new Color(12, 12, 12, 0), new Color(RoundedUtil.fadeBetween(new Color(120, 50, 180, 100).getRGB(),new Color(50, 50, 167, 100).getRGB(), RoundedUtil.getOffset(10))));
        Fonts.mainTitle.drawCenteredString("pulsive.club",scaledRes.getScaledWidth() / 2, scaledRes.getScaledHeight() / 2 - 60, new Color(255,255,255,220).getRGB());
        String[] buttons = {"Singleplayer", "Multiplayer", "AltManager" , "Options"};
        int count = 0;
        for(String name : buttons) {
            float x = scaledRes.getScaledWidth() /2;
            float y = 500/buttons.length * count / 5 + scaledRes.getScaledHeight() / 2 -29;
            boolean hovered = mouseX >= x  - 120 && mouseY >= y && mouseX < x + mc.fontRendererObj.getStringWidth(name) + 100 && mouseY < y + mc.fontRendererObj.FONT_HEIGHT + 6;

            ShaderRound.drawRoundOutline(scaledRes.getScaledWidth() / 2 - 2 - 60, 500/buttons.length * count / 5 + scaledRes.getScaledHeight() / 2 -25 - 2 - 3 - 3 + 2, 120 , 20 ,3,0.5f, new Color(12, 12, 12, 50), new Color(RoundedUtil.fadeBetween(new Color(120, 50, 180, 100).getRGB(),new Color(50, 50, 167, 100).getRGB(), RoundedUtil.getOffset(10))));

            Fonts.moon.drawCenteredString(name, scaledRes.getScaledWidth() /2 - 5, 500/buttons.length * count / 5 + scaledRes.getScaledHeight() / 2 -25 - 2 - 3 - 3 + 2 + 6, hovered ? 0x848484 : -1);
            count++;
        }
        Fonts.moontitle.drawStringWithShadow("Changlog:", 4, 4, -1);
        changelog.forEach(change -> {
            Fonts.moon.drawStringWithShadow(change, 12, mc.fontRendererObj.FONT_HEIGHT + 2 + 4 + (mc.fontRendererObj.FONT_HEIGHT + 1) * changelog.indexOf(change), -1);
        });
    }
    public void mouseClicked(int mouseX, int mouseY, int button) {
        ScaledResolution scaledRes = new ScaledResolution(mc);

        String[] buttons = {"Singleplayer", "Multiplayer", "AltManager", "Options"};
            int count = 0;
            for(String name : buttons) {
                float x = scaledRes.getScaledWidth() /2;
                float y = 500/buttons.length * count / 5 + scaledRes.getScaledHeight() / 2 -29;


                    if( mouseX >= x  - 120 && mouseY >= y && mouseX < x + mc.fontRendererObj.getStringWidth(name) + 100 && mouseY < y + mc.fontRendererObj.FONT_HEIGHT + 6) {
                        switch(name) {

                            case "Singleplayer":
                                mc.displayGuiScreen(new GuiSelectWorld(this));
                                break;
                            case "Multiplayer":
                                mc.displayGuiScreen(new GuiMultiplayer(this));
                                break;

                            case "AltManager":
                                this.mc.displayGuiScreen(new GuiAltManager(this));
                                break;


                            case "Options":
                                this.mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));

                                break;


                        }
                    }





                count++;
            }


    }
}
