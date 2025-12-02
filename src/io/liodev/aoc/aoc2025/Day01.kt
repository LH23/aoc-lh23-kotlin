package io.liodev.aoc.aoc2025

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

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
        var zeroes = 0
        for (inst in instructions) {
            position = (position + inst).mod(100)
            if (position == 0) {
                zeroes++
            }
        }
        return zeroes
    }

    override fun solvePart2(): Int {
        var position = 50
        var zeroes = 0
        for (inst in instructions) {
            val min = minOf(position, position + inst)
            val max = maxOf(position, position + inst)
            zeroes += countZeroesInRange(min, max)
            position = (position + inst).mod(100)
            if (position == 0) {
                // will be counted in the next range again
                zeroes--
            }
        }
        return zeroes
    }

    private fun countZeroesInRange(min: Int, max: Int) = max.floorDiv(100) - min.ceilDiv(100) + 1
}

private fun Int.ceilDiv(divider: Int) = (this + divider - 1).floorDiv(divider)


fun main() {
    val name = Day01::class.simpleName
    val year = 2025
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day01(testInput), Day01(realInput), year, printTimings = true)
}
