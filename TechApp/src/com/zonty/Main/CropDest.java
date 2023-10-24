package com.zonty.Main;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class CropDest implements Listener {
	
	public static Dictionary<String, Integer> drop_dict = new Hashtable();
	
	public static Main instance;
	
	
	/////
	/////
	///// Main initialization
	/////
	/////
	
	public CropDest(Main plugin) {
		
		instance = plugin;
		
		// Initialization of drop_dictionary
		
		drop_dict.put("WOODEN_HOE", 1);
		drop_dict.put("STONE_HOE", 2);
		drop_dict.put("GOLDEN_HOE", 3);
		drop_dict.put("DIAMOND_HOE", 4);
		drop_dict.put("NETHERITE_HOE", 5);
		
		// Registration of the listener
		
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	
	/////
	/////
	///// Helping Functions
	/////
	/////	
	
	private int addEnchantments(ItemStack hoe) {	
		double lvl = hoe.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);	
		
		int bonus = 0;
		
//		I just stole some formula from minecraft wiki which seemed closest to the truth
		
		double default_chance = (2 / (lvl + 2));
		double chance_per_lvl = (1-default_chance) / lvl;
		int min = 1; int max = 100;
		double rnd = (int)Math.floor(Math.random() * (max - min + 1) + min);
		
		
		double probability = rnd/100;
		
		
		probability -= default_chance;
		

		 
		while (probability>0) {
			probability -= chance_per_lvl; 
			bonus++; 
		}

		
		return bonus;
	}
	
	
	private boolean isPermitted(Main plugin, Player player) {
		
		NamespacedKey namespacedKey = new NamespacedKey(plugin, "tramping");
		PersistentDataContainer data = player.getPersistentDataContainer();
		
		if (!data.has(namespacedKey, PersistentDataType.INTEGER)) 
			data.set(namespacedKey, PersistentDataType.INTEGER, 0);
		
		Integer lvl = data.get(namespacedKey, PersistentDataType.INTEGER);
		
		
		if (lvl == 0 || lvl == 2) return false;
		
		return true;
	}
	
	/////
	/////
	///// A lot of EventHandlers
	/////
	/////


	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();
		
		ItemStack item = player.getInventory().getItemInMainHand();
		
		
		// If the broken block is wheat
		
		if (block.getType().name() == "WHEAT") {
			
			
			block.setType(Material.AIR);
			block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.WHEAT_SEEDS, 3));
			
			//////
			// Get the strength of the currently used instrument and drop the wheat accordingly
			//////
			Integer number = drop_dict.get(item.getType().name());
			
			number += addEnchantments(item);
			
			block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.WHEAT, number));
			
			//////
			// calculate the chance of rare drop and drop it accordingly
			//////
			
			int min = 1; int max = 100;
			Integer rare_drop = 0;
			if ((number / 1000) * (int)Math.floor(Math.random() * (max - min + 1) + min) >= 1) rare_drop++;
			
			block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.WITHER_SKELETON_SKULL, rare_drop));
			
			//////
			// We cancel the event since it's been handled manually
			//////
			
			event.setCancelled(true);
		}
		
		
		
	}
	
	// Additional handlers to prevent circumvention of the plugin
	
	@EventHandler
	public void onPistonExtension(BlockPistonExtendEvent event) {
		List<Block> blocks = event.getBlocks();
		if (blocks.get(0).getType().name() == "WHEAT") event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockFromToEvent(BlockFromToEvent event) {
		Block block = event.getToBlock();
		if (block.getType() == Material.WHEAT) event.setCancelled(true);
	}
	
	@EventHandler 
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();		
		boolean p = isPermitted(instance, player);
		
		if (event.getAction() == Action.PHYSICAL & !p) event.setCancelled(true);
	}
	
	
	// I feel like the following is way too memory expensive. P. S. actually, might not be that bad
	// I also opted to vaporize anyone who touches my crops.
	// Why?
	// No reason.
	
	@EventHandler 
	public void onEntityInteractEvent(EntityInteractEvent event) {
		if (event.getBlock().getType() == Material.FARMLAND) event.setCancelled(true); event.getEntity().remove();
	}
	
	// Ideally it's better to also check whether the farmland has crops on it, though, meh. Whatever.
	
	// The only remaining route to bypass the plugin which i thought of was the use of explosives,
	// but like.. If someone is willing to waste the coveted gunpowder on that, i'd just let them
	// :p
	// Otherwise it can be easily fixed by an additional event handler
}
