package tech.dort.dortware.impl.commands;

import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C03PacketPlayer;
import tech.dort.dortware.api.command.Command;
import tech.dort.dortware.api.command.CommandData;
import tech.dort.dortware.impl.utils.networking.PacketUtil;
import tech.dort.dortware.impl.utils.pathfinding.DortPathFinder;
import tech.dort.dortware.impl.utils.pathfinding.Vec3;
import tech.dort.dortware.impl.utils.player.ChatUtil;

import java.util.ArrayList;

public class TeleportCommand extends Command {

    public TeleportCommand() {
        super(new CommandData("teleport", "tp"));
    }

    @Override
    public void run(String command, String... args) {
        try {
            if (command.equalsIgnoreCase("tp") || command.equalsIgnoreCase("teleport")) {
                switch (args[1]) {
                    case "xyz": {
                        ArrayList<Vec3> path;

                        path = DortPathFinder.computePath(new tech.dort.dortware.impl.utils.pathfinding.Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), new tech.dort.dortware.impl.utils.pathfinding.Vec3(Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4])));

                        int i = 0;
                        for (tech.dort.dortware.impl.utils.pathfinding.Vec3 vector : path) {
                            i++;
                            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(vector.getX(), vector.getY(), vector.getZ(), true));
                            mc.thePlayer.setPosition(Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]));
//                            ChatUtil.displayChatMessage("Teleported " + i + " time(s).");
                        }

                        ChatUtil.displayChatMessage("Teleported to " + args[2] + " " + args[3] + " " + args[4] + " in " + i + " teleports.");
                    }
                    break;


                    case "player": {
                        String name = args[2];
                        Entity entity = mc.theWorld.getPlayerEntityByName(name, true);
                        ArrayList<Vec3> path;

                        path = DortPathFinder.computePath(new tech.dort.dortware.impl.utils.pathfinding.Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), new tech.dort.dortware.impl.utils.pathfinding.Vec3(entity.posX, entity.posY, entity.posZ));

                        int i = 0;
                        for (tech.dort.dortware.impl.utils.pathfinding.Vec3 vector : path) {
                            i++;
                            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(vector.getX(), vector.getY(), vector.getZ(), true));
                            mc.thePlayer.setPosition(entity.posX, entity.posY, entity.posZ);
                        }

                        ChatUtil.displayChatMessage("Teleported to " + entity.getName() + " in " + i + " teleports.");
                    }
                    break;

                    default:
                        ChatUtil.displayChatMessage("Invalid option.");
                        break;
//                }
                }
            }
        } catch (ArrayIndexOutOfBoundsException exception) {
            ChatUtil.displayChatMessage("Not enough arguments\nUsage:\nteleport [player/xyz] [username/x] [y] [z]");
        } catch (Exception exception) {
            ChatUtil.displayChatMessage("Invalid arguments or entity is not in range.");
        }
    }
}