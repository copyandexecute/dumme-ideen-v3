package gg.norisk.subwaysurfers.subwaysurfers

import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.player.PlayerEntity

interface SubwaySurfer {
    var rail: Int
    var isSliding: Boolean
    var coins: Int
}

var PlayerEntity.coins: Int
    get() {
        return this.dataTracker.get(coinDataTracker)
    }
    set(value) {
        this.dataTracker.set(coinDataTracker, value)
    }

val coinDataTracker =
    DataTracker.registerData<Int>(PlayerEntity::class.java, TrackedDataHandlerRegistry.INTEGER);

val PlayerEntity.surfer get() = this as SubwaySurfer
