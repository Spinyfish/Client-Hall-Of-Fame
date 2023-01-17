package com.alan.clients.module.impl.player;


import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.module.impl.player.antivoid.*;
import com.alan.clients.value.impl.ModeValue;


/**
 * @author Alan
 * @since 23/10/2021
 */

@Rise
@ModuleInfo(name = "Anti Void", description = "module.player.antivoid.description", category = Category.PLAYER)
public class AntiVoid extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new PacketAntiVoid("Packet", this))
            .add(new PositionAntiVoid("Position", this))
            .add(new BlinkAntiVoid("Blink", this))
            .add(new BlinkAntiVoid("Watchdog", this))
            .add(new VulcanAntiVoid("Vulcan", this))
            .add(new CollisionAntiVoid("Collision", this))
            .setDefault("Packet");
}