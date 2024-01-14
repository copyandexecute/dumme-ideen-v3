package gg.norisk.subwaysurfers.subwaysurfers

import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import gg.norisk.subwaysurfers.SubwaySurfers.toId
import gg.norisk.subwaysurfers.event.events.KeyEvents
import kotlinx.coroutines.Job
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.Perspective
import net.minecraft.util.math.Vec3d
import net.silkmc.silk.commands.clientCommand
import net.silkmc.silk.commands.player
import net.silkmc.silk.core.annotations.ExperimentalSilkApi
import net.silkmc.silk.core.entity.modifyVelocity
import net.silkmc.silk.core.task.infiniteMcCoroutineTask
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.literal
import net.silkmc.silk.network.packet.c2sPacket
import kotlin.time.Duration.Companion.seconds


@OptIn(ExperimentalSilkApi::class)
object SubwaySurfersManager {
    var isEnabled: Boolean = false
    var desiredCameraDistance: Double = 6.0
    var yawAndPitch = Pair(90f, 20f)
    var ticks = 0
    var multiplier = 0
    var coins = 0
    var runnable: Job? = null

    @OptIn(ExperimentalSerializationApi::class)
    private val movementTypePacket = c2sPacket<MovementType>("movement_type".toId())
    private val lifeCyclePacket = c2sPacket<Boolean>("life_cycle".toId())

    @Serializable
    enum class MovementType {
        LEFT, RIGHT, JUMP, SLIDE
    }

    fun init() {
        clientCommand("subwaysurfers") {
            literal("start") {
                runs {
                    if (!isEnabled) {
                        MinecraftClient.getInstance().options.perspective = Perspective.THIRD_PERSON_BACK
                        isEnabled = true
                        this.source.player.yaw = 90f
                        this.source.player.pitch = 0f
                        runnable?.cancel()
                        runnable = infiniteMcCoroutineTask(client = true) {
                            if (!MinecraftClient.getInstance().isPaused)
                                ticks++
                        }
                    } else {
                        isEnabled = false
                        runnable?.cancel()
                        MinecraftClient.getInstance().options.perspective = Perspective.FIRST_PERSON
                    }
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
        }
        sendClientInput()
        handlePlayerInput()
        //handleLifeCycle()
        HudRenderCallback.EVENT.register(HudRenderCallback { matrixStack, tickDelta ->
            val client = MinecraftClient.getInstance()
            val textRenderer = client.textRenderer

            // val multiplierOffSet = renderTicks(client, textRenderer, matrixStack)
            // renderMultiplier(multiplierOffSet, textRenderer, matrixStack)
            // renderCoins(client, textRenderer, matrixStack)
        })
    }

    /*private fun renderTicks(client: MinecraftClient, textRenderer: TextRenderer, drawContext: DrawContext): Float {
        val ticksSinceStart = String.format("%06d", ticks)
        val x: Float = client.window.scaledWidth - 2 - textRenderer.getWidth(ticksSinceStart)
        drawContext.drawText(textRenderer, ticksSinceStart, x.toInt(), 0, 14737632,false)
        return x
    }

    private fun renderCoins(client: MinecraftClient, textRenderer: TextRenderer, matrixStack: MatrixStack) {
        val x: Float = client.window.scaledWidth.toFloat() - 2f - textRenderer.getWidth("$coins")
        textRenderer.drawWithShadow(matrixStack, "$coins", x, 10f, 14737632)
    }

    private fun renderMultiplier(
        tickWidth: Float,
        textRenderer: TextRenderer,
        matrixStack: MatrixStack
    ) {
        val multiplierText = "x$multiplier"
        val x: Float = tickWidth - 2f - textRenderer.getWidth(multiplierText)
        //TODO gelb
        textRenderer.drawWithShadow(matrixStack, multiplierText, x, 0f, 14737632)
    }*/

    private fun sendClientInput() {
        if (!isEnabled) {
            //return
        }
        KeyEvents.keyPressedOnce.listen {
            if (it.client.options.leftKey.matchesKey(it.key, it.scanCode)) {
                movementTypePacket.send(MovementType.LEFT)
            } else if (it.client.options.rightKey.matchesKey(it.key, it.scanCode)) {
                movementTypePacket.send(MovementType.RIGHT)
            } else if (it.client.options.jumpKey.matchesKey(it.key, it.scanCode)) {
                movementTypePacket.send(MovementType.JUMP)
            } else if (it.client.options.sneakKey.matchesKey(it.key, it.scanCode)) {
                movementTypePacket.send(MovementType.SLIDE)
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

            if (packet == MovementType.SLIDE) {
                player.surfer.isSliding = true
                mcCoroutineTask(delay = 3.seconds) {
                    player.surfer.isSliding = false
                }
            } else if (packet == MovementType.JUMP) {
                player.modifyVelocity(Vec3d(0.0, 0.3, 0.0))
            } else if (player.surfer.rail == 1 && packet == MovementType.LEFT) {
                //TODO ERROR SOUND
            } else if (player.surfer.rail == 3 && packet == MovementType.RIGHT) {
                //TODO ERROR SOUND
            } else {
                player.sendMessage("Dashed $packet".literal)
                player.modifyVelocity(
                    Vec3d(
                        0.0,
                        0.0,
                        if (packet == MovementType.LEFT) dashStrength else -dashStrength
                    )
                )
                player.surfer.rail = player.surfer.rail + (if (packet == MovementType.LEFT) -1 else 1)
            }
        }
    }
}
