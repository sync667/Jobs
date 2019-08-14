package com.gamingmesh.jobs.commands.list;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.ItemBoostManager;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.BoostMultiplier;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobItems;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.stuff.ChatColor;

public class edititembonus implements Cmd {

    private enum actions {
	list, add, remove;
	public static actions getByname(String name) {
	    for (actions one : actions.values()) {
		if (one.name().equalsIgnoreCase(name))
		    return one;
	    }
	    return null;
	}
    }

    @Override
    @JobCommand(300)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return false;
	}

	if (args.length < 1)
	    return false;

	actions action = null;
//	Job job = null;
	JobItems jobitem = null;

	for (String one : args) {
	    if (action == null) {
		action = actions.getByname(one);
		if (action != null)
		    continue;
	    }
//	    if (job == null) {
//		job = Jobs.getJob(one);
//		if (job != null)
//		    continue;
//	    }

//	    if (job != null) {
	    jobitem = ItemBoostManager.getItemByKey(one);
//	    }
	}

	if (action == null)
	    return false;

	Player player = (Player) sender;

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);

	if (jPlayer == null)
	    return false;

	ItemStack iih = Jobs.getNms().getItemInMainHand(player);

	if (iih == null || iih.getType().equals(Material.AIR))
	    return false;

	switch (action) {
	case add:
	    if (jobitem == null)
		return false;
	    iih = Jobs.getReflections().setNbt(iih, "JobsItemBoost", jobitem.getNode());
	    Jobs.getNms().setItemInMainHand(player, iih);
	    break;
	case list:
	    break;
	case remove:
	    iih = Jobs.getReflections().removeNbt(iih, "JobsItemBoost");
	    Jobs.getNms().setItemInMainHand(player, iih);
	    break;
	default:
	    break;

	}

	sender.sendMessage(Jobs.getLanguage().getMessage("command.bonus.output.topline"));

	Object key = Jobs.getReflections().getNbt(iih, "JobsItemBoost");

	if (key == null)
	    return true;

	JobItems item = ItemBoostManager.getItemByKey(key.toString());

	if (item == null)
	    return true; 

	BoostMultiplier boost = item.getBoost();

	for (Job one : item.getJobs()) {
	    String msg = Jobs.getLanguage().getMessage("command.itembonus.output.list",
		"[jobname]", one.getName(),
		"%money%", mc + formatText((int) (boost.get(CurrencyType.MONEY) * 100)),
		"%points%", pc + formatText((int) (boost.get(CurrencyType.POINTS) * 100)),
		"%exp%", ec + formatText((int) (boost.get(CurrencyType.EXP) * 100)));
	    sender.sendMessage(msg);
	}
	return true;
    }

    String mc = ChatColor.DARK_GREEN.toString();
    String pc = ChatColor.GOLD.toString();
    String ec = ChatColor.YELLOW.toString();

    private static String formatText(double amount) {
	return ((amount > 0 ? "+" : "") + amount + "%");
    }

}
