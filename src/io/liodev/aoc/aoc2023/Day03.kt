package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

typealias Coord = Pair<Int, Int>

// --- 2023 Day 3: Gear Ratios ---
class Day03(input: String) : Day<Int> {
    override val expectedValues = listOf(4361, 533784, 467835, 78826761)

    private val schematic = input.split("\n")
        .map { it.toList() + '.' }  // adding . as workaround to "num at end of line" case

    override fun solvePart1(): Int =
        traverseSchematicToFindNumbers()
            .filter { (_, positionsToCheck) -> validatePartNumber(positionsToCheck) }
            .sumOf { it.first }

    override fun solvePart2(): Int =
        traverseSchematicToFindNumbers()
            .map { (number, positionsToCheck) ->
                findGear(positionsToCheck) to number
            }.groupBy(keySelector = { it.first }, valueTransform = { it.second })
            .filter { it.value.size == 2 }
            .values.sumOf { it[0] * it[1] }

    private fun traverseSchematicToFindNumbers() = buildList {
        var currentNum = ""
        (schematic.indices * schematic[0].indices).forEach { (i, j) ->
            if (schematic[i][j].isDigit()) {
                currentNum += schematic[i][j]
            } else if (currentNum != "") {
                val positionsToCheck = getBorder(i, j - currentNum.length, currentNum.length)
                add(currentNum.toInt() to positionsToCheck)
                currentNum = ""
            }
        }
    }

    private fun getBorder(i: Int, j: Int, length: Int): List<Coord> {
        return buildList {
            add(i to j - 1)
            repeat(length + 2) {
                add(i - 1 to j - 1 + it)
                add(i + 1 to j - 1 + it)
            }
            add(i to j + length)
        }
    }

    private fun validatePartNumber(positions: List<Coord>) =
        positions.any { (i, j) ->
            schematic.validIndex(i to j) && schematic[i][j] != '.'
        }

    private fun findGear(positions: List<Coord>) =
        positions.firstOrNull { (i, j) ->
            schematic.validIndex(i to j) && schematic[i][j] == '*'
        }

    private fun List<List<Char>>.validIndex(ij: Coord) =
        ij.first in this.indices && ij.second in this[0].indices

}

private operator fun IntRange.times(other: IntRange): List<Pair<Int, Int>> =
    this.flatMap { x -> other.map { y -> x to y } }

fun main() {
    val name = Day03::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(Day03(testInput), Day03(realInput))
}