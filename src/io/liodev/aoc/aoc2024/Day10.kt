package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.get
import io.liodev.aoc.utils.times

// --- 2024 Day 10: Hoof It ---
class Day10(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(36, 461, 81, 875)

    private val topographicMap =
        input.lines().map { line -> line.toCharArray().toList().map { it - '0' } }

    override fun solvePart1() =
        (topographicMap.indices * topographicMap[0].indices)
            .filter {
                topographicMap[it.first][it.second] == 0
            }.sumOf {
                calculateTrailheadScore(topographicMap, Coord(it.first, it.second)).toSet().size
            }

    override fun solvePart2() =
        (topographicMap.indices * topographicMap[0].indices)
            .filter {
                topographicMap[it.first][it.second] == 0
            }.sumOf {
                calculateTrailheadScore(topographicMap, Coord(it.first, it.second)).size
            }

    private fun calculateTrailheadScore(
        topographicMap: List<List<Int>>,
        coord: Coord,
    ): List<Coord> =
        if (topographicMap[coord] == 9) {
            listOf(coord)
        } else {
            coord
                .getCardinalBorder()
                .mapNotNull {
                    if (it.validIndex(topographicMap) && topographicMap[it] == topographicMap[coord] + 1) {
                        calculateTrailheadScore(topographicMap, it)
                    } else {
                        null
                    }
                }.flatten()
        }
}

fun main() {
    val name = Day10::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day10(testInput), Day10(realInput), year)
}
