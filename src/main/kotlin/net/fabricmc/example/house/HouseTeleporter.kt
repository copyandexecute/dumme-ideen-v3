package net.fabricmc.example.house

import net.fabricmc.example.extensions.forEachIndexed
import net.fabricmc.example.extensions.getBlockStates
import net.fabricmc.example.extensions.indexOfPlayerPos
import net.minecraft.block.Block
import net.minecraft.block.DoorBlock
import net.minecraft.registry.tag.StructureTags
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.structure.StructurePiece
import net.minecraft.util.math.BlockBox
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.task.mcCoroutineTask
import java.util.UUID
import kotlin.random.Random

object HouseTeleporter {
    private val houses = mutableMapOf<StructurePiece, BlockBox>()
    private val lastBlockBoxes = mutableMapOf<UUID, Pair<BlockBox, BlockBox?>>()

    fun onDoorInteraction(isOpen: Boolean, world: ServerWorld, pos: BlockPos, player: ServerPlayerEntity) {
        val server = world.server
        val house = getStructurePieceAt(pos, world)
        if (house != null) {
            if (isOpen) {
                houses[house] = randomPasteBlockBox(server, world, house.boundingBox)
            } else {
                lastBlockBoxes[player.uuid] = Pair(
                    teleportToNewBlockBox(house.boundingBox, houses[house] ?: error("No BlockBox $house"), player),
                    null
                )
            }
        } else {
            val blockBoxes = lastBlockBoxes[player.uuid] ?: return
            if (isOpen) {
                lastBlockBoxes[player.uuid] =
                    Pair(blockBoxes.first, randomPasteBlockBox(server, world, blockBoxes.first))
            } else {
                lastBlockBoxes[player.uuid] =
                    Pair(teleportToNewBlockBox(blockBoxes.first, blockBoxes.second!!, player), null)
            }
        }
    }

    private fun teleportToNewBlockBox(
        oldBlockBox: BlockBox,
        newBlockBox: BlockBox,
        player: ServerPlayerEntity
    ): BlockBox {
        val currentPlayerIndex = oldBlockBox.indexOfPlayerPos(player.blockPos)
        if (currentPlayerIndex != -1) {
            newBlockBox.forEachIndexed { index, blockPos ->
                if (index == currentPlayerIndex) {
                    player.teleport(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble())
                }
            }
        }
        return newBlockBox
    }

    private fun randomPasteBlockBox(server: MinecraftServer, world: ServerWorld, box: BlockBox): BlockBox {
        val randomWorld = server.randomWorld()
        val randomPos = randomWorld.getRandomPos(1000)

        val newBlockBox = BlockBox.create(
            randomPos, randomPos.add(
                box.blockCountX - 1,
                box.blockCountY - 1,
                box.blockCountZ - 1
            )
        )

        val oldBlockStates = box.getBlockStates(world)

        newBlockBox.forEachIndexed { index, blockPos ->
            var blockState = oldBlockStates.getOrNull(index) ?: return@forEachIndexed
            blockState = blockState.withIfExists(DoorBlock.OPEN, false)
            world.setBlockState(blockPos, blockState, Block.REDRAW_ON_MAIN_THREAD)
        }

        return newBlockBox
    }

    private fun getStructurePieceAt(pos: BlockPos, world: ServerWorld): StructurePiece? {
        return world.structureAccessor.getStructureContaining(
            pos,
            StructureTags.VILLAGE
        ).children.firstOrNull { it.boundingBox.contains(pos) }
    }

    private fun World.getRandomPos(offSet: Int = 1000): BlockPos {
        val x = Random.nextInt(-offSet, offSet)
        val z = Random.nextInt(-offSet, offSet)
        val y = Random.nextInt(bottomY, topY)
        return BlockPos(x, y, z)
    }

    private fun MinecraftServer.randomWorld(): ServerWorld {
        return worlds.toList().random()
    }
}
