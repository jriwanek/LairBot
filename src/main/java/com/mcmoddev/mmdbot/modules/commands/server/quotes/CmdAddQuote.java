package com.mcmoddev.mmdbot.modules.commands.server.quotes;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mcmoddev.mmdbot.core.Utils;
import com.mcmoddev.mmdbot.utilities.quotes.Quote;
import com.mcmoddev.mmdbot.utilities.quotes.QuoteList;
import com.mcmoddev.mmdbot.utilities.quotes.StringQuote;
import com.mcmoddev.mmdbot.utilities.quotes.UserReference;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Add a quote to the list.
 *
 * Possible forms:
 *  !addquote "I said something funny" - @Curle
 *  !addquote "I said something funny" - 462617385157787648
 *  !addquote "I said something funny"
 *  !addquote "I said something funny - The best bot developer
 *
 * Can be used by anyone.
 *
 * TODO: Prepare for more Quote implementations.
 *
 * @author Curle
 */
public final class CmdAddQuote extends Command {

    /**
     * Create the command.
     * Sets all the usual flags.
     */
    public CmdAddQuote() {
        super();
        name = "addquote";
        aliases = new String[] { "add-quote", "quoteadd", "quote-add" };
        help = "Adds a new Quote to the list.\nAdd a quote like so: !addquote \"I said something funny - author\"!";
    }

    @Override
    protected void execute(final CommandEvent event) {
        if (!Utils.checkCommand(this, event))
            return;

        final TextChannel channel = event.getTextChannel();
        String argsFull = event.getArgs();

        String[] args = argsFull.split("-");
        // args = [ "text", <@ID> ]

        // Verify that there were any arguments
        if (!(args.length > 0)) {
            channel.sendMessage("Invalid arguments. See the help for this command.").queue();
            return;
        }

        // Verify that there's a message being quoted.
        if (!(args[0].charAt(0) == '\"')) {
            channel.sendMessage("Invalid arguments. See the help for this command.").queue();
            return;
        }

        // Remove the start and end quote.
        args[0] = args[0].trim();
        String quote = args[0].substring(1, args[0].length() - 1);

        Quote finishedQuote;
        // Fetch the user who created the quote
        UserReference author = new UserReference(event.getAuthor().getIdLong());

        // Check if there's any attribution
        if (args.length == 1) {
            // Anonymous quote.
            UserReference quotee = new UserReference();

            finishedQuote = new StringQuote(quotee, quote, author);
        } else {
            // args.length == 2 by logical deduction.

            String quotee = args[1].trim();

            final int mentionUsernameHeader = 3;
            final int mentionStandardHeader = 2;

            // Check if this is a mention
            if (quotee.charAt(0) == '<' && quotee.charAt(1) == '@')
                // Strip the tags from it
                quotee = quotee.substring(quotee.charAt(2) == '!' ? mentionUsernameHeader : mentionStandardHeader,
                    quotee.length() - 1);

            // Check if there's a user ID here
            try {
                long id = Long.parseLong(quotee);
                UserReference quoteeUser = new UserReference(id);
                finishedQuote = new StringQuote(quoteeUser, quote, author);
            } catch (NumberFormatException exception) {
                // No user ID. Must be a string assignment.
                UserReference quoteeUser = new UserReference(quotee);
                finishedQuote = new StringQuote(quoteeUser, quote, author);
            }
        }

        int quoteID = QuoteList.getQuoteSlot();
        finishedQuote.setID(quoteID);

        // All execution leads to here, where finishedQuote is valid.
        QuoteList.addQuote(finishedQuote);

        channel.sendMessage("Added quote " + quoteID + "!").queue();
    }
}