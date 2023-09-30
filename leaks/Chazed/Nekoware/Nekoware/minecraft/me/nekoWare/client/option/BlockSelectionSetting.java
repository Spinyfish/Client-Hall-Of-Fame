package me.nekoWare.client.option;

import net.minecraft.block.Block;

import java.util.ArrayList;

import me.nekoWare.client.module.Module;

public class BlockSelectionSetting extends Option<ArrayList<Block>> {
    public BlockSelectionSetting(Module module, String parentModuleMode, String name, ArrayList<Block> blocks) {
        super(module, parentModuleMode, name, blocks);
    }

    public BlockSelectionSetting(Module module, String name, ArrayList<Block> blocks) {
        this(module, null, name, blocks);
    }
}
