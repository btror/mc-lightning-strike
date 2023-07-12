package mclightningstrike.mclightningstrike;

import mclightningstrike.mclightningstrike.storm.StormListener;
import org.bukkit.plugin.java.JavaPlugin;

public class McLightningStrike extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new StormListener(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("mc-lightning-strike disabled...");
    }
}
