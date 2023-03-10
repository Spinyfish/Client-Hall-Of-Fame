/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.spongepowered.api.command.CommandCallable
 *  org.spongepowered.api.command.CommandException
 *  org.spongepowered.api.command.CommandResult
 *  org.spongepowered.api.command.CommandSource
 *  org.spongepowered.api.text.Text
 *  org.spongepowered.api.world.Location
 *  org.spongepowered.api.world.World
 */
package com.viaversion.viaversion.sponge.commands;

import com.viaversion.viaversion.commands.ViaCommandHandler;
import com.viaversion.viaversion.sponge.commands.SpongeCommandSender;
import java.util.List;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class SpongeCommandHandler
extends ViaCommandHandler
implements CommandCallable {
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        String[] args = arguments.length() > 0 ? arguments.split(" ") : new String[]{};
        this.onCommand(new SpongeCommandSender(source), args);
        return CommandResult.success();
    }

    public List<String> getSuggestions(CommandSource commandSource, String s, @Nullable Location<World> location) throws CommandException {
        return this.getSuggestions(commandSource, s);
    }

    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        String[] args = arguments.split(" ", -1);
        return this.onTabComplete(new SpongeCommandSender(source), args);
    }

    public boolean testPermission(CommandSource source) {
        return source.hasPermission("viaversion.admin");
    }

    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.of(Text.of((String)"Shows ViaVersion Version and more."));
    }

    public Optional<Text> getHelp(CommandSource source) {
        return Optional.empty();
    }

    public Text getUsage(CommandSource source) {
        return Text.of((String)"Usage /viaversion");
    }
}

