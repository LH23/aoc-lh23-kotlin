package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.times

// --- 2023 Day 11: Cosmic Expansion ---
class Day11(input: String): Day<Long> {
    override val expectedValues = listOf(374L, 9639160, 82000210, 752936133304)

    private val galaxyMap = input.split("\n").map { it.toList() }.toGalaxyMap()

    override fun solvePart1() = galaxyMap.galaxyPairs.sumOf { gs ->
        galaxyMap.getDistance(gs.first(), gs.last()).toLong()
    }

    override fun solvePart2() = galaxyMap.galaxyPairs.sumOf { gs ->
        val distance = galaxyMap.getDistance(gs.first(), gs.last(), 1000000 - 1).toLong()
        distance
    }
}

private fun List<List<Char>>.toGalaxyMap(): GalaxyMap {
    val skymap = this
    val galaxies = buildList {
        (skymap.indices * skymap[0].indices).forEach { (i,j) ->
            if (skymap[i][j] == '#') add(Coord(i,j))
        }
    }
    val emptyRows = skymap.indices.filter { i -> skymap[i].all { it == '.' }}
    val emptyColumns = skymap[0].indices.filter { j -> skymap.indices.all { i -> skymap[i][j] == '.' }}
    return GalaxyMap(galaxies, emptyRows, emptyColumns)
}

data class GalaxyMap(val galaxies: List<Coord>, val emptyRows: List<Int>, val emptyColumns: List<Int>) {
    val galaxyPairs: Set<Set<Coord>> = galaxies.cartesianProduct(galaxies).map { (x,y) -> setOf(x,y) }.filter { it.size == 2 }.toSet()

    fun getDistance(g1: Coord, g2: Coord, multiplier: Int = 1): Int {
        val minCol = minOf(g1.c, g2.c)
        val maxCol = maxOf(g1.c, g2.c)
        val minRow = minOf(g1.r, g2.r)
        val maxRow = maxOf(g1.r, g2.r)
        return (maxCol - minCol + (minCol..maxCol).count { it in emptyColumns } * multiplier) +
               (maxRow - minRow + (minRow..maxRow).count { it in emptyRows } * multiplier )
    }
}

fun <T, S> Collection<T>.cartesianProduct(other: Iterable<S>): List<Pair<T, S>> {
    return cartesianProduct(other) { first, second -> first to second }
}

fun <T, S, V> Collection<T>.cartesianProduct(other: Iterable<S>, transformer: (first: T, second: S) -> V): List<V> {
    return this.flatMap { first -> other.map { second -> transformer.invoke(first, second) } }
}

fun main() {
    val name = Day11::class.simpleName
    val testInput= readInputAsString("src/input/2023/${name}_test.txt")
    val realInput= readInputAsString("src/input/2023/${name}.txt")
    runDay(Day11(testInput), Day11(realInput), printTimings = true)
}