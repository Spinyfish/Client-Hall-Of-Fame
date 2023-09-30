package club.pulsive.impl.module.impl.visual;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.api.font.Fonts;
import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.event.client.ShaderEvent;
import club.pulsive.impl.event.player.AttackEvent;
import club.pulsive.impl.event.render.Render2DEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.module.impl.combat.Aura;
import club.pulsive.impl.module.impl.exploit.TPAura;
import club.pulsive.impl.module.impl.misc.ClientSettings;
import club.pulsive.impl.property.implementations.EnumProperty;
import club.pulsive.impl.util.math.MathUtil;
import club.pulsive.impl.util.render.*;
import club.pulsive.impl.util.render.animations.Animation;
import club.pulsive.impl.util.render.animations.Direction;
import club.pulsive.impl.util.render.secondary.ShaderRound;
import club.pulsive.impl.util.render.animations.SmoothStep;
import club.pulsive.impl.util.render.secondary.ShaderUtil;
import club.pulsive.impl.util.render.shaders.Blur;
import lombok.AllArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "TargetHUD", description = "Displays the target of the player", category = Category.VISUALS)
public class TargetHUD extends Module {

    public Draggable draggable = Pulsive.INSTANCE.getDraggablesManager().createNewDraggable(this, "targethud", 40, 50);
    private EnumProperty<MODE> modeEnumProperty = new EnumProperty<MODE>("Mode", MODE.YEHAH);
    public static Animation ease = new SmoothStep(255, 1);
    private boolean aniamte, shouldRender, doneAnimationReverse;
    private float health, dragX, dragY, x, y;
    private EntityLivingBase entityLivingBase, prevEntity;
    private Aura aura;

    @Override
    public void onEnable() {
        super.onEnable();
        aniamte = true;
        health = 0;
        prevEntity = null;
        entityLivingBase = null;
        if(aura == null)
            aura = Pulsive.INSTANCE.getModuleManager().getModule(Aura.class);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        aniamte = true;
    }

    @EventHandler
    private final Listener<ShaderEvent> shaderEventListener = event -> {
        //entityLivingBase = isAura ? aura.getTarget() : mc.thePlayer;return;
        //shouldRender = (isAura ? aura.isToggled() : mc.currentScreen instanceof GuiChat) && entityLivingBase != null && entityLivingBase instanceof EntityPlayer;
        if(!ease.finished(Direction.BACKWARDS) && prevEntity != null){
            // Logger.print("a");

            RenderUtil.scaleStart(draggable.getX() + draggable.getWidth() / 2, draggable.getY() + draggable.getHeight() / 2, (float) ease.getOutput());
            RoundedUtil.drawRoundedRect(draggable.getX(), draggable.getY(), draggable.getX() + draggable.getWidth(), draggable.getY() + draggable.getHeight(), 8, RenderUtil.applyOpacity(new Color(HUD.getColor()), (float) ease.getOutput()).getRGB());
            RenderUtil.scaleEnd();
        }
    };

    @EventHandler
    private final Listener<AttackEvent> attackEventListener = event -> {
        entityLivingBase = (EntityLivingBase) event.getTarget();
    };

    @EventHandler
    private final Listener<Render2DEvent> render2DEventListener = event -> {
        boolean isAura = aura.isToggled() && aura.getTarget() != null;
        if(isAura){
            entityLivingBase = aura.getTarget();
        }
        if(mc.currentScreen instanceof GuiChat){
            entityLivingBase = mc.thePlayer;
            prevEntity = mc.thePlayer;
            if(!aniamte){
                aniamte = true;
                ease.setDirection(Direction.FORWARDS);
            }
        }else{
            if(aniamte){
                aniamte = false;
                ease.setDirection(Direction.BACKWARDS);
            }
        }

        if(entityLivingBase != mc.thePlayer && entityLivingBase != null && prevEntity != entityLivingBase){
            prevEntity = entityLivingBase;
        }

        EntityLivingBase entity = prevEntity;
        final double diffX = entity.posX - entity.lastTickPosX;
        final double diffY = entity.posY - entity.lastTickPosY;
        final double diffZ = entity.posZ - entity.lastTickPosZ;
        final float partialTicks = event.getPartialTicks();
        final AxisAlignedBB interpolatedBB = new AxisAlignedBB(
                entity.lastTickPosX - entity.width / 2 + diffX * partialTicks,
                entity.lastTickPosY + diffY * partialTicks,
                entity.lastTickPosZ - entity.width / 2 + diffZ * partialTicks,
                entity.lastTickPosX + entity.width / 2 + diffX * partialTicks,
                entity.lastTickPosY + entity.height + diffY * partialTicks,
                entity.lastTickPosZ + entity.width / 2 + diffZ * partialTicks);
        final double[][] vectors = new double[8][2];
        final float[] coords = new float[4];
        convertTo2D(interpolatedBB, vectors, coords);
        float minX = coords[0], minY = coords[1], maxX = coords[2], maxY = coords[3];

        float thingX = maxX - 80, thingY = maxY + 10;

        if(modeEnumProperty.getValue() != MODE.YEHAH && mc.gameSettings.thirdPersonView == 0 && prevEntity == mc.thePlayer) {
            draggable.setX(event.getScaledResolution().getScaledWidth() / 2);
            draggable.setY(event.getScaledResolution().getScaledHeight() - 200);
        }

        if((mc.gameSettings.thirdPersonView == 0 && prevEntity != mc.thePlayer) && modeEnumProperty.getValue() == MODE.BALLS) {
            draggable.setX(thingX);
            draggable.setY(thingY);
        }
        draggable.setWidth(66 + Fonts.fontforflashy.getStringWidth(prevEntity.getName()));
        draggable.setHeight(40);
        //entityLivingBase = isAura ? aura.getTarget() : mc.thePlayer;
        // shouldRender = entityLivingBase != null && entityLivingBase instanceof EntityPlayer;
        x = draggable.getWidth() / 2;
        y = draggable.getHeight() / 2;
        dragX = draggable.getX() / 2;
        dragY = draggable.getY() / 2;
        //aniamte = mc.thePlayer.getDistanceToEntity(entityLivingBase) <= 100;
//        x =
//        if(x <= draggable.getWidth()){
//            x = draggable.getWidth();
//        }
//
//        if (y <= draggable.getHeight()) {
//            y = draggable.getHeight();
//        }


        if(prevEntity != mc.thePlayer) {
            if ((prevEntity.getDistanceToEntity(mc.thePlayer) >= (Pulsive.INSTANCE.getModuleManager().getModule(TPAura.class).isToggled() ? 120 : 10)) || !mc.theWorld.loadedEntityList.contains(prevEntity) || prevEntity.isDead || prevEntity.isInvisible()) {
                if (doneAnimationReverse) {
                    doneAnimationReverse = false;
                    ease.setDirection(Direction.BACKWARDS);
                    prevEntity = null;
                }
            } else if (prevEntity instanceof EntityPlayer && (mc.thePlayer.getDistanceToEntity(prevEntity) <= 100 && !prevEntity.isDead)) {
                if (!doneAnimationReverse) {
                    doneAnimationReverse = true;
                    ease.setDirection(Direction.FORWARDS);
                }
            }
        }


        if(ease.finished(Direction.BACKWARDS)) {
            shouldRender = false;
            // Logger.print("a");
            doneAnimationReverse = false;
        }



        if(!ease.finished(Direction.BACKWARDS) && prevEntity != null) {
            glPushMatrix();
            // glTranslated(minX - draggable.getWidth() - 20, maxY / 2, 0);




            RenderUtil.scaleStart((draggable.getX() + draggable.getWidth() / 2), (draggable.getY() + draggable.getHeight() / 2), (float) ease.getOutput());

            RoundedUtil.drawGradientRoundedRect(draggable.getX(), draggable.getY(), draggable.getX() + draggable.getWidth(), draggable.getY() + draggable.getHeight(), 8, RenderUtil.applyOpacity(new Color(HUD.getColor()), (float) 0.1).getRGB(), RenderUtil.applyOpacity(new Color(HUD.getColor()).darker(), (float) 0.1f).getRGB());

            if (Shaders.blur.getValue()) {
                StencilUtil.initStencilToWrite();
                RoundedUtil.drawRoundedRect(draggable.getX(), draggable.getY(), draggable.getX() + draggable.getWidth(), draggable.getY() + draggable.getHeight(), 8, RenderUtil.applyOpacity(new Color(HUD.getColor()), (float) ease.getOutput()).getRGB());
                StencilUtil.readStencilBuffer(1);
                Blur.renderBlur(Shaders.blurRadius.getValue().floatValue());
                StencilUtil.uninitStencilBuffer();
            }
            float ez = 2;
            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

            int posX = (int) draggable.getX() * 2, posY = (int) draggable.getY() * 2,
                    width = (int) draggable.getWidth() * 2, height = (int) draggable.getHeight() * 2;
            RoundedUtil.drawRoundedRect(draggable.getX(),draggable.getY(), draggable.getX() + draggable.getWidth(), draggable.getY() + draggable.getHeight(), 8,  new Color(0,0,0, 100).getRGB());
            Fonts.icons15.drawString("P", draggable.getX() + 28 + 7 +  Fonts.googleMedium.getStringWidth(prevEntity.getName()), draggable.getY() + 11 -2, new Color(230,230,230, 200).getRGB());


            //background
            if(ClientSettings.uiOutlines.getValue()) {
                RoundedUtil.drawRoundedOutline(draggable.getX(), draggable.getY(), draggable.getX() + draggable.getWidth(), draggable.getY() + draggable.getHeight(), 8, 3, RenderUtil.applyOpacity(new Color(HUD.getColor()), (float) ease.getOutput()).getRGB());
            }

            //healthbar
            health = RenderUtil.animate((draggable.getWidth() - 8) * (prevEntity.getHealth() / prevEntity.getMaxHealth()), health, 0.05f);
            ShaderRound.drawRound(draggable.getX() + 4, draggable.getY() + draggable.getHeight() - 10, health, 4, 0, RenderUtil.applyOpacity(new Color(HUD.getColor()), (float) ease.getOutput()));

            //head
            NetworkPlayerInfo info = mc.getNetHandler().getPlayerInfo(prevEntity.getUniqueID());
            if (info != null && info.getLocationSkin() != null) {
                StencilUtil.initStencilToWrite();
                RoundedUtil.drawRoundedRect(draggable.getX() + 4, draggable.getY() + 4, draggable.getX() + 27, draggable.getY() + 27, 6, RenderUtil.applyOpacity(Color.WHITE, (float) ease.getOutput()).getRGB());
                StencilUtil.readStencilBuffer(1);
                renderPlayerModelTexture((draggable.getX() + 4), (draggable.getY() + 4), 23F, 23F, (AbstractClientPlayer) prevEntity);
                StencilUtil.uninitStencilBuffer();
            }

            //name and shit
            Fonts.googleMedium.drawString(prevEntity.getName(), draggable.getX() + 28 + 3, draggable.getY() + 11 -2, RenderUtil.applyOpacity(new Color(-1), (float) ease.getOutput()).getRGB());
            Fonts.google.drawString("Health: " + MathUtil.round(prevEntity.getHealth(), 2, 0.1f), draggable.getX() + 28 + 3, draggable.getY() + Fonts.fontforflashytitle.getStringHeight(prevEntity.getName()) + 11, RenderUtil.applyOpacity(new Color(-1), (float) ease.getOutput()).getRGB());

            RenderUtil.scaleEnd();
            glPopMatrix();
        }

        if(!shouldRender){
            // aniamte = false;
            //doneAnimationReverse = false;
            //health = dragX = dragY = x = y = 0;
            //ease.reset();
        }
    };

    private void convertTo2D(AxisAlignedBB interpolatedBB, double[][] vectors, float[] coords) {
        if (coords == null || vectors == null || interpolatedBB == null) return;
        double x = mc.getRenderManager().viewerPosX;
        double y = mc.getRenderManager().viewerPosY;
        double z = mc.getRenderManager().viewerPosZ;

        vectors[0] = RenderUtil.project2D(interpolatedBB.minX - x, interpolatedBB.minY - y,
                interpolatedBB.minZ - z);
        vectors[1] = RenderUtil.project2D(interpolatedBB.minX - x, interpolatedBB.minY - y,
                interpolatedBB.maxZ - z);
        vectors[2] = RenderUtil.project2D(interpolatedBB.minX - x, interpolatedBB.maxY - y,
                interpolatedBB.minZ - z);
        vectors[3] = RenderUtil.project2D(interpolatedBB.maxX - x, interpolatedBB.minY - y,
                interpolatedBB.minZ - z);
        vectors[4] = RenderUtil.project2D(interpolatedBB.maxX - x, interpolatedBB.maxY - y,
                interpolatedBB.minZ - z);
        vectors[5] = RenderUtil.project2D(interpolatedBB.maxX - x, interpolatedBB.minY - y,
                interpolatedBB.maxZ - z);
        vectors[6] = RenderUtil.project2D(interpolatedBB.minX - x, interpolatedBB.maxY - y,
                interpolatedBB.maxZ - z);
        vectors[7] = RenderUtil.project2D(interpolatedBB.maxX - x, interpolatedBB.maxY - y,
                interpolatedBB.maxZ - z);

        float minW = (float) Arrays.stream(vectors).min(Comparator.comparingDouble(pos -> pos[2])).orElse(new double[]{0.5})[2];
        float maxW = (float) Arrays.stream(vectors).max(Comparator.comparingDouble(pos -> pos[2])).orElse(new double[]{0.5})[2];
        if (maxW > 1 || minW < 0) return;
        float minX = (float) Arrays.stream(vectors).min(Comparator.comparingDouble(pos -> pos[0])).orElse(new double[]{0})[0];
        float maxX = (float) Arrays.stream(vectors).max(Comparator.comparingDouble(pos -> pos[0])).orElse(new double[]{0})[0];
        final float top = (mc.displayHeight / (float) new ScaledResolution(mc).getScaleFactor());
        float minY = (float) (top - Arrays.stream(vectors).min(Comparator.comparingDouble(pos -> top - pos[1])).orElse(new double[]{0})[1]);
        float maxY = (float) (top - Arrays.stream(vectors).max(Comparator.comparingDouble(pos -> top - pos[1])).orElse(new double[]{0})[1]);
        coords[0] = minX;
        coords[1] = minY;
        coords[2] = maxX;
        coords[3] = maxY;
    }

    @AllArgsConstructor
    private enum MODE{
        YEHAH("Yehah"),
        MY("My"),
        BALLS("Balls"),
        ITCH("Itch"),
        PLS("Pls"),
        HELP("Help"),
        ME("Me");

        private final String modeName;

        @Override
        public String toString() { return modeName; }
    }

    public void renderPlayerModelTexture(final double x, final double y, final float width, final float height, final AbstractClientPlayer target) {
        final ResourceLocation skin = target.getLocationSkin();
        Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
        glPushMatrix();
        glEnable(GL_BLEND);
        Gui.drawScaledCustomSizeModalRect(x, y, 8, 8, 8, 8, width, height, 64, 64);
        glDisable(GL_BLEND);
        glPopMatrix();
    }


}