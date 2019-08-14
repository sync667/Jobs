package com.gamingmesh.jobs.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.commands.Cmd;
import com.gamingmesh.jobs.commands.JobCommand;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;

public class fire implements Cmd {

    @Override
    @JobCommand(1900)
    public boolean perform(Jobs plugin, final CommandSender sender, final String[] args) {
	if (args.length < 2) {
	    Jobs.getCommandManager().sendUsage(sender, "fire");
	    return true;
	}

	JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(args[0]);

	Job job = Jobs.getJob(args[1]);
	if (job == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.job"));
	    return true;
	}

	if (jPlayer == null) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.error.noinfoByPlayer", "%playername%", args[0]));
	    return true;
	}

	if (!jPlayer.isInJob(job)) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("command.fire.error.nojob", "%jobname%", job.getChatColor() + job.getName()));
	    return true;
	}
	try {
	    Jobs.getPlayerManager().leaveJob(jPlayer, job);
	    Player player = jPlayer.getPlayer();
	    if (player != null)
		player.sendMessage(Jobs.getLanguage().getMessage("command.fire.output.target", "%jobname%", job.getChatColor() + job.getName()));

	    sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.success"));
	} catch (Throwable e) {
	    sender.sendMessage(Jobs.getLanguage().getMessage("general.admin.error"));
	}
	return true;
    }
}
