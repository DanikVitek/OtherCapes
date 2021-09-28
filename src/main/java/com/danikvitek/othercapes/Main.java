package com.danikvitek.othercapes;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("cape").setExecutor(new CapeCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
