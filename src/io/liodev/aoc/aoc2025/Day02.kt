package io.liodev.aoc.aoc2025

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2025 Day 2: Gift Shop ---
class Day02(
    input: String,
) : Day<Long> {
    override val expectedValues = listOf(1227775554L, 29818212493, 4174379265, 37432260594)

    private val sequences =
        input.split(",").map { range -> range.split("-").let { it[0].toLong()..it[1].toLong() } }

    override fun solvePart1(): Long {
        return sequences.sumOf { range -> range.sumOf { it.toString().invalidIdValue() } }
    }

    override fun solvePart2(): Long {
        return sequences.sumOf { range -> range.sumOf { it.toString().extraInvalidIdValue() } }
    }
}

private fun String.invalidIdValue(): Long {
    val firstHalf = this.take(this.length / 2)
    val secondHalf = this.drop(this.length / 2)
    return if (firstHalf == secondHalf) this.toLong() else 0
}

private fun String.extraInvalidIdValue(): Long {
    val size = this.length
    for (pieceSize in 1..< size / 2 + 1) {
        if (size % pieceSize != 0) continue
        val piece = this.take(pieceSize)
        if (this == piece.repeat(size / pieceSize)) {
            return this.toLong()
        }
    }
    return 0
}

fun main() {
    val name = Day02::class.simpleName
    val year = 2025
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day02(testInput), Day02(realInput), year, printTimings = true)
}
