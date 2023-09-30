package Reality.Realii.mods.modules.render;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import Reality.Realii.Client;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.rendering.EventRender2D;
import Reality.Realii.event.value.Mode;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.guis.font.CFontRenderer;
import Reality.Realii.guis.font.FontLoaders;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.mods.modules.ClientSettings;
import Reality.Realii.mods.modules.combat.Killaura;
import Reality.Realii.utils.cheats.world.TimerUtil;
import Reality.Realii.utils.math.AnimationUtils;
import Reality.Realii.utils.render.ColorUtils;
import Reality.Realii.utils.render.RenderUtil;

import java.awt.*;

public class TargetHud extends Module {
    public Mode mod = new Mode("Mode", "Mode", new String[]{"Classic", "NewReality", "Reality"}, "Classic");
    public Numbers<Number> x1 = new Numbers<Number>("X", "X", 80.0, -100, 300.0, 5);
    public Numbers<Number> y1 = new Numbers<Number>("Y", "Y", 80.0, -100, 300.0, 5);
    public Mode fonter = new Mode("FontMode", "FontMode", new String[]{"Robot16", "Vannila"}, "Robot16");
    public Mode colorbk = new Mode("BackroundColor", "BackroundColor", new String[]{"Normal", "Client"}, "Normal");
    public AnimationUtils animationUtils = new AnimationUtils();
    public AnimationUtils animationUtils2 = new AnimationUtils();

    private static final Color COLOR = new Color(0, 0, 0, 100);
    private final TimerUtil animationStopwatch = new TimerUtil();
    private EntityOtherPlayerMP target;
    private float healthBarWidth;
    private float hudHeight;

    public TargetHud() {
        super("TargetHUD", ModuleType.Render);

        addValues(x1, y1, mod, fonter, colorbk);
    }

    float anim = 150;

    @EventHandler
    public void EventRender(EventRender2D e) {
        this.setSuffix(mod.getModeAsString());
        ScaledResolution sr = new ScaledResolution(mc);
        if (Killaura.target != null) {
            EntityLivingBase target = (EntityLivingBase) Killaura.target;
//            GlStateManager.scale(target.hurtTime / 10f, target.hurtTime / 10f, 0);
            if (mod.getValue().equals("NewReality")) {
            	
            	Color c = new Color(ClientSettings.r.getValue().intValue(), ClientSettings.g.getValue().intValue(), ClientSettings.b.getValue().intValue());
                float targetx = (float) (x1.getValue().floatValue() / 100 * sr.getScaledWidth_double());
                float targety = (float) (y1.getValue().floatValue() / 100 * sr.getScaledHeight_double());

                if (Killaura.target != null) {
                    RenderUtil.drawRect(targetx + 35, targety + 40, targetx + 195, targety + 72, new Color(28, 28, 28).getRGB());

                    Client.fontLoaders.msFont18.drawCenteredString(Killaura.target.getName(), (int) targetx + 40 + 75, (int) targety + 45, -1);
                    if (anim < 150 * (Killaura.target.getHealth() / Killaura.target.getMaxHealth())) {
                        anim = (int) (150 * (Killaura.target.getHealth() / Killaura.target.getMaxHealth()));
                    } else if (anim > 150 * (Killaura.target.getHealth() / Killaura.target.getMaxHealth())) {
                        anim -= 120f / mc.debugFPS;
                    }
                    

                    //RenderUtil.drawRect(targetx + 40 - Killaura.target.hurtTime / 2, targety + 60 - Killaura.target.hurtTime / 2, targetx + 40 - Killaura.target.hurtTime / 2 + 150 + Killaura.target.hurtTime, targety + 60 - Killaura.target.hurtTime / 2 + 10 + Killaura.target.hurtTime, new Color(Killaura.target.hurtTime * 10 + 86, 212, 163).getRGB());
                    RenderUtil.drawRect(targetx + 40, targety + 60, targetx + 40 + 150, targety + 65, new Color(Killaura.target.hurtTime * 20, 0, 0).getRGB());
                    
                    Color c2 = new Color(ClientSettings.r.getValue().intValue(), ClientSettings.g.getValue().intValue(), ClientSettings.b.getValue().intValue());

                    RenderUtil.drawRect(targetx + 40 + 150 * (Killaura.target.getHealth() / Killaura.target.getMaxHealth()), targety + 60, targetx + 40 + anim, targety + 65,c2.getRGB());
                    RenderUtil.drawGradientSideways(targetx + 40, targety + 60, targetx + 40 + 150 * (Killaura.target.getHealth() / Killaura.target.getMaxHealth()), targety + 65, c2.getRGB(), c2.getRGB());
                }
            } else if ((mod.getValue().equals("Classic"))) {
                if (Killaura.target != null) {
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    mc.fontRendererObj.drawStringWithShadow(Killaura.target.getName(), sr.getScaledWidth() / 2.0f - mc.fontRendererObj.getStringWidth(Killaura.target.getName()) / 2.0f, sr.getScaledHeight() / 2.0f - 33.0f, 16777215);
                    RenderHelper.enableGUIStandardItemLighting();
                    mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/icons.png"));
                    GL11.glDisable(2929);
                    GL11.glEnable(3042);
                    GL11.glDepthMask(false);
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                    for (int n = 0; n < Killaura.target.getMaxHealth() / 2.0f; ++n) {
                        mc.ingameGUI.drawTexturedModalRect(sr.getScaledWidth() / 2 - Killaura.target.getMaxHealth() / 2.0f * 10.0f / 2.0f + n * 10, (float) (sr.getScaledHeight() / 2 - 20), 16, 0, 9, 9);
                    }
                    for (int n2 = 0; n2 < Killaura.target.getHealth() / 2.0f; ++n2) {
                        mc.ingameGUI.drawTexturedModalRect(sr.getScaledWidth() / 2 - Killaura.target.getMaxHealth() / 2.0f * 10.0f / 2.0f + n2 * 10, (float) (sr.getScaledHeight() / 2 - 20), 52, 0, 9, 9);
                    }
                    GL11.glDepthMask(true);
                    GL11.glDisable(3042);
                    GL11.glEnable(2929);
                    GlStateManager.disableBlend();
                    GlStateManager.color(1.0f, 1.0f, 1.0f);
                    RenderHelper.disableStandardItemLighting();

                }
            } else if ((mod.getValue().equals("Reality"))) {
                Killaura aura = (Killaura) Client.instance.getModuleManager().getModuleByClass(Killaura.class);
                float scaledWidth = sr.getScaledWidth();
                float scaledHeight = sr.getScaledHeight();
                if (Killaura.target != null && aura.isEnabled()) {
//                if (Killaura.target instanceof EntityOtherPlayerMP) {
//                Killaura.target = (EntityOtherPlayerMP) Killaura.target;
                    float width = 140.0f;
                    float height = 40.0f;
                    float xOffset = 40.0f;
                    float x = scaledWidth / 2.0f - (float) (x1.getValue().floatValue());
                    float y = scaledHeight / 2.0f + (float) (y1.getValue().floatValue());
                    float health = Killaura.target.getHealth();
                    float hpPercentage = health / Killaura.target.getMaxHealth();
                    hpPercentage = MathHelper.clamp_float(hpPercentage, 0.0f, 1.0f);
                    float hpWidth = 92.0f * hpPercentage;
                    int healthColor = ColorUtils.getHealthColor(Killaura.target.getHealth(), Killaura.target.getMaxHealth()).getRGB();
                    String healthStr = String.valueOf((float) ((int) Killaura.target.getHealth()) / 2.0f);
//                    this.healthBarWidth = hpWidth;
//                    this.hudHeight = 40.0f;
                    this.healthBarWidth = (float) animationUtils.animate(hpWidth, this.healthBarWidth, 0.353f);
                    this.hudHeight = (float) animationUtils2.animate(40.0, this.hudHeight, 0.1);
                    GL11.glEnable((int) 3089);
                    RenderUtil.prepareScissorBox(x, y, x + 140.0f, y + this.hudHeight);
                    if (this.colorbk.getValue().equals("Normal")) {
                    Gui.drawRect(x, y, x + 140.0f, y + 40.0f, COLOR.getRGB());
                    } else if (this.colorbk.getValue().equals("Client")) {
                    	Color c = new Color(ClientSettings.r.getValue().intValue(), ClientSettings.g.getValue().intValue(), ClientSettings.b.getValue().intValue(), 200);
                        Gui.drawRect(x, y, x + 140.0f, y + 40.0f, c.getRGB());
                      }
                    if ((fonter.getValue().equals("Robot16"))) {
                    CFontRenderer font = FontLoaders.roboto16;
                    Color c = new Color(ClientSettings.r.getValue().intValue(), ClientSettings.g.getValue().intValue(), ClientSettings.b.getValue().intValue(), 100);
                    Gui.drawRect(x + 40.0f, y + 39.0f, (double) (x + 46.8f) + this.healthBarWidth, y + 25.0f, c.getRGB());
                    font.drawStringWithShadow("Health :" + healthStr, x + 1.0f + 46.0f - (float) mc.fontRendererObj.getStringWidth(healthStr) / 3.0f, y + 14.0f, -1);
                    font.drawStringWithShadow(Killaura.target.getName(), x + 40.0f, y + 5.0f, -1);
                  
                    
                   
                    }
                    if ((fonter.getValue().equals("Vannila"))) {
                    	Color c = new Color(ClientSettings.r.getValue().intValue(), ClientSettings.g.getValue().intValue(), ClientSettings.b.getValue().intValue(), 100);
                        Gui.drawRect(x + 40.0f, y + 39.0f, (double) (x + 46.8f) + this.healthBarWidth, y + 25.0f, c.getRGB());
                        mc.fontRendererObj.drawStringWithShadow("Health :" + healthStr, x + 1.0f + 46.0f - (float) mc.fontRendererObj.getStringWidth(healthStr) / 3.0f, y + 14.0f, -1);
                        mc.fontRendererObj.drawStringWithShadow(Killaura.target.getName(), x + 40.0f, y + 5.0f, -1);
                      
                        }
                    
                    GuiInventory.drawEntityOnScreen((int) (x + 13.333333f), (int) (y + 40.0f), 20, Killaura.target.rotationYaw, Killaura.target.rotationPitch, Killaura.target);
                    GL11.glDisable((int) 3089);
//                }
                } else {
                    this.healthBarWidth = 92.0F;
                    this.hudHeight = 0.0F;
//                Killaura.target = null;
                }
//                GlStateManager.scale(1 / (target.hurtTime / 10f), 1 / (target.hurtTime / 10f), 0);
            }
        }
    }
}
