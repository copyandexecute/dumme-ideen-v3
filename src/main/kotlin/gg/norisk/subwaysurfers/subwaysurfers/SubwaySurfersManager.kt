package gg.norisk.subwaysurfers.subwaysurfers

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import gg.norisk.subwaysurfers.event.events.KeyEvents
import kotlinx.coroutines.Job
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.Vec3d
import net.silkmc.silk.core.annotations.ExperimentalSilkApi
import net.silkmc.silk.core.entity.modifyVelocity
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.literal
import net.silkmc.silk.network.packet.c2sPacket
import kotlin.time.Duration.Companion.seconds


@OptIn(ExperimentalSilkApi::class)
object SubwaySurfersManager {
    var ticks = 0
    var runnable: Job? = null

    private val lifeCyclePacket = c2sPacket<Boolean>("life_cycle".toId())


    fun init() {
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


    //TODO dash unten via sneak
    //TODO jump
    //TODO prodeuzal world gen
}
