package io.github.freya022.bot;

import io.github.freya022.botcommands.api.Logging;
import io.github.freya022.botcommands.api.core.annotations.BEventListener;
import io.github.freya022.botcommands.api.core.service.annotations.BService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import org.slf4j.Logger;

@BService
public class ReadyListener {
    private static final Logger LOGGER = Logging.getLogger();

    @BEventListener
    public void onReady(ReadyEvent event) {
        final JDA jda = event.getJDA();

        LOGGER.info("Bot connected as {}", jda.getSelfUser().getName());
        LOGGER.info("The bot is present on these guilds :");
        for (Guild guild : jda.getGuildCache()) {
            LOGGER.info("\t- {} ({})", guild.getName(), guild.getId());
        }
    }
}
