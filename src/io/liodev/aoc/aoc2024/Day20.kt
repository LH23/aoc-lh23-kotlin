package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.findFirstOrNull
import io.liodev.aoc.utils.get
import java.util.PriorityQueue

// --- 2024 Day 20: Race Condition ---
class Day20(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(44, 1311, 285, 961364)

    private val racetrack = input.split("\n").map { it.toList() }
    private val racetrackPath = findBestPath(racetrack.findFirstOrNull('S')!!, racetrack.findFirstOrNull('E')!!)

    override fun solvePart1(): Int {
        val saveAtLeast = if (testInput(racetrackPath)) 1 else 100
        val (positions, endPositions) = precalculateMaps(racetrackPath, 2, saveAtLeast)

        return findCheats(racetrackPath, saveAtLeast, positions, endPositions).size
    }

    override fun solvePart2(): Int {
        val saveAtLeast = if (testInput(racetrackPath)) 50 else 100
        val (positions, endPositions) = precalculateMaps(racetrackPath, 20, saveAtLeast)
        return findCheats(racetrackPath, saveAtLeast, positions, endPositions).size
    }

    private fun testInput(path: List<Coord>) = path.size == 85

    private fun findCheats(
        path: List<Coord>,
        saveAtLeast: Int,
        positions: Map<Coord, Int>,
        endPositions: Map<Coord, List<Coord>>,
    ): List<Pair<Coord, Coord>> =
        path
            .flatMapIndexed { n, start ->
                endPositions[start]!!
                    .map { start to it }
                    .filter { (start, end) ->
                        val cheatPathSize = n + start.manhattanDistance(end) + (path.size - positions[end]!!)
                        path.size - cheatPathSize >= saveAtLeast
                    }
            }

    private fun precalculateMaps(
        path: List<Coord>,
        cheatDistance: Int,
        saveAtLeast: Int,
    ): Pair<Map<Coord, Int>, Map<Coord, List<Coord>>> {
        val positions = mutableMapOf<Coord, Int>()
        val endPositions = mutableMapOf<Coord, List<Coord>>()

        val trackLeft = path.drop(saveAtLeast.coerceAtMost(path.lastIndex)).toMutableSet()
        for ((n, coord) in path.withIndex()) {
            positions[coord] = n
            trackLeft.remove(path[(n + saveAtLeast + 1).coerceAtMost(path.lastIndex)])
            endPositions[coord] = getAllEndPositions(coord, cheatDistance, trackLeft)
        }
        return positions to endPositions
    }

    private fun getAllEndPositions(
        pos: Coord,
        cheatDistance: Int,
        trackLeft: Set<Coord>,
    ): List<Coord> = trackLeft.filter { it.manhattanDistance(pos) <= cheatDistance }

    private fun findBestPath(
        start: Coord,
        end: Coord,
    ): List<Coord> {
        val openSet = PriorityQueue<Pair<List<Coord>, Int>>(compareBy { it.second })
        openSet.add(listOf(start) to 0)
        val bestScore = mutableMapOf(start to 0)
        val visited = mutableSetOf<Coord>()

        while (openSet.isNotEmpty()) {
            val (path, score) = openSet.poll()
            val location = path.last()
            visited += location
            if (location == end) {
                return path
            }

            for (next in location
                .getCardinalBorder()
                .filter { it.validIndex(racetrack) && racetrack[it] != '#' }) {
                val tentativeScore = bestScore[location]!! + 1
                if (next !in visited &&
                    tentativeScore <=
                    bestScore.getOrDefault(
                        next,
                        Int.MAX_VALUE,
                    )
                ) {
                    bestScore[next] = tentativeScore
                    openSet.offer(path + next to tentativeScore)
                }
            }
        }
        return listOf()
    }
}

fun main() {
    val name = Day20::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day20(testInput), Day20(realInput), year, printTimings = true)
}
