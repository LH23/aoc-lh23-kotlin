package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.checkResult
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.parityFloodFillLimit
import io.liodev.aoc.utils.times

// --- 2023 Day 21: Step Counter ---
class Day21(val input: String) : Day<Long> {
    override val expectedValues = listOf(42L, 3724, 359867795768093, 620348631910321)

    private var garden = input.split("\n").map { it.toMutableList() }

    override fun solvePart1(): Long {
        garden.parityFloodFillLimit(
            Coord((garden.indices * garden[0].indices).first() { (i, j) -> garden[i][j] == 'S' }),
            'A', listOf('.', 'S'), 64
        )
        return garden.sumOf { r -> r.count { it == 'B' }.toLong() }
    }

    override fun solvePart2(): Long {
        val cache = generateCacheTable(garden.size)
        //cache.map { (k,v) -> println("$k: $v") }

        // for validation with Day21_test3.txt
//        checkResult("s0", calculatePos(cache, garden.size, 0), 1L) // 1 + 0 + 0 + 0
//        checkResult("s1", calculatePos(cache, garden.size, 1), 4L) // 4 + 0 + 0 + 0
//        checkResult("s2", calculatePos(cache, garden.size, 2), 9L) // 5 + 0 + 4 + 0
//        checkResult("s3", calculatePos(cache, garden.size, 3), 16L) // 0 + 4 + 12 + 0
//        checkResult("s4", calculatePos(cache, garden.size, 4), 25L) // 0 + 5 + 16 + 4
//        checkResult("s5", calculatePos(cache, garden.size, 5), 36L) // 0 + 12 + 24 + 0 + 0
//        checkResult("s6", calculatePos(cache, garden.size, 6), 49L) // 0 + 13 + 32 + 0 + 4
//        checkResult("s7", calculatePos(cache, garden.size, 7), 64L) // 0 + 12 + 44 + 0 + 8
//        checkResult("s8", calculatePos(cache, garden.size, 8), 81L) // 0 + 13 + 48 + 4 + 16
//        checkResult("s9", calculatePos(cache, garden.size, 9), 100L) // 0 + 12 + 52 + 12 + 24
//        checkResult("s10", calculatePos(cache, garden.size, 10), 121L) // 0 + (13+48) + 24 + 0 + 36

        return calculatePos(cache, garden.size, 26501365)
    }

    private fun generateCacheTable(n: Int): Map<Coord, List<Pair<Long, Long>>> {
        val entryPoints = listOf(
            Coord(0, 0),
            Coord(0, n / 2),
            Coord(0, n - 1),
            Coord(n / 2, 0),
            Coord(n / 2, n / 2),
            Coord(n / 2, n - 1),
            Coord(n - 1, 0),
            Coord(n - 1, n / 2),
            Coord(n - 1, n - 1),
        )

        return entryPoints.associateWith { coord ->
            (0..<n).map { steps ->
                val tmpGarden = input.split("\n").map { it.toMutableList() }
                tmpGarden.parityFloodFillLimit(coord, 'A', listOf('.', 'S'), steps)
                (tmpGarden.sumOf { r ->
                    r.count { it == 'A' }.toLong()
                } to tmpGarden.sumOf { r -> r.count { it == 'B' }.toLong() })
            }
        }
    }

    private fun calculatePos(cache: Map<Coord, List<Pair<Long, Long>>>, n: Int, steps: Int): Long {
        val fullSquares = steps / n
        val position = (steps % n - 1).coerceAtLeast(0)
        // println(position)

        if (steps <= n / 2) {
            val result = cache[Coord(n / 2, n / 2)]!![position + 1]
            return if (steps % 2 == 0) result.even() else result.odd()
        }

        val cornersEven = cache[Coord(0, 0)]!![position].even() +
                    cache[Coord(0, n - 1)]!![position].even() +
                    cache[Coord(n - 1, 0)]!![position].even() +
                    cache[Coord(n - 1, n - 1)]!![position].even()

        val cornersOdd = cache[Coord(0, 0)]!![position].odd() +
                    cache[Coord(0, n - 1)]!![position].odd() +
                    cache[Coord(n - 1, 0)]!![position].odd() +
                    cache[Coord(n - 1, n - 1)]!![position].odd() + 1

        val innerEven = (cache[Coord(n / 2, n / 2)]!!.last()).even()
        val innerOdd = (cache[Coord(n / 2, n / 2)]!!.last()).odd()

        return innerOdd * ((fullSquares.toLong() + 1) * (fullSquares.toLong() + 1)) +
                innerEven * (fullSquares.toLong() * fullSquares.toLong()) +
                cornersEven * (fullSquares.toLong()) -
                cornersOdd * (fullSquares.toLong() + 1)
    }
}

private fun Pair<Long, Long>.odd() = this.first
private fun Pair<Long, Long>.even() = this.second

fun main() {
    val name = Day21::class.simpleName
    val year = 2023
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/${name}.txt")
    runDay(Day21(testInput), Day21(realInput), year, skipTests = listOf(false, false, false, false))
}