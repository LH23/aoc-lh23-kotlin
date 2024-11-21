package io.liodev.aoc.aoc2022

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

class Day04(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(2, 595, 4, 952)

    data class RangePair(
        val range1: IntRange,
        val range2: IntRange,
    ) {
        fun totalOverlap() = range1 contains range2 || range2 contains range1

        fun partialOverlap() = range1 overlaps range2
    }

    private fun String.toRangePair(): RangePair {
        val result = Regex("""(\d+)-(\d+),(\d+)-(\d+)""").find(this)
        val (start1, end1, start2, end2) = result!!.destructured
        return RangePair(
            start1.toInt()..end1.toInt(),
            start2.toInt()..end2.toInt(),
        )
    }

    private val rangePairs = parseInput(input)

    private fun parseInput(input: String): List<RangePair> =
        input.split("\n").map {
            it.toRangePair()
        }

    override fun solvePart1() = rangePairs.count { it.totalOverlap() }

    override fun solvePart2() = rangePairs.count { it.partialOverlap() }
}

infix fun IntRange.contains(other: IntRange): Boolean = this.first <= other.first && other.last <= this.last

infix fun IntRange.overlaps(other: IntRange) = this.first <= other.last && other.first <= this.last

fun main() {
    val name = Day04::class.simpleName
    val year = 2022
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day04(testInput), Day04(realInput), year)
}
