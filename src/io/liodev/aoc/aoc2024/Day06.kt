package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.Dir
import io.liodev.aoc.utils.get

// --- 2024 Day 6: Guard Gallivant ---
class Day06(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(41, 5212, 6, 1767)

    private val walkingMap = input.split("\n").map { it.toCharArray().toList() }

    override fun solvePart1(): Int =
        calculateGuardPath(guardPosition(walkingMap), Dir.North)
            .map { it.first }
            .toSet()
            .size

    private fun guardPosition(walkingMap: List<List<Char>>): Coord {
        for (r in walkingMap.indices) {
            for (c in walkingMap[r].indices) {
                if (walkingMap[r][c] == '^') {
                    return Coord(r, c)
                }
            }
        }
        return Coord(-1, -1)
    }

    private fun calculateGuardPath(
        guardPosition: Coord,
        initialDir: Dir,
        visitedAlready: Set<Pair<Coord, Dir>> = setOf(),
        newObstacle: Coord? = null,
    ): List<Pair<Coord, Dir>> {
        var currentPos = guardPosition.copy()
        var currentDir = initialDir
        val visited = mutableSetOf(currentPos to currentDir)
        visited.addAll(visitedAlready)
        while (true) {
            if (walkingMap[currentPos.move(currentDir)] == '#' || currentPos.move(currentDir) == newObstacle) {
                currentDir = currentDir.turnRight()
            } else {
                currentPos = currentPos.move(currentDir)
                if (currentPos to currentDir in visited) {
                    visited += Coord(-1, -1) to Dir.North
                    break
                }
                visited += currentPos to currentDir
                if (!currentPos.move(currentDir).validIndex(walkingMap)) break
            }
        }
        return visited.toList()
    }

    override fun solvePart2(): Int {
        val guardPath = calculateGuardPath(guardPosition(walkingMap), Dir.North)
        val visitedAlready = mutableSetOf<Pair<Coord, Dir>>()
        val obstacles = mutableSetOf<Coord>()
        val guard = guardPosition(walkingMap)
        for (position in guardPath.drop(1)) {
            val newObstacle = position.first
            if (newObstacle in obstacles || newObstacle in visitedAlready.map { it.first }) {
                continue
            }
            // TODO use visitedAlready to speed up
            val theoreticalPath = calculateGuardPath(guard, Dir.North, setOf(), newObstacle)
            if (theoreticalPath.last().first == Coord(-1, -1)) {
                obstacles += newObstacle
            }
            visitedAlready += position
        }
        return obstacles.size
    }
}

fun main() {
    val name = Day06::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day06(testInput), Day06(realInput), year, printTimings = true)
}
