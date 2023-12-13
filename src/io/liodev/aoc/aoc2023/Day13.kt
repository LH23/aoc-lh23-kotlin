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
                while (u >= 0 && d <= lastIndex) {
                    sm += differences(this[u], this[d])
                    if (sm > smudges) break
                    u--; d++
                }
                if (sm == smudges) return i + 1
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

    override fun solvePart1(): Int =
        patterns.map { pattern ->
            val mirrorRow = pattern.findReflectingRow()
            val mirrorCol = if (mirrorRow == -1) pattern.findReflectingCol() else -1
            if (mirrorRow == -1 && mirrorCol == -1) error("No line of reflection in:\n $pattern")
            Pair(mirrorRow, mirrorCol)
        }.sumOf { (r, c) ->
            (if (r != -1) r else 0) * 100 + (if (c != -1) c else 0)
        }


    override fun solvePart2(): Int =
        patterns.map { pattern ->
            val mirrorRow = pattern.findReflectingRow(1)
            val mirrorCol = if (mirrorRow == -1) pattern.findReflectingCol(1) else -1
            if (mirrorRow == -1 && mirrorCol == -1) error("No line of reflection in:\n $pattern")
            Pair(mirrorRow, mirrorCol)
        }.sumOf { (r, c) ->
            (if (r != -1) r else 0) * 100 + (if (c != -1) c else 0)
        }
}

fun main() {
    val name = Day13::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(Day13(testInput), Day13(realInput), printTimings = true)
}