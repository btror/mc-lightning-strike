package mclightningstrike.mclightningstrike.storm;

import mclightningstrike.mclightningstrike.McLightningStrike;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.apache.logging.log4j.LogManager.getLogger;

public record StormListener(McLightningStrike plugin) implements Listener {

    private static final Set<UUID> arrows = new HashSet<>();

    @EventHandler
    public void onProjectileShot(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow) {
            getLogger().info("projectile shot");
            StormListener.arrows.add(event.getEntity().getUniqueId());
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow && arrows.contains(event.getEntity().getUniqueId())) {
            getLogger().info("projectile hit");
            new Storm(plugin, event.getEntity().getLocation()).generate();
            arrows.remove(event.getEntity().getUniqueId());
        }
    }
}
