package io.liodev.aoc.aoc2025

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2025 Day 6: Trash Compactor ---
class Day06(
    input: String,
) : Day<Long> {
    override val expectedValues = listOf(4277556L, 7098065460541, 3263827, 13807151830618)

    private val operationsListStr = input.split("\n")

    private data class Operation(
        val operands: List<Long>,
        val operation: String,
    ) {
        fun solve(): Long {
            return when (operation) {
                "+" -> operands.sum()
                "*" -> operands.fold(1L) { acc, l -> acc * l }
                else -> 0L
            }
        }
    }

    override fun solvePart1(): Long {
        val operationsList =
            operationsListStr.map { line -> line.split("\\s+".toRegex()).filter { it.isNotEmpty() } }
        val operationsIndices = operationsList[0].indices
        val operandsIndices = 0..<operationsList.lastIndex
        val operationIndex = operationsList.lastIndex

        return operationsIndices.map { j ->
            Operation(
                operands = operandsIndices.map { i -> operationsList[i][j].toLong() },
                operation = operationsList[operationIndex][j],
            )
        }.sumOf { it.solve() }
    }

    override fun solvePart2(): Long {
        val operationIndex = operationsListStr.lastIndex
        val maxLineSize = operationsListStr.maxOf { it.length }
        val operationPositions =
            operationsListStr[operationIndex].withIndex().filter { (_, value) -> value != ' ' }.map { it.index }
        val chunkSizes = 
            operationPositions.zipWithNext().map { (a, b) -> b - a - 1 } + (maxLineSize - operationPositions.last())

        val operationsList = operationsListStr.map { line ->
            //val paddedLine = line.padEnd(maxLineSize, ' ') <- AndroidStudio removing my trailing whitespaces! -_-
            buildList {
                var currentIndex = 0
                for (size in chunkSizes) {
                    add(line.substring(currentIndex, currentIndex + size))
                    currentIndex += size + 1
                }
            }
        }
        val operationsIndices = operationsList[0].indices
        val operandsIndices = 0..<operationsList.lastIndex

        return operationsIndices.map { j ->
            Operation(
                operands = operandsIndices.map { i -> operationsList[i][j].toList() }.transpose()
                    .map { it.joinToString("").trim().toLong() },
                operation = operationsList[operationIndex][j].trim(),
            )
        }.sumOf { it.solve() }
    }
}

private fun List<List<Char>>.transpose(): List<List<Char>> {
    return this[0].indices.map { j ->
        this.indices.map { i -> this[i][j] }
    }
}

fun main() {
    val name = Day06::class.simpleName
    val year = 2025
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day06(testInput), Day06(realInput), year, printTimings = true)
}
