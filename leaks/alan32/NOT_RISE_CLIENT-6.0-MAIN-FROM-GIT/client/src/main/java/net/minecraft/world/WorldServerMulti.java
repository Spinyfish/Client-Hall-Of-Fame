package net.minecraft.world;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.ISaveHandler;

public class WorldServerMulti extends WorldServer {
    private final WorldServer delegate;

    public WorldServerMulti(final MinecraftServer server, final ISaveHandler saveHandlerIn, final int dimensionId, final WorldServer delegate, final Profiler profilerIn) {
        super(server, saveHandlerIn, new DerivedWorldInfo(delegate.getWorldInfo()), dimensionId, profilerIn);
        this.delegate = delegate;
        delegate.getWorldBorder().addListener(new IBorderListener() {
            public void onSizeChanged(final WorldBorder border, final double newSize) {
                WorldServerMulti.this.getWorldBorder().setTransition(newSize);
            }

            public void onTransitionStarted(final WorldBorder border, final double oldSize, final double newSize, final long time) {
                WorldServerMulti.this.getWorldBorder().setTransition(oldSize, newSize, time);
            }

            public void onCenterChanged(final WorldBorder border, final double x, final double z) {
                WorldServerMulti.this.getWorldBorder().setCenter(x, z);
            }

            public void onWarningTimeChanged(final WorldBorder border, final int newTime) {
                WorldServerMulti.this.getWorldBorder().setWarningTime(newTime);
            }

            public void onWarningDistanceChanged(final WorldBorder border, final int newDistance) {
                WorldServerMulti.this.getWorldBorder().setWarningDistance(newDistance);
            }

            public void onDamageAmountChanged(final WorldBorder border, final double newAmount) {
                WorldServerMulti.this.getWorldBorder().setDamageAmount(newAmount);
            }

            public void onDamageBufferChanged(final WorldBorder border, final double newSize) {
                WorldServerMulti.this.getWorldBorder().setDamageBuffer(newSize);
            }
        });
    }

    /**
     * Saves the chunks to disk.
     */
    protected void saveLevel() throws MinecraftException {
    }

    public World init() {
        this.mapStorage = this.delegate.getMapStorage();
        this.worldScoreboard = this.delegate.getScoreboard();
        final String s = VillageCollection.fileNameForProvider(this.provider);
        final VillageCollection villagecollection = (VillageCollection) this.mapStorage.loadData(VillageCollection.class, s);

        if (villagecollection == null) {
            this.villageCollectionObj = new VillageCollection(this);
            this.mapStorage.setData(s, this.villageCollectionObj);
        } else {
            this.villageCollectionObj = villagecollection;
            this.villageCollectionObj.setWorldsForAll(this);
        }

        return this;
    }
}
