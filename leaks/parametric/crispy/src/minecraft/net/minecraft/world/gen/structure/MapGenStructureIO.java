package net.minecraft.world.gen.structure;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapGenStructureIO
{
    private static final Logger logger = LogManager.getLogger();
    private static Map startNameToClassMap = Maps.newHashMap();
    private static Map startClassToNameMap = Maps.newHashMap();
    private static Map componentNameToClassMap = Maps.newHashMap();
    private static Map componentClassToNameMap = Maps.newHashMap();

    private static void registerStructure(Class startClass, String structureName)
    {
        startNameToClassMap.put(structureName, startClass);
        startClassToNameMap.put(startClass, structureName);
    }

    static void registerStructureComponent(Class componentClass, String componentName)
    {
        componentNameToClassMap.put(componentName, componentClass);
        componentClassToNameMap.put(componentClass, componentName);
    }

    public static String getStructureStartName(StructureStart start)
    {
        return (String)startClassToNameMap.get(start.getClass());
    }

    public static String getStructureComponentName(StructureComponent component)
    {
        return (String)componentClassToNameMap.get(component.getClass());
    }

    public static StructureStart getStructureStart(NBTTagCompound tagCompound, World worldIn)
    {
        StructureStart var2 = null;

        try
        {
            Class var3 = (Class)startNameToClassMap.get(tagCompound.getString("id"));

            if (var3 != null)
            {
                var2 = (StructureStart)var3.newInstance();
            }
        }
        catch (Exception var4)
        {
            logger.warn("Failed net.minecraft.Start with id " + tagCompound.getString("id"));
            var4.printStackTrace();
        }

        if (var2 != null)
        {
            var2.readStructureComponentsFromNBT(worldIn, tagCompound);
        }
        else
        {
            logger.warn("Skipping Structure with id " + tagCompound.getString("id"));
        }

        return var2;
    }

    public static StructureComponent getStructureComponent(NBTTagCompound tagCompound, World worldIn)
    {
        StructureComponent var2 = null;

        try
        {
            Class var3 = (Class)componentNameToClassMap.get(tagCompound.getString("id"));

            if (var3 != null)
            {
                var2 = (StructureComponent)var3.newInstance();
            }
        }
        catch (Exception var4)
        {
            logger.warn("Failed Piece with id " + tagCompound.getString("id"));
            var4.printStackTrace();
        }

        if (var2 != null)
        {
            var2.readStructureBaseNBT(worldIn, tagCompound);
        }
        else
        {
            logger.warn("Skipping Piece with id " + tagCompound.getString("id"));
        }

        return var2;
    }

    static
    {
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
