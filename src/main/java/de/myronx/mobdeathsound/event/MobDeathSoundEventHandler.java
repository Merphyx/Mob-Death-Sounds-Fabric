package de.myronx.mobdeathsound.event;

import de.myronx.mobdeathsound.config.MobDeathSoundConfig;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class MobDeathSoundEventHandler {
    private World previousWorld;
    private int updateTimer;
    private int previousStatisticValue;

    public void registerEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (MobDeathSoundConfig.doSound) {
                handleClientTick(client);
            }
        });
    }

    private void handleClientTick(MinecraftClient client) {
        if (client.player != null) {
            if (client.world != previousWorld) {
                previousStatisticValue = 0;
                previousWorld = client.world;
            }

            Identifier identifier = Stats.MOB_KILLS;
            Stat<Identifier> stat = Stats.CUSTOM.getOrCreateStat(identifier);

            int currentValue = client.player.getStatHandler().getStat(stat);
            if (currentValue > previousStatisticValue) {
                client.player.playSound(SoundEvent.of(MobDeathSoundConfig.soundEvent), 1f, MobDeathSoundConfig.soundPitch);
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
}