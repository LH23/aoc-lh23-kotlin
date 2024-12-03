package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2024 Day 3: Mull It Over ---
class Day03(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(322, 184576302, 96, 118173507)

    private val corruptedMemory = input.split("\n").joinToString("")
    private val mulRegex = """mul\((\d+),(\d+)\)""".toRegex()
    private val dontDoSegmentRegex = """(don't\(\).*?(do\(\)|${'$'}))""".toRegex()

    override fun solvePart1() =
        mulRegex.findAll(corruptedMemory).sumOf { match ->
            match.multiply()
        }

    override fun solvePart2() =
        mulRegex.findAll(corruptedMemory.processDoDontInstructions()).sumOf { match ->
            match.multiply()
        }

    private fun MatchResult.multiply() = groups[1]!!.value.toInt() * groups[2]!!.value.toInt()

    private fun String.processDoDontInstructions() = replace(dontDoSegmentRegex, "")
}


fun main() {
    val name = Day03::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day03(testInput), Day03(realInput), year)
}
