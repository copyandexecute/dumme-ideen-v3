package gg.norisk.subwaysurfers

import gg.norisk.subwaysurfers.client.ClientSettings
import gg.norisk.subwaysurfers.client.hud.InGameHud
import gg.norisk.subwaysurfers.client.input.KeyboardInput
import gg.norisk.subwaysurfers.client.listener.ClientAnimationListener
import gg.norisk.subwaysurfers.registry.*
import gg.norisk.subwaysurfers.server.command.StartCommand
import gg.norisk.subwaysurfers.server.listener.MovementInputListener
import gg.norisk.subwaysurfers.subwaysurfers.SubwaySurfersManager
import gg.norisk.subwaysurfers.worldgen.RailWorldManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier

object SubwaySurfers : ModInitializer, ClientModInitializer {
    override fun onInitialize() {
        EntityRegistry.registerEntityAttributes()
        SoundRegistry.init()
        BlockRegistry.init()
        EntityRendererRegistry.init()
        SubwaySurfersManager.init()
        RailWorldManager.init()
        StartCommand.init()
        MovementInputListener.init()
    }

    override fun onInitializeClient() {
        ClientSettings.init()
        KeyboardInput.init()
        InGameHud.init()
        BlockRendererRegistry.init()
        ClientAnimationListener.init()
    }

    fun String.toId() = Identifier("subwaysurfers", this)
    val noriskSkin = "textures/norisk_skin.png".toId()
}
