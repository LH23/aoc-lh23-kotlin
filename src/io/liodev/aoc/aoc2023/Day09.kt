package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// 2023 Day09
class Day09(input: String) : Day<Long> {
    override val expectedValues = listOf(114L, 1637452029, 2, 908)

    private val histories = input.split("\n").map { it.split(" ").map { it.toLong() } }

    override fun solvePart1(): Long =
        histories.sumOf { history -> history.calculateNextElement() }

    private fun List<Long>.calculateNextElement(): Long {
        val seq = this.zipWithNext { a, b -> b - a }

        return if (seq.all { it == 0L }) last()
        else last() + seq.calculateNextElement()
    }

    override fun solvePart2() =
        histories.sumOf { history -> history.calculatePreviousElement() }

    private fun List<Long>.calculatePreviousElement(): Long {
        val seq = this.zipWithNext { a, b -> b - a }

        return if (seq.all { it == 0L }) first()
        else first() - seq.calculatePreviousElement()
    }
}


fun main() {
    val name = Day09::class.simpleName
    val year = 2023
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/${name}.txt")
    runDay(Day09(testInput), Day09(realInput), year)
}