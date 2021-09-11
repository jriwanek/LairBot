/*
 * MMDBot - https://github.com/MinecraftModDevelopment/MMDBot
 * Copyright (C) 2016-2021 <MMD - MinecraftModDevelopment>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 * https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 */
package com.mcmoddev.mmdbot.modules.commands.server.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mcmoddev.mmdbot.MMDBot;
import com.mcmoddev.mmdbot.utilities.Utils;
import com.mcmoddev.mmdbot.utilities.oldchannels.OldChannelsHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The type Cmd old channels.
 */
public final class CmdOldChannels extends Command {

    /**
     * Instantiates a new Cmd old channels.
     */
    public CmdOldChannels() {
        super();
        name = "old-channels";
        help = "Gives channels which haven't been used in an amount of days given as an argument (default 60)."
            + "Usage: " + MMDBot.getConfig().getMainPrefix() + "old-channels [threshold] "
            + "[channel or category list, separated by spaces]";
        hidden = true;
    }

    /**
     * Execute.
     *
     * @param event the event
     */
    @Override
    protected void execute(final CommandEvent event) {
        final var guild = event.getGuild();
        final var embed = new EmbedBuilder();
        final var outputChannel = event.getTextChannel();
        // I have to do this, so we can use `remove` later. williambl
        final List<String> args = new ArrayList<>(Arrays.asList(event.getArgs().split(" ")));
        if (!Utils.checkCommand(this, event)) {
            outputChannel.sendMessage("This command is channel locked.").queue();
            return;
        }

        if (!OldChannelsHelper.isReady()) {
            outputChannel.sendMessage("Command is still setting up. Please try again in a few moments.").queue();
            return;
        }

        final int dayThreshold = args.size() > 0 && args.get(0).matches("-?\\d+")
            ? Integer.parseInt(args.remove(0)) : 60;

        List<String> toCheck = args.stream().map(it -> it.toLowerCase(Locale.ROOT)).collect(Collectors.toList());

        embed.setTitle("Days since last message in channels:");
        embed.setColor(Color.YELLOW);

        guild.getTextChannels().stream()
            .distinct()
            .filter(channelIsAllowedByList(toCheck))
            .map(channel -> new ChannelData(channel, OldChannelsHelper.getLastMessageTime(channel)))
            .forEach(channelData -> {
                if (channelData.days > dayThreshold) {
                    embed.addField("#" + channelData.channel.getName(),
                        String.valueOf(channelData.days), true);
                } else if (channelData.days == -1) {
                    embed.addField("#" + channelData.channel.getName(), "Never had a message", true);
                }
            });

        outputChannel.sendMessageEmbeds(embed.build()).queue();
    }

    /**
     * Channel is allowed by list predicate.
     *
     * @param list the list
     * @return the predicate
     */
    private Predicate<TextChannel> channelIsAllowedByList(final List<String> list) {
        return (channel) -> list.isEmpty() || (channel.getParent() != null
            && list.contains(channel.getParent().getName().toLowerCase(Locale.ROOT)
            .replace(' ', '-'))) || list.contains(channel.getName().toLowerCase(Locale.ROOT));
    }

    /**
     * The type Channel data.
     */
    private record ChannelData(TextChannel channel, long days) {

        /**
         * Instantiates a new Channel data.
         *
         * @param channel the channel
         * @param days    the days
         */
        private ChannelData {
        }
    }
}
