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
package com.mcmoddev.mmdbot.utilities.tricks;

import com.google.common.collect.Lists;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * The type Embed trick.
 *
 * @author williambl
 */
public class EmbedTrick implements Trick {

    /**
     * Name of the trick.
     */
    private final List<String> names;

    /**
     * Trick title.
     */
    private final String title;

    /**
     * The description of the trick.
     */
    private final String description;

    /**
     * The embed edge color for the trick.
     */
    private final int color;

    /**
     * The Fields.
     */
    private final List<MessageEmbed.Field> fields;

    /**
     * Instantiates a new Embed trick.
     *
     * @param names       Name of the trick.
     * @param title       Trick title.
     * @param description The description of the trick.
     * @param color       The embed edge color for the trick.
     * @param fields      the fields
     */
    public EmbedTrick(final List<String> names, final String title, final String description, final int color,
                      final MessageEmbed.Field... fields) {
        this.names = names;
        this.title = title;
        this.description = description;
        this.color = color;
        this.fields = Arrays.asList(fields);
    }

    /**
     * Gets names.
     *
     * @return The names of the tricks.
     */
    @Override
    public List<String> getNames() {
        return names;
    }

    /**
     * Gets message.
     *
     * @param args the args
     * @return message
     */
    @Override
    public Message getMessage(final String[] args) {
        EmbedBuilder builder = new EmbedBuilder()
            .setTitle(getTitle())
            .setDescription(getDescription())
            .setColor(color);
        for (MessageEmbed.Field field : getFields()) {
            builder.addField(field);
        }
        return new MessageBuilder(builder.build()).setAllowedMentions(Set.of(Message.MentionType.CHANNEL, Message.MentionType.EMOTE)).build();
    }

    /**
     * Gets description.
     *
     * @return Get the trick's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets title.
     *
     * @return Get the title of the trick.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets color.
     *
     * @return Get the embed color for the trick.
     */
    public int getColor() {
        return color;
    }

    /**
     * Gets fields.
     *
     * @return fields fields
     */
    public List<MessageEmbed.Field> getFields() {
        return fields;
    }

    /**
     * The type Type.
     */
    static class Type implements TrickType<EmbedTrick> {

        /**
         * Gets clazz.
         *
         * @return clazz
         */
        @Override
        public Class<EmbedTrick> getClazz() {
            return EmbedTrick.class;
        }

        /**
         * Gets arg names.
         *
         * @return arg names
         */
        @Override
        public List<String> getArgNames() {
            return Lists.newArrayList("names", "title", "description", "color", "fields");
        }

        /**
         * Create from args embed trick.
         *
         * @param args the args
         * @return embed trick
         */
        @Override
        public EmbedTrick createFromArgs(final String args) {
            String[] argsArray = args.split(" \\| ");
            return new EmbedTrick(
                Arrays.asList(argsArray[0].split(" ")),
                argsArray[1],
                argsArray[2],
                Integer.parseInt(argsArray[3].replace("#", ""), 16)
            );
        }
    }
}
