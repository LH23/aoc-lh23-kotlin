package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.get

// --- 2024 Day 4: Ceres Search ---
class Day04(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(18, 2571, 9, 1992)

    private val wordSearch = input.split("\n").map { it.toCharArray().toList() }

    override fun solvePart1(): Int {
        var count = 0
        for (i in wordSearch.indices) {
            for (j in wordSearch[0].indices) {
                val ij = Coord(i, j)
                if (wordSearch[ij] == 'X') {
                    if (wordSearch.isXmas(ij, ij.goDown(1), ij.goDown(2), ij.goDown(3))) count++
                    if (wordSearch.isXmas(ij, ij.goUp(1), ij.goUp(2), ij.goUp(3))) count++
                    if (wordSearch.isXmas(ij, ij.goLeft(1), ij.goLeft(2), ij.goLeft(3))) count++
                    if (wordSearch.isXmas(ij, ij.goRight(1), ij.goRight(2), ij.goRight(3))) count++

                    if (wordSearch.isXmas(ij, ij.goDown(1).goRight(1), ij.goDown(2).goRight(2), ij.goDown(3).goRight(3))) count++
                    if (wordSearch.isXmas(ij, ij.goDown(1).goLeft(1), ij.goDown(2).goLeft(2), ij.goDown(3).goLeft(3))) count++
                    if (wordSearch.isXmas(ij, ij.goUp(1).goRight(1), ij.goUp(2).goRight(2), ij.goUp(3).goRight(3))) count++
                    if (wordSearch.isXmas(ij, ij.goUp(1).goLeft(1), ij.goUp(2).goLeft(2), ij.goUp(3).goLeft(3))) count++
                }
            }
        }
        return count
    }

    override fun solvePart2(): Int {
        var count = 0
        for (i in 1..<wordSearch.lastIndex) {
            for (j in 1..<wordSearch[0].lastIndex) {
                if (wordSearch[Coord(i, j)] == 'A') {
                    if (wordSearch.isXCrossedMasPattern(i, j)) count++
                }
            }
        }
        return count
    }

    private fun List<List<Char>>.isXmas(
        x: Coord,
        m: Coord,
        a: Coord,
        s: Coord,
    ): Boolean =
        listOf(x, m, a, s).all { it.validIndex(this) } &&
            this[x] == 'X' &&
            this[m] == 'M' &&
            this[a] == 'A' &&
            this[s] == 'S'

    private fun List<List<Char>>.isXCrossedMasPattern(
        i: Int,
        j: Int,
    ): Boolean {
        val ij = Coord(i, j)
        return isXCrossedMas(ij.goUp().goLeft(), ij, ij.goDown().goRight(), ij.goUp().goRight(), ij.goDown().goLeft()) ||
            isXCrossedMas(ij.goDown().goRight(), ij, ij.goUp().goLeft(), ij.goDown().goLeft(), ij.goUp().goRight()) ||
            isXCrossedMas(ij.goUp().goLeft(), ij, ij.goDown().goRight(), ij.goDown().goLeft(), ij.goUp().goRight()) ||
            isXCrossedMas(ij.goDown().goRight(), ij, ij.goUp().goLeft(), ij.goUp().goRight(), ij.goDown().goLeft())
    }

    private fun List<List<Char>>.isXCrossedMas(
        m: Coord,
        a: Coord,
        s: Coord,
        m2: Coord,
        s2: Coord,
    ): Boolean =
        listOf(m, a, s, m2, s2).all { it.validIndex(this) } &&
            this[m] == 'M' &&
            this[a] == 'A' &&
            this[s] == 'S' &&
            this[m2] == 'M' &&
            this[s2] == 'S'
}

fun main() {
    val name = Day04::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day04(testInput), Day04(realInput), year)
}
