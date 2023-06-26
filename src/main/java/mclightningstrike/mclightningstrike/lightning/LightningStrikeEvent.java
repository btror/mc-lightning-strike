package mclightningstrike.mclightningstrike.lightning;

import mclightningstrike.mclightningstrike.McLightningStrike;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public record LightningStrikeEvent(McLightningStrike plugin) implements Listener {

    private static Arrow arrow;

    @EventHandler
    public void onProjectileShot(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow arrow) {
            LightningStrikeEvent.arrow = arrow;
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity().getEntityId() == arrow.getEntityId()) {
            new Lightning(plugin, arrow.getLocation()).strike();
            // arrow.getWorld().strikeLightning(arrow.getLocation());
        }
    }
}
