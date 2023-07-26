package dev.hera.client.mods;

import dev.hera.client.events.EventBus;
import dev.hera.client.events.impl.EventKeyType;
import dev.hera.client.events.types.EventTarget;
import dev.hera.client.mods.impl.movement.Fly;
import dev.hera.client.mods.impl.render.ClickGui;
import dev.hera.client.mods.impl.render.HUD;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModManager {

    public List<Mod> mods = new ArrayList<>();

    public ModManager(){
        mods.add(new Fly());
        mods.add(new ClickGui());
        mods.add(new HUD());

        EventBus.register(this);
    }

    @EventTarget
    public void onKeyTyped(EventKeyType e){
        mods.stream().filter(m -> m.keyCode == e.keyCode).forEach(Mod::toggle);
    }


   public List<Mod> getModsBy(Category category){
        return mods.stream().filter(m -> m.category == category).collect(Collectors.toList());
   }
}