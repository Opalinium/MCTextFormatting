package me.randomgamingdev.mctextformatting;

import org.bukkit.plugin.java.JavaPlugin;

public final class MCTextFormatting extends JavaPlugin {

    @Override
    public void onEnable() {
        System.out.println("MCTextFormatting is shutting down!");
        this.getServer().getPluginManager().registerEvents(new ChatListener(this), this);
    }

    @Override
    public void onDisable() {
        System.out.println("MCTextFormatting is shutting down!");
    }
}
