package mclightningstrike.mclightningstrike.lightning.pathfinding;

import mclightningstrike.mclightningstrike.McLightningStrike;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;

import static org.apache.logging.log4j.LogManager.getLogger;

public class Animation {

    private McLightningStrike plugin;
    private Location[][][] lightningZone;
    private Location strikeStart;
    private Location strikeTarget;

    private int[][][] simulationLightningZone;
    private int[] simulationStrikeStart;
    private int[] simulationStrikeTarget;

    private int[][][] completedSimulationLightningZone;

    public Animation(
            McLightningStrike plugin,
            Location[][][] lightningZone,
            Location strikeStart,
            Location strikeTarget
    ) {
        this.plugin = plugin;
        this.lightningZone = lightningZone;
        this.strikeStart = strikeStart;
        this.strikeTarget = strikeTarget;
    }

    public void start() {
        createLightningStrikeZoneSimulation();
        startLightningStrikeSimulation();
        if (completedSimulationLightningZone != null) {
            for (int i = 0; i < completedSimulationLightningZone.length; i++) {
                for (int j = 0; j < completedSimulationLightningZone[0].length; j++) {
                    for (int k = 0; k < completedSimulationLightningZone[0][0].length; k++) {
                        if (completedSimulationLightningZone[i][j][k] == 3) {
                            // lightningZone[i][j][k].getBlock().setType(Material.DIAMOND_BLOCK);
                            World w = lightningZone[i][j][k].getBlock().getLocation().getWorld();
                            w.spawnParticle(
                                    Particle.HEART, // NOTE
                                    lightningZone[i][j][k].getBlock().getLocation(),
                                    1
                            );
                        }
                    }
                }
            }
        }
    }

    /*
     * simulation lightning zone definitions:
     *  - 0 = open
     *  - 1 = blocker
     *  - 2 = explored
     *  - 3 = in final path
     *  - 4 = start
     *  - 5 = target
     */
    public void createLightningStrikeZoneSimulation() {
        simulationLightningZone = new int[lightningZone.length][lightningZone.length][lightningZone.length];
        simulationStrikeStart = new int[3];
        simulationStrikeTarget = new int[3];

        for (int i = 0; i < lightningZone.length; i++) {
            for (int j = 0; j < lightningZone[i].length; j++) {
                for (int k = 0; k < lightningZone[i][j].length; k++) {
                    if (lightningZone[i][j][k].getBlock().getType() == Material.AIR) {
                        simulationLightningZone[i][j][k] = 0;
                    } else {
                        simulationLightningZone[i][j][k] = 1;
                    }

                    if (lightningZone[i][j][k] == strikeStart) {
                        simulationLightningZone[i][j][k] = 4;

                        simulationStrikeStart[0] = i;
                        simulationStrikeStart[1] = j;
                        simulationStrikeStart[2] = k;
                    }

                    if (lightningZone[i][j][k] == strikeTarget) {
                        simulationLightningZone[i][j][k] = 5;

                        simulationStrikeTarget[0] = i;
                        simulationStrikeTarget[1] = j;
                        simulationStrikeTarget[2] = k;
                    }
                }
            }
        }
    }

    public void startLightningStrikeSimulation() {
        createLightningStrikeZoneSimulation();

        Simulation simulation = new Simulation(simulationLightningZone, simulationStrikeStart, simulationStrikeTarget);
        simulation.setup();

        boolean successfulSimulation = simulation.start();

        if (!successfulSimulation) {
            getLogger().info("No possible path found.");
        } else {
            completedSimulationLightningZone = simulation.getSimulationLightningZone();
        }
    }
}
