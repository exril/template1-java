package io.github.freya022.bot;

import io.github.freya022.bot.config.Config;
import io.github.freya022.botcommands.api.core.JDAService;
import io.github.freya022.botcommands.api.core.events.BReadyEvent;
import io.github.freya022.botcommands.api.core.service.annotations.BService;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@BService
public class Bot extends JDAService {
    private final Config config;

    public Bot(Config config) {
        this.config = config;
    }

    @NotNull
    @Override
    public Set<GatewayIntent> getIntents() {
        return getDefaultIntents();
    }

    @Override
    public void createJDA(@NotNull BReadyEvent event, @NotNull IEventManager eventManager) {
        JDABuilder.createLight(config.getToken(), getIntents())
                .setMaxReconnectDelay(120) //Try to reconnect every 2 minutes instead of the default 15 minutes
                .setActivity(Activity.playing("with Java"))
                .setEventManager(eventManager) //Mandatory
                .build();
    }
}
