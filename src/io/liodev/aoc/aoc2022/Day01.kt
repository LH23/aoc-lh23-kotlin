package io.liodev.aoc.aoc2022

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

class Day01(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(24000, 69177, 45000, 207456)

    private val calories = parseInput(input)

    private fun parseInput(input: String): List<Int> =
        input
            .split("\n\n")
            .map { it.lines().sumOf(String::toInt) }
            .sortedDescending()

    override fun solvePart1(): Int = calories.first()

    override fun solvePart2(): Int = calories.take(3).sum()
}

fun main() {
    val name = Day01::class.simpleName
    val year = 2022
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day01(testInput), Day01(realInput), year)
}
