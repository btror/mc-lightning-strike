package mclightningstrike.mclightningstrike.lightning;

import mclightningstrike.mclightningstrike.McLightningStrike;
import mclightningstrike.mclightningstrike.lightning.pathfinding.Animation;
import org.bukkit.Location;
import org.bukkit.Material;

public class Lightning {

    private McLightningStrike plugin;
    private Location strikeTarget;
    private Location strikeStart;

    public Lightning(McLightningStrike plugin, Location strikeTarget) {
        this.plugin = plugin;
        this.strikeTarget = strikeTarget;
    }

    public void strike() {
        Location[][][] lightningZone = generateLightningZone();
        new Animation(plugin, lightningZone, strikeStart, strikeTarget).start();

        strikeTarget.getBlock().setType(Material.GOLD_BLOCK);
        strikeStart.getBlock().setType(Material.GOLD_BLOCK);
    }

    public Location[][][] generateLightningZone() {
        Location[][][] lightningZone = new Location[20][20][20];
        // i is width, j is height, k is length
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
