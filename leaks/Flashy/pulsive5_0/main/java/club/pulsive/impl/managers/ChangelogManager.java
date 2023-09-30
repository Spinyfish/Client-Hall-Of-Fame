package club.pulsive.impl.managers;

import club.pulsive.api.font.FontRenderer;
import club.pulsive.api.font.Fonts;
import club.pulsive.impl.util.client.changelog.Changelog;
import club.pulsive.impl.util.client.changelog.ChangelogType;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChangelogManager {
    public CopyOnWriteArrayList<Changelog> changelogs = new CopyOnWriteArrayList<>();

    public ChangelogManager(){
        add(
            new Changelog(ChangelogType.ReDone, "testing my dad")
        );
    }

    public void add(Changelog... changelogs){
        Arrays.stream(changelogs).forEach(changelog -> {
            this.changelogs.add(changelog);
        });
    }

    public void render(){
       //flashy u can do something here, im too lazy to make it
    }

}
