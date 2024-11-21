package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.println
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.times

// --- 2023 Day 11: Cosmic Expansion ---
class Day11(input: String) : Day<Long> {

    override val expectedValues = listOf(374L, 9639160, 82000210, 752936133304)

    private val galaxyMap = input.split("\n").map { it.toList() }.toGalaxyMap()

    override fun solvePart1() = galaxyMap.galaxyPairs.sumOf { gs ->
        galaxyMap.getDistance(gs.first(), gs.last()).toLong()
    }

    override fun solvePart2() = galaxyMap.galaxyPairs.sumOf { gs ->
        val distance = galaxyMap.getDistance(gs.first(), gs.last(), 1000000).toLong()
        distance
    }
}

private fun List<List<Char>>.toGalaxyMap(): GalaxyMap {
    val skymap = this
    val galaxies = buildList {
        (skymap.indices * skymap[0].indices).forEach { (i, j) ->
            if (skymap[i][j] == '#') add(Coord(i, j))
        }
    }
    val emptyRows = skymap.indices.filter { i -> skymap[i].all { it == '.' } }
    val emptyColumns =
        skymap[0].indices.filter { j -> skymap.indices.all { i -> skymap[i][j] == '.' } }

    return GalaxyMap(galaxies, emptyRows, emptyColumns)
}

data class GalaxyMap(
    val galaxies: List<Coord>,
    val emptyRows: List<Int>,
    val emptyColumns: List<Int>
) {

    val galaxyPairs: Set<Set<Coord>> =
        (galaxies * galaxies)
            .map { (x, y) -> setOf(x, y) }
            .filter { it.size == 2 }
            .toSet()

    fun getDistance(g1: Coord, g2: Coord, multiplier: Int = 2): Int {
        val minCol = minOf(g1.c, g2.c)
        val maxCol = maxOf(g1.c, g2.c)
        val minRow = minOf(g1.r, g2.r)
        val maxRow = maxOf(g1.r, g2.r)
        val emptyColsBetween = emptyColumns.count { it < maxCol } - emptyColumns.count { it < minCol }
        val emptyRowsBetween = emptyRows.count { it < maxRow } - emptyRows.count { it < minRow }
        return (maxCol - minCol + emptyColsBetween * (multiplier-1)) + (maxRow - minRow + emptyRowsBetween * (multiplier-1))
    }
}

fun main() {
    val name = Day11::class.simpleName
    val year = 2023
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/${name}.txt")
    runDay(Day11(testInput), Day11(realInput), year, printTimings = true)
}