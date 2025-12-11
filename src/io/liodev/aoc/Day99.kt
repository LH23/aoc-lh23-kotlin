package io.liodev.aoc

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// TEMPLATE 2024
class Day99(
    input: String,
) : Day<Long> {
    override val expectedValues = listOf(-1L, -1, -1, -1)

    private val parsedInput = input.split("\n")

    override fun solvePart1(): Long {
        return 0
    }

    override fun solvePart2(): Long {
        return 0
    }
}

fun main() {
    val name = Day99::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day99(testInput), Day99(realInput), year)
}
