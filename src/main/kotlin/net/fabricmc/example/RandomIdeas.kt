package net.fabricmc.example

import net.fabricmc.api.ModInitializer
import net.fabricmc.example.subwaysurfers.SubwaySurfersManager
import net.minecraft.util.Identifier

class RandomIdeas : ModInitializer {
    override fun onInitialize() {
        SubwaySurfersManager.init()
    }

    companion object {
        fun String.toId() = Identifier("norisk", this)
    }
}