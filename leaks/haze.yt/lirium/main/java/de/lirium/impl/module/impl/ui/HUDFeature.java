package de.lirium.impl.module.impl.ui;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.Client;
import de.lirium.base.setting.Dependency;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.base.setting.impl.ComboBox;
import de.lirium.impl.events.Render2DEvent;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.playtime.PlayTimeUtil;
import de.lirium.util.render.ColorUtil;
import de.lirium.util.render.RenderUtil;
import me.felix.shader.access.ShaderAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@ModuleFeature.Info(name = "HUD", description = "Renders the client design", category = ModuleFeature.Category.UI)
public class HUDFeature extends ModuleFeature {

    @Value(name = "HUD Design")
    final ComboBox<String> mode = new ComboBox<>("Lirium", new String[]{"Clean", "Simple"});

    @Value(name = "Show Early Access")
    final CheckBox showEarlyAccess = new CheckBox(true, new Dependency<>(mode, "Lirium"));

    @Value(name = "Show playtime")
    final CheckBox showPlayTime = new CheckBox(true);

    @Value(name = "TM")
    final CheckBox tm = new CheckBox(false);

    @Value(name = "Sorting")
    final ComboBox<String> sorting = new ComboBox<>("Length", new String[]{"Alphabetic"});

    private final ResourceLocation lirium = new ResourceLocation("lirium/images/lirum.png"), lilaLirium = new ResourceLocation("lirium/images/lilalirium.png"),
            ea = new ResourceLocation("lirium/images/ea.png"), tails = new ResourceLocation("lirium/images/tails.png"), pakistan = new ResourceLocation("lirium/images/pakistan.png"),
            rofl = new ResourceLocation("lirium/images/rofl.png"), pashanim = new ResourceLocation("lirium/images/pashanim.png"), haram = new ResourceLocation("lirium/images/haram.png");

    final Color color = new Color(0, 0, 0, 200), color1 = new Color(255, 255, 255), color2 = new Color(0, 190, 0), color3 = new Color(0, 0, 0), color4 = new Color(0, 190, 0);

    public HUDFeature() {
        super();
        setEnabled(true);
    }

    private final CopyOnWriteArrayList<ModuleFeature> features = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<ModuleFeature> featuresCopy = new CopyOnWriteArrayList<>();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    public FontRenderer font = null, fontClean = null, fontClean2 = null;

    @EventHandler
    public final Listener<Render2DEvent> render2DEventListener = e -> {
        final float watermarkWidth = 100, watermarkHeight = 40;
        final float eaWidth = 100, eaHeight = 35;
        final ScaledResolution resolution = new ScaledResolution(mc);

        final net.minecraft.client.gui.FontRenderer fr = mc.fontRendererObj;

        if (font == null) {
            font = Client.INSTANCE.getFontLoader().get("Arial", 20);
            fontClean = Client.INSTANCE.customFontLoader.sfui10;
            fontClean2 = Client.INSTANCE.getFontLoader().get("Consolas", 16);
        }
        FontRenderer arrayListFont = font;
        switch (mode.getValue()) {
            case "Tails":
            case "Simple":
                arrayListFont = mc.fontRendererObj;
                break;
            case "Lirium":
                arrayListFont = font;
                break;
            case "Clean":
                arrayListFont = fontClean;
                break;
        }

        features.clear();
        featuresCopy.clear();
        FontRenderer finalArrayListFont = arrayListFont;
        final List<ModuleFeature> modules = Client.INSTANCE.getModuleManager().getFeatures().stream().sorted(getSorting(finalArrayListFont)).filter(ModuleFeature::isEnabled).collect(Collectors.toList());
        features.addAll(modules);
        final AtomicInteger index = new AtomicInteger(0);
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        featuresCopy.addAll(features);
        features.forEach(moduleFeature -> {
            final String display = moduleFeature.getDisplayName(tm.getValue(), mode.getValue().equalsIgnoreCase("Tails"));
            final int curIndex = index.get();
            int y;
            Color color;
            switch (mode.getValue()) {
                case "Simple":
                    final int fontHeight = mc.fontRendererObj.FONT_HEIGHT + 1;
                    final int spacing = 4, borderSize = 2;
                    y = curIndex * fontHeight + 1;
                    GuiScreen.drawRect(resolution.getScaledWidth() - 3 - mc.fontRendererObj.getStringWidth(display) - spacing, (int) (y - 1), resolution.getScaledWidth(), (int) y + mc.fontRendererObj.FONT_HEIGHT, Integer.MIN_VALUE);
                    GuiScreen.drawRect(resolution.getScaledWidth() - 3 - mc.fontRendererObj.getStringWidth(display) - spacing, y - 1, resolution.getScaledWidth() - 3 - mc.fontRendererObj.getStringWidth(display) - spacing - borderSize, y + mc.fontRendererObj.FONT_HEIGHT, Color.black.getRGB());
                    if (curIndex < features.size() - 1) {
                        final ModuleFeature feature = featuresCopy.get(curIndex + 1);
                        GuiScreen.drawRect(resolution.getScaledWidth() - 3 - mc.fontRendererObj.getStringWidth(display) - spacing - 2, y + mc.fontRendererObj.FONT_HEIGHT, resolution.getScaledWidth() - 3 - mc.fontRendererObj.getStringWidth(feature.getDisplayName(tm.getValue(), false)) - spacing - borderSize, y + mc.fontRendererObj.FONT_HEIGHT + borderSize, Color.black.getRGB());
                        GL11.glColor4f(1, 1, 1, 1);
                    } else {
                        GuiScreen.drawRect(resolution.getScaledWidth() - 3 - mc.fontRendererObj.getStringWidth(display) - spacing - 2, y + mc.fontRendererObj.FONT_HEIGHT, resolution.getScaledWidth(), y + mc.fontRendererObj.FONT_HEIGHT + borderSize, Color.black.getRGB());
                    }
                    GuiScreen.drawRect(resolution.getScaledWidth() - (spacing - 2), (int) (y - 1), resolution.getScaledWidth(), (int) y + mc.fontRendererObj.FONT_HEIGHT, new Color(moduleFeature.randomColor, false).getRGB());
                    mc.fontRendererObj.drawStringWithShadow(display, resolution.getScaledWidth() - mc.fontRendererObj.getStringWidth(display) - spacing, y, moduleFeature.randomColor);
                    index.getAndSet(curIndex + 1);
                    break;
                case "Tails":
                    y = curIndex * mc.fontRendererObj.FONT_HEIGHT;
                    mc.fontRendererObj.drawStringWithShadow(display, resolution.getScaledWidth() - mc.fontRendererObj.getStringWidth(display) - 2, y, ColorUtil.getRainbow(10000, 0, 1F).getRGB());
                    index.getAndSet(curIndex + 1);
                    break;
                case "Lirium":
                    y = curIndex * (font.FONT_HEIGHT + 1);
                    color = ColorUtil.getColor(2000, curIndex * 200);
                    GuiScreen.drawRect(0, y, font.getStringWidth(display) + 3, y + font.FONT_HEIGHT + 1, Integer.MIN_VALUE);
                    GuiScreen.drawRect(font.getStringWidth(display) + 3, y, font.getStringWidth(display) + 5, y + font.FONT_HEIGHT + 1, color.getRGB());
                    if (curIndex == features.size() - 1) {
                        GuiScreen.drawRect(0, y + font.FONT_HEIGHT + 1, font.getStringWidth(display) + 5, y + font.FONT_HEIGHT + 3, color.getRGB());
                    } else {
                        final ModuleFeature next = features.get(curIndex + 1);
                        GuiScreen.drawRect(font.getStringWidth(next.getDisplayName(tm.getValue(), false)) + 3, y + font.FONT_HEIGHT + 1, font.getStringWidth(display) + 5, y + font.FONT_HEIGHT + 3, color.getRGB());
                    }
                    font.drawStringWithShadow(display, 1, y, color.getRGB());
                    index.getAndSet(curIndex + 1);
                    break;
                case "Clean":
                    y = fontClean2.FONT_HEIGHT * 2 + curIndex * (fontClean.FONT_HEIGHT + 1);
                    GuiScreen.drawRect(0, (int) y, fontClean.getStringWidth(display) + 3, (int) y + fontClean.FONT_HEIGHT + 1, Integer.MIN_VALUE);

                    color = ColorUtil.getColor(1300, curIndex * 200, Color.WHITE, new Color(136, 36, 173));
                    ShaderAccess.blurShaderRunnables.add(() -> GuiScreen.drawRect(0, (int) y, fontClean.getStringWidth(display) + 3, (int) y + fontClean.FONT_HEIGHT + 1, Integer.MIN_VALUE));

                    ShaderAccess.bloomRunnables.add(() -> GuiScreen.drawRect(0, (int) y, fontClean.getStringWidth(display) + 3, (int) y + fontClean.FONT_HEIGHT + 1, color.getRGB()));

                    // JHLabsShaderRenderer.renderShadowOnObject(0, y, fontClean.getStringWidth(display) + 3, fontClean.FONT_HEIGHT + 1, 18, ColorUtil.getColor(2000, curIndex * 200), () -> GuiScreen.drawRect(0, (int) y, fontClean.getStringWidth(display) + 3, (int) y + fontClean.FONT_HEIGHT + 1, Integer.MIN_VALUE));

                    fontClean.drawString(display, 1, (int) y, -1);

                    index.getAndSet(curIndex + 1);
                    break;
                case "Allah":
                    y = curIndex * (fontClean.FONT_HEIGHT + 1);

                    final int roflIndex = atomicInteger.get();
                    color = ColorUtil.getColor(2000, roflIndex * 200, color1, color2);

                    GuiScreen.drawRect(resolution.getScaledWidth() - fontClean.getStringWidth(moduleFeature.getName()) - 6, (int) y, resolution.getScaledWidth(), (int) y + fontClean.FONT_HEIGHT + 1, color.getRGB());

                    fontClean.drawString(moduleFeature.getName(), resolution.getScaledWidth() - fontClean.getStringWidth(moduleFeature.getName()) - 3, (int) y + 2, color.getRGB());
                    index.getAndSet(curIndex + 1);

                    atomicInteger.getAndIncrement();
                    break;
            }
        });

        String playtime = showPlayTime.getValue() ? ": " + PlayTimeUtil.getPlayTime(System.currentTimeMillis()) : "";
        switch (mode.getValue()) {
            case "Simple":
                final String coords = "§7XYZ: §r" + (int) getX() + ", " + (int) getY() + ", " + (int) getZ();
                fr.drawStringWithShadow("§a" + Client.NAME + ": §f" + Minecraft.getDebugFPS(), 3, 3, -1);
                fr.drawStringWithShadow(coords, 3, 3 + fr.FONT_HEIGHT, -1);
                if (showPlayTime.getValue()) {
                    playtime = PlayTimeUtil.getPlayTime(System.currentTimeMillis());
                    fr.drawStringWithShadow("§7Playtime: §f" + playtime, 3, 3 + fr.FONT_HEIGHT * 2, -1);
                }
                final String date = dateFormat.format(new Date());
                fr.drawStringWithShadow("§7" + date, resolution.getScaledWidth() / 2F - fr.getStringWidth(date) / 2F, 2, -1);
                GL11.glColor4f(1, 1, 1, 1);
                break;
            case "Clean": {
                String finalPlaytime = playtime;
                ShaderAccess.bloomRunnables.add(() -> fontClean2.drawString(Client.NAME + " v" + Client.VERSION + finalPlaytime, 2, 5, Color.BLACK.getRGB()));

                fontClean2.drawString(Client.NAME + " v" + Client.VERSION + playtime, 2, 5, -1);
                break;
            }
            case "Tails": {
                GL11.glColor4f(1, 1, 1, 1);
                RenderUtil.drawPicture(tails, 2, 2, 120, 80);
                break;
            }
            case "Lirium": {
                GL11.glColor4f(1, 1, 1, 1);
                if (!mc.gameSettings.language.equalsIgnoreCase("zh_cn") && !mc.gameSettings.language.equalsIgnoreCase("zh_hk") && !mc.gameSettings.language.equalsIgnoreCase("zh_tw"))
                    RenderUtil.drawPicture(lirium, (int) (resolution.getScaledWidth() - watermarkWidth - 2), 2, watermarkWidth, watermarkHeight);
                else
                    RenderUtil.drawPicture(lilaLirium, (int) (resolution.getScaledWidth() - watermarkWidth - 2), 2, watermarkWidth, watermarkHeight);
                if (showEarlyAccess.getValue()) {
                    RenderUtil.drawPicture(ea, (int) (resolution.getScaledWidth() - eaWidth - 1), (int) (resolution.getScaledHeight() - eaHeight - (mc.currentScreen instanceof GuiChat ? 15 : 2)), eaWidth, eaHeight);
                }

                if (showPlayTime.getValue()) {
                    playtime = PlayTimeUtil.getPlayTime(System.currentTimeMillis());
                    final AtomicInteger bossY = new AtomicInteger();
                    if (!GuiBossOverlay.mapBossInfos.isEmpty()) {
                        if(bossY.get() >= resolution.getScaledHeight() / 3)
                            break;
                        bossY.set(10);
                        GuiBossOverlay.mapBossInfos.forEach((uuid, bossInfoClient) -> {
                            bossY.addAndGet(10 + mc.fontRendererObj.FONT_HEIGHT - 9);
                        });
                    }
                    font.drawStringWithShadow(playtime, resolution.getScaledWidth() / 2F - font.getStringWidth(playtime) / 2F, 2 + bossY.get(), -1);
                }
                break;
            }
            case "Allah": {
                RenderUtil.drawPicture(pakistan, 5, 5, 100, 100);
                RenderUtil.drawPicture(rofl, resolution.getScaledWidth() / 2 - 100 / 2, 20, 100, 100);
                RenderUtil.drawPicture(pashanim, resolution.getScaledWidth() - 100 - 5, resolution.getScaledHeight() - 100 - 5, 100, 100);

                final String s = "HARAM!!! ابن العاهرة";

                final Color col = ColorUtil.getColor(5000, 200, color3, color4);
                fr.drawStringWithShadow("قتلت عائلتي من أجل الدولة الإسلامية قتلت عائلتي من أجل الدولة الإسلامية قتلت عائلتي من أجل الدولة الإسلامية قتلت عائلتي من أجل الدولة الإسلامية قتلت عائلتي من أجل الدولة الإسلامية قتلت عائلتي من أجل الدولة الإسلامية قتلت عائلتي من أجل الدولة الإسلامية", 25, 5, col.getRGB());

                fr.drawStringWithShadow(s, 5, resolution.getScaledHeight() - 100 - 7.5F - fr.FONT_HEIGHT, -1);
                RenderUtil.drawPicture(haram, 5, resolution.getScaledHeight() - 100 - 5, 100, 100);

                playtime = PlayTimeUtil.getPlayTime(System.currentTimeMillis());
                font.drawStringWithShadow(playtime, resolution.getScaledWidth() / 2F - font.getStringWidth(playtime) / 2F, 120, col.getRGB());
            }
        }
    };

    private Comparator<ModuleFeature> getSorting(FontRenderer font) {
        switch (sorting.getValue()) {
            case "Alphabetic": {
                return Comparator.comparing(ModuleFeature::getName);
            }
            case "Length": {
                return Comparator.comparing(moduleFeature -> -font.getStringWidth(moduleFeature.getDisplayName(tm.getValue(), mode.getValue().equalsIgnoreCase("Tails"))));
            }
        }
        return Comparator.comparing(ModuleFeature::getName);
    }
}