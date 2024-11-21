package io.liodev.aoc.aoc2022

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.Dir
import io.liodev.aoc.utils.parseDir
import io.liodev.aoc.utils.printMatrix
import kotlin.math.abs
import kotlin.math.sign

// --- Day 9: Rope Bridge ---
class Day09(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(88, 6044, 36, 2384)

    private val instructions = parseInput(input)

    private fun parseInput(input: String): List<Instr> = input.split("\n").map { it.toInstruction() }

    override fun solvePart1() = countTailPositions(instructions, 2)

    override fun solvePart2() = countTailPositions(instructions, 10)

    private fun countTailPositions(
        instructions: List<Instr>,
        size: Int,
    ): Int {
        val knots = List(size) { Coord(0, 0) }.toMutableList()

        val tailPositions = mutableSetOf(knots.last())
        for (instr in instructions) {
            repeat(instr.steps) {
                knots[0] = knots[0].move(instr.dir, 1)

                for (i in 0..<knots.lastIndex) {
                    val newTail = newTailPosition(knots[i], knots[i + 1])
                    if (knots[i] == newTail) {
                        break
                    }
                    knots[i + 1] = newTail
                    if (i == knots.lastIndex - 1) {
                        tailPositions.add(newTail)
                    }
                }
            }
        }
        return tailPositions.size
    }

    private fun newTailPosition(
        h: Coord,
        t: Coord,
    ) = when {
        areSeparated(h, t) -> t + Coord((h.r - t.r).sign, (h.c - t.c).sign)
        else -> t
    }

    private fun areSeparated(
        h: Coord,
        t: Coord,
    ) = abs(h.c - t.c) > 1 || abs(h.r - t.r) > 1

    private fun String.toInstruction(): Instr = Instr(parseDir(this[0]), this.drop(2).toInt())

    data class Instr(
        val dir: Dir,
        val steps: Int,
    )

    // print tail positions in array
    private fun printTailMatrix(positions: Collection<Coord>) {
        val maxR = positions.maxOf { it.r }
        val minR = positions.minOf { it.r }
        val n = maxR - minR + 1
        val maxC = positions.maxOf { it.c }
        val minC = positions.minOf { it.c }
        val m = maxC - minC + 1

        val matrix = List(n) { MutableList(m) { '.' } }
        for (coord in positions) {
            matrix[coord.r - minR][coord.c - minC] = '#'
        }
        matrix.printMatrix()
    }
}

fun main() {
    val name = Day09::class.simpleName
    val year = 2022
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day09(testInput), Day09(realInput), year)
}
