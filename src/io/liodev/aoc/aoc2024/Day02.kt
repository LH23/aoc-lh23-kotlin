package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2024 Day 2: Red-Nosed Reports ---
class Day02(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(2, 526, 4, 566)

    private val reportLevels = input.lines().map { line -> line.split(" ").map { it.toInt() } }

    override fun solvePart1() = reportLevels.count { safe(it) }

    override fun solvePart2() = reportLevels.count { safe(it) || toleratingOneError2(it) }

    private fun safe(levels: List<Int>): Boolean =
        levels.zipWithNext().let { zips ->
            zips.all { (a, b) -> a < b && b - a < 4 } ||
                zips.all { (a, b) -> a > b && a - b < 4 }
        }

    // Quadratic version
    private fun toleratingOneError(levels: List<Int>): Boolean =
        levels.indices.any { i ->
            safe(levels.subList(0, i) + levels.subList(i + 1, levels.size))
        }

    // O(N) version
    private fun toleratingOneError2(levels: List<Int>): Boolean {
        val index = indexOfFirstFail(levels)
        return (
            safe(levels.filterIndexed { i, _ -> i != index }) ||
                safe(levels.filterIndexed { i, _ -> i != index - 1 })
        )
    }

    private fun indexOfFirstFail(levels: List<Int>): Int {
        val zips = levels.zipWithNext()
        val ascendingCount = zips.count { (a, b) -> a < b }

        return if (ascendingCount > levels.size / 2) {
            zips.indexOfFirst { (a, b) -> !(a < b && b - a < 4) } + 1
        } else {
            zips.indexOfFirst { (a, b) -> !(a > b && a - b < 4) } + 1
        }
    }
}

fun main() {
    val name = Day02::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day02(testInput), Day02(realInput), year, printTimings = true)
}
