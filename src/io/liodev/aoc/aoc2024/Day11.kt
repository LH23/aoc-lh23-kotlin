package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2024 Day 11: Plutonian Pebbles ---
class Day11(
    input: String,
) : Day<Long> {
    override val expectedValues = listOf(55312L, 198075, 65601038650482, 235571309320764)

    private val initialArrangement = input.split(" ").map { it.toLong() }

    override fun solvePart1(): Long =
        initialArrangement
            .sumOf { stone -> calculateBlinks(25, stone) }

    override fun solvePart2(): Long {
        val stonesMap = mutableMapOf<Pair<Int, Long>, Long>()
        return initialArrangement
            .sumOf { stone -> calculateBlinks(75, stone, stonesMap) }
    }

    private fun calculateBlinks(
        repeat: Int,
        stone: Long,
        stonesMap: MutableMap<Pair<Int, Long>, Long> = mutableMapOf(),
    ): Long {
        if (stonesMap[repeat to stone] != null) return stonesMap[repeat to stone]!!
        val stones = blink(listOf(stone))
        if (repeat == 1) return stones.size.toLong()
        val size = stones.sumOf { s -> calculateBlinks(repeat - 1, s, stonesMap) }
        stonesMap[repeat to stone] = size
        return size
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
    runDay(Day11(testInput), Day11(realInput), year, printTimings = true)
}
