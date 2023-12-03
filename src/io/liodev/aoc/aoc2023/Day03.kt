package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

class Day03(input: String) : Day<Int> {
    override val expectedValues = listOf(4361, 533784, 467835, 78826761)

    private val schematic = parseInput(input)
    private fun parseInput(input: String): List<List<Char>> = input.split("\n").map { it.toList() }

    override fun solvePart1(): Int {
        val nums = mutableListOf<Int>()
        var curr = 0
        var currValid = false
        for (i in schematic.indices) {
            for (j in schematic[0].indices) {
                if (schematic[i][j].isDigit()) {
                    curr = curr * 10 + schematic[i][j].digitToInt()
                    if (!currValid) currValid = validate(i, j)
                    println("${schematic[i][j]} $curr $currValid")
                } else {
                    if (currValid) nums.add(curr)
                    curr = 0
                    currValid = false
                }
            }
        }
        return nums.sum()
    }

    private fun validate(i: Int, j: Int): Boolean {
        val positions = listOf(
            i - 1 to j - 1, i to j - 1, i + 1 to j - 1,
            i - 1 to j, i + 1 to j,
            i - 1 to j + 1, i to j + 1, i + 1 to j + 1
        )
        return positions.any { (i,j) -> i in schematic.indices && j in schematic.indices &&
                (!schematic[i][j].isDigit() && schematic[i][j] != '.')}
    }
    private fun validateGear(i: Int, j: Int): Pair<Int,Int>? {
        val positions = listOf(
            i - 1 to j - 1, i to j - 1, i + 1 to j - 1,
            i - 1 to j, i + 1 to j,
            i - 1 to j + 1, i to j + 1, i + 1 to j + 1
        )
        return positions.firstOrNull { (i,j) -> i in schematic.indices && j in schematic.indices &&
                schematic[i][j] == '*'}
    }

    override fun solvePart2(): Int {
        val gearRatios = mutableMapOf<Pair<Int,Int>, MutableList<Int>>()
        var curr = 0
        var currGear: Pair<Int,Int>? = null
        for (i in schematic.indices) {
            for (j in schematic[0].indices) {
                if (schematic[i][j].isDigit()) {
                    curr = curr * 10 + schematic[i][j].digitToInt()
                    if (currGear == null) currGear = validateGear(i, j)
                    println("${schematic[i][j]} $curr $currGear")
                } else {
                    if (currGear != null) gearRatios.getOrPut(currGear, {mutableListOf()}).add(curr)
                    curr = 0
                    currGear = null
                }
            }
        }
        return gearRatios.filter { it.value.size == 2 }.values.map { it[0]*it[1] }.sum()
    }
}

fun main() {
    val name = Day03::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(Day03(testInput), Day03(realInput))
}