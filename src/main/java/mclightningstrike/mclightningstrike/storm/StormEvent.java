package mclightningstrike.mclightningstrike.storm;

import mclightningstrike.mclightningstrike.McLightningStrike;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public record StormEvent(McLightningStrike plugin) implements Listener {

    private static Arrow arrow;

    /**
     * Projectile Launch Event Listener.
     * <p>
     * Tracks the initial location of the arrow object (where it's shot from).
     *
     * @param event projectile launch event
     */
    @EventHandler
    public void onProjectileShot(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow arrow) {
            StormEvent.arrow = arrow;
        }
    }

    /**
     * Projectile Hit Event Listener.
     * <p>
     * Tracks the arrow object upon shooting.
     *
     * @param event projectile launch event
     */
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity().getEntityId() == arrow.getEntityId()) {
            new Storm(plugin, arrow.getLocation()).generate();
            // arrow.getWorld().strikeLightning(arrow.getLocation());
        }
    }
}
