package io.liodev.aoc.aoc2025

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.allCoordinates
import io.liodev.aoc.utils.get
import io.liodev.aoc.utils.set
import kotlin.collections.count

// --- 2025 Day 4: Printing Department ---
class Day04(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(13, 1320, 43, 8354)

    private val grid = input.split("\n").map { it.toList() }

    override fun solvePart1(): Int = grid.allCoordinates.count { coord ->
        grid.isPaper(coord) && grid.lessThanFourPapersAround(coord)
    }

    override fun solvePart2(): Int {
        var removedPapers = 0
        val mutableGrid = grid.map { it.toMutableList() }
        while (true) {
            val toRemove = mutableGrid.allCoordinates.filter { coord ->
                mutableGrid.isPaper(coord) && mutableGrid.lessThanFourPapersAround(coord)
            }
            if (toRemove.isEmpty()) break
            removedPapers += toRemove.size
            toRemove.forEach { coord ->
                mutableGrid[coord] = '.'
            }
        }
        return removedPapers
    }

}

private fun List<List<Char>>.isPaper(coord: Coord): Boolean = this[coord] == '@'

private fun List<List<Char>>.lessThanFourPapersAround(coord: Coord) : Boolean =
    coord.getBorder().count { it.validIndex(this) && this[it] == '@' } < 4

fun main() {
    val name = Day04::class.simpleName
    val year = 2025
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day04(testInput), Day04(realInput), year)
}
