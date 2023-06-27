package rip.athena.client.gui.clickgui.pages;

import net.minecraft.entity.*;
import net.minecraft.util.*;
import rip.athena.client.gui.clickgui.components.cosmetics.*;
import rip.athena.client.gui.clickgui.pages.fps.*;
import net.minecraft.client.*;
import rip.athena.client.gui.clickgui.*;
import rip.athena.client.*;
import rip.athena.client.modules.impl.fpssettings.*;
import rip.athena.client.modules.*;
import rip.athena.client.modules.impl.other.*;
import rip.athena.client.gui.clickgui.components.fps.*;
import rip.athena.client.gui.clickgui.components.macros.*;
import java.util.*;
import rip.athena.client.config.*;
import java.awt.*;
import rip.athena.client.config.types.*;
import rip.athena.client.gui.framework.*;
import rip.athena.client.gui.clickgui.components.mods.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.monster.*;
import net.minecraft.tileentity.*;
import net.minecraft.block.*;
import net.minecraft.client.entity.*;

public class SettingsPage extends Page
{
    private static ResourceLocation OF_LOW;
    private static ResourceLocation OF_MEDMED;
    private static ResourceLocation OF_HIGH;
    public static List<Class<? extends Entity>> ENTITIES;
    public static List<Class<? extends TileEntity>> TILE_ENTITIES;
    public static List<Class<? extends Block>> BLOCKS;
    public static List<EnumParticleTypes> PARTICLES;
    private static List<BlacklistModule> blacklistModules;
    private CosmeticGenericButton entities;
    private CosmeticGenericButton tileEntities;
    private CosmeticGenericButton blocks;
    private CosmeticGenericButton particles;
    private ModScrollPane blacklistPane;
    private BlacklistType currentType;
    private MainWindowBackgroundPS menuBackground;
    private MainWindowBackgroundPS fpsBackground;
    private ModScrollPane genericPane;
    private CosmeticGenericButton fpsButton;
    private CosmeticGenericButton optifineButton;
    private CosmeticGenericButton settingsButton;
    private FPSSubTab subTab;
    private boolean cleared;
    
    public SettingsPage(final Minecraft mc, final Menu menu, final IngameMenu parent) {
        super(mc, menu, parent);
    }
    
    @Override
    public void onInit() {
        SettingsPage.OF_LOW = new ResourceLocation("Athena/gui/menu/greenfps.png");
        SettingsPage.OF_MEDMED = new ResourceLocation("Athena/gui/menu/bluefps.png");
        SettingsPage.OF_HIGH = new ResourceLocation("Athena/gui/menu/redfps.png");
        final int x = 15;
        final int y = 109;
        final int typeWidth = 125;
        final int typeHeight = 20;
        final int width = 300;
        final int compWidth = width - 6 - 40;
        final int bWidth = 207;
        final int bSpace = 10;
        this.fpsButton = new FPSGenericButton("FPS", 270, y - 18, bWidth, 30, true) {
            @Override
            public void onAction() {
                this.setActive(false);
                SettingsPage.this.subTab = FPSSubTab.FPS;
                SettingsPage.this.populateMainBoard();
            }
        };
        this.optifineButton = new FPSGenericButton("OPTIFINE", 285 + bWidth + bSpace, y - 18, bWidth, 30, true) {
            @Override
            public void onAction() {
                this.setActive(false);
                SettingsPage.this.subTab = FPSSubTab.OPTIFINE;
                SettingsPage.this.populateMainBoard();
            }
        };
        this.settingsButton = new FPSGenericButton("SETTINGS", 305 + bWidth * 2 + bSpace * 2, y - 18, bWidth, 30, true) {
            @Override
            public void onAction() {
                this.setActive(false);
                SettingsPage.this.subTab = FPSSubTab.SETTINGS;
                SettingsPage.this.populateMainBoard();
            }
        };
        this.genericPane = new ModScrollPane(263, y + 30, this.menu.getWidth() - 285 - 40, this.menu.getHeight() - y - 30 - 30, true);
        this.fpsButton.onAction();
    }
    
    @Override
    public void onRender() {
        final int width = 300;
        final int x = this.menu.getX() + this.menu.getWidth() - width + 20;
        final int y = this.menu.getY() + 59;
        final int height = 32;
        this.drawVerticalLine(this.menu.getX() + 215, y + height - 30, height + 432, 3, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor());
    }
    
    @Override
    public void onLoad() {
        this.cleared = false;
        for (final BlacklistModule module : SettingsPage.blacklistModules) {
            boolean found = false;
            if (module.getType() == BlacklistType.BLOCK) {
                for (final Class<? extends Block> block : SettingsPage.BLOCKS) {
                    if (module.getClazz() == block) {
                        found = true;
                        break;
                    }
                }
            }
            else if (module.getType() == BlacklistType.ENTITY) {
                for (final Class<? extends Entity> entity : SettingsPage.ENTITIES) {
                    if (module.getClazz() == entity) {
                        found = true;
                        break;
                    }
                }
            }
            else if (module.getType() == BlacklistType.TILE_ENTITY) {
                for (final Class<? extends TileEntity> tileEntity : SettingsPage.TILE_ENTITIES) {
                    if (module.getClazz() == tileEntity) {
                        found = true;
                        break;
                    }
                }
            }
            else if (module.getType() == BlacklistType.PARTICLE) {
                for (final EnumParticleTypes particle : SettingsPage.PARTICLES) {
                    if (particle.toString().equals(module.getParticle().toString())) {
                        found = true;
                        break;
                    }
                }
            }
            module.setEnabled(found);
        }
        this.menu.addComponent(this.genericPane);
        this.menu.addComponent(this.fpsButton);
        this.menu.addComponent(this.optifineButton);
        this.menu.addComponent(this.settingsButton);
    }
    
    @Override
    public void onUnload() {
        if (!this.cleared) {
            this.cleared = true;
        }
    }
    
    @Override
    public void onOpen() {
        this.cleared = false;
    }
    
    @Override
    public void onClose() {
        if (!this.cleared) {
            this.cleared = true;
        }
    }
    
    private void populateMainBoard() {
        this.genericPane.getComponents().clear();
        this.fpsButton.setActive(false);
        this.optifineButton.setActive(false);
        this.settingsButton.setActive(false);
        switch (this.subTab) {
            case FPS: {
                this.fpsButton.setActive(true);
                break;
            }
            case OPTIFINE: {
                this.optifineButton.setActive(true);
                break;
            }
            case SETTINGS: {
                this.settingsButton.setActive(true);
                break;
            }
        }
        if (this.subTab == FPSSubTab.FPS) {
            this.initSettings(Athena.INSTANCE.getModuleManager().get(OptimizerMod.class));
        }
        else if (this.subTab == FPSSubTab.OPTIFINE) {
            this.genericPane.addComponent(new OptifineParentBackground(SettingsPage.OF_HIGH, "BEST FPS", "LOW QUALITY", 15, 16, 200, 280));
            this.genericPane.addComponent(new OptifineParentBackground(SettingsPage.OF_MEDMED, "MEDIUM FPS", "MEDIUM QUALITY", 240, 16, 200, 280));
            this.genericPane.addComponent(new OptifineParentBackground(SettingsPage.OF_LOW, "LOW FPS", "HIGH QUALITY", 480, 16, 200, 280));
            this.genericPane.addComponent(new CosmeticGenericButton("APPLY", 25, 257, 180, 30, true) {
                @Override
                public void onAction() {
                    this.setActive(false);
                    SettingsPage.this.loadLowSettings();
                }
            });
            this.genericPane.addComponent(new CosmeticGenericButton("APPLY", 250, 257, 180, 30, true) {
                @Override
                public void onAction() {
                    this.setActive(false);
                    SettingsPage.this.loadMedmedSettings();
                }
            });
            this.genericPane.addComponent(new CosmeticGenericButton("APPLY", 490, 257, 180, 30, true) {
                @Override
                public void onAction() {
                    this.setActive(false);
                    SettingsPage.this.loadHighSettings();
                }
            });
        }
        else if (this.subTab == FPSSubTab.SETTINGS) {
            this.initSettings(Athena.INSTANCE.getModuleManager().get(Settings.class));
        }
    }
    
    private void populateScrollPane() {
        this.blacklistPane.getComponents().clear();
        this.entities.setActive(false);
        this.tileEntities.setActive(false);
        this.blocks.setActive(false);
        this.particles.setActive(false);
        switch (this.currentType) {
            case BLOCK: {
                this.blocks.setActive(true);
                break;
            }
            case ENTITY: {
                this.entities.setActive(true);
                break;
            }
            case PARTICLE: {
                this.particles.setActive(true);
                break;
            }
            case TILE_ENTITY: {
                this.tileEntities.setActive(true);
                break;
            }
        }
        final int spacing = 10;
        final int x;
        final int originalX = x = 0;
        int y = 10;
        final int width = this.blacklistPane.getWidth() - 15;
        final int height = 30;
        for (final BlacklistModule module : SettingsPage.blacklistModules) {
            if (module.getType() != this.currentType) {
                continue;
            }
            this.blacklistPane.addComponent(new FlipButtonParent(module.getName(), x, y + height - 25, width - 10, height));
            final FlipButton flip = new FlipButtonFPS(module, x + width - 60, y + height - 18, 40, 15) {
                @Override
                public void onAction() {
                    module.setEnabled(this.isActive());
                }
            };
            flip.setActive(module.isEnabled());
            this.blacklistPane.addComponent(flip);
            y += height + spacing;
        }
    }
    
    private static void registerParticle(final String name, final EnumParticleTypes particle) {
        SettingsPage.blacklistModules.add(new BlacklistModule(name, particle));
    }
    
    private static void registerEntity(final String name, final Class<? extends Entity> entity) {
        if (name.contains(".") || name.contains("_")) {
            return;
        }
        SettingsPage.blacklistModules.add(new BlacklistModule(name, BlacklistType.ENTITY, entity));
    }
    
    private static void registerTileEntity(final String name, final Class<? extends TileEntity> tileEntity) {
        if (name.contains(".") || name.contains("_")) {
            return;
        }
        SettingsPage.blacklistModules.add(new BlacklistModule(name, BlacklistType.TILE_ENTITY, tileEntity));
    }
    
    private static void registerBlock(final String name, final Class<? extends Block> block) {
        if (name.contains(".") || name.contains("_")) {
            return;
        }
        SettingsPage.blacklistModules.add(new BlacklistModule(name, BlacklistType.BLOCK, block));
    }
    
    private void loadLowSettings() {
        Minecraft.getMinecraft().gameSettings.fancyGraphics = false;
        Minecraft.getMinecraft().gameSettings.renderDistanceChunks = 2;
        Minecraft.getMinecraft().gameSettings.ofFogType = 3;
        Minecraft.getMinecraft().gameSettings.ofFogStart = 0.2f;
        Minecraft.getMinecraft().gameSettings.mipmapLevels = 0;
        Minecraft.getMinecraft().gameSettings.ofOcclusionFancy = false;
        Minecraft.getMinecraft().gameSettings.ofSmoothFps = true;
        Minecraft.getMinecraft().gameSettings.ofSmoothWorld = true;
        Minecraft.getMinecraft().gameSettings.ofAoLevel = 0.0f;
        Minecraft.getMinecraft().gameSettings.ofClouds = 3;
        Minecraft.getMinecraft().gameSettings.ofCloudsHeight = 0.0f;
        Minecraft.getMinecraft().gameSettings.ofTrees = 1;
        Minecraft.getMinecraft().gameSettings.ofDroppedItems = 1;
        Minecraft.getMinecraft().gameSettings.ofRain = 3;
        Minecraft.getMinecraft().gameSettings.ofAnimatedWater = 2;
        Minecraft.getMinecraft().gameSettings.ofAnimatedLava = 2;
        Minecraft.getMinecraft().gameSettings.ofAnimatedFire = false;
        Minecraft.getMinecraft().gameSettings.ofAnimatedPortal = false;
        Minecraft.getMinecraft().gameSettings.ofAnimatedExplosion = false;
        Minecraft.getMinecraft().gameSettings.ofAnimatedFlame = false;
        Minecraft.getMinecraft().gameSettings.ofAnimatedRedstone = false;
        Minecraft.getMinecraft().gameSettings.ofAnimatedSmoke = false;
        Minecraft.getMinecraft().gameSettings.ofVoidParticles = false;
        Minecraft.getMinecraft().gameSettings.ofWaterParticles = false;
        Minecraft.getMinecraft().gameSettings.ofPortalParticles = false;
        Minecraft.getMinecraft().gameSettings.ofFireworkParticles = false;
        Minecraft.getMinecraft().gameSettings.ofDrippingWaterLava = false;
        Minecraft.getMinecraft().gameSettings.ofAnimatedTerrain = false;
        Minecraft.getMinecraft().gameSettings.ofAnimatedTextures = false;
        Minecraft.getMinecraft().gameSettings.ofRainSplash = false;
        Minecraft.getMinecraft().gameSettings.ofLagometer = false;
        Minecraft.getMinecraft().gameSettings.ofShowFps = false;
        Minecraft.getMinecraft().gameSettings.ofAutoSaveTicks = 30000;
        Minecraft.getMinecraft().gameSettings.ofBetterGrass = 3;
        Minecraft.getMinecraft().gameSettings.ofConnectedTextures = 3;
        Minecraft.getMinecraft().gameSettings.ofWeather = false;
        Minecraft.getMinecraft().gameSettings.ofSky = false;
        Minecraft.getMinecraft().gameSettings.ofStars = false;
        Minecraft.getMinecraft().gameSettings.ofSunMoon = false;
        Minecraft.getMinecraft().gameSettings.ofVignette = 1;
        Minecraft.getMinecraft().gameSettings.ofChunkUpdates = 1;
        Minecraft.getMinecraft().gameSettings.ofChunkUpdatesDynamic = false;
        Minecraft.getMinecraft().gameSettings.ofTime = 0;
        Minecraft.getMinecraft().gameSettings.ofClearWater = false;
        Minecraft.getMinecraft().gameSettings.ofAaLevel = 0;
        Minecraft.getMinecraft().gameSettings.ofAfLevel = 1;
        Minecraft.getMinecraft().gameSettings.ofProfiler = false;
        Minecraft.getMinecraft().gameSettings.ofBetterSnow = false;
        Minecraft.getMinecraft().gameSettings.ofSwampColors = false;
        Minecraft.getMinecraft().gameSettings.ofSmoothBiomes = false;
        Minecraft.getMinecraft().gameSettings.ofCustomFonts = false;
        Minecraft.getMinecraft().gameSettings.ofCustomColors = false;
        Minecraft.getMinecraft().gameSettings.ofCustomSky = false;
        Minecraft.getMinecraft().gameSettings.ofShowCapes = false;
        Minecraft.getMinecraft().gameSettings.ofNaturalTextures = false;
        Minecraft.getMinecraft().gameSettings.ofLazyChunkLoading = false;
        Minecraft.getMinecraft().gameSettings.ofDynamicFov = true;
        Minecraft.getMinecraft().gameSettings.ofDynamicLights = 3;
        Minecraft.getMinecraft().gameSettings.ofFastMath = true;
        Minecraft.getMinecraft().gameSettings.ofFastRender = true;
        Minecraft.getMinecraft().gameSettings.ofTranslucentBlocks = 1;
        Minecraft.getMinecraft().gameSettings.useVbo = true;
        Minecraft.getMinecraft().gameSettings.allowBlockAlternatives = false;
        Minecraft.getMinecraft().gameSettings.saveOptions();
        Minecraft.getMinecraft().gameSettings.loadOfOptions();
    }
    
    private void loadMedmedSettings() {
        Minecraft.getMinecraft().gameSettings.fancyGraphics = true;
        Minecraft.getMinecraft().gameSettings.renderDistanceChunks = 8;
        Minecraft.getMinecraft().gameSettings.ofFogType = 3;
        Minecraft.getMinecraft().gameSettings.ofFogStart = 0.2f;
        Minecraft.getMinecraft().gameSettings.mipmapLevels = 0;
        Minecraft.getMinecraft().gameSettings.ofOcclusionFancy = false;
        Minecraft.getMinecraft().gameSettings.ofSmoothFps = true;
        Minecraft.getMinecraft().gameSettings.ofSmoothWorld = true;
        Minecraft.getMinecraft().gameSettings.ofAoLevel = 0.0f;
        Minecraft.getMinecraft().gameSettings.ofClouds = 3;
        Minecraft.getMinecraft().gameSettings.ofCloudsHeight = 0.0f;
        Minecraft.getMinecraft().gameSettings.ofTrees = 1;
        Minecraft.getMinecraft().gameSettings.ofDroppedItems = 1;
        Minecraft.getMinecraft().gameSettings.ofRain = 3;
        Minecraft.getMinecraft().gameSettings.ofAnimatedWater = 2;
        Minecraft.getMinecraft().gameSettings.ofAnimatedLava = 2;
        Minecraft.getMinecraft().gameSettings.ofAnimatedFire = false;
        Minecraft.getMinecraft().gameSettings.ofAnimatedPortal = false;
        Minecraft.getMinecraft().gameSettings.ofAnimatedExplosion = true;
        Minecraft.getMinecraft().gameSettings.ofAnimatedFlame = false;
        Minecraft.getMinecraft().gameSettings.ofAnimatedRedstone = true;
        Minecraft.getMinecraft().gameSettings.ofAnimatedSmoke = true;
        Minecraft.getMinecraft().gameSettings.ofVoidParticles = false;
        Minecraft.getMinecraft().gameSettings.ofWaterParticles = false;
        Minecraft.getMinecraft().gameSettings.ofPortalParticles = false;
        Minecraft.getMinecraft().gameSettings.ofFireworkParticles = false;
        Minecraft.getMinecraft().gameSettings.ofDrippingWaterLava = false;
        Minecraft.getMinecraft().gameSettings.ofAnimatedTerrain = true;
        Minecraft.getMinecraft().gameSettings.ofAnimatedTextures = true;
        Minecraft.getMinecraft().gameSettings.ofRainSplash = false;
        Minecraft.getMinecraft().gameSettings.ofLagometer = false;
        Minecraft.getMinecraft().gameSettings.ofShowFps = false;
        Minecraft.getMinecraft().gameSettings.ofAutoSaveTicks = 30000;
        Minecraft.getMinecraft().gameSettings.ofBetterGrass = 3;
        Minecraft.getMinecraft().gameSettings.ofConnectedTextures = 3;
        Minecraft.getMinecraft().gameSettings.ofWeather = false;
        Minecraft.getMinecraft().gameSettings.ofSky = true;
        Minecraft.getMinecraft().gameSettings.ofStars = true;
        Minecraft.getMinecraft().gameSettings.ofSunMoon = true;
        Minecraft.getMinecraft().gameSettings.ofVignette = 1;
        Minecraft.getMinecraft().gameSettings.ofChunkUpdates = 1;
        Minecraft.getMinecraft().gameSettings.ofChunkUpdatesDynamic = false;
        Minecraft.getMinecraft().gameSettings.ofTime = 0;
        Minecraft.getMinecraft().gameSettings.ofClearWater = true;
        Minecraft.getMinecraft().gameSettings.ofAaLevel = 0;
        Minecraft.getMinecraft().gameSettings.ofAfLevel = 1;
        Minecraft.getMinecraft().gameSettings.ofProfiler = false;
        Minecraft.getMinecraft().gameSettings.ofBetterSnow = false;
        Minecraft.getMinecraft().gameSettings.ofSwampColors = false;
        Minecraft.getMinecraft().gameSettings.ofSmoothBiomes = true;
        Minecraft.getMinecraft().gameSettings.ofCustomFonts = false;
        Minecraft.getMinecraft().gameSettings.ofCustomColors = true;
        Minecraft.getMinecraft().gameSettings.ofCustomSky = false;
        Minecraft.getMinecraft().gameSettings.ofShowCapes = false;
        Minecraft.getMinecraft().gameSettings.ofNaturalTextures = false;
        Minecraft.getMinecraft().gameSettings.ofLazyChunkLoading = false;
        Minecraft.getMinecraft().gameSettings.ofDynamicFov = true;
        Minecraft.getMinecraft().gameSettings.ofDynamicLights = 3;
        Minecraft.getMinecraft().gameSettings.ofFastMath = true;
        Minecraft.getMinecraft().gameSettings.ofFastRender = true;
        Minecraft.getMinecraft().gameSettings.ofTranslucentBlocks = 1;
        Minecraft.getMinecraft().gameSettings.useVbo = true;
        Minecraft.getMinecraft().gameSettings.allowBlockAlternatives = false;
        Minecraft.getMinecraft().gameSettings.saveOptions();
        Minecraft.getMinecraft().gameSettings.loadOfOptions();
    }
    
    private void loadHighSettings() {
        Minecraft.getMinecraft().gameSettings.fancyGraphics = true;
        Minecraft.getMinecraft().gameSettings.renderDistanceChunks = 32;
        Minecraft.getMinecraft().gameSettings.ofFogType = 1;
        Minecraft.getMinecraft().gameSettings.ofFogStart = 0.6f;
        Minecraft.getMinecraft().gameSettings.mipmapLevels = 3;
        Minecraft.getMinecraft().gameSettings.ofOcclusionFancy = false;
        Minecraft.getMinecraft().gameSettings.ofSmoothFps = true;
        Minecraft.getMinecraft().gameSettings.ofSmoothWorld = true;
        Minecraft.getMinecraft().gameSettings.ofAoLevel = 1.0f;
        Minecraft.getMinecraft().gameSettings.ofClouds = 2;
        Minecraft.getMinecraft().gameSettings.ofCloudsHeight = 0.0f;
        Minecraft.getMinecraft().gameSettings.ofTrees = 2;
        Minecraft.getMinecraft().gameSettings.ofDroppedItems = 1;
        Minecraft.getMinecraft().gameSettings.ofRain = 2;
        Minecraft.getMinecraft().gameSettings.ofAnimatedWater = 0;
        Minecraft.getMinecraft().gameSettings.ofAnimatedLava = 0;
        Minecraft.getMinecraft().gameSettings.ofAnimatedFire = true;
        Minecraft.getMinecraft().gameSettings.ofAnimatedPortal = true;
        Minecraft.getMinecraft().gameSettings.ofAnimatedExplosion = true;
        Minecraft.getMinecraft().gameSettings.ofAnimatedFlame = true;
        Minecraft.getMinecraft().gameSettings.ofAnimatedRedstone = true;
        Minecraft.getMinecraft().gameSettings.ofAnimatedSmoke = true;
        Minecraft.getMinecraft().gameSettings.ofVoidParticles = true;
        Minecraft.getMinecraft().gameSettings.ofWaterParticles = true;
        Minecraft.getMinecraft().gameSettings.ofPortalParticles = true;
        Minecraft.getMinecraft().gameSettings.ofFireworkParticles = true;
        Minecraft.getMinecraft().gameSettings.ofDrippingWaterLava = true;
        Minecraft.getMinecraft().gameSettings.ofAnimatedTerrain = true;
        Minecraft.getMinecraft().gameSettings.ofAnimatedTextures = true;
        Minecraft.getMinecraft().gameSettings.ofRainSplash = true;
        Minecraft.getMinecraft().gameSettings.ofLagometer = false;
        Minecraft.getMinecraft().gameSettings.ofShowFps = false;
        Minecraft.getMinecraft().gameSettings.ofAutoSaveTicks = 30000;
        Minecraft.getMinecraft().gameSettings.ofBetterGrass = 2;
        Minecraft.getMinecraft().gameSettings.ofConnectedTextures = 2;
        Minecraft.getMinecraft().gameSettings.ofWeather = false;
        Minecraft.getMinecraft().gameSettings.ofSky = true;
        Minecraft.getMinecraft().gameSettings.ofStars = true;
        Minecraft.getMinecraft().gameSettings.ofSunMoon = true;
        Minecraft.getMinecraft().gameSettings.ofVignette = 2;
        Minecraft.getMinecraft().gameSettings.ofChunkUpdates = 5;
        Minecraft.getMinecraft().gameSettings.ofChunkUpdatesDynamic = true;
        Minecraft.getMinecraft().gameSettings.ofTime = 0;
        Minecraft.getMinecraft().gameSettings.ofClearWater = true;
        Minecraft.getMinecraft().gameSettings.ofAaLevel = 0;
        Minecraft.getMinecraft().gameSettings.ofAfLevel = 1;
        Minecraft.getMinecraft().gameSettings.ofProfiler = false;
        Minecraft.getMinecraft().gameSettings.ofBetterSnow = true;
        Minecraft.getMinecraft().gameSettings.ofSwampColors = true;
        Minecraft.getMinecraft().gameSettings.ofSmoothBiomes = true;
        Minecraft.getMinecraft().gameSettings.ofCustomFonts = true;
        Minecraft.getMinecraft().gameSettings.ofCustomColors = true;
        Minecraft.getMinecraft().gameSettings.ofCustomSky = true;
        Minecraft.getMinecraft().gameSettings.ofShowCapes = true;
        Minecraft.getMinecraft().gameSettings.ofNaturalTextures = true;
        Minecraft.getMinecraft().gameSettings.ofLazyChunkLoading = true;
        Minecraft.getMinecraft().gameSettings.ofDynamicFov = true;
        Minecraft.getMinecraft().gameSettings.ofDynamicLights = 2;
        Minecraft.getMinecraft().gameSettings.ofFastMath = true;
        Minecraft.getMinecraft().gameSettings.ofFastRender = true;
        Minecraft.getMinecraft().gameSettings.ofTranslucentBlocks = 2;
        Minecraft.getMinecraft().gameSettings.useVbo = true;
        Minecraft.getMinecraft().gameSettings.allowBlockAlternatives = false;
        Minecraft.getMinecraft().gameSettings.saveOptions();
        Minecraft.getMinecraft().gameSettings.loadOfOptions();
    }
    
    private void initSettings(final Module module) {
        final List<MenuComponent> toAdd = new ArrayList<MenuComponent>();
        final int xSpacing = 25;
        final int ySpacing = 15;
        final int sliderWidth = this.genericPane.getWidth() - xSpacing * 2;
        for (final ConfigEntry configEntry : module.getEntries()) {
            final String key = configEntry.getKey().toUpperCase();
            FeatureText label;
            if (configEntry.hasDescription()) {
                toAdd.add(label = new FeatureText(key, 0, 0));
            }
            else {
                toAdd.add(label = new FeatureText(key, configEntry.getDescription(), 0, 0));
            }
            if (configEntry instanceof BooleanEntry) {
                final BooleanEntry entry = (BooleanEntry)configEntry;
                final MenuModCheckbox checkbox = new MenuModCheckbox(0, 0, 15, 15) {
                    @Override
                    public void onAction() {
                        entry.setValue(module, this.isChecked());
                    }
                };
                checkbox.setChecked((boolean)entry.getValue(module));
                toAdd.add(checkbox);
            }
            else if (configEntry instanceof ColorEntry) {
                final ColorEntry entry2 = (ColorEntry)configEntry;
                toAdd.add(new MenuModColorPicker(0, 0, 15, 15, ((Color)entry2.getValue(module)).getRGB()) {
                    @Override
                    public void onAction() {
                        entry2.setValue(module, this.getColor());
                    }
                });
            }
            else if (configEntry instanceof DoubleEntry) {
                final DoubleEntry entry3 = (DoubleEntry)configEntry;
                final FeatureValueText valueText = new FeatureValueText("", 0, 0);
                toAdd.add(valueText);
                final MenuModSlider slider = new MenuModSlider((double)entry3.getValue(module), entry3.getMin(), entry3.getMax(), 2, 0, 0, sliderWidth, 15) {
                    @Override
                    public void onAction() {
                        label.setText((entry3.getKey() + " | ").toUpperCase());
                        entry3.setValue(module, (double)this.getValue());
                        valueText.setText(this.getValue() + "");
                    }
                };
                slider.onAction();
                toAdd.add(slider);
            }
            else if (configEntry instanceof FloatEntry) {
                final FloatEntry entry4 = (FloatEntry)configEntry;
                final FeatureValueText valueText = new FeatureValueText("", 0, 0);
                toAdd.add(valueText);
                final MenuModSlider slider = new MenuModSlider((float)entry4.getValue(module), entry4.getMin(), entry4.getMax(), 2, 0, 0, sliderWidth, 15) {
                    @Override
                    public void onAction() {
                        label.setText((entry4.getKey() + " | ").toUpperCase());
                        entry4.setValue(module, this.getValue());
                        valueText.setText(this.getValue() + "");
                    }
                };
                slider.onAction();
                toAdd.add(slider);
            }
            else if (configEntry instanceof IntEntry) {
                final IntEntry entry5 = (IntEntry)configEntry;
                final FeatureValueText valueText = new FeatureValueText("", 0, 0);
                toAdd.add(valueText);
                if (entry5.isKeyBind()) {
                    final MenuModKeybind bind = new MenuModKeybind(0, 0, 175, 15) {
                        @Override
                        public void onAction() {
                            entry5.setValue(module, this.getBind());
                        }
                    };
                    bind.setBind((int)entry5.getValue(module));
                    toAdd.add(bind);
                }
                else {
                    final MenuModSlider slider = new MenuModSlider((int)entry5.getValue(module), entry5.getMin(), entry5.getMax(), 0, 0, sliderWidth, 15) {
                        @Override
                        public void onAction() {
                            label.setText((entry5.getKey() + " | ").toUpperCase());
                            entry5.setValue(module, this.getIntValue());
                            valueText.setText(this.getIntValue() + "");
                        }
                    };
                    slider.onAction();
                    toAdd.add(slider);
                }
            }
            else if (configEntry instanceof ListEntry) {
                final ListEntry entry6 = (ListEntry)configEntry;
                final MenuModList list = new MenuModList(entry6.getValues(), 0, 0, 15) {
                    @Override
                    public void onAction() {
                        entry6.setValue(module, this.getValue());
                    }
                };
                list.setValue((String)configEntry.getValue(module));
                toAdd.add(list);
            }
            else {
                if (!(configEntry instanceof StringEntry)) {
                    continue;
                }
                final StringEntry entry7 = (StringEntry)configEntry;
                final ModTextbox box = new ModTextbox(TextPattern.NONE, 0, 0, 175, 15) {
                    @Override
                    public void onAction() {
                        entry7.setValue(module, this.getText());
                    }
                };
                box.setText((String)configEntry.getValue(module));
                toAdd.add(box);
            }
        }
        int xPos;
        final int defaultX = xPos = 2;
        int yPos = 25;
        boolean isText = false;
        MenuComponent last = null;
        for (final MenuComponent component : toAdd) {
            if (component instanceof FeatureValueText) {
                if (last != null) {
                    component.setX(xPos);
                    component.setY(yPos);
                }
            }
            else if (component instanceof FeatureText) {
                component.setX(xPos);
                component.setY(yPos);
                xPos += component.getWidth();
                isText = true;
            }
            else {
                xPos = defaultX;
                if (isText) {
                    if (component instanceof MenuModSlider) {
                        yPos += ySpacing;
                        component.setX(xPos);
                        component.setY(yPos);
                    }
                    else {
                        if (component instanceof MenuModList) {
                            component.setX(this.genericPane.getWidth() - component.getWidth() - xSpacing * 3 + 12);
                        }
                        else {
                            component.setX(this.genericPane.getWidth() - component.getWidth() - xSpacing);
                        }
                        component.setY(yPos);
                    }
                    isText = false;
                }
                else {
                    component.setX(xPos);
                    component.setY(yPos);
                }
                yPos += ySpacing + component.getHeight();
            }
            this.genericPane.addComponent(component);
            last = component;
        }
    }
    
    static {
        SettingsPage.ENTITIES = new ArrayList<Class<? extends Entity>>();
        SettingsPage.TILE_ENTITIES = new ArrayList<Class<? extends TileEntity>>();
        SettingsPage.BLOCKS = new ArrayList<Class<? extends Block>>();
        SettingsPage.PARTICLES = new ArrayList<EnumParticleTypes>();
        SettingsPage.blacklistModules = new ArrayList<BlacklistModule>();
        for (final EnumParticleTypes type : EnumParticleTypes.values()) {
            registerParticle(type.toString(), type);
        }
        registerEntity("Armor Stand", EntityArmorStand.class);
        registerEntity("Arrow", EntityArrow.class);
        registerEntity("FallingSand", EntityFallingBlock.class);
        registerEntity("Paintings", EntityPainting.class);
        registerEntity("Bat", EntityBat.class);
        registerEntity("Blaze", EntityBlaze.class);
        registerEntity("Boat", EntityBoat.class);
        registerEntity("CaveSpider", EntityCaveSpider.class);
        registerEntity("Chicken", EntityChicken.class);
        registerEntity("Cow", EntityCow.class);
        registerEntity("Creeper", EntityCreeper.class);
        registerEntity("Enderman", EntityEnderman.class);
        registerEntity("Horse", EntityHorse.class);
        registerEntity("Ghast", EntityGhast.class);
        registerEntity("Guardian", EntityGuardian.class);
        registerEntity("Ocelot", EntityOcelot.class);
        registerEntity("Pig", EntityPig.class);
        registerEntity("Zombie Pigmen", EntityPigZombie.class);
        registerEntity("Rabbit", EntityRabbit.class);
        registerEntity("Sheep", EntitySheep.class);
        registerEntity("Silverfish", EntitySilverfish.class);
        registerEntity("Skeleton", EntitySkeleton.class);
        registerEntity("Slime", EntitySlime.class);
        registerEntity("Spider", EntitySpider.class);
        registerEntity("Squid", EntitySquid.class);
        registerEntity("Villager", EntityVillager.class);
        registerEntity("Witch", EntityWitch.class);
        registerEntity("Wolf", EntityWolf.class);
        registerEntity("Zombie", EntityZombie.class);
        registerTileEntity("Banner", TileEntityBanner.class);
        registerTileEntity("Beacon", TileEntityBeacon.class);
        registerTileEntity("Chest", TileEntityChest.class);
        registerTileEntity("Comparitor", TileEntityComparator.class);
        registerTileEntity("Dropper", TileEntityDropper.class);
        registerTileEntity("Enchantment Table", TileEntityEnchantmentTable.class);
        registerTileEntity("Piston", TileEntityPiston.class);
        registerTileEntity("Sign", TileEntitySign.class);
        registerBlock("Anvil", BlockAnvil.class);
        registerBlock("Barrier", BlockBarrier.class);
        registerBlock("Beacon", BlockBeacon.class);
        registerBlock("Bed", BlockBed.class);
        registerBlock("Door", BlockDoor.class);
        registerBlock("Fence", BlockFence.class);
        registerBlock("Stairs", BlockStairs.class);
        registerBlock("Redstone Comparator", BlockRedstoneComparator.class);
        registerBlock("Redstone Diode", BlockRedstoneDiode.class);
        registerBlock("Redstone Light", BlockRedstoneLight.class);
        registerBlock("Redstone Repeater", BlockRedstoneRepeater.class);
        registerBlock("Redstone Torch", BlockRedstoneTorch.class);
        registerBlock("Redstone Wire", BlockRedstoneWire.class);
        registerBlock("Brewing Stand", BlockBrewingStand.class);
        registerBlock("Button", BlockButton.class);
        registerBlock("Cactus", BlockCactus.class);
        registerBlock("Carpet", BlockCarpet.class);
        registerBlock("Carrots", BlockCarrot.class);
        registerBlock("Cauldron", BlockCauldron.class);
        registerBlock("Chest", BlockChest.class);
        registerBlock("Cobweb", BlockWeb.class);
        registerBlock("Cocoa", BlockCocoa.class);
        registerBlock("Command Block", BlockCommandBlock.class);
        registerBlock("Crops", BlockCrops.class);
        registerBlock("Bush", BlockBush.class);
        registerBlock("Dispenser", BlockDispenser.class);
        registerBlock("Dropper", BlockDropper.class);
        registerBlock("Enchantment Table", BlockEnchantmentTable.class);
        registerBlock("End Portal", BlockEndPortal.class);
        registerBlock("Enderchest", BlockEnderChest.class);
        registerBlock("Fire", BlockFire.class);
        registerBlock("Flower", BlockFlower.class);
        registerBlock("Hopper", BlockHopper.class);
        registerBlock("Ladder", BlockLadder.class);
        registerBlock("Lava", BlockLiquid.class);
        registerBlock("Leaves", BlockLeaves.class);
        registerBlock("LilyPad", BlockLilyPad.class);
        registerBlock("Monster Spawner", BlockMobSpawner.class);
        registerBlock("Mushroom", BlockMushroom.class);
        registerBlock("Netherwart", BlockNetherWart.class);
        registerBlock("Piston Base", BlockPistonBase.class);
        registerBlock("Piston Extension", BlockPistonExtension.class);
        registerBlock("Piston Movement", BlockPistonMoving.class);
        registerBlock("Portal", BlockPortal.class);
        registerBlock("Rail", BlockRail.class);
        registerBlock("Sign", BlockSign.class);
        registerBlock("Snow", BlockSnow.class);
        registerBlock("Slab", BlockSlab.class);
        registerBlock("Trap Door", BlockTrapDoor.class);
        registerBlock("Tripwire", BlockTripWire.class);
        registerBlock("Tripwire Hook", BlockTripWireHook.class);
        registerEntity("Player (Local)", EntityPlayerSP.class);
        registerEntity("Player (Other)", EntityOtherPlayerMP.class);
    }
}
