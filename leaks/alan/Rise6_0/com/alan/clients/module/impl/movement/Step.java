package com.alan.clients.module.impl.movement;

import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.module.impl.movement.step.*;
import com.alan.clients.value.impl.ModeValue;

@Rise
@ModuleInfo(name = "Step", description = "module.movement.step.description", category = Category.MOVEMENT)
public class Step extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new VanillaStep("Vanilla", this))
            .add(new WatchdogStep("Watchdog", this))
            .add(new NCPStep("NCP", this))
            .add(new NCPPacketLessStep("NCP Packetless", this))
            .add(new VulcanStep("Vulcan", this))
            .add(new MatrixStep("Matrix", this))
            .add(new JumpStep("Jump", this))
            .setDefault("Vanilla");
}