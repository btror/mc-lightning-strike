package mclightningstrike.mclightningstrike;

import mclightningstrike.mclightningstrike.lightning.LightningStrikeCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class McLightningStrike extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("lightning strike plugin enabled...");

        LightningStrikeCommand command = new LightningStrikeCommand(this);
        Objects.requireNonNull(getCommand("strike")).setExecutor(command);
        getServer().getPluginManager().registerEvents(command, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("lightning strike plugin disabled...");
    }
}
