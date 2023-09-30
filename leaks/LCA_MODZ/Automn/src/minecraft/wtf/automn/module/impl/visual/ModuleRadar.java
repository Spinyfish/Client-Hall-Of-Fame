package wtf.automn.module.impl.visual;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import wtf.automn.events.EventHandler;
import wtf.automn.events.impl.visual.EventRender2D;
import wtf.automn.module.Category;
import wtf.automn.module.Module;
import wtf.automn.module.ModuleInfo;
import wtf.automn.module.settings.SettingBoolean;
import wtf.automn.module.settings.SettingNumber;
import wtf.automn.utils.render.RenderUtils;

import java.awt.*;

@ModuleInfo(name = "radar", displayName = "Radar", category = Category.VISUAL)
public class ModuleRadar extends Module {
    public static int posX = 2;
    public static int posY = 23;
    public static double width = 75;
    public static double height = 75;
    public static int mouseX;
    public static int mouseY;

    public final SettingBoolean rounded = new SettingBoolean("rounded", true, "Rounded", this, "Rounded Rect");
    public final SettingBoolean blur = new SettingBoolean("blur", true, "Blur", this,"Blur");
    public final SettingBoolean cross = new SettingBoolean("cross", true, "Cross", this, "make a Cross in the Middle");
    public final SettingNumber crossSize = new SettingNumber("crossSize", 5D, -5D, 27D, "CrossSize", this,"set the Cross Size");

    @Override
    protected void onDisable() {
    }
    @Override
    protected void onEnable() {
    }
    @EventHandler
    public void onUpdate(final EventRender2D event) {
        final float crossSize = (float) this.crossSize.getValue();
        final ScaledResolution sr = new ScaledResolution(this.MC);
        if (rounded.getBoolean() && !blur.getBoolean()) {
            RenderUtils.drawRoundedRect(sr.getScaledWidth() - 80, 30, 70, 60, 3, Integer.MIN_VALUE);
        }else{
            if (!rounded.getBoolean() && !blur.getBoolean()) {
                Gui.drawRect(sr.getScaledWidth() - 80, 30, sr.getScaledWidth() - 10, 90, Integer.MIN_VALUE);
            }
        }
        if (rounded.getBoolean() && blur.getBoolean()) {
            if(mm.shadow.enabled()) {
                ModuleShadow.drawShadow(() -> RenderUtils.drawRoundedRect(sr.getScaledWidth() - 80, 30, 70, 60, 3, Color.black.getRGB()), false);
            }
            ModuleBlur.drawBlurred(() ->  RenderUtils.drawRoundedRect(sr.getScaledWidth() - 80, 30, 70, 60, 3, Integer.MIN_VALUE), false);
        }else{
            ModuleBlur.drawBlurred(() ->      Gui.drawRect(sr.getScaledWidth() - 80, 30,sr.getScaledWidth()-10,90,Integer.MIN_VALUE), false);
        }
        if (cross.getBoolean()){
            ModuleBloom.drawGlow(() ->   Gui.drawRect(sr.getScaledWidth() - 45,35 + crossSize, sr.getScaledWidth() - 46,85 - crossSize,Color.red.getRGB())  , false);
            ModuleBloom.drawGlow(() ->    Gui.drawRect(sr.getScaledWidth() - 15 - crossSize,60, sr.getScaledWidth() - 75 + crossSize,59, Color.red.getRGB())  , false);
            Gui.drawRect(sr.getScaledWidth() - 45,35 + crossSize, sr.getScaledWidth() - 46,85 - crossSize,-1);
            Gui.drawRect(sr.getScaledWidth() - 15 - crossSize,60, sr.getScaledWidth() - 75 + crossSize,59, -1);
        }else{
            Gui.drawRect(sr.getScaledWidth() - 45,62, sr.getScaledWidth() - 47,60,-1);
        }
        int posX = ModuleRadar.posX + mouseX;
        int posY = ModuleRadar.posY + mouseY;
        double halfWidth =sr.getScaledWidth() - 45;
        double halfHeight = height / 2 - 0.5;
        for (EntityPlayer player : MC.theWorld.playerEntities ) {
            if (player != MC.thePlayer) {
                double playerX = player.posX;
                double playerZ = player.posZ;
                double diffX = playerX - MC.thePlayer.posX;
                double diffZ = playerZ - MC.thePlayer.posZ;
                if (MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ) < 30) {
                    double clampedX = MathHelper.clamp_double(diffX, -halfWidth + 3, halfWidth - 3);
                    double clampedY = MathHelper.clamp_double(diffZ, -halfHeight + 5, halfHeight - 3);
                    Gui.drawRect((float) (posX + halfWidth + clampedX), (float) (posY + halfHeight + clampedY),
                            (float) (posX + halfWidth + clampedX + 1), (float) (posY + halfHeight + clampedY + 1),
                            Color.green.getRGB());
                }
            }
        }
    }
}
