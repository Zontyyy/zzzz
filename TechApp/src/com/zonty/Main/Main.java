package com.zonty.Main;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    // Fired when plugin is first enabled
    
    
    @Override
    public void onEnable() {
    	new CropDest(this);
    	this.getCommand("trample").setExecutor(new CommandShanenigans(this));
    }
    
    // Fired when plugin is disabled
    @Override
    public void onDisable() {

    }
    
}
