package io.liodev.aoc.aoc2025

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2025 Day 5: Cafeteria ---
class Day05(
    input: String,
) : Day<Long> {
    override val expectedValues = listOf(3L, 681, 14, 348820208020395)

    private val freshRanges = input.substringBefore("\n\n")
        .split("\n")
        .map { rangeString ->
            rangeString.split("-").let { it[0].toLong()..it[1].toLong() }
        }

    private val ingredients = input.substringAfter("\n\n").lines().map { it.toLong() }

    override fun solvePart1(): Long =
        ingredients.count { ingredientID -> freshRanges.any { it.contains(ingredientID)} }.toLong()

    override fun solvePart2(): Long {
        val disjointRanges = mutableSetOf<LongRange>()
        for (freshRange in freshRanges) {
            val overlapping = disjointRanges.filter { it.overlaps(freshRange) }.toSet()
            disjointRanges.removeAll(overlapping)

            val newRange = overlapping.fold(freshRange) { acc, range ->
                minOf(acc.first, range.first)..maxOf(acc.last, range.last)
            }
            disjointRanges.add(newRange)
        }
        return disjointRanges.sumOf { it.last + 1 - it.first }
    }
}

private fun LongRange.overlaps(other: LongRange) = this.first <= other.last && other.first <= this.last

fun main() {
    val name = Day05::class.simpleName
    val year = 2025
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day05(testInput), Day05(realInput), year, printTimings = true)
}
