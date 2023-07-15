package mclightningstrike.mclightningstrike.storm;

import mclightningstrike.mclightningstrike.McLightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public record StormListener(McLightningStrike plugin) implements Listener {

    private static final Map<UUID, PotionEffect> projectiles = new HashMap<>();

    /**
     * Event handler for shooting a projectile.
     *
     * @param event ProjectileLaunchEvent
     */
    @EventHandler
    public void onProjectileShot(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof ThrownPotion) {
            Player shooter = (Player) event.getEntity().getShooter();
            assert shooter != null;
            Collection<PotionEffect> effects = ((ThrownPotion) event.getEntity()).getEffects();
            for (PotionEffect effect : effects) {
                if (effect.getType().getName().equalsIgnoreCase("HARM")) {
                    projectiles.put(event.getEntity().getUniqueId(), effect);
                    break;
                }
            }
        }
    }

    /**
     * Event handler for the harm potion on landing.
     *
     * @param event ProjectileHitEvent
     */
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof ThrownPotion && projectiles.containsKey(event.getEntity().getUniqueId())) {
            if (projectiles.get(event.getEntity().getUniqueId()).getType().getName().equalsIgnoreCase("HARM")
                    && projectiles.get(event.getEntity().getUniqueId()).getAmplifier() == 0) {
                new Storm(plugin, event.getEntity().getLocation()).generate(false);
            } else if (projectiles.get(event.getEntity().getUniqueId()).getType().getName().equalsIgnoreCase("HARM")
                    && projectiles.get(event.getEntity().getUniqueId()).getAmplifier() == 1) {
                new Storm(plugin, event.getEntity().getLocation()).generate(true);
            }
            projectiles.remove(event.getEntity().getUniqueId());
        }
    }
}
