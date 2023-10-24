package com.zonty.Main;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class CommandShanenigans implements CommandExecutor{
	
	private final Main plugin;
	
	public CommandShanenigans (Main plugin) {
		this.plugin = plugin;
	}
	
	
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (sender instanceof Player) {
			
			Player player = (Player) sender;
			
	    	String name = args[0];
	    	Integer val = Integer.parseInt(args[1]);
	    	Player victim = Bukkit.getPlayer(name);
	    	
	    	if (val > 3) {
	    		player.sendMessage("The only possible values are 0-3");
	    		return false;
	    	}
	    		
	    	
	    	if (val > 1 & !player.hasPermission("techapp.ttoggle")) {
	    		player.sendMessage("Permit of that level is grantable only by the qualified persons"); 
	    		return false; 
	    	}
	    	
	    	NamespacedKey namespacedKey = new NamespacedKey(plugin, "tramping");
			PersistentDataContainer data = victim.getPersistentDataContainer();
			Integer grr = 0;
			if (data.has(namespacedKey, PersistentDataType.INTEGER)) 
				grr = data.get(namespacedKey, PersistentDataType.INTEGER);
			if (grr > 1 & !player.hasPermission("techapp.ttoggle")) return false;
	
			data.set(namespacedKey, PersistentDataType.INTEGER, val);
	    	
	    	
	    	
	    	
		}
			
		else {
			String name = args[0];
	    	Integer val = Integer.parseInt(args[1]);
	    	Player victim = Bukkit.getPlayer(name);
	    	
	    	// The power of untold..
	    	
	    	NamespacedKey namespacedKey = new NamespacedKey(plugin, "tramping");
			PersistentDataContainer data = victim.getPersistentDataContainer();
			data.set(namespacedKey, PersistentDataType.INTEGER, val);
		}
		
		return true;
    }
}
