/*
 * Decompiled with CFR 0.150.
 */
package Reality.Realii.utils.entity.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import Reality.Realii.utils.entity.ICheck;

public final class WallCheck
implements ICheck {
    @Override
    public boolean validate(Entity entity) {
        return Minecraft.getMinecraft().thePlayer.canEntityBeSeen(entity);
    }
}

