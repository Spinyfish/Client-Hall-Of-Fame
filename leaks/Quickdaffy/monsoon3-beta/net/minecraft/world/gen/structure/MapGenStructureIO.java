/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.gen.structure;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces;
import net.minecraft.world.gen.structure.MapGenNetherBridge;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureMineshaftPieces;
import net.minecraft.world.gen.structure.StructureMineshaftStart;
import net.minecraft.world.gen.structure.StructureNetherBridgePieces;
import net.minecraft.world.gen.structure.StructureOceanMonument;
import net.minecraft.world.gen.structure.StructureOceanMonumentPieces;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureStrongholdPieces;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapGenStructureIO {
    private static final Logger logger = LogManager.getLogger();
    private static Map<String, Class<? extends StructureStart>> startNameToClassMap = Maps.newHashMap();
    private static Map<Class<? extends StructureStart>, String> startClassToNameMap = Maps.newHashMap();
    private static Map<String, Class<? extends StructureComponent>> componentNameToClassMap = Maps.newHashMap();
    private static Map<Class<? extends StructureComponent>, String> componentClassToNameMap = Maps.newHashMap();

    private static void registerStructure(Class<? extends StructureStart> startClass, String structureName) {
        startNameToClassMap.put(structureName, startClass);
        startClassToNameMap.put(startClass, structureName);
    }

    static void registerStructureComponent(Class<? extends StructureComponent> componentClass, String componentName) {
        componentNameToClassMap.put(componentName, componentClass);
        componentClassToNameMap.put(componentClass, componentName);
    }

    public static String getStructureStartName(StructureStart start) {
        return startClassToNameMap.get(start.getClass());
    }

    public static String getStructureComponentName(StructureComponent component) {
        return componentClassToNameMap.get(component.getClass());
    }

    public static StructureStart getStructureStart(NBTTagCompound tagCompound, World worldIn) {
        StructureStart structurestart = null;
        try {
            Class<? extends StructureStart> oclass = startNameToClassMap.get(tagCompound.getString("id"));
            if (oclass != null) {
                structurestart = oclass.newInstance();
            }
        }
        catch (Exception exception) {
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

    public static StructureComponent getStructureComponent(NBTTagCompound tagCompound, World worldIn) {
        StructureComponent structurecomponent = null;
        try {
            Class<? extends StructureComponent> oclass = componentNameToClassMap.get(tagCompound.getString("id"));
            if (oclass != null) {
                structurecomponent = oclass.newInstance();
            }
        }
        catch (Exception exception) {
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
        MapGenStructureIO.registerStructure(StructureMineshaftStart.class, "Mineshaft");
        MapGenStructureIO.registerStructure(MapGenVillage.Start.class, "Village");
        MapGenStructureIO.registerStructure(MapGenNetherBridge.Start.class, "Fortress");
        MapGenStructureIO.registerStructure(MapGenStronghold.Start.class, "Stronghold");
        MapGenStructureIO.registerStructure(MapGenScatteredFeature.Start.class, "Temple");
        MapGenStructureIO.registerStructure(StructureOceanMonument.StartMonument.class, "Monument");
        StructureMineshaftPieces.registerStructurePieces();
        StructureVillagePieces.registerVillagePieces();
        StructureNetherBridgePieces.registerNetherFortressPieces();
        StructureStrongholdPieces.registerStrongholdPieces();
        ComponentScatteredFeaturePieces.registerScatteredFeaturePieces();
        StructureOceanMonumentPieces.registerOceanMonumentPieces();
    }
}

