package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2024 Day 3: Mull It Over ---
class Day03(
    input: String,
) : Day<Long> {
    override val expectedValues = listOf(322L, 184576302, 96, 118173507)

    private val corruptedMemory = input.split("\n").joinToString("")
    private val mulRegex = """mul\((\d+),(\d+)\)""".toRegex()

    override fun solvePart1(): Long =
        mulRegex.findAll(corruptedMemory).sumOf { match ->
            match.multiply()
        }

    override fun solvePart2(): Long =
        mulRegex.findAll(corruptedMemory.processDoDontInstructions()).sumOf { match ->
            match.multiply()
        }
}

private fun MatchResult.multiply() = groups[1]!!.value.toLong() * groups[2]!!.value.toLong()

private fun String.processDoDontInstructions(): String {
    var next = 0
    var processed = ""
    while (next > -1) {
        val nextDont = this.indexOf("don't()", next)
        if (nextDont != -1) {
            processed += this.substring(next, nextDont)
            next = this.indexOf("do()", nextDont)
        } else {
            processed += this.substring(next)
            next = -1
        }
    }
    return processed
}

fun main() {
    val name = Day03::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day03(testInput), Day03(realInput), year)
}
