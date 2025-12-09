package io.liodev.aoc.aoc2025

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.findAll
import io.liodev.aoc.utils.floodFill
import io.liodev.aoc.utils.floodOverflows
import kotlin.math.abs

// --- 2025 Day 9: Movie Theater ---
class Day09(
    val input: String,
) : Day<Long> {
    override val expectedValues = listOf(50L, 4773451098, 24, 1429075575)

    private val redTiles = input.split("\n").map {
        it.split(',').let { (c, r) -> Coord(r.toInt(), c.toInt()) }
    }

    private val areas = buildList {
        for (i in redTiles.indices) {
            for (j in i + 1 until redTiles.size) {
                add(
                    PairsWithArea(
                        setOf(redTiles[i], redTiles[j]), calculateArea(redTiles[i], redTiles[j])
                    )
                )
            }
        }
    }.sortedByDescending { it.area }

    data class PairsWithArea(
        val coords: Set<Coord>,
        val area: Long,
    )

    override fun solvePart1(): Long {
        return areas.maxOf { it.area }
    }

    override fun solvePart2(): Long {
        val usedRows = redTiles.map { it.r }.toSet().sorted()
        val usedColumns = redTiles.map { it.c }.toSet().sorted()

        val greenBorderTiles = calculateGreenBorder(redTiles).toSet()

        val floor = List(usedRows.size) { r ->
            MutableList(usedColumns.size) { c ->
                val coord = Coord(usedRows[r], usedColumns[c])
                when (coord) {
                    in redTiles -> 'R'
                    in greenBorderTiles -> 'G'
                    else -> '.'
                }
            }
        }

        val emptySeats = floor.findAll('.')
        for (seat in emptySeats) {
            if (!floor.floodOverflows(seat, 'X', '.')) {
                floor.floodFill(seat, 'X', '.')
                break
            }
        }

        return areas.first { pair ->
            val (a, b) = pair.coords.toList()
            val indexAr = usedRows.indexOf(a.r)
            val indexBr = usedRows.indexOf(b.r)
            val indexAc = usedColumns.indexOf(a.c)
            val indexBc = usedColumns.indexOf(b.c)
            val rowRange = minOf(indexAr, indexBr)..maxOf(indexAr, indexBr)
            val columnRange = minOf(indexAc, indexBc)..maxOf(indexAc, indexBc)
            for (r in rowRange) {
                for (c in columnRange) {
                    if (floor[r][c] == '.') {
                        return@first false
                    }
                }
            }
            return@first true
        }.area
    }

    private fun calculateGreenBorder(redTiles: List<Coord>): List<Coord> = buildList { 
        (redTiles + redTiles.first()).zipWithNext().forEach { (t1, t2) ->
            if (t1.r == t2.r) {
                for (c in minOf(t1.c, t2.c) + 1 until maxOf(t1.c, t2.c)) {
                    add(Coord(t1.r, c))
                }
            } else if (t1.c == t2.c) {
                for (r in minOf(t1.r, t2.r) + 1 until maxOf(t1.r, t2.r)) {
                    add(Coord(r, t1.c))
                }
            }
        }
    }

    private fun calculateArea(
        a: Coord, b: Coord
    ): Long = (1 + abs(a.r - b.r)).toLong() * (1 + abs(a.c - b.c)).toLong()

}

fun main() {
    val name = Day09::class.simpleName
    val year = 2025
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day09(testInput), Day09(realInput), year, printTimings = true)
}
