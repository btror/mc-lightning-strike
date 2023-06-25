package mclightningstrike.mclightningstrike.lightning.pathfinding;

import mclightningstrike.mclightningstrike.McLightningStrike;
import org.apache.logging.log4j.Logger;
import org.bukkit.Location;
import org.bukkit.Material;

import static org.apache.logging.log4j.LogManager.getLogger;

public class Animation {

    private McLightningStrike plugin;
    private Location[][][] lightningZone;
    private Location strikeStart;
    private Location strikeTarget;

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
        generateLightningStrikeSimulation();
    }

    /*
     * simulation lightning zone definitions:
     *  - 0 = open
     *  - 1 = blocker
     *  - 2 = start
     *  - 3 = target
     */
    public void generateLightningStrikeSimulation() {
        int[][][] simulationLightningZone = new int[lightningZone.length][lightningZone.length][lightningZone.length];

        for (int i = 0; i < lightningZone.length; i++) {
            for (int j = 0; j < lightningZone[i].length; j++) {
                for (int k = 0; k < lightningZone[i][j].length; k++) {
                    if (lightningZone[i][j][k].getBlock().getType() == Material.AIR) {
                        simulationLightningZone[i][j][k] = 0;
                    } else {
                        simulationLightningZone[i][j][k] = 1;
                    }

                    if (lightningZone[i][j][k] == strikeStart) {
                        simulationLightningZone[i][j][k] = 3;
                        getLogger().info("strike start");
                        getLogger().info("i: " + i + ", j: " + j + ", k: " + k);
                        getLogger().info(lightningZone[i][j][k]);
                    }
                    if (lightningZone[i][j][k] == strikeTarget) {
                        simulationLightningZone[i][j][k] = 4;
                        getLogger().info("strike target");
                        getLogger().info("i: " + i + ", j: " + j + ", k: " + k);
                        getLogger().info(lightningZone[i][j][k]);
                    }
                }
            }
        }
    }
}
