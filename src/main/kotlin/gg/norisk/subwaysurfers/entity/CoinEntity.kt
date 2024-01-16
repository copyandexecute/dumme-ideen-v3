package gg.norisk.subwaysurfers.entity

import gg.norisk.subwaysurfers.entity.TrainEntity.Companion.handleDiscard
import gg.norisk.subwaysurfers.subwaysurfers.coins
import net.minecraft.block.BlockState
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import software.bernie.geckolib.animatable.GeoEntity
import software.bernie.geckolib.constant.DefaultAnimations
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar
import software.bernie.geckolib.util.GeckoLibUtil
import java.util.*

class CoinEntity(type: EntityType<out AnimalEntity>, level: World) : AnimalEntity(type, level), GeoEntity {
    private val cache: AnimatableInstanceCache = GeckoLibUtil.createInstanceCache(this)
    var owner: UUID? = null

    init {
        this.ignoreCameraFrustum = true
    }

    // Let the player ride the entity
    override fun interactMob(player: PlayerEntity, hand: Hand): ActionResult {
        if (!this.hasPassengers()) {
            player.startRiding(this)

            return super.interactMob(player, hand)
        }

        return super.interactMob(player, hand)
    }

    override fun tick() {
        super.tick()
        if (!world.isClient) {
            handleDiscard(owner)
        }
    }

    override fun onPlayerCollision(player: PlayerEntity) {
        if (!world.isClient) {
            player.coins++
            player.sendMessage(Text.of("Coins: ${player.coins}"), true)
            player.playSound(
                SoundEvents.ENTITY_ITEM_PICKUP,
                SoundCategory.PLAYERS,
                0.5f,
                3f
            )
            this.discard()
        }
    }

    // Turn off step sounds since it's a bike
    override fun playStepSound(pos: BlockPos, block: BlockState) {}

    // Apply player-controlled movement
    override fun travel(pos: Vec3d) {
        if (this.isAlive) {
            if (this.hasPassengers()) {
                val passenger = controllingPassenger
                this.prevYaw = yaw
                this.prevPitch = pitch

                yaw = passenger!!.yaw
                pitch = passenger.pitch * 0.5f
                setRotation(yaw, pitch)

                this.bodyYaw = this.yaw
                this.headYaw = this.bodyYaw
                val x = passenger.sidewaysSpeed * 0.5f
                var z = passenger.forwardSpeed

                if (z <= 0) z *= 0.25f

                this.movementSpeed = 0.3f
                super.travel(Vec3d(x.toDouble(), pos.y, z.toDouble()))
            }
        }
    }

    // Get the controlling passenger
    override fun getControllingPassenger(): LivingEntity? {
        return firstPassenger as? LivingEntity?
    }

    override fun isLogicalSideForUpdatingMovement(): Boolean {
        return true
    }

    // Add our generic idle animation controller
    override fun registerControllers(controllers: ControllerRegistrar) {
        controllers.add(DefaultAnimations.genericIdleController(this))
    }

    override fun getAnimatableInstanceCache(): AnimatableInstanceCache {
        return this.cache
    }

    override fun createChild(level: ServerWorld, partner: PassiveEntity): PassiveEntity? {
        return null
    }
}
