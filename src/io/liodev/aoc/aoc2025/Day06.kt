package io.liodev.aoc.aoc2025

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2025 Day 6: Trash Compactor ---
class Day06(
    input: String,
) : Day<Long> {
    override val expectedValues = listOf(4277556L, 7098065460541, 3263827, 13807151830618)

    private val operationsList = input.split("\n").toOperationsList()

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

    override fun solvePart1(): Long = solveHomework { charArray -> charArray.asLongs() } 

    override fun solvePart2(): Long = solveHomework { charArray -> charArray.transpose().asLongs() }

    fun solveHomework(getOperands: (List<List<Char>>) -> List<Long>): Long {
        return operationsList[0].indices.map { j ->
            val x = (0..<operationsList.lastIndex).map { i -> operationsList[i][j].toList() }
            Operation(
                operands = getOperands(x),
                operation = operationsList[operationsList.lastIndex][j].trim(),
            )
        }.sumOf { it.solve() }
    }
}

private fun List<String>.toOperationsList(): List<List<String>> {
    val lines = this
    val maxLineSize = lines.maxOf { it.length } // <- AndroidStudio removing trailing whitespaces! -_-

    val emptyColumnsIndices = listOf(-1) +
            lines[0].indices.filter {
                    j -> lines.indices.all { i -> lines[i][j] == ' ' }
            } + listOf(maxLineSize)
    val chunkSizes = emptyColumnsIndices.zipWithNext().map { (a,b) -> b - a - 1 }

    return lines.map { line ->
        val paddedLine = line.padEnd(maxLineSize, ' ') // <- AndroidStudio removing trailing whitespaces! -_-
        buildList {
            var currentIndex = 0
            for (size in chunkSizes) {
                add(paddedLine.substring(currentIndex, currentIndex + size))
                currentIndex += size + 1
            }
        }
    }
}

private fun List<List<Char>>.asLongs(): List<Long> = this.map { it.joinToString("").trim().toLong() }

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
