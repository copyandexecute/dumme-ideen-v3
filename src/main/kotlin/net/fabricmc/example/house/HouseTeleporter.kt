package net.fabricmc.example.house

import net.fabricmc.example.extensions.forEachIndexed
import net.fabricmc.example.extensions.getBlockStates
import net.fabricmc.example.extensions.indexOfPlayerPos
import net.minecraft.block.BlockState
import net.minecraft.registry.tag.StructureTags
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.structure.StructurePiece
import net.minecraft.util.math.BlockBox
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.silkmc.silk.core.text.broadcastText
import kotlin.random.Random

object HouseTeleporter {
    private val houses = mutableMapOf<StructurePiece, BlockBox>()

    fun onDoorInteraction(isOpen: Boolean, world: ServerWorld, pos: BlockPos, player: ServerPlayerEntity) {
        val server = world.server
        val house = getStructurePieceAt(pos, world)
        if (house != null) {
            if (isOpen) {
                val randomWorld = server.randomWorld()
                val randomPos = randomWorld.getRandomPos(1000)

                val newBlockBox = BlockBox.create(
                    randomPos, randomPos.add(
                        house.boundingBox.blockCountX - 1,
                        house.boundingBox.blockCountY - 1,
                        house.boundingBox.blockCountZ - 1
                    )
                )

                val oldBlockStates = house.boundingBox.getBlockStates(world)

                newBlockBox.forEachIndexed { index, blockPos ->
                    world.setBlockState(blockPos, oldBlockStates.getOrNull(index) ?: return@forEachIndexed)
                }

                houses[house] = newBlockBox
            } else {
                val blockBox = houses[house] ?: error("No BlockBox $house")
                val playerIndex = house.boundingBox.indexOfPlayerPos(player.blockPos)
                if (playerIndex != -1) {
                    blockBox.forEachIndexed { index, blockPos ->
                        if (index == playerIndex) {
                            player.teleport(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble())
                        }
                    }
                }
            }
        }
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

    data class StructureTeleporter(
        val blockBox: BlockBox,
        val playerPosIndex: Int,
        val blocks: MutableList<BlockState>
    )
}