package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import java.util.PriorityQueue
import kotlin.math.abs

// --- 2024 Day 18: RAM Run ---
class Day18(
    input: String,
) : Day<String> {
    override val expectedValues = listOf("22", "252", "6,1", "5,60")

    private val corruptedBytesFalling =
        input.split("\n").map { it.split(',').let { Coord(it[1].toInt(), it[0].toInt()) } }

    override fun solvePart1(): String {
        val n = if (testInput()) 6 else 70
        val after = if (testInput()) 12 else 1024
        return calculateSteps(Coord(0, 0), Coord(n, n), after).toString()
    }

    override fun solvePart2(): String {
        val n = if (testInput()) 6 else 70
        var start = 0
        var end = corruptedBytesFalling.lastIndex
        while (start + 1 < end) {
            val middle = start + (end - start) / 2
            if (calculateSteps(Coord(0, 0), Coord(n, n), middle) != -1) {
                start = middle
            } else {
                end = middle
            }
        }
        return "${corruptedBytesFalling[end - 1].c},${corruptedBytesFalling[end - 1].r}"
    }

    private fun testInput(): Boolean = corruptedBytesFalling[0] == Coord(4, 5)

    private fun calculateSteps(
        start: Coord,
        end: Coord,
        after: Int,
    ): Int {
        val alreadyCorrupted = corruptedBytesFalling.take(after).toSet()
        val openSet =
            PriorityQueue<Pair<Coord, Int>>(compareBy { it.second + it.first.heuristic(end) })
        val gScore = mutableMapOf(start to 0)
        val visited = mutableSetOf<Coord>()

        openSet.add(start to 0)
        while (openSet.isNotEmpty()) {
            val (current, steps) = openSet.poll()
            visited.add(current)
            if (current == end) return steps

            for (next in current
                .getCardinalBorder()
                .filter {
                    it.validIndex(end.c + 1, end.r + 1) &&
                        it !in visited &&
                        it !in alreadyCorrupted
                }) {
                val tentativeGScore = gScore[current]!! + 1
                if (tentativeGScore < gScore.getOrDefault(next, Int.MAX_VALUE)) {
                    gScore[next] = tentativeGScore
                    openSet.offer(next to tentativeGScore)
                }
            }
        }
        return -1
    }

    private fun Coord.heuristic(end: Coord): Int = abs(this.r - end.r) + abs(this.c - end.c)
}

fun main() {
    val name = Day18::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day18(testInput), Day18(realInput), year, printTimings = true)
}
