package mclightningstrike.mclightningstrike.storm;

import mclightningstrike.mclightningstrike.McLightningStrike;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.UUID;

import static org.apache.logging.log4j.LogManager.getLogger;
import static org.bukkit.Bukkit.getServer;

public record StormEvent(McLightningStrike plugin) implements Listener {

    private static UUID arrow;

    @EventHandler
    public void onProjectileShot(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow) {
            StormEvent.arrow = event.getEntity().getUniqueId();
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow && arrow != null) {
            getLogger().info("projectile hit");
            new Storm(plugin, event.getEntity().getLocation()).generate();
            arrow = null;
        }
    }
}
