package gg.norisk.subwaysurfers.subwaysurfers

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.player.PlayerEntity

interface SubwaySurfer {
    var rail: Int
    var isSliding: Boolean
    var coins: Int
}

val isEnabled: Boolean
    get() {
        return MinecraftClient.getInstance().player?.isSubwaySurfers == true
    }

var PlayerEntity.isSubwaySurfers: Boolean
    get() {
        return this.dataTracker.get(subwaySurfersTracker)
    }
    set(value) {
        this.dataTracker.set(subwaySurfersTracker, value)
    }

var PlayerEntity.coins: Int
    get() {
        return this.dataTracker.get(coinDataTracker)
    }
    set(value) {
        this.dataTracker.set(coinDataTracker, value)
    }

val coinDataTracker =
    DataTracker.registerData(PlayerEntity::class.java, TrackedDataHandlerRegistry.INTEGER)
val subwaySurfersTracker =
    DataTracker.registerData(PlayerEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)

val PlayerEntity.surfer get() = this as SubwaySurfer
