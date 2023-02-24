package me.kansio.client.modules.impl.visuals;

import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.value.value.BooleanValue;
import me.kansio.client.value.value.ModeValue;
import me.kansio.client.value.value.NumberValue;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

@ModuleData(
        name = "Animations",
        category = ModuleCategory.VISUALS,
        description = "Custom client animations"
)
public class Animations extends Module {

    public BooleanValue attackanim = new BooleanValue("Attack Animations", this, false);
    public BooleanValue smoothhit = new BooleanValue("Smooth Hit", this, false, attackanim);
    public NumberValue<Float> scale = new NumberValue<>("Scale", this, 1.0f, 0.0f, 2.0f, 0.1f, attackanim);
    public NumberValue<Integer> slowdown = new NumberValue<>("Swing Speed", this, 1, -4, 12, 1, attackanim);
    private ModeValue modeblockanim = new ModeValue("Block Mode", this, attackanim, "Normal", "Hide", "1.7", "Ethereal", "Stella", "Interia", "Styles", "Slide", "Lucky", "Remix", "Swang", "Down", "Knife", "Exhi", "oHare", "oHare2", "Wizzard", "Lennox", "ETB", "Spin", "Rotate");
    private float rotate;

    private void func_178103_d(final float n) {
        GlStateManager.translate(-0.5f, n, 0.0f);
        GlStateManager.rotate(30.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-80.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(60.0f, 0.0f, 1.0f, 0.0f);
    }

    public void render(ItemStack itemToRender, float partialTicks) {
        rotate++;
        float f = 1.0F - (mc.getItemRenderer().prevEquippedProgress + (mc.getItemRenderer().equippedProgress - mc.getItemRenderer().prevEquippedProgress) * partialTicks);
        EntityPlayerSP entityplayersp = mc.thePlayer;
        float f1 = entityplayersp.getSwingProgress(partialTicks);
        float f2 = entityplayersp.prevRotationPitch + (entityplayersp.rotationPitch - entityplayersp.prevRotationPitch) * partialTicks;
        float f3 = entityplayersp.prevRotationYaw + (entityplayersp.rotationYaw - entityplayersp.prevRotationYaw) * partialTicks;
        final float var = MathHelper.sin((float) (MathHelper.sqrt_float(f1) * Math.PI));
        final float sin = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927f);
        if (attackanim.getValue()) {
            GlStateManager.scale(scale.getValue(), scale.getValue(), scale.getValue());
            switch (modeblockanim.getValue().toUpperCase()) {
                case "Stella":
                    mc.getItemRenderer().transformFirstPersonItem(f, 0.0f);
                    func_178103_d(0.2f);
                    GlStateManager.translate(0.1f, 0.2f, 0.3f);
                    GlStateManager.rotate(-var * 30.0f, -5.0f, 0.0f, 9.0f);
                    GlStateManager.rotate(-var * 10.0f, 1.0f, -0.4f, -0.5f);
                    break;
                case "1.7":
                    mc.getItemRenderer().transformFirstPersonItem(0, f1);
                    mc.getItemRenderer().func_178103_d();
                    break;
                case "INTERIA":
                    mc.getItemRenderer().transformFirstPersonItem(0.05f, f1);
                    GlStateManager.translate(-0.5f, 0.5f, 0.0f);
                    GlStateManager.rotate(30.0f, 0.0f, 1.0f, 0.0f);
                    GlStateManager.rotate(-80.0f, 1.0f, 0.0f, 0.0f);
                    GlStateManager.rotate(60.0f, 0.0f, 1.0f, 0.0f);
                    break;
                case "ETHEREAL":
                    mc.getItemRenderer().transformFirstPersonItem(f, 0.0f);
                    this.func_178103_d(0.2f);
                    GlStateManager.translate(-0.05f, 0.2f, 0.2f);
                    GlStateManager.rotate(-sin * 70.0f / 2.0f, -8.0f, -0.0f, 9.0f);
                    GlStateManager.rotate(-sin * 70.0f, 1.0f, -0.4f, -0.0f);
                    break;
                case "STYLES":
                    mc.getItemRenderer().transformFirstPersonItem(f, 0.0f);
                    this.func_178103_d(0.2f);
                    final float sin2 = MathHelper.sin((float) (MathHelper.sqrt_float(f1) * 3.141592653589793));
                    GlStateManager.translate(-0.05f, 0.2f, 0.0f);
                    GlStateManager.rotate(-sin2 * 70.0f / 2.0f, -8.0f, -0.0f, 9.0f);
                    GlStateManager.rotate(-sin2 * 70.0f, 1.0f, -0.4f, -0.0f);
                    break;
                case "NORMAL":
                    mc.getItemRenderer().transformFirstPersonItem(0, 0.0f);
                    mc.getItemRenderer().func_178103_d();
                    float var8 = MathHelper.sin(f1 * f1 * 0.3215927f);
                    float var9 = MathHelper.sin(MathHelper.sqrt_float(0) * 0.3215927f);
                    GlStateManager.translate(-0.0f, -0f, 0.2f);

                    break;
                case "HIDE":

                    mc.getItemRenderer().func_178105_d(f1);
                    mc.getItemRenderer().transformFirstPersonItem(f, f1);

                    break;
                case "SWANG":
                    mc.getItemRenderer().transformFirstPersonItem(f / 2.0f, f1);
                    final float sin4 = MathHelper.sin((float) (MathHelper.sqrt_float(f1) * 3.141592653589793));
                    GlStateManager.rotate(sin4 * 30.0f / 2.0f, -sin4, -0.0f, 9.0f);
                    GlStateManager.rotate(sin4 * 40.0f, 1.0f, -sin4 / 2.0f, -0.0f);
                    this.func_178103_d(0.4f);
                    break;
                case "SLIDE":

                    mc.getItemRenderer().transformFirstPersonItem(0, 0.0f);
                    mc.getItemRenderer().func_178103_d();
                    var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927f);
                    GlStateManager.translate(-0.05f, -0.0f, 0.35f);
                    GlStateManager.rotate(-var9 * (float) 60.0 / 2.0f, -15.0f, -0.0f, 9.0f);
                    GlStateManager.rotate(-var9 * (float) 70.0, 1.0f, -0.4f, -0.0f);

                    break;
                case "LUCKY":

                    mc.getItemRenderer().transformFirstPersonItem(0, 0.0f);
                    mc.getItemRenderer().func_178103_d();
                    var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * 0.3215927f);
                    GlStateManager.translate(-0.05f, -0.0f, 0.3f);
                    GlStateManager.rotate(-var9 * (float) 60.0 / 2.0f, -15.0f, -0.0f, 9.0f);
                    GlStateManager.rotate(-var9 * (float) 70.0, 1.0f, -0.4f, -0.0f);

                    break;
                case "KNIFE":
                    GlStateManager.translate(0.41F, -0.25F, -0.5555557F);
                    GlStateManager.translate(0.0F, 0, 0.0F);
                    GlStateManager.rotate(35.0F, 0f, 1.5F, 0.0F);
                    float gay = MathHelper.sin(f1 * f1 / 64 * (float) Math.PI);
                    float gay2 = MathHelper.sin(MathHelper.sqrt_float(f1) / .4f * (float) Math.PI);
                    float amaz = MathHelper.sin(MathHelper.sqrt_float(f1) / .4f * (float) Math.PI);
                    GlStateManager.rotate(gay * -5.0F, 0.0F, 0.0F, 0.0F);
                    GlStateManager.rotate(gay2 * -12.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.rotate(gay2 * -65.0F, 1.0F, 0.0F, 0.0F);

                    float size = 0.295F;
                    GlStateManager.scale(size, size, size);
                    break;
                case "DOWN":

                    mc.getItemRenderer().transformFirstPersonItem(f1 - 0.125F, 0);
                    GlStateManager.rotate(-var * 55 / 2.0F, -8.0F, 0.4f, 9.0F);
                    GlStateManager.rotate(-var * 45, 1.0F, var / 2, -0.0F);
                    GlStateManager.translate(0.0f, 0.1F, 0.0f);
                    mc.getItemRenderer().func_178103_d();

                    break;
                case "EXHI":

                    float f6 = MathHelper.sin((float) (MathHelper.sqrt_float(f1) * 3.1));
                    GL11.glTranslated(-0.1D, 0.1D, 0.0D);
                    // mc.getItemRenderer().transformFirstPersonItem(f / 3, 0.0f);
                    mc.getItemRenderer().transformFirstPersonItem(f / 2, 0.0f);
                    GlStateManager.rotate(-f6 * 40.0F / 2.0F, f6 / 2.0F, -0.0F, 9.0F);
                    GlStateManager.rotate(-f6 * 30.0F, 1.0F, f6 / 2.0F, -0.0F);
                    mc.getItemRenderer().func_178103_d();

                    break;
                case "OHARE2":

                    mc.getItemRenderer().transformFirstPersonItem(f1, 0.0F);
                    mc.getItemRenderer().func_178103_d();
                    GlStateManager.translate(-0.05F, 0.6F, 0.3F);
                    GlStateManager.rotate(-var * 70.0F / 2.0F, -8.0F, -0.0F, 9.0F);
                    GlStateManager.rotate(-var * 70.0F, 1.5F, -0.4F, -0.0F);

                    break;
                case "REMIX":
                    mc.getItemRenderer().transformFirstPersonItem(f / 2, 0);
                    mc.getItemRenderer().func_178103_d();
                    GL11.glRotatef(-var * (float) 50.0 / 2.0f, -8.0f, 0.4f, 9.0f);
                    GL11.glRotatef(-var * (float) 40, 1.5f, -0.4f, -0.0f);
                    break;
                case "OHARE":

                    f6 = MathHelper.sin((MathHelper.sqrt_float(f1) * 3.1415927f));
                    GL11.glTranslated(-0.05D, 0.0D, -0.25);
                    mc.getItemRenderer().transformFirstPersonItem(f / 2, 0.0f);
                    GlStateManager.rotate(-f6 * 60.0F, 2.0F, -f6 * 2, -0.0f);
                    mc.getItemRenderer().func_178103_d();

                    break;
                case "WIZZARD":

                    f6 = MathHelper.sin((float) (MathHelper.sqrt_float(f1) * 3.1));
                    mc.getItemRenderer().transformFirstPersonItem(f / 3, 0.0f);
                    GlStateManager.rotate(f6 * 30.0F / 1.0F, f6 / -1.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(f6 * 10.0F / 10.0F, -f6 / -1.0F, 1.0F, 0.0F);
                    GL11.glTranslated(0.0D, 0.4D, 0.0D);
                    mc.getItemRenderer().func_178103_d();

                    break;
                case "LENNOX":

                    f6 = MathHelper.sin((float) (MathHelper.sqrt_float(f1) * 3.1));
                    GL11.glTranslated(0.0D, 0.125D, -0.1D);
                    mc.getItemRenderer().transformFirstPersonItem(f / 3, 0.0F);
                    GlStateManager.rotate(-f6 * 75.0F / 4.5F, f6 / 3.0F, -2.4F, 5.0F);
                    GlStateManager.rotate(-f6 * 75.0F, 1.5F, f6 / 3.0F, -0.0F);
                    GlStateManager.rotate(f6 * 72.5F / 2.25F, f6 / 3.0F, -2.7F, 5.0F);
                    mc.getItemRenderer().func_178103_d();

                    break;
                case "ETB":

                    mc.getItemRenderer().transformFirstPersonItem(f, 0.0F);
                    mc.getItemRenderer().func_178103_d();
                    var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927f);
                    GlStateManager.translate(-0.05f, 0.6f, 0.3f);
                    GlStateManager.rotate(-var9 * (float) 80.0 / 2.0f, -4.0f, -0.0f, 18.0f);
                    GlStateManager.rotate(-var9 * (float) 70.0, 1.5f, -0.4f, -0.0f);

                    break;
                case "SPIN":

                    mc.getItemRenderer().transformFirstPersonItem(f, 0.0f);
                    mc.getItemRenderer().func_178103_d();
                    GL11.glRotatef(rotate, rotate, 0, rotate);
                    GL11.glScalef(0.5f, 0.5f, 0.5F);
                    //GL11.glTranslatef(0, 5, 0);
                    rotate += slowdown.getValue();

                    break;
                case "ROTATE":
                    GL11.glTranslated(-0.04, 0.1, 0.0);
                    mc.getItemRenderer().transformFirstPersonItem(f / 2.5f, 0.0f);
                    GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.2f);
                    GlStateManager.rotate(rotate, 0.0f, -1.0f, 0.0f);
                    break;
            }
        }
    }

    @Override
    public String getSuffix() {
        return " " + modeblockanim.getValue();
    }

    private void doBlockTransformations() {
        GlStateManager.translate(-0.5F, 0.2F, 0.0F);
        GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
    }

}
