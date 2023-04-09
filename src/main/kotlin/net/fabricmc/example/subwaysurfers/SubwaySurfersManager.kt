package net.fabricmc.example.subwaysurfers

import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import net.fabricmc.example.RandomIdeas.Companion.toId
import net.fabricmc.example.event.events.KeyEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.Perspective
import net.minecraft.util.math.Vec3d
import net.silkmc.silk.commands.clientCommand
import net.silkmc.silk.core.annotations.ExperimentalSilkApi
import net.silkmc.silk.core.entity.modifyVelocity
import net.silkmc.silk.core.text.literal
import net.silkmc.silk.network.packet.c2sPacket


@OptIn(ExperimentalSilkApi::class)
object SubwaySurfersManager {
    var moveForward: Boolean = false
    var lockView: Boolean = false
    var isEnabled: Boolean = false
    var desiredCameraDistance: Double = 6.0
    var yawAndPitch = Pair(90f, 90f)

    @OptIn(ExperimentalSerializationApi::class)
    private val movementTypePacket = c2sPacket<MovementType>("movement_type".toId())

    @Serializable
    enum class MovementType {
        LEFT, RIGHT
    }

    fun init() {
        clientCommand("subwaysurfers") {
            literal("start") {
                runs {
                    moveForward = true
                    lockView = true
                    MinecraftClient.getInstance().options.perspective = Perspective.THIRD_PERSON_BACK
                    isEnabled = true
                }
            }
            literal("yawAndPitch") {
                argument("yaw", FloatArgumentType.floatArg()) { yaw ->
                    argument("pitch", FloatArgumentType.floatArg()) { pitch ->
                        runs {
                            yawAndPitch = Pair(yaw(), pitch())
                        }
                    }
                }
            }
            literal("desiredCameraDistance") {
                argument("value", DoubleArgumentType.doubleArg()) { value ->
                    runs {
                        desiredCameraDistance = value()
                    }
                }
            }
            literal("move") {
                runs {
                    moveForward = !moveForward
                }
            }
            literal("view") {
                runs {
                    lockView = !lockView
                }
            }
        }
        sendClientInput()
        handlePlayerInput()
    }

    private fun sendClientInput() {
        KeyEvents.keyPressedOnce.listen {
            if (it.client.options.leftKey.matchesKey(it.key, it.scanCode)) {
                movementTypePacket.send(MovementType.LEFT)
            } else if (it.client.options.rightKey.matchesKey(it.key, it.scanCode)) {
                movementTypePacket.send(MovementType.RIGHT)
            }
        }
    }

    //TODO dash unten via sneak
    //TODO jump
    //TODO prodeuzal world gen
    

    private fun handlePlayerInput() {
        movementTypePacket.receiveOnServer { packet, context ->
            val player = context.player
            val dashStrength = 1.0

            if (player.surfer.rail == 1 && packet == MovementType.LEFT) {
                //TODO ERROR SOUND
            } else if (player.surfer.rail == 3 && packet == MovementType.RIGHT) {
                //TODO ERROR SOUND
            } else {
                player.sendMessage("Dashed $packet".literal)
                player.modifyVelocity(
                    Vec3d(
                        0.0,
                        0.1,
                        if (packet == MovementType.LEFT) dashStrength else -dashStrength
                    )
                )
                player.surfer.rail = player.surfer.rail + (if (packet == MovementType.LEFT) -1 else 1)
            }
        }
    }
}