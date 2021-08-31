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
package com.mcmoddev.mmdbot.modules.commands.general.info;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mcmoddev.mmdbot.core.Utils;
import com.mcmoddev.mmdbot.utilities.updatenotifiers.forge.ForgeVersionHelper;
import com.mcmoddev.mmdbot.utilities.updatenotifiers.forge.MinecraftForgeVersion;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;
import java.time.Instant;

/**
 * The type Cmd forge version.
 *
 * @author
 */
public final class CmdForgeVersion extends Command {

    /**
     * Instantiates a new Cmd forge version.
     */
    public CmdForgeVersion() {
        super();
        this.name = "forge";
        aliases = new String[]{"forgev"};
        help = "Get forge versions for latest Minecraft version";
    }

    /**
     * Execute.
     *
     * @param event The {@link CommandEvent CommandEvent} that triggered this Command.
     */
    @Override
    protected void execute(final CommandEvent event) {
        if (!Utils.checkCommand(this, event)) {
            return;
        }

        final var channel = event.getTextChannel();
        MinecraftForgeVersion latest;
        try {
            latest = ForgeVersionHelper.getLatestMcVersionForgeVersions();
        } catch (Exception ex) {
            channel.sendMessage("Unable to get forge versions.").queue();
            ex.printStackTrace();
            return;
        }

        final var latestForgeVersion = latest.getForgeVersion();
        final String latestForge = latestForgeVersion.getLatest();
        String recommendedForge = latestForgeVersion.getRecommended();
        if (recommendedForge == null) {
            recommendedForge = "none";
        }
        final String changelogLink = Utils.makeHyperlink("Changelog",
            String.format("https://files.minecraftforge.net/maven/net/minecraftforge/forge/%1$s-%2$s/forge-%1$s-%2$s-changelog.txt",
                latest.getMcVersion(), latest.getForgeVersion().getLatest()));
        final var embed = new EmbedBuilder();

        embed.setTitle(String.format("Forge Versions for MC %s", latest.getMcVersion()));
        embed.addField("Latest", latestForge, true);
        embed.addField("Recommended", recommendedForge, true);
        embed.setDescription(changelogLink);
        embed.setColor(Color.ORANGE);
        embed.setTimestamp(Instant.now());
        channel.sendMessageEmbeds(embed.build()).queue();
    }
}
