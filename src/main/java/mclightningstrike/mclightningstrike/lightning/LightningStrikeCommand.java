package mclightningstrike.mclightningstrike.lightning;


import mclightningstrike.mclightningstrike.McLightningStrike;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import static org.apache.logging.log4j.LogManager.getLogger;

public record LightningStrikeCommand(McLightningStrike plugin) implements CommandExecutor, Listener {
    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args)
    {
        try {
            Player player = (Player) sender;

            Location strikeTarget = new Location(
                    player.getLocation().getWorld(),
                    player.getLocation().getX() + 25,
                    player.getLocation().getY(),
                    player.getLocation().getZ()
            );

            new Lightning(plugin, strikeTarget).strike();

            player.sendMessage("strike at: " + strikeTarget);

            return true;

        } catch (Exception e) {
            getLogger().info(e);
        }
        return false;
    }
}
