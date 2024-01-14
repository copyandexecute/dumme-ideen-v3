package gg.norisk.subwaysurfers

import gg.norisk.subwaysurfers.registry.EntityRegistry
import gg.norisk.subwaysurfers.registry.EntityRendererRegistry
import gg.norisk.subwaysurfers.subwaysurfers.SubwaySurfersManager
import gg.norisk.subwaysurfers.worldgen.RailWorldGenerator
import gg.norisk.subwaysurfers.worldgen.RailWorldManager
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier

object SubwaySurfers : ModInitializer {
    override fun onInitialize() {
        EntityRegistry.registerEntityAttributes()
        EntityRendererRegistry.init()
        SubwaySurfersManager.init()
        RailWorldManager.init()
    }

    fun String.toId() = Identifier("subwaysurfers", this)
    val noriskSkin = "textures/norisk_skin.png".toId()
}
