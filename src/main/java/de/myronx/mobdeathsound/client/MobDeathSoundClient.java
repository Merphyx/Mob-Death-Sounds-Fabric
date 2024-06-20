package de.myronx.mobdeathsound.client;

import net.fabricmc.api.ClientModInitializer;

import de.myronx.mobdeathsound.command.MobDeathSoundCommands;
import de.myronx.mobdeathsound.config.MobDeathSoundConfig;
import de.myronx.mobdeathsound.event.MobDeathSoundEventHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MobDeathSoundClient implements ClientModInitializer {
    private static final String MOD_ID = "mob-death-sound";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private final MobDeathSoundConfig config = new MobDeathSoundConfig();
    private final MobDeathSoundCommands commands = new MobDeathSoundCommands();
    private final MobDeathSoundEventHandler eventhandler = new MobDeathSoundEventHandler();

    @Override
    public void onInitializeClient() {
        config.loadConfig();
        commands.registerCommands();
        eventhandler.registerEvents();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            config.saveConfig();
            LOGGER.info("Mob Death-Sound config saved.");
        }));

        LOGGER.info("Initialized Mob Death-Sound mod.");
    }
}