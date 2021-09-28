package com.danikvitek.othercapes;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.StringReader;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CapeCommand implements TabExecutor {
    private static final HashMap<String, JsonObject> titleToCapeMap;

    static {
        titleToCapeMap = new HashMap<>();
//        JsonObject cape = new JsonObject();
        JsonObject url = new JsonObject();
        url.addProperty("url", "http://textures.minecraft.net/texture/153b1a0dfcbae953cdeb6f2c2bf6bf79943239b1372780da44bcbb29273131da");
//        cape.add("CAPE", url);
        titleToCapeMap.put("MineCon2013", url);
    }

    public CapeCommand() {}

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("othercapes.command.cape")) {
                if (args.length == 1) {
                    if (titleToCapeMap.containsKey(args[0])) {
                        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
                        GameProfile profile = nmsPlayer.getProfile();
                        PropertyMap propertyMap = profile.getProperties();
                        Property skinProperty = propertyMap.get("textures").iterator().next();
                        JsonReader reader = new JsonReader(new StringReader(fromBase64String(skinProperty.getValue())));
                        reader.setLenient(true);
                        JsonObject skinJSON = new JsonParser().parse(reader).getAsJsonObject();
                        JsonObject texturesJSON = skinJSON.get("textures").getAsJsonObject();
                        texturesJSON.add("CAPE", titleToCapeMap.get(args[0]));
                        skinJSON.add("textures", texturesJSON);
                        System.out.println(skinJSON);
                    }
                    else player.sendMessage(ChatColor.RED + "Wrong cape title");
                }
                else return false;
            }
            else player.sendMessage(ChatColor.RED + "You do not have permission to do that");
        }
        else sender.sendMessage(ChatColor.RED + "Command can only be used by a player");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (sender instanceof Player && sender.hasPermission("othercapes.command.cape")) {
            if (args.length == 1)
                return titleToCapeMap.keySet().stream().filter(k -> k.contains(args[0])).collect(Collectors.toList());
        }
        return null;
    }

    private static String toBase64String(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes());
    }

    private static String fromBase64String(String s) {
        return new String(Base64.getDecoder().decode(s));
    }
}
