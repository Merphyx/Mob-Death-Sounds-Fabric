package de.myronx;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.util.Identifier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MobDeathSoundConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File configFile = new File(FabricLoader.getInstance().getConfigDir() + "/mob_death_sound.json");
    public Identifier soundEvent = new Identifier("entity.experience_orb.pickup");
    public static final Identifier DEFAULT_SOUND_EVENT = new Identifier("entity.experience_orb.pickup");
    public static final float DEFAULT_SOUND_PITCH = 1.0f;
    public float soundPitch = 1.0f;
    public boolean doSound = true;
    public void loadConfig() {
        if (!configFile.exists()) {
            createDefaultConfig();
            return;
        }

        try (FileReader reader = new FileReader(configFile)) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);
            soundEvent = new Identifier(json.get("soundEvent").getAsString());
            soundPitch = json.get("soundPitch").getAsFloat();
            doSound = json.has("doSound") ? json.get("doSound").getAsBoolean() : doSound;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDefaultConfig() {
        try (FileWriter writer = new FileWriter(configFile)) {
            JsonObject json = new JsonObject();
            json.addProperty("soundEvent", DEFAULT_SOUND_EVENT.toString());
            json.addProperty("soundPitch", DEFAULT_SOUND_PITCH);
            json.addProperty("doSound", doSound);
            GSON.toJson(json, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try (FileWriter writer = new FileWriter(configFile)) {
            JsonObject json = new JsonObject();
            json.addProperty("soundEvent", soundEvent.toString());
            json.addProperty("soundPitch", soundPitch);
            json.addProperty("doSound", doSound);
            GSON.toJson(json, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}