package store.femboy.spring.custommenus;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;

import ru.hogoshi.Animation;
import ru.hogoshi.util.Easings;
import store.femboy.spring.impl.util.ColorUtils;
import store.femboy.spring.impl.util.FontUtil;

import java.awt.*;

public class MainMenu extends GuiScreen {

    public Minecraft mc = Minecraft.getMinecraft();

    public ScaledResolution sr = new ScaledResolution(mc);
    public ResourceLocation resourceLocation = new ResourceLocation("store/femboy/spring/logo.png");

    private final Animation logoAnimation = new Animation();

    public double var1 = 500;

    double var2 = logoAnimation.getValue() - var1;

    public MainMenu(){
    }

    public void initGui(){
        super.initGui();

        logoAnimation.update();
        logoAnimation.animate(var1, 1, Easings.EXPO_OUT,true);

        this.buttonList.add(new GuiButton(0, this.width / 2 - 50, this.height / 2 - 22, 100, 20, "Singleplayer"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 50, this.height / 2 - 0, 100, 20, "Multiplayer"));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 50, this.height / 2 + 22, 100, 20, "AltManager"));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 50, this.height / 2 + 44, 100, 20, "Options"));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 50, this.height / 2 + 66, 100, 20, "Exit"));

    }

    protected void actionPerformed(GuiButton button) {
        switch(button.id) {
            case 0:
                this.mc.displayGuiScreen(new GuiSelectWorld(this));
                break;
            case 1:
                this.mc.displayGuiScreen(new GuiMultiplayer(this));
                break;
            case 2:
                this.mc.displayGuiScreen(new GuiAltManager());
                break;
            case 3:
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;
            case 4:
                this.mc.shutdown();
                break;
        }
    }


    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        drawGradientRect(0, 0, this.width, this.height, new Color(0,0,0).getRGB(), ColorUtils.getRainbow(20, 1f, 1, 180));

        FontUtil.springTitle.drawCenteredString("Spring", (float) (this.width/2-(FontUtil.springTitle.getStringWidth("Spring"))/2)-5, this.height/2 - 40, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
