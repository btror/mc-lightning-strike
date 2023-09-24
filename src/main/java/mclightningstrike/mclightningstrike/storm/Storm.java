package mclightningstrike.mclightningstrike.storm;

import com.github.btror.mcpathfinding.McPathfinding;
import mclightningstrike.mclightningstrike.McLightningStrike;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public record Storm(McLightningStrike plugin, Location strikeTarget) {

    /**
     * Constructor.
     * <p>
     * Creates a storm.
     *
     * @param plugin       McLightningStrike instance.
     * @param strikeTarget location of storm bolt target.
     */
    public Storm {
    }

    /**
     * Generates a storm.
     */
    public void generate(boolean scatteredStorm) {
        if (!scatteredStorm) {
            int random = (int) (Math.random() * 15 + 12); // 15 max 12 min
            createStormZone(strikeTarget, random, random, random);
        } else {
            for (int i = 0; i < 8; i++) {
                int randomSize = (int) (Math.random() * 15 + 12); // 15 max 12 min
                int randomLocationOffset = (int) (Math.random() * 10 + -10); // 10 max -10 min
                createStormZone(
                        generateNewLocationRelativeToTarget(randomLocationOffset),
                        randomSize,
                        randomSize,
                        randomSize);
            }
        }
    }

    /**
     * Generates a new location relative to the storm target location.
     *
     * @param size distance (x and z) from the original target location.
     */
    private Location generateNewLocationRelativeToTarget(int size) {
        Location location = new Location(
                strikeTarget.getWorld(),
                strikeTarget.getX() + size,
                strikeTarget.getY(),
                strikeTarget.getZ() + size);
        while (location.getBlock().getType() != Material.AIR) {
            double newY = location.getY() + 1.0;
            location = new Location(
                    location.getWorld(),
                    location.getX(),
                    newY,
                    location.getZ());
        }
        return location;
    }

    /**
     * Creates storm zone (LxWxH).
     * <p>
     * Storm Zone Mappings:
     * i = length
     * j = height
     * k = width
     *
     * @param strikeTarget target location of the lightning bolt.
     * @param x            storm zone x size
     * @param y            storm zone y size
     * @param z            storm zone z size
     */
    private void createStormZone(Location strikeTarget, int x, int y, int z) {
        Location[][][] stormZone = new Location[x][y][z];
        for (int i = 0; i < stormZone.length; i++) {
            for (int j = 0; j < stormZone[i].length; j++) {
                for (int k = 0; k < stormZone[i][j].length; k++) {
                    Location location = new Location(
                            strikeTarget.getWorld(),
                            strikeTarget.getX() - (x / 2.0) + i,
                            strikeTarget.getY() + 0 + j,
                            strikeTarget.getZ() + (z / 2.0) - k);
                    stormZone[i][j][k] = location;
                }
            }
        }

        Location strikeStart = stormZone[(int) (Math.random() * x - 1)][y - 1][(int) (Math.random() * z - 1)];
        strikeTarget = stormZone[x / 2][0][z / 2];

        new StormAnimation(stormZone, strikeStart, strikeTarget).runTaskTimer(plugin, 0, 1);
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
        private final Location strikeStart;
        private final Location strikeTarget;
        private final Location[][][] stormZone;

        /**
         * Constructor.
         * <p>
         * Plays a sound for beginning of storm creation on initialization.
         */
        private StormAnimation(Location[][][] stormZone, Location strikeStart, Location strikeTarget) {
            this.stormZone = stormZone;
            this.strikeStart = strikeStart;
            this.strikeTarget = strikeTarget;
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

                        McPathfinding.astarSearch(plugin, stormZone, strikeStart, strikeTarget,
                                Particle.ELECTRIC_SPARK, true, true, 0, 0);

                        strikeTarget.getWorld().createExplosion(strikeTarget.getX(), strikeTarget.getY(),
                                strikeTarget.getZ(), 2.5f, true, true);
                        cancel();
                    }
                }.runTaskTimer(plugin, (long) this.t + 50L, (long) this.t + 50L);

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
