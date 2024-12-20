package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2024 Day 19: Linen Layout ---
class Day19(
    input: String,
) : Day<Long> {
    override val expectedValues = listOf(6L, 322, 16, 715514563508258)

    private val designs = input.substringBefore("\n").split(", ").sortedByDescending { it.length }
    private val requestedTowels = input.lines().drop(2)

    private val arrangementsCount = mutableMapOf<String, Long>()

    override fun solvePart1(): Long = requestedTowels.count { possibleWaysToArrange(it) != 0L }.toLong()

    override fun solvePart2(): Long = requestedTowels.sumOf { possibleWaysToArrange(it) }

    private fun possibleWaysToArrange(pattern: String): Long =
        arrangementsCount.getOrPut(pattern) {
            designs.sumOf { design ->
                when {
                    design == pattern -> 1L
                    pattern.startsWith(design) -> possibleWaysToArrange(pattern.substringAfter(design))
                    else -> 0L
                }
            }
        }
}

fun main() {
    val name = Day19::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day19(testInput), Day19(realInput), year, printTimings = true)
}
