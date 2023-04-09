package tech.dort.dortware.impl.modules.render;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.RenderItemEvent;

public class Animations extends Module {

    public final EnumValue<Mode> enumValue = new EnumValue<>("Mode", this, Animations.Mode.values());
    public final NumberValue slowdown = new NumberValue("Swing Slowdown", this, 0, 0, 12, true);
    public final BooleanValue smoothSwing = new BooleanValue("Smooth Swing", this, false);

    public Animations(ModuleData moduleData) {
        super(moduleData);
        register(enumValue, slowdown, smoothSwing);
    }

    @Subscribe
    public void onItemRender(RenderItemEvent event) {
        final float var = MathHelper.sin((float) (MathHelper.sqrt_float(event.getSwingProgress()) * Math.PI));
        ItemRenderer itemRenderer = mc.getItemRenderer();

        if (event.getUseAction() == RenderItemEvent.ItemUseAction.BLOCK) {
            switch (enumValue.getValue()) {
                case VANILLA: // Credits: Mojang
                    itemRenderer.func_178096_b(event.getUseProgress(), event.getSwingProgress());
                    itemRenderer.func_178103_d();
                    break;
                case SWING: // Credits: Virtue
                    itemRenderer.func_178096_b(event.getUseProgress(), event.getSwingProgress());
                    itemRenderer.func_178103_d();
                    GL11.glTranslated(-0.25D, 0.2D, 0.0D);
                    GL11.glTranslatef(-0.05F, mc.thePlayer.isSneaking() ? -0.2F : 0.0F, 0.1F);
                    break;
                case WARPED: // Credits: Unknown
                    itemRenderer.func_178096_b(event.getUseProgress(), 0);
                    itemRenderer.func_178103_d();
                    GlStateManager.rotate(-var * 55, 1.0F, 0.0F, 0.0F);
                    GlStateManager.rotate(-var * 45, 1.0F, 1, 0.0F);
                    break;
                case EXHIBITION: // Credits: Arithmo
                    itemRenderer.func_178096_b(event.getUseProgress() - 0.125F, 0);
                    GlStateManager.rotate(-var * 55 / 2.0F, -8.0F, 0.4f, 9.0F);
                    GlStateManager.rotate(-var * 45, 1.0F, var / 2, -0.0F);
                    GlStateManager.translate(0.0f, 0.1F, 0.0f);
                    itemRenderer.func_178103_d();
                    break;
                case PUSH: // Credits: Unknown
                    itemRenderer.func_178096_b(event.getUseProgress() - 0.2f, 0.0f);
                    itemRenderer.func_178103_d();
                    GlStateManager.rotate(-var * 17.0F, 1.0F, 0.0F, 1.0F);
                    GlStateManager.rotate(-var * 17.0F, 1.0F, 0.0F, 1.0F);
                    GL11.glTranslated(-0.1, -0.4, 0);
                    GlStateManager.rotate(-var * -48.0F, 32.0F, 128.0F, 54.0F);
                    GlStateManager.translate(event.getSwingProgress() * 0.3, event.getSwingProgress() * 0.3, event.getSwingProgress() * 0.3);
                    break;
                case SLIDE: // Credits: Unknown
                    final float smooth = event.getSwingProgress() * 0.78f - event.getSwingProgress() * event.getSwingProgress() * 0.78f;
                    itemRenderer.func_178096_b(-0.25F, 0.0F);
                    GlStateManager.scale(1.7f, 1.7f, 1.7f);
                    GlStateManager.rotate(48.0f, 0.0f, -0.6f, 0.0f);
                    GlStateManager.translate(-0.3f, 0.4f, 0.0f);
                    GlStateManager.translate(0.0f, 0.08f, 0.0f);
                    GlStateManager.translate(0.56f, -0.489f, -0.71999997f);
                    GlStateManager.translate(0.0f, 0.0f, 0.0f);
                    GlStateManager.rotate(52.0f, 0.0f, 180.0f + smooth * 0.5f, smooth * 20.0f);
                    GlStateManager.rotate(var * -41.3f, 2.0f, 0.0f, 0.0f);
                    GlStateManager.translate(0.0f, -0.2f, 0.0f);
                    itemRenderer.func_178103_d();
                    break;
                case SPIN: // Credits: Auth
                    itemRenderer.func_178096_b(event.getUseProgress() / 2.0F, 0.0F);
                    GlStateManager.rotate((-event.getSwingProgress() * 750.0F) / 2.0F, event.getSwingProgress(), -0.0F, 10.0F);
                    itemRenderer.func_178103_d();
                    break;
                case IN: // Credits: Unknown
                    itemRenderer.func_178096_b(event.getUseProgress(), event.getSwingProgress());
                    GlStateManager.translate(0.05F, 0.2F, 0.05F);
                    GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(50.0F, 1.0F, 0.0F, 0.0F);
                    break;
                case SLAP: // Credits: Auth
                    itemRenderer.func_178096_b(event.getUseProgress(), event.getSwingProgress());
                    GlStateManager.translate(0.05F, 0.2F, 0.05F);
                    GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(50.0F, 1.0F, 0.0F, 0.0F);
                    itemRenderer.func_178103_d();
                    break;
                case ASTRO: // Credits: Zane
                    itemRenderer.func_178096_b(event.getUseProgress() / 2, event.getSwingProgress());
                    GL11.glRotatef(var * 50.0F / 9.0F, -var, -0.0F, 90.0F);
                    GL11.glRotatef(var * 50.0F, 200.0F, -var / 2.0F, -0.0F);
                    itemRenderer.func_178103_d();
                    break;
                case OLD_ASTRO: // Credits: Zane
                    itemRenderer.func_178096_b(event.getUseProgress() / 2 - .3f, event.getSwingProgress());
                    GlStateManager.rotate(var * 30.0F / 2.0F, -var, -0.0F, 9.0F);
                    GlStateManager.rotate(var * 40.0F, 1.0F, -var / 2.0F, -0.0F);
                    itemRenderer.func_178103_d();
                    break;
                case SIGMA: // Credits: http://pool.supportxmr.com/
                    itemRenderer.func_178096_b(event.getUseProgress() * 0.5F, 0);
                    GlStateManager.rotate(-var * 55 / 2.0F, -8.0F, -0.0F, 9.0F);
                    GlStateManager.rotate(-var * 45, 1.0F, var / 2, 0.0F);
                    itemRenderer.func_178103_d();
                    GL11.glTranslated(1.2, 0.3, 0.5);
                    break;
                case SMALL: // Credits: Unknown
                    GL11.glTranslated(0.5D, 0.0D, -0.5D);
                    itemRenderer.func_178096_b(event.getUseProgress() - 0.2f, event.getSwingProgress());
                    itemRenderer.func_178103_d();
                    break;
                case WEIRD: // Credits: http://pool.supportxmr.com/ & Auth
                    GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
                    GlStateManager.translate(0.0F, 0, 0.0F);
                    GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(MathHelper.sin(event.getSwingProgress() * event.getSwingProgress() * (float) Math.PI) * -20.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(var * -20.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.rotate(var * -40.0F, 1.0F, 0.0F, 0.0F);
                    GlStateManager.scale(0.4F, 0.4F, 0.4F);
                    break;
                case MELIDA: // Credits: iBhopper
                    itemRenderer.func_178096_b(event.getUseProgress() - 0.1F, 0);
                    itemRenderer.func_178103_d();
                    GlStateManager.rotate(-var * 36F, 1F, 0, 1F);
                    GL11.glTranslated(-0.1, -0.8, 0.2);
                    GlStateManager.translate(var * 0.1F, var * 0.3F, var * 0.6F);
                    break;
                case TEMPERANCE: // Credits: iBhopper
                    itemRenderer.func_178096_b(event.getUseProgress() - 0.2f, 0.0f);
                    itemRenderer.func_178103_d();
                    GlStateManager.rotate(-var * 20.0f, 1.0f, 0.0f, 1.0f);
                    GlStateManager.rotate(-var * 20.0f, 1.0f, 0.0f, 1.0f);
                    break;
                case NOON: // Credits: iBhopper
                    itemRenderer.func_178096_b(event.getUseProgress() - 0.3f, event.getSwingProgress() - 0.2f);
                    itemRenderer.func_178103_d();
                    GlStateManager.translate(-0.0f, 0.1f, 0.3f);
                    GlStateManager.translate(event.getSwingProgress() * 0.5, event.getSwingProgress() * 0.5, event.getSwingProgress() * 0.5);
                    GlStateManager.rotate(-var * 8.0f, 1.0f, 0.0f, 1.0f);
                    break;
                case SAMURAI: // Credits: iBhopper
                    itemRenderer.func_178096_b(event.getUseProgress() - 0.3f, 0.0f);
                    itemRenderer.func_178103_d();
                    GlStateManager.rotate(-var * 12, 3.0f, 0.0f, 1.0f);
                    GlStateManager.rotate(-var * 12.0f, 3.0f, 0.0f, 1.0f);
                    GL11.glTranslated(-0.1, -0.4, 0);
                    break;
                case OHARE: // Credits: oHare
                    itemRenderer.func_178096_b(event.getUseProgress(), 0.0F);
                    itemRenderer.func_178103_d();
                    GlStateManager.translate(-0.05F, 0.6F, 0.3F);
                    GlStateManager.rotate(-var * 70.0F / 2.0F, -8.0F, -0.0F, 9.0F);
                    GlStateManager.rotate(-var * 70.0F, 1.5F, -0.4F, -0.0F);
                    break;
                case REMIX: // Credits: Remix Client
                    itemRenderer.func_178096_b(event.getUseProgress(), event.getSwingProgress() / 40.0f);
                    itemRenderer.func_178103_d();
                    GlStateManager.translate(-0.1f, 0.0f, 0.0f);
                    break;
                case POKE: // Credits: shotbowxd
                    itemRenderer.func_178096_b(event.getUseProgress(), 1.0f);
                    itemRenderer.func_178103_d();
                    GL11.glTranslatef(0.1f, 0.3f, 0.7f);
                    GlStateManager.translate(-0.4f, -0.1f, -0.2f);
                    GlStateManager.rotate(var * 33.0f, 85.0f, -0.4f, -5.0f);
                    break;
                case HELIUM: // Credits: Helium Client
                    itemRenderer.func_178096_b(event.getUseProgress(), 1.0f);
                    itemRenderer.func_178103_d();
                    GL11.glTranslatef(0.4f, 0.1f, -0.01f);
                    GlStateManager.translate(-0.1f, 0.3f, 0.2f);
                    GlStateManager.rotate(var * 13.0f, -10.0f, -1.4f, -10.0f);
                    break;
                case SLOW: // Credits: shotbowxd
                    itemRenderer.func_178096_b(event.getUseProgress(), event.getSwingProgress() / 40);
                    itemRenderer.func_178103_d();
                    GlStateManager.translate(-0.5, 0, 0);
                    break;
                case SPAZ: // Credits: Auth
                    itemRenderer.func_178096_b(event.getUseProgress(), event.getSwingProgress() * 10);
                    itemRenderer.func_178103_d();
                    break;
                case VIBRATE: // Credits: Auth & shotbowxd
                    itemRenderer.func_178096_b(event.getUseProgress(), 1.0f);
                    GlStateManager.rotate(-var * 10, 0.0f, 15.0f, 200.0f);
                    GlStateManager.rotate(-var * 10, 300.0f, var / 2.0f, 1.0f);
                    itemRenderer.func_178103_d();
                    GL11.glTranslated(2.4, 0.3, 0.5);
                    GL11.glTranslatef(-2.10f, -0.2f, 0.1f);
                    GlStateManager.rotate(MathHelper.sin((float) (event.getSwingProgress() * event.getSwingProgress() * Math.PI - 3) * 13.0f), -10.0f, -1.4f, -10.0f);
                    break;
                case DORTWARE: // Credits: Auth & shotbowxd
                    final float var1 = MathHelper.sin((float) (event.getSwingProgress() * event.getSwingProgress() * Math.PI - 3));
                    itemRenderer.func_178096_b(event.getUseProgress(), 1.0f);
                    GlStateManager.rotate(-var * 10, 0.0f, 15.0f, 200.0f);
                    GlStateManager.rotate(-var * 10f, 300.0f, var / 2.0f, 1.0f);
                    itemRenderer.func_178103_d();
                    GL11.glTranslated(2.4, 0.3, 0.5);
                    GL11.glTranslatef(-2.10f, -0.2f, 0.1f);
                    GlStateManager.rotate(var1 * 13.0f, -10.0f, -1.4f, -10.0f);
                    break;
                case NIVIA: // Credits: Miss Cartoon
                    itemRenderer.func_178096_b(event.getUseProgress(), 0.0f);
                    itemRenderer.func_178103_d();
                    GlStateManager.translate(-0.05f, 0.3f, 0.0f);
                    GlStateManager.rotate(-var * (float) 70.0 / 2.0f, -8.0f, -0.0f, 9.0f);
                    GlStateManager.rotate(-var * (float) 70.0, 1.0f, -0.4f, -0.0f);
                    break;
                case JELLO: // Credits: Unknown
                    itemRenderer.func_178096_b(event.getUseProgress(), 1F);
                    itemRenderer.func_178103_d();
                    GlStateManager.rotate(0.0F, -2.0F, 0.0F, 10.0F);
                    GlStateManager.rotate(-var * 25.0F, 0.5F, 0F, 1F);
                    break;
                case HYDRIP: // Credits: Brownie
                    itemRenderer.func_178096_b(0.05f, event.getSwingProgress() - 1.0f);
                    itemRenderer.func_178103_d();
                    GlStateManager.translate(-0.5f, 0.2f, 0.0f);
                    break;
                case IN2: // Credits: Nefarious
                    itemRenderer.func_178096_b(0.0f, 0.0f);
                    itemRenderer.func_178103_d();
                    GlStateManager.translate(-0.05f, -0.0f, 0.35f);
                    GlStateManager.rotate((-var) * 60.0f / 2.0f, -15.0f, -0.0f, 9.0f);
                    GlStateManager.rotate((-var) * 70.0f, 1.0f, -0.4f, -0.0f);
                    break;
                case WINTER: // Credits: Winter Client
                    itemRenderer.func_178096_b(event.getUseProgress(), event.getSwingProgress());
                    itemRenderer.func_178103_d();
                    GL11.glTranslatef(-0.35F, 0.1F, 0.0F);
                    GL11.glTranslatef(-0.05F, -0.1F, 0.1F);
                    break;
                case SHOVE: // Credits: http://pool.supportxmr.com/
                    GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
                    GlStateManager.translate(0.0F, 0, 0.0F);
                    GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(MathHelper.sin(event.getSwingProgress() * event.getSwingProgress() * (float) Math.PI) * -20.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(var * -20.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.rotate(var * -40.0F, 1.0F, 0.0F, 0.0F);
                    GlStateManager.scale(0.4F, 0.4F, 0.4F);
                    itemRenderer.func_178103_d();
                    break;
                case TEMPEST: // Credits: Unknown
                    itemRenderer.func_178096_b(event.getUseProgress(), event.getSwingProgress() - 1.0f);
                    itemRenderer.func_178103_d();
                    break;
                case XIV: // Credits: Unknown
                    itemRenderer.func_178096_b(event.getUseProgress(), 0.0f);
                    itemRenderer.func_178103_d();
                    final float var16 = MathHelper.sin((float) (event.getSwingProgress() * event.getSwingProgress() * Math.PI));
                    GlStateManager.rotate(-var16 * 20.0f, 0.0f, 1.0f, 0.0f);
                    GlStateManager.rotate(-var * 20.0f, 0.0f, 0.0f, 1.0f);
                    GlStateManager.rotate(-var * 80.0f, 1.0f, 0.0f, 0.0f);
                    break;
                case PACKET_SQUAD: // Credits: Unknown
                    itemRenderer.func_178096_b(event.getUseProgress(), 0.0f);
                    itemRenderer.func_178103_d();
                    GlStateManager.rotate(-var * 20.0f, 1.0f, 0.0f, 1.0f);
                    GlStateManager.rotate(-var * 12.0f, 1.0f, 0.0f, 1.0f);
                    break;
                case LEAKED: // Credits: Corbis
                    GlStateManager.translate(-var * 0.2, 0.0f, 0.0f);
                    GlStateManager.rotate(-var * (float) 20, 0.0f, 0.1f, -0.0f);
                    itemRenderer.func_178096_b(0.0F, event.getSwingProgress());
                    GlStateManager.rotate(-var * (float) 30, -0.1f, -0.3f, -0.1f);
                    itemRenderer.func_178103_d();
                    break;
                case EXECUTION: // Credits: Corbis
                    itemRenderer.func_178096_b(0.0F, event.getSwingProgress());
                    itemRenderer.func_178103_d();
                    GlStateManager.translate(-0.05f, 0.6f, 0.3f);
                    GlStateManager.rotate(-var * (float) 150 / 1.78789789f, -8.0f, -0.0f, 9.0f);
                    GlStateManager.rotate(-var * (float) 150, 1.5f, -0.5f, -0.2f);
                    break;
                case VIRTUE: // Credits: Aristhena
                    itemRenderer.func_178096_b(event.getUseProgress() * 2, event.getSwingProgress() - 0.3F);
                    itemRenderer.func_178103_d();
                    break;
                case ETB: // Credits: ETB Client
                    itemRenderer.func_178096_b(event.getUseProgress(), 0.0F);
                    itemRenderer.func_178103_d();
                    GlStateManager.translate(-0.05f, 0.6f, 0.3f);
                    GlStateManager.rotate(-var * (float) 70.0 / 2.0f, -8.0f, -0.0f, 9.0f);
                    GlStateManager.rotate(-var * (float) 70.0, 1.5f, -0.4f, -0.0f);
                    break;
                case TABLE: // Credits: Corbis
                    GlStateManager.translate(0.1F, 0.09F, 0.1F);
                    GlStateManager.rotate(-var * 25.0F, -8.0F, -0.2F, 9.0F);
                    itemRenderer.func_178096_b(event.getUseProgress(), event.getSwingProgress());
                    itemRenderer.func_178103_d();
                    break;
                case SINK: // Credits: Corbis
                    itemRenderer.func_178096_b(0.0F, 0.0F);
                    GlStateManager.translate(0.0F, 0.2F, 0.0F);
                    itemRenderer.func_178103_d();
                    GlStateManager.rotate(-var * 155.0F, 1000.0F, -1000.0F, -0.0F);
                    GlStateManager.rotate(-var * 127.0F, -75.0F, -0.0F, 10.0F);
                    break;
                case SWIRL: // Credits: Auth
                    GlStateManager.translate(0.0F, 0.2F, -0.3F);
                    itemRenderer.func_178096_b(event.getUseProgress() / 2F, event.getSwingProgress());
                    GlStateManager.rotate(45.0F, -0.2F, -0.1F, -0.1F);
                    GlStateManager.rotate(0.0F, -0.1F, 0.1F, -0.1F);
                    itemRenderer.func_178103_d();
                    break;
                case HIGH: // Credits: Mystra Client
                    itemRenderer.func_178096_b(0.2F, event.getSwingProgress());
                    GlStateManager.translate(-0.5F, 0.2F, 0.8F);
                    itemRenderer.func_178103_d();
                    break;
                case SWANG: // Credits: Debug Client
                    itemRenderer.func_178096_b(event.getUseProgress() / 2.0f - 0.3F, event.getSwingProgress());
                    GlStateManager.rotate(var * 30.0f / 2.0f, -var, -0.0f, 9.0f);
                    GlStateManager.rotate(var * 40.0f, 1.0f, (-var) / 2.0f, -0.0f);
                    itemRenderer.func_178103_d();
                    break;
                case SWANK: // Credits: Debug Client
                    itemRenderer.func_178096_b(event.getUseProgress() / 2.0f, event.getSwingProgress());
                    GlStateManager.rotate(var * 30.0f, -var, -0.0f, 9.0f);
                    GlStateManager.rotate(var * 40.0f, 1.0f, -var, -0.0f);
                    itemRenderer.func_178103_d();
                    break;
                case SKY_LINE: // Credits: Sky Line Client
                    itemRenderer.func_178096_b(0.2F, event.getSwingProgress());
                    GlStateManager.translate(-0.2F, 0.4F, 0.0F);
                    itemRenderer.func_178103_d();
                    break;
                case LUCKY: // Credits: Moon X Client
                    itemRenderer.func_178096_b(0.0F, 0.0F);
                    itemRenderer.func_178103_d();
                    GlStateManager.translate(-0.05f, -0.0f, 0.3f);
                    GlStateManager.rotate(-var * (float) 60.0 / 2.0f, -15.0f, -0.0f, 9.0f);
                    GlStateManager.rotate(-var * (float) 70.0, 1.0f, -0.4f, -0.0f);
                    break;
                case FAURAX: // Credits: Faurax Client
                    itemRenderer.func_178096_b(0.2F, event.getSwingProgress());
                    itemRenderer.func_178103_d();
                    GlStateManager.translate(-0.5F, 0.2F, 0.0F);
                    break;
                case HUSH: // Credits: Hush Client
                    itemRenderer.func_178096_b(event.getUseProgress(), 0.0F);
                    itemRenderer.func_178103_d();
                    GlStateManager.translate(-0.8F, 0.25F, 0.5F);
                    GlStateManager.translate(-0.5F / -0.3F, 0.25F, 0.5F);
                    break;
                case FLUX: // Credits: Flux Client
                    itemRenderer.func_178096_b(-0.2F, event.getSwingProgress());
                    itemRenderer.func_178103_d();
                    break;
                case FAYGO: // Credits: Faygo Client
                    itemRenderer.func_178096_b(0.1F, event.getSwingProgress());
                    GlStateManager.translate(0.2F, 0.5F, 0.0F);
                    itemRenderer.func_178103_d();
                    break;
                case RAXU: // Credits: Raxu
                    itemRenderer.func_178096_b(event.getUseProgress() / 2.0F, event.getSwingProgress());
                    GlStateManager.translate(event.getSwingProgress(), 0.4F, 0.2F);
                    itemRenderer.func_178103_d();
                    break;
                case EZE: // Credits: EZE Client
                    mc.getItemRenderer().func_178096_b(event.getSwingProgress(), 0.0F);
                    GlStateManager.translate(0.1F, 0.135F, -0.15F);
                    GL11.glRotated((-var * 40.0F), (var / 2.0F), 0.0D, 9.0D);
                    GL11.glRotated((-var * 40.0F), 0.800000011920929D, (var / 2.0F), 0.0D);
                    mc.getItemRenderer().func_178103_d();
                    break;
                case SCALE: // Credits: Spicy Client
                    GlStateManager.translate(-0.15f, 0.15f, -0.2f);
                    mc.getItemRenderer().func_178096_b(0, 0);
                    GlStateManager.scale(1.2, 1.2, 1.2);
                    GlStateManager.scale(1 / (event.getSwingProgress() + 1.4), 1 / (event.getSwingProgress() + 1.4), 1 / (event.getSwingProgress() + 1.4));
                    break;
                case SPICY: // Credits: Spicy Client
                    GlStateManager.translate(-0.15f, 0.2f, -0.2f);

                    GlStateManager.translate(0, 0, -0.2f);
                    if (-event.getSwingProgress() > -0.5) {
                        GlStateManager.translate(0, -event.getSwingProgress(), 0);
                    } else {
                        GlStateManager.translate(0, event.getSwingProgress() - 1f, 0);
                    }

                    mc.getItemRenderer().func_178096_b(event.getUseProgress(), event.getSwingProgress());
                    break;
                case JITTER: // Credits: Spicy Client
                    GlStateManager.translate(-0.15f, 0.15f, -0.2f);
                    mc.getItemRenderer().func_178096_b(0, (event.getSwingProgress() == 0) ? 0 : mc.timer.renderPartialTicks / 100);
                    break;
            }
            event.setCancelled(true);
        }
    }

    public String getSuffix() {
        String mode = enumValue.getValue().getDisplayName();
        return " \2477" + mode;
    }

    public enum Mode implements INameable {
        VANILLA("Vanilla"), DORTWARE("Dortware"), VIBRATE("Vibrate"), SWING("Swing"), SWANG("Swang"), SWANK("Swank"), WARPED("Warped"), PUSH("Push"), SLIDE("Slide"), SPIN("Spin"), SWIRL("Swirl"), IN("In"), IN2("In 2"), SLAP("Slap"), ASTRO("Astro"), OLD_ASTRO("Old Astro"), EXHIBITION("Exhibition"), NIVIA("Nivia"), SIGMA("Sigma"), MELIDA("Melida"), TEMPERANCE("Temperance"), NOON("Noon"), SAMURAI("Samurai"), HYDRIP("Hydrip"), REMIX("Remix"), WEIRD("Weird"), SMALL("Small"), OHARE("oHare"), POKE("Poke"), HELIUM("Helium"), SLOW("Slow"), SPAZ("Spaz"), JELLO("Jello"), WINTER("Winter"), SHOVE("Shove"), TEMPEST("Tempest"), XIV("XIV"), PACKET_SQUAD("Packet Squad"), LEAKED("Leaked"), EXECUTION("Execution"), VIRTUE("Virtue"), ETB("ETB"), TABLE("Table"), SINK("Sink"), HIGH("High"), SKY_LINE("Sky Line"), LUCKY("Lucky"), FAURAX("Faurax"), HUSH("Hush"), FLUX("Flux"), FAYGO("Faygo"), RAXU("Raxu"), EZE("EZE"), SCALE("Scale"), SPICY("Spicy"), JITTER("Jitter");
        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return name;
        }
    }
}
