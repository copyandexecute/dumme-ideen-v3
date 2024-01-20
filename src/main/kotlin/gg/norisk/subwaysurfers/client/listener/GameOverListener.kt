package gg.norisk.subwaysurfers.client.listener

import gg.norisk.subwaysurfers.client.hud.GameOverScreen
import gg.norisk.subwaysurfers.network.s2c.gameOverScreenS2C
import net.silkmc.silk.core.task.mcCoroutineTask

object GameOverListener {
    fun init() {
        gameOverScreenS2C.receiveOnClient { packet, context ->
            mcCoroutineTask(sync = true, client = true) {
                context.client.setScreen(GameOverScreen())
            }
        }
    }
}