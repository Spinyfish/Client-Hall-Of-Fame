package net.optifine;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.config.ConnectedParser;
import net.optifine.config.MatchBlock;
import net.optifine.config.Matches;
import net.optifine.util.TextureUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomColormap implements CustomColors.IColorizer {
    public String name = null;
    public String basePath = null;
    private int format = -1;
    private MatchBlock[] matchBlocks = null;
    private String source = null;
    private int color = -1;
    private int yVariance = 0;
    private int yOffset = 0;
    private int width = 0;
    private int height = 0;
    private int[] colors = null;
    private float[][] colorsRgb = null;
    private static final int FORMAT_UNKNOWN = -1;
    private static final int FORMAT_VANILLA = 0;
    private static final int FORMAT_GRID = 1;
    private static final int FORMAT_FIXED = 2;
    public static final String FORMAT_VANILLA_STRING = "vanilla";
    public static final String FORMAT_GRID_STRING = "grid";
    public static final String FORMAT_FIXED_STRING = "fixed";
    public static final String[] FORMAT_STRINGS = new String[]{"vanilla", "grid", "fixed"};
    public static final String KEY_FORMAT = "format";
    public static final String KEY_BLOCKS = "blocks";
    public static final String KEY_SOURCE = "source";
    public static final String KEY_COLOR = "color";
    public static final String KEY_Y_VARIANCE = "yVariance";
    public static final String KEY_Y_OFFSET = "yOffset";

    public CustomColormap(final Properties props, final String path, final int width, final int height, final String formatDefault) {
        final ConnectedParser connectedparser = new ConnectedParser("Colormap");
        this.name = connectedparser.parseName(path);
        this.basePath = connectedparser.parseBasePath(path);
        this.format = this.parseFormat(props.getProperty("format", formatDefault));
        this.matchBlocks = connectedparser.parseMatchBlocks(props.getProperty("blocks"));
        this.source = parseTexture(props.getProperty("source"), path, this.basePath);
        this.color = ConnectedParser.parseColor(props.getProperty("color"), -1);
        this.yVariance = connectedparser.parseInt(props.getProperty("yVariance"), 0);
        this.yOffset = connectedparser.parseInt(props.getProperty("yOffset"), 0);
        this.width = width;
        this.height = height;
    }

    private int parseFormat(String str) {
        if (str == null) {
            return 0;
        } else {
            str = str.trim();

            switch (str) {
                case "vanilla":
                    return 0;
                case "grid":
                    return 1;
                case "fixed":
                    return 2;
                default:
                    warn("Unknown format: " + str);
                    return -1;
            }
        }
    }

    public boolean isValid(final String path) {
        if (this.format != 0 && this.format != 1) {
            if (this.format != 2) {
                return false;
            }

            if (this.color < 0) {
                this.color = 16777215;
            }
        } else {
            if (this.source == null) {
                warn("Source not defined: " + path);
                return false;
            }

            this.readColors();

            if (this.colors == null) {
                return false;
            }

            if (this.color < 0) {
                if (this.format == 0) {
                    this.color = this.getColor(127, 127);
                }

                if (this.format == 1) {
                    this.color = this.getColorGrid(BiomeGenBase.plains, new BlockPos(0, 64, 0));
                }
            }
        }

        return true;
    }

    public boolean isValidMatchBlocks(final String path) {
        if (this.matchBlocks == null) {
            this.matchBlocks = this.detectMatchBlocks();

            if (this.matchBlocks == null) {
                warn("Match blocks not defined: " + path);
                return false;
            }
        }

        return true;
    }

    private MatchBlock[] detectMatchBlocks() {
        final Block block = Block.getBlockFromName(this.name);

        if (block != null) {
            return new MatchBlock[]{new MatchBlock(Block.getIdFromBlock(block))};
        } else {
            final Pattern pattern = Pattern.compile("^block([0-9]+).*$");
            final Matcher matcher = pattern.matcher(this.name);

            if (matcher.matches()) {
                final String s = matcher.group(1);
                final int i = Config.parseInt(s, -1);

                if (i >= 0) {
                    return new MatchBlock[]{new MatchBlock(i)};
                }
            }

            final ConnectedParser connectedparser = new ConnectedParser("Colormap");
            return connectedparser.parseMatchBlock(this.name);
        }
    }

    private void readColors() {
        try {
            this.colors = null;

            if (this.source == null) {
                return;
            }

            final String s = this.source + ".png";
            final ResourceLocation resourcelocation = new ResourceLocation(s);
            final InputStream inputstream = Config.getResourceStream(resourcelocation);

            if (inputstream == null) {
                return;
            }

            final BufferedImage bufferedimage = TextureUtil.readBufferedImage(inputstream);

            if (bufferedimage == null) {
                return;
            }

            final int i = bufferedimage.getWidth();
            final int j = bufferedimage.getHeight();
            final boolean flag = this.width < 0 || this.width == i;
            final boolean flag1 = this.height < 0 || this.height == j;

            if (!flag || !flag1) {
                dbg("Non-standard palette size: " + i + "x" + j + ", should be: " + this.width + "x" + this.height + ", path: " + s);
            }

            this.width = i;
            this.height = j;

            if (this.width <= 0 || this.height <= 0) {
                warn("Invalid palette size: " + i + "x" + j + ", path: " + s);
                return;
            }

            this.colors = new int[i * j];
            bufferedimage.getRGB(0, 0, i, j, this.colors, 0, i);
        } catch (final IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    private static void dbg(final String str) {
        Config.dbg("CustomColors: " + str);
    }

    private static void warn(final String str) {
        Config.warn("CustomColors: " + str);
    }

    private static String parseTexture(String texStr, final String path, final String basePath) {
        if (texStr != null) {
            texStr = texStr.trim();
            final String s1 = ".png";

            if (texStr.endsWith(s1)) {
                texStr = texStr.substring(0, texStr.length() - s1.length());
            }

            texStr = fixTextureName(texStr, basePath);
            return texStr;
        } else {
            String s = path;
            final int i = path.lastIndexOf(47);

            if (i >= 0) {
                s = path.substring(i + 1);
            }

            final int j = s.lastIndexOf(46);

            if (j >= 0) {
                s = s.substring(0, j);
            }

            s = fixTextureName(s, basePath);
            return s;
        }
    }

    private static String fixTextureName(String iconName, final String basePath) {
        iconName = TextureUtils.fixResourcePath(iconName, basePath);

        if (!iconName.startsWith(basePath) && !iconName.startsWith("textures/") && !iconName.startsWith("mcpatcher/")) {
            iconName = basePath + "/" + iconName;
        }

        if (iconName.endsWith(".png")) {
            iconName = iconName.substring(0, iconName.length() - 4);
        }

        final String s = "textures/blocks/";

        if (iconName.startsWith(s)) {
            iconName = iconName.substring(s.length());
        }

        if (iconName.startsWith("/")) {
            iconName = iconName.substring(1);
        }

        return iconName;
    }

    public boolean matchesBlock(final BlockStateBase blockState) {
        return Matches.block(blockState, this.matchBlocks);
    }

    public int getColorRandom() {
        if (this.format == 2) {
            return this.color;
        } else {
            final int i = CustomColors.random.nextInt(this.colors.length);
            return this.colors[i];
        }
    }

    public int getColor(int index) {
        index = Config.limit(index, 0, this.colors.length - 1);
        return this.colors[index] & 16777215;
    }

    public int getColor(int cx, int cy) {
        cx = Config.limit(cx, 0, this.width - 1);
        cy = Config.limit(cy, 0, this.height - 1);
        return this.colors[cy * this.width + cx] & 16777215;
    }

    public float[][] getColorsRgb() {
        if (this.colorsRgb == null) {
            this.colorsRgb = toRgb(this.colors);
        }

        return this.colorsRgb;
    }

    public int getColor(final IBlockState blockState, final IBlockAccess blockAccess, final BlockPos blockPos) {
        return this.getColor(blockAccess, blockPos);
    }

    public int getColor(final IBlockAccess blockAccess, final BlockPos blockPos) {
        final BiomeGenBase biomegenbase = CustomColors.getColorBiome(blockAccess, blockPos);
        return this.getColor(biomegenbase, blockPos);
    }

    public boolean isColorConstant() {
        return this.format == 2;
    }

    public int getColor(final BiomeGenBase biome, final BlockPos blockPos) {
        return this.format == 0 ? this.getColorVanilla(biome, blockPos) : (this.format == 1 ? this.getColorGrid(biome, blockPos) : this.color);
    }

    public int getColorSmooth(final IBlockAccess blockAccess, final double x, final double y, final double z, final int radius) {
        if (this.format == 2) {
            return this.color;
        } else {
            final int i = MathHelper.floor_double(x);
            final int j = MathHelper.floor_double(y);
            final int k = MathHelper.floor_double(z);
            int l = 0;
            int i1 = 0;
            int j1 = 0;
            int k1 = 0;
            final BlockPosM blockposm = new BlockPosM(0, 0, 0);

            for (int l1 = i - radius; l1 <= i + radius; ++l1) {
                for (int i2 = k - radius; i2 <= k + radius; ++i2) {
                    blockposm.setXyz(l1, j, i2);
                    final int j2 = this.getColor(blockAccess, blockposm);
                    l += j2 >> 16 & 255;
                    i1 += j2 >> 8 & 255;
                    j1 += j2 & 255;
                    ++k1;
                }
            }

            final int k2 = l / k1;
            final int l2 = i1 / k1;
            final int i3 = j1 / k1;
            return k2 << 16 | l2 << 8 | i3;
        }
    }

    private int getColorVanilla(final BiomeGenBase biome, final BlockPos blockPos) {
        final double d0 = MathHelper.clamp_float(biome.getFloatTemperature(blockPos), 0.0F, 1.0F);
        double d1 = MathHelper.clamp_float(biome.getFloatRainfall(), 0.0F, 1.0F);
        d1 = d1 * d0;
        final int i = (int) ((1.0D - d0) * (double) (this.width - 1));
        final int j = (int) ((1.0D - d1) * (double) (this.height - 1));
        return this.getColor(i, j);
    }

    private int getColorGrid(final BiomeGenBase biome, final BlockPos blockPos) {
        final int i = biome.biomeID;
        int j = blockPos.getY() - this.yOffset;

        if (this.yVariance > 0) {
            final int k = blockPos.getX() << 16 + blockPos.getZ();
            final int l = Config.intHash(k);
            final int i1 = this.yVariance * 2 + 1;
            final int j1 = (l & 255) % i1 - this.yVariance;
            j += j1;
        }

        return this.getColor(i, j);
    }

    public int getLength() {
        return this.format == 2 ? 1 : this.colors.length;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    private static float[][] toRgb(final int[] cols) {
        final float[][] afloat = new float[cols.length][3];

        for (int i = 0; i < cols.length; ++i) {
            final int j = cols[i];
            final float f = (float) (j >> 16 & 255) / 255.0F;
            final float f1 = (float) (j >> 8 & 255) / 255.0F;
            final float f2 = (float) (j & 255) / 255.0F;
            final float[] afloat1 = afloat[i];
            afloat1[0] = f;
            afloat1[1] = f1;
            afloat1[2] = f2;
        }

        return afloat;
    }

    public void addMatchBlock(final MatchBlock mb) {
        if (this.matchBlocks == null) {
            this.matchBlocks = new MatchBlock[0];
        }

        this.matchBlocks = (MatchBlock[]) Config.addObjectToArray(this.matchBlocks, mb);
    }

    public void addMatchBlock(final int blockId, final int metadata) {
        final MatchBlock matchblock = this.getMatchBlock(blockId);

        if (matchblock != null) {
            if (metadata >= 0) {
                matchblock.addMetadata(metadata);
            }
        } else {
            this.addMatchBlock(new MatchBlock(blockId, metadata));
        }
    }

    private MatchBlock getMatchBlock(final int blockId) {
        if (this.matchBlocks != null) {
            for (final MatchBlock matchblock : this.matchBlocks) {
                if (matchblock.getBlockId() == blockId) {
                    return matchblock;
                }
            }
        }
        return null;
    }

    public int[] getMatchBlockIds() {
        if (this.matchBlocks == null) {
            return null;
        } else {
            final Set set = new HashSet();

            for (final MatchBlock matchblock : this.matchBlocks) {
                if (matchblock.getBlockId() >= 0) {
                    set.add(matchblock.getBlockId());
                }
            }

            final Integer[] ainteger = (Integer[]) set.toArray(new Integer[set.size()]);
            final int[] aint = new int[ainteger.length];

            for (int j = 0; j < ainteger.length; ++j) {
                aint[j] = ainteger[j];
            }

            return aint;
        }
    }

    public String toString() {
        return "" + this.basePath + "/" + this.name + ", blocks: " + Config.arrayToString(this.matchBlocks) + ", source: " + this.source;
    }
}
