package com.mcmoddev.mmdbot.commands.staff;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mcmoddev.mmdbot.MMDBot;
import com.mcmoddev.mmdbot.core.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public class CmdMute extends Command {

    public CmdMute() {
        super();
        name = "mute";
        help = "Mutes a user. Usage: !mmd-mute <userID/mention> [time, otherwise forever] [unit, otherwise minutes]";
        hidden = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        final Guild guild = event.getGuild();
        final MessageChannel channel = event.getChannel();
        final String[] args = event.getArgs().split(" ");
        final Member author = event.getGuild().getMember(event.getAuthor());
        final Member member = Utils.getMemberFromString(args[0], event.getGuild());
        final Role mutedRole = guild.getRoleById(MMDBot.getConfig().getRoleMuted());
        final TextChannel consoleChannel = guild.getTextChannelById(MMDBot.getConfig().getChannelIDConsole());

        if (author.hasPermission(Permission.KICK_MEMBERS)) {
            if (member == null) {
                channel.sendMessage(String.format("User %s not found.", event.getArgs())).queue();
                return;
            }

            if (mutedRole == null) {
                MMDBot.LOGGER.error("Unable to find muted role!");
                return;
            }

            final long time;
            final TimeUnit unit;
            if (args.length > 1) {
                long time1;
                try {
                    time1 = Long.parseLong(args[1]);
                } catch (NumberFormatException e) {
                    time1 = -1;
                }
                time = time1;
            } else {
                time = -1;
            }

            if (args.length > 2) {
                TimeUnit unit1;
                try {
                    unit1 = TimeUnit.valueOf(args[2].toUpperCase());
                } catch (IllegalArgumentException e) {
                    unit1 = TimeUnit.MINUTES;
                }
                unit = unit1;
            } else {
                unit = TimeUnit.MINUTES;
            }

            guild.addRoleToMember(member, mutedRole).queue();

            if (time > 0) {
                guild.removeRoleFromMember(member, mutedRole).queueAfter(time, unit);
            }

            final String timeString;
            if (time > 0) {
                timeString = " " + time + " " + unit.toString().toLowerCase();
            } else {
                timeString = "ever";
            }

            channel.sendMessageFormat("Muted user %s for%s.", member.getAsMention(), timeString).queue();
            consoleChannel.sendMessageFormat("Muted user %s for%s", member.getAsMention(), timeString).queue();
        } else {
            channel.sendMessage("You do not have permission to use this command.").queue();
        }
    }
}