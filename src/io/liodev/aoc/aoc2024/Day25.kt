package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Dir
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.get
import io.liodev.aoc.utils.times

// --- 2024 Day 25: Code Chronicle ---
class Day25(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(3, 3344, 2024, 2024)

    private val keysLocks = input.split("\n\n").map { it.lines().map { it.toList() } }

    override fun solvePart1(): Int {
        val locks = keysLocks.filter{ it.first().all { it == '#' }}
        val keys = keysLocks.filter{ it.last().all { it == '#' }}
        val locksHeights = locks.map { heights(it, Dir.South) }
        val keysHeights = keys.map { heights(it, Dir.North) }

        return locksHeights.times(keysHeights).count { (lock, key) -> fits(lock, key) }
    }

    private fun fits(lock: List<Int>, key: List<Int>): Boolean {
        return lock.zip(key).all { (lh, kh) -> lh + kh < 6 }
    }

    private fun heights(matrix: List<List<Char>>, dir: Dir): List<Int> {
        return matrix[0].indices.map { c ->
            var coord = Coord(if (dir == Dir.South) 0 else matrix.lastIndex, c)
            var height = -1
            while (matrix[coord] == '#') {
                height++
                coord = coord.move(dir)
            }
            height
        }
    }

    override fun solvePart2(): Int {
        return 2024
    }
}

fun main() {
    val name = Day25::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day25(testInput), Day25(realInput), year)
}
