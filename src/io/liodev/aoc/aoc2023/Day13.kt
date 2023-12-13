package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2023 Day 13: Point of Incidence ---
class Day13(input: String) : Day<Int> {
    override val expectedValues = listOf(405, 34821, 400, 36919)

    private val patterns = input.split("\n\n").map { it.lines() }

    private fun List<String>.findReflectingRow(smudges: Int = 0): Int {
        for (i in 0..<lastIndex) {
            if (differences(this[i], this[i + 1]) <= smudges) {
                var u = i
                var d = i + 1
                var sm = 0
                var reflection = true
                while (u >= 0 && d <= lastIndex) {
                    sm += differences(this[u], this[d])
                    if (sm > smudges) {
                        reflection = false; break
                    }
                    u--
                    d++
                }
                if (reflection && sm == smudges) return i + 1
            }
        }
        return -1
    }

    private fun differences(s1: String, s2: String): Int {
        return s1.zip(s2).count { (c1, c2) -> c1 != c2 }
    }

    private fun List<String>.findReflectingCol(smudges: Int = 0): Int {
        val transpose = this[0].indices.map { j ->
            this.indices.map { i -> this[i][j] }.joinToString("") { it.toString() }
        }
        return transpose.findReflectingRow(smudges)
    }

    override fun solvePart1(): Int {
        var cols = 0
        var rows = 0
        for (pattern in patterns) {
            val row = pattern.findReflectingRow()
            if (row != -1) {
                rows += row; continue
            }
            val col = pattern.findReflectingCol()
            if (col != -1) {
                cols += col; continue
            }
        }
        return cols + 100 * rows
    }

    override fun solvePart2(): Int {
        var cols = 0
        var rows = 0
        for (pattern in patterns) {
            val row = pattern.findReflectingRow(1)
            if (row != -1) {
                rows += row; continue
            }
            val col = pattern.findReflectingCol(1)
            if (col != -1) {
                cols += col; continue
            }
        }
        return cols + 100 * rows
    }
}

fun main() {
    val name = Day13::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(Day13(testInput), Day13(realInput))
}