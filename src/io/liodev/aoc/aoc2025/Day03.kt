package io.liodev.aoc.aoc2025

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import java.lang.Math.pow
import kotlin.math.pow

// --- 2025 Day 3: Lobby ---
class Day03(
    input: String,
) : Day<Long> {
    override val expectedValues = listOf(357L, 17346, 3121910778619, 172981362045136)

    private val banks = input.split("\n")

    override fun solvePart1(): Long = banks.sumOf { bank ->
        bank.joltage()
    }

    override fun solvePart2(): Long = banks.sumOf { bank ->
        bank.joltageUnlimited()
    }
}

private fun String.joltage(): Long {
    val max = this.dropLast(1).maxOf { it.digitToInt() }
    return max * 10L + this.substringAfter("$max").maxOf { it.digitToInt() }
}

private fun String.joltageUnlimited(): Long {
    var acum = 0L
    var i = 0
    for (joltages in 11 downTo 0) {
        val piece = this.substring(i)
        val max = piece.dropLast(joltages).maxOf { it.digitToInt() }
        acum = acum * 10L + max
        i += piece.indexOfFirst { it.digitToInt() == max } + 1
    }
    return acum
}

fun main() {
    val name = Day03::class.simpleName
    val year = 2025
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day03(testInput), Day03(realInput), year)
}
