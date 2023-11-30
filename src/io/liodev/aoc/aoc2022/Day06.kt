package io.liodev.aoc.aoc2022

import io.liodev.aoc.Day
import io.liodev.aoc.println
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

class Day06(private val input: String): Day<Int> {
    override val expectedValues = listOf(10, 1760, 29, 2974)

    override fun solvePart1() = input.getFirstDistinctPosition(4)

    override fun solvePart2() = input.getFirstDistinctPosition(14)
}

private fun String.getFirstDistinctPosition(window: Int): Int =
    this.windowed(window)
        .takeWhile { it.toSet().size != window }
        .size + window


fun main() {
    val name = Day06::class.simpleName
    val testInput= readInputAsString("src/input/2022/${name}_test.txt")
    val realInput= readInputAsString("src/input/2022/${name}.txt")
    runDay(Day06(testInput), Day06(realInput))
}