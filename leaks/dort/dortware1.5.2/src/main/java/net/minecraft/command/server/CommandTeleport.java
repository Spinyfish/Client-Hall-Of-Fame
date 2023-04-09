package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

import java.util.EnumSet;
import java.util.List;

public class CommandTeleport extends CommandBase {
    // private static final String __OBFID = "CL_00001180";

    public String getCommandName() {
        return "tp";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel() {
        return 2;
    }

    public String getCommandUsage(ICommandSender sender) {
        return "commands.tp.usage";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            throw new WrongUsageException("commands.tp.usage");
        } else {
            byte var3 = 0;
            Entity var4;

            if (args.length != 2 && args.length != 4 && args.length != 6) {
                var4 = getCommandSenderAsPlayer(sender);
            } else {
                var4 = func_175768_b(sender, args[0]);
                var3 = 1;
            }

            if (args.length != 1 && args.length != 2) {
                if (var4.worldObj != null) {
                    int var14 = var3 + 1;
                    CoordinateArg var6 = func_175770_a(var4.posX, args[var3], true);
                    CoordinateArg var7 = func_175767_a(var4.posY, args[var14++], 0, 0, false);
                    CoordinateArg var8 = func_175770_a(var4.posZ, args[var14++], true);
                    CoordinateArg var9 = func_175770_a(var4.rotationYaw, args.length > var14 ? args[var14++] : "~", false);
                    CoordinateArg var10 = func_175770_a(var4.rotationPitch, args.length > var14 ? args[var14] : "~", false);
                    float var12;

                    if (var4 instanceof EntityPlayerMP) {
                        EnumSet var11 = EnumSet.noneOf(S08PacketPlayerPosLook.EnumFlags.class);

                        if (var6.func_179630_c()) {
                            var11.add(S08PacketPlayerPosLook.EnumFlags.X);
                        }

                        if (var7.func_179630_c()) {
                            var11.add(S08PacketPlayerPosLook.EnumFlags.Y);
                        }

                        if (var8.func_179630_c()) {
                            var11.add(S08PacketPlayerPosLook.EnumFlags.Z);
                        }

                        if (var10.func_179630_c()) {
                            var11.add(S08PacketPlayerPosLook.EnumFlags.X_ROT);
                        }

                        if (var9.func_179630_c()) {
                            var11.add(S08PacketPlayerPosLook.EnumFlags.Y_ROT);
                        }

                        var12 = (float) var9.func_179629_b();

                        if (!var9.func_179630_c()) {
                            var12 = MathHelper.wrapAngleTo180_float(var12);
                        }

                        float var13 = (float) var10.func_179629_b();

                        if (!var10.func_179630_c()) {
                            var13 = MathHelper.wrapAngleTo180_float(var13);
                        }

                        if (var13 > 90.0F || var13 < -90.0F) {
                            var13 = MathHelper.wrapAngleTo180_float(180.0F - var13);
                            var12 = MathHelper.wrapAngleTo180_float(var12 + 180.0F);
                        }

                        var4.mountEntity(null);
                        ((EntityPlayerMP) var4).playerNetServerHandler.func_175089_a(var6.func_179629_b(), var7.func_179629_b(), var8.func_179629_b(), var12, var13, var11);
                        var4.setRotationYawHead(var12);
                    } else {
                        float var15 = (float) MathHelper.wrapAngleTo180_double(var9.func_179628_a());
                        var12 = (float) MathHelper.wrapAngleTo180_double(var10.func_179628_a());

                        if (var12 > 90.0F || var12 < -90.0F) {
                            var12 = MathHelper.wrapAngleTo180_float(180.0F - var12);
                            var15 = MathHelper.wrapAngleTo180_float(var15 + 180.0F);
                        }

                        var4.setLocationAndAngles(var6.func_179628_a(), var7.func_179628_a(), var8.func_179628_a(), var15, var12);
                        var4.setRotationYawHead(var15);
                    }

                    notifyOperators(sender, this, "commands.tp.success.coordinates", var4.getName(), var6.func_179628_a(), var7.func_179628_a(), var8.func_179628_a());
                }
            } else {
                Entity var5 = func_175768_b(sender, args[args.length - 1]);

                if (var5.worldObj != var4.worldObj) {
                    throw new CommandException("commands.tp.notSameDimension");
                } else {
                    var4.mountEntity(null);

                    if (var4 instanceof EntityPlayerMP) {
                        ((EntityPlayerMP) var4).playerNetServerHandler.setPlayerLocation(var5.posX, var5.posY, var5.posZ, var5.rotationYaw, var5.rotationPitch);
                    } else {
                        var4.setLocationAndAngles(var5.posX, var5.posY, var5.posZ, var5.rotationYaw, var5.rotationPitch);
                    }

                    notifyOperators(sender, this, "commands.tp.success", var4.getName(), var5.getName());
                }
            }
        }
    }

    public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length != 1 && args.length != 2 ? null : getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }
}
