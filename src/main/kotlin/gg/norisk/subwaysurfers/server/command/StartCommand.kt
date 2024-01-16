package gg.norisk.subwaysurfers.server.command

import com.mojang.brigadier.context.CommandContext
import gg.norisk.subwaysurfers.network.s2c.VisualClientSettings
import gg.norisk.subwaysurfers.network.s2c.visualClientSettingsS2C
import gg.norisk.subwaysurfers.worldgen.RailWorldManager
import net.minecraft.server.command.ServerCommandSource
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.task.mcCoroutineTask

object StartCommand {
    fun init() {
        command("subwaysurfers") {
            literal("stop") {
                runs { extracted(false) }
            }
            literal("start") {
                argument<Float>("yaw") { yawArg ->
                    runs { extracted(yawArg = yawArg()) }
                    argument<Float>("pitch") { pitchArg ->
                        runs { extracted(yawArg = yawArg(), pitchArg = pitchArg()) }
                        argument<Double>("desiredCameraDistance") { cameraDistanceArg ->
                            runs { extracted(true, cameraDistanceArg(), yawArg(), pitchArg()) }
                        }
                    }
                }
                runs { extracted() }
            }
        }
    }

    private fun CommandContext<ServerCommandSource>.extracted(
        isEnabled: Boolean = true,
        cameraDistanceArg: Double? = null,
        yawArg: Float? = null,
        pitchArg: Float? = null
    ) {
        val player = this.source.playerOrThrow

        val settings = VisualClientSettings()
        isEnabled.apply { settings.isEnabled = this }
        cameraDistanceArg?.apply { settings.desiredCameraDistance = this }
        yawArg?.apply { settings.yaw = this }
        pitchArg?.apply { settings.pitch = this }

        visualClientSettingsS2C.send(settings, player)

        mcCoroutineTask(delay = 1.ticks) {
            RailWorldManager.addPlayer(player)
        }
    }
}
