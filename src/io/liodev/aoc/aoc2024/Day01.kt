package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import kotlin.math.abs

// --- Day 1 2024: Historian Hysteria ---
class Day01(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(11, 1189304, 31, 24349736)

    private val listPairs =
        input.split("\n").map { it.split("   ").let { it[0].toInt() to it[1].toInt() } }

    override fun solvePart1(): Int {
        val a = listPairs.map { it.first }.sorted()
        val b = listPairs.map { it.second }.sorted()
        return a.zip(b).sumOf { abs(it.first - it.second) }
    }

    override fun solvePart2(): Int {
        val b = listPairs.map { it.second }.sorted()

        return listPairs.sumOf { pair -> pair.first * b.count { pair.first == it } }
    }
}

fun main() {
    val name = Day01::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day01(testInput), Day01(realInput), year)
}
