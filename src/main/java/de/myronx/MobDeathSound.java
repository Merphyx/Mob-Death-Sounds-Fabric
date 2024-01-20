package de.myronx;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MobDeathSound implements ModInitializer {
	public static final String MOD_ID = "mob-death-sound";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initialized Mob Death-Sound mod.");
	}
}