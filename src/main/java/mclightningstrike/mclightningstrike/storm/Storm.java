package mclightningstrike.mclightningstrike.storm;

import mclightningstrike.mclightningstrike.McLightningStrike;
import mclightningstrike.mclightningstrike.storm.pathfinding.Animation;
import org.bukkit.Location;
import org.bukkit.Material;

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
     *
     * @param scatteredStorm a very large storm.
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
                        randomSize
                );
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
                strikeTarget.getZ() + size
        );
        while (location.getBlock().getType() != Material.AIR) {
            double newY = location.getY() + 1.0;
            location = new Location(
                    location.getWorld(),
                    location.getX(),
                    newY,
                    location.getZ()
            );
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
                            strikeTarget.getZ() + (z / 2.0) - k
                    );
                    stormZone[i][j][k] = location;
                }
            }
        }

        Location strikeStart = stormZone[(int) (Math.random() * x - 1)][y - 1][(int) (Math.random() * z - 1)];
        strikeTarget = stormZone[x / 2][0][z / 2];

        new Animation(plugin, stormZone, strikeStart, strikeTarget).start();
    }
}
