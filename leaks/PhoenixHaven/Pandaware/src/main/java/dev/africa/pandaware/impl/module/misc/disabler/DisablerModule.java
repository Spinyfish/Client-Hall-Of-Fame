package dev.africa.pandaware.impl.module.misc.disabler;

import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.module.misc.disabler.modes.*;

@ModuleInfo(name = "Disabler", description = "Anal rape anticheats because why not", category = Category.MISC)
public class DisablerModule extends Module {
    public DisablerModule() {
        this.registerModes(
                new HypixelDisabler("Hypixel", this),
                new DEVDisabler("DEV", this),
                new C03ReplaceDisabler("C03 Replace", this),
                new S08Disabler("S08", this),
                new BlocksMCDisabler("Verus (old BlocksMC)", this),
                new HazelMCDisabler("HazelMC (old Verus)", this),
                new FuncraftDisabler("Funcraft Staff", this),
                new VerusCombatDisabler("Verus Combat", this),
                new CancelDmgPacketDisabler("Cancel Damage", this),
                new InvalidPosDisabler("Invalid Position", this),
                new PlaceDisabler("1.17+ Place", this),
                new SparkyDisabler("Sparky", this),
                new GhostlyDisabler("Ghostly", this),
                new PingSpoofDisabler("Ping Spoof", this)
        );
    }

    @Override
    public String getSuffix() {
        String add = this.getCurrentMode().getInformationSuffix() != null ? " "
                + this.getCurrentMode().getInformationSuffix() : "";

        return this.getCurrentMode().getName() + add;
    }
}
