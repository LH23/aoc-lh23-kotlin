package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.Dir
import io.liodev.aoc.utils.dirFromTo
import io.liodev.aoc.utils.findFirstOrNull
import io.liodev.aoc.utils.get
import java.util.PriorityQueue

// --- 2024 Day 16: Reindeer Maze ---
class Day16(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(11048, 101492, 64, 543)
    private val maze = input.split("\n").map { it.toList() }

    override fun solvePart1(): Int {
        val s = maze.findFirstOrNull('S')!!
        val e = maze.findFirstOrNull('E')!!
        return calculateMinScore(s, e)
    }

    override fun solvePart2(): Int {
        val s = maze.findFirstOrNull('S')!!
        val e = maze.findFirstOrNull('E')!!
        val minScore = calculateMinScore(s, e)
        return calculateMinPaths(s, e, minScore)
    }

    data class ReindeerPos(
        val coord: Coord,
        val dir: Dir,
    )

    data class ReindeerPath(
        val path: List<Coord>,
        val dir: Dir,
    ) {
        val last = ReindeerPos(path.last(), dir)
    }

    private fun calculateMinPaths(
        start: Coord,
        end: Coord,
        minScore: Int,
    ): Int {
        val openSet = PriorityQueue<Pair<ReindeerPath, Int>>(compareBy { it.second })
        openSet.add(ReindeerPath(listOf(start), Dir.East) to 0)
        val bestScore = mutableMapOf(ReindeerPos(start, Dir.East) to 0)
        val bestSeats = mutableSetOf<Coord>()
        val visited = mutableSetOf<ReindeerPos>()

        while (openSet.isNotEmpty()) {
            val (reindeerPath, score) = openSet.poll()
            val reindeer = reindeerPath.last
            visited += reindeer
            if (reindeer.coord == end && score == minScore) {
                bestSeats.addAll(reindeerPath.path)
            }

            for (next in reindeer.coord
                .getCardinalBorder()
                .filter { it.validIndex(maze) && maze[it] != '#' }) {
                val nextDir = dirFromTo(reindeer.coord, next)
                val tentativeScore = bestScore[reindeer]!! + if (reindeer.dir == nextDir) 1 else 1001
                val nextPos = ReindeerPos(next, nextDir)
                if (nextPos !in visited && tentativeScore <= bestScore.getOrDefault(nextPos, Int.MAX_VALUE)) {
                    bestScore[nextPos] = tentativeScore
                    openSet.offer(ReindeerPath(reindeerPath.path + next, nextDir) to tentativeScore)
                }
            }
        }
        // maze.printPathInMatrix(bestSeats.toList(), fill = 'O')
        return bestSeats.size
    }

    private fun calculateMinScore(
        start: Coord,
        end: Coord,
    ): Int {
        val openSet = PriorityQueue<Pair<ReindeerPos, Int>>(compareBy { it.second })
        openSet.add(ReindeerPos(start, Dir.East) to 0)
        val bestScore = mutableMapOf(ReindeerPos(start, Dir.East) to 0)
        while (openSet.isNotEmpty()) {
            val (reindeer, score) = openSet.poll()
            if (reindeer.coord == end) {
                return score
            }

            for (next in reindeer.coord
                .getCardinalBorder()
                .filter { it.validIndex(maze) && maze[it] != '#' }) {
                val nextDir = dirFromTo(reindeer.coord, next)
                val tentativeScore = bestScore[reindeer]!! + if (reindeer.dir == nextDir) 1 else 1001
                val nextPos = ReindeerPos(next, nextDir)
                if (tentativeScore < bestScore.getOrDefault(nextPos, Int.MAX_VALUE)) {
                    bestScore[nextPos] = tentativeScore
                    openSet.offer(nextPos to tentativeScore)
                }
            }
        }
        return -1
    }
}

fun main() {
    val name = Day16::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day16(testInput), Day16(realInput), year, printTimings = true)
}
