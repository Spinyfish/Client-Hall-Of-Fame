package net.optifine.entity.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.entity.model.anim.ModelResolver;
import net.optifine.entity.model.anim.ModelUpdater;

import java.io.IOException;
import java.util.*;

public class CustomEntityModels {
    private static boolean active = false;
    private static Map<Class, Render> originalEntityRenderMap = null;
    private static Map<Class, TileEntitySpecialRenderer> originalTileEntityRenderMap = null;

    public static void update() {
        final Map<Class, Render> map = getEntityRenderMap();
        final Map<Class, TileEntitySpecialRenderer> map1 = getTileEntityRenderMap();

        if (map == null) {
            Config.warn("Entity render map not found, custom entity models are DISABLED.");
        } else if (map1 == null) {
            Config.warn("Tile entity render map not found, custom entity models are DISABLED.");
        } else {
            active = false;
            map.clear();
            map1.clear();
            map.putAll(originalEntityRenderMap);
            map1.putAll(originalTileEntityRenderMap);

            if (Config.isCustomEntityModels()) {
                final ResourceLocation[] aresourcelocation = getModelLocations();

                for (int i = 0; i < aresourcelocation.length; ++i) {
                    final ResourceLocation resourcelocation = aresourcelocation[i];
                    Config.dbg("CustomEntityModel: " + resourcelocation.getResourcePath());
                    final IEntityRenderer ientityrenderer = parseEntityRender(resourcelocation);

                    if (ientityrenderer != null) {
                        final Class oclass = ientityrenderer.getEntityClass();

                        if (oclass != null) {
                            if (ientityrenderer instanceof Render) {
                                map.put(oclass, (Render) ientityrenderer);
                            } else if (ientityrenderer instanceof TileEntitySpecialRenderer) {
                                map1.put(oclass, (TileEntitySpecialRenderer) ientityrenderer);
                            } else {
                                Config.warn("Unknown renderer type: " + ientityrenderer.getClass().getName());
                            }

                            active = true;
                        }
                    }
                }
            }
        }
    }

    private static Map<Class, Render> getEntityRenderMap() {
        final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        final Map<Class, Render> map = rendermanager.getEntityRenderMap();

        if (map == null) {
            return null;
        } else {
            if (originalEntityRenderMap == null) {
                originalEntityRenderMap = new HashMap<>(map);
            }

            return map;
        }
    }

    private static Map<Class, TileEntitySpecialRenderer> getTileEntityRenderMap() {
        final Map<Class, TileEntitySpecialRenderer> map = TileEntityRendererDispatcher.instance.mapSpecialRenderers;

        if (originalTileEntityRenderMap == null) {
            originalTileEntityRenderMap = new HashMap<>(map);
        }

        return map;
    }

    private static ResourceLocation[] getModelLocations() {
        final String s = "optifine/cem/";
        final String s1 = ".jem";
        final List<ResourceLocation> list = new ArrayList<>();
        final String[] astring = CustomModelRegistry.getModelNames();

        for (int i = 0; i < astring.length; ++i) {
            final String s2 = astring[i];
            final String s3 = s + s2 + s1;
            final ResourceLocation resourcelocation = new ResourceLocation(s3);

            if (Config.hasResource(resourcelocation)) {
                list.add(resourcelocation);
            }
        }

        final ResourceLocation[] aresourcelocation = list.toArray(new ResourceLocation[list.size()]);
        return aresourcelocation;
    }

    private static IEntityRenderer parseEntityRender(final ResourceLocation location) {
        try {
            final JsonObject jsonobject = CustomEntityModelParser.loadJson(location);
            final IEntityRenderer ientityrenderer = parseEntityRender(jsonobject, location.getResourcePath());
            return ientityrenderer;
        } catch (final IOException ioexception) {
            Config.error("" + ioexception.getClass().getName() + ": " + ioexception.getMessage());
            return null;
        } catch (final JsonParseException jsonparseexception) {
            Config.error("" + jsonparseexception.getClass().getName() + ": " + jsonparseexception.getMessage());
            return null;
        } catch (final Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private static IEntityRenderer parseEntityRender(final JsonObject obj, final String path) {
        final CustomEntityRenderer customentityrenderer = CustomEntityModelParser.parseEntityRender(obj, path);
        final String s = customentityrenderer.getName();
        final ModelAdapter modeladapter = CustomModelRegistry.getModelAdapter(s);
        checkNull(modeladapter, "Entity not found: " + s);
        final Class oclass = modeladapter.getEntityClass();
        checkNull(oclass, "Entity class not found: " + s);
        final IEntityRenderer ientityrenderer = makeEntityRender(modeladapter, customentityrenderer);

        if (ientityrenderer == null) {
            return null;
        } else {
            ientityrenderer.setEntityClass(oclass);
            return ientityrenderer;
        }
    }

    private static IEntityRenderer makeEntityRender(final ModelAdapter modelAdapter, final CustomEntityRenderer cer) {
        final ResourceLocation resourcelocation = cer.getTextureLocation();
        final CustomModelRenderer[] acustommodelrenderer = cer.getCustomModelRenderers();
        float f = cer.getShadowSize();

        if (f < 0.0F) {
            f = modelAdapter.getShadowSize();
        }

        final ModelBase modelbase = modelAdapter.makeModel();

        if (modelbase == null) {
            return null;
        } else {
            final ModelResolver modelresolver = new ModelResolver(modelAdapter, modelbase, acustommodelrenderer);

            if (!modifyModel(modelAdapter, modelbase, acustommodelrenderer, modelresolver)) {
                return null;
            } else {
                final IEntityRenderer ientityrenderer = modelAdapter.makeEntityRender(modelbase, f);

                if (ientityrenderer == null) {
                    throw new JsonParseException("Entity renderer is null, model: " + modelAdapter.getName() + ", adapter: " + modelAdapter.getClass().getName());
                } else {
                    if (resourcelocation != null) {
                        ientityrenderer.setLocationTextureCustom(resourcelocation);
                    }

                    return ientityrenderer;
                }
            }
        }
    }

    private static boolean modifyModel(final ModelAdapter modelAdapter, final ModelBase model, final CustomModelRenderer[] modelRenderers, final ModelResolver mr) {
        for (int i = 0; i < modelRenderers.length; ++i) {
            final CustomModelRenderer custommodelrenderer = modelRenderers[i];

            if (!modifyModel(modelAdapter, model, custommodelrenderer, mr)) {
                return false;
            }
        }

        return true;
    }

    private static boolean modifyModel(final ModelAdapter modelAdapter, final ModelBase model, final CustomModelRenderer customModelRenderer, final ModelResolver modelResolver) {
        final String s = customModelRenderer.getModelPart();
        final ModelRenderer modelrenderer = modelAdapter.getModelRenderer(model, s);

        if (modelrenderer == null) {
            Config.warn("Model part not found: " + s + ", model: " + model);
            return false;
        } else {
            if (!customModelRenderer.isAttach()) {
                if (modelrenderer.cubeList != null) {
                    modelrenderer.cubeList.clear();
                }

                if (modelrenderer.spriteList != null) {
                    modelrenderer.spriteList.clear();
                }

                if (modelrenderer.childModels != null) {
                    final ModelRenderer[] amodelrenderer = modelAdapter.getModelRenderers(model);
                    final Set<ModelRenderer> set = Collections.<ModelRenderer>newSetFromMap(new IdentityHashMap());
                    set.addAll(Arrays.asList(amodelrenderer));
                    final List<ModelRenderer> list = modelrenderer.childModels;
                    final Iterator iterator = list.iterator();

                    while (iterator.hasNext()) {
                        final ModelRenderer modelrenderer1 = (ModelRenderer) iterator.next();

                        if (!set.contains(modelrenderer1)) {
                            iterator.remove();
                        }
                    }
                }
            }

            modelrenderer.addChild(customModelRenderer.getModelRenderer());
            final ModelUpdater modelupdater = customModelRenderer.getModelUpdater();

            if (modelupdater != null) {
                modelResolver.setThisModelRenderer(customModelRenderer.getModelRenderer());
                modelResolver.setPartModelRenderer(modelrenderer);

                if (!modelupdater.initialize(modelResolver)) {
                    return false;
                }

                customModelRenderer.getModelRenderer().setModelUpdater(modelupdater);
            }

            return true;
        }
    }

    private static void checkNull(final Object obj, final String msg) {
        if (obj == null) {
            throw new JsonParseException(msg);
        }
    }

    public static boolean isActive() {
        return active;
    }
}
