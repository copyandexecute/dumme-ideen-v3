package net.fabricmc.example

import net.fabricmc.api.ModInitializer
import net.minecraft.block.BlockState
import net.minecraft.registry.tag.StructureTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockBox
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3i
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.literal
import kotlin.random.Random

class RandomIdeas : ModInitializer {
    override fun onInitialize() {
        command("structure") {
            runs {
                val player = this.source.playerOrThrow
                val world = player.world as ServerWorld
                val structureStart =
                    world.structureAccessor.getStructureContaining(player.blockPos, StructureTags.VILLAGE)

                data class StructureTeleporter(
                    val blockBox: BlockBox,
                    val playerPosIndex: Int,
                    val blocks: MutableList<BlockState>
                )


                val structurePiece =
                    structureStart.children.firstOrNull { it.boundingBox.contains(player.blockPos) } ?: return@runs
                val box = structurePiece.boundingBox

                val structureTeleporter = StructureTeleporter(
                    box,
                    box.indexOfPlayerPos(player.blockPos),
                    mutableListOf()
                )

                player.sendMessage("${box.dimensions} ${box.normalized().dimensions} $box".literal)
                player.sendMessage("${player.blockPos} ${box.indexOfPlayerPos(player.blockPos)}".literal)
                BlockPos.iterate(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ).forEach { pos ->
                    structureTeleporter.blocks.add(world.getBlockState(pos))
                }


                val offSet = 1000

                val randomPos = BlockPos(
                    Random.nextInt(-offSet, offSet),
                    Random.nextInt(world.bottomY, world.topY),
                    Random.nextInt(-offSet, offSet)
                )

                val (_, playerIndex, blocks) = structureTeleporter

                val newBox =
                    BlockBox.create(
                        randomPos,
                        randomPos.add(
                            structureTeleporter.blockBox.blockCountX - 1,
                            structureTeleporter.blockBox.blockCountY - 1,
                            structureTeleporter.blockBox.blockCountZ - 1
                        )
                    )
                player.sendMessage("${newBox.dimensions} $newBox".literal)
                newBox.forEachIndexed { index, blockPos ->
                    val state = blocks.getOrNull(index) ?: return@forEachIndexed
                    world.setBlockState(blockPos, state)
                }
                newBox.forEachIndexed { index, blockPos ->
                    if (index == playerIndex) {
                        player.teleport(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble())
                    }
                }
            }
        }
    }

    private fun BlockBox.indexOfPlayerPos(pos: BlockPos): Int {
        var playerIndex = -1
        forEachIndexed { index, blockPos ->
            if (blockPos == pos) {
                playerIndex = index
            }
        }
        return playerIndex
    }

    private fun BlockBox.normalized(): BlockBox {
        return BlockBox.create(Vec3i(0, 0, 0), Vec3i(blockCountX - 1, blockCountY - 1, blockCountZ - 1))
    }

    private fun BlockBox.forEachIndexed(action: (index: Int, BlockPos) -> Unit) {
        BlockPos.iterate(minX, minY, minZ, maxX, maxY, maxZ).forEachIndexed(action::invoke)
    }
}