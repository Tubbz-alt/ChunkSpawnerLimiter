package com.cyprias.chunkspawnerlimiter;

import com.cyprias.chunkspawnerlimiter.listeners.EntityListener;
import com.cyprias.chunkspawnerlimiter.listeners.WorldListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

import org.bukkit.Location;

public class ChunkSpawnerLimiter extends JavaPlugin {
    private static Plugin instance = null;
    public static String chatPrefix = "&4[&bCSL&4]&r ";

    public static HashMap<String, Location> deaths = new HashMap<String, Location>();

    public void onEnable() {
        instance = this;

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // Register our event listener.
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
        getServer().getPluginManager().registerEvents(new WorldListener(), this);
    }

    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
    }

    public static Plugin getInstance() {
        return instance;
    }


    public static int scheduleSyncRepeatingTask(Runnable run, long delay) {
        return scheduleSyncRepeatingTask(run, delay, delay);
    }

    public static int scheduleSyncRepeatingTask(Runnable run, long start, long delay) {
        return instance.getServer().getScheduler().scheduleSyncRepeatingTask(instance, run, start, delay);
    }

    public static void cancelTask(int taskID) {
        instance.getServer().getScheduler().cancelTask(taskID);
    }


}
