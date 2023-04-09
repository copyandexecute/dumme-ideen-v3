package net.fabricmc.example.subwaysurfers

import net.minecraft.entity.player.PlayerEntity

interface SubwaySurfer {
    var rail: Int
}

val PlayerEntity.surfer get() = this as SubwaySurfer