package gg.norisk.subwaysurfers.subwaysurfers

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.MathHelper
import org.joml.Vector3f

interface SubwaySurfer {
    var isSliding: Boolean
    var coins: Int
}

val isEnabled: Boolean
    get() {
        return MinecraftClient.getInstance().player?.isSubwaySurfers == true
    }

var PlayerEntity.lastCameraX: Vector3f
    get() {
        return this.dataTracker.get(lastCameraTracker)
    }
    set(value) {
        this.dataTracker.set(lastCameraTracker, value)
    }

var PlayerEntity.gravity: Double
    get() {
        return this.dataTracker.get(gravityTracker).toDouble()
    }
    set(value) {
        this.dataTracker.set(gravityTracker, value.toFloat())
    }

var PlayerEntity.jumpStrength: Double
    get() {
        return this.dataTracker.get(jumpStrengthTracker).toDouble()
    }
    set(value) {
        this.dataTracker.set(jumpStrengthTracker, value.toFloat())
    }

var PlayerEntity.dashStrength: Double
    get() {
        return this.dataTracker.get(dashStrengthTracker).toDouble()
    }
    set(value) {
        this.dataTracker.set(dashStrengthTracker, value.toFloat())
    }

var PlayerEntity.rail: Int
    get() {
        return this.dataTracker.get(railDataTracker)
    }
    set(value) {
        this.dataTracker.set(railDataTracker, MathHelper.clamp(value, 0, 2))
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
val dashStrengthTracker =
    DataTracker.registerData(PlayerEntity::class.java, TrackedDataHandlerRegistry.FLOAT)
val gravityTracker =
    DataTracker.registerData(PlayerEntity::class.java, TrackedDataHandlerRegistry.FLOAT)
val jumpStrengthTracker =
    DataTracker.registerData(PlayerEntity::class.java, TrackedDataHandlerRegistry.FLOAT)
val railDataTracker =
    DataTracker.registerData(PlayerEntity::class.java, TrackedDataHandlerRegistry.INTEGER)
val lastCameraTracker =
    DataTracker.registerData(PlayerEntity::class.java, TrackedDataHandlerRegistry.VECTOR3F)
val subwaySurfersTracker =
    DataTracker.registerData(PlayerEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)

val PlayerEntity.surfer get() = this as SubwaySurfer
