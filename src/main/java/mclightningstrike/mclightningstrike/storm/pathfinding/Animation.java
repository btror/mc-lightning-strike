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
        strikeStart.getWorld().playSound(strikeStart, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 50, 10);

        new BukkitRunnable() {
            double t = 0;

            @Override
            public void run() {
                Vector direction = strikeStart.getDirection().normalize();
                double randTx = 0 + new Random().nextDouble() * (0.3);
                double randTy = 0 + new Random().nextDouble() * (0.5);
                double randTz = 0 + new Random().nextDouble() * (0.3);
                double x = direction.getX() * (t + randTx);
                double y = direction.getY() * (t + randTy);
                double z = direction.getZ() * (t + randTz);

                spawnCloudLayer(6, 0, x, y, z);
                spawnCloudLayer(7, 0.5, x, y, z);
                spawnCloudLayer(9, 1.0, x, y, z);
                spawnCloudLayer(8, 1.5, x, y, z);
                spawnCloudLayer(6, 2.0, x, y, z);

                if (t > 5.0) {
                    createLightning();
                    this.cancel();
                }
                double random = 0 + new Random().nextDouble() * (0.25);
                t += random;
            }

            /**
             * Shoots a storm bolt from the storm cloud.
             */
            private void createLightning() {
                strikeStart.getWorld().playSound(strikeStart, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 150, 100);
                strikeTarget.getWorld().playSound(strikeStart, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 250, 100);

                for (int i = 0; i < stormSimulation.length; i++) {
                    for (int j = 0; j < stormSimulation[0].length; j++) {
                        for (int k = 0; k < stormSimulation[0][0].length; k++) {
                            if (stormSimulation[i][j][k] == 3) {
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

            /**
             * Spawns a layer of the storm cloud.
             *
             * @param length length of cloud layer
             * @param layer n height of the cloud layer
             * @param x x offset location
             * @param y y offset location
             * @param z z offset location
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
             * @param x offset location x position (length)
             * @param y offset location y position (height)
             * @param z offset location z position (width)
             * @param location location
             */
            private void goForward(double x, double y, double z, Location location) {
                location.add(x, y, z);
                location.getWorld().spawnParticle(
                        Particle.SMOKE_LARGE,
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
             * @param x offset location x position (length)
             * @param y offset location y position (height)
             * @param z offset location z position (width)
             * @param location location
             */
            private void goBackward(double x, double y, double z, Location location) {
                location.subtract(x, y, z);
                location.getWorld().spawnParticle(
                        Particle.SMOKE_LARGE,
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
        }.runTaskTimer(plugin, 0, 1);
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
        int[][][] simulationStormZone = new int[stormZone.length][stormZone.length][stormZone.length];
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
}
