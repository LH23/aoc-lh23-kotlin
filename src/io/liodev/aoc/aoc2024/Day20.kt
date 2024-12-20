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

    override fun solvePart1(): Int {
        val path = findBestPath(racetrack.findFirstOrNull('S')!!, racetrack.findFirstOrNull('E')!!)
        val saveAtLeast = if (testInput(path)) 1 else 100
        return findCheats(path, 2, saveAtLeast).size
    }

    override fun solvePart2(): Int {
        val path = findBestPath(racetrack.findFirstOrNull('S')!!, racetrack.findFirstOrNull('E')!!)
        val saveAtLeast = if (testInput(path)) 50 else 100
        return findCheats(path, 20, saveAtLeast).size
    }

    private fun testInput(path: List<Coord>) = path.size == 85

    private fun findCheats(
        path: List<Coord>,
        cheatDistance: Int,
        saveAtLeast: Int,
    ): List<Pair<Coord, Coord>> =
        path
            .flatMapIndexed { n, start ->
                getAllEndPositions(start, cheatDistance, path.drop(n + saveAtLeast + 2))
                    .map { start to it }
                    .filter { (start, end) ->
                        val cheatPathSize = n + start.manhattanDistance(end) + (path.size - path.indexOf(end))
                        path.size - cheatPathSize >= saveAtLeast
                    }
            }

    private fun getAllEndPositions(
        pos: Coord,
        cheatDistance: Int,
        trackLeft: List<Coord>,
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
    runDay(Day20(testInput), Day20(realInput), year, printTimings = true, benchmark = false)
}
