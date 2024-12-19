package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2024 Day 19: Linen Layout ---
class Day19(
    input: String,
) : Day<Long> {
    override val expectedValues = listOf(6L, 322, 16, 715514563508258)

    private val designs = input.split("\n\n")[0].split(", ").sortedByDescending { it.length }
    private val requestedTowels = input.split("\n\n")[1].lines()

    private val arrangementsCount = mutableMapOf<String, Long>()

    override fun solvePart1(): Long = requestedTowels.count { possibleWaysToArrange(it) != 0L }.toLong()

    override fun solvePart2(): Long = requestedTowels.sumOf { possibleWaysToArrange(it) }

    private fun possibleWaysToArrange(pattern: String): Long {
        if (arrangementsCount.getOrDefault(pattern, -1L) != -1L) {
            return arrangementsCount[pattern]!!
        }
        var count = 0L
        for (d in designs) {
            if (d == pattern) {
                count++
            } else if (pattern.startsWith(d)) {
                count += possibleWaysToArrange(pattern.substringAfter(d))
            }
        }
        arrangementsCount[pattern] = count
        return count
    }
}

fun main() {
    val name = Day19::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day19(testInput), Day19(realInput), year)
}
