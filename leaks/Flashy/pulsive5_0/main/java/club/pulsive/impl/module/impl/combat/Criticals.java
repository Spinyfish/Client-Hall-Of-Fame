package club.pulsive.impl.module.impl.combat;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.event.player.AttackEvent;
import club.pulsive.impl.event.player.PlayerMotionEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.module.impl.exploit.TPAura;
import club.pulsive.impl.property.implementations.EnumProperty;
import club.pulsive.impl.util.client.TimerUtil;
import club.pulsive.impl.util.math.apache.ApacheMath;
import club.pulsive.impl.util.network.PacketUtil;
import club.pulsive.impl.util.player.MovementUtil;
import club.pulsive.impl.util.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "Criticals", renderName = "Criticals", category = Category.COMBAT, keybind = Keyboard.KEY_NONE)
//This Class Wont Be Used, Look in killaura code
public class Criticals extends Module {}
