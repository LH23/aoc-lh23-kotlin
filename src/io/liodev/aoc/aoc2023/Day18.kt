package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import kotlin.math.abs

// --- 2023 Day 18: Lavaduct Lagoon ---
class Day18(input: String) : Day<Long> {
    override val expectedValues = listOf(62L, 67891, 952_408_144_115, 94_116_351_948_493)

    private val digPlanInstructions = input.split("\n").map { it.toInstruction() }
    private val digPlanRealInstructions = input.split("\n").map { it.toRealInstruction() }

    private fun String.toInstruction(): Instruction {
        val split = this.split(" ")
        return Instruction(split[0][0], split[1].toInt(), split[2])
    }
    private fun String.toRealInstruction(): Instruction {
        val split = this.split(" ")
        // 0 means R, 1 means D, 2 means L, and 3 means U.
        val dir = when (split[2][7]) {
            '0' -> 'R'
            '1' -> 'D'
            '2' -> 'L'
            '3' -> 'U'
            else -> error("Invalid")
        }
        val meters = split[2].substring(2,7).toInt(16)
        return Instruction(dir, meters, split[0]+split[1])
    }

    data class Instruction(val dir: Char, val meters: Int, val color: String)

    override fun solvePart1() = calculateArea(digPlanInstructions)

    override fun solvePart2() = calculateArea(digPlanRealInstructions)

    private fun calculateArea(instructions: List<Instruction>): Long {
        var worker = Coord(0, 0)
        val terrain = mutableListOf(worker)
        for (inst in instructions) {
            val newPosition = when (inst.dir) {
                'U' -> worker.goUp(inst.meters)
                'D' -> worker.goDown(inst.meters)
                'L' -> worker.goLeft(inst.meters)
                'R' -> worker.goRight(inst.meters)
                else -> error("Invalid dir: $inst")
            }
            terrain.add(newPosition)
            worker = newPosition
        }
        return (abs(terrain.zipWithNext().sumOf {(x,y) ->
            (x.c+y.c) * (x.r-y.r).toLong()
        }) + instructions.sumOf { it.meters } ) / 2 + 1
    }
}

fun main() {
    val name = Day18::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(Day18(testInput), Day18(realInput))
}