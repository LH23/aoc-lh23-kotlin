package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.printMatrix

// --- 2024 14
class Day14(
    input: String,
) : Day<Long> {
    // 1, 3, 4, and 1
    override val expectedValues = listOf(12L, 228410028, 0, 8258)

    private val robots = input.split("\n").map { Robot.from(it) }

    data class Robot(
        val p: Coord,
        val v: Coord,
    ) {
        companion object {
            fun from(it: String): Robot {
                val (ps, vs) = it.split(" ")
                val p = ps.drop(2).split(",").map { it.toInt() }
                val v = vs.drop(2).split(",").map { it.toInt() }
                return Robot(Coord(p[1], p[0]), Coord(v[1], v[0]))
            }
        }
    }

    private fun mod(
        a: Int,
        b: Int,
        mod: Int,
    ): Int = if ((a + b) % mod < 0) ((a + b) % mod) + mod else ((a + b) % mod)

    override fun solvePart1(): Long {
        val (rows, cols) = if (robots[0].p == Coord(4, 0)) Pair(7, 11) else Pair(103, 101)
        val currPositions = robots.map { it.p }.toMutableList()
        for ((i, robot) in robots.withIndex()) {
            repeat(100) {
                currPositions[i] =
                    Coord(
                        mod(currPositions[i].r, robot.v.r, rows),
                        mod(currPositions[i].c, robot.v.c, cols),
                    )
            }
        }
        return calculateQuadrants(currPositions, rows, cols).reduce { a, b -> a * b }.toLong()
    }

    override fun solvePart2(): Long {
        val (rows, cols) = if (robots[0].p == Coord(4, 0)) Pair(7, 11) else Pair(103, 101)
        val currPositions = robots.map { it.p }.toMutableList()
        val repeats = 100000
        repeat(repeats) {
            for ((i, robot) in robots.withIndex()) {
                currPositions[i] =
                    Coord(
                        mod(currPositions[i].r, robot.v.r, rows),
                        mod(currPositions[i].c, robot.v.c, cols),
                    )
            }
            if (contiguousCols(currPositions.toSet(), rows, cols) > 8) {
//                println("/n $it")
//                printRobots(currPositions, rows, cols)
                return (it + 1).toLong()
            }
        }
        return 0L
    }

    private fun contiguousCols(
        currPositions: Set<Coord>,
        rows: Int,
        cols: Int,
    ): Int =
        (0..<rows).maxOf { r ->
            var max = 0
            var contiguous = 0
            for (c in 0..<cols) {
                if (Coord(r, c) in currPositions) {
                    contiguous++
                    if (contiguous > max) max = contiguous
                } else {
                    contiguous = 0
                }
            }
            max
        }

    private fun printRobots(
        currPositions: MutableList<Coord>,
        rows: Int,
        cols: Int,
    ) {
        val bathroom = List(rows) { MutableList(cols) { ' ' } }
        for (pos in currPositions) {
            bathroom[pos.r][pos.c] = 'R'
        }
        bathroom.printMatrix()
    }

    private fun calculateQuadrants(
        currPositions: MutableList<Coord>,
        rows: Int,
        cols: Int,
    ): List<Int> {
        var q1 = 0
        var q2 = 0
        var q3 = 0
        var q4 = 0
        for (pos in currPositions) {
            if (pos.r in 0..<rows / 2 && pos.c in 0..<cols / 2) q1++
            if (pos.r in 0..<rows / 2 && pos.c in cols / 2 + 1..cols) q2++
            if (pos.r in rows / 2 + 1..rows && pos.c in 0..<cols / 2) q3++
            if (pos.r in rows / 2 + 1..rows && pos.c in cols / 2 + 1..cols) q4++
        }
        // println("q1 = $q1, q2 = $q2, q3 = $q3, q4 = $q4")
        return listOf(q1, q2, q3, q4)
    }
}

fun main() {
    val name = Day14::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day14(testInput), Day14(realInput), year)
}
