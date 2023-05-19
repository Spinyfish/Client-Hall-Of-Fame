package club.marsh.bloom.impl.mods.render;

import org.lwjgl.input.Keyboard;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.ModeValue;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.EnumAction;
import net.minecraft.util.MathHelper;

public class Animations extends Module {
	public static boolean on = false;
	public Animations() {
		super("Animations",Keyboard.KEY_NONE,Category.VISUAL);
	}
	public static ModeValue modeValue = new ModeValue("Animation","Bloom",new String[] {"Bloom","Vanilla","Screwdriver"});
	@Override
	public void onEnable() {
		on = true;
	}
	@Override
	public void onDisable() {
		on = false;
	}
	@Override
	public void addModesToModule() {
		autoSetName(modeValue);
	}
    public static boolean animate(float partialticks, float inuseticks, EnumAction action, float swingProgress, float equipProgress) {
        if (!on || swingProgress <= 0)
            return false;
        else {
            switch (modeValue.getMode()) {
                case "Bloom": {
                    if (action == EnumAction.BLOCK) {
                        if (swingProgress> 0) {
                            GlStateManager.translate(0.56F, -0.42F, -0.71999997F);
                            GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
                            GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                            float var3 = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
                            float var4 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
                            GlStateManager.rotate(var4 * -10, 1, 0, 1);
                            GlStateManager.rotate(var4 * -20, 0.0F, 1.0F, 0.0F);
                            GlStateManager.rotate(var4 * -30, 0.0F, 0.0F, 1.0F);
                            //GlStateManager.rotate(var4 * -40.0F, 1.0F, 0.0F, 0.0F);
                            GlStateManager.scale(0.4F, 0.4F, 0.4F);
                        }
                    }
                    break;
                }
                case "Screwdriver": {
                    if (action == EnumAction.BLOCK) {
                        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
                        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
                        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                        float var4 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
                        GlStateManager.rotate(var4 * -10, 1, 0, 1);
                        GlStateManager.rotate(var4 * -40.0F, 0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate(var4 * -60.0F, 0.0F, 0.0F, 1.0F);
                        GlStateManager.rotate(var4 * -80.0F, 1.0F, 0.0F, 0.0F);
                        GlStateManager.scale(0.4,0.4,0.4);
                    }
                    break;
                }
                case "Vanilla": {

                    GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
                    GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
                    GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                    float f = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
                    float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
                    GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
                    GlStateManager.scale(0.4F, 0.4F, 0.4F);
                    break;
                }
            }
            return true;
        }

    }
}
