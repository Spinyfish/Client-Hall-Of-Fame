package de.lirium.impl.command.impl;

import de.lirium.impl.command.CommandFeature;
import de.lirium.util.mojang.MojangUtil;
import net.minecraft.util.Tuple;

import java.io.IOException;
import java.util.Arrays;
import java.util.StringJoiner;

@CommandFeature.Info(name = "changeskin", alias = {"cskin", "skinchange", "cs"})
public class ChangeSkinFeature extends CommandFeature {
    @Override
    public boolean execute(String[] args) {
        if(args.length == 2) {
            new Thread(() -> {
                try {
                    String url = args[1];
                    if (Arrays.stream(MojangUtil.SkinVariant.values()).anyMatch(skinVariant -> skinVariant.name.equalsIgnoreCase(args[0]))) {
                        if (args[1].matches("[a-zA-Z0-9_]*"))
                            url = MojangUtil.getSkin(MojangUtil.getUUID(args[1]));
                        final Tuple<Integer, String> tuple = MojangUtil.changeSkin(MojangUtil.SkinVariant.valueOf(args[0].toUpperCase()), url, mc.session.getToken());
                        if (tuple.getFirst() == 200) {
                            sendMessage("§aSkin was changed!");
                            sendMessage("§aplease rejoin!");
                        } else {
                            sendMessage("§cCant set your skin!");
                            sendMessage("§cResponse Code: §e" + tuple.getFirst() + " §b" + tuple.getSecond());
                        }
                    } else {
                        final StringJoiner joiner = new StringJoiner("§7, §e");
                        for (MojangUtil.SkinVariant variant : MojangUtil.SkinVariant.values()) {
                            joiner.add(variant.name().toLowerCase());
                        }
                        sendMessage("§cWrong skin variant, available variants: §e" + joiner);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            return true;
        }
        return false;
    }
}
