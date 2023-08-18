package me.jinthium.straight.impl.modules.visual;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.dragging.Dragging;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.api.setting.ParentAttribute;
import me.jinthium.straight.api.util.Util;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.render.Render2DEvent;
import me.jinthium.straight.impl.event.render.Render3DEvent;
import me.jinthium.straight.impl.event.render.RenderModelEvent;
import me.jinthium.straight.impl.event.render.ShaderEvent;
import me.jinthium.straight.impl.settings.*;
import me.jinthium.straight.impl.utils.animation.Animation;
import me.jinthium.straight.impl.utils.animation.Direction;
import me.jinthium.straight.impl.utils.animation.impl.EaseInOutQuad;
import me.jinthium.straight.impl.utils.font.CustomFont;
import me.jinthium.straight.impl.utils.math.MathUtils;
import me.jinthium.straight.impl.utils.network.BalanceUtil;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import me.jinthium.straight.impl.utils.render.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import org.lwjgl.opengl.GL11;
import org.lwjglx.input.Keyboard;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;
import java.awt.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Hud extends Module implements Util {

    private final CopyOnWriteArrayList<Module> enabledModules = new CopyOnWriteArrayList<>();
    private Collection<Module> moduleCollection;
    public final ColorSetting hudColor = new ColorSetting("Hud Color", Color.white);
    public final ColorSetting fistColor = new ColorSetting("First Color", Color.white);
    public final ColorSetting secondColor = new ColorSetting("Second Color", Color.white);
    private final StringSetting hudName = new StringSetting("Hud Name", "Straightware");
    private final ModeSetting notificationMode = new ModeSetting("Notification Mode", "Minecraft", "Minecraft", "System");
    private final ModeSetting shaderMode = new ModeSetting("Shader Mode", "Bloom", "Glow", "Bloom");
    private final ModeSetting colorMode = new ModeSetting("Color Mode", "Client", "Blend", "Client", "Custom");
    private final BooleanSetting outline = new BooleanSetting("Outline", true);
    private final BooleanSetting background = new BooleanSetting("Background", true);
    private final ModeSetting backgroundMode = new ModeSetting("Background Mode", "Black", "Black", "Colored");
    private final ModeSetting backgroundColorMode = new ModeSetting("BG Color Mode", "Client", "Client", "Custom");
    private final ColorSetting backgroundColor = new ColorSetting("BG Color", Color.black);
    private final NumberSetting backgroundOppacity = new NumberSetting("Background Oppacity", 190, 0, 255, 1);
    
    private final Dragging keyBindGuiDrag = Client.INSTANCE.createDrag(this, "keybind-gui", 5, 100),
                        fpsCounter = Client.INSTANCE.createDrag(this, "fps-counter", 5, 120),
                        bpsCounter = Client.INSTANCE.createDrag(this, "bps-counter", 5, 140),
                        balanceCounter = Client.INSTANCE.createDrag(this, "packet-balance-counter", 5, 160);

    private float animated, animated2;

    public final MultiBoolSetting components = new MultiBoolSetting("Components",
            new BooleanSetting("Keybind Gui", true),
            new BooleanSetting("Hotbar", true),
            new BooleanSetting("FPS Counter", false),
            new BooleanSetting("BPS Counter", true),
            new BooleanSetting("Packet Balance", false)
    );

    private final BooleanSetting pripriMode = new BooleanSetting("Pri Pri", false);
    public final Map<Entity, Vector4f> entityPosition = new HashMap<>();
    private final Animation animation = new EaseInOutQuad(500, 1);

//    private final Animation arrayAnim = new DecelerateAnimation(250, 1);

    public Hud(){
        super("Hud", 0, Category.VISUALS);
        hudColor.addParent(colorMode, r -> colorMode.is("Custom"));
        fistColor.addParent(colorMode, r -> colorMode.is("Blend"));
        secondColor.addParent(colorMode, r -> colorMode.is("Blend"));
        pripriMode.addParent(colorMode, r -> (Client.INSTANCE.getUser().getUsername().equalsIgnoreCase("priorities")
                || Client.INSTANCE.getUser().getUsername().equalsIgnoreCase("Jinthium")));
        shaderMode.addParent(colorMode, r -> Client.INSTANCE.getModuleManager().getModule(PostProcessing.class).isEnabled());
        backgroundOppacity.addParent(background, ParentAttribute.BOOLEAN_CONDITION);
        backgroundColorMode.addParent(background, r -> (background.isEnabled() && backgroundMode.is("Colored")));
        backgroundColor.addParent(backgroundColorMode, r -> (!backgroundColorMode.cannotBeShown() && backgroundColorMode.is("Custom")));
        addSettings(notificationMode, colorMode, shaderMode, hudName, outline, background, backgroundMode, backgroundColorMode, backgroundOppacity, backgroundColor, hudColor, fistColor, secondColor, components, pripriMode);
    }

    public Color getHudColor(float y){
        switch(colorMode.getMode()){
            case "Custom" -> {
                return hudColor.getColor();
            }
            case "Client" -> {
                return Client.INSTANCE.getClientColor();
            }
            case "Blend" -> {
                return ColorUtil.blend(secondColor.getColor(), fistColor.getColor(), new Vector2d(0, y));
            }
        }
        return null;
    }


    @Callback
    final EventCallback<Render3DEvent> render3DEventEventCallback = event -> {
        if(pripriMode.isEnabled()){
            entityPosition.clear();
            for (final Entity entity : mc.theWorld.loadedEntityList) {
                if(entity == mc.thePlayer || !(entity instanceof EntityPlayer) || entity.isInvisible() || entity.isDead)
                    continue;

                if (RenderUtil.isInView(entity)) {
                    entityPosition.put(entity, RenderUtil.getEntityPositionsOn2D(entity));
                }
            }
        }
    };

    @Callback
    final EventCallback<RenderModelEvent> renderModelEventEventCallback = event -> {
        if(!pripriMode.isEnabled())
            return;
        for(Entity entity : entityPosition.keySet()){
            if(entity == event.getEntity()){
                event.cancel();
            }
        }
    };

    @Callback
    final EventCallback<Render2DEvent> render2DEventEventCallback = event -> {
        if(!(Client.INSTANCE.getUser().getUsername().equalsIgnoreCase("priorities") || Client.INSTANCE.getUser().getUsername().equalsIgnoreCase("Jinthium")))
            pripriMode.setState(false);

        if(moduleCollection == null){
            moduleCollection = Client.INSTANCE.getModuleManager().getModuleMap().values();
            return;
        }

        Client.INSTANCE.getNotificationManager().render(false);

        Gui.drawRect(0, 0, 0, 0, 0);
        CustomFont fontRenderer = normalFont20;
        ScaledResolution scaledResolution = ScaledResolution.fetchResolution(mc);

        boolean change = animation.getDirection() == Direction.FORWARDS;

        if(animation.isDone()){
//            animation.set
            animation.changeDirection();
        }

//        animated2 = RenderUtil.animate((float) MathUtils.calculatePercentage(mc.thePlayer.getHealth(), mc.thePlayer.getMaxHealth()), animated2, 0.05f);
//        RenderUtil.drawCircle(100f, 100f, 30f, animated2, -1, Color.white, .05f);

//        fontRenderer.drawStringWithShadow("BPS: " + MovementUtil.getBPS(), 2, 10, -1);

        components.getBoolSettings().forEach(booleanSetting -> {
            if(booleanSetting.isEnabled()){
                switch (booleanSetting.getName()) {
                    case "Keybind Gui" -> {
                        RoundedUtil.drawRound(keyBindGuiDrag.getX(), keyBindGuiDrag.getY(), keyBindGuiDrag.getWidth(), keyBindGuiDrag.getHeight(), 4, new Color(0, 0, 0, 140));
                        RoundedUtil.drawRound(keyBindGuiDrag.getX(), keyBindGuiDrag.getY(), keyBindGuiDrag.getWidth(), 12, 4, new Color(0, 0, 0, 150));
                        fontRenderer.drawStringWithShadow("Keybinds (Toggled)", keyBindGuiDrag.getX() + 1, keyBindGuiDrag.getY() + (float) fontRenderer.getHeight() / 2, -1);

                        float y = keyBindGuiDrag.getY() + 15;
                        for (Module module : Client.INSTANCE.getModuleManager().getModules()) {
                            if(module.getName().equalsIgnoreCase("ClickGui"))
                                continue;

                            module.getAnimation().setDirection(module.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);

                            if (module.getKey() != Keyboard.KEY_NONE && !module.getAnimation().finished(Direction.BACKWARDS)) {
                                RenderUtil.scaleStart(keyBindGuiDrag.getX() + fontRenderer.getStringWidth(module.getName()) / 2, (y + 1), module.getAnimation().getOutput().floatValue());
                                fontRenderer.drawStringWithShadow(module.getName(), keyBindGuiDrag.getX() + 1, (y + 1), -1);
                                RenderUtil.scaleEnd();
                                RenderUtil.scaleStart(keyBindGuiDrag.getX() + keyBindGuiDrag.getWidth() -
                                        fontRenderer.getStringWidth(Keyboard.getKeyName(module.getKey())) - 1, y + 1, module.getAnimation().getOutput().floatValue());
                                fontRenderer.drawStringWithShadow(Keyboard.getKeyName(module.getKey()), keyBindGuiDrag.getX() + keyBindGuiDrag.getWidth() -
                                        fontRenderer.getStringWidth(Keyboard.getKeyName(module.getKey())) - 1, (y + 1), -1);
                                RenderUtil.scaleEnd();
                                y += fontRenderer.getHeight() + 2;
                                keyBindGuiDrag.setHeight(animated);
                            }
                        }
                        animated = RenderUtil.animate(y - keyBindGuiDrag.getY(), animated, 0.15f);
                    }
                    case "Hotbar" -> {

                        RoundedUtil.drawRound((float) scaledResolution.getScaledWidth() / 2 - 91, scaledResolution.getScaledHeight() - 25,
                                182, 22, 4, new Color(0, 0, 0, 190));
                        RoundedUtil.drawRound((float) scaledResolution.getScaledWidth() / 2 - 91 + mc.thePlayer.inventory.currentItem * 20,
                                scaledResolution.getScaledHeight() - 25, 22, 22, 4, new Color(0, 0, 0, 190));

                        RenderHelper.enableGUIStandardItemLighting();
                        for (int j = 0; j < 9; ++j) {
                            int k = scaledResolution.getScaledWidth() / 2 - 90 + j * 20 + 2;
                            int l = scaledResolution.getScaledHeight() - 22;
                            this.renderHotbarItem(j, k, l, event.getPartialTicks(), mc.thePlayer);
                        }
                        RenderHelper.disableStandardItemLighting();
                    }
                    case "FPS Counter" -> {
                        fpsCounter.setWidth(fontRenderer.getStringWidth(String.format("FPS: %s", Minecraft.getDebugFPS())) + 10);
                        fpsCounter.setHeight(20);
                        RoundedUtil.drawRound(fpsCounter.getX(), fpsCounter.getY(), fpsCounter.getWidth(), fpsCounter.getHeight(), 4, new Color(0, 0, 0, 150));
                        fontRenderer.drawStringWithShadow(String.format("FPS: %s", Minecraft.getDebugFPS()), fpsCounter.getX()+ 5
                                , fpsCounter.getY() + fontRenderer.getHeight(), -1);
                    }
                    case "BPS Counter" -> {
                        bpsCounter.setWidth(fontRenderer.getStringWidth(String.format("BPS: %s", MovementUtil.getBPS())) + 10);
                        bpsCounter.setHeight(20);
                        RoundedUtil.drawRound(bpsCounter.getX(), bpsCounter.getY(), bpsCounter.getWidth(), bpsCounter.getHeight(), 4,  new Color(0, 0, 0, 150));
                        fontRenderer.drawStringWithShadow(String.format("BPS: %s", MovementUtil.getBPS()), bpsCounter.getX()+ 5
                                , bpsCounter.getY() + fontRenderer.getHeight(), -1);
                    }
                    case "Packet Balance" -> {
                        balanceCounter.setWidth(fontRenderer.getStringWidth(String.format("Balance: %s", BalanceUtil.INSTANCE.getBalance() / 50)) + 10);
                        balanceCounter.setHeight(20);
                        RoundedUtil.drawRound(balanceCounter.getX(), balanceCounter.getY(), balanceCounter.getWidth(), balanceCounter.getHeight(), 4, new Color(0, 0, 0, 150));
                        fontRenderer.drawStringWithShadow(String.format("Balance: %s", BalanceUtil.INSTANCE.getBalance() / 50), balanceCounter.getX() + 5
                                , balanceCounter.getY() + fontRenderer.getHeight(), -1);
                    }
                }
            }
        });

        float stringY = 4;
        for(String string : hudName.getString().split("/n")){
            if(colorMode.is("Blend")){
                String fun = StringUtils.colorCodeString(string) + (Client.INSTANCE.getUser() == null ? "" : " | " + Client.INSTANCE.getUser().getUsername());
                char[] letters = fun.toCharArray();
                float xOffset = 0, fadeOffset = 0;

                for(char character : letters){
                    fontRenderer.drawStringWithShadow(String.valueOf(character), 4 + xOffset, stringY, getHudColor(fadeOffset).getRGB());
                    xOffset += fontRenderer.getStringWidth(String.valueOf(character));
                    fadeOffset -= 3;
                }
            }else {
                fontRenderer.drawStringWithShadow(StringUtils.colorCodeString(string) + (Client.INSTANCE.getUser() == null ?
                        "" : " | " + Client.INSTANCE.getUser().getUsername()), 4, stringY, getHudColor(stringY));
            }
            stringY += fontRenderer.getHeight() + 1;
        }

        float y = 2;
        for (Module module : getEnabledModules()) {
            float stringWidth = fontRenderer.getStringWidth(module.getDisplayName());
            if(!module.isEnabled())
                module.getAnimation().setDirection(Direction.BACKWARDS);
            
            Color bgColor = ColorUtil.applyOpacity(Color.black, backgroundOppacity.getValue().intValue());
            switch(backgroundMode.getMode()){
                case "Black" -> bgColor = new Color(0, 0, 0, backgroundOppacity.getValue().intValue());
                case "Colored" -> {
                    switch(backgroundColorMode.getMode()){
                        case "Client" -> bgColor = ColorUtil.applyOpacity(getHudColor(y * -1), (float) backgroundOppacity.getValue().intValue() / 255);
                        case "Custom" -> bgColor = ColorUtil.applyOpacity(backgroundColor.getColor(), (float) backgroundOppacity.getValue().intValue() / 255);
                    }
                }
            }

            if(background.isEnabled()) {
                Gui.drawRect2(scaledResolution.getScaledWidth() - (fontRenderer.getStringWidth(module.getDisplayName()) * module.getAnimation().getOutput().floatValue()) - 3.5f, y - 1.5f,
                        fontRenderer.getStringWidth(module.getDisplayName()) + 3.5f, fontRenderer.getHeight() + 1.5f,
                        bgColor.getRGB());
            }
            
            fontRenderer.drawStringWithShadow(module.getDisplayName(), scaledResolution.getScaledWidth() -
                    (fontRenderer.getStringWidth(module.getDisplayName()) * module.getAnimation().getOutput().floatValue()) - 2, y + 0.5f,
                    getHudColor(y * -1).getRGB());


            if(outline.isEnabled()) {
                if (getEnabledModules().indexOf(module) != getEnabledModules().size() - 1) {
                    Module nextModule = getEnabledModules().get(getEnabledModules().indexOf(module) + 1);
                    float dist = (stringWidth - fontRenderer.getStringWidth(nextModule.getDisplayName()));

                    Gui.drawRect2(scaledResolution.getScaledWidth() - (fontRenderer.getStringWidth(module.getDisplayName()) * module.getAnimation().getOutput().floatValue()) - 3.5f,
                            y + fontRenderer.getHeight(), dist + 0.2f, 1.2f, getHudColor(y * -1).getRGB());
                } else {
                    Gui.drawRect2(scaledResolution.getScaledWidth() - (fontRenderer.getStringWidth(module.getDisplayName()) * module.getAnimation().getOutput().floatValue()) - 3.5f,
                            y + fontRenderer.getHeight(), stringWidth + 6, 1.2f, getHudColor(y * -1).getRGB());
                }

                Gui.drawRect2(scaledResolution.getScaledWidth() - (fontRenderer.getStringWidth(module.getDisplayName()) * module.getAnimation().getOutput().floatValue()) - 3.5f, y - (getEnabledModules().indexOf(module) == 0 ? 2f : 1.5f),
                        1.2f, fontRenderer.getHeight() + 2.5f,
                        getHudColor(y * -1).getRGB());
            }


            y += fontRenderer.getHeight() + 1.5f;
        }
    };

    @Callback
    final EventCallback<ShaderEvent> shaderEventEventCallback = event -> {
        Client.INSTANCE.getNotificationManager().render(true);
        CustomFont fontRenderer = normalFont20;
        ScaledResolution scaledResolution = ScaledResolution.fetchResolution(mc);

        if(moduleCollection == null){
            moduleCollection = Client.INSTANCE.getModuleManager().getModules();
            return;
        }

        

//        mc.getItemRenderer().renderItem(mc.thePlayer, mc.thePlayer.getHeldItem(), ItemCameraTransforms.TransformType.FIXED);
        GL11.glPushMatrix();



//        mc.getItemRenderer().;
        GL11.glPopMatrix();
        components.getBoolSettings().forEach(booleanSetting -> {
            if(booleanSetting.isEnabled()){
                switch (booleanSetting.getName()) {
                    case "Keybind Gui" -> {
                        RoundedUtil.drawRound(keyBindGuiDrag.getX(), keyBindGuiDrag.getY(), keyBindGuiDrag.getWidth(), keyBindGuiDrag.getHeight(), 4, shaderMode.is("Bloom") ? Color.black : getHudColor((float) System.currentTimeMillis() / 600));
                        RoundedUtil.drawRound(keyBindGuiDrag.getX(), keyBindGuiDrag.getY(), keyBindGuiDrag.getWidth(), 12, 4, shaderMode.is("Bloom") ? Color.black : getHudColor((float) System.currentTimeMillis() / 600));
                    }
                    case "Hotbar" -> {
                        RoundedUtil.drawRound((float) scaledResolution.getScaledWidth() / 2 - 91, scaledResolution.getScaledHeight() - 25,
                                182, 22, 4, new Color(0, 0, 0, 190));
                        RoundedUtil.drawRound((float) scaledResolution.getScaledWidth() / 2 - 91 + mc.thePlayer.inventory.currentItem * 20,
                                scaledResolution.getScaledHeight() - 25, 22, 22, 4, new Color(0, 0, 0, 190));
                    }
                    case "FPS Counter" -> RoundedUtil.drawRound(fpsCounter.getX(), fpsCounter.getY(), fpsCounter.getWidth(), fpsCounter.getHeight(), 4, shaderMode.is("Bloom") ? Color.black : getHudColor((float) System.currentTimeMillis() / 600));
                    case "BPS Counter" -> RoundedUtil.drawRound(bpsCounter.getX(), bpsCounter.getY(), bpsCounter.getWidth(), bpsCounter.getHeight(), 4, shaderMode.is("Bloom") ? Color.black : getHudColor((float) System.currentTimeMillis() / 600));
                    case "Packet Balance" -> RoundedUtil.drawRound(balanceCounter.getX(), balanceCounter.getY(), balanceCounter.getWidth(), balanceCounter.getHeight(), 4, shaderMode.is("Bloom") ? Color.black : getHudColor((float) System.currentTimeMillis() / 600));
                }
            }
        });


        float y = 2;
        //  Gui.drawRect(30, 30, 30, 30, -1);
        for (Module module : getEnabledModules()) {
            if(background.isEnabled()) {
                Gui.drawRect2(scaledResolution.getScaledWidth() - (fontRenderer.getStringWidth(module.getDisplayName()) * module.getAnimation().getOutput().floatValue()) - 3.5f, y - 1.5f,
                        fontRenderer.getStringWidth(module.getDisplayName()) + 2.5f, fontRenderer.getHeight() + 1.5f,
                        shaderMode.is("Bloom") ? Color.black.getRGB() : getHudColor(y * -1).getRGB());
            }

            Color bgColor = ColorUtil.applyOpacity(Color.black, backgroundOppacity.getValue().intValue());
            switch(backgroundMode.getMode()){
                case "Black" -> bgColor = new Color(0, 0, 0, backgroundOppacity.getValue().intValue());
                case "Colored" -> {
                    switch(backgroundColorMode.getMode()){
                        case "Client" -> bgColor = ColorUtil.applyOpacity(getHudColor(y * -1), (float) backgroundOppacity.getValue().intValue() / 255);
                        case "Custom" -> bgColor = ColorUtil.applyOpacity(backgroundColor.getColor(), (float) backgroundOppacity.getValue().intValue() / 255);
                    }
                }
            }

            if(!event.isBloom()) {
                Gui.drawRect2(scaledResolution.getScaledWidth() - (fontRenderer.getStringWidth(module.getDisplayName()) * module.getAnimation().getOutput().floatValue()) - 3.5f, y - 1.5f,
                        fontRenderer.getStringWidth(module.getDisplayName()) + 3.5f, fontRenderer.getHeight() + 1.5f,
                        bgColor.getRGB());
            }

            if(event.isBloom()){
                fontRenderer.drawStringWithShadow(module.getDisplayName(), scaledResolution.getScaledWidth() -
                                (fontRenderer.getStringWidth(module.getDisplayName()) * module.getAnimation().getOutput().floatValue()) - 2, y + 0.5f,
                        getHudColor(y * -1).getRGB());
            }

            y += fontRenderer.getHeight() + 1.5f;
        }
    };

    public CopyOnWriteArrayList<Module> getEnabledModules(){
        moduleCollection.forEach(module -> {
            if(module.isEnabled() && !enabledModules.contains(module)){
                enabledModules.add(module);
            } else if(!module.isEnabled() && module.getAnimation().finished(Direction.BACKWARDS)){
                enabledModules.remove(module);
            }
        });
        enabledModules.sort(Comparator.comparingDouble(mName -> -normalFont20.getStringWidth(mName.getDisplayName())));
        return enabledModules;
    }

    private void renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer player)
    {
        ItemStack itemstack = player.inventory.mainInventory[index];

        if (itemstack != null)
        {
            float f = (float)itemstack.animationsToGo - partialTicks;

            if (f > 0.0F)
            {
                GlStateManager.pushMatrix();
                float f1 = 1.0F + f / 5.0F;
                GlStateManager.translate((float)(xPos + 8), (float)(yPos + 12), 0.0F);
                GlStateManager.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
                GlStateManager.translate((float)(-(xPos + 8)), (float)(-(yPos + 12)), 0.0F);
            }

            mc.getRenderItem().renderItemAndEffectIntoGUI(itemstack, xPos, yPos);

            if (f > 0.0F)
            {
                GlStateManager.popMatrix();
            }

            mc.getRenderItem().renderItemOverlays(this.mc.fontRendererObj, itemstack, xPos, yPos);
        }
    }
}
