package me.kansio.client.targets;

import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TargetManager {

    @Getter
    private final ArrayList<String> target = new ArrayList<>();

    public boolean isTarget(EntityPlayer ent) {
        return target.contains(ent.getName());
    }

    public boolean isTarget(String ent) {
        return target.contains(ent);
    }

}
