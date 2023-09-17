package mclightningstrike.mclightningstrike.storm.pathfinding;

import com.github.btror.mcpathfinding.McPathfinding;
import mclightningstrike.mclightningstrike.McLightningStrike;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class Animation {
    private final McLightningStrike plugin;
    private final Location[][][] stormZone;
    private final Location strikeStart;
    private final Location strikeTarget;
    private final long delay = 0;
    private final long period = 0;

    /**
     * Constructor.
     *
     * @param plugin       storm strike plugin instance
     * @param stormZone    volume of space consisting of the storm
     * @param strikeStart  start location of the top of the storm bolt
     * @param strikeTarget target location of the storm bolt
     */
    public Animation(
            McLightningStrike plugin,
            Location[][][] stormZone,
            Location strikeStart,
            Location strikeTarget) {
        this.plugin = plugin;
        this.stormZone = stormZone;
        this.strikeStart = strikeStart;
        this.strikeTarget = strikeTarget;
    }

    /**
     * Begin storm creation.
     */
    public void start() {
        createStorm();
    }

    /**
     * Creates and animates a storm.
     */
    private void createStorm() {
        new StormAnimation().runTaskTimer(plugin, 0, 1);
    }

    /**
     * Helper Class.
     * <p>
     * Storm animation class that extends BukkitRunnable. Responsible for handling
     * cloud and lightning bold animations.
     */
    private class StormAnimation extends BukkitRunnable {
        private double t = 0;
        private final double cloudSize = new Random().nextDouble() * 1.2 + (3.0 - 1.2);

        /**
         * Constructor.
         * <p>
         * Plays a sound for beginning of storm creation on initialization.
         */
        private StormAnimation() {
            strikeStart.getWorld().playSound(strikeStart, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 50, 10);
        }

        @Override
        public void run() {
            Vector direction = strikeStart.getDirection().normalize();

            double x = direction.getX() * (t + new Random().nextDouble() * (0.3));
            double y = direction.getY() * (t + new Random().nextDouble() * (0.5));
            double z = direction.getZ() * (t + new Random().nextDouble() * (0.3));

            spawnCloudLayer(3, -1.0, x, y, z);
            spawnCloudLayer(4, -0.5, x, y, z);
            spawnCloudLayer(5, 0.0, x, y, z);
            spawnCloudLayer(4, 0.5, x, y, z);

            if (t > cloudSize) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        strikeStart.getWorld().playSound(strikeStart, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 150, 100);
                        strikeTarget.getWorld().playSound(strikeStart, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 250, 100);
                        McPathfinding.greedyBestFirstSearch(plugin, stormZone, strikeStart, strikeTarget,
                                Particle.ELECTRIC_SPARK, true, delay, period);
                        strikeTarget.getWorld().createExplosion(strikeTarget.getX(), strikeTarget.getY(),
                                strikeTarget.getZ(), 2.5f, true, true);
                        cancel();
                    }
                }.runTaskTimer(plugin, (long) this.t + 50L, 0L);
                this.cancel();
            }

            t += new Random().nextDouble() * (0.25);
        }

        /**
         * Spawns a layer of the storm cloud.
         *
         * @param length length of cloud layer
         * @param layer  n height of the cloud layer
         * @param x      x offset location
         * @param y      y offset location
         * @param z      z offset location
         */
        private void spawnCloudLayer(int length, double layer, double x, double y, double z) {
            for (int i = 0; i < length; i++) {
                double randomX = 1.5 + new Random().nextDouble();
                double randomY = layer + new Random().nextDouble();
                Location location = new Location(
                        strikeTarget.getWorld(),
                        strikeStart.getX() + (i / randomX),
                        strikeStart.getY() + randomY,
                        strikeStart.getZ());
                goForward(x, y, z, location);
            }
            for (int i = 0; i < length; i++) {
                double randomX = 1.5 + new Random().nextDouble();
                double randomY = layer + new Random().nextDouble();
                Location location = new Location(
                        strikeTarget.getWorld(),
                        strikeStart.getX() - (i / randomX),
                        strikeStart.getY() + randomY,
                        strikeStart.getZ());
                goForward(x, y, z, location);
            }

            for (int i = 0; i < length; i++) {
                double randomX = 1.5 + new Random().nextDouble();
                double randomY = layer + new Random().nextDouble();
                Location location = new Location(
                        strikeTarget.getWorld(),
                        strikeStart.getX() + (i / randomX),
                        strikeStart.getY() + randomY,
                        strikeStart.getZ());
                goBackward(x, y, z, location);
            }
            for (int i = 0; i < length; i++) {
                double randomX = 1.5 + new Random().nextDouble();
                double randomY = layer + new Random().nextDouble();
                Location location = new Location(
                        strikeTarget.getWorld(),
                        strikeStart.getX() - (i / randomX),
                        strikeStart.getY() + randomY,
                        strikeStart.getZ());
                goBackward(x, y, z, location);
            }
        }

        /**
         * Spawns the front layer of the storm cloud.
         *
         * @param x        offset location x position (length)
         * @param y        offset location y position (height)
         * @param z        offset location z position (width)
         * @param location location
         */
        private void goForward(double x, double y, double z, Location location) {
            location.add(x, y, z);
            location.getWorld().spawnParticle(
                    Particle.ELECTRIC_SPARK,
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    1,
                    0,
                    0,
                    0,
                    0,
                    null,
                    true);
            location.getWorld().spawnParticle(
                    Particle.CAMPFIRE_SIGNAL_SMOKE,
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    1,
                    0,
                    0,
                    0,
                    0,
                    null,
                    true);
            location.subtract(x, y, z);
        }

        /**
         * Spawns the back layer of the storm cloud.
         *
         * @param x        offset location x position (length)
         * @param y        offset location y position (height)
         * @param z        offset location z position (width)
         * @param location location
         */
        private void goBackward(double x, double y, double z, Location location) {
            location.subtract(x, y, z);
            location.getWorld().spawnParticle(
                    Particle.ELECTRIC_SPARK,
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    1,
                    0,
                    0,
                    0,
                    0,
                    null,
                    true);
            location.getWorld().spawnParticle(
                    Particle.CAMPFIRE_SIGNAL_SMOKE,
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    1,
                    0,
                    0,
                    0,
                    0,
                    null,
                    true);
            location.add(x, y, z);
        }
    }
}
