package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.Dir
import io.liodev.aoc.utils.findFirstOrNull
import io.liodev.aoc.utils.get

// --- 2024 Day 6: Guard Gallivant ---
class Day06(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(41, 5212, 6, 1767)

    private val walkingMap = input.split("\n").map { it.toCharArray().toList() }

    private val cycle = setOf(Coord(-1, -1) to Dir.North)

    override fun solvePart1(): Int =
        calculateGuardPath(guardPosition(walkingMap), Dir.North)
            .map { it.first }
            .toSet()
            .size

    override fun solvePart2(): Int = calculateCycles(calculateGuardPath(guardPosition(walkingMap), Dir.North)).size

    private fun guardPosition(walkingMap: List<List<Char>>) = walkingMap.findFirstOrNull('^')!!

    private fun calculateGuardPath(
        guardPosition: Coord,
        initialDir: Dir,
        visitedAlready: Set<Pair<Coord, Dir>> = setOf(),
        newObstacle: Coord? = null,
    ): Set<Pair<Coord, Dir>> {
        var currentPos = guardPosition
        var currentDir = initialDir
        val visited = mutableSetOf(currentPos to currentDir)
        while (true) {
            if (walkingMap[currentPos.move(currentDir)] == '#' || currentPos.move(currentDir) == newObstacle) {
                currentDir = currentDir.turnRight()
            } else {
                currentPos = currentPos.move(currentDir)
                val current = currentPos to currentDir
                if (current in visited || current in visitedAlready) {
                    return cycle
                }
                visited += current
                if (!currentPos.move(currentDir).validIndex(walkingMap)) break
            }
        }
        return visited
    }

    private fun calculateCycles(guardPath: Set<Pair<Coord, Dir>>): Set<Coord> {
        val visitedAlready = mutableSetOf<Pair<Coord, Dir>>()
        val obstacles = mutableSetOf<Coord>()
        var last = guardPath.first()
        for (position in guardPath.drop(1)) {
            val newObstacle = position.first
            if (newObstacle in visitedAlready.map { it.first }) {
                // already tested
                continue
            }
            val theoreticalPath =
                calculateGuardPath(last.first, last.second, visitedAlready, newObstacle)
            if (theoreticalPath == cycle) {
                obstacles += newObstacle
            }
            last = position
            visitedAlready += position
        }
        return obstacles
    }
}

fun main() {
    val name = Day06::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day06(testInput), Day06(realInput), year, printTimings = true)
}
