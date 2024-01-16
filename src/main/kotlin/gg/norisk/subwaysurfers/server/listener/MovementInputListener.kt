package gg.norisk.subwaysurfers.server.listener

import gg.norisk.subwaysurfers.network.c2s.MovementType
import gg.norisk.subwaysurfers.network.c2s.movementTypePacket
import gg.norisk.subwaysurfers.subwaysurfers.rail
import gg.norisk.subwaysurfers.subwaysurfers.surfer
import net.minecraft.network.packet.s2c.play.PositionFlag
import net.minecraft.util.math.Vec3d
import net.silkmc.silk.core.entity.modifyVelocity
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.broadcastText
import net.silkmc.silk.core.text.literal
import kotlin.time.Duration.Companion.seconds

object MovementInputListener {
    fun init() {
        movementTypePacket.receiveOnServer { packet, context ->
            val player = context.player
            val dashStrength = 2

            context.server.broadcastText("DashStrength $dashStrength")

            if (packet == MovementType.SLIDE) {
                player.surfer.isSliding = true
                mcCoroutineTask(delay = 3.seconds) {
                    player.surfer.isSliding = false
                }
            } else if (packet == MovementType.JUMP) {
                player.modifyVelocity(Vec3d(0.0, 0.3, 0.0))
            } else if (player.rail == 0 && packet == MovementType.LEFT) {
                //TODO ERROR SOUND
            } else if (player.rail == 2 && packet == MovementType.RIGHT) {
                //TODO ERROR SOUND
            } else {
                player.sendMessage("Dashed $packet".literal)
                val centerPos = player.pos

                player.teleport(
                    player.serverWorld,
                    centerPos.x + if (packet == MovementType.LEFT) dashStrength else -dashStrength,
                    player.y,
                    centerPos.z,
                    PositionFlag.VALUES.toSet(),
                    player.yaw,
                    player.pitch
                )
                /*player.modifyVelocity(
                    Vec3d(
                        if (packet == MovementType.LEFT) 1.0 else -1.0,
                        0.0,
                        0.0
                    )
                )*/
                player.rail = player.rail + (if (packet == MovementType.LEFT) -1 else 1)
            }
        }
    }
}
