package me.kw.ee.handlers;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.kw.ee.EarthEngine;

public class ClaimingModeHandler implements Listener {
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		if(EarthEngine.editing.containsKey(e.getPlayer()) && EarthEngine.editing.get(e.getPlayer())) {
			switch(e.getAction()) {
			case LEFT_CLICK_BLOCK:
				if(EarthEngine.editmarkers.containsKey(e.getClickedBlock().getLocation())) {
					e.getClickedBlock().setType(EarthEngine.editmarkers.get(e.getClickedBlock().getLocation()));
					EarthEngine.editmarkers.remove(e.getClickedBlock().getLocation());
					e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
					ArrayList<Location> updated = EarthEngine.editlocations.get(e.getPlayer());
					updated.remove(e.getClickedBlock().getLocation());
					EarthEngine.editlocations.put(e.getPlayer(), updated);
				}
				break;
			case RIGHT_CLICK_BLOCK:
				if(!EarthEngine.editmarkers.containsKey(e.getClickedBlock().getLocation())) {
					EarthEngine.editmarkers.put(e.getClickedBlock().getLocation(), e.getClickedBlock().getType());
					e.getClickedBlock().setType(Material.REDSTONE_BLOCK);
					e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 2);
					if(!EarthEngine.editlocations.containsKey(e.getPlayer())) {
						EarthEngine.editlocations.put(e.getPlayer(), new ArrayList<>());
					}
					ArrayList<Location> updated = EarthEngine.editlocations.get(e.getPlayer());
					updated.add(e.getClickedBlock().getLocation());
					EarthEngine.editlocations.put(e.getPlayer(), updated);
				}
				break;
			}
		}
	}
	
	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent e) {
		if(EarthEngine.editing.containsKey(e.getPlayer()) && EarthEngine.editing.get(e.getPlayer())) {
			e.setCancelled(true);
			if(EarthEngine.editmarkers.containsKey(e.getBlock().getLocation())) {
				e.getBlock().setType(EarthEngine.editmarkers.get(e.getBlock().getLocation()));
				EarthEngine.editmarkers.remove(e.getBlock().getLocation());
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
				ArrayList<Location> updated = EarthEngine.editlocations.get(e.getPlayer());
				updated.remove(e.getBlock().getLocation());
				EarthEngine.editlocations.put(e.getPlayer(), updated);
			}
		}
	}

}
