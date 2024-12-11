package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import kotlin.math.pow

// --- 2024 Day 11: Plutonian Pebbles ---
class Day11(
    input: String,
) : Day<Long> {
    override val expectedValues = listOf(55312L, 198075, 65601038650482, 235571309320764)

    private val initialArrangement = input.split(" ").map { it.toLong() }

    override fun solvePart1(): Long =
        initialArrangement
            .sumOf { stone -> calculate25Blinks(stone).size }
            .toLong()

    override fun solvePart2(): Long {
        val stonesMap25 = mutableMapOf<Long, List<Long>>()
        var totalStones = 0L

        for (stone in initialArrangement) {
            val stones = calculate25Blinks(stone, stonesMap25)
            stonesMap25[stone] = stones

            for (stone2 in stones) {
                val stones2 = calculate25Blinks(stone2, stonesMap25)
                stonesMap25[stone2] = stones2

                for (stone3 in stones2) {
                    val stones3 = calculate25Blinks(stone3, stonesMap25)
                    stonesMap25[stone3] = stones3
                    totalStones += stones3.size
                }
            }
        }
        return totalStones
    }

    private fun calculate25Blinks(
        stone: Long,
        stonesMap: MutableMap<Long, List<Long>> = mutableMapOf(),
    ): List<Long> {
        if (stonesMap[stone] != null) return stonesMap[stone]!!
        var stones = listOf(stone)
        repeat(25) {
            stones = blink(stones)
        }
        return stones
    }

    private fun blink(stones: List<Long>): List<Long> {
        val result = mutableListOf<Long>()
        for (stone in stones) {
            when {
                stone == 0L -> result.add(1)
                stone.digits() % 2 == 0 -> {
                    var p = 1L
                    repeat(stone.digits() / 2) { p *= 10 }
                    result.add(stone / p)
                    result.add(stone % p)
                }
                else -> result.add(stone * 2024L)
            }
        }
        return result.toList()
    }
}

private fun Long.digits(): Int {
    var p = 1
    var n = this
    while (n >= 10) {
        n /= 10
        p += 1
    }
    return p
}

fun main() {
    val name = Day11::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day11(testInput), Day11(realInput), year, printTimings = true, benchmark = false)
}
