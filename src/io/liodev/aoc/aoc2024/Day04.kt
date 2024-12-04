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

                    if (wordSearch.isXmas(ij, ij.goDownRight(1), ij.goDownRight(2), ij.goDownRight(3))) count++
                    if (wordSearch.isXmas(ij, ij.goDownLeft(1), ij.goDownLeft(2), ij.goDownLeft(3))) count++
                    if (wordSearch.isXmas(ij, ij.goUpRight(1), ij.goUpRight(2), ij.goUpRight(3))) count++
                    if (wordSearch.isXmas(ij, ij.goUpLeft(1), ij.goUpLeft(2), ij.goUpLeft(3))) count++
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

    private fun List<List<Char>>.isXmas(x: Coord, m: Coord, a: Coord, s: Coord) =
        listOf(x, m, a, s).all { it.validIndex(this) } && this[x] == 'X' && this[m] == 'M' && this[a] == 'A' && this[s] == 'S'

    private fun List<List<Char>>.isXCrossedMasPattern(i: Int, j: Int): Boolean {
        val ij = Coord(i, j)
        return isXCrossedMas(ij.goUpLeft(), ij, ij.goDownRight(), ij.goUpRight(), ij.goDownLeft()) ||
            isXCrossedMas(ij.goDownRight(), ij, ij.goUpLeft(), ij.goDownLeft(), ij.goUpRight()) ||
            isXCrossedMas(ij.goUpLeft(), ij, ij.goDownRight(), ij.goDownLeft(), ij.goUpRight()) ||
            isXCrossedMas(ij.goDownRight(), ij, ij.goUpLeft(), ij.goUpRight(), ij.goDownLeft())
    }

    private fun List<List<Char>>.isXCrossedMas(m: Coord, a: Coord, s: Coord, m2: Coord, s2: Coord) =
        this[m] == 'M' && this[a] == 'A' && this[s] == 'S' && this[m2] == 'M' && this[s2] == 'S'

    // less code, but slower
    private fun List<List<Char>>.isXCrossedMasPattern2(i: Int, j: Int): Boolean {
        val ij = Coord(i, j)
        val ms = setOf('M', 'S')
        return this[ij] == 'A' &&
            setOf(this[ij.goUpLeft()], this[ij.goDownRight()]) == ms &&
            setOf(this[ij.goUpRight()], this[ij.goDownLeft()]) == ms
    }
}

fun main() {
    val name = Day04::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day04(testInput), Day04(realInput), year)
}
