package io.liodev.aoc.aoc2022

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord

// --- Day 12 2022: Hill Climbing Algorithm ---
class Day12(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(31, 412, 29, -1)

    private val elevationMap = input.split("\n").map { it.toCharArray().toList() }

    override fun solvePart1(): Int {
        val (start, end) = getStartEnd(elevationMap)

        return calculateMinStepsBFS(start, end, elevationMap)
    }

    override fun solvePart2(): Int {
        val (_, end) = getStartEnd(elevationMap)

        return (0..elevationMap.lastIndex).minOf { r ->
            val newStart = Coord(r, 0)
            calculateMinStepsBFS(newStart, end, elevationMap)
        }
    }

    private fun getStartEnd(elevationMap: List<List<Char>>): Pair<Coord, Coord> {
        var start = Coord(0, 0)
        var end = Coord(0, 0)
        for (r in elevationMap.indices) {
            for (c in elevationMap[r].indices) {
                if (elevationMap[r][c] == 'S') {
                    start = Coord(r, c)
                }
                if (elevationMap[r][c] == 'E') {
                    end = Coord(r, c)
                }
            }
        }
        return start to end
    }

    private fun calculateMinStepsBFS(
        start: Coord,
        end: Coord,
        elevationMap: List<List<Char>>,
    ): Int {
        val queue = ArrayDeque<Pair<Coord, Int>>().apply { add(start to 0) }
        val visited = mutableSetOf<Coord>()

        while (queue.isNotEmpty()) {
            val (current, steps) = queue.removeFirst()
            // println("Visiting $current (${elevationMap[current.r][current.c]}) in $steps")
            visited.add(current)
            if (current == end) return steps

            for (next in current.getCardinalBorder()) {
                // println("Checking $next : valid? ${next.validIndex(elevationMap)} map ${elevationMap.size}x${elevationMap[0].size}")
                if (next.validIndex(elevationMap) &&
                    canClimbFromTo(current, next) &&
                    next !in visited &&
                    next !in queue.map { it.first }
                ) {
                    println("Adding $next")
                    queue.add(next to steps + 1)
                }
            }
        }
        return -1
    }

    private fun canClimbFromTo(
        cur: Coord,
        next: Coord,
    ): Boolean {
        val currElevation =
            if (elevationMap[cur.r][cur.c] == 'S') 'a' else elevationMap[cur.r][cur.c]
        val nextElevation =
            if (elevationMap[next.r][next.c] == 'E') 'z' else elevationMap[next.r][next.c]
        return (nextElevation - currElevation <= 1)
    }
}

fun main() {
    val name = Day12::class.simpleName
    val year = 2022
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day12(testInput), Day12(realInput), year)
}
