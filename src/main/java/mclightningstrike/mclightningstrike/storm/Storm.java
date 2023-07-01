package mclightningstrike.mclightningstrike.storm;

import mclightningstrike.mclightningstrike.McLightningStrike;
import mclightningstrike.mclightningstrike.storm.pathfinding.Animation;
import org.bukkit.Location;

public class Storm {

    private final McLightningStrike plugin;
    private Location strikeTarget;
    private Location strikeStart;

    /**
     * Constructor.
     * <p>
     * Creates a storm.
     *
     * @param plugin       McLightningStrike instance
     * @param strikeTarget location of storm bolt target
     */
    public Storm(McLightningStrike plugin, Location strikeTarget) {
        this.plugin = plugin;
        this.strikeTarget = strikeTarget;
    }

    /**
     * Strikes storm
     * <p>
     * Storm Zone Mappings:
     * i = length
     * j = height
     * k = width
     */
    public void generate() {
        Location[][][] lightningZone = createStormZone();
        new Animation(plugin, lightningZone, strikeStart, strikeTarget).start();
    }

    /**
     * Creates storm zone (LxWxH).
     * <p>
     * Storm Zone Mappings:
     * i = length
     * j = height
     * k = width
     *
     * @return storm location.
     */
    private Location[][][] createStormZone() {
        Location[][][] lightningZone = new Location[20][20][20];
        for (int i = 0; i < lightningZone.length; i++) {
            for (int j = 0; j < lightningZone[i].length; j++) {
                for (int k = 0; k < lightningZone[i][j].length; k++) {
                    Location location = new Location(
                            strikeTarget.getWorld(),
                            strikeTarget.getX() - 10 + i,
                            strikeTarget.getY() + 0 + j,
                            strikeTarget.getZ() + 10 - k
                    );
                    lightningZone[i][j][k] = location;
                }
            }
        }

        strikeStart = lightningZone[(int) (Math.random() * 19)][19][(int) (Math.random() * 19)];
        strikeTarget = lightningZone[10][0][10];

        return lightningZone;
    }
}
