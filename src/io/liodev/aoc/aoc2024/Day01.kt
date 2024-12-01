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
        input
            .split("\n")
            .map { line -> line.split("   ") }
            .let { it.map { it[0].toInt() } to it.map { it[1].toInt() } }

    override fun solvePart1() =
        listPairs.let { (firstList, secondList) ->
            firstList.sorted().zip(secondList.sorted()).sumOf { abs(it.first - it.second) }
        }

    override fun solvePart2() =
        listPairs.let { (firstList, secondList) ->
            firstList.sumOf { a -> a * secondList.count { b -> b == a } }
        }
}

fun main() {
    val name = Day01::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day01(testInput), Day01(realInput), year)
}
