package me.jinthium.straight.impl.modules.visual;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.dragging.Dragging;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.api.setting.ParentAttribute;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.components.ProjectionComponent;
import me.jinthium.straight.impl.event.render.Render2DEvent;
import me.jinthium.straight.impl.event.render.ShaderEvent;
import me.jinthium.straight.impl.modules.combat.KillAura;
import me.jinthium.straight.impl.modules.player.Scaffold;
import me.jinthium.straight.impl.settings.BooleanSetting;
import me.jinthium.straight.impl.settings.ModeSetting;
import me.jinthium.straight.impl.utils.animation.Animation;
import me.jinthium.straight.impl.utils.animation.Direction;
import me.jinthium.straight.impl.utils.animation.impl.EaseBackIn;
import me.jinthium.straight.impl.utils.render.ColorUtil;
import me.jinthium.straight.impl.utils.render.GLUtil;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import me.jinthium.straight.impl.utils.render.RoundedUtil;
import me.jinthium.straight.impl.utils.vector.Vector2f;
import me.jinthium.straight.impl.utils.vector.Vector4d;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DecimalFormat;

public class TargetHud extends Module {

    private final Dragging targetHudDrag = Client.INSTANCE.createDrag(this, "targethud", 400, 200);
    private final ModeSetting modeSetting = new ModeSetting("Mode", "Straightware", "Exhibition", "Straightware");
    private final BooleanSetting followTarget = new BooleanSetting("Follow Target", false);
    private final ModeSetting followMode = new ModeSetting("Follow Mode", "Right", "Top", "Left", "Right", "Bottom");
    private final Animation globalAnimation = new EaseBackIn(300, 1, 2f);
    private final DecimalFormat DF_1 = new DecimalFormat("0.0");
    private float health, barAnim, origX, origY;
    private EntityLivingBase target;
    private Vector2f position = new Vector2f(0, 0);

    public TargetHud(){
        super("TargetHud", Category.VISUALS);
        followMode.addParent(followTarget, ParentAttribute.BOOLEAN_CONDITION);
        this.addSettings(modeSetting, followTarget, followMode);
    }


    @Callback
    final EventCallback<Render2DEvent> render2DEventEventCallback = event -> {
        if(Client.INSTANCE.getModuleManager().getModule(Scaffold.class).isEnabled())
            return;

        if(target == null && globalAnimation.finished(Direction.BACKWARDS)){
            health = 0;
            barAnim = 0;
        }


        KillAura killAura = Client.INSTANCE.getModuleManager().getModule(KillAura.class);

        if (globalAnimation.finished(Direction.BACKWARDS))
            target = null;

        if (killAura.target != null) {
            target = killAura.getTarget();
            globalAnimation.setDirection(Direction.FORWARDS);
        }else{
            if(target != mc.thePlayer)
                globalAnimation.setDirection(Direction.BACKWARDS);
        }

        if(mc.currentScreen instanceof GuiChat && target == null){
            target = mc.thePlayer;
            globalAnimation.setDirection(Direction.FORWARDS);
        }

        if(target == mc.thePlayer){
            if(origX == 0 || origY == 0){
                origX = targetHudDrag.getX();
                origY = targetHudDrag.getY();
            }
            if(!(mc.currentScreen instanceof GuiChat))
                globalAnimation.setDirection(Direction.BACKWARDS);
        }

        Hud hud = Client.INSTANCE.getModuleManager().getModule(Hud.class);

        if(target == null)
            return;

        if(followTarget.isEnabled()){
            if(target == mc.thePlayer) {
                this.position = new Vector2f(targetHudDrag.getX(), targetHudDrag.getY());
            }else{
                Vector4d position = ProjectionComponent.get(target);

                if (position == null) return;

                switch (followMode.getMode()) {
                    case "Right" -> {
                        this.position.x = (float) position.z;
                        this.position.y = (float) (position.w - (position.w - position.y) / 2 - targetHudDrag.getHeight() / 2f);
                    }
                    case "Top" -> {
                        this.position.x = (float) (position.x + position.z) / 2 - targetHudDrag.getWidth() / 2f;
                        this.position.y = (float) position.y - targetHudDrag.getHeight() / 2f;
                    }
                    case "Left" -> {
                        this.position.x = (float) position.x - targetHudDrag.getWidth();
                        this.position.y = (float) (position.w - (position.w - position.y) / 2 - targetHudDrag.getHeight() / 2f);
                    }
                    case "Bottom" -> {
                        this.position.x = (float) (position.x + position.z) / 2 - targetHudDrag.getWidth() / 2f;
                        this.position.y = (float) position.w;
                    }
                }
            }
        }else{
            this.position = new Vector2f(targetHudDrag.getX(), targetHudDrag.getY());
        }

        switch(modeSetting.getMode()){
            case "Straightware" -> {
                targetHudDrag.setWidth(130);
                targetHudDrag.setHeight(50);

                RenderUtil.scaleStart(position.getX() + targetHudDrag.getWidth() / 2, position.getY() + targetHudDrag.getHeight() / 2, globalAnimation.getOutput().floatValue());
                RoundedUtil.drawRound(position.getX(), position.getY(), targetHudDrag.getWidth(), targetHudDrag.getHeight(), 3, new Color(0, 0, 0, 190));

                //render player health
                health = RenderUtil.animate((targetHudDrag.getWidth() - 25) * (target.getHealth() / target.getMaxHealth()), health, 0.015f);
                RoundedUtil.drawRound(position.getX() + 2, position.getY() + targetHudDrag.getHeight() - 8,  health, 5, 2, hud.getHudColor((float) System.currentTimeMillis() / 600));
                normalFont19.drawStringWithShadow(DF_1.format(target.getHealth()), position.getX() + health + 3,
                        position.getY() + targetHudDrag.getHeight() - 8, -1);

                //render player head & name
                renderPlayer2D(position.getX() + 2, position.getY() + 2, (AbstractClientPlayer) target);
                normalFont20.drawStringWithShadow(target.getName(), position.getX() + 39, position.getY() + 5, -1);
                normalFont20.drawStringWithShadow(String.format("Distance: %s", DF_1.format(target.getDistanceToEntity(mc.thePlayer))),
                        position.getX() + 39, position.getY() + normalFont20.getHeight() + 6, -1);


                float seperation = 15;
                float size = 30;
                RenderHelper.enableGUIStandardItemLighting();
                for (int i = 0; i <= 3; i++) {
                    if (target.getCurrentArmor(i) == null) continue;
                    RenderUtil.resetColor();
                    GLUtil.startBlend();
                    RenderUtil.color(-1);
                    mc.getRenderItem().renderItemAndEffectIntoGUI(target.getCurrentArmor(i),
                            (int) (position.getX() + size + 7 + (seperation * (3 - i))), (int) (position.getY() + 22));
                    GLUtil.endBlend();
                }

                if (target.getHeldItem() != null) {
                    GLUtil.startBlend();
                    RenderUtil.resetColor();
                    RenderUtil.color(-1);
                    mc.getRenderItem().renderItemAndEffectIntoGUI(target.getHeldItem(),
                            (int) (position.getX() + size + 7 + (seperation * 4)), (int) (position.getY() + 22));
                    GLUtil.endBlend();
                }
                RenderHelper.disableStandardItemLighting();
                RenderUtil.scaleEnd();
            }
            case "Exhibition" -> {
                targetHudDrag.setWidth(130);
                targetHudDrag.setHeight(50);

                RenderUtil.scaleStart(position.getX() + targetHudDrag.getWidth() / 2, position.getY() + targetHudDrag.getHeight() / 2, globalAnimation.getOutput().floatValue());
                RoundedUtil.drawRoundOutline(position.getX(), position.getY(),
                        targetHudDrag.getWidth(), targetHudDrag.getHeight(), 2, 1f, new Color(0xFF080809), new Color(0xFF3C3C3D));
                RoundedUtil.drawRoundOutline(position.getX() + 3, position.getY() + 3, 32, 44, 2,
                        0.5f, new Color(0xFF151515), new Color(0xFF3C3C3D));

//                RenderHelper.enableGUIStandardItemLighting();
                GL11.glPushMatrix();
                RendererLivingEntity.NAME_TAG_RANGE = 0;
                RendererLivingEntity.NAME_TAG_RANGE_SNEAK = 0;
                GuiInventory.drawEntityOnScreen((int) (position.getX() + 18), (int) (position.getY() + 44), 20, target.rotationYaw, -target.rotationPitch, target);
                RendererLivingEntity.NAME_TAG_RANGE = 64f;
                RendererLivingEntity.NAME_TAG_RANGE_SNEAK = 32f;
                GL11.glPopMatrix();
//                RenderHelper.disableStandardItemLighting();

                health = RenderUtil.animate((targetHudDrag.getWidth() - 41) * (target.getHealth() / target.getMaxHealth()), health, 0.025f);
                barAnim = RenderUtil.animate(targetHudDrag.getWidth() - 41, barAnim, 0.025f);
                normalFont20.drawStringWithShadow(target.getName(), position.getX() + 37, position.getY() + 6, -1);

                RoundedUtil.drawRound(position.getX() + 38, position.getY() + 8 + normalFont20.getHeight(),
                        barAnim, 5, 0, new Color(ColorUtil.getHealthColor(target)).darker().darker());

                RoundedUtil.drawRound(position.getX() + 38, position.getY() + 8 + normalFont20.getHeight(), health,
                        5, 0, new Color(ColorUtil.getHealthColor(target)));

                RenderUtil.scissorStart(position.getX() + 38, position.getY() + 7 + normalFont20.getHeight(), barAnim, 9);

                float amount = barAnim / 10;
                float length = barAnim / amount;
                for(int i = 1; i < amount; i++){
                    RoundedUtil.drawRound(position.getX() + 38 + i * length - 0.5f, position.getY() + 6.9f + normalFont20.getHeight(), 0.5f, 6.9f, 0, Color.black);
                }
                RenderUtil.scissorEnd();

                normalFont17.drawStringWithShadow(String.format("HP: %s | DIST: %s", DF_1.format(target.getHealth()), DF_1.format(mc.thePlayer.getDistanceToEntity(target))),
                        position.getX() + 38, position.getY() + 27, -1);

                boolean winning = target.getHealth() < mc.thePlayer.getHealth();
                normalFont17.drawStringWithShadow(mc.thePlayer.getHealth() == target.getHealth() ? "Indecisive" : winning ? "Winning" : "Losing", position.getX() + 38, position.getY() + 29 + normalFont17.getHeight(), -1);
                RenderUtil.scaleEnd();
            }
        }
    };

    @Callback
    final EventCallback<ShaderEvent> shaderEventEventCallback = event -> {
        if(target == null || !modeSetting.is("Straightware") || position == null)
            return;
        
        Hud hud = Client.INSTANCE.getModuleManager().getModule(Hud.class);
        RenderUtil.scaleStart(position.getX() + targetHudDrag.getWidth() / 2, position.getY() + targetHudDrag.getHeight() / 2, globalAnimation.getOutput().floatValue());
        RoundedUtil.drawRound(position.getX(), position.getY(), targetHudDrag.getWidth(), targetHudDrag.getHeight(), 3, hud.getModeSetting("Shader Mode").is("Bloom") ? Color.black : hud.getHudColor((float) System.currentTimeMillis() / 600));

        //render player health
        health = RenderUtil.animate((targetHudDrag.getWidth() - 25) * (target.getHealth() / target.getMaxHealth()), health, 0.015f);
        Gui.drawRect2(position.getX() + 2, position.getY() + targetHudDrag.getHeight() - 8,  health, 5, hud.getHudColor((float) System.currentTimeMillis() / 600).getRGB());
        RenderUtil.scaleEnd();
    };

    protected void renderPlayer2D(float x, float y, AbstractClientPlayer player) {
        GLUtil.startBlend();
        mc.getTextureManager().bindTexture(player.getLocationSkin());
        Gui.drawScaledCustomSizeModalRect(x, y, (float) 8.0, (float) 8.0, 8, 8, (float) 36, (float) 36, 64.0F, 64.0F);
        GLUtil.endBlend();
    }


}
