package dev.africa.pandaware.impl.module.render;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.game.MouseEvent;
import dev.africa.pandaware.impl.event.render.RenderEvent;
import dev.africa.pandaware.impl.font.Fonts;
import dev.africa.pandaware.impl.module.combat.KillAuraModule;
import dev.africa.pandaware.impl.module.combat.TPAuraModule;
import dev.africa.pandaware.impl.module.misc.StreamerModule;
import dev.africa.pandaware.impl.setting.EnumSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.impl.ui.UISettings;
import dev.africa.pandaware.utils.client.MouseUtils;
import dev.africa.pandaware.utils.math.MathUtils;
import dev.africa.pandaware.utils.math.apache.ApacheMath;
import dev.africa.pandaware.utils.player.PlayerUtils;
import dev.africa.pandaware.utils.render.ColorUtils;
import dev.africa.pandaware.utils.render.RenderUtils;
import dev.africa.pandaware.utils.render.StencilUtils;
import dev.africa.pandaware.utils.render.animator.Animator;
import dev.africa.pandaware.utils.render.animator.Easing;
import lombok.AllArgsConstructor;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

@ModuleInfo(name = "Target HUD", category = Category.VISUAL)
public class TargetHudModule extends Module {
    private EntityPlayer cachedEntity;
    private EntityPlayer entity;

    private boolean dragging;

    private int draggingX, draggingY, width, height;

    private final Animator animator = new Animator();

    private float healthAnimation;
    private float armorAnimation;

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.PANDAWARE);

    private final NumberSetting posX = new NumberSetting("Pos X",
            Toolkit.getDefaultToolkit().getScreenSize().width / 2, 0, 0, 1);
    private final NumberSetting posY = new NumberSetting("Pos Y",
            Toolkit.getDefaultToolkit().getScreenSize().height / 2, 0, 0, 1);

    public TargetHudModule() {
        this.registerSettings(this.mode, this.posX, this.posY);
    }

    @EventHandler
    EventCallback<RenderEvent> onRender = event -> {
        if (event.getType() == RenderEvent.Type.RENDER_2D) {
            KillAuraModule killAuraModule = Client.getInstance().getModuleManager()
                    .getByClass(KillAuraModule.class);
            TPAuraModule tpAuraModule = Client.getInstance().getModuleManager()
                    .getByClass(TPAuraModule.class);

            if (killAuraModule.getData().isEnabled() && killAuraModule.getTarget() != null) {
                if (killAuraModule.getTarget() instanceof EntityPlayer) {
                    this.entity = (EntityPlayer) killAuraModule.getTarget();

                    if (this.entity != null) {
                        this.cachedEntity = this.entity;
                    }
                }
            } else if (tpAuraModule.getData().isEnabled() && tpAuraModule.getTarget() != null) {
                if (tpAuraModule.getTarget() instanceof EntityPlayer) {
                    this.entity = (EntityPlayer) tpAuraModule.getTarget();

                    if (this.entity != null) {
                        this.cachedEntity = this.entity;
                    }
                }
            } else {
                this.entity = null;
            }
            if (this.entity == null && killAuraModule.getTarget() == null
                    || !killAuraModule.getData().isEnabled()) {
                if (mc.currentScreen instanceof GuiChat) {
                    this.entity = mc.thePlayer;

                    if (this.entity != null) {
                        this.cachedEntity = this.entity;
                    }
                }
            }

            if (this.cachedEntity == null) return;

            if (this.dragging) {
                if (!(mc.currentScreen instanceof GuiChat)) {
                    this.dragging = false;
                } else {
                    this.posX.setValue(this.draggingX + event.getMousePosition().getX());
                    this.posY.setValue(this.draggingY + event.getMousePosition().getY());
                }
            }

            this.posX.setValue(Math.min(this.posX.getValue().doubleValue(), event.getResolution().getScaledWidth() - this.width - 1));
            this.posY.setValue(Math.min(this.posY.getValue().doubleValue(), event.getResolution().getScaledHeight() - this.height - 1));
            this.posX.setValue(Math.max(this.posX.getValue().doubleValue(), 1));
            this.posY.setValue(Math.max(this.posY.getValue().doubleValue(), 0.5));

            this.animator.setMin(0).setMax(1).setSpeed(3.3f);

            if (this.entity != null && this.animator.getValue() <= 1F) {
                this.animator.setEase(Easing.QUINTIC_OUT).setReversed(false).update();
            } else if (this.entity == null && this.animator.getValue() > 0F) {
                this.animator.setEase(Easing.QUINTIC_IN).setReversed(true).update();
            } else if (this.animator.getValue() <= 0F) {
                this.cachedEntity = null;
                return;
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate(this.posX.getValue().intValue(), this.posY.getValue().intValue(), 0);

            if (this.animator.getValue() < 1D) {
                GlStateManager.translate((this.width / 2f) * (1 - this.animator.getValue()),
                        (this.height / 2f) * (1 - this.animator.getValue()), 0);
                GlStateManager.scale(this.animator.getValue(), this.animator.getValue(),
                        this.animator.getValue());
            }

            float healthPercentage = MathHelper.clamp_float(this.cachedEntity.getHealth()
                    / this.cachedEntity.getMaxHealth(), 0, 1);
            float armorPercentage = MathHelper.clamp_float(this.cachedEntity.getTotalArmorValue()
                    / 20.0f, 0, 1);

            switch (this.mode.getValue()) {
                case PANDAWARE: {
                    this.width = 150;
                    this.height = 53;

                    double nameLength = Fonts.getInstance().getArialBdNormal()
                            .getStringWidth(this.cachedEntity.getName());
                    if (nameLength > 94) {
                        this.width = (int) (55 + nameLength);
                    }

                    HUDModule hud = Client.getInstance().getModuleManager().getByClass(HUDModule.class);
                    if (hud.getHudMode().getValue() == HUDModule.HUDMode.ROUNDED) {
                        RenderUtils.drawRoundedRect(0, 0, this.width, this.height, 3,
                                UISettings.INTERNAL_COLOR);
                    } else {
                        RenderUtils.drawVerticalGradientRect(0, 0, this.width, this.height, UISettings.INTERNAL_COLOR,
                                UISettings.INTERNAL_COLOR);
                    }

                    if (Client.getInstance().getModuleManager().getByClass(StreamerModule.class).getData().isEnabled()) {
                        if (this.cachedEntity.getName() == mc.thePlayer.getName())
                            Fonts.getInstance().getArialBdNormal().drawString("Legit Player", this.height - 9, 5, -1);
                        else Fonts.getInstance().getArialBdNormal().drawString("Player", this.height - 9, 5, -1);
                    } else {
                        Fonts.getInstance().getArialBdNormal().drawString(this.cachedEntity.getName(),
                                this.height - 9, 5, -1);
                    }

                    Fonts.getInstance().getArialBdNormal().drawStringWithShadow("§fHealth: §7" +
                                    MathUtils.roundToDecimal(this.cachedEntity.getHealth(), 2),
                            this.height - 9, 16, -1);

                    Fonts.getInstance().getArialBdNormal().drawStringWithShadow("§fPing: §7" +
                            PlayerUtils.getPing(this.cachedEntity), this.height - 9, 28, -1);

                    RenderUtils.drawRoundedRect(2, 2, this.height - 14, this.height - 14, 3,
                            new Color(50, 50, 50, 255));
                    StencilUtils.stencilStage(StencilUtils.StencilStage.ENABLE_MASK);
                    RenderUtils.drawRoundedRect(3, 3, this.height - 16,
                            this.height - 16, 3, Color.WHITE);
                    StencilUtils.stencilStage(StencilUtils.StencilStage.ENABLE_DRAW);
                    RenderUtils.renderSkinHead(this.cachedEntity, 3, 3, this.height - 16);
                    ESPModule esp = Client.getInstance().getModuleManager().getByClass(ESPModule.class);
                    if (esp.getData().isEnabled() && esp.getMode().getValue() == ESPModule.Mode.FEMBOY && esp.getFemboyMode().getValue() == ESPModule.Femboy.MIXED_NUTS)
                        RenderUtils.drawImage(new ResourceLocation("pandaware/icons/mixednuts.png"), 3, 3, this.width - 16, this.height - 16);
                    StencilUtils.stencilStage(StencilUtils.StencilStage.DISABLE);

                    HUDModule hudModule = Client.getInstance().getModuleManager().getByClass(HUDModule.class);

                    StencilUtils.stencilStage(StencilUtils.StencilStage.ENABLE_MASK);
                    if (hud.getHudMode().getValue() == HUDModule.HUDMode.ROUNDED) {
                        RenderUtils.drawRoundedRect(3, this.height - 9,
                                (this.width - 6) * MathHelper.clamp_double(this.healthAnimation, 0.02, 1),
                                5, 2, Color.WHITE);
                    } else {
                        RenderUtils.drawVerticalGradientRect(3, this.height - 9, (this.width - 6) *
                                MathHelper.clamp_double(this.healthAnimation, 0.02, 1), 5,
                                UISettings.INTERNAL_COLOR, UISettings.INTERNAL_COLOR);
                    }
                    StencilUtils.stencilStage(StencilUtils.StencilStage.ENABLE_DRAW);
                    for (int i = 3; i < (this.width - 3); i++) {
                        RenderUtils.drawRect(i, this.height - 9, i + 1, this.height - 4,
                                hudModule.getColorMode().getValue() == HUDModule.ColorMode.RAINBOW ?
                                        ColorUtils.rainbow(i * 20, 0.7f, 3.5).getRGB() :
                                        ColorUtils.getColorSwitch(UISettings.FIRST_COLOR, UISettings.SECOND_COLOR,
                                                850, i, 11, 1).getRGB());
                    }
                    StencilUtils.stencilStage(StencilUtils.StencilStage.DISABLE);

                    this.healthAnimation += ((healthPercentage - this.healthAnimation) / 5.5f) * RenderUtils.fpsMultiplier();
                    break;
                }

                case FLUX: {
                    this.height = 32;

                    double nameLength = Fonts.getInstance().getProductSansMedium()
                            .getStringWidth(this.cachedEntity.getName());
                    this.width = ApacheMath.max((int) (65 + nameLength), 140);

                    RenderUtils.drawRect(0, 0, this.width, this.height, new Color(0, 0, 0, 100).getRGB());

                    RenderUtils.drawRect(1, 1, this.height - 1, this.height - 1,
                            new Color(0, 255, 111).getRGB());
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(-0.5, -0.5, 0);
                    RenderUtils.renderSkinHead(this.cachedEntity, 2, 2, this.height - 3);
                    GlStateManager.popMatrix();

                    if (Client.getInstance().getModuleManager().getByClass(StreamerModule.class).getData().isEnabled()) {
                        Fonts.getInstance().getProductSansNormal().drawString("Legit Player", this.height, 2, -1);
                    } else {
                        Fonts.getInstance().getProductSansNormal().drawString(this.cachedEntity.getName(),
                                this.height, 2, -1);
                    }

                    String string = MathUtils.roundToDecimal(this.cachedEntity.getHealth(), 1) + " hp";
                    Fonts.getInstance().getProductSansSmall().drawString(string, this.width -
                            Fonts.getInstance().getProductSansSmall().getStringWidth(string), 3.5, -1);

                    GlStateManager.translate(this.height + 2, 0, 0);
                    RenderUtils.drawRect(0, this.height - 8 - 6, this.width - 37, this.height - 5 - 6,
                            new Color(0, 0, 0, 113).getRGB());
                    RenderUtils.drawRect(0, this.height - 8 - 6,
                            this.healthAnimation * (this.width - 37), this.height - 5 - 6,
                            new Color(0, 131, 128).getRGB());
                    RenderUtils.drawRect(0, this.height - 8 - 6,
                            healthPercentage * (this.width - 37), this.height - 5 - 6,
                            new Color(128, 255, 251).getRGB());


                    RenderUtils.drawRect(0, this.height - 8, this.width - 37, this.height - 5,
                            new Color(0, 0, 0, 113).getRGB());
                    RenderUtils.drawRect(0, this.height - 8, (this.armorAnimation) * (this.width - 37), this.height - 5,
                            new Color(0, 166, 255).getRGB());

                    this.healthAnimation += ((healthPercentage - this.healthAnimation) / 7) * RenderUtils.fpsMultiplier();
                    this.armorAnimation += ((armorPercentage - this.armorAnimation) / 7) * RenderUtils.fpsMultiplier();
                    break;
                }

                case NOVOLINE: {
                    this.height = 41;
                    double nameLength = Fonts.getInstance().getArialBdBig()
                            .getStringWidth(this.cachedEntity.getName());
                    this.width = ApacheMath.max((int) (35 + nameLength), 150);

                    RenderUtils.drawRect(0, 0, this.width, this.height, new Color(0, 0, 0, 100).getRGB());

                    RenderUtils.renderSkinHead(this.cachedEntity, 2, 2, this.height - 12);

                    if (Client.getInstance().getModuleManager().getByClass(StreamerModule.class).getData().isEnabled()) {
                        Fonts.getInstance().getArialBdBig().drawString("Legit Player", this.height - 9, 1, -1);
                    } else {
                        Fonts.getInstance().getArialBdBig().drawString(this.cachedEntity.getName(),
                                this.height - 9, 1, -1);
                    }

                    String string = "Health: " + MathUtils.roundToDecimal(this.cachedEntity.getHealth(), 1);
                    Fonts.getInstance().getProductSansSmall().drawString(string, this.height - 9, 14, -1);

                    string = "Distance: " + MathUtils.roundToDecimal(
                            mc.thePlayer.getDistanceToEntity(this.cachedEntity), 1) + "m";
                    Fonts.getInstance().getProductSansSmall().drawString(string, this.height - 9, 23, -1);

                    GlStateManager.translate(2, 0, 0);
                    RenderUtils.drawRect(0, this.height - 4 - 4, this.width - 4, this.height - 2 - 4,
                            new Color(0, 0, 0, 113).getRGB());
                    RenderUtils.drawHorizontalGradientRect(0, this.height - 4 - 4,
                            this.healthAnimation * (this.width - 4), 2,
                            new Color(0, 145, 70), new Color(0, 255, 157));

                    RenderUtils.drawRect(0, this.height - 4, this.width - 4, this.height - 2,
                            new Color(0, 0, 0, 113).getRGB());
                    RenderUtils.drawHorizontalGradientRect(0, this.height - 4,
                            (this.armorAnimation) * (this.width - 4), 2,
                            new Color(0, 77, 178), new Color(0, 217, 255));
                    this.healthAnimation += ((healthPercentage - this.healthAnimation) / 5.5f) * RenderUtils.fpsMultiplier();
                    this.armorAnimation += ((armorPercentage - this.armorAnimation) / 5.5f) * RenderUtils.fpsMultiplier();
                    break;
                }

                case BASIC_SMALL: {
                    this.height = 25;
                    RenderUtils.drawRoundedRect(0, 0, this.width, this.height, 3, new Color(0, 0, 0, 100));
                    double nameLength1 = Fonts.getInstance().getArialBdBig().getStringWidth(this.cachedEntity.getName());
                    this.width = ApacheMath.max((int) (35 + nameLength1), 150);
                    if (Client.getInstance().getModuleManager().getByClass(StreamerModule.class).getData().isEnabled()) {
                        Fonts.getInstance().getArialBdBig().drawString("********", this.height - 9, 1, -1);
                    } else {
                        Fonts.getInstance().getTahomaBig().drawString("§l" + this.cachedEntity.getName(), this.height, 1, -1);
                    }
                    String playerHealth1 = String.valueOf(ApacheMath.round(this.cachedEntity.getHealth()) + " / " + this.cachedEntity.getMaxHealth());
                    String playerPing1 = PlayerUtils.getPing(this.cachedEntity) + "ms";
                    this.width = ApacheMath.max((int) (35 + nameLength1), 150);
                    Fonts.getInstance().getTahomaNormal().drawString(playerHealth1, this.height, 14, getPlayerColor().getRGB());
                    Fonts.getInstance().getTahomaNormal().drawString(playerPing1, width - Fonts.getInstance().getTahomaNormal().getStringWidth(playerPing1), 15, -1);
                    RenderUtils.renderSkinHead(this.cachedEntity, 2, 2, this.height - 4);
                    break;
                }

                case BASIC_BIG: {
                    this.height = 50;
                    RenderUtils.drawRoundedRect(0, 0, this.width, this.height, 3, new Color(0, 0, 0, 100));
                    double nameLength = Fonts.getInstance().getArialBdBig().getStringWidth(this.cachedEntity.getName());
                    this.width = ApacheMath.max((int) (35 + nameLength), 150);
                    if (Client.getInstance().getModuleManager().getByClass(StreamerModule.class).getData().isEnabled()) {
                        Fonts.getInstance().getArialBdBig().drawString("********", this.height - 9, 1, -1);
                    } else {
                        Fonts.getInstance().getTahomaBig().drawString("§l" + this.cachedEntity.getName(), this.height, 1, -1);
                    }
                    String playerHealth = String.valueOf(ApacheMath.round(this.cachedEntity.getHealth()));
                    String maxPlayerHealth = " / " + this.cachedEntity.getMaxHealth();
                    String playerPing = PlayerUtils.getPing(this.cachedEntity) + "ms";
                    this.width = ApacheMath.max((int) (35 + nameLength), 150);
                    Fonts.getInstance().getTahomaNormal().drawString(playerHealth, this.height, 12, getPlayerColor().getRGB());
                    Fonts.getInstance().getTahomaNormal().drawString(maxPlayerHealth, this.height + 18, 12, -1);
                    Fonts.getInstance().getTahomaNormal().drawString(playerPing, width - Fonts.getInstance().getTahomaNormal().getStringWidth(playerPing), height - Fonts.getInstance().getTahomaNormal().FONT_HEIGHT, -1);
                    RenderUtils.renderSkinHead(this.cachedEntity, 2, 2, this.height - 4);
                }
            }
            GlStateManager.popMatrix();
        }
    };

    @EventHandler
    EventCallback<MouseEvent> onMouse = event -> {
        if (event.getType() == MouseEvent.Type.CLICK && event.getMouseButton() == 0) {
            if (MouseUtils.isMouseInBounds(event.getMouseX(), event.getMouseY(),
                    this.posX.getValue().intValue(), this.posY.getValue().intValue(),
                    this.posX.getValue().intValue() + this.width,
                    this.posY.getValue().intValue() + this.height)) {
                this.dragging = true;
                this.draggingX = this.posX.getValue().intValue() - event.getMouseX();
                this.draggingY = this.posY.getValue().intValue() - event.getMouseY();
            }
        }

        if (event.getType() == MouseEvent.Type.RELEASED) {
            this.dragging = false;
        }
    };

    @Override
    public String getSuffix() {
        return this.mode.getValue().label;
    }

    public Color getPlayerColor() {
        Color playerHealthColor = null;
        if (this.cachedEntity.getHealth() >= 16) {
            playerHealthColor = new Color(55, 245, 55, 255);
        } else if (this.cachedEntity.getHealth() >= 10 && this.cachedEntity.getHealth() <= 16) {
            playerHealthColor = new Color(255, 162, 0, 255);
        } else if (this.cachedEntity.getHealth() >= 0 && this.cachedEntity.getHealth() <= 10) {
            playerHealthColor = new Color(255, 69, 69, 255);
        }
        return playerHealthColor;
    }

    @AllArgsConstructor
    enum Mode {
        PANDAWARE("Pandaware"),
        FLUX("Flux"),
        BASIC_BIG("Basic (Big)"),
        BASIC_SMALL("Basic (Small)"),
        NOVOLINE("Novoline");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }
}
