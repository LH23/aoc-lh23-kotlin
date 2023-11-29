package io.liodev.aoc.aoc2022

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

class Day03(input: String) : Day<Int> {
    // TEST1: 16 (p), 38 (L), 42 (P), 22 (v), 20 (t), and 19 (s); the sum of these is 157.
    // TEST2: they are 18 (r) for the first group and 52 (Z) for the second group. Total 70
    override val expectedValues = listOf(157, 7826, 70, 2577)

    private val parsedInput = parseInput(input)
    private fun parseInput(input: String): List<String> = input.split("\n")

    private fun priority(c: Char): Int {
        return when (c) {
            in 'a'..'z' -> c - 'a' + 1
            in 'A'..'Z' -> c - 'A' + 27
            else -> throw IllegalArgumentException("Invalid char $c")
        }
    }

    override fun solvePart1() = parsedInput
        .map { rucksack ->
            listOf(
                rucksack.substring(0, rucksack.length / 2).toSet(),
                rucksack.substring(rucksack.length / 2).toSet()
            ).reduce { acc, items ->
                acc intersect items
            }.single()
        }.sumOf { priority(it) }

    override fun solvePart2() = parsedInput
        .chunked(3)
        .map { rucksacks ->
            rucksacks
                .map { it.toSet() }
                .reduce { acc, items ->
                    acc intersect items
                }.single()
        }.sumOf { priority(it) }
}

fun main() {
    val name = Day03::class.simpleName
    val testInput = readInputAsString("src/2022/input/${name}_test.txt")
    val realInput = readInputAsString("src/2022/input/${name}.txt")
    runDay(Day03(testInput), Day03(realInput))
}