package io.liodev.aoc.aoc2022

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2022 Day 2: Rock Paper Scissors ---
class Day02(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(15, 13009, 12, 10398)

    private val matches = parseInput(input)

    private fun parseInput(input: String): List<String> = input.split("\n")

    override fun solvePart1() =
        matches.sumOf { match ->
            // R P S
            // A B C
            // X Y Z
            when (match) {
                "A X" -> 3 + 1
                "A Y" -> 6 + 2
                "A Z" -> 0 + 3
                "B X" -> 0 + 1
                "B Y" -> 3 + 2
                "B Z" -> 6 + 3
                "C X" -> 6 + 1
                "C Y" -> 0 + 2
                "C Z" -> 3 + 3
                else -> throw Exception("Invalid match")
            }.toInt()
        }

    override fun solvePart2() =
        matches.sumOf { match ->
            // R P S   L D W
            // A B C   X y Z
            when (match) {
                "A X" -> 0 + 3
                "A Y" -> 3 + 1
                "A Z" -> 6 + 2
                "B X" -> 0 + 1
                "B Y" -> 3 + 2
                "B Z" -> 6 + 3
                "C X" -> 0 + 2
                "C Y" -> 3 + 3
                "C Z" -> 6 + 1
                else -> throw Exception("Invalid match")
            }.toInt()
        }
}

fun main() {
    val name = Day02::class.simpleName
    val year = 2022
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day02(testInput), Day02(realInput), year)
}
