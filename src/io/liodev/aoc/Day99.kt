package io.liodev.aoc

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// TEMPLATE
class Day99(input: String): Day<Int> {
    override val expectedValues = listOf(-1, -1, -1, -1)

    private val parsedInput = parseInput(input)
    private fun parseInput(input: String): List<String> = input.split("\n")

    override fun solvePart1() = 0

    override fun solvePart2() = 0
}

fun main() {
    val name = Day99::class.simpleName
    val testInput= readInputAsString("src/2022/input/${name}_test.txt")
    val realInput= readInputAsString("src/2022/input/${name}.txt")
    runDay(Day99(testInput), Day99(realInput))
}