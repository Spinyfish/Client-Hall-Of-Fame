package net.minecraft.world.gen.structure;

import com.google.common.collect.Maps;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class MapGenStructureIO {
    private static final Logger logger = LogManager.getLogger();
    private static final Map<String, Class<? extends StructureStart>> startNameToClassMap = Maps.newHashMap();
    private static final Map<Class<? extends StructureStart>, String> startClassToNameMap = Maps.newHashMap();
    private static final Map<String, Class<? extends StructureComponent>> componentNameToClassMap = Maps.newHashMap();
    private static final Map<Class<? extends StructureComponent>, String> componentClassToNameMap = Maps.newHashMap();

    private static void registerStructure(final Class<? extends StructureStart> startClass, final String structureName) {
        startNameToClassMap.put(structureName, startClass);
        startClassToNameMap.put(startClass, structureName);
    }

    static void registerStructureComponent(final Class<? extends StructureComponent> componentClass, final String componentName) {
        componentNameToClassMap.put(componentName, componentClass);
        componentClassToNameMap.put(componentClass, componentName);
    }

    public static String getStructureStartName(final StructureStart start) {
        return startClassToNameMap.get(start.getClass());
    }

    public static String getStructureComponentName(final StructureComponent component) {
        return componentClassToNameMap.get(component.getClass());
    }

    public static StructureStart getStructureStart(final NBTTagCompound tagCompound, final World worldIn) {
        StructureStart structurestart = null;

        try {
            final Class<? extends StructureStart> oclass = startNameToClassMap.get(tagCompound.getString("id"));

            if (oclass != null) {
                structurestart = oclass.newInstance();
            }
        } catch (final Exception exception) {
            logger.warn("Failed Start with id " + tagCompound.getString("id"));
            exception.printStackTrace();
        }

        if (structurestart != null) {
            structurestart.readStructureComponentsFromNBT(worldIn, tagCompound);
        } else {
            logger.warn("Skipping Structure with id " + tagCompound.getString("id"));
        }

        return structurestart;
    }

    public static StructureComponent getStructureComponent(final NBTTagCompound tagCompound, final World worldIn) {
        StructureComponent structurecomponent = null;

        try {
            final Class<? extends StructureComponent> oclass = componentNameToClassMap.get(tagCompound.getString("id"));

            if (oclass != null) {
                structurecomponent = oclass.newInstance();
            }
        } catch (final Exception exception) {
            logger.warn("Failed Piece with id " + tagCompound.getString("id"));
            exception.printStackTrace();
        }

        if (structurecomponent != null) {
            structurecomponent.readStructureBaseNBT(worldIn, tagCompound);
        } else {
            logger.warn("Skipping Piece with id " + tagCompound.getString("id"));
        }

        return structurecomponent;
    }

    static {
        registerStructure(StructureMineshaftStart.class, "Mineshaft");
        registerStructure(MapGenVillage.Start.class, "Village");
        registerStructure(MapGenNetherBridge.Start.class, "Fortress");
        registerStructure(MapGenStronghold.Start.class, "Stronghold");
        registerStructure(MapGenScatteredFeature.Start.class, "Temple");
        registerStructure(StructureOceanMonument.StartMonument.class, "Monument");
        StructureMineshaftPieces.registerStructurePieces();
        StructureVillagePieces.registerVillagePieces();
        StructureNetherBridgePieces.registerNetherFortressPieces();
        StructureStrongholdPieces.registerStrongholdPieces();
        ComponentScatteredFeaturePieces.registerScatteredFeaturePieces();
        StructureOceanMonumentPieces.registerOceanMonumentPieces();
    }
}
