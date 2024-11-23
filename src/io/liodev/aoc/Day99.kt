package io.liodev.aoc

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// TEMPLATE (replace Day99 for DayXX and year)
class Day99(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(-1, -1, -1, -1)

    private val parsedInput = parseInput(input)

    private fun parseInput(input: String): List<String> = input.split("\n")

    override fun solvePart1() = 0

    override fun solvePart2() = 0
}

fun main() {
    val name = Day99::class.simpleName
    val year = 2022
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day99(testInput), Day99(realInput), year)
}
