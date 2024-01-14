package gg.norisk.subwaysurfers.worldgen

import gg.norisk.subwaysurfers.entity.CoinEntity
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.world.ServerWorld
import net.silkmc.silk.commands.command
import java.util.*

object RailWorldManager : ServerTickEvents.EndWorldTick {
    val rails = mutableMapOf<UUID, RailWorldGenerator>()

    fun init() {
        command("railworld") {
            runs {
                val player = this.source.playerOrThrow
                if (rails.containsKey(player.uuid)) {
                    rails.remove(player.uuid)
                } else {
                    rails[player.uuid] = RailWorldGenerator(player)
                }
            }
        }
        ServerTickEvents.END_WORLD_TICK.register(this)
    }

    override fun onEndTick(world: ServerWorld) {
        for (player in world.players) {
            rails[player.uuid]?.tick(player, world)
        }
    }
}
