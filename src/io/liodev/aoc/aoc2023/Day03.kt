package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

typealias Coord = Pair<Int, Int>

// --- 2023 Day 3: Gear Ratios ---
class Day03(input: String) : Day<Int> {
    override val expectedValues = listOf(4361, 533784, 467835, 78826761)

    private val schematic = input.split("\n").map { it.toList() }

    override fun solvePart1(): Int {
        val partNumbers = mutableListOf<Int>()
        traverseSchematicToFindNumbers { numberFound, positionsToCheck ->
            if (validatePartNumber(positionsToCheck)) partNumbers.add(numberFound)
        }
        return partNumbers.sum()
    }

    override fun solvePart2(): Int {
        val gearRatios = mutableMapOf<Coord, MutableList<Int>>()
        traverseSchematicToFindNumbers { numberFound, positionsToCheck ->
            findGear(positionsToCheck)?.let { gearCoordinates ->
                gearRatios.getOrPut(gearCoordinates) { mutableListOf() }.add(numberFound)
            }
        }
        return gearRatios.filter { it.value.size == 2 }.values.sumOf { it[0] * it[1] }
    }

    private fun traverseSchematicToFindNumbers(numFound: (Int, Set<Coord>) -> Unit) {
        val positionsToCheck = mutableSetOf<Coord>()
        var currentNum = 0
        for (i in schematic.indices) {
            for (j in schematic[0].indices) {
                if (schematic[i][j].isDigit()) {
                    currentNum = currentNum * 10 + schematic[i][j].digitToInt()
                    positionsToCheck.remove(i to j)
                    positionsToCheck += neighborPositions(i, j)
                } else if (currentNum != 0) {
                    numFound(currentNum, positionsToCheck)
                    currentNum = 0
                    positionsToCheck.clear()
                }
            }
        }
    }

    private fun neighborPositions(i: Int, j: Int): List<Coord> = listOf(
        i - 1 to j - 1, i to j - 1, i + 1 to j - 1,
        i - 1 to j,                 i + 1 to j,
        i - 1 to j + 1, i to j + 1, i + 1 to j + 1
    )

    private fun validatePartNumber(positions: Set<Coord>) =
        positions.any { (i, j) ->
            schematic.validIndex(i to j) && !schematic[i][j].isDigit() && schematic[i][j] != '.'
        }

    private fun findGear(positions: Set<Coord>) =
        positions.firstOrNull { (i, j) ->
            schematic.validIndex(i to j) && schematic[i][j] == '*'
        }

    private fun List<List<Char>>.validIndex(ij: Coord): Boolean =
        ij.first in this.indices && ij.second in this[0].indices

}

fun main() {
    val name = Day03::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(Day03(testInput), Day03(realInput))
}