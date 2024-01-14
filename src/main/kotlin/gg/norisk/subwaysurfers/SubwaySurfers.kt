package gg.norisk.subwaysurfers

import gg.norisk.subwaysurfers.subwaysurfers.SubwaySurfersManager
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier

object SubwaySurfers : ModInitializer {
    override fun onInitialize() {
        SubwaySurfersManager.init()
    }

    fun String.toId() = Identifier("subwaysurfers", this)
    val noriskSkin = "textures/norisk_skin".toId()
}
