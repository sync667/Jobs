package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.economy.PaymentData;
import com.gamingmesh.jobs.stuff.TimeManage;

public class limit implements Cmd {

    @Override
    @JobCommand(700)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (args.length != 0 && args.length != 1) {
	    Jobs.getCommandManager().sendUsage(sender, "limit");
	    return true;
	}

	JobsPlayer JPlayer = null;
	if (args.length >= 1)
	    JPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);
	else if (sender instanceof Player)
		JPlayer = Jobs.getPlayerManager().getJobsPlayer((Player) sender);

	boolean disabled = true;
	for (CurrencyType type : CurrencyType.values()) {
	    if (Jobs.getGCManager().getLimit(type).isEnabled()) {
		disabled = false;
		break;
	    }
	}

	if (disabled) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.limit.output.notenabled"));
	    return true;
	}

	if (JPlayer == null) {
	    if (args.length >= 1)
		sender.sendMessage(Jobs.getLanguage().getMessage("general.error.noinfo"));
	    else if (!(sender instanceof Player))
		Jobs.getCommandManager().sendUsage(sender, "limit");
	    return true;
	}

	for (CurrencyType type : CurrencyType.values()) {
	    if (!Jobs.getGCManager().getLimit(type).isEnabled())
		continue;
	    PaymentData limit = JPlayer.getPaymentLimit();
	    if (limit == null) {
		int lefttime1 = Jobs.getGCManager().getLimit(type).getTimeLimit() * 1000;
		sender.sendMessage(Jobs.getLanguage().getMessage("command.limit.output." + type.getName().toLowerCase() + "time", "%time%", TimeManage.to24hourShort((long) lefttime1)));
		sender.sendMessage(Jobs.getLanguage().getMessage("command.limit.output." + type.getName().toLowerCase() + "Limit",
		    "%current%", "0.0",
		    "%total%", JPlayer.getLimit(type)));
		continue;
	    }
	    if (limit.GetLeftTime(type) > 0) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.limit.output." + type.getName().toLowerCase() + "time", "%time%", TimeManage.to24hourShort(limit.GetLeftTime(type))));
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.limit.output." + type.getName().toLowerCase() + "Limit",
		    "%current%", (int) (limit.GetAmount(type) * 100) / 100D,
		    "%total%", JPlayer.getLimit(type)));
	    }
	}
	return true;
    }
}
