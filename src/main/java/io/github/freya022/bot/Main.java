package io.github.freya022.bot;

import ch.qos.logback.classic.ClassicConstants;
import dev.reformator.stacktracedecoroutinator.runtime.DecoroutinatorRuntime;
import io.github.freya022.bot.config.Config;
import io.github.freya022.bot.config.Environment;
import io.github.freya022.botcommands.api.core.BBuilder;
import io.github.freya022.botcommands.api.core.Logging;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.slf4j.Logger;

import java.lang.management.ManagementFactory;
import java.util.Arrays;

public class Main {
    private static final String MAIN_PACKAGE_NAME = "io.github.freya022.bot";

    public static void main(String[] args) {
        System.setProperty(ClassicConstants.CONFIG_FILE_PROPERTY, Environment.LOGBACK_CONFIG_PATH.toAbsolutePath().toString());
        final Logger logger = Logging.getLogger();
        logger.info("Loading logback configuration at {}", Environment.LOGBACK_CONFIG_PATH.toAbsolutePath());

        // I use hotswap agent to update my code without restarting the bot
        // Of course this only supports modifying existing code
        // Refer to https://github.com/HotswapProjects/HotswapAgent#readme on how to use hotswap

        // stacktrace-decoroutinator has issues when reloading with hotswap agent
        if (ManagementFactory.getRuntimeMXBean().getInputArguments().contains("-XX:+AllowEnhancedClassRedefinition")) {
            logger.info("Skipping stacktrace-decoroutinator as enhanced hotswap is active");
        } else if (Arrays.asList(args).contains("--no-decoroutinator")) {
            logger.info("Skipping stacktrace-decoroutinator as --no-decoroutinator is specified");
        } else {
            DecoroutinatorRuntime.INSTANCE.load();
        }

        try {
            final Config config = Config.getInstance();
            BBuilder.newBuilder(builder -> {
                if (Environment.IS_DEV) {
                    builder.disableExceptionsInDMs(true);
                    builder.disableAutocompleteCache(true);
                }

                builder.addOwners(config.getOwnerIds());

                builder.addSearchPath(MAIN_PACKAGE_NAME);

                builder.textCommands(textCommands -> {
                    //Use ping as prefix if configured
                    textCommands.usePingAsPrefix(config.getPrefixes().contains("<ping>"));

                    for (String prefix : config.getPrefixes()) {
                        if (prefix.equals("<ping>")) continue;
                        textCommands.getPrefixes().add(prefix);
                    }
                });

                builder.applicationCommands(applicationCommands -> {
                    // Check command updates based on Discord's commands.
                    // This is only useful during development,
                    // as you can develop on multiple machines (but not simultaneously!).
                    // Using this in production is only going to waste API requests.
                    applicationCommands.enableOnlineAppCommandChecks(Environment.IS_DEV);

                    // Guilds in which @Test commands will be inserted
                    applicationCommands.getTestGuildIds().addAll(config.getTestGuildIds());

                    // Add french localization for application commands
                    applicationCommands.addLocalizations("Commands", DiscordLocale.FRENCH);
                });

                builder.components(components -> {
                    components.useComponents(true);
                });
            });

            // There is no JDABuilder going on here, it's taken care of in Bot

            logger.info("Loaded bot");
        } catch (Exception e) {
            logger.error("Unable to start the bot", e);
            System.exit(1);
        }
    }
}
