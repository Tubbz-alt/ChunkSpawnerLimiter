package com.cyprias.chunkspawnerlimiter.listeners;

import com.cyprias.chunkspawnerlimiter.ChunkSpawnerLimiter;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class EntityListener implements Listener {

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent e) {
        if (e.isCancelled())
            return;

        if (!ChunkSpawnerLimiter.getInstance().getConfig().getBoolean("properties.watch-creature-spawns"))
            return;

        // LivingEntity ent = e.getEntity();

        // EntityType t = ent.getType();
        // String eType = t.toString();
        // String eGroup = MobGroupCompare.getMobGroup(ent);

        Chunk c = e.getLocation().getChunk();

        WorldListener.CheckChunk(c);

        int surrounding = ChunkSpawnerLimiter.getInstance().getConfig().getInt("properties.check-surrounding-chunks");

        if (surrounding > 0) {
            World w = e.getLocation().getWorld();
            for (int x = c.getX() + surrounding; x >= (c.getX() - surrounding); x--) {
                for (int z = c.getZ() + surrounding; z >= (c.getZ() - surrounding); z--) {
                    // Logger.debug("Checking chunk " + x + " " +z);
                    WorldListener.CheckChunk(w.getChunkAt(x, z));
                }
            }

        }
    }
}
