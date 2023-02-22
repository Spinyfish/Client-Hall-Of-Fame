package net.optifine.reflect;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReflectorForge {
    public static Object EVENT_RESULT_ALLOW = Reflector.getFieldValue(Reflector.Event_Result_ALLOW);
    public static Object EVENT_RESULT_DENY = Reflector.getFieldValue(Reflector.Event_Result_DENY);
    public static Object EVENT_RESULT_DEFAULT = Reflector.getFieldValue(Reflector.Event_Result_DEFAULT);

    public static void FMLClientHandler_trackBrokenTexture(final ResourceLocation loc, final String message) {
        if (!Reflector.FMLClientHandler_trackBrokenTexture.exists()) {
            final Object object = Reflector.call(Reflector.FMLClientHandler_instance);
            Reflector.call(object, Reflector.FMLClientHandler_trackBrokenTexture, loc, message);
        }
    }

    public static void FMLClientHandler_trackMissingTexture(final ResourceLocation loc) {
        if (!Reflector.FMLClientHandler_trackMissingTexture.exists()) {
            final Object object = Reflector.call(Reflector.FMLClientHandler_instance);
            Reflector.call(object, Reflector.FMLClientHandler_trackMissingTexture, loc);
        }
    }

    public static void putLaunchBlackboard(final String key, final Object value) {
        final Map map = (Map) Reflector.getFieldValue(Reflector.Launch_blackboard);

        if (map != null) {
            map.put(key, value);
        }
    }

    public static boolean renderFirstPersonHand(final RenderGlobal renderGlobal, final float partialTicks, final int pass) {
        return Reflector.ForgeHooksClient_renderFirstPersonHand.exists() && Reflector.callBoolean(Reflector.ForgeHooksClient_renderFirstPersonHand, renderGlobal, Float.valueOf(partialTicks), Integer.valueOf(pass));
    }

    public static InputStream getOptiFineResourceStream(String path) {
        if (!Reflector.OptiFineClassTransformer_instance.exists()) {
            return null;
        } else {
            final Object object = Reflector.getFieldValue(Reflector.OptiFineClassTransformer_instance);

            if (object == null) {
                return null;
            } else {
                if (path.startsWith("/")) {
                    path = path.substring(1);
                }

                final byte[] abyte = (byte[]) Reflector.call(object, Reflector.OptiFineClassTransformer_getOptiFineResource, new Object[]{path});

                if (abyte == null) {
                    return null;
                } else {
                    final InputStream inputstream = new ByteArrayInputStream(abyte);
                    return inputstream;
                }
            }
        }
    }

    public static boolean blockHasTileEntity(final IBlockState state) {
        final Block block = state.getBlock();
        return !Reflector.ForgeBlock_hasTileEntity.exists() ? block.hasTileEntity() : Reflector.callBoolean(block, Reflector.ForgeBlock_hasTileEntity, state);
    }

    public static boolean isItemDamaged(final ItemStack stack) {
        return !Reflector.ForgeItem_showDurabilityBar.exists() ? stack.isItemDamaged() : Reflector.callBoolean(stack.getItem(), Reflector.ForgeItem_showDurabilityBar, stack);
    }

    public static boolean armorHasOverlay(final ItemArmor itemArmor, final ItemStack itemStack) {
        final int i = itemArmor.getColor(itemStack);
        return i != -1;
    }

    public static MapData getMapData(final ItemMap itemMap, final ItemStack stack, final World world) {
        return Reflector.ForgeHooksClient.exists() ? ((ItemMap) stack.getItem()).getMapData(stack, world) : itemMap.getMapData(stack, world);
    }

    public static String[] getForgeModIds() {
        if (!Reflector.Loader.exists()) {
            return new String[0];
        } else {
            final Object object = Reflector.call(Reflector.Loader_instance);
            final List list = (List) Reflector.call(object, Reflector.Loader_getActiveModList, new Object[0]);

            if (list == null) {
                return new String[0];
            } else {
                final List<String> list1 = new ArrayList();

                for (final Object object1 : list) {
                    if (Reflector.ModContainer.isInstance(object1)) {
                        final String s = Reflector.callString(object1, Reflector.ModContainer_getModId);

                        if (s != null) {
                            list1.add(s);
                        }
                    }
                }

                final String[] astring = list1.toArray(new String[list1.size()]);
                return astring;
            }
        }
    }

    public static boolean canEntitySpawn(final EntityLiving entityliving, final World world, final float x, final float y, final float z) {
        final Object object = Reflector.call(Reflector.ForgeEventFactory_canEntitySpawn, entityliving, world, Float.valueOf(x), Float.valueOf(y), Float.valueOf(z));
        return object == EVENT_RESULT_ALLOW || object == EVENT_RESULT_DEFAULT && entityliving.getCanSpawnHere() && entityliving.isNotColliding();
    }

    public static boolean doSpecialSpawn(final EntityLiving entityliving, final World world, final float x, final int y, final float z) {
        return Reflector.ForgeEventFactory_doSpecialSpawn.exists() && Reflector.callBoolean(Reflector.ForgeEventFactory_doSpecialSpawn, entityliving, world, Float.valueOf(x), Integer.valueOf(y), Float.valueOf(z));
    }
}
