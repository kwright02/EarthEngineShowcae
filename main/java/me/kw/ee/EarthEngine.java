
package me.kw.ee;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import me.kw.ee.handlers.ClaimingModeHandler;
import me.kw.ee.handlers.PlayerJoinHandler;
import net.sf.corn.httpclient.HttpClient;
import net.sf.corn.httpclient.HttpClient.HTTP_METHOD;
import net.sf.corn.httpclient.HttpForm;
import net.sf.corn.httpclient.HttpResponse;

public class EarthEngine extends JavaPlugin implements Listener {
	
	public static final int RESOURCE_SERVER_PORT = 6000;
	public static final String RESOURCE_SERVER_IP = "redacted",
			BASE_URL = "http://" + RESOURCE_SERVER_IP + ":" + RESOURCE_SERVER_PORT;
	
	public static HashMap<Player, Boolean> editing = new HashMap<>();
	
	public static HashMap<Location, Material> editmarkers = new HashMap<>();
	
	public static HashMap<Player, ArrayList<Location>> editlocations = new HashMap<>();
	
	public static PlayerJoinHandler pjh = new PlayerJoinHandler();
	public static ClaimingModeHandler cmh = new ClaimingModeHandler();
	
	@Override
	public void onEnable() {
		log("&eChecking connection to resource server....");
		try {
			HttpClient client = new HttpClient(new URI(BASE_URL + "/connection"));
			
			HttpResponse response = client.sendData(HTTP_METHOD.GET);
			
			if(response.getCode() != 200) {
				log("\n\n&4There was a very severe error when trying to connect to the resource server. Shutting down.\n\n");
//				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/stop");
				return;
			}
			
			log("&2Came back with response &9" + response.getCode());
			
			Bukkit.getPluginManager().registerEvents(pjh, this);
			Bukkit.getPluginManager().registerEvents(cmh, this);
			
		} catch (URISyntaxException | IOException e) {
			log("\n\n&4There was a very severe error when trying to connect to the resource server. Shutting down.\n\n");
			e.printStackTrace();
//			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/stop");
		}
	}
	
	@Override
	public void onDisable() {
		for(Location l: editmarkers.keySet()) {
			l.getBlock().setType(editmarkers.get(l));
		}
	}
	
	public static void log(String in) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&2[&bEarth Engine&2] " + in));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player p = (Player) sender;
		if(command.getName().equals("cl") || command.getName().equals("clear")) {
			log("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		} else if(command.getName().equalsIgnoreCase("nation")) {
			if(args.length == 0) {
				p.sendMessage("implement later");
				return true;
			}
			switch(args[0]) {
				case "verify":
					try {
						HttpForm client = new HttpForm(new URI(EarthEngine.BASE_URL + "/claim-nation"));
						
						client.putFieldValue("playername", p.getName());
						
						HttpResponse response = client.doPost();

						
						p.sendMessage("§2We've verified that you are the leader, assigning roles now...");
						JSONObject base = new JSONObject(response.getData());
						JSONObject res = (JSONObject) base.get("data");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex group " + res.getString("name") + " set prefix '&3" + res.getString("name") + " '");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + p.getName() + " group add " + res.getString("name"));
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + p.getName() + " group add leader");						
						
					} catch (URISyntaxException | IOException  e) {
						p.sendMessage("error");
						e.printStackTrace();
					}
				case "claim":
					if(p.hasPermission("ee.land.modify") && args.length == 1) {
						if(editing.containsKey(p)) {
							editing.put(p, !editing.get(p));
						} else {
							editing.put(p, true);
						}
						p.sendMessage("§eClaim editing mode set to: " + editing.get(p));
					} else if(p.hasPermission("ee.land.modify") && args.length == 2 && args[1].equals("set")) {
						p.sendMessage("§eChecking border validity.....");
						Location[] locs = editlocations.get(p).toArray(new Location[editlocations.get(p).size()]);
						for(int i = editlocations.get(p).size()-1; i != 1; i--) {
							Location last = locs[i], next = locs[i-1];
							double dist = last.distance(next);
							if(dist != 1) {
								p.sendMessage("§cYour border is missing a block where a diagonal corner is present.");
								break;
							}
						}
						Location first = locs[0], last = locs[locs.length-1];
						double dist = first.distance(last);
						if(dist != 1) {
							p.sendMessage("§cYour border is not a closed loop.");
							return true;
						}
						p.sendMessage("§aYour border was verified, displaying final border now.... (editing disabled");
						editing.put(p, false);
						for(Location l: editlocations.get(p)) {
							l.getBlock().setType(Material.EMERALD_BLOCK);
						}
						p.sendMessage("§9Final border is displayed, type '/nation claim confirm' to finalize");
					}
			}
		}
		return false;
	}
	
	

}
