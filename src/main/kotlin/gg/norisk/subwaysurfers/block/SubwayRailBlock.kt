package gg.norisk.subwaysurfers.block

import net.minecraft.block.BlockState
import net.minecraft.block.RailBlock
import net.minecraft.block.enums.RailShape

class SubwayRailBlock(settings: Settings) : RailBlock(settings) {
    init {
        defaultState =
            ((stateManager.defaultState as BlockState).with(SHAPE, RailShape.NORTH_SOUTH) as BlockState).with(
                WATERLOGGED, false
            ) as BlockState
    }
}