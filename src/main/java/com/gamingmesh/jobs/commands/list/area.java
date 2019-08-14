package com.gamingmesh.jobs.commands.list;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.ItemManager.CMIMaterial;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.config.RestrictedAreaManager;
import com.gamingmesh.jobs.container.CuboidArea;
import com.gamingmesh.jobs.container.RestrictedArea;

public class area implements Cmd {

    @Override
    @JobCommand(300)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (!(sender instanceof Player)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.ingame"));
	    return false;
	}
	Player player = (Player) sender;

	RestrictedAreaManager ra = Jobs.getRestrictedAreaManager();

	if (args.length == 3) {
	    if (args[0].equalsIgnoreCase("add")) {
		if (!Jobs.hasPermission(player, "jobs.area.add", true))
		    return true;
		String name = args[1];
		double bonus = 0D;
		try {
		    bonus = Double.parseDouble(args[2]);
		} catch (Throwable e) {
		    return false;
		}
		boolean wg = false;

		if (name.startsWith("wg:")) {
		    wg = true;
		    name = name.substring("wg:".length(), name.length());
		}

		if (ra.isExist(name)) {
		    sender.sendMessage(Jobs.getLanguage().getMessage("command.area.output.exist"));
		    return true;
		}

		if (!wg && !Jobs.getSelectionManager().hasPlacedBoth(player)) {
		    sender.sendMessage(Jobs.getLanguage().getMessage("command.area.output.select", "%tool%", CMIMaterial.get(Jobs.getGCManager().getSelectionTool()).getName()));
		    return true;
		}

		if (wg && Jobs.getWorldGuardManager() != null) {
		    name = Jobs.getWorldGuardManager().getNameByName(name);
		    if (name == null) {
			sender.sendMessage(Jobs.getLanguage().getMessage("command.area.output.wgDontExist"));
			return true;
		    }
		}

		if (!wg)
		    ra.addNew(new RestrictedArea(name, Jobs.getSelectionManager().getSelectionCuboid(player), bonus), true);
		else
		    ra.addNew(new RestrictedArea(name, name, bonus), true);
		sender.sendMessage(Jobs.getLanguage().getMessage("command.area.output.addedNew", "%bonus%", bonus));
		return true;
	    }
	}

	if (args.length == 2) {
	    if (args[0].equalsIgnoreCase("remove")) {
		if (!Jobs.hasPermission(player, "jobs.area.remove", true))
		    return true;

		String name = args[1];

		if (!ra.isExist(name)) {
		    sender.sendMessage(Jobs.getLanguage().getMessage("command.area.output.dontExist"));
		    return true;
		}

		ra.remove(name);
		sender.sendMessage(Jobs.getLanguage().getMessage("command.area.output.removed", "%name%", name));
		return true;
	    }
	}

	if (args.length == 1 && args[0].equalsIgnoreCase("info")) {

	    List<RestrictedArea> areas = Jobs.getRestrictedAreaManager().getRestrictedAreasByLoc(player.getLocation());

	    String msg = "";

	    for (RestrictedArea area : areas) {
		if (!msg.isEmpty())
		    msg += ", ";
		msg += area.getName();
	    }

	    if (msg.isEmpty()) {
		sender.sendMessage(Jobs.getLanguage().getMessage("command.area.output.noAreasByLoc"));
		return true;
	    }
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.area.output.areaList", "%list%", msg));
	    return true;
	}

	if (args.length == 1 && args[0].equalsIgnoreCase("list")) {

	    HashMap<String, RestrictedArea> areas = Jobs.getRestrictedAreaManager().getRestrictedAres();
	    if (areas.isEmpty()) {
		sender.sendMessage(Jobs.getLanguage().getMessage("command.area.output.noAreas"));
		return true;
	    }

	    sender.sendMessage(Jobs.getLanguage().getMessage("general.info.separator"));
	    int i = 0;
	    for (Entry<String, RestrictedArea> area : areas.entrySet()) {
		i++;
		CuboidArea cuboid = area.getValue().getCuboidArea();
		if (area.getValue().getWgName() == null) {
		    sender.sendMessage(Jobs.getLanguage().getMessage("command.area.output.list", "%number%", i,
			"%areaname%", area.getKey(),
			"%worldname%", cuboid.getWorld().getName(),
			"%x1%", cuboid.getLowLoc().getBlockX(),
			"%y1%", cuboid.getLowLoc().getBlockY(),
			"%z1%", cuboid.getLowLoc().getBlockZ(),
			"%x2%", cuboid.getHighLoc().getBlockX(),
			"%y2%", cuboid.getHighLoc().getBlockY(),
			"%z2%", cuboid.getHighLoc().getBlockZ(),
			"%bonus%", area.getValue().getMultiplier()));
		} else {
		    sender.sendMessage(Jobs.getLanguage().getMessage("command.area.output.wgList", "%number%", i,
			"%areaname%", area.getKey(),
			"%bonus%", area.getValue().getMultiplier()));
		}
	    }
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.info.separator"));
	    return true;
	}

	if (args.length > 0) {
	    if (args[0].equalsIgnoreCase("add")) {
		sender.sendMessage(Jobs.getLanguage().getMessage("command.area.help.addUsage"));
		return true;
	    }

	    if (args[0].equalsIgnoreCase("remove")) {
		sender.sendMessage(Jobs.getLanguage().getMessage("command.area.help.removeUsage"));
		return true;
	    }
	}

	return false;
    }

}
