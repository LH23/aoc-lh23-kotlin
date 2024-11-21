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

    private fun String.toInstruction() = this.split(" ")
        .let { (dir, meters, _) ->
            Instruction(dir[0], meters.toInt())
        }

    private fun String.toRealInstruction() = this.split(" ")[2].let { hexa ->
        val dir = when (hexa[7]) {
            '0' -> 'R'
            '1' -> 'D'
            '2' -> 'L'
            '3' -> 'U'
            else -> error("Invalid dir")
        }
        val meters = hexa.substring(2, 7).toInt(16)
        Instruction(dir, meters)
    }

    data class Instruction(val dir: Char, val meters: Int)

    override fun solvePart1() = calculateArea(digPlanInstructions)

    override fun solvePart2() = calculateArea(digPlanRealInstructions)

    private fun calculateArea(instructions: List<Instruction>): Long {
        var diggerPos = Coord(0, 0)
        val terrain = mutableListOf(diggerPos)
        for (inst in instructions) {
            val newPosition = when (inst.dir) {
                'U' -> diggerPos.goUp(inst.meters)
                'D' -> diggerPos.goDown(inst.meters)
                'L' -> diggerPos.goLeft(inst.meters)
                'R' -> diggerPos.goRight(inst.meters)
                else -> error("Invalid dir: $inst")
            }
            terrain.add(newPosition)
            diggerPos = newPosition
        }
        return (abs(terrain.zipWithNext().sumOf { (x, y) ->
            (x.c + y.c) * (x.r - y.r).toLong() // Trapezoid formula
        }) + instructions.sumOf { it.meters }) / 2 + 1
    }
}

fun main() {
    val name = Day18::class.simpleName
    val year = 2023
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/${name}.txt")
    runDay(Day18(testInput), Day18(realInput), year)
}