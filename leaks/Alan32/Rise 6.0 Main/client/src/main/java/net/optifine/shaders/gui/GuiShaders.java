package net.optifine.shaders.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.src.Config;
import net.optifine.Lang;
import net.optifine.gui.TooltipManager;
import net.optifine.gui.TooltipProviderEnumShaderOptions;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersTex;
import net.optifine.shaders.config.EnumShaderOption;
import org.lwjgl.Sys;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class GuiShaders extends GuiScreen {
    protected GuiScreen parentGui;
    protected String screenTitle = "Shaders";
    private final TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderEnumShaderOptions());
    private int updateTimer = -1;
    private GuiSlotShaders shaderList;
    private boolean saved = false;
    private static final float[] QUALITY_MULTIPLIERS = new float[]{0.5F, 0.70710677F, 1.0F, 1.4142135F, 2.0F};
    private static final String[] QUALITY_MULTIPLIER_NAMES = new String[]{"0.5x", "0.7x", "1x", "1.5x", "2x"};
    private static final float[] HAND_DEPTH_VALUES = new float[]{0.0625F, 0.125F, 0.25F};
    private static final String[] HAND_DEPTH_NAMES = new String[]{"0.5x", "1x", "2x"};
    public static final int EnumOS_UNKNOWN = 0;
    public static final int EnumOS_WINDOWS = 1;
    public static final int EnumOS_OSX = 2;
    public static final int EnumOS_SOLARIS = 3;
    public static final int EnumOS_LINUX = 4;

    public GuiShaders(final GuiScreen par1GuiScreen, final GameSettings par2GameSettings) {
        this.parentGui = par1GuiScreen;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        this.screenTitle = I18n.format("of.options.shadersTitle");

        if (Shaders.shadersConfig == null) {
            Shaders.loadConfig();
        }

        final int i = 120;
        final int j = 20;
        final int k = this.width - i - 10;
        final int l = 30;
        final int i1 = 20;
        final int j1 = this.width - i - 20;
        this.shaderList = new GuiSlotShaders(this, j1, this.height, l, this.height - 50, 16);
        this.shaderList.registerScrollButtons(7, 8);
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.ANTIALIASING, k, 0 * i1 + l, i, j));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.NORMAL_MAP, k, 1 * i1 + l, i, j));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.SPECULAR_MAP, k, 2 * i1 + l, i, j));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.RENDER_RES_MUL, k, 3 * i1 + l, i, j));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.SHADOW_RES_MUL, k, 4 * i1 + l, i, j));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.HAND_DEPTH_MUL, k, 5 * i1 + l, i, j));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.OLD_HAND_LIGHT, k, 6 * i1 + l, i, j));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.OLD_LIGHTING, k, 7 * i1 + l, i, j));
        final int k1 = Math.min(150, j1 / 2 - 10);
        final int l1 = j1 / 4 - k1 / 2;
        final int i2 = this.height - 25;
        this.buttonList.add(new GuiButton(201, l1, i2, k1 - 22 + 1, j, Lang.get("of.options.shaders.shadersFolder")));
        this.buttonList.add(new GuiButtonDownloadShaders(210, l1 + k1 - 22 - 1, i2));
        this.buttonList.add(new GuiButton(202, j1 / 4 * 3 - k1 / 2, this.height - 25, k1, j, I18n.format("gui.done")));
        this.buttonList.add(new GuiButton(203, k, this.height - 25, i, j, Lang.get("of.options.shaders.shaderOptions")));
        this.updateButtons();
    }

    public void updateButtons() {
        final boolean flag = Config.isShaders();

        for (final GuiButton guibutton : this.buttonList) {
            if (guibutton.id != 201 && guibutton.id != 202 && guibutton.id != 210 && guibutton.id != EnumShaderOption.ANTIALIASING.ordinal()) {
                guibutton.enabled = flag;
            }
        }
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.shaderList.handleMouseInput();
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(final GuiButton button) {
        if (button.enabled) {
            if (button instanceof GuiButtonEnumShaderOption) {
                final GuiButtonEnumShaderOption guibuttonenumshaderoption = (GuiButtonEnumShaderOption) button;

                switch (guibuttonenumshaderoption.getEnumShaderOption()) {
                    case ANTIALIASING:
                        Shaders.nextAntialiasingLevel();
                        Shaders.uninit();
                        break;

                    case NORMAL_MAP:
                        Shaders.configNormalMap = !Shaders.configNormalMap;
                        Shaders.uninit();
                        this.mc.scheduleResourcesRefresh();
                        break;

                    case SPECULAR_MAP:
                        Shaders.configSpecularMap = !Shaders.configSpecularMap;
                        Shaders.uninit();
                        this.mc.scheduleResourcesRefresh();
                        break;

                    case RENDER_RES_MUL:
                        final float f2 = Shaders.configRenderResMul;
                        final float[] afloat2 = QUALITY_MULTIPLIERS;
                        final String[] astring2 = QUALITY_MULTIPLIER_NAMES;
                        int k = getValueIndex(f2, afloat2);

                        if (isShiftKeyDown()) {
                            --k;

                            if (k < 0) {
                                k = afloat2.length - 1;
                            }
                        } else {
                            ++k;

                            if (k >= afloat2.length) {
                                k = 0;
                            }
                        }

                        Shaders.configRenderResMul = afloat2[k];
                        Shaders.uninit();
                        Shaders.scheduleResize();
                        break;

                    case SHADOW_RES_MUL:
                        final float f1 = Shaders.configShadowResMul;
                        final float[] afloat1 = QUALITY_MULTIPLIERS;
                        final String[] astring1 = QUALITY_MULTIPLIER_NAMES;
                        int j = getValueIndex(f1, afloat1);

                        if (isShiftKeyDown()) {
                            --j;

                            if (j < 0) {
                                j = afloat1.length - 1;
                            }
                        } else {
                            ++j;

                            if (j >= afloat1.length) {
                                j = 0;
                            }
                        }

                        Shaders.configShadowResMul = afloat1[j];
                        Shaders.uninit();
                        Shaders.scheduleResizeShadow();
                        break;

                    case HAND_DEPTH_MUL:
                        final float f = Shaders.configHandDepthMul;
                        final float[] afloat = HAND_DEPTH_VALUES;
                        final String[] astring = HAND_DEPTH_NAMES;
                        int i = getValueIndex(f, afloat);

                        if (isShiftKeyDown()) {
                            --i;

                            if (i < 0) {
                                i = afloat.length - 1;
                            }
                        } else {
                            ++i;

                            if (i >= afloat.length) {
                                i = 0;
                            }
                        }

                        Shaders.configHandDepthMul = afloat[i];
                        Shaders.uninit();
                        break;

                    case OLD_HAND_LIGHT:
                        Shaders.configOldHandLight.nextValue();
                        Shaders.uninit();
                        break;

                    case OLD_LIGHTING:
                        Shaders.configOldLighting.nextValue();
                        Shaders.updateBlockLightLevel();
                        Shaders.uninit();
                        this.mc.scheduleResourcesRefresh();
                        break;

                    case TWEAK_BLOCK_DAMAGE:
                        Shaders.configTweakBlockDamage = !Shaders.configTweakBlockDamage;
                        break;

                    case CLOUD_SHADOW:
                        Shaders.configCloudShadow = !Shaders.configCloudShadow;
                        break;

                    case TEX_MIN_FIL_B:
                        Shaders.configTexMinFilB = (Shaders.configTexMinFilB + 1) % 3;
                        Shaders.configTexMinFilN = Shaders.configTexMinFilS = Shaders.configTexMinFilB;
                        button.displayString = "Tex Min: " + Shaders.texMinFilDesc[Shaders.configTexMinFilB];
                        ShadersTex.updateTextureMinMagFilter();
                        break;

                    case TEX_MAG_FIL_N:
                        Shaders.configTexMagFilN = (Shaders.configTexMagFilN + 1) % 2;
                        button.displayString = "Tex_n Mag: " + Shaders.texMagFilDesc[Shaders.configTexMagFilN];
                        ShadersTex.updateTextureMinMagFilter();
                        break;

                    case TEX_MAG_FIL_S:
                        Shaders.configTexMagFilS = (Shaders.configTexMagFilS + 1) % 2;
                        button.displayString = "Tex_s Mag: " + Shaders.texMagFilDesc[Shaders.configTexMagFilS];
                        ShadersTex.updateTextureMinMagFilter();
                        break;

                    case SHADOW_CLIP_FRUSTRUM:
                        Shaders.configShadowClipFrustrum = !Shaders.configShadowClipFrustrum;
                        button.displayString = "ShadowClipFrustrum: " + toStringOnOff(Shaders.configShadowClipFrustrum);
                        ShadersTex.updateTextureMinMagFilter();
                }

                guibuttonenumshaderoption.updateButtonText();
            } else {
                switch (button.id) {
                    case 201:
                        switch (getOSType()) {
                            case 1:
                                final String s = String.format("cmd.exe /C start \"Open file\" \"%s\"", Shaders.shaderPacksDir.getAbsolutePath());

                                try {
                                    Runtime.getRuntime().exec(s);
                                    return;
                                } catch (final IOException ioexception) {
                                    ioexception.printStackTrace();
                                    break;
                                }

                            case 2:
                                try {
                                    Runtime.getRuntime().exec(new String[]{"/usr/bin/open", Shaders.shaderPacksDir.getAbsolutePath()});
                                    return;
                                } catch (final IOException ioexception1) {
                                    ioexception1.printStackTrace();
                                }
                        }

                        boolean flag = false;

                        try {
                            final Class oclass1 = Class.forName("java.awt.Desktop");
                            final Object object1 = oclass1.getMethod("getDesktop", new Class[0]).invoke(null);
                            oclass1.getMethod("browse", new Class[]{URI.class}).invoke(object1, (new File(this.mc.mcDataDir, "shaderpacks")).toURI());
                        } catch (final Throwable throwable1) {
                            throwable1.printStackTrace();
                            flag = true;
                        }

                        if (flag) {
                            Config.dbg("Opening via system class!");
                            Sys.openURL("file://" + Shaders.shaderPacksDir.getAbsolutePath());
                        }

                        break;

                    case 202:
                        Shaders.storeConfig();
                        this.saved = true;
                        this.mc.displayGuiScreen(this.parentGui);
                        break;

                    case 203:
                        final GuiShaderOptions guishaderoptions = new GuiShaderOptions(this, Config.getGameSettings());
                        Config.getMinecraft().displayGuiScreen(guishaderoptions);
                        break;

                    case 210:
                        try {
                            final Class<?> oclass = Class.forName("java.awt.Desktop");
                            final Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null);
                            oclass.getMethod("browse", new Class[]{URI.class}).invoke(object, new URI("http://optifine.net/shaderPacks"));
                        } catch (final Throwable throwable) {
                            throwable.printStackTrace();
                        }

                    case 204:
                    case 205:
                    case 206:
                    case 207:
                    case 208:
                    case 209:
                    default:
                        this.shaderList.actionPerformed(button);
                }
            }
        }
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed() {
        super.onGuiClosed();

        if (!this.saved) {
            Shaders.storeConfig();
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        this.shaderList.drawScreen(mouseX, mouseY, partialTicks);

        if (this.updateTimer <= 0) {
            this.shaderList.updateList();
            this.updateTimer += 20;
        }

        this.drawCenteredString(this.fontRendererObj, this.screenTitle + " ", this.width / 2, 15, 16777215);
        final String s = "OpenGL: " + Shaders.glVersionString + ", " + Shaders.glVendorString + ", " + Shaders.glRendererString;
        final int i = this.fontRendererObj.width(s);

        if (i < this.width - 5) {
            this.drawCenteredString(this.fontRendererObj, s, this.width / 2, this.height - 40, 8421504);
        } else {
            this.drawString(this.fontRendererObj, s, 5, this.height - 40, 8421504);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
        this.tooltipManager.drawTooltips(mouseX, mouseY, this.buttonList);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {
        super.updateScreen();
        --this.updateTimer;
    }

    public Minecraft getMc() {
        return this.mc;
    }

    public void drawCenteredString(final String text, final int x, final int y, final int color) {
        this.drawCenteredString(this.fontRendererObj, text, x, y, color);
    }

    public static String toStringOnOff(final boolean value) {
        final String s = Lang.getOn();
        final String s1 = Lang.getOff();
        return value ? s : s1;
    }

    public static String toStringAa(final int value) {
        return value == 2 ? "FXAA 2x" : (value == 4 ? "FXAA 4x" : Lang.getOff());
    }

    public static String toStringValue(final float val, final float[] values, final String[] names) {
        final int i = getValueIndex(val, values);
        return names[i];
    }

    public static int getValueIndex(final float val, final float[] values) {
        for (int i = 0; i < values.length; ++i) {
            final float f = values[i];

            if (f >= val) {
                return i;
            }
        }

        return values.length - 1;
    }

    public static String toStringQuality(final float val) {
        return toStringValue(val, QUALITY_MULTIPLIERS, QUALITY_MULTIPLIER_NAMES);
    }

    public static String toStringHandDepth(final float val) {
        return toStringValue(val, HAND_DEPTH_VALUES, HAND_DEPTH_NAMES);
    }

    public static int getOSType() {
        final String s = System.getProperty("os.name").toLowerCase();
        return s.contains("win") ? 1 : (s.contains("mac") ? 2 : (s.contains("solaris") ? 3 : (s.contains("sunos") ? 3 : (s.contains("linux") ? 4 : (s.contains("unix") ? 4 : 0)))));
    }
}
