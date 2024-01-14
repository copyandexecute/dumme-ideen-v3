package gg.norisk.subwaysurfers.worldgen

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.block.Blocks
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.literal
import kotlin.math.sqrt

object RailWorldGenerator : ServerTickEvents.EndWorldTick {
    var direction: Direction = Direction.EAST
    var isEnabled = false
    var startPos: BlockPos? = null

    fun init() {
        command("railworld") {
            runs {
                val player = this.source.playerOrThrow
                isEnabled = !isEnabled
                direction = player.horizontalFacing
                startPos = player.blockPos
                this.source.player?.sendMessage("RailWorld: $isEnabled, Direction: $direction".literal)
            }
        }
        ServerTickEvents.END_WORLD_TICK.register(this)
    }

    override fun onEndTick(world: ServerWorld) {
        if (isEnabled) {
            for (player in world.players) {
                generateRailsForPlayer(player, world)
            }
        }
    }

    private fun BlockPos.toAxisPos(direction: Direction): BlockPos {
        return BlockPos(
            x * direction.offsetX,
            y * direction.offsetY,
            z * direction.offsetZ
        )
    }

    private fun generateRailsForPlayer(player: PlayerEntity, world: ServerWorld) {
        val playerPos = player.blockPos.toAxisPos(direction)
        val startAxisPos = startPos?.toAxisPos(direction) ?: return

        val distanceToOrigin = sqrt(playerPos.getSquaredDistance(startAxisPos)).toInt()
        //player.sendMessage("Distance to Origin $distanceToOrigin".literal)

        val currentPos = startPos!!.offset(direction, distanceToOrigin)
        val maxDistance = 10
        val scale = 2

        generateRailWay(maxDistance, scale, currentPos, world)
        generateWalls(maxDistance, scale, currentPos, world)
    }

    private fun generateWalls(maxDistance: Int, scale: Int, currentPos: BlockPos, world: ServerWorld) {
        repeat(maxDistance) { distance ->
            for (i in listOf(-2, 2)) {
                val offset = currentPos.offset(direction, distance).offset(direction.rotateYClockwise(), i * scale)
                world.setBlockState(offset,Blocks.DIAMOND_BLOCK.defaultState)
            }
        }
    }

    private fun generateRailWay(maxDistance: Int, scale: Int, currentPos: BlockPos, world: ServerWorld) {
        repeat(maxDistance) { distance ->
            for (i in listOf(-1, 0, 1)) {
                val offset = currentPos.offset(direction, distance).offset(direction.rotateYClockwise(), i * scale)
                placeRailIfNotExists(world, offset)
            }
        }
    }

    private fun placeRailIfNotExists(world: ServerWorld, blockPos: BlockPos) {
        val blockState = world.getBlockState(blockPos)
        if (blockState.isAir && !world.getBlockState(blockPos.down()).isSolid) {
            return
        }
        if (!blockState.isOf(Blocks.RAIL)) {
            world.setBlockState(blockPos, Blocks.RAIL.defaultState)
        }
    }
}
