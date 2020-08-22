package me.kw.ee.handlers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.JSONObject;

import me.kw.ee.EarthEngine;
import net.sf.corn.httpclient.HttpForm;
import net.sf.corn.httpclient.HttpResponse;

public class PlayerJoinHandler implements Listener {
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		log("&eEvaluating whitelist entry for player " + e.getPlayer().getName());
		try {
			HttpForm client = new HttpForm(new URI(EarthEngine.BASE_URL + "/whitelist"));
			
			client.putFieldValue("playername", e.getPlayer().getName());
			
			HttpResponse response = client.doPost();
			
			JSONObject res = new JSONObject(response.getData());
//			
			JSONObject data = (JSONObject) res.get("data");

			boolean whitelisted = data.getBoolean("whitelisted");
			
			if(whitelisted) {
				log("&2" + e.getPlayer().getName() + " IS whitelisted!");
			} else {
				log("&c" + e.getPlayer().getName() + " IS NOT whitelisted!");
				e.getPlayer().kickPlayer("§cYou have not been whitelisted yet! Please check #how-to-join in the discord.");
			}
			
			
		} catch (URISyntaxException | IOException ex) {
			log("&eWhitelist evaluation returned: &cfalse");
			log("\n\n&4There was a very severe error when trying to connect to the resource server.\n\n");
			ex.printStackTrace();
//			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/stop");
		}
	}
	
	private void log(String in) {
		EarthEngine.log(in);
	}

}
