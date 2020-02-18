package com.cyprias.chunkspawnerlimiter.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.cyprias.chunkspawnerlimiter.ChatUtils;
import com.cyprias.chunkspawnerlimiter.ChunkSpawnerLimiter;
import com.cyprias.chunkspawnerlimiter.Logger;
import com.cyprias.chunkspawnerlimiter.compare.MobGroupCompare;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldListener implements Listener {
    HashMap<Chunk, Integer> chunkTasks = new HashMap<Chunk, Integer>();

    class inspectTask extends BukkitRunnable {
        Chunk c;
        public inspectTask(Chunk c) {
            this.c = c;
        }

        @Override
        public void run() {
            Logger.debug("Active check " + c.getX() + " " + c.getZ());
            if (!c.isLoaded()){
                ChunkSpawnerLimiter.cancelTask(taskID);
                return;
            }

            CheckChunk(c);
        }

        int taskID;
        public void setId(int taskID) {
            this.taskID = taskID;

        }

    }

    @EventHandler
    public void onChunkLoadEvent(final ChunkLoadEvent e) {
        if (ChunkSpawnerLimiter.getInstance().getConfig().getBoolean("properties.active-inspections")){
            inspectTask task = new inspectTask(e.getChunk());
            int taskID = ChunkSpawnerLimiter.scheduleSyncRepeatingTask(task, ChunkSpawnerLimiter.getInstance().getConfig().getInt("properties.inspection-frequency") * 20L);
            task.setId(taskID);

            chunkTasks.put(e.getChunk(), taskID);
        }


        if (ChunkSpawnerLimiter.getInstance().getConfig().getBoolean("properties.check-chunk-load"))
            CheckChunk(e.getChunk());
    }

    public void onChunkUnloadEvent(final ChunkUnloadEvent e) {
        if (chunkTasks.containsKey(e.getChunk())){
            ChunkSpawnerLimiter.getInstance().getServer().getScheduler().cancelTask(chunkTasks.get(e.getChunk()));
            chunkTasks.remove(e.getChunk());
        }

        if (ChunkSpawnerLimiter.getInstance().getConfig().getBoolean("properties.check-chunk-unload"))
            CheckChunk(e.getChunk());
    }



    public static void CheckChunk(Chunk c) {
        // Stop processing quickly if this world is excluded from limits.
        if (ChunkSpawnerLimiter.getInstance().getConfig().getStringList("excludedWorlds").contains(c.getWorld().getName())) {
            return;
        }

        Entity[] ents = c.getEntities();

        HashMap<String, ArrayList<Entity>> types = new HashMap<String, ArrayList<Entity>>();

        for (int i = ents.length - 1; i >= 0; i--) {
            // ents[i].getType();
            EntityType t = ents[i].getType();

            String eType = t.toString();
            String eGroup = MobGroupCompare.getMobGroup(ents[i]);

            if (ChunkSpawnerLimiter.getInstance().getConfig().contains("entities." + eType)) {
                if (!types.containsKey(eType))
                    types.put(eType, new ArrayList<Entity>());
                types.get(eType).add(ents[i]);
            }

            if (ChunkSpawnerLimiter.getInstance().getConfig().contains("entities." + eGroup)) {
                if (!types.containsKey(eGroup))
                    types.put(eGroup, new ArrayList<Entity>());
                types.get(eGroup).add(ents[i]);
            }
        }

        for (Entry<String, ArrayList<Entity>> entry : types.entrySet()) {
            String eType = entry.getKey();
            int limit = ChunkSpawnerLimiter.getInstance().getConfig().getInt("entities." + eType);

            // Logger.debug(c.getX() + " " + c.getZ() + ": " + eType + " = " +
            // entry.getValue().size());

            if (entry.getValue().size() > limit) {
                Logger.debug("Removing " + (entry.getValue().size() - limit) + " " + eType + " @ " + c.getX() + " " + c.getZ());

                if (ChunkSpawnerLimiter.getInstance().getConfig().getBoolean("properties.notify-players")){
                    for (int i = ents.length - 1; i >= 0; i--) {
                        if (ents[i] instanceof Player){
                            Player p = (Player) ents[i];
                            ChatUtils.send(p, String.format(ChunkSpawnerLimiter.getInstance().getConfig().getString("messages.removedEntites"), entry.getValue().size() - limit, eType));
                        }
                    }
                }


                for (int i = entry.getValue().size() - 1; i >= limit; i--) {
                    entry.getValue().get(i).remove();
                }

            }

        }

    }



}
