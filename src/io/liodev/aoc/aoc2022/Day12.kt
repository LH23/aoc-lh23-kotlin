package io.liodev.aoc.aoc2022

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import java.util.PriorityQueue
import kotlin.math.abs

// --- Day 12 2022: Hill Climbing Algorithm ---
class Day12(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(31, 412, 29, 402)

    private val elevationMap = input.split("\n").map { it.toCharArray().toList() }

    override fun solvePart1() = calculateMinStepsAStar(findFirst('S'), findFirst('E'), elevationMap)

    override fun solvePart2() =
        (0..elevationMap.lastIndex).minOf { r ->
            // TODO make it work in a general case (optimal 'a' can be anywhere)
            // only checking column 0, in my inputs that's enough
            val newStart = Coord(r, 0)
            calculateMinStepsAStar(newStart, findFirst('E'), elevationMap)
        }

    private fun findFirst(c: Char) =
        Coord(
            elevationMap.indexOfFirst { c in it },
            elevationMap.first { c in it }.indexOf(c),
        )

    // not used, A* is 30% faster
    private fun calculateMinStepsBFS(
        start: Coord,
        end: Coord,
        elevationMap: List<List<Char>>,
    ): Int {
        val queue = ArrayDeque<Pair<Coord, Int>>().apply { add(start to 0) }
        val visited = mutableSetOf<Coord>()

        while (queue.isNotEmpty()) {
            val (current, steps) = queue.removeFirst()
            visited.add(current)
            if (current == end) return steps

            for (next in current.getCardinalBorder()) {
                if (next.validIndex(elevationMap) &&
                    canClimbFromTo(current, next) &&
                    next !in visited &&
                    next !in queue.map { it.first }
                ) {
                    queue.add(next to steps + 1)
                }
            }
        }
        return -1
    }

    private fun calculateMinStepsAStar(
        start: Coord,
        end: Coord,
        elevationMap: List<List<Char>>,
    ): Int {
        val openSet =
            PriorityQueue<Pair<Coord, Int>>(compareBy { it.second + it.first.heuristic(end) })
                .apply { add(start to 0) }
        val gScore = mutableMapOf(start to 0)
        val visited = mutableSetOf<Coord>()

        while (openSet.isNotEmpty()) {
            val (current, steps) = openSet.poll()
            visited.add(current)
            if (current == end) return steps

            for (next in current.getCardinalBorder()) {
                if (next.validIndex(elevationMap) && canClimbFromTo(current, next)) {
                    val tentativeGScore = gScore[current]!! + 1
                    if (tentativeGScore < gScore.getOrDefault(next, Int.MAX_VALUE)) {
                        gScore[next] = tentativeGScore
                        openSet.offer(next to tentativeGScore)
                    }
                }
            }
        }
        return -1
    }

    // Manhattan distance works here for optimal path
    private fun Coord.heuristic(end: Coord): Int = abs(this.r - end.r) + abs(this.c - end.c)

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
    runDay(Day12(testInput), Day12(realInput), year, printTimings = true)
}
