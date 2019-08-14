package com.gamingmesh.jobs.McMMO;

import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.ItemActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityDeactivateEvent;
import com.gmail.nossr50.events.skills.repair.McMMOPlayerRepairCheckEvent;

public class McMMO2_X_listener implements Listener {

    private Jobs plugin;

    public McMMO2_X_listener(Jobs plugin) {
	this.plugin = plugin;
    }

    @EventHandler
    public void OnItemrepair(McMMOPlayerRepairCheckEvent event) {
	//disabling plugin in world
	if (event.getPlayer() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getPlayer().getWorld()))
	    return;
	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	Player player = event.getPlayer();

	ItemStack resultStack = event.getRepairedObject();

	if (resultStack == null)
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld().getName()))
	    return;

	// check if in creative
	if (player.getGameMode().equals(GameMode.CREATIVE) && !player.hasPermission("jobs.paycreative") && !Jobs.getGCManager().payInCreative())
	    return;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
	if (jPlayer == null)
	    return;

	Jobs.action(jPlayer, new ItemActionInfo(resultStack, ActionType.REPAIR));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void OnAbilityOn(McMMOPlayerAbilityActivateEvent event) {
	HashMap<String, Long> InfoMap = Jobs.getMcMMOManager().getMap().get(event.getPlayer().getUniqueId());
	if (InfoMap == null) {
	    InfoMap = new HashMap<>();
	    Jobs.getMcMMOManager().getMap().put(event.getPlayer().getUniqueId(), InfoMap);
	}
	InfoMap.put(event.getAbility().toString(), System.currentTimeMillis() + (30 * 1000));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void OnAbilityOff(McMMOPlayerAbilityDeactivateEvent event) {
	HashMap<String, Long> InfoMap = Jobs.getMcMMOManager().getMap().get(event.getPlayer().getUniqueId());
	if (InfoMap != null) {
	    InfoMap.remove(event.getAbility().toString());
	    if (InfoMap.isEmpty())
		Jobs.getMcMMOManager().getMap().remove(event.getPlayer().getUniqueId());
	}
    }

}
