package de.myronx;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvent;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class MobDeathSoundClient implements ClientModInitializer {
            private World previousWorld;
            private int updateTimer;
            private int previousStatisticValue = 0;
            private final MobDeathSoundConfig config = new MobDeathSoundConfig();

            @Override
            public void onInitializeClient() {
                config.loadConfig();

                SuggestionProvider<FabricClientCommandSource> AVAILABLE_SOUNDS = SuggestionProviders.register(new Identifier("client_available_sounds"), (context, builder) -> CommandSource.suggestIdentifiers((context.getSource()).getSoundIds(), builder));

                ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
                            dispatcher.register(ClientCommandManager.literal("mobs")
                                            .then(ClientCommandManager.literal("on").executes(context -> {
                                                config.doSound = true;
                                                if (context.getSource().getClient().player != null) {
                                                    context.getSource().getClient().player.sendMessage(Text.literal("Turned Death-Sound §aon"), true);
                                                }
                                                config.saveConfig();
                                                return 1;
                                            }))
                                            .then(ClientCommandManager.literal("off").executes(context -> {
                                                config.doSound = false;
                                                if (context.getSource().getClient().player != null) {
                                                    context.getSource().getClient().player.sendMessage(Text.literal("Turned Death-Sound §coff"), true);
                                                }
                                                config.saveConfig();
                                                return 1;
                                            }))
                                            .then(ClientCommandManager.literal("default")
                                            .executes(context -> {
                                                Identifier defaultSound = MobDeathSoundConfig.DEFAULT_SOUND_EVENT;
                                                float defaultPitch = MobDeathSoundConfig.DEFAULT_SOUND_PITCH;

                                                config.soundEvent = defaultSound;
                                                config.soundPitch = defaultPitch;

                                                if (context.getSource().getClient().player != null) {
                                                    context.getSource().getClient().player.sendMessage(Text.literal("Default Death-Sound restored: §a" + defaultSound + "§f | Pitch: " + defaultPitch), true);
                                                }

                                                config.saveConfig();

                                                return 1;
                                            }))
                                            .then(ClientCommandManager.literal("set")
                                            .then(ClientCommandManager.argument("sound", IdentifierArgumentType.identifier()).suggests(AVAILABLE_SOUNDS)
                                                    .then(ClientCommandManager.argument("pitch", FloatArgumentType.floatArg())
                                                            .executes(context -> {
                                                                Identifier soundId = context.getArgument("sound", Identifier.class);
                                                                float pitch = context.getArgument("pitch", Float.class);

                                                                config.soundEvent = soundId;
                                                                config.soundPitch = pitch;

                                                                if (context.getSource().getClient().player != null) {
                                                                    context.getSource().getClient().player.sendMessage(Text.literal("Set Death-Sound to: §a" + soundId + "§f | Pitch: " + pitch), true);
                                                                }

                                                                config.saveConfig();

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


                ClientTickEvents.END_CLIENT_TICK.register(client -> {
                    if (config.doSound) {
                        if (client.player != null) {

                            if (client.world != previousWorld) {
                                previousStatisticValue = 0;
                                previousWorld = client.world;
                            }

                            Identifier identifier = Stats.MOB_KILLS;
                            Stat<Identifier> stat = Stats.CUSTOM.getOrCreateStat(identifier);

                            int currentValue = client.player.getStatHandler().getStat(stat);
                            if (currentValue > previousStatisticValue) {
                                client.player.playSound(SoundEvent.of(config.soundEvent), 11f, config.soundPitch);

                                previousStatisticValue = currentValue;
                            }
                        }

                        if (client.getNetworkHandler() != null) {
                            updateTimer += 1;
                            if (updateTimer >= 5) {
                                client.getNetworkHandler().sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS));
                                updateTimer = 0;
                            }
                        }
                    }
                });
            }
        }




