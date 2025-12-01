package io.liodev.aoc.aoc2025

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import kotlin.math.abs

// --- 2025 Day 1 Secret Entrance ---
class Day01(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(3, 1066, 12, 6223)

    private val instructions = input.split("\n").map {
        (if (it[0] == 'L') -1 else 1) * (it.drop(1).toInt())
    }

    override fun solvePart1(): Int {
        var position = 50
        var zero = 0
        for (inst in instructions) {
            position = (position + inst).mod(100)
            if (position == 0) {
                zero++
            }
        }
        return zero
    }

    override fun solvePart2(): Int {
        var position = 50
        var zero = 0
        for (inst in instructions) {
            val min = minOf(position, position + inst)
            val max = maxOf(position, position + inst)
            zero += (min..max).count { it.mod(100) == 0 }
            position = (position + inst).mod(100)
            if (position == 0) {
                zero--
            }
        }
        return zero
    }
}

fun main() {
    val name = Day01::class.simpleName
    val year = 2025
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day01(testInput), Day01(realInput), year, printTimings = true)
}
