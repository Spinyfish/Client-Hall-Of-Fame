package net.optifine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.entity.Entity;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.optifine.override.PlayerControllerOF;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class CustomGuis {
    private static final Minecraft mc = Config.getMinecraft();
    private static final PlayerControllerOF playerControllerOF = null;
    private static CustomGuiProperties[][] guiProperties = null;
    public static boolean isChristmas = isChristmas();

    public static ResourceLocation getTextureLocation(final ResourceLocation loc) {
        return loc;
    }

    private static ResourceLocation getTexturePos(final CustomGuiProperties.EnumContainer container, final BlockPos pos, final IBlockAccess blockAccess, final ResourceLocation loc, final GuiScreen screen) {
        final CustomGuiProperties[] acustomguiproperties = guiProperties[container.ordinal()];

        if (acustomguiproperties != null) {
            for (final CustomGuiProperties customguiproperties : acustomguiproperties) {
                if (customguiproperties.matchesPos(container, pos, blockAccess, screen)) {
                    return customguiproperties.getTextureLocation(loc);
                }
            }
        }

        return loc;
    }

    private static ResourceLocation getTextureEntity(final CustomGuiProperties.EnumContainer container, final Entity entity, final IBlockAccess blockAccess, final ResourceLocation loc) {
        final CustomGuiProperties[] acustomguiproperties = guiProperties[container.ordinal()];

        if (acustomguiproperties != null) {
            for (final CustomGuiProperties customguiproperties : acustomguiproperties) {
                if (customguiproperties.matchesEntity(container, entity, blockAccess)) {
                    return customguiproperties.getTextureLocation(loc);
                }
            }
        }

        return loc;
    }

    public static void update() {
        guiProperties = null;

        if (Config.isCustomGuis()) {
            final List<List<CustomGuiProperties>> list = new ArrayList<>();
            final IResourcePack[] airesourcepack = Config.getResourcePacks();

            for (int i = airesourcepack.length - 1; i >= 0; --i) {
                final IResourcePack iresourcepack = airesourcepack[i];
                update(iresourcepack, list);
            }

            guiProperties = propertyListToArray(list);
        }
    }

    private static CustomGuiProperties[][] propertyListToArray(final List<List<CustomGuiProperties>> listProps) {
        if (listProps.isEmpty()) {
            return null;
        } else {
            final CustomGuiProperties[][] acustomguiproperties = new CustomGuiProperties[CustomGuiProperties.EnumContainer.VALUES.length][];

            for (int i = 0; i < acustomguiproperties.length; ++i) {
                if (listProps.size() > i) {
                    final List<CustomGuiProperties> list = listProps.get(i);

                    if (list != null) {
                        final CustomGuiProperties[] acustomguiproperties1 = list.toArray(new CustomGuiProperties[0]);
                        acustomguiproperties[i] = acustomguiproperties1;
                    }
                }
            }

            return acustomguiproperties;
        }
    }

    private static void update(final IResourcePack rp, final List<List<CustomGuiProperties>> listProps) {
        final String[] astring = ResUtils.collectFiles(rp, "optifine/gui/container/", ".properties", null);
        Arrays.sort(astring);

        for (final String s : astring) {
            Config.dbg("CustomGuis: " + s);

            try {
                final ResourceLocation resourcelocation = new ResourceLocation(s);
                final InputStream inputstream = rp.getInputStream(resourcelocation);

                if (inputstream == null) {
                    Config.warn("CustomGuis file not found: " + s);
                } else {
                    final Properties properties = new PropertiesOrdered();
                    properties.load(inputstream);
                    inputstream.close();
                    final CustomGuiProperties customguiproperties = new CustomGuiProperties(properties, s);

                    if (customguiproperties.isValid(s)) {
                        addToList(customguiproperties, listProps);
                    }
                }
            } catch (final FileNotFoundException var9) {
                Config.warn("CustomGuis file not found: " + s);
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private static void addToList(final CustomGuiProperties cgp, final List<List<CustomGuiProperties>> listProps) {
        if (cgp.getContainer() == null) {
            warn("Invalid container: " + cgp.getContainer());
        } else {
            final int i = cgp.getContainer().ordinal();

            while (listProps.size() <= i) {
                listProps.add(null);
            }

            List<CustomGuiProperties> list = listProps.get(i);

            if (list == null) {
                list = new ArrayList();
                listProps.set(i, list);
            }

            list.add(cgp);
        }
    }

    public static PlayerControllerOF getPlayerControllerOF() {
        return playerControllerOF;
    }

    public static void setPlayerControllerOF(PlayerControllerOF playerControllerOF) {
        playerControllerOF = playerControllerOF;
    }

    private static boolean isChristmas() {
        final Calendar calendar = Calendar.getInstance();
        return calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26;
    }

    private static void warn(final String str) {
        Config.warn("[CustomGuis] " + str);
    }
}
