package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

typealias Coord = Pair<Int,Int>

// --- 2023 Day 3: Gear Ratios ---
class Day03(input: String) : Day<Int> {
    override val expectedValues = listOf(4361, 533784, 467835, 78826761)

    private val schematic =  input.split("\n").map { it.toList() }

    override fun solvePart1(): Int {
        val partNumbers = mutableListOf<Int>()
        val positionsToCheck = mutableSetOf<Coord>()
        var curr = 0
        for (i in schematic.indices) {
            for (j in schematic[0].indices) {
                if (schematic[i][j].isDigit()) {
                    curr = curr * 10 + schematic[i][j].digitToInt()
                    positionsToCheck.addAll(neighborPositions(i,j))
                } else if (curr != 0) {
                    if (validatePartNumber(positionsToCheck)) partNumbers.add(curr)
                    curr = 0
                    positionsToCheck.clear()
                }
            }
        }
        return partNumbers.sum()
    }

    override fun solvePart2(): Int {
        val gearRatios = mutableMapOf<Coord, MutableList<Int>>()
        val positionsToCheck = mutableSetOf<Coord>()
        var curr = 0
        for (i in schematic.indices) {
            for (j in schematic[0].indices) {
                if (schematic[i][j].isDigit()) {
                    curr = curr * 10 + schematic[i][j].digitToInt()
                    positionsToCheck.addAll(neighborPositions(i,j))
                } else if (curr != 0) {
                    findGear(positionsToCheck)?.let { gearCoord ->
                        gearRatios.getOrPut(gearCoord) { mutableListOf() }.add(curr)
                    }
                    curr = 0
                    positionsToCheck.clear()
                }
            }
        }
        return gearRatios.filter { it.value.size == 2 }.values.sumOf { it[0] * it[1] }
    }

    private fun neighborPositions(i: Int, j: Int) : List<Coord> = listOf(
        i - 1 to j - 1, i to j - 1, i + 1 to j - 1,
        i - 1 to j, i + 1 to j,
        i - 1 to j + 1, i to j + 1, i + 1 to j + 1
    )

    private fun validatePartNumber(positions: Set<Coord>): Boolean =
        positions.any { (i,j) ->
            i in schematic.indices && j in schematic.indices &&
                !schematic[i][j].isDigit() && schematic[i][j] != '.'
        }

    private fun findGear(positions: Set<Coord>): Coord? =
        positions.firstOrNull { (i,j) ->
            i in schematic.indices && j in schematic.indices && schematic[i][j] == '*'
        }
}

fun main() {
    val name = Day03::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(Day03(testInput), Day03(realInput))
}