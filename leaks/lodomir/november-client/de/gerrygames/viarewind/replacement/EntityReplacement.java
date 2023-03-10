/*
 * Decompiled with CFR 0.152.
 */
package de.gerrygames.viarewind.replacement;

import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import java.util.List;

public interface EntityReplacement {
    public int getEntityId();

    public void setLocation(double var1, double var3, double var5);

    public void relMove(double var1, double var3, double var5);

    public void setYawPitch(float var1, float var2);

    public void setHeadYaw(float var1);

    public void spawn();

    public void despawn();

    public void updateMetadata(List<Metadata> var1);
}

