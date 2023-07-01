package mclightningstrike.mclightningstrike.storm;

import mclightningstrike.mclightningstrike.McLightningStrike;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import static org.apache.logging.log4j.LogManager.getLogger;

public record StormCommand(McLightningStrike plugin) implements CommandExecutor, Listener {

    /**
     * Command listener.
     *
     * @param sender  player who sent the command.
     * @param command command
     * @param label   label
     * @param args    args
     */
    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args) {
        try {
            Player player = (Player) sender;
            Location strikeTarget = new Location(
                    player.getLocation().getWorld(),
                    player.getLocation().getX() + 25,
                    player.getLocation().getY(),
                    player.getLocation().getZ()
            );

            new Storm(plugin, strikeTarget).generate();

            return true;

        } catch (Exception e) {
            getLogger().info(e);
        }
        return false;
    }
}
