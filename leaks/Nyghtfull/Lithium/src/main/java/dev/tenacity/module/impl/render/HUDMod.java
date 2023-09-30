package dev.tenacity.module.impl.render;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.render.Render2DEvent;
import dev.tenacity.event.impl.render.ShaderEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.combat.KillAura;
import dev.tenacity.module.impl.player.ChestStealer;
import dev.tenacity.module.impl.player.InvManager;
import dev.tenacity.module.settings.impl.*;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.font.AbstractFontRenderer;
import dev.tenacity.utils.font.CustomFont;
import dev.tenacity.utils.misc.RomanNumeralUtils;
import dev.tenacity.utils.render.*;
import dev.tenacity.utils.server.PingerUtils;
import dev.tenacity.utils.tuples.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

public class HUDMod extends Module {

    private final StringSetting clientName = new StringSetting("Client Name");
    private final ModeSetting watermarkMode = new ModeSetting("Watermark Mode", "Tenacity","Japan", "Tenacity", "ZeroDay", "Jello", "Neverlose", "Tenasense", "Tenabition", "Logo", "None");
    public static final ColorSetting color1 = new ColorSetting("Color 1", new Color(0xffa028d4));
    public static final ColorSetting color2 = new ColorSetting("Color 2", new Color(0xff0008ff));
    public static final ModeSetting theme = Theme.getModeSetting("Theme Selection", "Tenacity");
    public static final BooleanSetting customFont = new BooleanSetting("Custom Font", true);
    private static final MultipleBoolSetting infoCustomization = new MultipleBoolSetting("Info Options",
            new BooleanSetting("Show Ping", false),
            new BooleanSetting("Semi-Bold Info", true),
            new BooleanSetting("White Info", false),
            new BooleanSetting("Info Shadow", true));

    public static final MultipleBoolSetting hudCustomization = new MultipleBoolSetting("HUD Options",
            new BooleanSetting("Radial Gradients", true),
            new BooleanSetting("Potion HUD", true),
            new BooleanSetting("Armor HUD", true),
            new BooleanSetting("Render Cape", true),
            new BooleanSetting("Lowercase", false));

    private static final MultipleBoolSetting disableButtons = new MultipleBoolSetting("Disable Buttons",
            new BooleanSetting("Disable KillAura", true),
            new BooleanSetting("Disable InvManager", true),
            new BooleanSetting("Disable ChestStealer", true));

    public HUDMod() {
        super("HUD", Category.RENDER, "customizes the client's appearance");
        color1.addParent(theme, modeSetting -> modeSetting.is("Custom Theme"));
        color2.addParent(theme, modeSetting -> modeSetting.is("Custom Theme") && !color1.isRainbow());
        this.addSettings(clientName, watermarkMode, theme, color1, color2, customFont, infoCustomization, hudCustomization, disableButtons);
        if (!enabled) this.toggleSilent();
    }

    public static int offsetValue = 0;

    private boolean version = true;

    public static float xOffset = 0;


    @Override
    public void onShaderEvent(ShaderEvent e) {
        Pair<Color, Color> clientColors = getClientColors();
        String name = Tenacity.NAME;


        if (e.isBloom()) {

            if (!clientName.getString().equals("")) {
                name = clientName.getString().replace("%time%", getCurrentTimeStamp());
            }


            String finalName = get(name);
            String intentInfo = Tenacity.INSTANCE.getIntentAccount().username;
            switch (watermarkMode.getMode()) {
                case "Logo":
                    GradientUtil.applyGradientCornerRL(
                            1.0F, 1.0F,
                            107.5F, 67.5F,
                            1.0F,
                            clientColors.getFirst().darker(),
                            clientColors.getSecond().darker(),
                            () -> {
                                mc.getTextureManager().bindTexture(new ResourceLocation("Tenacity/Logos/Lithium.png"));
                                Gui.drawModalRectWithCustomSizedTexture(1.0F, 1.0F, 0, 0, 107.5F, 67.5F, 107.5F, 67.5F);
                            }
                    );

                    GradientUtil.applyGradientCornerRL(
                            0, 0,
                            107.5F, 67.5F,
                            1.0F,
                            clientColors.getFirst(),
                            clientColors.getSecond(),
                            () -> {
                                mc.getTextureManager().bindTexture(new ResourceLocation("Tenacity/Logos/Lithium.png"));
                                Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 107.5F, 67.5F, 107.5F, 67.5F);
                            }
                    );

                    lithiumBoldFont24.drawStringWithShadow("Beta", 102, 15, Color.WHITE);
                    break;

                case "japan":
                    GradientUtil.applyGradientCornerRL(
                            1.0F, 1.0F,
                            107.5F, 67.5F,
                            1.0F,
                            clientColors.getFirst().darker(),
                            clientColors.getSecond().darker(),
                            () -> {
                                mc.getTextureManager().bindTexture(new ResourceLocation("Tenacity/Logos/Lyiasdnasd.png"));
                                Gui.drawModalRectWithCustomSizedTexture(1.0F, 1.0F, 0, 0, 107.5F, 67.5F, 107.5F, 67.5F);
                            }
                    );

                    GradientUtil.applyGradientCornerRL(
                            0, 0,
                            107.5F, 67.5F,
                            1.0F,
                            clientColors.getFirst(),
                            clientColors.getSecond(),
                            () -> {
                                mc.getTextureManager().bindTexture(new ResourceLocation("Tenacity/Logos/Lyiasdnasd.png"));
                                Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 107.5F, 67.5F, 107.5F, 67.5F);
                            }
                    );


                    break;
                case "Tenacity":
                    float xVal = 6f;
                    float yVal = 6f;
                    float versionWidth = lithiumFont16.getStringWidth(Tenacity.INSTANCE.getVersion());
                    float versionX = xVal + lithiumBoldFont40.getStringWidth(finalName);
                    float width = version ? (versionX + versionWidth) - xVal : lithiumBoldFont40.getStringWidth(finalName);


                    RenderUtil.resetColor();
                    GradientUtil.applyGradientHorizontal(xVal, yVal, width, 20, 1, clientColors.getFirst(), clientColors.getSecond(), () -> {
                        RenderUtil.setAlphaLimit(0);
                        lithiumBoldFont40.drawString(finalName, xVal, yVal, 0);
                        if (version) {
                            lithiumFont16.drawString(Tenacity.INSTANCE.getVersion(), versionX, yVal, 0);
                        }
                    });

                    break;
                case "Jello":
                    RenderUtil.resetColor();
                    GradientUtil.applyGradientHorizontal(5, 5, 75, 20, 1, clientColors.getFirst(), clientColors.getSecond(), () -> {
                        RenderUtil.setAlphaLimit(0);
                        lithiumBoldFont40.drawString(finalName, 5, 5, 0);
                    });

                    break;
                case "Neverlose":
                    CustomFont m22 = neverloseFont.size(22), t18 = lithiumFont18;

                    String str = String.format(
                            " §8|§f %s fps §8|§f %s §8|§f %s",
                            Minecraft.getDebugFPS(),
                            mc.session.getUsername(),
                            mc.isSingleplayer() || mc.getCurrentServerData() == null ? "Singleplayer" : mc.getCurrentServerData().serverIP
                    );

                    name = name.toUpperCase();

                    float nw = m22.getStringWidth(name);

                    Pair<Color, Color> colors = HUDMod.getClientColors();

                    RoundedUtil.drawGradientCornerLR(
                            7.5F,
                            7.5F,
                            nw + t18.getStringWidth(str) + 6f,
                            t18.getHeight() + 6,
                            2,
                            ColorUtil.interpolateColorsBackAndForth(5, 0, colors.getFirst().darker().darker(), colors.getSecond().darker().darker(), true),
                            ColorUtil.interpolateColorsBackAndForth(5, 20, colors.getFirst().darker().darker(), colors.getSecond().darker().darker(), true)
                    );

                    break;
                case "Tenasense":
                    String text = "§ftena§rsense§f" + " - " + intentInfo + " - " + (mc.isSingleplayer() ? "singleplayer" : mc.getCurrentServerData().serverIP) + " - "
                            + PingerUtils.getPing() + "ms ";
                    float x = 4.5f, y = 4.5f;

                    Gui.drawRect2(x, y, lithiumFont16.getStringWidth(text) + 7, 18.5, e.isBloom() ? new Color(59, 57, 57).getRGB() : Color.BLACK.getRGB());
                    break;
            }
        }
    }

    @Override
    public void onRender2DEvent(Render2DEvent e) {
        ScaledResolution sr = new ScaledResolution(mc);
        Pair<Color, Color> clientColors = getClientColors();
        String name = Tenacity.NAME;
        PostProcessing postProcessing = Tenacity.INSTANCE.getModuleCollection().getModule(PostProcessing.class);
        if (!postProcessing.isEnabled()) {
            version = false;
        }

        if (!clientName.getString().equals("")) {
            name = clientName.getString().replace("%time%", getCurrentTimeStamp());
        }

        version = name.equalsIgnoreCase(Tenacity.NAME);

        String finalName = get(name);
        String intentInfo = Tenacity.INSTANCE.getIntentAccount().username;

        switch (watermarkMode.getMode()) {
            case "Logo":
                GradientUtil.applyGradientCornerRL(
                        1.0F, 1.0F,
                        107.5F, 67.5F,
                        1.0F,
                        clientColors.getFirst().darker(),
                        clientColors.getSecond().darker(),
                        () -> {
                            mc.getTextureManager().bindTexture(new ResourceLocation("Tenacity/Logos/Lithium.png"));
                            Gui.drawModalRectWithCustomSizedTexture(1.0F, 1.0F, 0, 0, 107.5F, 67.5F, 107.5F, 67.5F);
                        }
                );

                GradientUtil.applyGradientCornerRL(
                        0, 0,
                        107.5F, 67.5F,
                        1.0F,
                        clientColors.getFirst(),
                        clientColors.getSecond(),
                        () -> {
                            mc.getTextureManager().bindTexture(new ResourceLocation("Tenacity/Logos/Lithium.png"));
                            Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 107.5F, 67.5F, 107.5F, 67.5F);
                        }
                );

                lithiumBoldFont24.drawStringWithShadow("Beta", 102, 15, Color.WHITE);
                break;

            case "Japan":
                GradientUtil.applyGradientCornerRL(
                        1.0F, 1.0F,
                        107.5F, 67.5F,
                        1.0F,
                        clientColors.getFirst().darker(),
                        clientColors.getSecond().darker(),
                        () -> {
                            mc.getTextureManager().bindTexture(new ResourceLocation("Tenacity/Logos/Lyiasdnasd.png"));
                            Gui.drawModalRectWithCustomSizedTexture(1.0F, 1.0F, 0, 0, 107.5F, 67.5F, 107.5F, 67.5F);
                        }
                );

                GradientUtil.applyGradientCornerRL(
                        0, 0,
                        107.5F, 67.5F,
                        1.0F,
                        clientColors.getFirst(),
                        clientColors.getSecond(),
                        () -> {
                            mc.getTextureManager().bindTexture(new ResourceLocation("Tenacity/Logos/Lyiasdnasd.png"));
                            Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 107.5F, 67.5F, 107.5F, 67.5F);
                        }
                );


                break;

            case "Tenacity":
                float xVal = 5;
                float yVal = 5;
                float spacing = 1;
                float versionWidth = lithiumFont16.getStringWidth(Tenacity.INSTANCE.getVersion());
                float versionX = xVal + lithiumBoldFont40.getStringWidth(finalName);
                float width = version ? (versionX + versionWidth) - xVal : lithiumBoldFont40.getStringWidth(finalName);

                Pair<Color, Color> darkerColors = clientColors.apply((c1, c2) -> Pair.of(ColorUtil.darker(c1, .6f), ColorUtil.darker(c2, .6f)));

                GradientUtil.applyGradientHorizontal(xVal + spacing, yVal + spacing, width + spacing,
                        20, 1, darkerColors.getFirst(), darkerColors.getSecond(), () -> {
                            RenderUtil.setAlphaLimit(0);
                            lithiumBoldFont40.drawString(finalName, xVal + spacing, yVal + spacing, 0);
                            if (version) {
                                lithiumFont16.drawString(Tenacity.INSTANCE.getVersion(), versionX + (spacing / 2f), yVal + (spacing / 2f), 0);
                            }
                        });


                RenderUtil.resetColor();
                GradientUtil.applyGradientHorizontal(xVal, yVal, width, 20, 1, clientColors.getFirst(), clientColors.getSecond(), () -> {
                    RenderUtil.setAlphaLimit(0);
                    lithiumBoldFont40.drawString(finalName, xVal, yVal, 0);
                    if (version) {
                        lithiumFont16.drawString(Tenacity.INSTANCE.getVersion(), versionX, yVal, 0);
                    }
                });
                break;

            case "ZeroDay":
                GradientUtil.applyGradientHorizontal(
                        7, 7,
                        lithiumFont40.getStringWidth(Tenacity.NAME),
                        lithiumFont40.getHeight(),
                        1.0F,
                        ColorUtil.rainbow(10, 0, 0.45F, 1.0F, 1.0F),
                        ColorUtil.rainbow(10, 50, 0.45F, 1.0F, 1.0F),
                        () -> {
                            lithiumFont40.drawStringWithShadow(
                                    Tenacity.NAME,
                                    7, 7,
                                    Color.BLACK.getRGB()
                            );
                        }
                );

                lithiumBoldFont16.drawString(
                        Tenacity.VERSION,
                        lithiumFont40.getStringWidth(Tenacity.NAME) + 7, 7,
                        Color.WHITE.getRGB()
                );
                break;

            case "Jello":
                RenderUtil.resetColor();
                GradientUtil.applyGradientHorizontal(5, 5, 25, 20, 0.6F, new Color(235, 235, 235), new Color(205, 205, 205), () -> {
                    RenderUtil.setAlphaLimit(0);
                    lithiumBoldFont40.drawString(finalName, 5, 5, 0);
                });

                break;
            case "Neverlose":
                CustomFont m22 = neverloseFont.size(22), t18 = lithiumFont18;

                String str = String.format(
                        " §0|§f %s fps §0|§f %s §0|§f %s",
                        Minecraft.getDebugFPS(),
                        mc.session.getUsername(),
                        mc.isSingleplayer() || mc.getCurrentServerData() == null ? "Singleplayer" : mc.getCurrentServerData().serverIP
                );

                name = name.toUpperCase();

                float nw = m22.getStringWidth(name);

                Pair<Color, Color> colors = HUDMod.getClientColors();

                RoundedUtil.drawGradientCornerLR(
                        7.5F,
                        7.5F,
                        nw + t18.getStringWidth(str) + 6f,
                        t18.getHeight() + 6,
                        2,
                        ColorUtil.interpolateColorsBackAndForth(5, 0, colors.getFirst().darker().darker(), colors.getSecond().darker().darker(), true),
                        ColorUtil.interpolateColorsBackAndForth(5, 20, colors.getFirst().darker().darker(), colors.getSecond().darker().darker(), true)
                );

                t18.drawString(str, 10.0F + nw, 10.0F, Color.WHITE);
                m22.drawString(name, 10.0F, 10.0F, Color.BLACK);
                m22.drawString(name, 10.0F, 10.0F, Color.WHITE);
                break;
            case "Tenasense":
                String text = "§ftena§rsense§f" + " - " + intentInfo + " - " + (mc.isSingleplayer() ? "singleplayer" : mc.getCurrentServerData().serverIP) + " - "
                        + PingerUtils.getPing() + "ms ";

                float x = 4.5f, y = 4.5f;

                int lineColor = new Color(59, 57, 57).darker().getRGB();
                Gui.drawRect2(x, y, lithiumFont16.getStringWidth(text) + 7, 18.5, new Color(59, 57, 57).getRGB());

                Gui.drawRect2(x + 2.5, y + 2.5, lithiumFont16.getStringWidth(text) + 2, 13, new Color(23, 23, 23).getRGB());

                // Top small bar
                Gui.drawRect2(x + 1, y + 1, lithiumFont16.getStringWidth(text) + 5, .5, lineColor);

                // Bottom small bar
                Gui.drawRect2(x + 1, y + 17, lithiumFont16.getStringWidth(text) + 5, .5, lineColor);

                // Left bar
                Gui.drawRect2(x + 1, y + 1.5, .5, 16, lineColor);

                // Right Bar
                Gui.drawRect2((x + 1.5) + lithiumFont16.getStringWidth(text) + 4, y + 1.5, .5, 16, lineColor);

                // Lowly saturated rainbow bar
                GradientUtil.drawGradientLR(x + 2.5f, y + 14.5f, lithiumFont16.getStringWidth(text) + 2, 1, 1, clientColors.getFirst(), clientColors.getSecond());

                // Bottom of the rainbow bar
                Gui.drawRect2(x + 2.5, y + 16, lithiumFont16.getStringWidth(text) + 2, .5, lineColor);
                lithiumFont16.drawString(text, x + 4.5f, y + 5.5f, clientColors.getSecond().getRGB());
                break;
            case "Tenabition":
                StringBuilder stringBuilder = new StringBuilder(name.replace("tenacity", "Tenabition")).insert(1, EnumChatFormatting.WHITE);

                RenderUtil.resetColor();
                mc.fontRendererObj.drawStringWithShadow(stringBuilder.toString(), 4, 4, clientColors.getFirst().getRGB());
                break;
        }


        RenderUtil.resetColor();
        drawBottomRight();

        RenderUtil.resetColor();
//        drawInfo(clientColors);

        drawArmor(sr);
    }

    private void drawBottomRight() {
        AbstractFontRenderer fr = customFont.isEnabled() ? lithiumFont20 : mc.fontRendererObj;
        ScaledResolution sr = new ScaledResolution(mc);
        float yOffset = (float) (14.5 * GuiChat.openingAnimation.getOutput().floatValue());

        boolean shadowInfo = infoCustomization.isEnabled("Info Shadow");

        if (hudCustomization.getSetting("Potion HUD").isEnabled()) {
            java.util.List<PotionEffect> potions = new ArrayList<>(mc.thePlayer.getActivePotionEffects());
            potions.sort(Comparator.comparingDouble(e -> -fr.getStringWidth(I18n.format(e.getEffectName()))));

            int count = 0;
            for (PotionEffect effect : potions) {
                Potion potion = Potion.potionTypes[effect.getPotionID()];
                String name = I18n.format(potion.getName()) + (effect.getAmplifier() > 0 ? " " + RomanNumeralUtils.generate(effect.getAmplifier() + 1) : "");
                Color c = new Color(potion.getLiquidColor());
                String str = get(name + " §7[" + Potion.getDurationString(effect) + "]");
                fr.drawString(str, sr.getScaledWidth() - fr.getStringWidth(str) - 2,
                        -10 + sr.getScaledHeight() - fr.getHeight() + (7 - (10 * (count + 1))) - yOffset,
                        new Color(c.getRed(), c.getGreen(), c.getBlue(), 255).getRGB(), shadowInfo);
                count++;
            }

            offsetValue = count * fr.getHeight();
        }





    }

    private final Map<String, String> bottomLeftText = new LinkedHashMap<>();

    private void drawInfo(Pair<Color, Color> clientColors) {
        boolean shadowInfo = infoCustomization.isEnabled("Info Shadow");
        boolean semiBold = infoCustomization.isEnabled("Semi-Bold Info");
        boolean whiteInfo = infoCustomization.isEnabled("White Info");
        String titleBold = semiBold ? "§l" : "";
        ScaledResolution sr = new ScaledResolution(mc);

        bottomLeftText.put("XYZ", Math.round(mc.thePlayer.posX) + " " + Math.round(mc.thePlayer.posY) + " " + Math.round(mc.thePlayer.posZ));
        bottomLeftText.put("Speed", String.valueOf(calculateBPS()));
        bottomLeftText.put("FPS", String.valueOf(Minecraft.getDebugFPS()));

        if (infoCustomization.isEnabled("Show Ping")) {
            bottomLeftText.put("Ping", PingerUtils.getPing());
            GuiNewChat.chatPos = 17 - 7;
        } else {
            GuiNewChat.chatPos = 17;
            bottomLeftText.remove("Ping");
        }

        //InfoStuff
        AbstractFontRenderer nameInfoFr = lithiumFont20;
        if (!customFont.isEnabled()) {
            nameInfoFr = mc.fontRendererObj;
        }

        if (semiBold) {
            xOffset = nameInfoFr.getStringWidth("§lXYZ: " + bottomLeftText.get("XYZ"));
        } else {
            xOffset = nameInfoFr.getStringWidth("XYZ: " + bottomLeftText.get("XYZ"));
        }


        float yOffset = (float) (14.5 * GuiChat.openingAnimation.getOutput().floatValue());
        float f2 = customFont.isEnabled() ? 0.5F : 1.0F;
        float f3 = customFont.isEnabled() ? 1 : 0.5F;
        float yMovement = !customFont.isEnabled() ? -1 : 0;


        if (whiteInfo) {
            float boldFontMovement = nameInfoFr.getHeight() + 2 + yOffset + yMovement;
            for (Map.Entry<String, String> line : bottomLeftText.entrySet()) {
                nameInfoFr.drawString(get(titleBold + line.getKey() + "§r: " + line.getValue()), 2, sr.getScaledHeight() - boldFontMovement, -1, shadowInfo);
                boldFontMovement += nameInfoFr.getHeight() + f3;
            }
        } else {

            float f = nameInfoFr.getHeight() + 2 + yOffset + yMovement;
            for (Map.Entry<String, String> line : bottomLeftText.entrySet()) {
                // Simulate a shadow
                if (shadowInfo) {
                    nameInfoFr.drawString(get(line.getValue()), 2 + f2 + nameInfoFr.getStringWidth(titleBold + line.getKey() + ":§r "), sr.getScaledHeight() - f + f2, 0xFF000000);
                }

                nameInfoFr.drawString(get(line.getValue()), 2 + nameInfoFr.getStringWidth(titleBold + line.getKey() + ":§r "), sr.getScaledHeight() - f, -1);

                f += nameInfoFr.getHeight() + f3;
            }


            float height = (nameInfoFr.getHeight() + 2) * bottomLeftText.size();
            float width = nameInfoFr.getStringWidth(titleBold + "Speed:");
            AbstractFontRenderer finalFr = nameInfoFr;

            if (shadowInfo) {
                float boldFontMovement1 = finalFr.getHeight() + 2 + yOffset + yMovement;
                for (Map.Entry<String, String> line : bottomLeftText.entrySet()) {
                    finalFr.drawString(get(titleBold + line.getKey() + ": "), 2 + f2, sr.getScaledHeight() - boldFontMovement1 + f2, 0xFF000000);
                    boldFontMovement1 += finalFr.getHeight() + f3;
                }
            }

            GradientUtil.applyGradientVertical(2, sr.getScaledHeight() - (height + yOffset + yMovement), width, height, 1, clientColors.getFirst(), clientColors.getSecond(), () -> {
                float boldFontMovement = finalFr.getHeight() + 2 + yOffset + yMovement;
                for (Map.Entry<String, String> line : bottomLeftText.entrySet()) {
                    finalFr.drawString(get(titleBold + line.getKey() + ": "), 2, sr.getScaledHeight() - boldFontMovement, -1);
                    boldFontMovement += finalFr.getHeight() + f3;
                }
            });

        }
    }

    private double calculateBPS() {
        double bps = (Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * mc.timer.timerSpeed) * 20;
        return Math.round(bps * 100.0) / 100.0;
    }


    public static Pair<Color, Color> getClientColors() {
        return Theme.getThemeColors(theme.getMode());
    }

    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("h:mm a").format(new Date());
    }

    public static String get(String text) {
        return hudCustomization.getSetting("Lowercase").isEnabled() ? text.toLowerCase() : text;
    }

    private void drawArmor(ScaledResolution sr) {
        if (hudCustomization.getSetting("Armor HUD").isEnabled()) {
            List<ItemStack> equipment = new ArrayList<>();
            boolean inWater = mc.thePlayer.isEntityAlive() && mc.thePlayer.isInsideOfMaterial(Material.water);
            int x = -94;

            ItemStack armorPiece;
            for (int i = 3; i >= 0; i--) {
                if ((armorPiece = mc.thePlayer.inventory.armorInventory[i]) != null) {
                    equipment.add(armorPiece);
                }
            }
            Collections.reverse(equipment);

            for (ItemStack itemStack : equipment) {
                armorPiece = itemStack;
                RenderHelper.enableGUIStandardItemLighting();
                x += 15;
                GlStateManager.pushMatrix();
                GlStateManager.disableAlpha();
                GlStateManager.clear(256);
                mc.getRenderItem().zLevel = -150.0F;
                int s = mc.thePlayer.capabilities.isCreativeMode ? 15 : 0;
                mc.getRenderItem().renderItemAndEffectIntoGUI(armorPiece, -x + sr.getScaledWidth() / 2 - 4,
                        (int) (sr.getScaledHeight() - (inWater ? 65 : 55) + s - (16 * GuiChat.openingAnimation.getOutput().floatValue())));
                mc.getRenderItem().zLevel = 0.0F;
                GlStateManager.disableBlend();
                GlStateManager.disableDepth();
                GlStateManager.disableLighting();
                GlStateManager.enableDepth();
                GlStateManager.enableAlpha();
                GlStateManager.popMatrix();
                armorPiece.getEnchantmentTagList();
            }
        }
    }

    public static boolean isRainbowTheme() {
        return theme.is("Custom Theme") && color1.isRainbow();
    }

    public static boolean drawRadialGradients() {
        return hudCustomization.getSetting("Radial Gradients").isEnabled();
    }

    public static void addButtons(List<GuiButton> buttonList) {
        for (ModuleButton mb : ModuleButton.values()) {
            if (mb.getSetting().isEnabled()) {
                buttonList.add(mb.getButton());
            }
        }
    }

    public static void updateButtonStatus() {
        for (ModuleButton mb : ModuleButton.values()) {
            mb.getButton().enabled = Tenacity.INSTANCE.getModuleCollection().getModule(mb.getModule()).isEnabled();
        }
    }

    public static void handleActionPerformed(GuiButton button) {
        for (ModuleButton mb : ModuleButton.values()) {
            if (mb.getButton() == button) {
                Module m = Tenacity.INSTANCE.getModuleCollection().getModule(mb.getModule());
                if (m.isEnabled()) {
                    m.toggle();
                }
                break;
            }
        }
    }

    @Getter
    @AllArgsConstructor
    public enum ModuleButton {
        AURA(KillAura.class, disableButtons.getSetting("Disable KillAura"), new GuiButton(2461, 3, 4, 120, 20, "Disable KillAura")),
        INVMANAGER(InvManager.class, disableButtons.getSetting("Disable InvManager"), new GuiButton(2462, 3, 26, 120, 20, "Disable InvManager")),
        CHESTSTEALER(ChestStealer.class, disableButtons.getSetting("Disable ChestStealer"), new GuiButton(2463, 3, 48, 120, 20, "Disable ChestStealer"));

        private final Class<? extends Module> module;
        private final BooleanSetting setting;
        private final GuiButton button;
    }

}

