package gg.norisk.subwaysurfers.server.listener

import gg.norisk.subwaysurfers.network.c2s.MovementType
import gg.norisk.subwaysurfers.network.c2s.movementTypePacket
import gg.norisk.subwaysurfers.network.s2c.AnimationPacket
import gg.norisk.subwaysurfers.network.s2c.playAnimationS2C
import gg.norisk.subwaysurfers.registry.SoundRegistry
import gg.norisk.subwaysurfers.subwaysurfers.dashStrength
import gg.norisk.subwaysurfers.subwaysurfers.jumpStrength
import gg.norisk.subwaysurfers.subwaysurfers.rail
import gg.norisk.subwaysurfers.subwaysurfers.surfer
import net.minecraft.network.packet.s2c.play.PositionFlag
import net.minecraft.sound.SoundCategory
import net.minecraft.util.math.Vec3d
import net.silkmc.silk.core.entity.modifyVelocity
import net.silkmc.silk.core.task.mcCoroutineTask
import kotlin.time.Duration.Companion.seconds

object MovementInputListener {

    //Sprunganimation kürzer machen
    //fade hinzufügen
    //dash spekatulärer machen

    fun init() {
        movementTypePacket.receiveOnServer { packet, context ->
            val player = context.player

            if (packet == MovementType.SLIDE) {
                playAnimationS2C.sendToAll(AnimationPacket(player.uuid,"subway_jump"))

                player.surfer.isSliding = true
                mcCoroutineTask(delay = 3.seconds) {
                    player.surfer.isSliding = false
                }
            } else if (packet == MovementType.JUMP) {
                playAnimationS2C.sendToAll(AnimationPacket(player.uuid,"subway_jump"))
                (player.world.playSoundFromEntity(null, player, SoundRegistry.WHOOSH, SoundCategory.PLAYERS, 0.4f, 0.8f))
                player.modifyVelocity(
                    Vec3d(0.0, player.jumpStrength, 0.0)
                )
            } else if (player.rail == 0 && packet == MovementType.LEFT) {
                //TODO ERROR SOUND
            } else if (player.rail == 2 && packet == MovementType.RIGHT) {
                //TODO ERROR SOUND
            } else {
                playAnimationS2C.sendToAll(AnimationPacket(player.uuid,"subway_dash"))

                val centerPos = player.pos
                //Teleportation is better than modifying the velocity right=?
                (player.world.playSoundFromEntity(null, player, SoundRegistry.WHOOSH, SoundCategory.PLAYERS, 0.4f, 0.8f))
                player.teleport(
                    player.serverWorld,
                    centerPos.x + if (packet == MovementType.LEFT) player.dashStrength else -player.dashStrength,
                    player.y,
                    centerPos.z,
                    PositionFlag.VALUES.toSet(),
                    player.yaw,
                    player.pitch
                )
                player.rail = player.rail + (if (packet == MovementType.LEFT) -1 else 1)
            }
        }
    }
}
