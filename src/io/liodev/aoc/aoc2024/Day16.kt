package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.Dir
import io.liodev.aoc.utils.findFirstOrNull
import io.liodev.aoc.utils.get
import io.liodev.aoc.utils.printPathInMatrix
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

    private fun calculateMinPaths(
        start: Coord,
        end: Coord,
        minScore: Int,
    ): Int {
        val openSet =
            PriorityQueue<Pair<Pair<List<Coord>, Dir>, Int>>(compareBy { it.second })
                .apply { add(Pair(listOf(start), Dir.East) to 0) }
        val bestScore = mutableMapOf((start to Dir.East) to 0)
        val bestSeats = mutableSetOf<Coord>()
        val visited = mutableSetOf<Pair<Coord, Dir>>()

        while (openSet.isNotEmpty()) {
            val (current, score) = openSet.poll()
            val (currentPath, currentDir) = current
            visited += currentPath.last() to currentDir
            if (currentPath.last() == end && score == minScore) {
                println("Adding path $currentPath")
                bestSeats.addAll(currentPath)
            }

            for (next in currentPath.last()
                .getCardinalBorder()
                .filter { it.validIndex(maze) && maze[it] != '#' }) {
                val nextDir = dirFromTo(currentPath.last(), next)
                val tentativeScore = bestScore[currentPath.last() to currentDir]!! + if (currentDir == nextDir) 1 else 1001
                if (next to nextDir !in visited && tentativeScore <= bestScore.getOrDefault(next to nextDir, Int.MAX_VALUE)) {
                    bestScore[next to nextDir] = tentativeScore
                    openSet.offer(Pair(currentPath + next, nextDir) to tentativeScore)
                }
            }
        }
        //maze.printPathInMatrix(bestSeats.toList(), fill = 'O')
        return bestSeats.size
    }

    private fun calculateMinScore(
        start: Coord,
        end: Coord,
    ): Int {
        val openSet =
            PriorityQueue<Pair<Pair<Coord, Dir>, Int>>(compareBy { it.second })
                .apply { add(Pair(start, Dir.East) to 0) }
        val bestScore = mutableMapOf((start to Dir.East) to 0)
        while (openSet.isNotEmpty()) {
            val (current, score) = openSet.poll()
            val (currentCoord, currentDir) = current
            if (currentCoord == end) {
                return score
            }

            for (next in currentCoord
                .getCardinalBorder()
                .filter { it.validIndex(maze) && maze[it] != '#' }) {
                val nextDir = dirFromTo(currentCoord, next)
                val tentativeScore = bestScore[current]!! + if (currentDir == nextDir) 1 else 1001
                if (tentativeScore <= bestScore.getOrDefault(next to nextDir, Int.MAX_VALUE)) {
                    bestScore[next to nextDir] = tentativeScore
                    openSet.offer(Pair(next, nextDir) to tentativeScore)
                }
            }
        }
        return -1
    }

    private fun dirFromTo(
        current: Coord,
        next: Coord,
    ): Dir =
        when (next) {
            current.move(Dir.North) -> Dir.North
            current.move(Dir.South) -> Dir.South
            current.move(Dir.East) -> Dir.East
            current.move(Dir.West) -> Dir.West
            else -> throw IllegalArgumentException("Invalid direction")
        }

}

fun main() {
    val name = Day16::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day16(testInput), Day16(realInput), year)
}
