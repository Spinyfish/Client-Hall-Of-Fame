/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 *  org.lwjgl.opengl.GL11
 */
package wtf.monsoon.impl.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.font.FontUtil;
import wtf.monsoon.api.util.font.impl.FontRenderer;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventRender3D;

public class SuperheroFX
extends Module {
    public final Setting<Double> maximum = new Setting<Double>("Maximum", 20.0).minimum(1.0).maximum(50.0).incrementation(1.0).describedBy("The maximum amount of popups to display per hit");
    public final Setting<Float> length = new Setting<Float>("Length", Float.valueOf(400.0f)).minimum(Float.valueOf(100.0f)).maximum(Float.valueOf(500.0f)).incrementation(Float.valueOf(10.0f)).describedBy("The length of the popup animation");
    public final Setting<Boolean> useInterfaceColor = new Setting<Boolean>("Use Interface Color", false).describedBy("Whether to use the client interface's color instead of the default ones");
    private final FontRenderer superheroFont = new FontRenderer(FontUtil.getFont("superhero.ttf", 40));
    private final List<String> texts = new ArrayList<String>();
    private final ArrayList<Popup> popups = new ArrayList();
    private final Random random = new Random();
    @EventLink
    private final Listener<EventRender3D> eventRender3DListener = event -> {
        this.popups.forEach(Popup::render);
        this.popups.removeIf(popup -> ((Popup)popup).animation.getAnimationFactor() == 0.0);
    };
    @EventLink
    private final Listener<EventPacket> eventAttackEntityListener = event -> {
        if (event.getDirection().equals((Object)EventPacket.Direction.SEND) && event.getPacket() instanceof C02PacketUseEntity) {
            try {
                C02PacketUseEntity packetUseEntity = (C02PacketUseEntity)event.getPacket();
                if (packetUseEntity.getAction().equals((Object)C02PacketUseEntity.Action.ATTACK)) {
                    for (int i = 0; i < this.random.nextInt(this.maximum.getValue().intValue()) + 1; ++i) {
                        float offsetX = this.random.nextFloat() * 2.0f;
                        float offsetY = this.random.nextFloat() * 2.0f;
                        float offsetZ = this.random.nextFloat() * 2.0f;
                        String text = this.texts.get(this.random.nextInt(this.texts.size()));
                        this.popups.add(new Popup(packetUseEntity.getEntityFromWorld(this.mc.theWorld).getPositionVector().addVector(offsetX - 1.0f, packetUseEntity.getEntityFromWorld((World)this.mc.theWorld).height + offsetY - 1.0f, offsetZ - 1.0f), text, this.getColor(this.popups.size())));
                    }
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    };

    public SuperheroFX() {
        super("SuperheroFX", "Renders superhero style messages when you hit people", Category.VISUAL);
        this.texts.addAll(Arrays.asList("POW", "KAPOW", "BOOM", "ZAP", "KABOOM"));
    }

    private Color getColor(int index) {
        if (this.useInterfaceColor.getValue().booleanValue()) {
            Color[] colorArray = ColorUtil.getClientAccentTheme();
            return ColorUtil.fadeBetween(10, index, colorArray[0], colorArray[colorArray.length - 1]);
        }
        ArrayList<Color> colors = new ArrayList<Color>();
        colors.add(Color.BLUE);
        colors.add(Color.ORANGE);
        colors.add(Color.RED);
        colors.add(Color.YELLOW);
        colors.add(Color.RED);
        return (Color)colors.get(this.random.nextInt(this.texts.size()));
    }

    private class Popup {
        private final Vec3 vec;
        private final String text;
        private final Color colour;
        private final Animation animation = new Animation(SuperheroFX.this.length::getValue, false, Easing.CUBIC_IN_OUT);

        public Popup(Vec3 vec, String text, Color colour) {
            this.vec = vec;
            this.text = text;
            this.colour = colour;
            this.animation.setState(true);
        }

        public void render() {
            if (this.animation.getState() && this.animation.getAnimationFactor() == 1.0) {
                this.animation.setState(false);
            }
            float width = SuperheroFX.this.superheroFont.getStringWidth(this.text);
            double[] renderValues = new double[]{SuperheroFX.this.mc.getRenderManager().renderPosX, SuperheroFX.this.mc.getRenderManager().renderPosY, SuperheroFX.this.mc.getRenderManager().renderPosZ};
            GL11.glPushMatrix();
            GL11.glTranslated((double)(this.vec.x - renderValues[0]), (double)(this.vec.y - renderValues[1]), (double)(this.vec.z - renderValues[2]));
            GL11.glRotated((double)(-SuperheroFX.this.mc.getRenderManager().playerViewY), (double)0.0, (double)1.0, (double)0.0);
            GL11.glRotated((double)SuperheroFX.this.mc.getRenderManager().playerViewX, (double)(SuperheroFX.this.mc.gameSettings.thirdPersonView == 2 ? -1 : 1), (double)0.0, (double)0.0);
            GL11.glScaled((double)-0.02, (double)-0.02, (double)-0.02);
            GL11.glDisable((int)2929);
            GL11.glTranslated((double)(-width / 2.0f), (double)0.0, (double)0.0);
            RenderUtil.scaleXY(width / 2.0f, SuperheroFX.this.superheroFont.getHeight() / 2, this.animation, () -> SuperheroFX.this.superheroFont.drawStringWithShadow(this.text, 0.0f, 0.0f, this.colour));
            GL11.glEnable((int)2929);
            GL11.glEnable((int)3008);
            GL11.glPopMatrix();
        }
    }
}

