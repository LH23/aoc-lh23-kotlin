package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.get

// --- 2024 10
class Day10(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(36, 461, 81, -1)

    private val topographicMap =
        input.lines().map { line -> line.toCharArray().toList().map { it - '0' } }

    override fun solvePart1(): Int {
        var trailheads = 0
        for (i in topographicMap.indices) {
            for (j in topographicMap[i].indices) {
                if (topographicMap[i][j] == 0) {
                    trailheads +=
                        calculateTrailheadScore(
                            topographicMap,
                            Coord(i, j),
                            listOf(Coord(i, j)),
                        ).toSet().size
                }
            }
        }
        return trailheads
    }

    override fun solvePart2(): Int {
        var trailheads = 0
        for (i in topographicMap.indices) {
            for (j in topographicMap[i].indices) {
                if (topographicMap[i][j] == 0) {
                    trailheads +=
                        calculateTrailheadScore(
                            topographicMap,
                            Coord(i, j),
                            listOf(Coord(i, j)),
                        ).size
                }
            }
        }
        return trailheads
    }

    private fun calculateTrailheadScore(
        topographicMap: List<List<Int>>,
        coord: Coord,
        debugList: List<Coord>,
    ): List<Coord> {
        val currHeight = topographicMap[coord]
        return if (currHeight == 9) {
            listOf(coord)
        } else {
            coord.getCardinalBorder().flatMap {
                if (it.validIndex(topographicMap) && topographicMap[it] == currHeight + 1) {
                    calculateTrailheadScore(topographicMap, it, debugList + it)
                } else {
                    listOf()
                }
            }
        }
    }
}

fun main() {
    val name = Day10::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day10(testInput), Day10(realInput), year)
}
