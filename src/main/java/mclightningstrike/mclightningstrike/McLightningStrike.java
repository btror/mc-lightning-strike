package mclightningstrike.mclightningstrike;

import mclightningstrike.mclightningstrike.storm.StormCommand;
import mclightningstrike.mclightningstrike.storm.StormListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class McLightningStrike extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("storm strike plugin enabled...");

        StormCommand command = new StormCommand(this);
        Objects.requireNonNull(getCommand("strike")).setExecutor(command);
        getServer().getPluginManager().registerEvents(command, this);

        getServer().getPluginManager().registerEvents(new StormListener(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("storm strike plugin disabled...");
    }
}
