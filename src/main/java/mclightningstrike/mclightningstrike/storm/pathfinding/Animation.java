package mclightningstrike.mclightningstrike.storm.pathfinding;

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
    private int[][][] stormSimulation;

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
            Location strikeTarget
    ) {
        this.plugin = plugin;
        this.stormZone = stormZone;
        this.strikeStart = strikeStart;
        this.strikeTarget = strikeTarget;
    }

    /**
     * Begin storm creation.
     */
    public void start() {
        stormSimulation = buildStormSimulation();
        if (stormSimulation != null) {
            createStorm();
        }
    }

    /**
     * Creates and animates a storm.
     */
    private void createStorm() {
        new StormAnimation().runTaskTimer(plugin, 0, 1);
    }

    /**
     * Creates a storm simulation.
     * <p>
     * Simulation Integer Mappings:
     * 0 = open space (can be explored in pathfinding)
     * 1 = blocked space (cannot be explored in pathfinding)
     * 2 = visited space (already explored in pathfinding)
     * 3 = final path space (space in the final path)
     * 4 = start space
     * 5 = target space
     *
     * @return storm simulation.
     */
    private int[][][] buildStormSimulation() {
        int[][][] simulationStormZone = new int[stormZone.length][stormZone[0].length][stormZone[0][0].length];
        int[] simulationStrikeStart = new int[3];
        int[] simulationStrikeTarget = new int[3];

        for (int i = 0; i < stormZone.length; i++) {
            for (int j = 0; j < stormZone[i].length; j++) {
                for (int k = 0; k < stormZone[i][j].length; k++) {
                    if (stormZone[i][j][k].getBlock().getType() == Material.AIR) {
                        simulationStormZone[i][j][k] = 0;
                    } else {
                        simulationStormZone[i][j][k] = 1;
                    }

                    if (stormZone[i][j][k] == strikeStart) {
                        simulationStormZone[i][j][k] = 4;

                        simulationStrikeStart[0] = i;
                        simulationStrikeStart[1] = j;
                        simulationStrikeStart[2] = k;
                    }

                    if (stormZone[i][j][k] == strikeTarget) {
                        simulationStormZone[i][j][k] = 5;

                        simulationStrikeTarget[0] = i;
                        simulationStrikeTarget[1] = j;
                        simulationStrikeTarget[2] = k;
                    }
                }
            }
        }

        Simulation simulation = new Simulation(simulationStormZone, simulationStrikeStart, simulationStrikeTarget);
        simulation.start();

        if (simulation.getPath()) {
            return simulation.getSimulationStormZone();
        }
        return null;
    }

    /**
     * Helper Class.
     * <p>
     * Storm animation class that extends BukkitRunnable. Responsible for handling cloud and lightning bold animations.
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
                createLightningBolt();
                this.cancel();
            }

            t += new Random().nextDouble() * (0.25);
        }

        /**
         * Shoots a lightning bolt from the storm cloud.
         */
        private void createLightningBolt() {
            strikeStart.getWorld().playSound(strikeStart, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 150, 100);
            strikeTarget.getWorld().playSound(strikeStart, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 250, 100);

            for (int i = 0; i < stormSimulation.length; i++) {
                for (int j = 0; j < stormSimulation[0].length; j++) {
                    for (int k = 0; k < stormSimulation[0][0].length; k++) {
                        if (stormSimulation[i][j][k] == 3) {
                            if (j > 0 && stormSimulation[i][j - 1][k] == 3) {
                                for (double n = 0.0; n <= 1.0; n += 0.20) {
                                    World w = stormZone[i][j][k].getBlock().getLocation().getWorld();
                                    w.spawnParticle(
                                            Particle.FIREWORKS_SPARK,
                                            stormZone[i][j][k].getBlock().getLocation().getX(),
                                            stormZone[i][j][k].getBlock().getLocation().getY() - n,
                                            stormZone[i][j][k].getBlock().getLocation().getZ(),
                                            1,
                                            0,
                                            0,
                                            0,
                                            0,
                                            null,
                                            true
                                    );
                                }
                            }
//                            else if (i > 0 && stormSimulation[i - 1][j][k] == 3) {
//                                for (double n = 0.0; n <= 1.0; n += 0.20) {
//                                    World w = stormZone[i][j][k].getBlock().getLocation().getWorld();
//                                    w.spawnParticle(
//                                            Particle.FIREWORKS_SPARK,
//                                            stormZone[i][j][k].getBlock().getLocation().getX() - n,
//                                            stormZone[i][j][k].getBlock().getLocation().getY(),
//                                            stormZone[i][j][k].getBlock().getLocation().getZ(),
//                                            1,
//                                            0,
//                                            0,
//                                            0,
//                                            0,
//                                            null,
//                                            true
//                                    );
//                                }
//                            }
//                            else if (i < stormSimulation.length - 1 && stormSimulation[i + 1][j][k] == 3) {
//                                for (double n = 0.0; n <= 1.0; n += 0.20) {
//                                    World w = stormZone[i][j][k].getBlock().getLocation().getWorld();
//                                    w.spawnParticle(
//                                            Particle.FIREWORKS_SPARK,
//                                            stormZone[i][j][k].getBlock().getLocation().getX() + n,
//                                            stormZone[i][j][k].getBlock().getLocation().getY(),
//                                            stormZone[i][j][k].getBlock().getLocation().getZ(),
//                                            1,
//                                            0,
//                                            0,
//                                            0,
//                                            0,
//                                            null,
//                                            true
//                                    );
//                                }
//                            }
//                            else if (k > 0 && stormSimulation[i][j][k - 1] == 3) {
//                                for (double n = 0.0; n <= 1.0; n += 0.20) {
//                                    World w = stormZone[i][j][k].getBlock().getLocation().getWorld();
//                                    w.spawnParticle(
//                                            Particle.FIREWORKS_SPARK,
//                                            stormZone[i][j][k].getBlock().getLocation().getX(),
//                                            stormZone[i][j][k].getBlock().getLocation().getY(),
//                                            stormZone[i][j][k].getBlock().getLocation().getZ() - n,
//                                            1,
//                                            0,
//                                            0,
//                                            0,
//                                            0,
//                                            null,
//                                            true
//                                    );
//                                }
//                            }
//                            else if (k < stormSimulation[0][0].length - 1 && stormSimulation[i][j][k + 1] == 3) {
//                                for (double n = 0.0; n <= 1.0; n += 0.20) {
//                                    World w = stormZone[i][j][k].getBlock().getLocation().getWorld();
//                                    w.spawnParticle(
//                                            Particle.FIREWORKS_SPARK,
//                                            stormZone[i][j][k].getBlock().getLocation().getX(),
//                                            stormZone[i][j][k].getBlock().getLocation().getY(),
//                                            stormZone[i][j][k].getBlock().getLocation().getZ() + n,
//                                            1,
//                                            0,
//                                            0,
//                                            0,
//                                            0,
//                                            null,
//                                            true
//                                    );
//                                }
//                            }
                            else {
                                World w = stormZone[i][j][k].getBlock().getLocation().getWorld();
                                w.spawnParticle(
                                        Particle.FIREWORKS_SPARK,
                                        stormZone[i][j][k].getBlock().getLocation().getX(),
                                        stormZone[i][j][k].getBlock().getLocation().getY(),
                                        stormZone[i][j][k].getBlock().getLocation().getZ(),
                                        1,
                                        0,
                                        0,
                                        0,
                                        0,
                                        null,
                                        true
                                );
                            }
                        }
                    }
                }
            }
            strikeTarget.getWorld().createExplosion(strikeTarget.getX(), strikeTarget.getY(), strikeTarget.getZ(), 2.5f, true, true);
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
                        strikeStart.getZ()
                );
                goForward(x, y, z, location);
            }
            for (int i = 0; i < length; i++) {
                double randomX = 1.5 + new Random().nextDouble();
                double randomY = layer + new Random().nextDouble();
                Location location = new Location(
                        strikeTarget.getWorld(),
                        strikeStart.getX() - (i / randomX),
                        strikeStart.getY() + randomY,
                        strikeStart.getZ()
                );
                goForward(x, y, z, location);
            }

            for (int i = 0; i < length; i++) {
                double randomX = 1.5 + new Random().nextDouble();
                double randomY = layer + new Random().nextDouble();
                Location location = new Location(
                        strikeTarget.getWorld(),
                        strikeStart.getX() + (i / randomX),
                        strikeStart.getY() + randomY,
                        strikeStart.getZ()
                );
                goBackward(x, y, z, location);
            }
            for (int i = 0; i < length; i++) {
                double randomX = 1.5 + new Random().nextDouble();
                double randomY = layer + new Random().nextDouble();
                Location location = new Location(
                        strikeTarget.getWorld(),
                        strikeStart.getX() - (i / randomX),
                        strikeStart.getY() + randomY,
                        strikeStart.getZ()
                );
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
                    true
            );
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
                    true
            );
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
                    true
            );
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
                    true
            );
            location.add(x, y, z);
        }
    }
}
