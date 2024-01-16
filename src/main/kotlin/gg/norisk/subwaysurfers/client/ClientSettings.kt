package gg.norisk.subwaysurfers.client

import gg.norisk.subwaysurfers.network.s2c.VisualClientSettings
import gg.norisk.subwaysurfers.network.s2c.visualClientSettingsS2C
import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfers
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.Perspective

object ClientSettings {
    var settings = VisualClientSettings()

    fun init() {
        visualClientSettingsS2C.receiveOnClient { packet, context ->
            val player = context.client.player ?: return@receiveOnClient
            if (packet.isEnabled) {
                player.yaw = 90f
                player.pitch = 0f
                MinecraftClient.getInstance().options.perspective = Perspective.THIRD_PERSON_BACK
            } else {
                MinecraftClient.getInstance().options.perspective = Perspective.FIRST_PERSON
            }
            settings = packet
        }
    }

    fun isEnabled(): Boolean {
        return settings.isEnabled
    }

    fun onToggle(player: ClientPlayerEntity) {
    }
}
