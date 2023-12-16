package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.printMatrix

// 2023 Day14
class Day14(input: String) : Day<Int> {

    override val expectedValues = listOf(136, 106517, 64, 79723, 15, 7)

    private val platform = input.split("\n").map { it.toList() }

    override fun solvePart1(): Int {
        val platformResult = List(platform.size) { i ->
            MutableList(platform[0].size) { j -> platform[i][j] }
        }
        platformResult.tiltNorthInPlace()
        return platformResult.calculateCostInPlace()
    }

    override fun solvePart2(): Int {
        val platformResult = List(platform.size) { i ->
            MutableList(platform[0].size) { j -> platform[i][j] }
        }
        repeat(1000){
            platformResult.runCycle()
        }
        return platformResult.calculateCostInPlace()
    }
}

private fun List<MutableList<Char>>.runCycle() {
    this.tiltNorthInPlace()
    this.tiltWestInPlace()
    this.tiltSouthInPlace()
    this.tiltEastInPlace()
}

private fun List<MutableList<Char>>.tiltNorthInPlace() {
    for (c in this[0].indices) {
        val columnTilted = this.indices.reversed().map { r ->
            this[r][c]
        }.tiltRight()
        for (i in columnTilted.indices) {
            this[this.lastIndex-i][c] = columnTilted[i]
        }
    }
}

private fun List<MutableList<Char>>.tiltWestInPlace() {
    for (r in this.indices) {
        val rowTilted = this[0].indices.reversed().map { c ->
            this[r][c]
        }.tiltRight()
        for (i in rowTilted.indices) {
            this[r][this[0].lastIndex-i] = rowTilted[i]
        }
    }
}
private fun List<MutableList<Char>>.tiltSouthInPlace() {
    for (c in this[0].indices) {
        val columnTilted = this.indices.map { r ->
            this[r][c]
        }.tiltRight()
        for (i in columnTilted.indices) {
            this[i][c] = columnTilted[i]
        }
    }
}
private fun List<MutableList<Char>>.tiltEastInPlace() {
    for (r in this.indices) {
        val rowTilted = this[0].indices.map { c ->
            this[r][c]
        }.tiltRight()
        for (i in rowTilted.indices) {
            this[r][i] = rowTilted[i]
        }
    }
}

private fun List<Char>.tiltRight(): List<Char> {
    val result = mutableListOf<Char>()
    var spaces = 0
    var rollingStones = 0
    for (i in this.indices) {
        when (this[i]) {
            '.' -> spaces++
            'O' -> rollingStones++
            '#' -> {
                repeat(spaces) { result.add('.') }
                repeat(rollingStones) { result.add('O') }
                result.add('#')
                spaces = 0
                rollingStones = 0
            }
        }
    }
    repeat(spaces) { result.add('.') }
    repeat(rollingStones) { result.add('O') }
    return result
}

private fun List<List<Char>>.calculateCostInPlace(): Int {
    return this.mapIndexed { i, row -> (this.size-i) * row.count{ it == 'O'} }.sum()
}

fun main() {
    val name = Day14::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test.txt")
    val testInput2 = readInputAsString("src/input/2023/${name}_test2.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(Day14(testInput), Day14(realInput), extraDays = listOf(Day14(testInput2)))
}