package de.myronx.mobdeathsound.command;

import de.myronx.mobdeathsound.config.MobDeathSoundConfig;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvent;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MobDeathSoundCommands {
    public void registerCommands() {
        SuggestionProvider<FabricClientCommandSource> AVAILABLE_SOUNDS = SuggestionProviders.register(Identifier.of("client_available_sounds"), (context, builder) -> CommandSource.suggestIdentifiers((context.getSource()).getSoundIds(), builder));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("mobs")
                    .then(ClientCommandManager.literal("on").executes(context -> {
                        MobDeathSoundConfig.doSound = true;
                        if (context.getSource().getClient().player != null) {
                            context.getSource().getClient().player.sendMessage(Text.literal("Turned Death-Sound §aon"), true);
                        }
                        return 1;
                    }))
                    .then(ClientCommandManager.literal("off").executes(context -> {
                        MobDeathSoundConfig.doSound = false;
                        if (context.getSource().getClient().player != null) {
                            context.getSource().getClient().player.sendMessage(Text.literal("Turned Death-Sound §coff"), true);
                        }
                        return 1;
                    }))
                    .then(ClientCommandManager.literal("default")
                            .executes(context -> {
                                Identifier defaultSound = MobDeathSoundConfig.DEFAULT_SOUND_EVENT;
                                float defaultPitch = MobDeathSoundConfig.DEFAULT_SOUND_PITCH;

                                MobDeathSoundConfig.soundEvent = defaultSound;
                                MobDeathSoundConfig.soundPitch = defaultPitch;

                                if (context.getSource().getClient().player != null) {
                                    context.getSource().getClient().player.sendMessage(Text.literal("Default Death-Sound restored: §a" + defaultSound + "§f | Pitch: " + defaultPitch), true);
                                }
                                return 1;
                            }))
                    .then(ClientCommandManager.literal("set")
                            .then(ClientCommandManager.argument("sound", IdentifierArgumentType.identifier()).suggests(AVAILABLE_SOUNDS)
                                    .then(ClientCommandManager.argument("pitch", FloatArgumentType.floatArg(0.5f, 2.0f))
                                            .executes(context -> {
                                                Identifier soundId = context.getArgument("sound", Identifier.class);
                                                float pitch = context.getArgument("pitch", Float.class);

                                                MobDeathSoundConfig.soundEvent = soundId;
                                                MobDeathSoundConfig.soundPitch = pitch;

                                                if (context.getSource().getClient().player != null) {
                                                    context.getSource().getClient().player.sendMessage(Text.literal("Set Death-Sound to: §a" + soundId + "§f | Pitch: " + pitch), true);
                                                    context.getSource().getClient().player.playSound(SoundEvent.of(MobDeathSoundConfig.soundEvent), 1f, MobDeathSoundConfig.soundPitch);
                                                }
                                                return 1;
                                            }))))
                    .then(ClientCommandManager.literal("forcesound")
                            .then(ClientCommandManager.literal("off").executes(context -> {
                                if (context.getSource().getClient().player != null) {
                                    SoundManager soundManager = context.getSource().getClient().getSoundManager();
                                    soundManager.stopAll();
                                    context.getSource().getClient().player.sendMessage(Text.literal("Running Death-Sound has been turned §coff"), true);
                                }
                                return 1;
                            }))));
        });
    }
}