package mclightningstrike.mclightningstrike.storm;

import mclightningstrike.mclightningstrike.McLightningStrike;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.*;

import static org.apache.logging.log4j.LogManager.getLogger;

public record StormListener(McLightningStrike plugin) implements Listener {

    private static final Map<UUID, Material> arrows = new HashMap<>();

    /**
     * Event handler for shooting an arrow.
     *
     * @param event ProjectileLaunchEvent
     */
    @EventHandler
    public void onProjectileShot(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Player shooter = (Player) event.getEntity().getShooter();
            assert shooter != null;
            getLogger().info("item: " + shooter.getInventory().getItemInMainHand().getType());
            StormListener.arrows.put(event.getEntity().getUniqueId(), shooter.getInventory().getItemInMainHand().getType());
        }
    }

    /**
     * Event handler for the arrow on landing.
     *
     * @param event ProjectileHitEvent
     */
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow && arrows.containsKey(event.getEntity().getUniqueId())) {
            if (arrows.get(event.getEntity().getUniqueId()) == Material.BOW) {
                new Storm(plugin, event.getEntity().getLocation()).generate(false);
            } else if (arrows.get(event.getEntity().getUniqueId()) == Material.CROSSBOW) {
                new Storm(plugin, event.getEntity().getLocation()).generate(true);
            }
            arrows.remove(event.getEntity().getUniqueId());
        }
    }
}
