package me.jinthium.straight.impl.commands;

import me.jinthium.straight.api.command.Command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.utils.ChatUtil;
import org.lwjglx.Sys;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FriendCommand extends Command {

    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
    private static final File file = new File(Client.INSTANCE.getClientDir(), "Friends.json");
    public static final List<String> friends = new ArrayList<>();

    public FriendCommand() {
        super("friend", "Manage friends", ".f [add/remove] [username]", "f");
        load();
    }

    @Override
    public void execute(String[] args) {
        boolean usage = false;
        if (args.length == 0) {
            ChatUtil.print("Friend list (§3" + friends.size() + "§7):");
            if (friends.isEmpty()) {
                ChatUtil.print(false, "§7- No friends");
            } else {
                friends.forEach(f -> ChatUtil.print(false, "§7- " + f));
            }
        } else if (args.length >= 2) {
            String name = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            switch (args[0].toLowerCase()) {
                case "add" -> {
                    if (isFriend(name)) {
                        ChatUtil.print("That player is already in your friends list!");
                    } else {
                        friends.add(name);
                        ChatUtil.print("Added §3" + name + " §7to your friends list!");
                    }
                    save();
                }
                case "remove" -> {
                    if (isFriend(name)) {
                        friends.removeIf(f -> f.equalsIgnoreCase(name));
                        ChatUtil.print("Removed §3" + name + " §7from your friends list.");
                    } else {
                        ChatUtil.print("That player is not in your friends list!");
                    }
                    save();
                }
                default -> usage = true;
            }
        } else {
            usage = true;
        }
        if (usage) {
            ChatUtil.print("Usage: " + getUsage());
        }
    }

    private void load() {
        if (!file.exists()) save();
        try {
            JsonObject object = gson.fromJson(new String(Files.readAllBytes(file.toPath())), JsonObject.class);
            object.get("friends").getAsJsonArray().forEach(f -> friends.add(f.getAsString()));
        } catch (Exception e) {
            System.err.println("Failed to load " + file);
            e.printStackTrace();
        }
    }

    public static boolean save() {
        try {
            if (!file.exists() && file.getParentFile().mkdirs()) {
                file.createNewFile();
            }
            Files.writeString(file.toPath(), serialize());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String serialize() {
        JsonObject obj = new JsonObject();
        JsonArray arr = new JsonArray();
        friends.forEach(arr::add);
        obj.add("friends", arr);
        return gson.toJson(obj);
    }

    public static boolean isFriend(String name) {
        if (name == null) return false;
        return friends.stream().anyMatch(name::equalsIgnoreCase);
    }

}