package net.fabricmc.example.subwaysurfers

import net.minecraft.entity.player.PlayerEntity

interface SubwaySurfer {
    var rail: Int
    var isSliding: Boolean
}

val PlayerEntity.surfer get() = this as SubwaySurfer