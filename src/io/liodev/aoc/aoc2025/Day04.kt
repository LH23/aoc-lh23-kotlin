package io.liodev.aoc.aoc2025

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.times
import kotlin.collections.count

// --- 2025 Day 4: Printing Department ---
class Day04(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(13, 1320, 43, 8354)

    private val grid = input.split("\n").map { it.toList() }

    override fun solvePart1(): Int {
        return (grid.indices * grid[0].indices).count { (r, c) ->
            grid[r][c] == '@' && lessThanFourPapersAround(r, c, grid)
        }
    }

    override fun solvePart2(): Int {
        var removedPapers = 0
        val mutableGrid = grid.map { it.toMutableList() }
        while (true) {
            val toRemove = (mutableGrid.indices * mutableGrid[0].indices).filter { (r, c) ->
                mutableGrid[r][c] == '@' && lessThanFourPapersAround(r, c, mutableGrid)
            }
            if (toRemove.isEmpty()) break
            removedPapers += toRemove.size
            toRemove.forEach { (r, c) ->
                mutableGrid[r][c] = '.'
            }
        }
        return removedPapers
    }

    private fun lessThanFourPapersAround(r: Int, c: Int, grid: List<List<Char>>): Boolean =
         Coord(r, c).getBorder().count { it.validIndex(grid) && grid[it.r][it.c] == '@' } < 4
}

fun main() {
    val name = Day04::class.simpleName
    val year = 2025
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day04(testInput), Day04(realInput), year)
}
