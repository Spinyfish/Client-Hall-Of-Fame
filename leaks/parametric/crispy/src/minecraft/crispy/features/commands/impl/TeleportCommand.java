package crispy.features.commands.impl;

import crispy.Crispy;
import crispy.features.commands.Command;
import crispy.features.commands.CommandInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Vec3;

@CommandInfo(name = "Teleport", description = "Teleports you across map", syntax = ".tp [x, z]", alias = "tp")
public class TeleportCommand extends Command {
    @Override
    public void onCommand(String arg, String[] args) throws Exception {
        if (args.length > 1) {
            crispy.features.hacks.impl.movement.Teleport teleport = Crispy.INSTANCE.getHackManager().getHack(crispy.features.hacks.impl.movement.Teleport.class);
            Vec3 vec3 = Minecraft.getMinecraft().thePlayer.getVec();
            vec3.add(0, 600, 0);

            teleport.blinkToPosFromPos(Minecraft.getMinecraft().thePlayer.getVec(), vec3, 6);
            Vec3 tpLoc = new Vec3(Double.parseDouble(args[0]), vec3.yCoord, Double.parseDouble(args[1]));
            teleport.blinkToPosFromPos(vec3, tpLoc, 6);
        } else {
            Crispy.addChatMessage("Too few arguments, syntax: " + getSyntax());
        }
    }
}
