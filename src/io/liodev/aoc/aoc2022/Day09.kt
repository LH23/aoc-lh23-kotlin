package io.liodev.aoc.aoc2022

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.Dir
import io.liodev.aoc.utils.parseDir
import io.liodev.aoc.utils.printMatrix
import kotlin.math.abs

class Day09(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(88, 6044, 36, -1)

    private val instructions = parseInput(input)

    private fun parseInput(input: String): List<Instr> =
        input
            .split("\n")
            .map { it.toInstruction() }

    override fun solvePart1(): Int {
        var head = Coord(0, 0)
        var tail = Coord(0, 0)
        val tailPositions = mutableSetOf(tail)
        for (instr in instructions) {
            repeat(instr.steps) {
                val (newHead, newTail) = nextPosition(head, tail, instr.dir)
                println("Moved from $head (${instr.steps}/${instr.dir}) to $newHead (tail $newTail)")
                if (newTail == head) {
                    println("Position to add: $newTail")
                    tailPositions.add(newTail)
                }
                head = newHead
                tail = newTail
            }
        }
        println("tailPositions $tailPositions")

        return tailPositions.size
    }

    override fun solvePart2(): Int {
        val knots = List(size = 10) { Coord(0, 0) }.toMutableList()

        val tailPositions = mutableSetOf(knots[9])
        for (instr in instructions) {
            println("Starting Instruction: ${instr.steps} ${instr.dir}")
            repeat(instr.steps) {
                var oldHead = knots[0]
                knots[0] = oldHead.move(instr.dir, 1)

                for (i in 0..<knots.lastIndex) {
                    val head = knots[i]
                    val tail = knots[i + 1]
                    val newTail = distanced(head, tail, oldHead)

                    println("Dist $i from $head to $tail (new tail $newTail, oldHead $oldHead)")
                    if (tail == newTail) {
                        println("breaking movement ${instr.dir} ${i + 1}/${instr.steps}")
                        println("KnotsAfterBreak: $knots")
                        break
                    }
                    oldHead = tail
                    knots[i + 1] = newTail
                    // println("Knots after change: $knots")

                    if (i == knots.lastIndex - 1) {
                        println("Position to add: $newTail")
                        tailPositions.add(newTail)
                    }
                }
            }
            println("Post Instruction knots: $knots")
            printTailMatrix(knots.toSet())
            println("-----")
        }
        println("tailPositions $tailPositions")
        printTailMatrix(tailPositions)
        return tailPositions.size
    }

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

    private fun distanced(
        h: Coord,
        t: Coord,
        oldH: Coord,
    ): Coord =
        if (abs(h.c - t.c) > 1 || abs(h.r - t.r) > 1) {
            if (abs(h.r - t.r) % 2 == 0 && abs(h.c - t.c) % 2 == 0) {
                Coord((h.r + t.r) / 2, (h.c + t.c) / 2)
            } else if (abs(h.c - t.c) == 2) {
                Coord(h.r, oldH.c)
            } else {
                Coord(oldH.r, h.c)
            }
        } else {
            t
        }

    private fun nextPosition(
        h: Coord,
        t: Coord,
        dir: Dir?,
    ): Pair<Coord, Coord> {
        val nh = if (dir != null) h.move(dir, 1) else h
        var nt = t
        if (abs(nh.c - t.c) > 1 || abs(nh.r - t.r) > 1) {
            nt = h
        }
        return Pair(nh, nt)
    }

    private fun String.toInstruction(): Instr = Instr(parseDir(this[0]), this.drop(2).toInt())

    data class Instr(
        val dir: Dir,
        val steps: Int,
    )
}

fun main() {
    val name = Day09::class.simpleName
    val testInput = readInputAsString("src/input/2022/${name}_test.txt")
    val realInput = readInputAsString("src/input/2022/$name.txt")
    runDay(Day09(testInput), Day09(realInput), skipTests = listOf(true, true, false, false))
}
